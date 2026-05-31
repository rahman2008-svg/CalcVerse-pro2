package com.example.data

import java.time.LocalDate
import java.time.temporal.ChronoUnit
import kotlin.math.*

object CalculatorEngines {

    // --- Expression Evaluator for Basic & Scientific Calculations ---
    fun evaluateExpression(expression: String): Double {
        // Safe preprocess: convert π and e names, and change multiply symbol if any
        val sanitized = expression
            .replace("×", "*")
            .replace("÷", "/")
            .replace("π", "3.141592653589793")
            .replace("e", "2.718281828459045")
            .trim()
        if (sanitized.isEmpty()) return 0.0
        return ExpressionEvaluator(sanitized).parse()
    }

    private class ExpressionEvaluator(val str: String) {
        var pos = -1
        var ch = 0

        fun nextChar() {
            ch = if (++pos < str.length) str[pos].code else -1
        }

        fun eat(charToEat: Int): Boolean {
            while (ch == ' '.code) nextChar()
            if (ch == charToEat) {
                nextChar()
                return true
            }
            return false
        }

        fun parse(): Double {
            nextChar()
            val x = parseExpression()
            if (pos < str.length) throw RuntimeException("Unexpected: " + ch.toChar())
            return x
        }

        fun parseExpression(): Double {
            var x = parseTerm()
            while (true) {
                if (eat('+'.code)) x += parseTerm()
                else if (eat('-'.code)) x -= parseTerm()
                else break
            }
            return x
        }

        fun parseTerm(): Double {
            var x = parseFactor()
            while (true) {
                if (eat('*'.code)) x *= parseFactor()
                else if (eat('/'.code)) x /= parseFactor()
                else break
            }
            return x
        }

        fun parseFactor(): Double {
            if (eat('+'.code)) return parseFactor()
            if (eat('-'.code)) return -parseFactor()

            var x: Double
            val startPos = this.pos
            if (eat('('.code)) {
                x = parseExpression()
                eat(')'.code)
            } else if ((ch >= '0'.code && ch <= '9'.code) || ch == '.'.code) {
                while ((ch >= '0'.code && ch <= '9'.code) || ch == '.'.code) nextChar()
                val numStr = str.substring(startPos, this.pos)
                x = numStr.toDoubleOrNull() ?: 0.0
            } else if (ch >= 'a'.code && ch <= 'z'.code) {
                while (ch >= 'a'.code && ch <= 'z'.code) nextChar()
                val func = str.substring(startPos, this.pos)
                if (eat('('.code)) {
                    x = parseExpression()
                    eat(')'.code)
                } else {
                    x = parseFactor()
                }
                x = when (func) {
                    "sqrt" -> sqrt(x)
                    "sin" -> sin(Math.toRadians(x))
                    "cos" -> cos(Math.toRadians(x))
                    "tan" -> tan(Math.toRadians(x))
                    "log" -> log10(x)
                    "ln" -> ln(x)
                    else -> throw RuntimeException("Unknown function: $func")
                }
            } else {
                throw RuntimeException("Unexpected character: " + ch.toChar())
            }

            if (eat('^'.code)) x = x.pow(parseFactor())

            return x
        }
    }

    // --- Engineering Base Conversion ---
    data class BaseConversionResult(
        val decimal: String,
        val binary: String,
        val octal: String,
        val hex: String
    )

    fun convertBase(input: String, fromBase: Int): BaseConversionResult {
        if (input.isBlank()) return BaseConversionResult("", "", "", "")
        return try {
            val decimalVal = when (fromBase) {
                2 -> input.toLong(2)
                8 -> input.toLong(8)
                10 -> input.toLong()
                16 -> input.toLong(16)
                else -> 0L
            }
            BaseConversionResult(
                decimal = decimalVal.toString(10),
                binary = decimalVal.toString(2),
                octal = decimalVal.toString(8),
                hex = decimalVal.toString(16).uppercase()
            )
        } catch (e: Exception) {
            BaseConversionResult("Error", "Error", "Error", "Error")
        }
    }

