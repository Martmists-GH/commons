package com.martmists.commons.math

import com.martmists.commons.extensions.roundTo
import com.martmists.commons.functions.product
import kotlin.math.max
import kotlin.math.pow
import kotlin.math.log
import kotlin.random.Random

class NDArray private constructor(val shape: List<Int>, private val offset: Int = 0, strides: List<Int>? = null, data: DoubleArray? = null, initializer: (Int) -> Double = { 0.0 }) : Collection<NDArray>, Sequence<NDArray> {
    constructor(vararg dimensions: Int) : this(dimensions.toList())
    constructor(vararg dimensions: Int, initializer: (Int) -> Double) : this(dimensions.toList(), initializer = initializer)
    constructor(list: List<Double>) : this(list.size, initializer = { list[it] })

    private val strides = strides ?: let {
        val stride = mutableListOf<Int>()
        var strideSize = 1
        for (element in shape.asReversed()) {
            stride.add(strideSize)
            strideSize *= element
        }
        stride
    }.asReversed()
    private val items = data ?: DoubleArray(shape.reduce { acc, i -> acc * i }, initializer)
    private val indices = product(
        *shape.zip(this.strides).map { (sh, st) -> (0 until sh).map { it * st } }.toTypedArray()
    ).map {
        it.sum()
    }.toList().toIntArray()

    override val size = indices.size
    override fun isEmpty() = indices.isEmpty()
    override fun iterator(): Iterator<NDArray> {
        val s = shape.first()
        return object : Iterator<NDArray> {
            var i = 0
            override fun hasNext() = i + 1 < s
            override fun next(): NDArray = view(i, axis = 0)
        }
    }

    override fun containsAll(elements: Collection<NDArray>): Boolean {
        return false
    }
    override fun contains(element: NDArray): Boolean {
        return false
    }
    operator fun contains(it: Double) : Boolean {
        return false
    }

    fun view(index: Int, axis: Int = 0): NDArray {
        if (axis < 0 || axis >= shape.size) throw IllegalArgumentException("Axis $axis is out of bounds [0, ${shape.lastIndex}]")

        val newDims = shape.toMutableList()
        val size = newDims.removeAt(axis)

        if (index < 0 || index >= size) throw IllegalArgumentException("Index $index is out of bounds [0, ${size - 1}]")

        val newStrides = strides.toMutableList()
        newStrides.removeAt(axis)

        return NDArray(newDims, offset + (index * strides[axis]), newStrides, items)
    }

    private fun getIndex(index: Int): Int {
        return offset + indices[index]
    }
    private fun getIndex(indices: IntArray) : Int {
        return offset + indices.mapIndexed { index, i ->
            i * strides[strides.lastIndex - indices.lastIndex + index]
        }.sum()
    }

    operator fun get(check: NDArray): NDArray {
        if (check.shape.size != shape.size) throw IllegalArgumentException("Shape mismatch")
        val nums = (0 until size).mapNotNull { i ->
            if (check[i] == 1.0) items[i] else null
        }
        return NDArray(nums)
    }
    operator fun get(vararg index: Int): Double {
        return items[getIndex(index)]
    }
    operator fun set(vararg index: Int, value: Double) {
        items[getIndex(index)] = value
    }

    fun broadcastTo(shape: List<Int>): NDArray {
        if (shape == this.shape) return this
        // check if shapes are compatible
        val longest = max(shape.size, this.shape.size)
        fun extend(s: List<Int>) : List<Int> {
            if (s.size == longest) return s
            return MutableList(longest) {
                1
            }.also {
                s.forEachIndexed { index, i ->
                    it[it.size - s.size + index] = i
                }
            }
        }

        val st = MutableList(longest) {
            0
        }.also {
            strides.forEachIndexed { index, i ->
                it[it.size - strides.size + index] = i
            }
        }
        val a = extend(shape)
        val b = extend(this.shape)
        val newShape = MutableList(longest) {
            1
        }

        for (i in 0 until longest) {
            if (a[i] != b[i] && a[i] != 1 && b[i] != 1) throw IllegalArgumentException("Shape mismatch")
            newShape[i] = max(a[i], b[i])
        }

        if (newShape == this.shape) return this
        return NDArray(newShape, offset, st, items)
    }

    fun transform(transform: (Double) -> Double): NDArray {
        return copy { i -> transform(items[getIndex(i)]) }
    }
    fun transformInplace(transform: (Double) -> Double) {
        for (i in 0 until size) {
            this[i] = transform(this[i])
        }
    }

