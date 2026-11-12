package com.example.sudoku.ui.desafios

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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.sudoku.data.local.AppDatabase
import com.example.sudoku.data.local.DesafioEntity
import com.example.sudoku.data.repository.DesafioRepository
import com.example.sudoku.logic.GeradorSudoku
import com.example.sudoku.logic.Nivel
import com.example.sudoku.logic.Sudoku
import com.example.sudoku.logic.ValidadorSudoku
import com.example.sudoku.ui.novojogo.TabuleiroSudoku
import com.example.sudoku.ui.novojogo.TecladoNumerico
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaDesafioJogo(
    desafioId: String,
    semente: Long,
    onVoltar: () -> Unit
) {
    val context = LocalContext.current
    val repository = remember { DesafioRepository(AppDatabase.getDatabase(context).desafioDAO()) }
    val scope = rememberCoroutineScope()

    var desafio by remember { mutableStateOf<DesafioEntity?>(null) }
    var jogoInicial by remember { mutableStateOf<Sudoku?>(null) }

    LaunchedEffect(desafioId, semente) {
        repository.inicializarDesafiosPadrao()
        val d = repository.buscarPorId(desafioId)
        desafio = d
        if (d != null) {
            val nivelObj = when (d.tamanho) {
                4 -> Nivel.FACIL
                6 -> Nivel.MEDIO
                else -> Nivel.DIFICIL
            }
            val rnd = if (semente == 0L) Random.Default else Random(semente)
            val gerado = if (d.casasVazias != nivelObj.casasVazias || d.tamanho != nivelObj.tamanho) {
                val n = nivelObj.tamanho
                val bl = nivelObj.linhasBloco
                val bc = nivelObj.colunasBloco
                val bandas = (0 until bc).shuffled(rnd)
                val linhas = bandas.flatMap { b -> (0 until bl).shuffled(rnd).map { l -> b * bl + l } }
                val pilhas = (0 until bl).shuffled(rnd)
                val colunas = pilhas.flatMap { p -> (0 until bc).shuffled(rnd).map { c -> p * bc + c } }
                val nums = (1..n).shuffled(rnd)
                val gabarito = linhas.map { l ->
                    colunas.map { c ->
                        val pos = (bc * (l % bl) + l / bl + c) % n
                        nums[pos]
                    }
                }
                val todasPosicoes = (0 until n).flatMap { l -> (0 until n).map { c -> l to c } }
                val casasParaEsvaziar = todasPosicoes.shuffled(rnd).take(d.casasVazias).toSet()
                val tabuleiro = gabarito.mapIndexed { l, linha ->
                    linha.mapIndexed { c, valor ->
                        if (l to c in casasParaEsvaziar) 0 else valor
                    }
                }
                Sudoku(nivel = nivelObj, tabuleiro = tabuleiro, gabarito = gabarito)
            } else {
                GeradorSudoku.gerar(nivelObj, rnd)
            }
            jogoInicial = gerado
        }
    }

    val d = desafio
    val jogo = jogoInicial

    if (d == null || jogo == null) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Carregando Desafio...") },
                    navigationIcon = {
                        IconButton(onClick = onVoltar) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                        }
                    }
                )
            }
        ) { padding ->
            Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
                Text("Carregando...")
            }
        }
        return
    }

    val nivelObj = when (d.tamanho) {
        4 -> Nivel.FACIL
        6 -> Nivel.MEDIO
        else -> Nivel.DIFICIL
    }

    var tabuleiro by remember(jogo) { mutableStateOf(jogo.tabuleiro) }
    val gabarito = jogo.gabarito

    val fixas = remember(jogo) {
        val set = mutableSetOf<Pair<Int, Int>>()
        for (r in 0 until nivelObj.tamanho) {
            for (c in 0 until nivelObj.tamanho) {
                if (jogo.tabuleiro[r][c] != 0) {
                    set.add(r to c)
                }
            }
        }
        set
    }

    var selecionada by remember { mutableStateOf<Pair<Int, Int>?>(null) }
    var erros by remember { mutableIntStateOf(0) }
    var dicasRestantes by remember { mutableIntStateOf(d.dicas) }
    
    val isCountdown = d.tempoLimiteSegundos > 0
    var tempoSegundos by remember { mutableIntStateOf(if (isCountdown) d.tempoLimiteSegundos else 0) }
    var jogoAcabou by remember { mutableStateOf(false) }
    var venceu by remember { mutableStateOf(false) }
    var mensagemFim by remember { mutableStateOf("") }

    val conflitos = remember(tabuleiro) { ValidadorSudoku.conflitos(tabuleiro, nivelObj) }

    LaunchedEffect(jogoAcabou) {
        while (!jogoAcabou) {
            delay(1000)
            if (isCountdown) {
                if (tempoSegundos > 0) {
                    tempoSegundos--
                    if (tempoSegundos == 0) {
                        jogoAcabou = true
                        venceu = false
                        mensagemFim = "O tempo esgotou!"
                    }
                }
            } else {
                tempoSegundos++
            }
        }
    }

    fun checarFimDeJogo(novoTabuleiro: List<List<Int>>, novosErros: Int) {
        if (d.maxErros == 0 && novosErros > 0) {
            jogoAcabou = true
            venceu = false
            mensagemFim = "Você cometeu um erro no desafio Sem Erros!"
        } else if (novosErros >= 3) {
            jogoAcabou = true
            venceu = false
            mensagemFim = "Você cometeu 3 erros."
        } else if (ValidadorSudoku.venceu(novoTabuleiro, nivelObj)) {
            jogoAcabou = true
            venceu = true
            mensagemFim = "Você completou o Desafio!"
            scope.launch {
                repository.marcarConcluido(desafioId)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    val min = tempoSegundos / 60
                    val seg = tempoSegundos % 60
                    val tempoFmt = String.format(Locale.getDefault(), "%02d:%02d", min, seg)
                    val prefix = if (isCountdown) "Tempo Restante: " else "Tempo: "
                    Text("${d.titulo} - $prefix$tempoFmt")
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
                    text = if (d.maxErros == 0) "Modo Sem Erros" else "Erros: $erros/${maxOf(3, d.maxErros)}",
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
        AlertDialog(
            onDismissRequest = {},
            title = {
                Text(if (venceu) "Vitória no Desafio! 🎉" else "Derrota no Desafio! ❌")
            },
            text = {
                Column {
                    Text(mensagemFim)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Desafio: ${d.titulo}")
                }
            },
            confirmButton = {
                Button(onClick = onVoltar) {
                    Text("Voltar aos Desafios")
                }
            }
        )
    }
}
