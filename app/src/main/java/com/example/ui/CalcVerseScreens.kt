package com.example.ui

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.CalculatorEngines
import com.example.ui.theme.*
import com.example.viewmodel.CalculatorViewModel
import kotlinx.coroutines.delay
import java.time.LocalDate
import java.time.format.DateTimeParseException

// Enumeration for Screens
sealed class CalcScreen {
    object Splash : CalcScreen()
    object Hub : CalcScreen()
    object History : CalcScreen()
    object Settings : CalcScreen()
    data class CalculatorDetail(val id: Int, val name: String) : CalcScreen()
}

// 18 Calculators Metadata
data class CalcMetadata(
    val id: Int,
    val name: String,
    val category: String,
    val icon: ImageVector,
    val description: String,
    val colorAccent: Color
)

@Composable
fun CalcVerseAppContent(viewModel: CalculatorViewModel) {
    var currentScreen by remember { mutableStateOf<CalcScreen>(CalcScreen.Splash) }
    val themeMode by viewModel.themeMode.collectAsState()

    val calculators = remember {
        listOf(
            CalcMetadata(1, "Basic Calculator", "Math", Icons.Default.Calculate, "Standard daily arithmetic grid with live expression parsing", Color(0xFF00E5FF)),
            CalcMetadata(2, "Scientific Calculator", "Math", Icons.Default.Science, "Advanced trigonometry, powers, logs, and absolute constants", Color(0xFFDF7AFF)),
            CalcMetadata(3, "Engineering Base Converter", "Engineering", Icons.Default.DeveloperMode, "Hexadecimal, decimal, octal, and binary instant bits tracker", Color(0xFFFFD54F)),
            CalcMetadata(4, "GPA Calculator", "Education", Icons.Default.Grade, "Quick grade point average based on credits and letter bounds", Color(0xFFFF4081)),
            CalcMetadata(5, "CGPA Calculator", "Education", Icons.Default.School, "Cumulative grade point average analyzer across multiple semesters", Color(0xFF00E5FF)),
            CalcMetadata(6, "Percentage Calculator", "Math", Icons.Default.Percent, "Yield percentage rates, percentage differences, and portion trends", Color(0xFFDF7AFF)),
            CalcMetadata(7, "Age Calculator", "Life", Icons.Default.Cake, "Total years, months, and countdown ticks until the next anniversary", Color(0xFFFFD54F)),
            CalcMetadata(8, "Date Difference", "Life", Icons.Default.DateRange, "Determine exact elapsed boundaries of days and years between dates", Color(0xFFFF4081)),
            CalcMetadata(9, "BMI Calculator", "Health", Icons.Default.MonitorWeight, "Assess body index metric classification and vital wellness tips", Color(0xFF00E5FF)),
            CalcMetadata(10, "Calorie Calculator", "Health", Icons.Default.LocalFireDepartment, "Evaluate active personal maintenance calories and target weight plans", Color(0xFFDF7AFF)),
            CalcMetadata(11, "EMI Calculator", "Finance", Icons.Default.AccountBalanceWallet, "Monthly Equated Installment tracker with comprehensive breakdown charts", Color(0xFFFFD54F)),
            CalcMetadata(12, "Loan Calculator", "Finance", Icons.Default.Payments, "Detailed simple or complex loan tenure principal and margin schedules", Color(0xFFFF4081)),
            CalcMetadata(13, "Compound Interest", "Finance", Icons.Default.TrendingUp, "Compound earnings calculator showing complete compounding intervals", Color(0xFF00E5FF)),
            CalcMetadata(14, "Currency Converter", "Finance", Icons.Default.CurrencyExchange, "Convert between popular currencies instantly with offline-ready rates", Color(0xFFDF7AFF)),
            CalcMetadata(15, "Unit Converter", "Engineering", Icons.Default.Straighten, "Metrology solver for lengths, temperatures, areas, weights, and scales", Color(0xFFFFD54F)),
            CalcMetadata(16, "Tax Calculator", "Finance", Icons.Default.ReceiptLong, "VAT or GST inclusive/exclusive calculation schemas made easy", Color(0xFFFF4081)),
            CalcMetadata(17, "Discount Calculator", "Finance", Icons.Default.LocalActivity, "Yield net savings and taxes on discounted commodities", Color(0xFF00E5FF)),
            CalcMetadata(18, "Time Calculator", "Life", Icons.Default.Schedule, "Add or subtract long durations of hours, minutes, and seconds", Color(0xFFDF7AFF))
        )
    }

    // Dynamic background brushing
    val bgModifier = when (themeMode) {
        "amoled" -> Modifier.background(Color.Black)
        "glass" -> Modifier.background(
            Brush.verticalGradient(
                colors = listOf(Color(0xFF090A14), Color(0xFF13152B), Color(0xFF0F1020))
            )
        )
        else -> Modifier.background(MaterialTheme.colorScheme.background)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .then(bgModifier)
            .windowInsetsPadding(WindowInsets.statusBars)
            .windowInsetsPadding(WindowInsets.navigationBars)
    ) {
        AnimatedContent(
            targetState = currentScreen,
            transitionSpec = {
                fadeIn(animationSpec = tween(350)) togetherWith fadeOut(animationSpec = tween(300))
            },
            label = "ScreenTransition"
        ) { screen ->
            when (screen) {
                is CalcScreen.Splash -> SplashScreen { currentScreen = CalcScreen.Hub }
                is CalcScreen.Hub -> HubScreen(
                    calculators = calculators,
                    onSelect = { resolved -> currentScreen = CalcScreen.CalculatorDetail(resolved.id, resolved.name) },
                    onGoHistory = { currentScreen = CalcScreen.History },
                    onGoSettings = { currentScreen = CalcScreen.Settings },
                    themeMode = themeMode
                )
                is CalcScreen.History -> HistoryScreen(
                    viewModel = viewModel,
                    onBack = { currentScreen = CalcScreen.Hub },
                    themeMode = themeMode
                )
                is CalcScreen.Settings -> SettingsScreen(
                    viewModel = viewModel,
                    onBack = { currentScreen = CalcScreen.Hub },
                    themeMode = themeMode
                )
                is CalcScreen.CalculatorDetail -> CalculatorDetailScreen(
                    calcId = screen.id,
                    calcName = screen.name,
                    viewModel = viewModel,
                    onBack = { currentScreen = CalcScreen.Hub },
                    themeMode = themeMode
                )
            }
        }
    }
}

