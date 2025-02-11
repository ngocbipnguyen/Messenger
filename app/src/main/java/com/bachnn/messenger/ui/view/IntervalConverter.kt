package com.bachnn.messenger.ui.view

class IntervalConverter private constructor(private val x: Float) {
    private var a = 0f
    private var b = 0f
    fun fromInterval(a: Float, b: Float): IntervalConverter {
        this.a = a
        this.b = b
        return this
    }

    fun toInterval(c: Float, d: Float): Float {
        return c + (d - c) / (b - a) * (x - a)
    }

    companion object {
        fun convertNumber(x: Float): IntervalConverter {
            return IntervalConverter(x)
        }
    }
}
