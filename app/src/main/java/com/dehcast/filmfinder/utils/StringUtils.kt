package com.dehcast.filmfinder.utils

import android.view.View
import android.widget.TextView

fun String.getJustYear() = this.split("-")[0]

fun String?.setTextOrHideViewIfNoData(view: TextView) {
    view.hideIfPropertyIsNullOrResolveWithProperty(this) { notNullText ->
        view.text = notNullText as String
    }
}

fun String?.setTextOrHideViewIfNoData(view: TextView, template: Int) {
    if (this != null) view.context.getString(template, this).setTextOrHideViewIfNoData(view)
    else view.visibility = View.GONE
}