package com.example.sudoku.data.remote

// ╔══════════════════════════════════════════════╗
// ║  RankingRemoto.kt                            ║
// ║  Documento do ranking no Firebase            ║
// ║  Campos com valor padrão (Firestore)         ║
// ╚══════════════════════════════════════════════╝

import com.google.firebase.firestore.DocumentId

// Todos os campos com valor padrão, senão o Firestore não consegue converter o documento em objeto
data class RankingRemoto(
    @DocumentId val id: String = "",
    val nomeJogador: String = "",
    val nivel: String = "MEDIO",
    val pontos: Int = 0,
    val tempoSegundos: Int = 0,
    val data: Long = System.currentTimeMillis()
)
