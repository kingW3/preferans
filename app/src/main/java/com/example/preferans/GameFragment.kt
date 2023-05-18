package com.example.preferans

import com.example.preferans.R
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.example.preferans.databinding.FragmentGameBinding

class GameFragment : Fragment() {
    private val viewModel: GameViewModel by activityViewModels()

    private var _binding: FragmentGameBinding? = null
    private val binding get() = _binding!!
    private var previousGame : Game? = null
    val playerHandNumOfColumns = 4
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

            val cardAdapterHand = CardAdapter(game.currentPlayer.hand) { card ->
                viewModel.onCardClick(card)
            }
            val cardAdapterTrick = CardAdapter(game.mainTrick.keys.toList()) { card ->
                viewModel.onCardClick(card)
            }
            binding.mainTrickRecyclerView.adapter = cardAdapterTrick
            binding.playerHandRecyclerView.adapter = cardAdapterHand
            val gridLayoutManager = GridLayoutManager(context, playerHandNumOfColumns) // replace 2 with the number of columns you want
            binding.playerHandRecyclerView.layoutManager = gridLayoutManager
            // Update the player hand text view with the current player's hand
            //binding.playerHandTextView.text = game.currentPlayer.hand.joinToString(" ")

            // Update game log text view with game events
            binding.gameLogTextView.text = game.log.joinToString("\n")
            binding.placeBidButton.text = if(!game.biddingOver) "Place Bid" else if(!game.selectingGameOver) "Select Game" else "Decide"

            binding.placeBidButton.setBackgroundColor(if(!game.biddingOver) Color.GREEN else if(!game.selectingGameOver) Color.BLUE else Color.MAGENTA)
            //binding.placeBidButton.setBackgroundColor(if(!game.biddingOver) Color.argb(255,98,0,238) else if(!game.selectingGameOver) Color.argb(255,98,100,247) else Color.argb(255,98,20,255))
            //Color.argb(255,98,0,238)
            if(game.defendingDecisionOver) {
                if (binding.numOfTricksTaken.visibility == View.GONE)
                    binding.numOfTricksTaken.visibility = View.VISIBLE
                binding.numOfTricksTaken.text = "Num of tricks taken: ${game.currentPlayer.tricks.size}"
            }

            (activity as MainActivity).languageContext.observe(viewLifecycleOwner) { languageContext ->
                //val asd = this@GameFragment
                val bidOptions =
                    if (!game.biddingOver) game.availableBids() else if (!game.selectingGameOver) game.availableGames() else game.availableDecisions()
                if(previousGame?.currentPlayer?.name != game.currentPlayer.name || previousGame?.biddingOver != game.biddingOver || previousGame?.numOfDecisions != game.numOfDecisions)
                    updateSpinner(languageContext, bidOptions)
            }
            binding.talonTextView.text = game.logTalon.joinToString()
            if (game.logTalon.isNotEmpty()) {
                binding.talonTextView.visibility = View.VISIBLE
                binding.talonLabel.visibility = View.VISIBLE
            }
            if(game.biddingOver && !game.selectingGameOver) {
                //binding.placeBidButton.alpha = 0.5f // Reduce the opacity to visually indicate it's disabled
                if(game.deck.talon.size < 2) {
                    binding.placeBidButton.isEnabled = false
                    binding.placeBidButton.setBackgroundColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.disabled_button_color
                        )
                    ) // Set a different background color for disabled state
                }
                else {
                    binding.placeBidButton.isEnabled = true
                    binding.placeBidButton.setBackgroundColor(ContextCompat.getColor(
                        requireContext(),
                        R.color.purple_700
                    ))
                }
            }
            // Set up click listener for the "Place Bid" button
            binding.placeBidButton.setOnClickListener {
                //val x = (activity as MainActivity)
                //val jezik = kotlin.random.Random.nextBoolean()
                //x.switchLanguage(Locale(if(jezik)"en" else "sr"))
                if(!game.biddingOver) {
                    val selectedBid = binding.bidOptionsSpinner.selectedItem as Bid
                    val tekst = "${game.currentPlayer.name} placed a bid of ${selectedBid.getDisplayName(requireContext())}"
                    game.log.add(tekst)
                    viewModel.placeBid(selectedBid)
                }
                else if(!game.selectingGameOver) {
                    val selectedGame = binding.bidOptionsSpinner.selectedItem as Bid
                    val tekst = "${game.currentPlayer.name} selected the game ${selectedGame.getDisplayName(requireContext())}"
                    game.log.add(tekst)
                    viewModel.selectGame(selectedGame)
                }
                else if(!game.defendingDecisionOver) {
                    val selectedDecision = binding.bidOptionsSpinner.selectedItem as PlayerDecision
                    val tekst = "${game.currentPlayer.name} selected to ${selectedDecision.getDisplayName(requireContext())}"
                    game.log.add(tekst)
                    viewModel.decideDefend(selectedDecision)
                }
                else {
                    binding.placeBidButton.visibility = View.GONE
                    binding.bidOptionsSpinner.visibility = View.GONE

                }
            }
            val asd = this@GameFragment
            previousGame = game.copy()
        })

    }
    private fun updateSpinner(languageContext: Context, bidOptions: List<DisplayNameProvider>) {
        val spinnerAdapter = object : ArrayAdapter<DisplayNameProvider>(requireContext(), android.R.layout.simple_spinner_item, bidOptions) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = convertView ?: layoutInflater.inflate(android.R.layout.simple_spinner_item, parent, false)
                val textView = view as TextView // assuming simple_spinner_item is a TextView
                textView.text = getItem(position)?.getDisplayName(languageContext)
                return view
            }

            override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = convertView ?: layoutInflater.inflate(android.R.layout.simple_spinner_dropdown_item, parent, false)
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