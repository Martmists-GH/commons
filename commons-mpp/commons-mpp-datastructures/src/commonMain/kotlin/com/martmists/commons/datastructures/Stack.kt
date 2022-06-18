package com.martmists.commons.datastructures

class Stack<T> : Collection<T> {
    private val backed = mutableListOf<T>()

    fun push(item: T) {
        backed.add(item)
    }

    fun pop(): T {
        return backed.removeLast()
    }

    fun peek(): T {
        return backed.last()
    }

    override val size: Int
        get() = backed.size

    override fun isEmpty(): Boolean {
        return backed.isEmpty()
    }

    override fun iterator(): Iterator<T> {
        return backed.iterator()
    }

    override fun containsAll(elements: Collection<T>): Boolean {
        return backed.containsAll(elements)
    }

    override fun contains(element: T): Boolean {
        return backed.contains(element)
    }
}
