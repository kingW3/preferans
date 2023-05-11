package com.example.preferans

import android.content.Context
import android.util.AttributeSet
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.res.ResourcesCompat

class CardView : AppCompatImageView {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    fun setCard(card: Card) {
        val resId = resources.getIdentifier(card.fileName(), "drawable", context.packageName)
        val drawable = ResourcesCompat.getDrawable(resources, resId, null)
        setImageDrawable(drawable)
    }

}