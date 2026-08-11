package com.example.utils

import java.text.NumberFormat
import java.util.Locale

object CurrencyFormatter {
    fun formatPrice(price: Double, symbol: String = "₹"): String {
        val numberFormat = NumberFormat.getNumberInstance(Locale("en", "IN"))
        numberFormat.minimumFractionDigits = if (price % 1.0 == 0.0) 0 else 2
        numberFormat.maximumFractionDigits = 2
        val formattedNumber = numberFormat.format(price)
        return "$symbol$formattedNumber"
    }
}