// ================= SPLASH SCREEN =================
@Composable
fun SplashScreen(onFinish: () -> Unit) {
    var startAnimation by remember { mutableStateOf(false) }
    val scaleAnimation = animateFloatAsState(
        targetValue = if (startAnimation) 1.1f else 0.8f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "SplashScale"
    )

    LaunchedEffect(key1 = true) {
        startAnimation = true
        delay(1800)
        onFinish()
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.scale(scaleAnimation.value)
        ) {
            Box(
                modifier = Modifier
                    .size(110.dp)
                    .background(
                        Brush.radialGradient(listOf(Color(0xFF00E5FF), Color(0xFFDF7AFF))),
                        shape = RoundedCornerShape(24.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Calculate,
                    contentDescription = "CalcVerse Logo",
                    tint = Color.White,
                    modifier = Modifier.size(60.dp)
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "CalcVerse Pro",
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                fontFamily = FontFamily.SansSerif,
                color = Color.White,
                letterSpacing = 1.5.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Modern Computational Suite",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White.copy(alpha = 0.6f)
            )
        }
    }
}

// ================= HUB DASHBOARD SCREEN =================
@Composable
fun HubScreen(
    calculators: List<CalcMetadata>,
    onSelect: (CalcMetadata) -> Unit,
    onGoHistory: () -> Unit,
    onGoSettings: () -> Unit,
    themeMode: String
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }
    val categories = listOf("All", "Math", "Finance", "Health", "Engineering", "Life", "Education")

    val filteredList = remember(searchQuery, selectedCategory) {
        calculators.filter {
            (selectedCategory == "All" || it.category == selectedCategory) &&
                    (it.name.contains(searchQuery, ignoreCase = true) || it.description.contains(searchQuery, ignoreCase = true))
        }
    }

    val screenWidth = LocalConfiguration.current.screenWidthDp
    val columns = if (screenWidth > 600) 3 else 2 // Tablet responsiveness

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        // App bar styled with Elegant Dark theme spec
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "CalcVerse Pro",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = if (themeMode == "amoled") Color(0xFFD0BCFF) else if (themeMode == "glass") Color.White else MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "ALL-IN-ONE ENGINE",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    color = if (themeMode == "amoled") Color(0xFF938F99) else if (themeMode == "glass") Color.White.copy(alpha = 0.6f) else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(if (themeMode == "amoled") Color(0xFF1C1B1F) else Color.Transparent)
                        .border(
                            1.dp,
                            if (themeMode == "amoled") Color(0xFF49454F) else Color.Transparent,
                            CircleShape
                        )
                        .clickable { onGoHistory() }
                        .testTag("hub_history_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = "History Log",
                        tint = if (themeMode == "amoled") Color(0xFFD0BCFF) else if (themeMode == "glass") Color.White else MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(if (themeMode == "amoled") Color(0xFF1C1B1F) else Color.Transparent)
                        .border(
                            1.dp,
                            if (themeMode == "amoled") Color(0xFF49454F) else Color.Transparent,
                            CircleShape
                        )
                        .clickable { onGoSettings() }
                        .testTag("hub_settings_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Quick Settings",
                        tint = if (themeMode == "amoled") Color(0xFFD0BCFF) else if (themeMode == "glass") Color.White else MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        // Search Input
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .testTag("hub_search_bar"),
            placeholder = { Text("Search for computation engines...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search icon") },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { searchQuery = "" }) {
                        Icon(Icons.Default.Clear, contentDescription = "Clear search")
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = if (themeMode == "amoled" || themeMode == "glass") Color.White else MaterialTheme.colorScheme.onSurface,
                unfocusedTextColor = if (themeMode == "amoled" || themeMode == "glass") Color.White.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurface,
                focusedBorderColor = CosmicCyan,
                unfocusedBorderColor = if (themeMode == "amoled" || themeMode == "glass") Color.White.copy(alpha = 0.2f) else MaterialTheme.colorScheme.outline
            )
        )

        // Horizontal Category Tabs
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            categories.forEach { cat ->
                val isSelected = selectedCategory == cat
                Surface(
                    onClick = { selectedCategory = cat },
                    shape = RoundedCornerShape(12.dp),
                    color = if (isSelected) {
                        CosmicCyan
                    } else {
                        if (themeMode == "amoled" || themeMode == "glass") Color.White.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surfaceVariant
                    },
                    border = BorderStroke(
                        1.dp,
                        if (isSelected) CosmicCyan else if (themeMode == "amoled" || themeMode == "glass") Color.White.copy(alpha = 0.1f) else Color.Transparent
                    ),
                    modifier = Modifier.testTag("tab_cat_$cat")
                ) {
                    Text(
                        text = cat,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                        color = if (isSelected) Color.Black else if (themeMode == "amoled" || themeMode == "glass") Color.White else MaterialTheme.colorScheme.onSurface,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        fontSize = 13.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Calculations Grid
        if (filteredList.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.SearchOff,
                        contentDescription = "No Results",
                        tint = if (themeMode == "amoled" || themeMode == "glass") Color.White.copy(alpha = 0.3f) else Color.LightGray,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "No computation engines match your query.",
                        color = if (themeMode == "amoled" || themeMode == "glass") Color.White.copy(alpha = 0.6f) else Color.Gray,
                        fontSize = 14.sp
                    )
                }
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(columns),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 16.dp)
            ) {
                itemsIndexed(filteredList) { index, calc ->
                    Card(
                        onClick = { onSelect(calc) },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = when (themeMode) {
                                "amoled" -> Color(0xFF1C1B1F)
                                "glass" -> Color(0x18FFFFFF)
                                else -> MaterialTheme.colorScheme.surface
                            }
                        ),
                        border = BorderStroke(
                            width = 1.dp,
                            color = when (themeMode) {
                                "amoled" -> Color(0xFF49454F)
                                "glass" -> Color.White.copy(alpha = 0.15f)
                                else -> MaterialTheme.colorScheme.outlineVariant
                            }
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp)
                            .testTag("calc_card_${calc.id}")
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(12.dp),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .background(
                                            if (themeMode == "amoled") Color(0xFF4A4458) else calc.colorAccent.copy(alpha = 0.15f),
                                            shape = RoundedCornerShape(10.dp)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = calc.icon,
                                        contentDescription = calc.name,
                                        tint = if (themeMode == "amoled") Color(0xFFD0BCFF) else calc.colorAccent,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .background(
                                            if (themeMode == "amoled") Color(0xFF381E72) else if (themeMode == "glass") Color.White.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surfaceVariant,
                                            shape = RoundedCornerShape(6.dp)
                                        )
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = calc.category,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (themeMode == "amoled") Color(0xFFD0BCFF) else if (themeMode == "glass") Color.White.copy(alpha = 0.7f) else MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                            Column {
                                Text(
                                    text = calc.name,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    color = if (themeMode == "amoled") Color(0xFFE6E1E9) else if (themeMode == "glass") Color.White else MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = calc.description,
                                    fontSize = 10.sp,
                                    lineHeight = 12.sp,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis,
                                    color = if (themeMode == "amoled") Color(0xFF938F99) else if (themeMode == "glass") Color.White.copy(alpha = 0.5f) else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// ================= HISTORY SCREEN =================
@Composable
fun HistoryScreen(viewModel: CalculatorViewModel, onBack: () -> Unit, themeMode: String) {
    val historyLog by viewModel.historyState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack, modifier = Modifier.testTag("history_back")) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = if (themeMode == "amoled" || themeMode == "glass") Color.White else MaterialTheme.colorScheme.onSurface
                    )
                }
                Text(
                    text = "Calculation History",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (themeMode == "amoled" || themeMode == "glass") Color.White else MaterialTheme.colorScheme.onSurface
                )
            }
            if (historyLog.isNotEmpty()) {
                TextButton(
                    onClick = { viewModel.clearAllHistory() },
                    modifier = Modifier.testTag("clear_history_btn")
                ) {
                    Text("Clear All", color = HotPink, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (historyLog.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Inbox,
                        contentDescription = "No History",
                        tint = if (themeMode == "amoled" || themeMode == "glass") Color.White.copy(alpha = 0.3f) else Color.LightGray,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "History logs empty. Try performing calculations!",
                        color = if (themeMode == "amoled" || themeMode == "glass") Color.White.copy(alpha = 0.6f) else Color.Gray,
                        fontSize = 14.sp
                    )
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(historyLog) { item ->
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (themeMode == "amoled") Color(0xFF0B0B0B) else if (themeMode == "glass") Color(0x1AFFFFFF) else MaterialTheme.colorScheme.surface
                        ),
                        border = BorderStroke(
                            1.dp,
                            if (themeMode == "amoled") Color.White.copy(alpha = 0.1f) else if (themeMode == "glass") Color.White.copy(alpha = 0.15f) else MaterialTheme.colorScheme.outlineVariant
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = item.calculatorType,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = CosmicCyan
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = item.inputExpression,
                                    fontSize = 14.sp,
                                    color = if (themeMode == "amoled" || themeMode == "glass") Color.White else MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "= ${item.result}",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = NeonPurple
                                )
                            }
                            IconButton(
                                onClick = { viewModel.deleteHistoryItem(item.id) },
                                modifier = Modifier.testTag("delete_item_btn_${item.id}")
                            ) {
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = "Delete item",
                                    tint = HotPink.copy(alpha = 0.8f)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// ================= SETTINGS SCREEN =================
@Composable
fun SettingsScreen(viewModel: CalculatorViewModel, onBack: () -> Unit, themeMode: String) {
    val keepHistory by viewModel.keepHistory.collectAsState()
    val vibrationEnabled by viewModel.vibrationFeedback.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(onClick = onBack, modifier = Modifier.testTag("settings_back")) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back to dashboard",
                    tint = if (themeMode == "amoled" || themeMode == "glass") Color.White else MaterialTheme.colorScheme.onSurface
                )
            }
            Text(
                text = "CalcVerse Settings",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = if (themeMode == "amoled" || themeMode == "glass") Color.White else MaterialTheme.colorScheme.onSurface
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Themes section
        Text(
            text = "PREMIUM FLAVORS & THEMES",
            fontSize = 11.sp,
            fontWeight = FontWeight.ExtraBold,
            color = if (themeMode == "amoled") Color(0xFFD0BCFF) else CosmicCyan,
            letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(12.dp))

        val themeOptions = listOf(
            Triple("light", "Vibrant Light Theme", Icons.Default.LightMode),
            Triple("amoled", "Elegant Dark Theme", Icons.Default.DarkMode),
            Triple("glass", "Glassmorphic Translucency", Icons.Default.AutoAwesome),
            Triple("dynamic", "Dynamic Material You", Icons.Default.Palette)
        )

        themeOptions.forEach { opt ->
            val isSel = themeMode == opt.first
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.setTheme(opt.first) }
                    .padding(vertical = 12.dp, horizontal = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = opt.third,
                        contentDescription = opt.second,
                        tint = if (isSel) {
                            if (themeMode == "amoled") Color(0xFFD0BCFF) else CosmicCyan
                        } else if (themeMode == "amoled" || themeMode == "glass") {
                            Color.White.copy(alpha = 0.5f)
                        } else {
                            Color.Gray
                        },
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = opt.second,
                        color = if (isSel) {
                            if (themeMode == "amoled") Color(0xFFD0BCFF) else CosmicCyan
                        } else if (themeMode == "amoled" || themeMode == "glass") {
                            Color.White
                        } else {
                            MaterialTheme.colorScheme.onSurface
                        },
                        fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium,
                        fontSize = 14.sp
                    )
                }
                RadioButton(
                    selected = isSel,
                    onClick = { viewModel.setTheme(opt.first) },
                    colors = RadioButtonDefaults.colors(
                        selectedColor = if (themeMode == "amoled") Color(0xFFD0BCFF) else CosmicCyan
                    )
                )
            }
        }

        Divider(
            modifier = Modifier.padding(vertical = 16.dp),
            color = if (themeMode == "amoled" || themeMode == "glass") Color.White.copy(alpha = 0.12f) else MaterialTheme.colorScheme.outlineVariant
        )

        // Preferences section
        Text(
            text = "UTILITIES PREFERENCES",
            fontSize = 11.sp,
            fontWeight = FontWeight.ExtraBold,
            color = if (themeMode == "amoled") Color(0xFFD0BCFF) else CosmicCyan,
            letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(12.dp))

        // Toggle History
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Track Calculations History",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (themeMode == "amoled" || themeMode == "glass") Color.White else MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Save computing schedules to easily trace and query them later.",
                    fontSize = 11.sp,
                    color = if (themeMode == "amoled" || themeMode == "glass") Color.White.copy(alpha = 0.5f) else Color.Gray
                )
            }
            Switch(
                checked = keepHistory,
                onCheckedChange = { viewModel.setKeepHistoryEnabled(it) },
                colors = if (themeMode == "amoled") SwitchDefaults.colors(
                    checkedThumbColor = Color(0xFFD0BCFF),
                    checkedTrackColor = Color(0xFF381E72),
                    uncheckedThumbColor = Color(0xFF938F99),
                    uncheckedTrackColor = Color(0xFF4A4458)
                ) else SwitchDefaults.colors()
            )
        }

        // Toggle vibration feedback
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Keypad Vibration Taps",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (themeMode == "amoled" || themeMode == "glass") Color.White else MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Experience mechanical response vibration upon clicking calculator buttons.",
                    fontSize = 11.sp,
                    color = if (themeMode == "amoled" || themeMode == "glass") Color.White.copy(alpha = 0.5f) else Color.Gray
                )
            }
            Switch(
                checked = vibrationEnabled,
                onCheckedChange = { viewModel.setVibrationEnabled(it) },
                colors = if (themeMode == "amoled") SwitchDefaults.colors(
                    checkedThumbColor = Color(0xFFD0BCFF),
                    checkedTrackColor = Color(0xFF381E72),
                    uncheckedThumbColor = Color(0xFF938F99),
                    uncheckedTrackColor = Color(0xFF4A4458)
                ) else SwitchDefaults.colors()
            )
        }
    }
}

