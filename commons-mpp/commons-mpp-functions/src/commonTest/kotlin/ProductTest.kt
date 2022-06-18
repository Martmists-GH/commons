import com.martmists.commons.functions.product
import kotlin.test.Test

class ProductTest {
    @Test
    fun test() {
        for (j in product(
            listOf("A", "B", "C"),
            listOf("1", "2", "3"),
            listOf(true, false)
        )) {
            println(j)
        }
    }
}
