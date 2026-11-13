package com.example.sudoku.ui.jogo

import androidx.activity.compose.BackHandler
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import kotlinx.coroutines.delay
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sudoku.data.local.AppDatabase
import com.example.sudoku.data.remote.RankingFirestore
import com.example.sudoku.data.repository.ConquistaRepository
import com.example.sudoku.data.repository.PartidaRepository
import com.example.sudoku.data.repository.RankingRepository
import com.example.sudoku.logic.GeradorSudoku
import com.example.sudoku.logic.Nivel
import com.example.sudoku.ui.novojogo.TabuleiroSudoku
import com.example.sudoku.ui.novojogo.TecladoNumerico
import kotlin.random.Random

@Composable
fun TelaJogo(
    nivel: String,
    semente: Long,
    onVoltar: () -> Unit,
    viewModel: JogoViewModel = viewModel(
        factory = JogoViewModelFactory(
            nivel = Nivel.deTexto(nivel),
            semente = semente,
            partidaRepository = PartidaRepository(AppDatabase.getDatabase(LocalContext.current).partidaDAO()),
            conquistaRepository = ConquistaRepository(
                AppDatabase.getDatabase(LocalContext.current).conquistaDAO(),
                AppDatabase.getDatabase(LocalContext.current).partidaDAO()
            ),
            rankingRepository = RankingRepository(
                RankingFirestore(AppDatabase.getDatabase(LocalContext.current).rankingDAO())
            )
        )
    )
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Sair no meio do jogo pede confirmação; depois que acabou, sai direto
    val pedirParaSair = { if (uiState.acabou) onVoltar() else viewModel.onPedirSair() }
    BackHandler { pedirParaSair() }

    ConteudoJogo(
        uiState = uiState,
        onVoltarClick = pedirParaSair,
        onCasaClick = { linha, coluna -> viewModel.onCasaClick(linha, coluna) },
        onNumeroClick = { viewModel.onNumeroClick(it) },
        onApagar = { viewModel.onApagar() },
        onDica = { viewModel.onDica() },
        onNomeChange = { viewModel.onNomeChange(it) },
        onPublicar = { viewModel.onPublicarRanking() },
        onJogarDeNovo = { viewModel.onJogarDeNovo() },
        onSair = onVoltar,
        onCancelarSaida = { viewModel.onCancelarSaida() },
        onErroVisto = { viewModel.onErroVisto() }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConteudoJogo(
    uiState: JogoUiState,
    onVoltarClick: () -> Unit,
    onCasaClick: (Int, Int) -> Unit,
    onNumeroClick: (Int) -> Unit,
    onApagar: () -> Unit,
    onDica: () -> Unit,
    onNomeChange: (String) -> Unit,
    onPublicar: () -> Unit,
    onJogarDeNovo: () -> Unit,
    onSair: () -> Unit,
    onCancelarSaida: () -> Unit,
    onErroVisto: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(uiState.titulo) },
                navigationIcon = {
                    IconButton(onClick = onVoltarClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Voltar"
                        )
                    }
                },
                actions = {
                    Text(
                        text = uiState.tempoFormatado,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.padding(end = 16.dp)
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            if (uiState.erro != null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = uiState.erro,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.weight(1f)
                    )
                    TextButton(onClick = onErroVisto) {
                        Text("OK")
                    }
                }
            }

            // Na vitória espera a onda do tabuleiro terminar antes de mostrar o resultado
            var mostrarResultado by remember { mutableStateOf(uiState.acabou) }
            LaunchedEffect(uiState.acabou) {
                if (uiState.status == StatusJogo.VENCEU) delay(1000)
                mostrarResultado = uiState.acabou
            }

            if (mostrarResultado) {
                ResultadoJogo(
                    uiState = uiState,
                    onNomeChange = onNomeChange,
                    onPublicar = onPublicar,
                    onJogarDeNovo = onJogarDeNovo,
                    onVoltar = onSair
                )
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Erros: ${uiState.erros}/${JogoViewModel.MAXIMO_DE_ERROS}",
                        color = if (uiState.erros > 0) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
                    )
                    Text("Faltam: ${uiState.casasRestantes}")
                    Text("Dicas: ${uiState.dicasRestantes}")
                }

                Spacer(modifier = Modifier.height(16.dp))

                TabuleiroSudoku(
                    tabuleiro = uiState.tabuleiro,
                    fixas = uiState.fixas,
                    conflitos = uiState.conflitos,
                    selecionada = uiState.selecionada,
                    nivel = uiState.nivel,
                    onCasaClick = onCasaClick
                )

                Spacer(modifier = Modifier.height(16.dp))

                TecladoNumerico(
                    nivel = uiState.nivel,
                    dicasRestantes = uiState.dicasRestantes,
                    habilitado = !uiState.acabou,
                    onNumeroClick = onNumeroClick,
                    onApagar = onApagar,
                    onDica = onDica
                )
            }
        }
    }

    if (uiState.confirmandoSaida) {
        AlertDialog(
            onDismissRequest = onCancelarSaida,
            title = { Text("Sair da partida") },
            text = { Text("A partida em andamento será perdida e não vai para o histórico. Deseja sair?") },
            confirmButton = {
                TextButton(onClick = onSair) {
                    Text("Sair")
                }
            },
            dismissButton = {
                TextButton(onClick = onCancelarSaida) {
                    Text("Continuar jogando")
                }
            }
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ConteudoJogoPreview() {
    val sudoku = GeradorSudoku.gerar(Nivel.DIFICIL, Random(7))
    MaterialTheme {
        ConteudoJogo(
            uiState = JogoUiState(
                nivel = Nivel.DIFICIL,
                tabuleiro = sudoku.tabuleiro,
                selecionada = Pair(0, 0),
                erros = 1,
                dicasRestantes = 1,
                segundos = 222
            ),
            onVoltarClick = {},
            onCasaClick = { _, _ -> },
            onNumeroClick = {},
            onApagar = {},
            onDica = {},
            onNomeChange = {},
            onPublicar = {},
            onJogarDeNovo = {},
            onSair = {},
            onCancelarSaida = {},
            onErroVisto = {}
        )
    }
}