    // --- GPA Calculator ---
    fun calculateGpa(grades: List<String>, credits: List<Double>): Double {
        if (grades.isEmpty() || grades.size != credits.size) return 0.0
        var totalPoints = 0.0
        var totalCredits = 0.0
        for (i in grades.indices) {
            val gradePoints = when (grades[i].uppercase().trim()) {
                "A", "A+" -> 4.0
                "A-" -> 3.7
                "B+" -> 3.3
                "B" -> 3.0
                "B-" -> 2.7
                "C+" -> 2.3
                "C" -> 2.0
                "C-" -> 1.7
                "D+" -> 1.3
                "D" -> 1.0
                "F" -> 0.0
                else -> 0.0
            }
            totalPoints += gradePoints * credits[i]
            totalCredits += credits[i]
        }
        return if (totalCredits > 0) totalPoints / totalCredits else 0.0
    }

    // --- CGPA Calculator ---
    fun calculateCgpa(semestersGpas: List<Double>, semestersCredits: List<Double>): Double {
        if (semestersGpas.isEmpty() || semestersGpas.size != semestersCredits.size) return 0.0
        var totalPoints = 0.0
        var totalCredits = 0.0
        for (i in semestersGpas.indices) {
            totalPoints += semestersGpas[i] * semestersCredits[i]
            totalCredits += semestersCredits[i]
        }
        return if (totalCredits > 0) totalPoints / totalCredits else 0.0
    }

    // --- Percentage Calculations ---
    fun getPercentageValue(percent: Double, total: Double): Double = (percent * total) / 100.0

    fun getPercentageOf(portion: Double, total: Double): Double = if (total != 0.0) (portion / total) * 100.0 else 0.0

    fun getPercentageChange(old: Double, new: Double): Double {
        return if (old != 0.0) ((new - old) / old) * 100.0 else 0.0
    }

    // --- Age Calculator ---
    data class AgeResult(
        val years: Int,
        val months: Int,
        val days: Int,
        val nextBirthdayDaysLeft: Long
    )

    fun calculateAge(dob: LocalDate, targetDate: LocalDate = LocalDate.now()): AgeResult {
        val years = ChronoUnit.YEARS.between(dob, targetDate).toInt()
        val birthMonthDayThisYear = dob.withYear(targetDate.year)
        val months = ChronoUnit.MONTHS.between(dob.plusYears(years.toLong()), targetDate).toInt()
        val days = ChronoUnit.DAYS.between(dob.plusYears(years.toLong()).plusMonths(months.toLong()), targetDate).toInt()

        var nextBirthday = dob.withYear(targetDate.year)
        if (nextBirthday.isBefore(targetDate) || nextBirthday.isEqual(targetDate)) {
            nextBirthday = nextBirthday.plusYears(1)
        }
        val nextBirthdayDaysLeft = ChronoUnit.DAYS.between(targetDate, nextBirthday)

        return AgeResult(years, months, days, nextBirthdayDaysLeft)
    }

    // --- Date Difference Calculator ---
    data class DateDiffResult(
        val totalDays: Long,
        val years: Int,
        val months: Int,
        val days: Int
    )

    fun calculateDateDifference(start: LocalDate, end: LocalDate): DateDiffResult {
        val totalDays = ChronoUnit.DAYS.between(start, end)
        val years = ChronoUnit.YEARS.between(start, end).toInt()
        val months = ChronoUnit.MONTHS.between(start.plusYears(years.toLong()), end).toInt()
        val days = ChronoUnit.DAYS.between(start.plusYears(years.toLong()).plusMonths(months.toLong()), end).toInt()
        return DateDiffResult(totalDays, years, months, days)
    }

    // --- BMI Calculator ---
    data class BmiResult(
        val bmi: Double,
        val category: String,
        val healthTip: String
    )

