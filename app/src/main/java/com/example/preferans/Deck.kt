package com.example.preferans
import kotlin.random.Random

class Deck() {
    private val cards : MutableList<Card> = mutableListOf();
    val talon : MutableList<Card> = mutableListOf()
    init {
        for (suit in Suit.values()) {
            for (rank in Rank.values()) {
                cards.add(Card(suit, rank))
            }
        }
    }
    fun initialize() {
        for(suit in Suit.values())
        {
            for(rank in Rank.values())
            {
                cards.add(Card(suit,rank))
            }
        }
    }
    fun shuffle() {
        //cards.shuffle(Random.Default);
        cards.shuffle(Random.Default)
    }
    fun deal(players: List<Player>, cardsPerPlayer: Int) {
        shuffle()
        if (players.size * cardsPerPlayer > cards.size) {
            throw IllegalArgumentException("Not enough cards in the deck to deal to all players.")
        }

        for (i in 0 until cardsPerPlayer) {
            for (player in players) {
                val card = cards.removeAt(0)
                player.hand.add(card)
            }
        }
        talon.addAll(cards)
    }
    fun addTalonToPlayer(player : Player) {
        player.hand.addAll(talon)
    }
}