package cz.monetplus.pqcdemoapp.presentation.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import cz.monetplus.pqcdemoapp.domain.model.PqcLibrary
import kotlinx.coroutines.android.awaitFrame

@Composable
fun MainScreen(vm: MainViewModel) {
    val logText by vm.logText.collectAsState()
    val scroll = rememberScrollState()

    val selectedKem by vm.selectedKem.collectAsState()
    val selectedSig by vm.selectedSig.collectAsState()

    val selectedLib by vm.selectedLibrary.collectAsState()
    val kemOptions by vm.kemOptions.collectAsState()
    val sigOptions by vm.sigOptions.collectAsState()

    val isInProgress by vm.isInProgress.collectAsState()


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

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "PQC Thesis Test",
                    style = MaterialTheme.typography.headlineMedium
                )
                Spacer(Modifier.weight(1f))
                IconButton(
                    onClick = vm::clearLogs,
                    enabled = isInProgress.not(),
                ) {
                    Icon(Icons.Default.Delete, contentDescription = "Clear logs")
                }
            }

            Picker(
                label = "Library",
                selectedLabel = selectedLib.name,
                options = PqcLibrary.entries,
                optionLabel = { it.name },
                onSelect = vm::selectLibrary,
                modifier = Modifier.fillMaxWidth(),
                isEnabled = isInProgress.not()
            )

            Picker(
                label = "KEM algorithm",
                selectedLabel = selectedKem?.name ?: "",
                options = kemOptions,
                optionLabel = { it.name },
                onSelect = vm::selectKem,
                modifier = Modifier.fillMaxWidth(),
                isEnabled = isInProgress.not(),
            )

            Picker(
                label = "Signature algorithm",
                selectedLabel = selectedSig?.name ?: "",
                options = sigOptions,
                optionLabel = { it.name },
                onSelect = vm::selectSig,
                modifier = Modifier.fillMaxWidth(),
                isEnabled = isInProgress.not(),
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
                    modifier = Modifier.weight(1f),
                    enabled = isInProgress.not(),
                ) { Text("Test KEM") }

                Button(
                    onClick = vm::runFullSigFlow,
                    modifier = Modifier.weight(1f),
                    enabled = isInProgress.not(),
                ) { Text("Test DSA") }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun <T> Picker(
    label: String,
    selectedLabel: String,
    options: List<T>,
    optionLabel: (T) -> String,
    onSelect: (T) -> Unit,
    modifier: Modifier = Modifier,
    isEnabled: Boolean = true
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier,
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
            onDismissRequest = { expanded = false },
        ) {
            options.forEach { opt ->
                DropdownMenuItem(
                    text = { Text(optionLabel(opt)) },
                    onClick = {
                        onSelect(opt)
                        expanded = false
                    },
                    enabled = isEnabled,
                )
            }
        }
    }
}