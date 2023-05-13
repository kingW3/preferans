package com.example.preferans

import android.content.Context
import android.util.Log
import androidx.annotation.StringRes

enum class Bid(val value: Int, @StringRes val stringResId: Int) : DisplayNameProvider {
    PASS(-1, R.string.bid_pass),
    NONE(0, R.string.bid_none),
    SPADE(2, R.string.bid_spade),
    DIAMOND(3, R.string.bid_diamond),
    HEART(4, R.string.bid_heart),
    CLUB(5, R.string.bid_club),
    BETL(6, R.string.bid_betl),
    SANS(7, R.string.bid_sans),
    PREFERANS(8, R.string.bid_preferans),
    GAME(9, R.string.bid_game),
    GAMESPADE(10, R.string.bid_game_spade),
    GAMEDIAMOND(11, R.string.bid_game_diamond),
    GAMEHEART(12, R.string.bid_game_heart),
    GAMECLUB(13, R.string.bid_game_club),
    GAMEBETL(14, R.string.bid_game_betl),
    GAMESANS(15, R.string.bid_game_sans),
    GAMEPREFERANS(16, R.string.bid_game_preferans);

    companion object {
        fun fromValue(value: Int): Bid {
            return values().firstOrNull { it.value == value }
                ?: throw IllegalArgumentException("Invalid bid value: $value")
        }

        fun fromResourceName(resourceName: String): Bid {
            return values().firstOrNull { it.name == resourceName }
                ?: throw IllegalArgumentException("Invalid bid value: $resourceName")
        }
    }

    override fun getDisplayName(context: Context): String {
        Log.d("Bid", "Getting display name for: $this in language: ${context.resources.configuration.locales[0].language}")
        return context.getString(stringResId)
    }
}