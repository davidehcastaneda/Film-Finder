package com.dehcast.filmfinder.utils

import android.content.res.Resources
import android.util.TypedValue
import androidx.annotation.DimenRes
import com.dehcast.filmfinder.R

fun Resources.getDpAsFloat(@DimenRes id: Int): Float {
    val value = TypedValue()
    getValue(R.dimen.target_discovery_viewholder_width, value, true)
    return TypedValue.complexToFloat(value.data)
}