// ================= CALCULATORS DETAILED SCREEN SWITCHER =================
@Composable
fun CalculatorDetailScreen(
    calcId: Int,
    calcName: String,
    viewModel: CalculatorViewModel,
    onBack: () -> Unit,
    themeMode: String
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(onClick = onBack, modifier = Modifier.testTag("detail_back")) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = if (themeMode == "amoled" || themeMode == "glass") Color.White else MaterialTheme.colorScheme.onSurface
                )
            }
            Text(
                text = calcName,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = if (themeMode == "amoled" || themeMode == "glass") Color.White else MaterialTheme.colorScheme.onSurface
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            when (calcId) {
                1 -> BasicCalculatorView(viewModel, themeMode)
                2 -> ScientificCalculatorView(viewModel, themeMode)
                3 -> BaseConverterView(viewModel, themeMode)
                4 -> GpaCalculatorView(viewModel, themeMode)
                5 -> CgpaCalculatorView(viewModel, themeMode)
                6 -> PercentageCalculatorView(viewModel, themeMode)
                7 -> AgeCalculatorView(viewModel, themeMode)
                8 -> DateDiffView(viewModel, themeMode)
                9 -> BmiCalculatorView(viewModel, themeMode)
                10 -> CalorieCalculatorView(viewModel, themeMode)
                11 -> EmiCalculatorView(viewModel, themeMode)
                12 -> LoanCalculatorView(viewModel, themeMode)
                13 -> CompoundInterestView(viewModel, themeMode)
                14 -> CurrencyConverterView(viewModel, themeMode)
                15 -> UnitConverterView(viewModel, themeMode)
                16 -> TaxCalculatorView(viewModel, themeMode)
                17 -> DiscountCalculatorView(viewModel, themeMode)
                18 -> TimeCalculatorView(viewModel, themeMode)
                else -> {
                    Text("Implementation pending for $calcName", color = Color.White)
                }
            }
        }
    }
}

// ================= INDIVIDUAL CALCULATOR UIs =================

// Helper Card container aligning layout aesthetics
@Composable
fun CalcContainerCard(themeMode: String, content: @Composable ColumnScope.() -> Unit) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (themeMode == "amoled") Color(0xFF1C1B1F) else if (themeMode == "glass") Color(0x13FFFFFF) else MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(
            1.dp,
            if (themeMode == "amoled") Color(0xFF49454F) else if (themeMode == "glass") Color.White.copy(alpha = 0.18f) else MaterialTheme.colorScheme.outlineVariant
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            content()
        }
    }
}

// Custom Keypad Button
@Composable
fun KeypadButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    containerColor: Color = Color(0x0EFFFFFF),
    contentColor: Color = Color.White
) {
    Box(
        modifier = modifier
            .aspectRatio(1.2f)
            .padding(4.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(containerColor)
            .clickable(onClick = onClick)
            .minimumInteractiveComponentSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = contentColor
        )
    }
}

