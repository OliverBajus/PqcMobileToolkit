package com.example.pqcdemoapp

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.pqcdemoapp.ui.theme.PqcDemoAppTheme
import org.bouncycastle.pqc.crypto.mlkem.MLKEMParameters

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PqcDemoAppTheme {
                PqcScreen(MainViewModel(MLKEMService()))
            }
        }
    }
}

@Composable
fun PqcScreen(viewModel: MainViewModel) {
    val logText by viewModel.logText.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    Scaffold(modifier = Modifier.fillMaxSize()) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val context = LocalContext.current
            Button(onClick = { if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                viewModel.runKEM(context, MainViewModel.SecurityLevel.LEVEL_3)
            }
            }) {
                Text("Run KEM Security level 3 tests")
            }
            Button(onClick = { if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                viewModel.runKEM(context, MainViewModel.SecurityLevel.LEVEL_5)
            }
            }) {
                Text("Run KEM Security level 5 tests")
            }
            Button(onClick = { if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                viewModel.runDSA(context, MainViewModel.SecurityLevel.LEVEL_3)
            }
            }) {
                Text("Run DSA  Security level 3 tests")
            }
            Button(onClick = { if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                viewModel.runDSA(context, MainViewModel.SecurityLevel.LEVEL_5)
            }
            }) {
                Text("Run DSA Security level 5 tests")
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text("Logs:", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = logText, modifier = Modifier.fillMaxWidth())
        }
    }
}
