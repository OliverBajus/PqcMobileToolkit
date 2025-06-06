package com.example.pqcdemoapp

import androidx.benchmark.junit4.BenchmarkRule
import androidx.benchmark.junit4.measureRepeated
import com.example.libqos_android.KeyEncapsulation
import org.junit.After
import org.junit.Test

import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.runner.RunWith

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