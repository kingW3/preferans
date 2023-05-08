package com.example.preferans

data class Card(val suit: Suit, val rank: Rank) {
    override fun toString(): String {
        val rankSymbol = when (rank) {
            Rank.SEVEN -> "7"
            Rank.EIGHT -> "8"
            Rank.NINE -> "9"
            Rank.TEN -> "10"
            Rank.JACK -> "J"
            Rank.QUEEN -> "Q"
            Rank.KING -> "K"
            Rank.ACE -> "A"
        }

        val suitSymbol = when (suit) {
            Suit.HEARTS -> "♥"
            Suit.DIAMONDS -> "♦"
            Suit.CLUBS -> "♣"
            Suit.SPADES -> "♠"
        }

        return "$rankSymbol$suitSymbol"
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
}