package com.example.sudoku.data.remote

// ╔══════════════════════════════════════════════╗
// ║  RankingFirestore.kt                         ║
// ║  Acesso ao Firebase (coleção ranking)        ║
// ║  Faz para o Firebase o papel do DAO          ║
// ╚══════════════════════════════════════════════╝

import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.snapshots
import com.google.firebase.firestore.toObjects
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

// Faz para o Firebase o papel que o DAO faz para o Room.
// "open" só para os testes poderem trocar por um falso, sem internet.
open class RankingFirestore {

    // lazy: o Firebase só é acessado quando alguém usa a coleção (o falso dos testes nunca usa)
    private val colecao by lazy { Firebase.firestore.collection("ranking") }

    open suspend fun inicializarRankingPadrao() {
        try {
            if (colecao.limit(1).get().await().isEmpty) {
                val agora = System.currentTimeMillis()
                listOf(
                    RankingRemoto("", "Carlos Silva", "FACIL", 1250, 45, agora - 86400000),
                    RankingRemoto("", "Ana Souza", "FACIL", 1100, 60, agora - 43200000),
                    RankingRemoto("", "Gabriel Admin", "MEDIO", 2400, 120, agora - 72000000),
                    RankingRemoto("", "Mariana", "MEDIO", 2150, 150, agora - 36000000),
                    RankingRemoto("", "Eduardo", "DIFICIL", 3800, 240, agora - 100000000),
                    RankingRemoto("", "Arthur", "DIFICIL", 3500, 310, agora - 15000000)
                ).forEach { colecao.add(it).await() }
            }
        } catch (_: Exception) {
        }
    }

    // snapshots(): o Flow emite de novo sempre que o ranking muda no Firebase, igual ao Flow do Room
    open fun buscarTodos(): Flow<List<RankingRemoto>> =
        colecao.snapshots().map { it.toObjects<RankingRemoto>() }

    open suspend fun inserir(ranking: RankingRemoto) {
        colecao.add(ranking).await()
    }

    open suspend fun deletar(id: String) {
        colecao.document(id).delete().await()
    }
}
