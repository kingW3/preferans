package com.example.preferans

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.viewModels
import androidx.lifecycle.Observer

class MainActivity : AppCompatActivity() {
    private val viewModel: GameViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val playerNameTextView: TextView = findViewById(R.id.playerName)
        val playerHandTextView: TextView = findViewById(R.id.playerHand)
        playerHandTextView.textSize = 37F
        val bidOptionsSpinner: Spinner = findViewById(R.id.bidOptions)
        val placeBidButton: Button = findViewById(R.id.placeBidButton)
        val gameLogTextView: TextView = findViewById(R.id.gameLog)
        val selectGameButton: Button = findViewById(R.id.placeBidButton)
        selectGameButton.setOnClickListener {
            val intent = Intent(this, SelectGameActivity::class.java)
            startActivity(intent)
        }
        // Initialize game with player names
        viewModel.createNewGame(
            listOf(
                Player("Alice"),
                Player("Bob"),
                Player("Charlie")
            )
        )

        // Set up spinner adapter for bid options
        //val bidOptions = Bid.values().filter { it != Bid.PASS }

        // Observe changes in the game state and update UI elements accordingly
        viewModel.game.observe(this, Observer { game ->
            // Update the player name text view with the current player's name
            playerNameTextView.text = game.currentPlayer.name //game.players[game.currentPlayerIndex].name //game.currentPlayer.name

            // Update the player hand text view with the current player's hand
            playerHandTextView.text = game.currentPlayer.hand.joinToString(" ")

            // Update game log text view with game events
            gameLogTextView.text = game.log.joinToString("\n")
            val bidOptions = game.availableBids()
            val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, bidOptions)
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            bidOptionsSpinner.adapter = spinnerAdapter
            // Update other UI elements as needed based on the game state
        })

        // Set up click listener for the "Place Bid" button
        placeBidButton.setOnClickListener {
            val selectedBid = bidOptionsSpinner.selectedItem as Bid
            viewModel.placeBid(selectedBid)

            // Update the UI with the new game state
        }


        // Set up click listeners for other interactive UI elements, like playing cards
    }
}