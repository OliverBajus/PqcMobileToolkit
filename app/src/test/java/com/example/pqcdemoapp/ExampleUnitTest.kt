package com.example.pqcdemoapp


import org.junit.Test


/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    private val random = java.security.SecureRandom()


    @Test
    fun addition_isCorrect() {
        println(random.nextInt(2))
        println(random.nextInt(2))
        println(random.nextInt(2))
        println(random.nextInt(2))
    }
}