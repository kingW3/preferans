package com.example.preferans
import android.os.Parcel
import android.os.Parcelable
class Player(var name: String) : Parcelable {
    var hand : MutableList<Card> = mutableListOf()
    var bid : Bid = Bid.NONE
    var defendingDecision : PlayerDecision = PlayerDecision.NONE
    var tricks : MutableList<List<Card>> = mutableListOf()
    var isPlaying = false
    constructor(parcel: Parcel) : this(parcel.readString()!!) {
        hand = parcel.createTypedArrayList(Card.CREATOR)!!
        bid = Bid.values()[parcel.readInt()]
    }

    fun playCard(cardIndex: Int): Card {
        if (cardIndex < 0 || cardIndex >= hand.size) {
            throw IllegalArgumentException("Invalid card index.")
        }

        val playedCard = hand[cardIndex]
        hand.removeAt(cardIndex)
        return playedCard
    }
    fun discardCard(cardIndex: Int): Card {
        if (cardIndex < 0 || cardIndex >= hand.size) {
            throw IllegalArgumentException("Invalid card index.")
        }

        val playedCard = hand[cardIndex]
        hand.removeAt(cardIndex)
        return playedCard
    }
    fun addTrick(trick: List<Card>) {
        tricks.add(trick)
    }
    fun placeBid(bid: Bid) : Bid {
        this.bid = bid
        return bid
    }
    fun sortHand() {
        hand.sortWith(compareBy( { it.suit }, { it.rank }))
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeTypedList(hand)
        parcel.writeInt(bid.ordinal)
    }

    override fun describeContents(): Int {
        return 0
    }

    fun makeDecision(selectedGame: Bid, ): PlayerDecision {
        return PlayerDecision.PASS
    }
    fun decideDefend(playerDecision: PlayerDecision) : PlayerDecision
    {
        defendingDecision = playerDecision
        return playerDecision
    }

    companion object CREATOR : Parcelable.Creator<Player> {
        override fun createFromParcel(parcel: Parcel): Player {
            return Player(parcel)
        }

        override fun newArray(size: Int): Array<Player?> {
            return arrayOfNulls(size)
        }
    }
    fun copy(): Player {
        val copiedPlayer = Player(name)
        copiedPlayer.hand.addAll(hand)
        copiedPlayer.bid = bid
        copiedPlayer.defendingDecision = defendingDecision
        return copiedPlayer
    }
}