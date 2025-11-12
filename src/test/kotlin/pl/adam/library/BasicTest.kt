package pl.adam.library

import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo

class BasicTest {
    @Test
    fun `simple sanity test`() {
        expectThat(2 + 2).isEqualTo(4)
    }
}
