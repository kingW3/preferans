package com.example.preferans

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AvailableGamesAdapter(
    private val availableGames: List<Bid>,
    private val onGameSelected: (Bid) -> Unit
) : RecyclerView.Adapter<AvailableGamesAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val gameNameTextView: TextView = view.findViewById(R.id.gameNameTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_available_game, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val game = availableGames[position]
        holder.gameNameTextView.text = game.toString()

        holder.itemView.setOnClickListener {
            onGameSelected(game)
        }
    }

    override fun getItemCount(): Int {
        return availableGames.size
    }
}