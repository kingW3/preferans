package com.example.preferans
import android.os.Parcel
import android.os.Parcelable
class Player(var name: String) : Parcelable {
    var hand : MutableList<Card> = mutableListOf()
    var bid : Bid = Bid.NONE

    constructor(parcel: Parcel) : this(parcel.readString()!!) {

    }

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

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Player> {
        override fun createFromParcel(parcel: Parcel): Player {
            return Player(parcel)
        }

        override fun newArray(size: Int): Array<Player?> {
            return arrayOfNulls(size)
        }
    }
}