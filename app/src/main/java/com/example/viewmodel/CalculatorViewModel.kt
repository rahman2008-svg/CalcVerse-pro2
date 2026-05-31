package com.example.viewmodel

import android.app.Application
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.CalculatorEngines
import com.example.data.HistoryEntity
import com.example.data.PreferencesManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate

class CalculatorViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val historyDao = db.historyDao()
    private val prefs = PreferencesManager(application)

    // --- Preferences State ---
    val themeMode = MutableStateFlow(prefs.themeMode)
    val keepHistory = MutableStateFlow(prefs.keepHistory)
    val vibrationFeedback = MutableStateFlow(prefs.vibrationFeedback)

    // --- Calculation History reactive stream ---
    val historyState: StateFlow<List<HistoryEntity>> = historyDao.getAllHistory()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun setTheme(theme: String) {
        prefs.themeMode = theme
        themeMode.value = theme
    }

    fun setKeepHistoryEnabled(enabled: Boolean) {
        prefs.keepHistory = enabled
        keepHistory.value = enabled
    }

    fun setVibrationEnabled(enabled: Boolean) {
        prefs.vibrationFeedback = enabled
        vibrationFeedback.value = enabled
    }

    // Insert history entry safely
    fun saveToHistory(calcType: String, expression: String, result: String) {
        if (!keepHistory.value) return
        viewModelScope.launch {
            historyDao.insertHistory(
                HistoryEntity(
                    calculatorType = calcType,
                    inputExpression = expression,
                    result = result
                )
            )
        }
    }

    fun deleteHistoryItem(id: Int) {
        viewModelScope.launch {
            historyDao.deleteHistoryById(id)
        }
    }

    fun clearAllHistory() {
        viewModelScope.launch {
            historyDao.clearAllHistory()
        }
    }

    // --- State Variables for 18 Calculators ---

    // 1. Basic Calculator State
    val basicExpression = mutableStateOf("")
    val basicResultResult = mutableStateOf("")

    fun appendToBasic(str: String) {
        if (str == "C") {
            basicExpression.value = ""
            basicResultResult.value = ""
        } else if (str == "⌫") {
            if (basicExpression.value.isNotEmpty()) {
                basicExpression.value = basicExpression.value.substring(0, basicExpression.value.length - 1)
            }
        } else if (str == "=") {
            try {
                val r = CalculatorEngines.evaluateExpression(basicExpression.value)
                basicResultResult.value = formatResultDouble(r)
                saveToHistory("Basic Calculator", basicExpression.value, basicResultResult.value)
            } catch (e: Exception) {
                basicResultResult.value = "Error"
            }
        } else {
            basicExpression.value += str
        }
    }

    // 2. Scientific Calculator State
    val sciExpression = mutableStateOf("")
    val sciResultResult = mutableStateOf("")

    fun appendToSci(str: String) {
        if (str == "C") {
            sciExpression.value = ""
            sciResultResult.value = ""
        } else if (str == "⌫") {
            if (sciExpression.value.isNotEmpty()) {
                sciExpression.value = sciExpression.value.substring(0, sciExpression.value.length - 1)
            }
        } else if (str == "=") {
            try {
                val r = CalculatorEngines.evaluateExpression(sciExpression.value)
                sciResultResult.value = formatResultDouble(r)
                saveToHistory("Scientific Calculator", sciExpression.value, sciResultResult.value)
            } catch (e: Exception) {
                sciResultResult.value = "Error"
            }
        } else {
            // Auto paren or formatting standard
            val addition = when (str) {
                "sin", "cos", "tan", "log", "ln", "sqrt" -> "$str("
                else -> str
            }
            sciExpression.value += addition
        }
    }

    // 3. Engineering Base State
    val engInput = mutableStateOf("")
    val engFromBase = mutableStateOf(10) // 2, 8, 10, 16
    val engResult = mutableStateOf(CalculatorEngines.BaseConversionResult("", "", "", ""))

    fun calculateEngineering() {
        val res = CalculatorEngines.convertBase(engInput.value, engFromBase.value)
        engResult.value = res
        if (engInput.value.isNotBlank() && res.decimal != "Error") {
            saveToHistory("Engineering Base Converter", "${engInput.value} (base ${engFromBase.value})", "Hex: ${res.hex}, Bin: ${res.binary}")
        }
    }

    // 4. GPA Calculator State
    data class GpaItem(val grade: String, val credits: Double)
    val gpaCourses = mutableStateListOf<GpaItem>()
    val tempGpaGrade = mutableStateOf("A")
    val tempGpaCredits = mutableStateOf("3")
    val gpaResult = mutableStateOf(0.0)

    fun addGpaCourse() {
        val cr = tempGpaCredits.value.toDoubleOrNull() ?: 1.0
        gpaCourses.add(GpaItem(tempGpaGrade.value, cr))
        recalcGpa()
    }

    fun removeGpaCourse(index: Int) {
        gpaCourses.removeAt(index)
        recalcGpa()
    }

    private fun recalcGpa() {
        val r = CalculatorEngines.calculateGpa(gpaCourses.map { it.grade }, gpaCourses.map { it.credits })
        gpaResult.value = r
    }

    fun saveGpaToHistory() {
        if (gpaCourses.isNotEmpty()) {
            val expr = "${gpaCourses.size} courses entered"
            val resStr = "GPA: ${"%.2f".format(gpaResult.value)}"
            saveToHistory("GPA Calculator", expr, resStr)
        }
    }

    // 5. CGPA Calculator State
    data class CgpaItem(val gpa: Double, val credits: Double)
    val cgpaSemesters = mutableStateListOf<CgpaItem>()
    val tempCgpaGpa = mutableStateOf("3.5")
    val tempCgpaCredits = mutableStateOf("15")
    val cgpaResult = mutableStateOf(0.0)

    fun addCgpaSemester() {
        val g = tempCgpaGpa.value.toDoubleOrNull() ?: 3.0
        val cr = tempCgpaCredits.value.toDoubleOrNull() ?: 12.0
        cgpaSemesters.add(CgpaItem(g, cr))
        recalcCgpa()
    }

    fun removeCgpaSemester(index: Int) {
        cgpaSemesters.removeAt(index)
        recalcCgpa()
    }

    private fun recalcCgpa() {
        val r = CalculatorEngines.calculateCgpa(cgpaSemesters.map { it.gpa }, cgpaSemesters.map { it.credits })
        cgpaResult.value = r
    }

    fun saveCgpaToHistory() {
        if (cgpaSemesters.isNotEmpty()) {
            val expr = "${cgpaSemesters.size} semesters entered"
            val resStr = "CGPA: ${"%.2f".format(cgpaResult.value)}"
            saveToHistory("CGPA Calculator", expr, resStr)
        }
    }

    // 6. Percentage Calculator State
    val pctValP = mutableStateOf("")
    val pctValTotal = mutableStateOf("")
    val pctValResult = mutableStateOf("")

    val pctOfPortion = mutableStateOf("")
    val pctOfTotal = mutableStateOf("")
    val pctOfResult = mutableStateOf("")

    val pctChgOld = mutableStateOf("")
    val pctChgNew = mutableStateOf("")
    val pctChgResult = mutableStateOf("")

    fun calculatePct1() {
        val p = pctValP.value.toDoubleOrNull() ?: 0.0
        val tot = pctValTotal.value.toDoubleOrNull() ?: 0.0
        val res = CalculatorEngines.getPercentageValue(p, tot)
        pctValResult.value = formatResultDouble(res)
        saveToHistory("Percentage", "$p% of $tot", pctValResult.value)
    }

    fun calculatePct2() {
        val part = pctOfPortion.value.toDoubleOrNull() ?: 0.0
        val tot = pctOfTotal.value.toDoubleOrNull() ?: 0.0
        val res = CalculatorEngines.getPercentageOf(part, tot)
        pctOfResult.value = "${"%.2f".format(res)}%"
        saveToHistory("Percentage", "$part is what % of $tot", pctOfResult.value)
    }

    fun calculatePct3() {
        val o = pctChgOld.value.toDoubleOrNull() ?: 0.0
        val n = pctChgNew.value.toDoubleOrNull() ?: 0.0
        val res = CalculatorEngines.getPercentageChange(o, n)
        val sign = if (res >= 0) "+" else ""
        pctChgResult.value = "$sign${"%.2f".format(res)}%"
        saveToHistory("Percentage Difference", "Change from $o to $n", pctChgResult.value)
    }

    // 7. Age Calculator State
    val ageDobDate = mutableStateOf(LocalDate.of(2000, 1, 1))
    val ageTargetDate = mutableStateOf(LocalDate.now())
    val ageResult = mutableStateOf<CalculatorEngines.AgeResult?>(null)

    fun calculateAge() {
        val res = CalculatorEngines.calculateAge(ageDobDate.value, ageTargetDate.value)
        ageResult.value = res
        saveToHistory("Age Calculator", "DOB: ${ageDobDate.value}", "${res.years}y ${res.months}m ${res.days}d")
    }

    // 8. Date Difference State
    val dateDiffStart = mutableStateOf(LocalDate.now())
    val dateDiffEnd = mutableStateOf(LocalDate.now().plusMonths(1))
    val dateDiffResult = mutableStateOf<CalculatorEngines.DateDiffResult?>(null)

    fun calculateDateDiff() {
        val res = CalculatorEngines.calculateDateDifference(dateDiffStart.value, dateDiffEnd.value)
        dateDiffResult.value = res
        saveToHistory("Date Difference", "From ${dateDiffStart.value} to ${dateDiffEnd.value}", "${res.totalDays} days total")
    }

    // 9. BMI Calculator State
    val bmiHeight = mutableStateOf("175")
    val bmiWeight = mutableStateOf("70")
    val bmiResult = mutableStateOf<CalculatorEngines.BmiResult?>(null)

    fun calculateBmi() {
        val w = bmiWeight.value.toDoubleOrNull() ?: 0.0
        val h = bmiHeight.value.toDoubleOrNull() ?: 0.0
        val res = CalculatorEngines.calculateBmi(w, h)
        bmiResult.value = res
        saveToHistory("BMI Calculator", "Height: ${h}cm, Weight: ${w}kg", "BMI: ${"%.1f".format(res.bmi)} (${res.category})")
    }

    // 10. Calorie Calculator State
    val calWeight = mutableStateOf("70")
    val calHeight = mutableStateOf("175")
    val calAge = mutableStateOf("25")
    val calIsMale = mutableStateOf(true)
    val calActivityIndex = mutableStateOf(1.375) // Default to Light Activity multiplier
    val calResult = mutableStateOf(2000.0)

    fun calculateCalories() {
        val w = calWeight.value.toDoubleOrNull() ?: 70.0
        val h = calHeight.value.toDoubleOrNull() ?: 175.0
        val a = calAge.value.toIntOrNull() ?: 25
        val r = CalculatorEngines.calculateDailyCalorieDemand(w, h, a, calIsMale.value, calActivityIndex.value)
        calResult.value = r
        saveToHistory("Calorie Calculator", "W:$w, H:$h, Age:$a, Male:${calIsMale.value}", "Demand: ${r.toInt()} kcal/day")
    }

    // 11. EMI Calculator State
    val emiPrincipal = mutableStateOf("50000")
    val emiRate = mutableStateOf("8.5")
    val emiTenure = mutableStateOf("24") // months
    val emiResult = mutableStateOf<CalculatorEngines.EmiResult?>(null)

    fun calculateEmi() {
        val p = emiPrincipal.value.toDoubleOrNull() ?: 0.0
        val r = emiRate.value.toDoubleOrNull() ?: 0.0
        val t = emiTenure.value.toIntOrNull() ?: 0
        val res = CalculatorEngines.calculateEmi(p, r, t)
        emiResult.value = res
        saveToHistory("EMI Calculator", "P:$p, Rate:$r%, $t mo", "EMI: ${formatResultDouble(res.monthlyEmi)}")
    }

    // 12. Loan Calculator State (can share or detail amortization values)
    val loanPrincipal = mutableStateOf("100000")
    val loanRate = mutableStateOf("7.2")
    val loanYears = mutableStateOf("5") // years
    val loanResult = mutableStateOf<CalculatorEngines.EmiResult?>(null)

    fun calculateLoan() {
        val p = loanPrincipal.value.toDoubleOrNull() ?: 0.0
        val r = loanRate.value.toDoubleOrNull() ?: 0.0
        val t = (loanYears.value.toDoubleOrNull() ?: 0.0 * 12.0).toInt()
        val finalTenure = if (t <= 0) ((loanYears.value.toIntOrNull() ?: 1) * 12) else t
        val res = CalculatorEngines.calculateEmi(p, r, finalTenure)
        loanResult.value = res
        saveToHistory("Loan Calculator", "P:$p, Rate:$r%, ${loanYears.value} yr", "Emi/mo: ${formatResultDouble(res.monthlyEmi)}")
    }

    // 13. Compound Interest Calculator State
    val ciPrincipal = mutableStateOf("10000")
    val ciRate = mutableStateOf("5.5")
    val ciYears = mutableStateOf("5")
    val ciCompounding = mutableStateOf(12) // monthly default
    val ciResult = mutableStateOf<CalculatorEngines.CompoundInterestResult?>(null)

    fun calculateCi() {
        val p = ciPrincipal.value.toDoubleOrNull() ?: 0.0
        val r = ciRate.value.toDoubleOrNull() ?: 0.0
        val y = ciYears.value.toDoubleOrNull() ?: 0.0
        val res = CalculatorEngines.calculateCompoundInterest(p, r, y, ciCompounding.value)
        ciResult.value = res
        saveToHistory("Compound Interest", "P:$p, R:$r%, $y yr", "FV: ${formatResultDouble(res.futureValue)}")
    }

    // 14. Currency State
    val curAmount = mutableStateOf("100")
    val curFrom = mutableStateOf("USD")
    val curTo = mutableStateOf("EUR")
    val curResult = mutableStateOf("92.00")

    fun getCurrencies(): List<String> = CalculatorEngines.getSupportedCurrencies()

    fun calculateCurrency() {
        val amt = curAmount.value.toDoubleOrNull() ?: 0.0
        val res = CalculatorEngines.convertCurrency(amt, curFrom.value, curTo.value)
        curResult.value = "%.2f".format(res)
        saveToHistory("Currency Converter", "$amt ${curFrom.value}", "${curResult.value} ${curTo.value}")
    }

    fun swapCurrencies() {
        val temp = curFrom.value
        curFrom.value = curTo.value
        curTo.value = temp
        calculateCurrency()
    }

    // 15. Unit Converter State
    val unitCategory = mutableStateOf("Length") // Length, Weight, Temp, Area
    val unitInput = mutableStateOf("1")
    val unitFrom = mutableStateOf("m")
    val unitTo = mutableStateOf("cm")
    val unitResult = mutableStateOf("100.0")

    fun updateUnitCategory(cat: String) {
        unitCategory.value = cat
        when (cat) {
            "Length" -> { unitFrom.value = "m"; unitTo.value = "cm" }
            "Weight" -> { unitFrom.value = "kg"; unitTo.value = "g" }
            "Temperature" -> { unitFrom.value = "C"; unitTo.value = "F" }
            "Area" -> { unitFrom.value = "sq_m"; unitTo.value = "sq_ft" }
        }
        calculateUnit()
    }

    fun calculateUnit() {
        val inp = unitInput.value.toDoubleOrNull() ?: 0.0
        val r = when (unitCategory.value) {
            "Length" -> CalculatorEngines.UnitConverter.convertLength(inp, unitFrom.value, unitTo.value)
            "Weight" -> CalculatorEngines.UnitConverter.convertWeight(inp, unitFrom.value, unitTo.value)
            "Temperature" -> CalculatorEngines.UnitConverter.convertTemperature(inp, unitFrom.value, unitTo.value)
            "Area" -> CalculatorEngines.UnitConverter.convertArea(inp, unitFrom.value, unitTo.value)
            else -> 0.0
        }
        unitResult.value = formatResultDouble(r)
        saveToHistory("Unit Converter", "$inp ${unitFrom.value}", "${unitResult.value} ${unitTo.value}")
    }

    // 16. Tax Calculator State
    val taxAmount = mutableStateOf("1000")
    val taxRate = mutableStateOf("18")
    val taxInclusive = mutableStateOf(false)
    val taxResult = mutableStateOf<CalculatorEngines.TaxResult?>(null)

    fun calculateTax() {
        val amt = taxAmount.value.toDoubleOrNull() ?: 0.0
        val r = taxRate.value.toDoubleOrNull() ?: 0.0
        val res = CalculatorEngines.calculateTax(amt, r, taxInclusive.value)
        taxResult.value = res
        saveToHistory("Tax Calculator", "Amt:$amt, Rate:$r%, Inc:${taxInclusive.value}", "Total: ${formatResultDouble(res.total)}, Tax: ${formatResultDouble(res.taxAmount)}")
    }

    // 17. Discount Calculator State
    val discPrice = mutableStateOf("100")
    val discPercent = mutableStateOf("20")
    val discTaxPercent = mutableStateOf("5")
    val discResult = mutableStateOf<CalculatorEngines.DiscountResult?>(null)

    fun calculateDiscount() {
        val p = discPrice.value.toDoubleOrNull() ?: 0.0
        val d = discPercent.value.toDoubleOrNull() ?: 0.0
        val t = discTaxPercent.value.toDoubleOrNull() ?: 0.0
        val res = CalculatorEngines.calculateDiscount(p, d, t)
        discResult.value = res
        saveToHistory("Discount Calculator", "Price:$p, -$d%, Tax:$t%", "Final: ${formatResultDouble(res.finalPrice)}, Save: ${formatResultDouble(res.savings)}")
    }

    // 18. Time Calculator State
    val timeH1 = mutableStateOf("5")
    val timeM1 = mutableStateOf("30")
    val timeS1 = mutableStateOf("0")
    val timeH2 = mutableStateOf("2")
    val timeM2 = mutableStateOf("15")
    val timeS2 = mutableStateOf("0")
    val timeOperationAdd = mutableStateOf(true)
    val timeResult = mutableStateOf<CalculatorEngines.TimeResult?>(null)

    fun calculateTime() {
        val h1 = timeH1.value.toIntOrNull() ?: 0
        val m1 = timeM1.value.toIntOrNull() ?: 0
        val s1 = timeS1.value.toIntOrNull() ?: 0
        val h2 = timeH2.value.toIntOrNull() ?: 0
        val m2 = timeM2.value.toIntOrNull() ?: 0
        val s2 = timeS2.value.toIntOrNull() ?: 0
        val res = CalculatorEngines.calculateTimeDifference(h1, m1, s1, h2, m2, s2, timeOperationAdd.value)
        timeResult.value = res
        val sign = if (timeOperationAdd.value) "+" else "-"
        saveToHistory("Time Calculator", "($h1 h $m1 m) $sign ($h2 h $m2 m)", "${res.hours}h ${res.minutes}m ${res.seconds}s")
    }

    // Formatting utilities
    private fun formatResultDouble(d: Double): String {
        return if (d == d.toLong().toDouble()) {
            d.toLong().toString()
        } else {
            // Keep at most 4 decimal places
            "%.4f".format(d).trimEnd('0').trimEnd('.')
        }
    }
}
