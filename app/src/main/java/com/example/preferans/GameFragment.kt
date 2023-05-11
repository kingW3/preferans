package com.example.preferans

import android.R
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.preferans.databinding.FragmentGameBinding

class GameFragment : Fragment() {
    private val viewModel: GameViewModel by activityViewModels()

    private var _binding: FragmentGameBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGameBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Observe changes in the game state and update UI elements accordingly
        viewModel.game.observe(viewLifecycleOwner, Observer { game ->
            // Update the player name text view with the current player's name
            binding.playerNameTextView.text = game.currentPlayer.name

            val cardAdapter = CardAdapter(game.currentPlayer.hand) { card ->
                // Handle card click here
            }
            binding.playerHandRecyclerView.adapter = cardAdapter
            binding.playerHandRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            // Update the player hand text view with the current player's hand
            //binding.playerHandTextView.text = game.currentPlayer.hand.joinToString(" ")

            // Update game log text view with game events
            binding.gameLogTextView.text = game.log.joinToString("\n")

            val bidOptions = game.availableBids()
            val spinnerAdapter = ArrayAdapter(requireContext(), R.layout.simple_spinner_item, bidOptions)
            spinnerAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
            binding.bidOptionsSpinner.adapter = spinnerAdapter
        })

        // Set up click listener for the "Place Bid" button
        binding.placeBidButton.setOnClickListener {
            val selectedBid = binding.bidOptionsSpinner.selectedItem as Bid
            viewModel.placeBid(selectedBid)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}