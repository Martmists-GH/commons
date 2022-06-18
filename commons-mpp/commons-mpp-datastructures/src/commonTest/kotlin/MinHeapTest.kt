import com.martmists.commons.datastructures.MinHeap
import kotlin.test.Test
import kotlin.test.assertEquals

class MinHeapTest {
    @Test
    fun IntHeap() {
        // Test to see if I actually implemented it correctly lol
        val h = MinHeap<Int> { -it }
        for (i in 0 until 10) {
            h.push(i)
        }

        for (i in 9 downTo 0) {
            assertEquals(i, h.peek())
            assertEquals(i, h.pop())
        }
    }
}
