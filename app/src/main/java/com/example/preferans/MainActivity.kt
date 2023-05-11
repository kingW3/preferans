package com.example.preferans

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels

class MainActivity : AppCompatActivity() {
    private val viewModel: GameViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize game with player names
        viewModel.createNewGame(
            listOf(
                Player("Alice"),
                Player("Bob"),
                Player("Charlie")
            )
        )
    }
}