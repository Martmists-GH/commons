package com.martmists.commons.datastructures

import com.martmists.commons.extensions.swap

class MinHeap<T>(private val key: (T) -> Int) : Collection<T> {
    private inner class Entry(val value: T) {
        val key = this@MinHeap.key(value)

        override fun toString(): String {
            return "Entry($value, $key)"
        }
    }
    private val backed = mutableListOf<Entry>()

    private fun parent(i: Int) = (i - 1) / 2
    private fun left(i: Int) = 2 * i + 1
    private fun right(i: Int) = 2 * i + 2

    private fun heapify(i: Int) {
        val l = left(i)
        val r = right(i)
        var smallest = i
        if (l < backed.size && backed[l].key < backed[smallest].key)
            smallest = l
        if (r < backed.size && backed[r].key < backed[smallest].key)
            smallest = r
        if (smallest != i) {
            backed.swap(i, smallest)
            heapify(smallest)
        }
    }

    fun push(value: T) {
        val entry = Entry(value)
        backed.add(entry)
        var i = backed.size - 1
        while (i > 0 && backed[parent(i)].key > backed[i].key) {
            backed.swap(i, parent(i))
            i = parent(i)
        }
    }

    fun peek(): T {
        return backed.first().value
    }

    fun pop(): T {
        return backed.removeFirst().value.also {
            heapify(0)
        }
    }

    override val size: Int
        get() = backed.size

    override fun isEmpty(): Boolean {
        return backed.isEmpty()
    }

    override fun iterator(): Iterator<T> {
        return backed.map { it.value }.iterator()
    }

    override fun containsAll(elements: Collection<T>): Boolean {
        return elements.all { contains(it) }
    }

    override fun contains(element: T): Boolean {
        return backed.any { it.value == element }
    }
}