    fun calculateBmi(weightKg: Double, heightCm: Double): BmiResult {
        if (heightCm <= 0.0) return BmiResult(0.0, "Unknown", "Height must be positive.")
        val bmi = weightKg / ((heightCm / 100.0).pow(2))
        val (category, tip) = when {
            bmi < 18.5 -> Pair("Underweight", "Try consuming nutrient-dense food and consult with a nutritionist.")
            bmi < 25.0 -> Pair("Normal", "Excellent! Keep up your healthy eating patterns and regular physical activity.")
            bmi < 30.0 -> Pair("Overweight", "Consider a balanced caloric deficit diet, and engage in modern cardio exercises.")
            else -> Pair("Obese", "Focus on structured lifestyle changes, portion controls, and regular health checkups.")
        }
        return BmiResult(bmi, category, tip)
    }

    // --- Calorie Calculator ---
    fun calculateDailyCalorieDemand(
        weightKg: Double,
        heightCm: Double,
        age: Int,
        isMale: Boolean,
        activityLevel: Double // Sedentary = 1.2, Light = 1.375, Moderate = 1.55, Active = 1.725, Very Active = 1.9
    ): Double {
        val bmr = if (isMale) {
            88.362 + (13.397 * weightKg) + (4.799 * heightCm) - (5.677 * age)
        } else {
            447.593 + (9.247 * weightKg) + (3.098 * heightCm) - (4.330 * age)
        }
        return bmr * activityLevel
    }

    // --- EMI / Loan Calculator ---
    data class EmiResult(
        val monthlyEmi: Double,
        val totalInterest: Double,
        val totalPayment: Double
    )

    fun calculateEmi(principal: Double, annualRate: Double, tenureMonths: Int): EmiResult {
        if (principal <= 0.0 || annualRate <= 0.0 || tenureMonths <= 0) return EmiResult(0.0, 0.0, 0.0)
        val r = (annualRate / 12.0) / 100.0
        val emi = (principal * r * (1.0 + r).pow(tenureMonths)) / ((1.0 + r).pow(tenureMonths) - 1.0)
        val totalPayment = emi * tenureMonths
        val totalInterest = totalPayment - principal
        return EmiResult(emi, totalInterest, totalPayment)
    }

    // --- Compound Interest Calculator ---
    data class CompoundInterestResult(
        val futureValue: Double,
        val interestEarned: Double
    )

    fun calculateCompoundInterest(
        principal: Double,
        annualRatePercent: Double,
        years: Double,
        compoundingFrequency: Int // 1=Annually, 4=Quarterly, 12=Monthly, 365=Daily
    ): CompoundInterestResult {
        val r = annualRatePercent / 100.0
        val n = compoundingFrequency.toDouble()
        val futureValue = principal * (1.0 + r / n).pow(n * years)
        val interestEarned = futureValue - principal
        return CompoundInterestResult(futureValue, interestEarned)
    }

    // --- Currency Converter (Offline Base Matrix USD) ---
    private val exchangeRatesToUsd = mapOf(
        "USD" to 1.0,
        "EUR" to 0.92,
        "GBP" to 0.79,
        "JPY" to 156.40,
        "CAD" to 1.37,
        "AUD" to 1.51,
        "INR" to 83.50,
        "AED" to 3.67,
        "SGD" to 1.35
    )

    fun convertCurrency(amount: Double, from: String, to: String): Double {
        val fromRate = exchangeRatesToUsd[from.uppercase()] ?: return 0.0
        val toRate = exchangeRatesToUsd[to.uppercase()] ?: return 0.0
        // Convert to USD base first, then to target currency
        val amountInUsd = amount / fromRate
        return amountInUsd * toRate
    }

    fun getSupportedCurrencies(): List<String> = exchangeRatesToUsd.keys.toList()

    // --- Unit Converter ---
    object UnitConverter {
        val lengthMap = mapOf(
            "m" to 1.0,
            "km" to 1000.0,
            "cm" to 0.01,
            "mm" to 0.001,
            "mile" to 1609.34,
            "yard" to 0.9144,
            "ft" to 0.3048,
            "inch" to 0.0254
        )

