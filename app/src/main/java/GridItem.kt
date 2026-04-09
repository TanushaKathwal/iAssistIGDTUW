package com.example.iassistdatabase

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class GridItem(
    val imageResId: Int,
    val title: String
) : Parcelable