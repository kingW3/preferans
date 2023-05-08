package com.example.preferans

class Player(val name: String) {
    var hand : MutableList<Card> = mutableListOf()
    var bid : Bid = Bid.NONE
    fun playCard(cardIndex: Int): Card {
        if (cardIndex < 0 || cardIndex >= hand.size) {
            throw IllegalArgumentException("Invalid card index.")
        }

        val playedCard = hand[cardIndex]
        hand.removeAt(cardIndex)
        return playedCard
    }
    fun placeBid(bid: Bid) : Bid {
        this.bid = bid
        return bid
    }
    fun sortHand() {
        hand.sortWith(compareBy( { it.suit }, { it.rank }))
    }
}