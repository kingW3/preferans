package com.example.preferans

import android.content.Context
import androidx.annotation.StringRes

enum class PlayerDecision(@StringRes val stringResId: Int) : DisplayNameProvider {
    NONE(R.string.player_decision_none),
    PASS(R.string.player_decision_pass),
    DEFEND(R.string.player_decision_defend),
    SAME(R.string.player_decision_same),
    CALL_PARTNER(R.string.player_decision_call_partner),
    CONTRA(R.string.player_decision_contra),
    RECONTRA(R.string.player_decision_recontra),
    SUBCONTRA(R.string.player_decision_subcontra),
    MORTCONTRA(R.string.player_decision_mortcontra);

    companion object {
        fun fromResourceName(resourceName: String): PlayerDecision {
            return PlayerDecision.values().firstOrNull { it.name == resourceName }
                ?: throw IllegalArgumentException("Invalid Player Decision value: $resourceName")
        }
    }

    override fun getDisplayName(context: Context): String {
        return context.getString(stringResId)
    }
}