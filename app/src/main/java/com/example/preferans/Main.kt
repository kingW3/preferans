package com.example.preferans

class Main {
    val player1 = Player("Jedan")
    val player2 = Player("Dva")
    val player3 = Player("Tri")
    val players = listOf<Player>(player1,player2,player3)
    val game = Game(players)

}