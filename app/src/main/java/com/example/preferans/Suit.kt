package com.example.preferans

enum class Suit(val color: String) {
    SPADES("black"),
    DIAMONDS("red"),
    CLUBS("black"),
    HEARTS("red");
    override fun toString(): String {
        return when (this) {
            HEARTS -> "♥"
            DIAMONDS -> "♦"
            CLUBS -> "♣"
            SPADES -> "♠"
        }
    }
}