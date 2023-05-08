package com.example.preferans
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
            game.log.add("${game.currentPlayer.name} placed a bid of $bid")

            // Move to the next player and update the game state
            moveToNextPlayer(game)

            // Notify observers of the updated game state
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
            game.currentBid = if (game.firstRound) 2 * bid.value else ++game.currentBid
        }
    }

    private fun moveToNextPlayer(game: Game) {
        var player = game.getNextPlayer()
        while (player.bid == Bid.PASS) {
            ++game.numOfBids
            player = game.getNextPlayer()
        }
        handlePlayerTurn(game, player)
    }

    private fun handlePlayerTurn(game: Game, player: Player) {
        if (game.winningBidPlayer == player) {
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
        game.deck.addTalonToPlayer(player)
        // TODO: Select suit and discard two cards
    }
    fun selectGame(selectedGame: Bid) {
        _game.value?.let { game ->
            game.selectedGame = selectedGame
            game.log.add("Selected game: $selectedGame")

            // TODO: Implement the logic for the selected game
            // ...

            // Notify observers of the updated game state
            _game.value = game
        }
    }
    // Add other methods to interact with the game and update LiveData objects
}