        val weightMap = mapOf(
            "kg" to 1.0,
            "g" to 0.001,
            "lb" to 0.45359237,
            "g_oz" to 0.028349523 // "oz" (ounce) -> g_oz to avoid conflicting keys if area also has oz names
        )

        val areaMap = mapOf(
            "sq_m" to 1.0,
            "sq_km" to 1000000.0,
            "sq_mile" to 2589988.11,
            "acre" to 4046.856,
            "hectare" to 10000.0,
            "sq_ft" to 0.092903
        )

        fun convertLength(value: Double, from: String, to: String): Double {
            val fromFactor = lengthMap[from] ?: return 0.0
            val toFactor = lengthMap[to] ?: return 0.0
            return (value * fromFactor) / toFactor
        }

        fun convertWeight(value: Double, from: String, to: String): Double {
            val fromFactor = weightMap[from] ?: return 0.0
            val toFactor = weightMap[to] ?: return 0.0
            return (value * fromFactor) / toFactor
        }

        fun convertArea(value: Double, from: String, to: String): Double {
            val fromFactor = areaMap[from] ?: return 0.0
            val toFactor = areaMap[to] ?: return 0.0
            return (value * fromFactor) / toFactor
        }

        fun convertTemperature(value: Double, from: String, to: String): Double {
            val celsius = when (from.uppercase()) {
                "C" -> value
                "F" -> (value - 32.0) * 5.0 / 9.0
                "K" -> value - 273.15
                else -> value
            }
            return when (to.uppercase()) {
                "C" -> celsius
                "F" -> celsius * 9.0 / 5.0 + 32.0
                "K" -> celsius + 273.15
                else -> celsius
            }
        }
    }

    // --- Tax Calculator ---
    data class TaxResult(
        val taxAmount: Double,
        val total: Double,
        val netPrice: Double
    )

    fun calculateTax(amount: Double, taxRatePercent: Double, isInclusive: Boolean): TaxResult {
        return if (isInclusive) {
            val taxFactor = 1.0 + (taxRatePercent / 100.0)
            val netPrice = amount / taxFactor
            val taxAmount = amount - netPrice
            TaxResult(taxAmount = taxAmount, total = amount, netPrice = netPrice)
        } else {
            val taxAmount = (amount * taxRatePercent) / 100.0
            val total = amount + taxAmount
            TaxResult(taxAmount = taxAmount, total = total, netPrice = amount)
        }
    }

    // --- Discount Calculator ---
    data class DiscountResult(
        val savings: Double,
        val finalPrice: Double,
        val taxPaid: Double
    )

    fun calculateDiscount(originalPrice: Double, discountPercent: Double, additionalTaxPercent: Double = 0.0): DiscountResult {
        val savings = (originalPrice * discountPercent) / 100.0
        val priceAfterDiscount = originalPrice - savings
        val taxPaid = (priceAfterDiscount * additionalTaxPercent) / 100.0
        val finalPrice = priceAfterDiscount + taxPaid
        return DiscountResult(savings, finalPrice, taxPaid)
    }

    // --- Time Calculator ---
    data class TimeResult(
        val hours: Int,
        val minutes: Int,
        val seconds: Int,
        val totalSeconds: Long
    )

    fun calculateTimeDifference(h1: Int, m1: Int, s1: Int, h2: Int, m2: Int, s2: Int, operationIsAdd: Boolean): TimeResult {
        val seconds1 = h1 * 3600L + m1 * 60L + s1
        val seconds2 = h2 * 3600L + m2 * 60L + s2
        val totalSeconds = if (operationIsAdd) {
            seconds1 + seconds2
        } else {
            max(0L, seconds1 - seconds2)
        }

        val hours = (totalSeconds / 3600).toInt()
        val minutes = ((totalSeconds % 3600) / 60).toInt()
        val seconds = (totalSeconds % 60).toInt()
        return TimeResult(hours, minutes, seconds, totalSeconds)
    }
}
