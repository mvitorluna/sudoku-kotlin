package com.example.sudoku.ui.novojogo

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.sudoku.logic.Nivel

@Composable
fun TecladoNumerico(
    nivel: Nivel,
    dicasRestantes: Int,
    habilitado: Boolean,
    onNumeroClick: (Int) -> Unit,
    onApagar: () -> Unit,
    onDica: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        val linhasDeNumeros = if (nivel.tamanho == 9) {
            listOf(1..5, 6..9)
        } else {
            listOf(1..nivel.tamanho)
        }

        for (numeros in linhasDeNumeros) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                for (numero in numeros) {
                    Button(
                        onClick = { onNumeroClick(numero) },
                        enabled = habilitado,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(text = numero.toString())
                    }
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                onClick = onApagar,
                enabled = habilitado,
                modifier = Modifier.weight(1f)
            ) {
                Text(text = "Apagar")
            }

            OutlinedButton(
                onClick = onDica,
                enabled = habilitado && dicasRestantes > 0,
                modifier = Modifier.weight(1f)
            ) {
                Text(text = "Dica ($dicasRestantes)")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TecladoNumericoFacilPreview() {
    TecladoNumerico(
        nivel = Nivel.FACIL,
        dicasRestantes = 3,
        habilitado = true,
        onNumeroClick = {},
        onApagar = {},
        onDica = {},
        modifier = Modifier.padding(16.dp)
    )
}

@Preview(showBackground = true)
@Composable
fun TecladoNumericoMedioPreview() {
    TecladoNumerico(
        nivel = Nivel.MEDIO,
        dicasRestantes = 2,
        habilitado = true,
        onNumeroClick = {},
        onApagar = {},
        onDica = {},
        modifier = Modifier.padding(16.dp)
    )
}

@Preview(showBackground = true)
@Composable
fun TecladoNumericoDificilPreview() {
    TecladoNumerico(
        nivel = Nivel.DIFICIL,
        dicasRestantes = 1,
        habilitado = true,
        onNumeroClick = {},
        onApagar = {},
        onDica = {},
        modifier = Modifier.padding(16.dp)
    )
}