// 1. Basic Calculator View
@Composable
fun BasicCalculatorView(viewModel: CalculatorViewModel, themeMode: String) {
    val expr by viewModel.basicExpression
    val res by viewModel.basicResultResult

    Column(modifier = Modifier.fillMaxSize()) {
        // Output Panel
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.4f)
                .background(
                    if (themeMode == "amoled") Color(0xFF050505) else if (themeMode == "glass") Color(0x0AFFFFFF) else MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(18.dp)
                )
                .border(
                    1.dp,
                    if (themeMode == "amoled") Color.White.copy(alpha = 0.08f) else Color.Transparent,
                    shape = RoundedCornerShape(18.dp)
                )
                .padding(16.dp),
            contentAlignment = Alignment.BottomEnd
        ) {
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = expr.ifEmpty { "0" },
                    fontSize = 32.sp,
                    color = if (themeMode == "amoled" || themeMode == "glass") Color.White else MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    textAlign = TextAlign.End,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = res.ifEmpty { "" },
                    fontSize = 24.sp,
                    color = CosmicCyan,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.End
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Grid Button Row Configs
        val rows = listOf(
            listOf("C", "(", ")", "÷"),
            listOf("7", "8", "9", "×"),
            listOf("4", "5", "6", "-"),
            listOf("1", "2", "3", "+"),
            listOf("0", ".", "⌫", "=")
        )

        Column(modifier = Modifier.weight(0.6f)) {
            rows.forEach { row ->
                Row(modifier = Modifier.fillMaxWidth()) {
                    row.forEach { btn ->
                        val isOp = btn in listOf("÷", "×", "-", "+", "=")
                        val isClear = btn in listOf("C", "⌫")
                        val bg = when {
                            btn == "=" -> CosmicCyan
                            isOp -> NeonPurple.copy(alpha = 0.2f)
                            isClear -> HotPink.copy(alpha = 0.15f)
                            else -> if (themeMode == "amoled") Color(0xFF141414) else if (themeMode == "glass") Color(0x1FFFFFFF) else Color(0x0D000000)
                        }
                        val fg = when {
                            btn == "=" -> Color.Black
                            isOp -> NeonPurple
                            isClear -> HotPink
                            else -> if (themeMode == "amoled" || themeMode == "glass") Color.White else Color.Black
                        }
                        KeypadButton(
                            text = btn,
                            onClick = { viewModel.appendToBasic(btn) },
                            modifier = Modifier.weight(1f),
                            containerColor = bg,
                            contentColor = fg
                        )
                    }
                }
            }
        }
    }
}

// 2. Scientific Calculator View
@Composable
fun ScientificCalculatorView(viewModel: CalculatorViewModel, themeMode: String) {
    val expr by viewModel.sciExpression
    val res by viewModel.sciResultResult

    Column(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.35f)
                .background(
                    if (themeMode == "amoled") Color(0xFF050505) else if (themeMode == "glass") Color(0x0AFFFFFF) else MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(18.dp)
                )
                .padding(16.dp),
            contentAlignment = Alignment.BottomEnd
        ) {
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = expr.ifEmpty { "0" },
                    fontSize = 26.sp,
                    color = if (themeMode == "amoled" || themeMode == "glass") Color.White else MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    textAlign = TextAlign.End
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = res.ifEmpty { "" },
                    fontSize = 20.sp,
                    color = NeonPurple,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.End
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Grid Button Row Configs including scientific tools
        val rows = listOf(
            listOf("sin", "cos", "tan", "sqrt"),
            listOf("log", "ln", "^", "π"),
            listOf("C", "(", ")", "÷"),
            listOf("7", "8", "9", "×"),
            listOf("4", "5", "6", "-"),
            listOf("1", "2", "3", "+"),
            listOf("0", ".", "⌫", "=")
        )

        Column(modifier = Modifier.weight(0.65f)) {
            rows.forEach { row ->
                Row(modifier = Modifier.fillMaxWidth()) {
                    row.forEach { btn ->
                        val isSci = btn in listOf("sin", "cos", "tan", "sqrt", "log", "ln", "^", "π")
                        val isOp = btn in listOf("÷", "×", "-", "+", "=")
                        val isClear = btn in listOf("C", "⌫")
                        val bg = when {
                            btn == "=" -> NeonPurple
                            isSci -> CosmicCyan.copy(alpha = 0.15f)
                            isOp -> NeonPurple.copy(alpha = 0.2f)
                            isClear -> HotPink.copy(alpha = 0.15f)
                            else -> if (themeMode == "amoled") Color(0xFF141414) else if (themeMode == "glass") Color(0x1FFFFFFF) else Color(0x0D000000)
                        }
                        val fg = when {
                            btn == "=" -> Color.Black
                            isSci -> CosmicCyan
                            isOp -> NeonPurple
                            isClear -> HotPink
                            else -> if (themeMode == "amoled" || themeMode == "glass") Color.White else Color.Black
                        }
                        KeypadButton(
                            text = btn,
                            onClick = { viewModel.appendToSci(btn) },
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1.3f), // Make button denser
                            containerColor = bg,
                            contentColor = fg
                        )
                    }
                }
            }
        }
    }
}

