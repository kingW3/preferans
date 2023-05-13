package com.example.preferans

import android.R
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.preferans.databinding.FragmentGameBinding
import java.util.*

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
                viewModel.onCardClick(card)
            }
            binding.playerHandRecyclerView.adapter = cardAdapter
            val gridLayoutManager = GridLayoutManager(context, 4) // replace 2 with the number of columns you want
            binding.playerHandRecyclerView.layoutManager = gridLayoutManager
            // Update the player hand text view with the current player's hand
            //binding.playerHandTextView.text = game.currentPlayer.hand.joinToString(" ")

            // Update game log text view with game events
            binding.gameLogTextView.text = game.log.joinToString("\n")
            binding.placeBidButton.text = if(!game.biddingOver) "Place Bid" else if(!game.selectingGameOver) "Select Game" else "Decide"

            binding.placeBidButton.setBackgroundColor(if(!game.biddingOver) Color.GREEN else if(!game.selectingGameOver) Color.BLUE else Color.MAGENTA)
            //binding.placeBidButton.setBackgroundColor(if(!game.biddingOver) Color.argb(255,98,0,238) else if(!game.selectingGameOver) Color.argb(255,98,100,247) else Color.argb(255,98,20,255))
            Color.argb(255,98,0,238)
            (activity as MainActivity).languageContext.observe(viewLifecycleOwner) { languageContext ->
                val bidOptions =
                    if (!game.biddingOver) game.availableBids() else if (!game.selectingGameOver) game.availableGames() else game.availableDecisions()
                updateSpinner(languageContext, bidOptions)
            }
            binding.talonTextView.text = game.logTalon.joinToString()
            if (game.logTalon.isNotEmpty()) {
                binding.talonTextView.visibility = View.VISIBLE
                binding.talonLabel.visibility = View.VISIBLE
            }
            binding.placeBidButton.setOnClickListener {
                val x = (activity as MainActivity)
                val jezik = kotlin.random.Random.nextBoolean()
                //x.switchLanguage(Locale(if(jezik)"en" else "sr"))
                if(!game.biddingOver) {
                    val selectedBid = binding.bidOptionsSpinner.selectedItem as Bid
                    val tekst = "${game.currentPlayer.name} placed a bid of ${selectedBid.getDisplayName(requireContext())}"
                    game.log.add(tekst)
                    viewModel.placeBid(selectedBid)
                }
                else if(!game.selectingGameOver) {
                    val selectedGame = binding.bidOptionsSpinner.selectedItem as Bid
                    viewModel.selectGame(selectedGame)
                }
                else if(!game.defendingDecisionOver) {
                    val selectedDecision = binding.bidOptionsSpinner.selectedItem as PlayerDecision
                    viewModel.decideDefend(selectedDecision)
                }
                else {
                    binding.placeBidButton.visibility = View.GONE
                    binding.bidOptionsSpinner.visibility = View.GONE

                }
            }
        })

        /*(activity as MainActivity).currentLangLiveData.observe(viewLifecycleOwner) {
            Log.d("GameFragment", "Detected language change to: $it")
            updateSpinner()
        }*/

        // Set up click listener for the "Place Bid" button

    }
    /*private fun updateSpinner() {
        val bidOptions = if(!viewModel.game.value!!.biddingOver) viewModel.game.value!!.availableBids() else if (!viewModel.game.value!!.selectingGameOver) viewModel.game.value!!.availableGames() else viewModel.game.value!!.availableDecisions()
        val spinnerAdapter = object: ArrayAdapter<DisplayNameProvider>(requireContext(), R.layout.simple_spinner_item, bidOptions) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = convertView ?: layoutInflater.inflate(R.layout.simple_spinner_item, parent, false)
                val textView = view as TextView // assuming simple_spinner_item is a TextView
                textView.text = getItem(position)?.getDisplayName(requireContext())
                return view
            }

            override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = convertView ?: layoutInflater.inflate(R.layout.simple_spinner_dropdown_item, parent, false)
                val textView = view as TextView // assuming simple_spinner_dropdown_item is a TextView
                textView.text = getItem(position)?.getDisplayName(requireContext())
                return view
            }
        }
        binding.bidOptionsSpinner.adapter = spinnerAdapter
    }*/
    private fun updateSpinner(languageContext: Context, bidOptions: List<DisplayNameProvider>) {
        val spinnerAdapter = object : ArrayAdapter<DisplayNameProvider>(requireContext(), R.layout.simple_spinner_item, bidOptions) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = convertView ?: layoutInflater.inflate(R.layout.simple_spinner_item, parent, false)
                val textView = view as TextView // assuming simple_spinner_item is a TextView
                textView.text = getItem(position)?.getDisplayName(languageContext)
                return view
            }

            override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = convertView ?: layoutInflater.inflate(R.layout.simple_spinner_dropdown_item, parent, false)
                val textView = view as TextView // assuming simple_spinner_dropdown_item is a TextView
                textView.text = getItem(position)?.getDisplayName(languageContext)
                return view
            }
        }
        binding.bidOptionsSpinner.adapter = spinnerAdapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        viewModel.saveState(outState)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.restoreState(savedInstanceState)
    }
}