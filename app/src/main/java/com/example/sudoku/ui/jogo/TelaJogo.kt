package com.example.sudoku.ui.jogo

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.sudoku.logic.GeradorSudoku
import com.example.sudoku.logic.Nivel
import com.example.sudoku.logic.Pontuacao
import com.example.sudoku.logic.ValidadorSudoku
import com.example.sudoku.ui.novojogo.TabuleiroSudoku
import com.example.sudoku.ui.novojogo.TecladoNumerico
import kotlinx.coroutines.delay
import java.util.Locale
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaJogo(
    nivel: String,
    semente: Long,
    onVoltar: () -> Unit
) {
    val nivelObj = remember(nivel) { Nivel.deTexto(nivel) }
    val jogoInicial = remember(nivel, semente) {
        val rnd = if (semente == 0L) Random.Default else Random(semente)
        GeradorSudoku.gerar(nivelObj, rnd)
    }

    var tabuleiro by remember(jogoInicial) { mutableStateOf(jogoInicial.tabuleiro) }
    val gabarito = jogoInicial.gabarito

    val fixas = remember(jogoInicial) {
        val set = mutableSetOf<Pair<Int, Int>>()
        for (r in 0 until nivelObj.tamanho) {
            for (c in 0 until nivelObj.tamanho) {
                if (jogoInicial.tabuleiro[r][c] != 0) {
                    set.add(r to c)
                }
            }
        }
        set
    }

    var selecionada by remember { mutableStateOf<Pair<Int, Int>?>(null) }
    var erros by remember { mutableIntStateOf(0) }
    var dicasRestantes by remember { mutableIntStateOf(nivelObj.dicas) }
    var tempoSegundos by remember { mutableIntStateOf(0) }
    var jogoAcabou by remember { mutableStateOf(false) }
    var venceu by remember { mutableStateOf(false) }

    val conflitos = remember(tabuleiro) { ValidadorSudoku.conflitos(tabuleiro, nivelObj) }

    LaunchedEffect(jogoAcabou) {
        while (!jogoAcabou) {
            delay(1000)
            tempoSegundos++
        }
    }

    fun checarFimDeJogo(novoTabuleiro: List<List<Int>>, novosErros: Int) {
        if (novosErros >= 3) {
            jogoAcabou = true
            venceu = false
        } else if (ValidadorSudoku.venceu(novoTabuleiro, nivelObj)) {
            jogoAcabou = true
            venceu = true
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    val min = tempoSegundos / 60
                    val seg = tempoSegundos % 60
                    val tempoFmt = String.format(Locale.getDefault(), "%02d:%02d", min, seg)
                    Text("${nivelObj.titulo} ${nivelObj.tamanho}x${nivelObj.tamanho} - $tempoFmt")
                },
                navigationIcon = {
                    IconButton(onClick = onVoltar) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Erros: $erros/3",
                    color = if (erros > 0) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Dicas: $dicasRestantes",
                    fontWeight = FontWeight.Bold
                )
            }

            TabuleiroSudoku(
                nivel = nivelObj,
                tabuleiro = tabuleiro,
                fixas = fixas,
                conflitos = conflitos,
                selecionada = selecionada,
                onCasaClick = { r, c ->
                    if (!jogoAcabou) {
                        selecionada = r to c
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            TecladoNumerico(
                nivel = nivelObj,
                dicasRestantes = dicasRestantes,
                habilitado = !jogoAcabou,
                onNumeroClick = { num ->
                    val pos = selecionada
                    if (pos != null && pos !in fixas && !jogoAcabou) {
                        val (r, c) = pos
                        val novo = tabuleiro.map { it.toMutableList() }
                        novo[r][c] = num
                        tabuleiro = novo

                        var novosErros = erros
                        if (ValidadorSudoku.temConflito(novo, r, c, nivelObj)) {
                            novosErros++
                            erros = novosErros
                        }
                        checarFimDeJogo(novo, novosErros)
                    }
                },
                onApagar = {
                    val pos = selecionada
                    if (pos != null && pos !in fixas && !jogoAcabou) {
                        val (r, c) = pos
                        val novo = tabuleiro.map { it.toMutableList() }
                        novo[r][c] = 0
                        tabuleiro = novo
                    }
                },
                onDica = {
                    val pos = selecionada
                    if (pos != null && pos !in fixas && dicasRestantes > 0 && !jogoAcabou) {
                        val (r, c) = pos
                        val correto = gabarito[r][c]
                        val novo = tabuleiro.map { it.toMutableList() }
                        novo[r][c] = correto
                        tabuleiro = novo
                        dicasRestantes--
                        checarFimDeJogo(novo, erros)
                    }
                }
            )
        }
    }

    if (jogoAcabou) {
        val pontos = Pontuacao.calcular(nivelObj, tempoSegundos, erros, nivelObj.dicas - dicasRestantes)
        AlertDialog(
            onDismissRequest = {},
            title = {
                Text(if (venceu) "Vitória! 🎉" else "Derrota! ❌")
            },
            text = {
                Column {
                    Text(if (venceu) "Você completou o Sudoku!" else "Você cometeu 3 erros.")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Pontuação: $pontos pontos")
                    Text("Tempo: ${tempoSegundos / 60}m ${tempoSegundos % 60}s")
                    Text("Erros: $erros | Dicas usadas: ${nivelObj.dicas - dicasRestantes}")
                }
            },
            confirmButton = {
                Button(onClick = onVoltar) {
                    Text("Voltar ao Menu")
                }
            }
        )
    }
}