// 3. Base Converter View (Engineering)
@Composable
fun BaseConverterView(viewModel: CalculatorViewModel, themeMode: String) {
    val input by viewModel.engInput
    val fromBase by viewModel.engFromBase
    val res by viewModel.engResult

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item {
            CalcContainerCard(themeMode) {
                Text("Select Source Radix", fontWeight = FontWeight.Bold, color = CosmicCyan, fontSize = 12.sp)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    val bases = listOf(2, 8, 10, 16)
                    val baseNames = listOf("BIN", "OCT", "DEC", "HEX")
                    bases.forEachIndexed { idx, b ->
                        val isSel = fromBase == b
                        Surface(
                            onClick = { viewModel.engFromBase.value = b; viewModel.calculateEngineering() },
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSel) CosmicCyan else Color.White.copy(alpha = 0.05f),
                            border = BorderStroke(1.dp, if (isSel) CosmicCyan else Color.Transparent)
                        ) {
                            Text(
                                text = baseNames[idx],
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                color = if (isSel) Color.Black else if (themeMode == "amoled" || themeMode == "glass") Color.White else Color.Black,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = input,
                    onValueChange = { viewModel.engInput.value = it; viewModel.calculateEngineering() },
                    placeholder = { Text("Enter digital bits...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = if (themeMode == "amoled" || themeMode == "glass") Color.White else Color.Black,
                        unfocusedTextColor = if (themeMode == "amoled" || themeMode == "glass") Color.White else Color.Black,
                        focusedBorderColor = CosmicCyan
                    )
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(12.dp))
            CalcContainerCard(themeMode) {
                Text("CONVERTED BASES OUTPUT", fontWeight = FontWeight.ExtraBold, color = NeonPurple, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(12.dp))

                val resultsRow = listOf(
                    Pair("DEC (Decimal)", res.decimal),
                    Pair("HEX (Hexadecimal)", res.hex),
                    Pair("BIN (Binary)", res.binary),
                    Pair("OCT (Octal)", res.octal)
                )

                resultsRow.forEach { p ->
                    Column(modifier = Modifier.padding(vertical = 6.dp)) {
                        Text(p.first, fontSize = 11.sp, color = if (themeMode == "amoled" || themeMode == "glass") Color.White.copy(alpha = 0.6f) else Color.Gray)
                        Text(
                            text = p.second.ifBlank { "-" },
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (p.second == "Error") HotPink else if (themeMode == "amoled" || themeMode == "glass") Color.White else Color.Black
                        )
                    }
                }
            }
        }
    }
}

// 4. GPA Calculator View
@Composable
fun GpaCalculatorView(viewModel: CalculatorViewModel, themeMode: String) {
    val courses = viewModel.gpaCourses
    val currentGrade by viewModel.tempGpaGrade
    val currentCredits by viewModel.tempGpaCredits
    val gpaResult by viewModel.gpaResult

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item {
            CalcContainerCard(themeMode) {
                Text("ADD NEW COURSE GRADE", fontWeight = FontWeight.Bold, color = CosmicCyan, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Grade selector dropdown simple
                    OutlinedTextField(
                        value = currentGrade,
                        onValueChange = { viewModel.tempGpaGrade.value = it },
                        label = { Text("Grade (e.g. A, B, C+, F)") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = CosmicCyan)
                    )

                    OutlinedTextField(
                        value = currentCredits,
                        onValueChange = { viewModel.tempGpaCredits.value = it },
                        label = { Text("Credits") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = CosmicCyan)
                    )

                    Button(
                        onClick = { viewModel.addGpaCourse() },
                        colors = ButtonDefaults.buttonColors(containerColor = CosmicCyan, contentColor = Color.Black),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Add", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(12.dp))
            if (courses.isNotEmpty()) {
                CalcContainerCard(themeMode) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("ADDED COURSES", fontWeight = FontWeight.Bold, color = NeonPurple, fontSize = 12.sp)
                        Button(
                            onClick = { viewModel.saveGpaToHistory() },
                            colors = ButtonDefaults.buttonColors(containerColor = NeonPurple, contentColor = Color.White),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Save Hist", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    courses.forEachIndexed { index, item ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Grade: ${item.grade} (${item.credits} Credits)", color = if (themeMode == "amoled" || themeMode == "glass") Color.White else Color.Black)
                            IconButton(onClick = { viewModel.removeGpaCourse(index) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = HotPink)
                            }
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
            // Result metric box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.horizontalGradient(listOf(CosmicCyan, NeonPurple)),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(20.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("SEM GRADE POINT AVERAGE", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    Text(
                        text = "%.2f".format(gpaResult),
                        fontSize = 38.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.Black
                    )
                }
            }
        }
    }
}

// 5. CGPA Calculator View
@Composable
fun CgpaCalculatorView(viewModel: CalculatorViewModel, themeMode: String) {
    val semesters = viewModel.cgpaSemesters
    val currentGpa by viewModel.tempCgpaGpa
    val currentCredits by viewModel.tempCgpaCredits
    val cgpaResult by viewModel.cgpaResult

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item {
            CalcContainerCard(themeMode) {
                Text("ADD SEMESTER PARAMETERS", fontWeight = FontWeight.Bold, color = NeonPurple, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = currentGpa,
                        onValueChange = { viewModel.tempCgpaGpa.value = it },
                        label = { Text("GPA") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = NeonPurple)
                    )

                    OutlinedTextField(
                        value = currentCredits,
                        onValueChange = { viewModel.tempCgpaCredits.value = it },
                        label = { Text("Credits") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = NeonPurple)
                    )

                    Button(
                        onClick = { viewModel.addCgpaSemester() },
                        colors = ButtonDefaults.buttonColors(containerColor = NeonPurple, contentColor = Color.White),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Add", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(12.dp))
            if (semesters.isNotEmpty()) {
                CalcContainerCard(themeMode) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("SEMESTERS LOGIFIED", fontWeight = FontWeight.Bold, color = CosmicCyan, fontSize = 12.sp)
                        Button(
                            onClick = { viewModel.saveCgpaToHistory() },
                            colors = ButtonDefaults.buttonColors(containerColor = CosmicCyan, contentColor = Color.Black),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Save Hist", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    semesters.forEachIndexed { index, item ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Semester GPA: ${item.gpa} (${item.credits} Credits)", color = if (themeMode == "amoled" || themeMode == "glass") Color.White else Color.Black)
                            IconButton(onClick = { viewModel.removeCgpaSemester(index) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = HotPink)
                            }
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.horizontalGradient(listOf(NeonPurple, CosmicCyan)),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(20.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("CUMULATIVE GRADE POINT AVERAGE", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    Text(
                        text = "%.2f".format(cgpaResult),
                        fontSize = 38.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.Black
                    )
                }
            }
        }
    }
}

// 6. Percentage Calculator View
@Composable
fun PercentageCalculatorView(viewModel: CalculatorViewModel, themeMode: String) {
    val pctValP by viewModel.pctValP
    val pctValTotal by viewModel.pctValTotal
    val pctValResult by viewModel.pctValResult

    val pctOfPortion by viewModel.pctOfPortion
    val pctOfTotal by viewModel.pctOfTotal
    val pctOfResult by viewModel.pctOfResult

    val pctChgOld by viewModel.pctChgOld
    val pctChgNew by viewModel.pctChgNew
    val pctChgResult by viewModel.pctChgResult

    LazyColumn(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        item {
            Text("PERCENTAGE VALUES", fontWeight = FontWeight.Bold, color = CosmicCyan, fontSize = 12.sp)
            CalcContainerCard(themeMode) {
                Text("What is X% of Y?", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = pctValP,
                        onValueChange = { viewModel.pctValP.value = it; viewModel.calculatePct1() },
                        placeholder = { Text("X %") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    Text("of")
                    OutlinedTextField(
                        value = pctValTotal,
                        onValueChange = { viewModel.pctValTotal.value = it; viewModel.calculatePct1() },
                        placeholder = { Text("Y") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text("Result: $pctValResult", fontWeight = FontWeight.ExtraBold, color = CosmicCyan, fontSize = 16.sp)
            }
        }

        item {
            Text("PROPORTION FRACTIONS", fontWeight = FontWeight.Bold, color = NeonPurple, fontSize = 12.sp)
            CalcContainerCard(themeMode) {
                Text("X is what percentage of Y?", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = pctOfPortion,
                        onValueChange = { viewModel.pctOfPortion.value = it; viewModel.calculatePct2() },
                        placeholder = { Text("X") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    Text("is what % of")
                    OutlinedTextField(
                        value = pctOfTotal,
                        onValueChange = { viewModel.pctOfTotal.value = it; viewModel.calculatePct2() },
                        placeholder = { Text("Y") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text("Result: $pctOfResult", fontWeight = FontWeight.ExtraBold, color = NeonPurple, fontSize = 16.sp)
            }
        }

        item {
            Text("PERCENTAGE DISSIMILARITY CHANGE", fontWeight = FontWeight.Bold, color = GoldYellow, fontSize = 12.sp)
            CalcContainerCard(themeMode) {
                Text("From X to Y percentage change?", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = pctChgOld,
                        onValueChange = { viewModel.pctChgOld.value = it; viewModel.calculatePct3() },
                        placeholder = { Text("X (Old)") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    Text("to")
                    OutlinedTextField(
                        value = pctChgNew,
                        onValueChange = { viewModel.pctChgNew.value = it; viewModel.calculatePct3() },
                        placeholder = { Text("Y (New)") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text("Result: $pctChgResult", fontWeight = FontWeight.ExtraBold, color = GoldYellow, fontSize = 16.sp)
            }
        }
    }
}

// 7. Age Calculator View
@Composable
fun AgeCalculatorView(viewModel: CalculatorViewModel, themeMode: String) {
    val dob by viewModel.ageDobDate
    val target by viewModel.ageTargetDate
    val res by viewModel.ageResult

    // Local pick state text helpers
    var dobInputStr by remember { mutableStateOf("2000-01-01") }
    var targetInputStr by remember { mutableStateOf(LocalDate.now().toString()) }

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item {
            CalcContainerCard(themeMode) {
                Text("CHOOSE DATE SEGMENTS (Format: YYYY-MM-DD)", fontWeight = FontWeight.Bold, color = CosmicCyan, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = dobInputStr,
                    onValueChange = {
                        dobInputStr = it
                        try {
                            viewModel.ageDobDate.value = LocalDate.parse(it)
                            viewModel.calculateAge()
                        } catch (e: DateTimeParseException) {}
                    },
                    label = { Text("Date of Birth") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = CosmicCyan)
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = targetInputStr,
                    onValueChange = {
                        targetInputStr = it
                        try {
                            viewModel.ageTargetDate.value = LocalDate.parse(it)
                            viewModel.calculateAge()
                        } catch (e: DateTimeParseException) {}
                    },
                    label = { Text("Calculate Age At") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = CosmicCyan)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = { viewModel.calculateAge() },
                    colors = ButtonDefaults.buttonColors(containerColor = CosmicCyan, contentColor = Color.Black),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("CALCULATE AGE NOW", fontWeight = FontWeight.Bold)
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(12.dp))
            res?.let { item ->
                CalcContainerCard(themeMode) {
                    Text("AGE MEASUREMENT RESULTS", fontWeight = FontWeight.Bold, color = NeonPurple, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Years", fontSize = 12.sp)
                            Text("${item.years}", fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = CosmicCyan)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Months", fontSize = 12.sp)
                            Text("${item.months}", fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = NeonPurple)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Days", fontSize = 12.sp)
                            Text("${item.days}", fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = GoldYellow)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Days left until next birthday party: ${item.nextBirthdayDaysLeft} days!",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

// 8. Date Difference Calculator View
@Composable
fun DateDiffView(viewModel: CalculatorViewModel, themeMode: String) {
    var startStr by remember { mutableStateOf(LocalDate.now().toString()) }
    var endStr by remember { mutableStateOf(LocalDate.now().plusMonths(1).toString()) }
    val res by viewModel.dateDiffResult

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item {
            CalcContainerCard(themeMode) {
                Text("DATE DIFFERENCE METRIC (Format: YYYY-MM-DD)", fontWeight = FontWeight.Bold, color = NeonPurple, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = startStr,
                    onValueChange = {
                        startStr = it
                        try {
                            viewModel.dateDiffStart.value = LocalDate.parse(it)
                            viewModel.calculateDateDiff()
                        } catch (e: Exception) {}
                    },
                    label = { Text("Start Date") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = NeonPurple)
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = endStr,
                    onValueChange = {
                        endStr = it
                        try {
                            viewModel.dateDiffEnd.value = LocalDate.parse(it)
                            viewModel.calculateDateDiff()
                        } catch (e: Exception) {}
                    },
                    label = { Text("End Date") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = NeonPurple)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = { viewModel.calculateDateDiff() },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonPurple, contentColor = Color.White),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("CALCULATE SPAN DISTANCE", fontWeight = FontWeight.Bold)
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(12.dp))
            res?.let { item ->
                CalcContainerCard(themeMode) {
                    Text("DATE DIFFERENCE SPAN TIME", fontWeight = FontWeight.Bold, color = CosmicCyan, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(12.dp))

                    Text("Total Days Boundary: ${item.totalDays} Days", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = CosmicCyan)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Equivalent Span breakdown: ${item.years} Years, ${item.months} Months, and ${item.days} Days", fontSize = 13.sp)
                }
            }
        }
    }
}

// 9. BMI Calculator View
@Composable
fun BmiCalculatorView(viewModel: CalculatorViewModel, themeMode: String) {
    val h by viewModel.bmiHeight
    val w by viewModel.bmiWeight
    val res by viewModel.bmiResult

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item {
            CalcContainerCard(themeMode) {
                Text("BODY METRIC INDICES", fontWeight = FontWeight.Bold, color = CosmicCyan, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = h,
                    onValueChange = { viewModel.bmiHeight.value = it; viewModel.calculateBmi() },
                    label = { Text("Height in centimeters (cm)") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = CosmicCyan)
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = w,
                    onValueChange = { viewModel.bmiWeight.value = it; viewModel.calculateBmi() },
                    label = { Text("Weight in kilograms (kg)") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = CosmicCyan)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = { viewModel.calculateBmi() },
                    colors = ButtonDefaults.buttonColors(containerColor = CosmicCyan, contentColor = Color.Black),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("EXECUTE BMI METRIC", fontWeight = FontWeight.Bold)
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(12.dp))
            res?.let { item ->
                // Visual color based on category
                val col = when (item.category) {
                    "Normal" -> CosmicCyan
                    "Underweight" -> GoldYellow
                    "Overweight" -> NeonPurple
                    else -> HotPink
                }

                CalcContainerCard(themeMode) {
                    Text("BMI CLASSIFICATION EVAL", fontWeight = FontWeight.Bold, color = NeonPurple, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(10.dp))

                    Text("BMI Index Score: ${"%.1f".format(item.bmi)}", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = col)
                    Text("Category: ${item.category}", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = col)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(item.healthTip, fontSize = 13.sp, color = if (themeMode == "amoled" || themeMode == "glass") Color.White.copy(alpha = 0.8f) else Color.DarkGray)
                }
            }
        }
    }
}

// 10. Calorie Calculator View
@Composable
fun CalorieCalculatorView(viewModel: CalculatorViewModel, themeMode: String) {
    val h by viewModel.calHeight
    val w by viewModel.calWeight
    val age by viewModel.calAge
    val isMale by viewModel.calIsMale
    val calorieDemand by viewModel.calResult

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item {
            CalcContainerCard(themeMode) {
                Text("CALORIC RATE METRICS", fontWeight = FontWeight.Bold, color = CosmicCyan, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(10.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = w,
                        onValueChange = { viewModel.calWeight.value = it; viewModel.calculateCalories() },
                        label = { Text("Weight (kg)") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    OutlinedTextField(
                        value = h,
                        onValueChange = { viewModel.calHeight.value = it; viewModel.calculateCalories() },
                        label = { Text("Height (cm)") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    value = age,
                    onValueChange = { viewModel.calAge.value = it; viewModel.calculateCalories() },
                    label = { Text("Age (Years)") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text("Gender Profile", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(selected = isMale, onClick = { viewModel.calIsMale.value = true; viewModel.calculateCalories() })
                        Text("Male profile")
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(selected = !isMale, onClick = { viewModel.calIsMale.value = false; viewModel.calculateCalories() })
                        Text("Female profile")
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = { viewModel.calculateCalories() },
                    colors = ButtonDefaults.buttonColors(containerColor = CosmicCyan, contentColor = Color.Black),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("RECALCULATE ENERGY METRICS", fontWeight = FontWeight.Bold)
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.horizontalGradient(listOf(CosmicCyan, NeonPurple)),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(20.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("DAILY MAINTENANCE CALORIES EST", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    Text(
                        text = "${calorieDemand.toInt()} kcal / day",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.Black
                    )
                }
            }
        }
    }
}

// 11. EMI Calculator View
@Composable
fun EmiCalculatorView(viewModel: CalculatorViewModel, themeMode: String) {
    val p by viewModel.emiPrincipal
    val r by viewModel.emiRate
    val t by viewModel.emiTenure
    val res by viewModel.emiResult

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item {
            CalcContainerCard(themeMode) {
                Text("EMI LOAN INPUTS", fontWeight = FontWeight.Bold, color = CosmicCyan, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = p,
                    onValueChange = { viewModel.emiPrincipal.value = it; viewModel.calculateEmi() },
                    label = { Text("Principal Amount ($)") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    value = r,
                    onValueChange = { viewModel.emiRate.value = it; viewModel.calculateEmi() },
                    label = { Text("Interest Rate per Annum (%)") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    value = t,
                    onValueChange = { viewModel.emiTenure.value = it; viewModel.calculateEmi() },
                    label = { Text("Tenure Duration (Months)") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = { viewModel.calculateEmi() },
                    colors = ButtonDefaults.buttonColors(containerColor = CosmicCyan, contentColor = Color.Black),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("RECALCULATE EMI", fontWeight = FontWeight.Bold)
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(12.dp))
            res?.let { item ->
                CalcContainerCard(themeMode) {
                    Text("EMI AMORTIZATION OUTPUT", fontWeight = FontWeight.Bold, color = NeonPurple, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(10.dp))

                    Text("Monthly EMI Payment: $${"%.2f".format(item.monthlyEmi)}", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = CosmicCyan)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Total Cumulative Interest: $${"%.2f".format(item.totalInterest)}", fontSize = 13.sp)
                    Text("Total Cumulative Payment: $${"%.2f".format(item.totalPayment)}", fontSize = 13.sp)
                }
            }
        }
    }
}

// 12. Loan Calculator View
@Composable
fun LoanCalculatorView(viewModel: CalculatorViewModel, themeMode: String) {
    val p by viewModel.loanPrincipal
    val r by viewModel.loanRate
    val t by viewModel.loanYears
    val res by viewModel.loanResult

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item {
            CalcContainerCard(themeMode) {
                Text("LOAN METRIC SPEC", fontWeight = FontWeight.Bold, color = NeonPurple, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = p,
                    onValueChange = { viewModel.loanPrincipal.value = it; viewModel.calculateLoan() },
                    label = { Text("Loan principal amount") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    value = r,
                    onValueChange = { viewModel.loanRate.value = it; viewModel.calculateLoan() },
                    label = { Text("Annual Margin Rate %") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    value = t,
                    onValueChange = { viewModel.loanYears.value = it; viewModel.calculateLoan() },
                    label = { Text("Tenure scale (Years)") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = { viewModel.calculateLoan() },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonPurple, contentColor = Color.White),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("RECOMPUTE LOAN", fontWeight = FontWeight.Bold)
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(12.dp))
            res?.let { item ->
                CalcContainerCard(themeMode) {
                    Text("REPAYMENT SUMMARY SCHEDULE", fontWeight = FontWeight.Bold, color = CosmicCyan, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(10.dp))

                    Text("Equivalent Monthly Emis: $${"%.2f".format(item.monthlyEmi)}", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = NeonPurple)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Total Calculated Interest: $${"%.2f".format(item.totalInterest)}", fontSize = 13.sp)
                    Text("Overall Ultimate Payment: $${"%.2f".format(item.totalPayment)}", fontSize = 13.sp)
                }
            }
        }
    }
}

// 13. Compound Interest View
@Composable
fun CompoundInterestView(viewModel: CalculatorViewModel, themeMode: String) {
    val p by viewModel.ciPrincipal
    val r by viewModel.ciRate
    val y by viewModel.ciYears
    val compounding by viewModel.ciCompounding
    val res by viewModel.ciResult

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item {
            CalcContainerCard(themeMode) {
                Text("COMPOUND INTEREST METRIC PARAMS", fontWeight = FontWeight.Bold, color = CosmicCyan, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = p,
                    onValueChange = { viewModel.ciPrincipal.value = it; viewModel.calculateCi() },
                    label = { Text("Principal Capital ($)") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    value = r,
                    onValueChange = { viewModel.ciRate.value = it; viewModel.calculateCi() },
                    label = { Text("Annual Rate %") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    value = y,
                    onValueChange = { viewModel.ciYears.value = it; viewModel.calculateCi() },
                    label = { Text("Duration scale (Years)") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text("Compounding Frequencies", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val compoundingFreqs = listOf(1, 4, 12, 365)
                    val compoundingNames = listOf("Annually", "Quarterly", "Monthly", "Daily")
                    compoundingFreqs.forEachIndexed { idx, freq ->
                        val isSel = compounding == freq
                        Surface(
                            onClick = { viewModel.ciCompounding.value = freq; viewModel.calculateCi() },
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSel) CosmicCyan else Color.White.copy(alpha = 0.05f)
                        ) {
                            Text(
                                compoundingNames[idx],
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                fontSize = 11.sp,
                                color = if (isSel) Color.Black else if (themeMode == "amoled" || themeMode == "glass") Color.White else Color.Black,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = { viewModel.calculateCi() },
                    colors = ButtonDefaults.buttonColors(containerColor = CosmicCyan, contentColor = Color.Black),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("RECOMPUTE INTEREST METRIC", fontWeight = FontWeight.Bold)
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(12.dp))
            res?.let { item ->
                CalcContainerCard(themeMode) {
                    Text("FUTURE GAIN SCHEDULE", fontWeight = FontWeight.Bold, color = NeonPurple, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(10.dp))

                    Text("Future Compounded Value: $${"%.2f".format(item.futureValue)}", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = CosmicCyan)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Compounded Interest Earned: $${"%.2f".format(item.interestEarned)}", fontSize = 13.sp)
                }
            }
        }
    }
}

// 14. Currency Converter View
@Composable
fun CurrencyConverterView(viewModel: CalculatorViewModel, themeMode: String) {
    val amount by viewModel.curAmount
    val fromCur by viewModel.curFrom
    val toCur by viewModel.curTo
    val result by viewModel.curResult

    val currencies = viewModel.getCurrencies()

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item {
            CalcContainerCard(themeMode) {
                Text("EXCHANGE AMOUNT", fontWeight = FontWeight.Bold, color = CosmicCyan, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = amount,
                    onValueChange = { viewModel.curAmount.value = it; viewModel.calculateCurrency() },
                    label = { Text("Amount value") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Quick selector simplifications for source currencies
                    Column(modifier = Modifier.weight(1f)) {
                        Text("From Currency", fontSize = 11.sp, color = CosmicCyan)
                        Row(modifier = Modifier.horizontalScroll(rememberScrollState())) {
                            currencies.forEach { c ->
                                Surface(
                                    onClick = { viewModel.curFrom.value = c; viewModel.calculateCurrency() },
                                    shape = RoundedCornerShape(6.dp),
                                    color = if (fromCur == c) CosmicCyan else Color.White.copy(alpha = 0.05f),
                                    modifier = Modifier.padding(2.dp)
                                ) {
                                    Text(
                                        c,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp),
                                        fontSize = 11.sp,
                                        color = if (fromCur == c) Color.Black else if (themeMode == "amoled" || themeMode == "glass") Color.White else Color.Black,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }

                    IconButton(onClick = { viewModel.swapCurrencies() }) {
                        Icon(Icons.Default.SwapHoriz, contentDescription = "Swap currencies", tint = NeonPurple)
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text("To Currency", fontSize = 11.sp, color = NeonPurple)
                        Row(modifier = Modifier.horizontalScroll(rememberScrollState())) {
                            currencies.forEach { c ->
                                Surface(
                                    onClick = { viewModel.curTo.value = c; viewModel.calculateCurrency() },
                                    shape = RoundedCornerShape(6.dp),
                                    color = if (toCur == c) NeonPurple else Color.White.copy(alpha = 0.05f),
                                    modifier = Modifier.padding(2.dp)
                                ) {
                                    Text(
                                        c,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp),
                                        fontSize = 11.sp,
                                        color = if (toCur == c) Color.White else if (themeMode == "amoled" || themeMode == "glass") Color.White else Color.Black,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.horizontalGradient(listOf(CosmicCyan, NeonPurple)),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(20.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("$fromCur TO $toCur OFFLINE CONVERSION", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    Text(
                        text = "$result $toCur",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.Black
                    )
                }
            }
        }
    }
}

// 15. Unit Converter View
@Composable
fun UnitConverterView(viewModel: CalculatorViewModel, themeMode: String) {
    val category by viewModel.unitCategory
    val inputVal by viewModel.unitInput
    val fromUnit by viewModel.unitFrom
    val toUnit by viewModel.unitTo
    val resultVal by viewModel.unitResult

    val categories = listOf("Length", "Weight", "Temperature", "Area")

    val unitsForCategory = when (category) {
        "Length" -> listOf("m", "km", "cm", "mm", "mile", "yard", "ft", "inch")
        "Weight" -> listOf("kg", "g", "lb", "g_oz")
        "Temperature" -> listOf("C", "F", "K")
        "Area" -> listOf("sq_m", "sq_km", "sq_mile", "acre", "hectare", "sq_ft")
        else -> emptyList()
    }

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item {
            CalcContainerCard(themeMode) {
                Text("SELECT PHYSICAL CATEGORY", fontWeight = FontWeight.Bold, color = CosmicCyan, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    categories.forEach { cat ->
                        val isSelected = category == cat
                        Surface(
                            onClick = { viewModel.updateUnitCategory(cat) },
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) CosmicCyan else Color.White.copy(alpha = 0.05f),
                            border = BorderStroke(1.dp, if (isSelected) CosmicCyan else Color.Transparent)
                        ) {
                            Text(
                                cat,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                                color = if (isSelected) Color.Black else if (themeMode == "amoled" || themeMode == "glass") Color.White else Color.Black,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(10.dp))
            CalcContainerCard(themeMode) {
                Text("UNIT CONVERSIONS VALUES", fontWeight = FontWeight.Bold, color = NeonPurple, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = inputVal,
                    onValueChange = { viewModel.unitInput.value = it; viewModel.calculateUnit() },
                    placeholder = { Text("Enter magnitude value") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("From Unit", fontSize = 11.sp, color = CosmicCyan)
                        Row(modifier = Modifier.horizontalScroll(rememberScrollState())) {
                            unitsForCategory.forEach { u ->
                                Surface(
                                    onClick = { viewModel.unitFrom.value = u; viewModel.calculateUnit() },
                                    shape = RoundedCornerShape(6.dp),
                                    color = if (fromUnit == u) CosmicCyan else Color.White.copy(alpha = 0.05f),
                                    modifier = Modifier.padding(2.dp)
                                ) {
                                    Text(
                                        u,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp),
                                        fontSize = 11.sp,
                                        color = if (fromUnit == u) Color.Black else if (themeMode == "amoled" || themeMode == "glass") Color.White else Color.Black,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }

                    Column(modifier = Modifier.weight(1f).padding(start = 8.dp)) {
                        Text("To Unit", fontSize = 11.sp, color = NeonPurple)
                        Row(modifier = Modifier.horizontalScroll(rememberScrollState())) {
                            unitsForCategory.forEach { u ->
                                Surface(
                                    onClick = { viewModel.unitTo.value = u; viewModel.calculateUnit() },
                                    shape = RoundedCornerShape(6.dp),
                                    color = if (toUnit == u) NeonPurple else Color.White.copy(alpha = 0.05f),
                                    modifier = Modifier.padding(2.dp)
                                ) {
                                    Text(
                                        u,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp),
                                        fontSize = 11.sp,
                                        color = if (toUnit == u) Color.White else if (themeMode == "amoled" || themeMode == "glass") Color.White else Color.Black,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.horizontalGradient(listOf(NeonPurple, CosmicCyan)),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(20.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("$fromUnit TO $toUnit OFFLINE MASS CONVERSION", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    Text(
                        text = "$resultVal $toUnit",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.Black
                    )
                }
            }
        }
    }
}

// 16. Tax Calculator View
@Composable
fun TaxCalculatorView(viewModel: CalculatorViewModel, themeMode: String) {
    val amount by viewModel.taxAmount
    val rate by viewModel.taxRate
    val isInclusive by viewModel.taxInclusive
    val res by viewModel.taxResult

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item {
            CalcContainerCard(themeMode) {
                Text("NET AMOUNT & TAX PERCENT", fontWeight = FontWeight.Bold, color = CosmicCyan, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = amount,
                    onValueChange = { viewModel.taxAmount.value = it; viewModel.calculateTax() },
                    label = { Text("Amount Price ($)") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    value = rate,
                    onValueChange = { viewModel.taxRate.value = it; viewModel.calculateTax() },
                    label = { Text("Tax GST / VAT Rate (%)") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text("Tax Option Mode", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(selected = !isInclusive, onClick = { viewModel.taxInclusive.value = false; viewModel.calculateTax() })
                        Text("Add Tax (Exclusive)")
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(selected = isInclusive, onClick = { viewModel.taxInclusive.value = true; viewModel.calculateTax() })
                        Text("Tax Included (Inclusive)")
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = { viewModel.calculateTax() },
                    colors = ButtonDefaults.buttonColors(containerColor = CosmicCyan, contentColor = Color.Black),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("RECOMPUTE TAX VALUES", fontWeight = FontWeight.Bold)
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(12.dp))
            res?.let { item ->
                CalcContainerCard(themeMode) {
                    Text("TAX AMORTIZATION RESULTS", fontWeight = FontWeight.Bold, color = NeonPurple, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(10.dp))

                    Text("Total Price: $${"%.2f".format(item.total)}", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = CosmicCyan)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Tax Applied Amount: $${"%.2f".format(item.taxAmount)}", fontSize = 13.sp)
                    Text("Pre-tax Net Price: $${"%.2f".format(item.netPrice)}", fontSize = 13.sp)
                }
            }
        }
    }
}

// 17. Discount Calculator View
@Composable
fun DiscountCalculatorView(viewModel: CalculatorViewModel, themeMode: String) {
    val price by viewModel.discPrice
    val discTypePercent by viewModel.discPercent
    val taxRatePercent by viewModel.discTaxPercent
    val res by viewModel.discResult

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item {
            CalcContainerCard(themeMode) {
                Text("DISCOUNT SPEC & OPTIONAL TAX", fontWeight = FontWeight.Bold, color = CosmicCyan, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = price,
                    onValueChange = { viewModel.discPrice.value = it; viewModel.calculateDiscount() },
                    label = { Text("Original Commodity Price ($)") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    value = discTypePercent,
                    onValueChange = { viewModel.discPercent.value = it; viewModel.calculateDiscount() },
                    label = { Text("Commodity Discount Percentage (%)") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    value = taxRatePercent,
                    onValueChange = { viewModel.discTaxPercent.value = it; viewModel.calculateDiscount() },
                    label = { Text("Tax GST Bounds (%)") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = { viewModel.calculateDiscount() },
                    colors = ButtonDefaults.buttonColors(containerColor = CosmicCyan, contentColor = Color.Black),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("EXECUTE REBATES METRIC", fontWeight = FontWeight.Bold)
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(12.dp))
            res?.let { item ->
                CalcContainerCard(themeMode) {
                    Text("SAVINGS AMORTIZATION RESULTS", fontWeight = FontWeight.Bold, color = NeonPurple, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(10.dp))

                    Text("Final Discount Price: $${"%.2f".format(item.finalPrice)}", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = CosmicCyan)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Net rebated savings: $${"%.2f".format(item.savings)}", fontSize = 13.sp)
                    Text("Applicable Tax Paid: $${"%.2f".format(item.taxPaid)}", fontSize = 13.sp)
                }
            }
        }
    }
}

// 18. Time Calculator View
@Composable
fun TimeCalculatorView(viewModel: CalculatorViewModel, themeMode: String) {
    val h1 by viewModel.timeH1
    val m1 by viewModel.timeM1
    val s1 by viewModel.timeS1
    val h2 by viewModel.timeH2
    val m2 by viewModel.timeM2
    val s2 by viewModel.timeS2
    val isAdd by viewModel.timeOperationAdd
    val res by viewModel.timeResult

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item {
            CalcContainerCard(themeMode) {
                Text("FIRST TIME SEGMENT", fontWeight = FontWeight.Bold, color = CosmicCyan, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = h1,
                        onValueChange = { viewModel.timeH1.value = it; viewModel.calculateTime() },
                        label = { Text("Hours") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    OutlinedTextField(
                        value = m1,
                        onValueChange = { viewModel.timeM1.value = it; viewModel.calculateTime() },
                        label = { Text("Mins") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    OutlinedTextField(
                        value = s1,
                        onValueChange = { viewModel.timeS1.value = it; viewModel.calculateTime() },
                        label = { Text("Secs") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text("Arithmetic Operation", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(selected = isAdd, onClick = { viewModel.timeOperationAdd.value = true; viewModel.calculateTime() })
                        Text("Add times (+)")
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(selected = !isAdd, onClick = { viewModel.timeOperationAdd.value = false; viewModel.calculateTime() })
                        Text("Subtract times (-)")
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text("SECOND TIME SEGMENT", fontWeight = FontWeight.Bold, color = NeonPurple, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = h2,
                        onValueChange = { viewModel.timeH2.value = it; viewModel.calculateTime() },
                        label = { Text("Hours") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    OutlinedTextField(
                        value = m2,
                        onValueChange = { viewModel.timeM2.value = it; viewModel.calculateTime() },
                        label = { Text("Mins") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    OutlinedTextField(
                        value = s2,
                        onValueChange = { viewModel.timeS2.value = it; viewModel.calculateTime() },
                        label = { Text("Secs") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = { viewModel.calculateTime() },
                    colors = ButtonDefaults.buttonColors(containerColor = CosmicCyan, contentColor = Color.Black),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("RECOMPUTE DURATION UNITS", fontWeight = FontWeight.Bold)
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(12.dp))
            res?.let { item ->
                CalcContainerCard(themeMode) {
                    Text("TOTAL RESULT SPAN BOUNDS", fontWeight = FontWeight.Bold, color = CosmicCyan, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(10.dp))

                    Text("Calculated Span: ${item.hours} Hours, ${item.minutes} Minutes, ${item.seconds} Seconds", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = CosmicCyan)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Total seconds metric: ${item.totalSeconds} Seconds", fontSize = 13.sp)
                }
            }
        }
    }
}
