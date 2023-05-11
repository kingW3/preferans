package com.example.preferans

data class Card(val suit: Suit, val rank: Rank) {
    override fun toString(): String {
        return "${rank}${suit}"
    }
    val cardComparator = Comparator<Card> { card1, card2 ->
        val suitOrder = listOf(Suit.SPADES, Suit.HEARTS, Suit.CLUBS, Suit.DIAMONDS)
        val suitComparison = suitOrder.indexOf(card1.suit) - suitOrder.indexOf(card2.suit)

        if (suitComparison != 0) {
            suitComparison
        } else {
            card1.rank.compareTo(card2.rank)
        }
    }
    fun fileName(): String {
        return if(rank > Rank.TEN && rank < Rank.ACE) "${rank.name.lowercase()}_of_${suit.name.lowercase()}2" else "${rank.name.lowercase()}_of_${suit.name.lowercase()}"
    }
}