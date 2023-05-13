package com.example.preferans
import android.content.Context
import android.os.Bundle
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
class GameViewModel : ViewModel() {
    private val _game = MutableLiveData<Game>()
    val game: LiveData<Game> = _game

    fun createNewGame(players: List<Player>) {
        _game.value = Game(players)
    }

    fun placeBid(bid: Bid) {
        _game.value?.let { game ->
            // Update the current player's bid
            game.currentPlayer.placeBid(bid)
            updateBidCounter(game)
            updateWinningBid(game, bid)

            // Add a log entry for the placed bid
            //

            // Move to the next player and update the game state
            moveToNextPlayerBid(game)

            // Notify observers of the updated game state
            _game.value = game
        }
    }
    fun onCardClick(card: Card) {
        _game.value?.let { game ->
            game.log.add("${game.currentPlayer.name} clicked the card ${card.toString()}")
            if (game.biddingOver && !game.selectingGameOver) {
                if (game.deck.talon.size < 2) {
                    val karta =
                        game.currentPlayer.discardCard(game.currentPlayer.hand.indexOfFirst { it == card })
                    game.deck.talon.add(karta)
                }
            }
            _game.value = game
        }
    }
    private fun updateBidCounter(game: Game) {
        ++game.numOfBids
        if (game.numOfBids == game.players.size) {
            game.firstRound = false
        }
    }

    private fun updateWinningBid(game: Game, bid: Bid) {
        if (bid >= game.winningBid) {
            game.winningBid = bid
            game.winningBidPlayer = game.currentPlayer
            game.currentBid = if (game.numOfBids <= 2) 2 * bid.value else ++game.currentBid
        }
    }

    private fun moveToNextPlayerBid(game: Game) {
        var player = game.getNextPlayer()
        if (game.players.filter { it.bid != Bid.PASS }.isEmpty())
        {
            // TODO end game and start new round
            return
        }
        while (player.bid == Bid.PASS) {
            ++game.numOfBids
            player = game.getNextPlayer()
        }
        handlePlayerTurn(game, player)
    }
    private fun moveToNextPlayerDefend(game: Game) {
        var player = game.getNextPlayer()
        if(player == game.winningBidPlayer || player.defendingDecision == PlayerDecision.PASS)
            player = game.getNextPlayer()
        //val defenders =
    }
    private fun handlePlayerTurn(game: Game, player: Player) {
        if (game.winningBidPlayer == player) {
            game.biddingOver = true
            when {
                player.bid < Bid.GAME -> {
                    handleWinningBid(game, player)
                }
                player.bid == Bid.GAME -> {
                    // TODO: Select the game
                }
            }
        }
    }

    private fun handleWinningBid(game: Game, player: Player) {
        game.log.add("${player.name} won the bidding with ${game.winningBid}")
        game.log.add(game.deck.talon.joinToString(" "))
        game.logTalon.add(game.deck.talon.joinToString(" "))
        game.deck.addTalonToPlayer(player)
        // TODO: Select suit and discard two cards
    }
    fun selectGame(selectedGame: Bid) {
        _game.value?.let { game ->
            game.selectedGame = selectedGame
            game.log.add("Selected game: $selectedGame")
            game.selectingGameOver = true
            game.getNextPlayer()
            //game.startGame(selectedGame)

            // Notify observers of the updated game state
            _game.value = game
        }
    }
    private fun handleDecisions(decisions: Map<Player, PlayerDecision>) {
        // handle the decisions here, for example:
        val defenders = decisions.filter { it.value == PlayerDecision.DEFEND }.keys
        if (defenders.isEmpty()) {
            // the player who selected the game wins, handle this case
        } else if (defenders.size == 1) {
            // one player defends, they can choose to do it alone, call the other player or declare a contra
            // handle this case
        } else {
            // handle other cases
        }
    }

    fun decideDefend(playerDecision: PlayerDecision)
    {
        game.value?.let { game ->
            // Update the current player's bid
            game.currentPlayer.decideDefend(playerDecision)
            //updateBidCounter(game)
            //updateWinningBid(game, bid)

            // Add a log entry for the placed bid
            game.log.add("${game.currentPlayer.name} placed a bid of $playerDecision")

            // Move to the next player and update the game state
            val defenders = game.players.filter { it != game.winningBidPlayer }
            val decisions = defenders.map { it.defendingDecision }
            if (PlayerDecision.CALL_PARTNER in decisions) {
                game.defendingDecisionOver = true
                // Start game here maybe?
                _game.value = game
                return
            }

            if(decisions.filter { it == PlayerDecision.SAME || it == PlayerDecision.PASS }.size == 2) {
                game.defendingDecisionOver = true
                // Start game here maybe?
                _game.value = game
                return
            }
            moveToNextPlayerDefend(game)

            // Notify observers of the updated game state
            _game.value = game
        }
    }
    fun saveState(outState: Bundle) {
        outState.putParcelable("game_state", game.value)
    }

    fun restoreState(savedInstanceState: Bundle?) {
        savedInstanceState?.let {
            val restoredGame: Game? = savedInstanceState.getParcelable("game_state")
            if (restoredGame != null) {
                _game.postValue(restoredGame!!)
            }
        }
    }
    fun forceRefresh() {
        // Depending on your implementation, you might want to create a new game or just update the current one
        _game.value = game.value
    }
    // Add other methods to interact with the game and update LiveData objects
}