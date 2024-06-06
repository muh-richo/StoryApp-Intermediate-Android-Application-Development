package com.example.storyapp.data.local.preference

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    var token: String? = ""
): Parcelable