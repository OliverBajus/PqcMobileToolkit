// MainActivity.kt
package com.example.pqcdemoapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.android.awaitFrame

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val vm = remember { PqcViewModel() }
            PqcScreen(vm)
        }
    }
}

@Composable
fun PqcScreen(vm: PqcViewModel) {
    val logText by vm.logText.collectAsState()
    val scroll = rememberScrollState()

    // NEW: selected alg state
    val selectedKem by vm.selectedKem.collectAsState()
    val selectedSig by vm.selectedSig.collectAsState()

    LaunchedEffect(Unit) { vm.onAppStart() }

    LaunchedEffect(logText) {
        awaitFrame()
        scroll.animateScrollTo(scroll.maxValue)
    }

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // Header + clear
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "PQC Thesis Test",
                    style = MaterialTheme.typography.headlineMedium
                )
                Spacer(Modifier.weight(1f))
                IconButton(onClick = vm::clearLogs) {
                    Icon(Icons.Default.Delete, contentDescription = "Clear logs")
                }
            }

            // NEW: pickers
            AlgoPicker(
                label = "KEM algorithm",
                selectedLabel = selectedKem.name,
                options = vm.kemOptions,
                optionLabel = { it.name },
                onSelect = vm::selectKem,
                modifier = Modifier.fillMaxWidth()
            )

            AlgoPicker(
                label = "Signature algorithm",
                selectedLabel = selectedSig.name,
                options = vm.sigOptions,
                optionLabel = { it.name },
                onSelect = vm::selectSig,
                modifier = Modifier.fillMaxWidth()
            )

            // Log window
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(12.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scroll)
                ) {
                    Text(
                        text = logText,
                        fontFamily = FontFamily.Monospace,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            // Buttons row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = vm::runFullKemFlow,
                    modifier = Modifier.weight(1f)
                ) { Text("Test KEM") }

                Button(
                    onClick = vm::runFullSigFlow,
                    modifier = Modifier.weight(1f)
                ) { Text("Test DSA") }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun <T> AlgoPicker(
    label: String,
    selectedLabel: String,
    options: List<T>,
    optionLabel: (T) -> String,
    onSelect: (T) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = selectedLabel,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { opt ->
                DropdownMenuItem(
                    text = { Text(optionLabel(opt)) },
                    onClick = {
                        onSelect(opt)
                        expanded = false
                    }
                )
            }
        }
    }
}