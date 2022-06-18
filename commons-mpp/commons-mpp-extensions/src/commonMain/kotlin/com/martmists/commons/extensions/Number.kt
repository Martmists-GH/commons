package com.martmists.commons.extensions

import kotlin.math.pow
import kotlin.math.round

fun Float.roundTo(precision: Int) : Float {
    val factor = (10f).pow(precision)
    return round(this * factor) / factor
}

fun Double.roundTo(precision: Int) : Double {
    val factor = (10.0).pow(precision)
    return round(this * factor) / factor
}