    fun copy(): NDArray {
        return copy { i -> items[getIndex(i)] }
    }
    fun copy(initializer: (Int) -> Double): NDArray {
        return NDArray(shape, initializer = initializer)
    }

    private fun toString(maxDisplay: Int, precision: Int = 4): String {
        return toString(maxDisplay, precision, (0 until size).maxOf { it -> this[it].roundTo(precision).toString().length })
    }
    private fun toString(maxDisplay: Int, precision: Int = 4, maxPad: Int = 0): String {
        val sb = StringBuilder()
        sb.append("[")

        val (separator, getter) = if (shape.size == 1) {
            Pair(", ") { it: Int -> this[it].roundTo(precision).toString().padStart(maxPad) }
        } else {
            Pair("," + "\n".repeat(shape.size - 1)) { it: Int -> this.view(it).toString(maxDisplay, precision, maxPad).prependIndent(" ").let { s -> if (it == 0) s.trimStart() else s } }
        }

        val dimSize = shape.first()

        if (dimSize <= maxDisplay) {
            for (i in 0 until dimSize) {
                sb.append(getter(i))
                if (i != dimSize - 1) sb.append(separator)
            }
        } else {
            val num = maxDisplay / 2
            for (i in 0 until num) {
                sb.append(getter(i))
                sb.append(separator)
            }
            sb.append("...")
            sb.append(separator)
            for (i in dimSize - num until dimSize) {
                sb.append(getter(i))
                if (i != dimSize - 1) sb.append(separator)
            }
        }

        sb.append("]")
        return sb.toString()
    }

    override fun toString(): String {
        val s = StringBuilder()
        s.append("array(")
        s.append(toString(8).prependIndent(" ".repeat(6)).trimStart())
        s.append(")")
        return s.toString()
    }


    operator fun plusAssign(other: NDArray) {
        if (shape != other.shape) throw IllegalArgumentException("Cannot add arrays of different shapes")
        for (i in 0 until size) {
            this[i] += other[i]
        }
    }
    operator fun plusAssign(other: Double) = transformInplace {
        it + other
    }

    operator fun plus(other: NDArray): NDArray {
        if (shape != other.shape) throw IllegalArgumentException("Cannot add arrays of different shapes")
        return copy {
            this[it] + other[it]
        }
    }
    operator fun plus(other: Double): NDArray {
        return transform {
            it + other
        }
    }

    operator fun minusAssign(other: NDArray) {
        if (shape != other.shape) throw IllegalArgumentException("Cannot subtract arrays of different shapes")
        for (i in 0 until size) {
            this[i] -= other[i]
        }
    }
    operator fun minusAssign(other: Double) = transformInplace {
        it - other
    }

    operator fun minus(other: NDArray): NDArray {
        if (shape != other.shape) throw IllegalArgumentException("Cannot subtract arrays of different shapes")
        return copy {
            this[it] - other[it]
        }
    }
    operator fun minus(other: Double): NDArray {
        return transform {
            it - other
        }
    }

    operator fun timesAssign(other: NDArray) {
        if (shape != other.shape) throw IllegalArgumentException("Cannot multiply arrays of different shapes")
        for (i in 0 until size) {
            this[i] *= other[i]
        }
    }
    operator fun timesAssign(other: Double) = transformInplace {
        it * other
    }

    operator fun times(other: NDArray): NDArray {
        if (shape != other.shape) throw IllegalArgumentException("Cannot multiply arrays of incompatible shapes (${shape} and ${other.shape})")
        return copy {
            this[it] * other[it]
        }
    }
    operator fun times(other: Double): NDArray {
        return transform {
            it * other
        }
    }

    operator fun divAssign(other: NDArray) {
        if (shape != other.shape) throw IllegalArgumentException("Cannot divide arrays of different shapes")
        for (i in 0 until size) {
            this[i] /= other[i]
        }
    }
    operator fun divAssign(other: Double) = transformInplace {
        it / other
    }

    operator fun div(other: NDArray): NDArray {
        if (shape != other.shape) throw IllegalArgumentException("Cannot divide arrays of different shapes")
        return copy {
            this[it] / other[it]
        }
    }
    operator fun div(other: Double): NDArray {
        return transform {
            it / other
        }
    }

    operator fun remAssign(other: NDArray) {
        if (shape != other.shape) throw IllegalArgumentException("Cannot mod arrays of different shapes")
        for (i in 0 until size) {
            this[i] %= other[i]
        }
    }
    operator fun remAssign(other: Double) = transformInplace {
        it % other
    }

