package com.martmists.commons.functions

private fun IntArray.nextKey(key: IntArray): IntArray {
    var i = key.size - 1
    while (i >= 0 && key[i] == this[i] - 1) {
        key[i] = 0
        i--
    }
    if (i >= 0) {
        key[i]++
    }
    return key
}

fun <T> product(vararg items: List<T>) = sequence {
    val keys = items.map { it.size }.toIntArray()
    var key = IntArray(keys.size) { 0 }
    var looped = false
    while (!looped || !key.all { it == 0 }) {
        yield(key.mapIndexed { i, v -> items[i][v] })
        key = keys.nextKey(key)
        looped = true
    }
}

fun <A, B> product(a: List<A>, b: List<B>) = sequence {
    val keys = intArrayOf(
        a.size,
        b.size
    )
    var key = IntArray(2) { 0 }
    var looped = false
    while (!looped || !key.all { it == 0 }) {
        yield(Pair(a[key[0]], b[key[1]]))
        key = keys.nextKey(key)
        looped = true
    }
}

fun <A, B, C> product(a: List<A>, b: List<B>, c: List<C>) = sequence {
    val keys = intArrayOf(
        a.size,
        b.size,
        c.size
    )
    var key = IntArray(3) { 0 }
    var looped = false
    while (!looped || key.any { it != 0 }) {
        yield(Triple(a[key[0]], b[key[1]], c[key[2]]))
        key = keys.nextKey(key)
        looped = true
    }
}
