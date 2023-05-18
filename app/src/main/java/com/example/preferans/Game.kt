package com.example.preferans

import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable

class Game(val players: List<Player>): Parcelable {
    var deck : Deck = Deck()
    var bula = 100
    var currentPlayerIndex = 0
    val currentPlayer get() = players[currentPlayerIndex]
    val defenders get() = players.filter { it != winningBidPlayer }
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
    var numOfDecisions = 0
    var numOfPlayingPlayers = -1
    var biddingOver = false
    var defendingDecisionOver = false
    var selectingGameOver = false
    var trumpSuit : Suit? = null
    var trickSuit : Suit? = null
    val log: MutableList<String> = mutableListOf()
    val logTalon: MutableList<String> = mutableListOf()
    val mainTrick : MutableMap<Card,Player> = mutableMapOf()

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

    fun availableDecisions() : List<PlayerDecision> {
        val nonVoters = players.filter { it.defendingDecision == PlayerDecision.NONE && it != winningBidPlayer}
        val decisions = mutableListOf<PlayerDecision>()
        if(nonVoters.isEmpty())
        {
            decisions.add(PlayerDecision.SAME)
            decisions.add(PlayerDecision.CONTRA)
            //TODO Implement Contra/ReContra/SubContra/MortContra into bidding
            if((players.filter {it.defendingDecision == PlayerDecision.PASS}).size == 1) {
                decisions.add(PlayerDecision.CALL_PARTNER)
                //return PlayerDecision.values().filter {it == PlayerDecision.SAME || it == PlayerDecision.CALL_PARTNER || it == PlayerDecision.CONTRA }
            }
        }
        else
        {
            decisions.add(PlayerDecision.PASS)
            decisions.add(PlayerDecision.DEFEND)
        }
        return decisions
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
    fun startGame(selectedGame: Bid) {
        // set up the game state
        // ...

        // start the game
        // ...
    }
    fun playerDecisions(selectedGame: Bid): Map<Player, PlayerDecision> {
        val decisions = mutableMapOf<Player, PlayerDecision>()
        for (player in players) {
            val decision = player.makeDecision(selectedGame) // makeDecision would be a method you implement in the Player class
            decisions[player] = decision
            if (decision == PlayerDecision.CONTRA) {
                // handle doubling the points of the game
            }
        }
        return decisions
    }
    fun decideDefend(playerDecision: PlayerDecision) {
        currentPlayer.decideDefend(playerDecision)
        ++numOfDecisions
        // Add a log entry for the placed bid
        log.add("${currentPlayer.name} placed a bid of $playerDecision")

        // Move to the next player and update the game state
        val defenders = players.filter { it != winningBidPlayer }
        val decisions = defenders.map { it.defendingDecision }
        if (PlayerDecision.CALL_PARTNER in decisions) {
            defendingDecisionOver = true
            // Start game here maybe?
            numOfPlayingPlayers = 3
            return
        }

        if (decisions.filter { it == PlayerDecision.SAME || it == PlayerDecision.PASS }.size == 2) {
            defendingDecisionOver = true
            // Start game here maybe?
            numOfPlayingPlayers = 3
            if (decisions.any { it == PlayerDecision.PASS })
                numOfPlayingPlayers = 2
            return
        }
        moveToNextPlayerDefend()
    }
    private fun moveToNextPlayerDefend() {
        var player = getNextPlayer()
        while(player == winningBidPlayer || player.defendingDecision == PlayerDecision.PASS)
            player = getNextPlayer()
        //val defenders =
    }
    fun wonTrick() : Player {
        val sortedKeys = mainTrick.entries.sortedWith(compareByDescending<Map.Entry<Card, Player>> { it.key.suit == trumpSuit }
            .thenByDescending { it.key.suit == trickSuit }
            .thenByDescending { it.key.rank.value }
        )
        val player = sortedKeys[0].value
        val cards = sortedKeys.map { it.key }
        player.tricks.add(cards)
        mainTrick.clear()
        trickSuit = null
        log.add("${player.name} won the trick with ${sortedKeys[0].key} and won $cards")
        currentPlayerIndex = players.indexOf(player)
        return player
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
    fun copy(): Game {
        val copiedPlayers = players.map { it.copy() }
        val copiedGame = Game(copiedPlayers)
        copiedGame.deck = deck.copy()
        copiedGame.bula = bula
        copiedGame.currentPlayerIndex = currentPlayerIndex
        copiedGame.dealerIndex = dealerIndex
        copiedGame.selectedGame = selectedGame
        copiedGame.firstRound = firstRound
        copiedGame.secondRound = secondRound
        copiedGame.winningBid = winningBid
        copiedGame.winningBidPlayer = winningBidPlayer?.copy()
        copiedGame.scores.putAll(scores.mapValues { it.value.toMutableMap() })
        copiedGame.currentBid = currentBid
        copiedGame.numOfBids = numOfBids
        copiedGame.biddingOver = biddingOver
        copiedGame.defendingDecisionOver = defendingDecisionOver
        copiedGame.selectingGameOver = selectingGameOver
        copiedGame.log.addAll(log)
        copiedGame.logTalon.addAll(logTalon)
        copiedGame.mainTrick.putAll(mainTrick)
        return copiedGame
    }
}