    operator fun rem(other: NDArray): NDArray {
        if (shape != other.shape) throw IllegalArgumentException("Cannot mod arrays of different shapes")
        return copy {
            this[it] % other[it]
        }
    }
    operator fun rem(other: Double): NDArray {
        return transform {
            it % other
        }
    }

    operator fun unaryMinus(): NDArray {
        return transform { -it }
    }

    operator fun unaryPlus(): NDArray {
        return transform { +it }
    }

    operator fun inc(): NDArray {
        return copy { this[it]++ }
    }

    operator fun dec(): NDArray {
        return copy { this[it]-- }
    }

    fun pow(other: Double): NDArray {
        return transform {
            it.pow(other)
        }
    }

    fun pow(other: NDArray): NDArray {
        if (shape != other.shape) throw IllegalArgumentException("Cannot pow arrays of different shapes")
        return copy {
            this[it].pow(other[it])
        }
    }

    fun log(base: Double = Constants.e): NDArray {
        return transform {
            log(it, base)
        }
    }

    fun log(base: NDArray): NDArray {
        if (shape != base.shape) throw IllegalArgumentException("Cannot log arrays of different shapes")
        return copy {
            log(this[it], base[it])
        }
    }

    infix fun matmul(other: NDArray): NDArray {
        if (shape.size != 2 || other.shape.size != 2) throw IllegalArgumentException("Can only matrix multiply 2D arrays")
        if (shape.last() != other.shape.first()) throw IllegalArgumentException("Cannot matrix multiply arrays of incompatible shapes (${shape} and ${other.shape})")
        val nums = DoubleArray(shape.first() * other.shape.last())

        for (i in 0 until shape.first()) {
            for (j in 0 until other.shape.last()) {
                var sum = 0.0
                for (k in 0 until shape.last()) {
                    sum += this[k, i] * other[j, k]
                }
                nums[i * other.shape.last() + j] = sum
            }
        }

        return NDArray(listOf(shape.first(), other.shape.last()), data=nums)
    }

    infix fun dot(other: NDArray): Double {
        if (shape.size != 1 || other.shape.size != 1) throw IllegalArgumentException("Dot product only supported for 1D arrays")
        if (shape[0] != other.shape[0]) throw IllegalArgumentException("Dot product only supported for arrays of the same length")
        return (0 until size).sumOf { this[it] * other[it] }
    }

    infix fun dot(other: Double): NDArray {
        if (shape.size != 1) throw IllegalArgumentException("Dot product only supported for 1D arrays")
        return copy { this[it] * other }
    }

    infix fun dotComplex(other: NDArray) : NDArray {
        if (shape.size == 1) throw IllegalArgumentException("Complex Dot product not supported for 1D arrays")
        return if (shape.size == 2 && other.shape.size == 2) {
            this matmul other
        } else if (other.shape.size == 1) {
            NDArray(shape.toMutableList().dropLast(1)).also {
                product(
                    *it.shape.map { itt -> (0 until itt).toList() }.toTypedArray()
                ).forEach { idx ->
                    it.set(*idx.toIntArray(), value=(0 until shape.last()).sumOf { v ->
                        this.get(*idx.toIntArray(), v) * other[v]
                    })
                }
            }
        } else {
            val aShape = shape.toMutableList().dropLast(1)
            val bShape = other.shape.toMutableList().also { it.removeAt(it.lastIndex - 1) }
            val newShape = aShape + bShape
            NDArray(newShape).also {
                product(
                    *it.shape.map { itt -> (0 until itt).toList() }.toTypedArray()
                ).forEach { idx ->
                    it.set(*idx.toIntArray(), value=(0 until shape.last()).sumOf { v ->
                        this.view(v, axis=this.shape.lastIndex).get(*IntArray(aShape.size) { id -> idx[id] }) *
                                other.view(v, axis=other.shape.lastIndex - 1).get(*IntArray(bShape.size) { id -> idx[id + aShape.size] })
                    })
                }
            }
        }
    }

    fun check(condition: (Double) -> Boolean): NDArray {
        return copy { if (condition(this[it])) 1.0 else 0.0 }
    }

    companion object {
        fun ones(vararg shape: Int): NDArray {
            return NDArray(shape.toList()) { 1.0 }
        }

        fun zeros(vararg shape: Int): NDArray {
            return NDArray(shape.toList()) { 0.0 }
        }

        fun range(vararg shape: Int): NDArray {
            return NDArray(shape.toList()) { it.toDouble() }
        }

        fun rand(vararg shape: Int): NDArray {
            return NDArray(shape.toList()) { Random.nextDouble() }
        }
    }
}
