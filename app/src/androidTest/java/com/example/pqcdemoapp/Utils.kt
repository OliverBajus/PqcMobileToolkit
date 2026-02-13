package com.example.pqcdemoapp

import android.content.Context
import android.os.Environment
import androidx.test.platform.app.InstrumentationRegistry
import java.io.File
import java.io.FileWriter

internal fun saveTimingsToCsv(
    fixedTimings: List<Long>,
    randomTimings: List<Long>,
    algorithmName: String,
    prefix: String,
) {
    val context: Context = InstrumentationRegistry.getInstrumentation().targetContext
    // App-scoped external "Downloads" directory (no permission needed)
    val dir = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)!!
    dir.mkdirs()

    val fixedFile = File(dir, "${prefix}_${algorithmName}_fixed_timings.csv")
    val randomFile = File(dir, "${prefix}_${algorithmName}_random_timings.csv")

    writeListToCsv(fixedTimings, fixedFile)
    writeListToCsv(randomTimings, randomFile)
}

private fun writeListToCsv(data: List<Long>, file: File) {
    file.parentFile?.mkdirs()
    FileWriter(file, false).use { writer ->
        data.forEach { writer.appendLine(it.toString()) }
    }
}