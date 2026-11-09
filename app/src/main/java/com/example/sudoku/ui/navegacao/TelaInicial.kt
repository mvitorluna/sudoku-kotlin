package com.example.sudoku.ui.navegacao

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun TelaInicial(onJogar: () -> Unit, onAdmin: () -> Unit) {
    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Sudoku", style = MaterialTheme.typography.displayMedium)

            Spacer(modifier = Modifier.height(48.dp))

            Button(onClick = onJogar, modifier = Modifier.fillMaxWidth()) {
                Text("Jogar")
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(onClick = onAdmin, modifier = Modifier.fillMaxWidth()) {
                Text("Admin")
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun TelaInicialPreview() {
    MaterialTheme {
        TelaInicial(onJogar = {}, onAdmin = {})
    }
}
