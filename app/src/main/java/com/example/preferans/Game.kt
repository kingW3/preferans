package com.example.preferans

import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable

class Game(val players: List<Player>): Parcelable {
    var deck : Deck = Deck()
    var bula = 100
    var currentPlayerIndex = 0
    val currentPlayer get() = players[currentPlayerIndex]
    var dealerIndex = 0
    var selectedGame : Bid = Bid.NONE
    val dealer get() = players[dealerIndex]
    var firstRound = true
    var secondRound = false
    var winningBid: Bid = Bid.PASS
    var winningBidPlayer: Player? = null
    private val scores: MutableMap<Player, MutableMap<Player, Int>> = mutableMapOf()
    var currentBid : Int = 2*Bid.PASS.value
    var numOfBids = 0
    val log: MutableList<String> = mutableListOf()

    constructor(parcel: Parcel) : this(parcel.createTypedArrayList(Player.CREATOR)!!) {
        bula = parcel.readInt()
        currentPlayerIndex = parcel.readInt()
        dealerIndex = parcel.readInt()
        firstRound = parcel.readByte() != 0.toByte()
        secondRound = parcel.readByte() != 0.toByte()
        currentBid = parcel.readInt()
        numOfBids = parcel.readInt()
    }

    init {
        // Initialize the scores map
        deck.deal(players,10)
        for (player in players) {
            player.sortHand()
            val playerScores = mutableMapOf<Player, Int>()
            for (opponent in players) {
                if (player != opponent) {
                    playerScores[opponent] = 0
                }
            }
            playerScores[player] = bula
            scores[player] = playerScores
        }
    }

    // Other methods...
    fun getNextPlayer(): Player {
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size
        return players[currentPlayerIndex]
    }

    fun updateScores(player: Player, opponent: Player, points: Int) {
        scores[player]?.let { playerScores ->
            playerScores[opponent]?.let { currentScore ->
                playerScores[opponent] = currentScore + points
            }
        }
    }

    fun getScores(player: Player): Map<Player, Int>? {
        return scores[player]
    }
    fun availableBids(): List<Bid> {
        val bids = mutableListOf<Bid>()
        bids.add(Bid.PASS)
        if(firstRound)
        {
            when (winningBid) {
                Bid.PASS -> {
                    bids.add(Bid.SPADE)
                }
                Bid.GAME -> {

                }
                else -> {
                    bids.add(Bid.fromValue(winningBid.value+1))
                }
            }
            bids.add(Bid.GAME)
        }
        else
        {
            if(winningBid == Bid.PASS)
            {
                return bids
            }
            if(winningBid >= Bid.GAME)
            {
                bids.addAll(Bid.values().filter { it > winningBid })
                return bids;
            }
            if(currentBid % 2 == 0)
            {
                bids.add(winningBid)
            }
            else
            {
                bids.add(Bid.fromValue(winningBid.value+1))
            }
        }
        //log.add("Current bid placed a bid of $currentBid")
        return bids
    }
    fun availableGames() : List<Bid> {
        if(winningBid < Bid.GAME)
        {
            return Bid.values().filter { it < Bid.GAME && it >= winningBid }
        }
        else if(winningBid == Bid.GAME)
        {
            return Bid.values().filter { it > Bid.GAME }
        }
        else
        {
            throw IllegalArgumentException("Function shouldn't be called if there's only one GAME")
        }
    }
    fun bidding1(): Bid {
        var passesInARow = 0
        var currentBid = Bid.PASS

        while (passesInARow < players.size) {
            val currentPlayer = getNextPlayer()
            val playerBid = currentPlayer.placeBid(currentBid) // This should be implemented in the Player class

            if (playerBid != Bid.PASS && playerBid.value > currentBid.value) {
                currentBid = playerBid
                winningBidPlayer = currentPlayer
                passesInARow = 0
            } else {
                passesInARow++
            }
        }

        winningBid = currentBid
        return winningBid
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(bula)
        parcel.writeInt(currentPlayerIndex)
        parcel.writeInt(dealerIndex)
        parcel.writeByte(if (firstRound) 1 else 0)
        parcel.writeByte(if (secondRound) 1 else 0)
        parcel.writeInt(currentBid)
        parcel.writeInt(numOfBids)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Game> {
        override fun createFromParcel(parcel: Parcel): Game {
            return Game(parcel)
        }

        override fun newArray(size: Int): Array<Game?> {
            return arrayOfNulls(size)
        }
    }
}