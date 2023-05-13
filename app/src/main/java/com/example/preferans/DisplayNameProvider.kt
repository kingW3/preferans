package com.example.preferans

import android.content.Context

interface DisplayNameProvider {
    fun getDisplayName(context: Context): String
}