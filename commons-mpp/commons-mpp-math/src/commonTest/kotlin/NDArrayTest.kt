import com.martmists.commons.math.NDArray
import kotlin.test.Test

class NDArrayTest {
    @Test
    fun testArray() {
        val a = NDArray.range(2, 3, 4)
        val b = NDArray.range(1, 4, 1)
        println(a dotComplex b)
    }
}
