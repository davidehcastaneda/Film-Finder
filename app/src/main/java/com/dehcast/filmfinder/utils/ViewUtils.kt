package com.dehcast.filmfinder.utils

import android.view.View

fun View.hideIfPropertyIsNullOrResolveWithProperty(property: Any?, cta: (Any) -> Unit) {
    if (property != null) {
        cta(property)
        visibility = View.VISIBLE
    } else {
        visibility = View.GONE
    }
}