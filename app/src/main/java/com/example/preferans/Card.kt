package com.example.preferans

import android.os.Parcel
import android.os.Parcelable

data class Card(val suit: Suit, val rank: Rank) : Parcelable {
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

    constructor(parcel: Parcel) : this(
        Suit.values()[parcel.readInt()], Rank.values()[parcel.readInt()]
    ) {
    }

    fun fileName(): String {
        return if(rank > Rank.TEN) "english_pattern_${rank.name.lowercase()}_of_${suit.name.lowercase()}" else "english_pattern_${rank.toString().lowercase()}_of_${suit.name.lowercase()}"
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(suit.ordinal)
        parcel.writeInt(rank.ordinal)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Card> {
        override fun createFromParcel(parcel: Parcel): Card {
            return Card(parcel)
        }

        override fun newArray(size: Int): Array<Card?> {
            return arrayOfNulls(size)
        }
    }
}