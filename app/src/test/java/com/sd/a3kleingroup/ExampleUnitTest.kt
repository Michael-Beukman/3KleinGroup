package com.sd.a3kleingroup

import com.sd.a3kleingroup.SayHello.*
import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun greeting_isCorrect(){
        assertEquals("Hello guy :)", hello("guy"));
    }
}
