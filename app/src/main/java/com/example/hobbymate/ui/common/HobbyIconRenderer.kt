package com.example.hobbymate.ui.common

import android.widget.TextView
import androidx.annotation.DrawableRes
import com.example.hobbymate.R
import com.example.hobbymate.model.HobbyIconStyle
import com.example.hobbymate.model.HobbyProfile

fun TextView.renderHobbyIcon(profile: HobbyProfile) {
    text = profile.icon
    setBackgroundResource(profile.iconStyle.backgroundRes)
}

@get:DrawableRes
private val HobbyIconStyle.backgroundRes: Int
    get() = when (this) {
        HobbyIconStyle.RUNNING -> R.drawable.bg_hobby_icon_running
        HobbyIconStyle.MUSIC -> R.drawable.bg_hobby_icon_music
        HobbyIconStyle.ART -> R.drawable.bg_hobby_icon_art
        HobbyIconStyle.COOKING -> R.drawable.bg_hobby_icon_cooking
        HobbyIconStyle.PHOTOGRAPHY -> R.drawable.bg_hobby_icon_photography
        HobbyIconStyle.READING -> R.drawable.bg_hobby_icon_reading
        HobbyIconStyle.DANCE -> R.drawable.bg_hobby_icon_dance
        HobbyIconStyle.PLANT -> R.drawable.bg_hobby_icon_plant
        HobbyIconStyle.TRAVEL -> R.drawable.bg_hobby_icon_travel
        HobbyIconStyle.YOGA -> R.drawable.bg_hobby_icon_yoga
    }
