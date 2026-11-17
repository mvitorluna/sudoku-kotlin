# Sudoku

Aplicativo Android de Sudoku feito como trabalho final de Desenvolvimento de Aplicativos Móveis.

**Kotlin · Jetpack Compose · Material 3 · MVVM · Room · Firebase Firestore · Navigation Compose**

## Sobre o jogo

São 3 níveis, e cada nível muda o tamanho do tabuleiro. Todo jogo é gerado na hora, então nunca se repete.

| Nível | Tabuleiro | Números | Casas vazias | Dicas | Pontos base |
|---|---|---|---|---|---|
| Fácil | 4x4 | 1 a 4 | 8 | 3 | 1000 |
| Médio | 6x6 | 1 a 6 | 18 | 2 | 2000 |
| Difícil | 9x9 | 1 a 9 | 45 | 1 | 3000 |

- Toque numa casa vazia e depois num número do teclado.
- **Erro** é quando o número repete na linha, na coluna ou no bloco. Com **3 erros** você perde.
- **Vitória** é preencher todas as casas sem nenhum conflito.
- **Pontuação** = `base - segundos - 50 x erros - 100 x dicas`, e nunca fica abaixo de 0.

## Funcionalidades

**Jogador**
- **Jogar**: escolhe o nível e vê o recorde e o número de partidas de cada um.
- **Partida**: cronômetro, contador de erros, dicas, apagar casa, animações de linha completa, vitória e derrota.
- **Desafios**: Sudokus fixos, iguais para todos os jogadores (gerados a partir de uma semente).
- **Histórico e Estatísticas**: lista das partidas jogadas e números por nível.
- **Ranking Global**: pontuações de todos os jogadores, guardadas no Firebase.
- **Conquistas**: medalhas que vão sendo desbloqueadas conforme você joga.

**Admin**
- **Gerenciar Desafios**: criar e excluir desafios.
- **Moderar Ranking**: remover pontuações do ranking.

> Não tem login. A tela inicial tem só os botões **Jogar** e **Admin**.

## Arquitetura

O projeto segue o padrão MVVM das aulas. Cada funcionalidade tem um pacote em `ui/` com **UiState + ViewModel + Factory + Tela**, e cada fonte de dados tem um **Repository** com try/catch que devolve `Resultado.Sucesso` ou `Resultado.Erro`.

```
app/src/main/java/com/example/sudoku/
├── MainActivity.kt
├── data/
│   ├── local/        Room: AppDatabase, Partida, Conquista, DesafioEntity e DAOs
│   ├── remote/       Firebase: RankingFirestore, RankingRemoto
│   └── repository/   Repositories + Resultado
├── logic/            Gerador, validador, pontuação, níveis e regras das conquistas
└── ui/
    ├── navegacao/    Rotas, AppNavigation, TelaPrincipal, TelaJogador, TelaAdmin, BottomNavBar
    ├── novojogo/     Aba Jogar, tabuleiro e teclado
    ├── jogo/         Tela da partida e resultado
    ├── desafios/     Desafios (jogador e admin)
    ├── historico/    Histórico e estatísticas
    ├── ranking/      Ranking (jogador e admin)
    └── conquistas/   Conquistas
```

**Onde ficam os dados**
- **Room** (no celular): partidas, conquistas e desafios.
- **Firebase Firestore** (na nuvem): coleção `ranking`, compartilhada entre todos os aparelhos e atualizada em tempo real.

**Como o gerador funciona**: monta um Sudoku resolvido por fórmula, embaralha linhas, colunas e números sem quebrar as regras, e depois esvazia as casas do nível. Para os desafios ele usa `Random(semente)`, então a mesma semente sempre gera o mesmo jogo.

## Como rodar

1. Clone o repositório e abra no Android Studio.
2. O `app/google-services.json` já está no projeto (Firebase `sudoku-50470`).
3. Rode num emulador ou num aparelho com Android. O ranking precisa de internet.

```bash
./gradlew assembleDebug       # gera o APK
./gradlew testDebugUnitTest   # roda os testes
```

## Testes

Os testes unitários ficam em `app/src/test` e cobrem o gerador, o validador, a pontuação, as regras das conquistas e os ViewModels de conquistas, histórico, jogo e ranking. O ranking é testado com um `RankingFirestoreFalso`, que não precisa de internet.

## Equipe

| Funcionalidade | Responsável |
|---|---|
| Conquistas e navegação | Marcos |
| Gerador e Novo Jogo | Dombrosk |
| Partida | Eduardo |
| Histórico e Estatísticas | Arthur |
| Desafios | Mariana |
| Ranking Global | Gabriel |
