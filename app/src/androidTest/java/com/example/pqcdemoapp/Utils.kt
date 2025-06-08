package com.example.pqcdemoapp

import android.os.Environment
import androidx.core.app.ActivityCompat
import java.io.File
import java.io.FileWriter

internal fun saveTimingsToCsv(
    fixedTimings: List<Long>,
    randomTimings: List<Long>,
    algorithmName: String,
    prefix: String,
) {
    val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)

    val fixedFile = File(downloadsDir, "${prefix}_${algorithmName}_fixed_timings.csv")
    val randomFile = File(downloadsDir, "${prefix}_${algorithmName}_random_timings.csv")

    writeListToCsv(fixedTimings, fixedFile)
    writeListToCsv(randomTimings, randomFile)
}

private fun writeListToCsv(data: List<Long>, file: File) {
    FileWriter(file, false).use { writer ->
        data.forEach { time ->
            writer.appendLine(time.toString())
        }
    }
}
