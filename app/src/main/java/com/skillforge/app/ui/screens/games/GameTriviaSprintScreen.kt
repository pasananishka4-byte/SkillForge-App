package com.skillforge.app.ui.screens.games

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.skillforge.app.ui.AppStorage
import com.skillforge.app.ui.theme.*
import kotlinx.coroutines.delay
import kotlin.random.Random

private data class TriviaQ(val category: String, val question: String, val options: List<String>, val correctIndex: Int)

private val triviaPool = listOf(
    TriviaQ("Science", "What is the chemical symbol for gold?", listOf("Go", "Gd", "Au", "Ag"), 2),
    TriviaQ("Science", "What planet is known as the Red Planet?", listOf("Venus", "Mars", "Jupiter", "Saturn"), 1),
    TriviaQ("Science", "What is the speed of light in vacuum (km/s)?", listOf("~200,000", "~250,000", "~300,000", "~350,000"), 2),
    TriviaQ("Science", "How many bones are in the adult human body?", listOf("186", "206", "226", "256"), 1),
    TriviaQ("Science", "What element has atomic number 1?", listOf("Helium", "Oxygen", "Hydrogen", "Carbon"), 2),
    TriviaQ("Science", "What force keeps planets orbiting the sun?", listOf("Magnetism", "Gravity", "Centrifugal", "Nuclear"), 1),
    TriviaQ("Science", "What gas do plants absorb from the atmosphere?", listOf("Oxygen", "Nitrogen", "Carbon dioxide", "Argon"), 2),
    TriviaQ("Science", "What is the largest organ in the human body?", listOf("Liver", "Brain", "Skin", "Heart"), 2),
    TriviaQ("Science", "What is the powerhouse of the cell?", listOf("Nucleus", "Ribosome", "Mitochondria", "Golgi"), 2),
    TriviaQ("Science", "What type of rock is formed from cooled magma?", listOf("Sedimentary", "Igneous", "Metamorphic", "Fossil"), 1),
    TriviaQ("History", "In what year did WWII end?", listOf("1943", "1944", "1945", "1946"), 2),
    TriviaQ("History", "Who was the first US President?", listOf("Adams", "Jefferson", "Washington", "Franklin"), 2),
    TriviaQ("History", "What civilization built Machu Picchu?", listOf("Aztec", "Maya", "Inca", "Olmec"), 2),
    TriviaQ("History", "What year was the Berlin Wall built?", listOf("1959", "1961", "1963", "1965"), 1),
    TriviaQ("History", "Who discovered penicillin?", listOf("Pasteur", "Fleming", "Koch", "Salk"), 1),
    TriviaQ("History", "What empire was ruled by Genghis Khan?", listOf("Ottoman", "Roman", "Mongol", "Persian"), 2),
    TriviaQ("History", "What year did humans first land on the moon?", listOf("1967", "1968", "1969", "1970"), 2),
    TriviaQ("History", "What ship sank on its maiden voyage in 1912?", listOf("Lusitania", "Titanic", "Britannic", "Olympic"), 1),
    TriviaQ("History", "What ancient wonder was located in Alexandria?", listOf("Colossus", "Lighthouse", "Temple of Artemis", "Hanging Gardens"), 1),
    TriviaQ("History", "What country gifted the Statue of Liberty to the US?", listOf("Britain", "Spain", "France", "Germany"), 2),
    TriviaQ("Geography", "What is the longest river in the world?", listOf("Amazon", "Nile", "Mississippi", "Yangtze"), 1),
    TriviaQ("Geography", "What is the smallest country in the world?", listOf("Monaco", "Vatican City", "San Marino", "Liechtenstein"), 1),
    TriviaQ("Geography", "What continent has the most countries?", listOf("Asia", "Europe", "Africa", "South America"), 2),
    TriviaQ("Geography", "What mountain is the tallest on Earth (base to peak)?", listOf("Everest", "K2", "Mauna Kea", "Denali"), 2),
    TriviaQ("Geography", "What is the capital of Japan?", listOf("Seoul", "Beijing", "Tokyo", "Osaka"), 2),
    TriviaQ("Geography", "What desert is the largest hot desert?", listOf("Gobi", "Kalahari", "Sahara", "Arabian"), 2),
    TriviaQ("Geography", "What is the deepest ocean trench?", listOf("Tonga", "Mariana", "Java", "Puerto Rico"), 1),
    TriviaQ("Geography", "What country has the most time zones?", listOf("Russia", "USA", "France", "China"), 2),
    TriviaQ("Geography", "What is the largest lake by surface area?", listOf("Superior", "Caspian", "Victoria", "Baikal"), 1),
    TriviaQ("Geography", "What strait separates Asia from North America?", listOf("Gibraltar", "Bosphorus", "Bering", "Malacca"), 2),
    TriviaQ("Mathematics", "What is the square root of 144?", listOf("10", "11", "12", "14"), 2),
    TriviaQ("Mathematics", "What is the next prime number after 7?", listOf("8", "9", "11", "13"), 2),
    TriviaQ("Mathematics", "What is the value of Pi to 2 decimal places?", listOf("3.12", "3.14", "3.16", "3.18"), 1),
    TriviaQ("Mathematics", "What is 15% of 200?", listOf("25", "30", "35", "45"), 1),
    TriviaQ("Mathematics", "How many sides does a dodecagon have?", listOf("10", "11", "12", "14"), 2),
    TriviaQ("Mathematics", "What is 7 factorial (7!)?", listOf("720", "5040", "2520", "1440"), 1),
    TriviaQ("Mathematics", "What number is Roman numeral XLII?", listOf("32", "42", "52", "62"), 1),
    TriviaQ("Mathematics", "What is the only even prime number?", listOf("0", "1", "2", "4"), 2),
    TriviaQ("Technology", "Who co-founded Apple Inc. with Steve Jobs?", listOf("Gates", "Wozniak", "Zuckerberg", "Ellison"), 1),
    TriviaQ("Technology", "What does 'HTTP' stand for?", listOf("Hyper Text Transfer Protocol", "High Transfer Text Protocol", "Hyper Text Transmission Protocol", "High Transmission Text Protocol"), 0),
    TriviaQ("Technology", "What programming language is also a snake?", listOf("Java", "Python", "Cobra", "Viper"), 1),
    TriviaQ("Technology", "What year was the first iPhone released?", listOf("2005", "2006", "2007", "2008"), 2),
    TriviaQ("Technology", "What does 'RAM' stand for?", listOf("Read Access Memory", "Random Access Memory", "Run Access Memory", "Rapid Access Module"), 1),
    TriviaQ("Technology", "What company created the Android OS?", listOf("Apple", "Microsoft", "Google", "Samsung"), 2),
    TriviaQ("Technology", "What does DNS stand for?", listOf("Domain Network System", "Domain Name Service", "Domain Name System", "Digital Network Service"), 2),
    TriviaQ("Technology", "What chip architecture do most smartphones use?", listOf("x86", "ARM", "MIPS", "PowerPC"), 1),
    TriviaQ("Literature", "Who wrote 'Romeo and Juliet'?", listOf("Marlowe", "Shakespeare", "Chaucer", "Dickens"), 1),
    TriviaQ("Literature", "What is the first book of the Bible?", listOf("Exodus", "Psalms", "Genesis", "Leviticus"), 2),
    TriviaQ("Literature", "Who wrote '1984'?", listOf("Huxley", "Orwell", "Bradbury", "Zamyatin"), 1),
    TriviaQ("Literature", "What fictional detective lived at 221B Baker Street?", listOf("Poirot", "Holmes", "Marple", "Dupin"), 1),
    TriviaQ("Literature", "Who wrote 'The Great Gatsby'?", listOf("Hemingway", "Fitzgerald", "Faulkner", "Steinbeck"), 1),
    TriviaQ("Literature", "What is the longest novel ever written?", listOf("War and Peace", "In Search of Lost Time", "Les Miserables", "The Artimaeus Cycle"), 1),
    TriviaQ("General", "What colors are on the French flag?", listOf("Red, White, Blue", "Green, White, Red", "Blue, White, Red", "White, Blue, Red"), 2),
    TriviaQ("General", "How many legs does a spider have?", listOf("6", "8", "10", "12"), 1),
    TriviaQ("General", "What is the hardest natural substance?", listOf("Gold", "Iron", "Diamond", "Quartz"), 2),
    TriviaQ("General", "What instrument has 88 keys?", listOf("Organ", "Piano", "Harpsichord", "Accordion"), 1),
    TriviaQ("General", "What is the most spoken language in the world?", listOf("English", "Mandarin", "Spanish", "Hindi"), 1),
    TriviaQ("General", "What is the boiling point of water in Celsius?", listOf("90", "100", "110", "120"), 1),
    TriviaQ("General", "What animal is the fastest on land?", listOf("Lion", "Cheetah", "Gazelle", "Horse"), 1),
    TriviaQ("General", "What is the currency of Japan?", listOf("Yuan", "Won", "Yen", "Ringgit"), 2)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameTriviaSprintScreen(difficulty: String, navController: NavHostController) {
    val totalQs = when(difficulty) { "hard" -> 20; "medium" -> 15; else -> 10 }
    val perQTime = when(difficulty) { "hard" -> 6; "medium" -> 8; else -> 10 }

    var questions by remember { mutableStateOf<List<TriviaQ>>(emptyList()) }
    var currentIdx by remember { mutableIntStateOf(0) }
    var score by remember { mutableIntStateOf(0) }
    var correctCount by remember { mutableIntStateOf(0) }
    var gameOver by remember { mutableStateOf(false) }
    var answered by remember { mutableStateOf(false) }
    var timeLeft by remember { mutableIntStateOf(perQTime) }

    LaunchedEffect(Unit) {
        val shuffled = triviaPool.shuffled().take(totalQs)
        questions = shuffled
    }

    LaunchedEffect(currentIdx, answered) {
        if (questions.isEmpty() || currentIdx >= totalQs) return@LaunchedEffect
        timeLeft = perQTime
        while (timeLeft > 0 && !answered) {
            delay(1000)
            timeLeft--
        }
        if (!answered) {
            answered = true
            currentIdx++
            if (currentIdx >= totalQs) gameOver = true
        }
    }

    LaunchedEffect(answered) {
        if (answered) {
            delay(800)
            if (currentIdx < totalQs) {
                answered = false
            } else {
                gameOver = true
            }
        }
    }

    if (gameOver) {
        val xp = when(difficulty) { "hard" -> 5; "medium" -> 3; else -> 2 }
        LaunchedEffect(Unit) { AppStorage.storage.saveGameResult("trivia_sprint", score, score * xp) }
        GameOverScreen(
            title = "Trivia Sprint Complete!",
            stats = listOf("Correct: $correctCount/$totalQs", "Accuracy: ${
                if (totalQs > 0) (correctCount * 100 / totalQs) else 0
            }%"),
            score = score,
            xpEarned = score * xp,
            onPlayAgain = { navController.popBackStack(); navController.navigate("game_trivia_sprint?difficulty=$difficulty") },
            onBack = { navController.popBackStack() }
        )
        return
    }

    if (questions.isEmpty()) return

    val q = questions[currentIdx]

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("Trivia Sprint") },
            navigationIcon = { IconButton(onClick = { navController.popBackStack() }) {             Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back") } },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Surface, titleContentColor = OnBackground)
        )
        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("${currentIdx + 1}/$totalQs", color = OnSurfaceVariant)
                Spacer(modifier = Modifier.width(8.dp))
                Text("[$q.category]", color = ProcessingSpeedColor, fontSize = 12.sp)
            }
            Text("Score: $score", color = Primary)
        }
        LinearProgressIndicator(
            progress = (totalQs - currentIdx).toFloat() / totalQs,
            modifier = Modifier.fillMaxWidth().height(4.dp),
            color = ProcessingSpeedColor, trackColor = SurfaceVariant
        )
        if (!answered) {
            Box(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), contentAlignment = Alignment.Center) {
                Text("⏱ ${timeLeft}s", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = if (timeLeft <= 2) ErrorColor else ProcessingSpeedColor)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Card(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            colors = CardDefaults.cardColors(containerColor = Surface),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(q.question, fontSize = 20.sp, fontWeight = FontWeight.SemiBold, color = OnBackground, textAlign = TextAlign.Center)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        q.options.forEachIndexed { idx, opt ->
            Card(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp),
                colors = CardDefaults.cardColors(containerColor = Surface),
                shape = RoundedCornerShape(12.dp)
            ) {
                TextButton(
                    onClick = {
                        if (!answered) {
                            answered = true
                            val bonus = maxOf(1, timeLeft)
                            if (idx == q.correctIndex) {
                                score += 10 + bonus
                                correctCount++
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                ) {
                    Text(opt, color = OnSurface, fontSize = 16.sp, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Start)
                }
            }
        }
    }
}
