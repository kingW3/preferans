package com.example.preferans

enum class Bid(val value: Int) {
    PASS(-1),
    NONE(0),
    SPADE(2),
    DIAMOND(3),
    HEART(4),
    CLUB(5),
    BETL(6),
    SANS(7),
    PREFERANS(8),
    GAME(9),
    GAMESPADE(10),
    GAMEDIAMOND(11),
    GAMEHEART(12),
    GAMECLUB(13),
    GAMEBETL(14),
    GAMESANS(15),
    GAMEPREFERANS(16);
    companion object {
        fun fromValue(value: Int): Bid {
            return values().firstOrNull { it.value == value } ?: throw IllegalArgumentException("Invalid bid value: $value")
        }
    }
}