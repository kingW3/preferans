package com.example.preferans

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.preferans.databinding.ActivitySelectGameBinding

class SelectGameActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySelectGameBinding
    private val viewModel: GameViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySelectGameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        val adapter = AvailableGamesAdapter(viewModel.game.value?.availableGames() ?: listOf()) { selectedGame ->
            viewModel.selectGame(selectedGame)
            finish()
        }
        binding.availableGamesRecyclerView.adapter = adapter
        binding.availableGamesRecyclerView.layoutManager = LinearLayoutManager(this)
    }
}