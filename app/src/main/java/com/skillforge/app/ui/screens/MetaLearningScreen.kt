package com.skillforge.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
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

private data class MetaTechnique(
    val name: String,
    val icon: String,
    val description: String,
    val procedure: String,
    val effectSize: String,
    val citation: String
)

private data class MetaQuiz(
    val question: String,
    val options: List<String>,
    val correctIndex: Int,
    val explanation: String
)

private val techniques = listOf(
    MetaTechnique(
        name = "Retrieval Practice",
        icon = "🔁",
        description = "Actively recalling information from memory strengthens neural pathways far more than passive review.",
        procedure = "After studying, close the book and write/say everything you remember. Use flashcards (digital/physical) and practice tests. Cover -> Recall -> Verify -> Repeat.",
        effectSize = "d = 0.74 (meta-analysis by Agarwal et al., 2021)",
        citation = "Roediger & Karpicke (2006). The power of testing memory. Perspectives on Psychological Science."
    ),
    MetaTechnique(
        name = "Spaced Repetition",
        icon = "⏳",
        description = "Distributing study sessions over time dramatically improves long-term retention compared to massed practice (cramming).",
        procedure = "Review material at increasing intervals: 1 day -> 3 days -> 1 week -> 2 weeks -> 1 month. Use an SRS (Spaced Repetition System) like Anki or Leitner boxes.",
        effectSize = "d = 0.65 (Cepeda et al., 2006 meta-analysis)",
        citation = "Ebbinghaus (1885). Memory: A Contribution to Experimental Psychology. Cepeda et al. (2006). Psychological Bulletin."
    ),
    MetaTechnique(
        name = "Interleaving",
        icon = "🔄",
        description = "Mixing different topics or types of problems during study improves discrimination and transfer.",
        procedure = "Instead of blocking (AAA BBB CCC), mix problems: A C B A B C. This forces your brain to identify which strategy to apply, building flexible knowledge.",
        effectSize = "d = 0.42 (Rohrer et al., 2014)",
        citation = "Rohrer, Dedrick, & Stershic (2014). The benefit of interleaved math practice. Journal of Educational Psychology."
    ),
    MetaTechnique(
        name = "Elaboration",
        icon = "🔗",
        description = "Explaining and connecting new ideas to existing knowledge creates richer memory traces.",
        procedure = "Ask 'Why?' and 'How?' about what you're learning. Connect new concepts to things you already know. Explain ideas to someone else (Feynman Technique).",
        effectSize = "d = 0.56 (Fiorella & Mayer, 2015)",
        citation = "Fiorella & Mayer (2015). Learning as a generative activity. Cambridge University Press."
    ),
    MetaTechnique(
        name = "Dual Coding",
        icon = "👁️",
        description = "Combining verbal and visual information leverages multiple cognitive channels for stronger encoding.",
        procedure = "Create diagrams, mind maps, flowcharts, and sketches alongside written notes. Combine text with relevant imagery. Use concept mapping.",
        effectSize = "d = 0.53 (Mayer, 2014)",
        citation = "Mayer (2014). Cognitive theory of multimedia learning. Cambridge Handbook of Multimedia Learning."
    ),
    MetaTechnique(
        name = "Concrete Examples",
        icon = "📋",
        description = "Using specific instances to understand abstract concepts improves transfer and application.",
        procedure = "After learning an abstract concept, generate 2-3 concrete examples from different contexts. Look for real-world applications. Collect case studies.",
        effectSize = "d = 0.44 (Rawson et al., 2014)",
        citation = "Rawson, Thomas, & Jacoby (2014). The power of examples. Journal of Experimental Psychology."
    )
)

private val quizzes = listOf(
    MetaQuiz("Which study technique has the HIGHEST average effect size for long-term retention?", listOf("Rereading notes", "Retrieval practice", "Highlighting", "Summarizing"), 1, "Meta-analyses consistently show retrieval practice (d=0.74) outperforms all other common study strategies."),
    MetaQuiz("What is the key principle behind spaced repetition?", listOf("Study everything in one session", "Distribute practice over increasing intervals", "Always study the same topic", "Never review old material"), 1, "Spaced repetition uses expanding intervals to strengthen memory at the optimal moment before forgetting occurs."),
    MetaQuiz("Interleaving improves learning by:", listOf("Making studying easier", "Forcing discrimination between problem types", "Reducing study time", "Eliminating difficult problems"), 1, "Interleaving forces your brain to identify which strategy fits each problem, building flexible knowledge."),
    MetaQuiz("The Feynman Technique is most closely related to which meta-learning strategy?", listOf("Spaced repetition", "Interleaving", "Elaboration", "Dual coding"), 2, "Explaining concepts in simple terms (Feynman Technique) is a form of elaboration - connecting ideas and identifying gaps."),
    MetaQuiz("Dual coding theory suggests learning is enhanced by:", listOf("Studying twice as long", "Combining visual and verbal information", "Listening to music", "Studying alone"), 1, "Dual coding leverages both visual and verbal processing channels for stronger, more redundant memory encoding."),
    MetaQuiz("What does the 'forgetting curve' demonstrate?", listOf("Memory improves over time", "Most forgetting occurs rapidly after learning", "Forgetting is constant", "Sleep eliminates forgetting"), 1, "Ebbinghaus' forgetting curve shows exponential decay - most forgetting happens immediately after learning, then levels off."),
    MetaQuiz("Which is NOT one of the six evidence-based learning strategies?", listOf("Retrieval practice", "Spaced repetition", "Highlighting", "Concrete examples"), 2, "Highlighting has low effectiveness (d=0.18) compared to strategies like retrieval practice (d=0.74) and spaced repetition (d=0.65)."),
    MetaQuiz("The 'generation effect' refers to:", listOf("Generating your own study music", "Better memory for self-generated information vs. read information", "Generating new ideas", "Creating study groups"), 1, "Generating information yourself (e.g., self-testing, creating examples) produces stronger memory than passively reading it.")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MetaLearningScreen(navController: NavHostController) {
    var selectedTechnique by remember { mutableStateOf<Int?>(null) }
    var quizScore by remember { mutableIntStateOf(0) }
    var quizIndex by remember { mutableIntStateOf(0) }
    var showQuiz by remember { mutableStateOf(false) }
    var showResult by remember { mutableStateOf(false) }
    var selectedAnswer by remember { mutableStateOf<Int?>(null) }
    var answerState by remember { mutableStateOf(-1) }

    if (showResult) {
        MetaQuizResultScreen(
            score = quizScore,
            total = quizzes.size,
            onBack = { navController.popBackStack() },
            onRetry = { quizScore = 0; quizIndex = 0; showResult = false; selectedAnswer = null; answerState = -1 }
        )
        return
    }

    if (showQuiz) {
        if (quizIndex >= quizzes.size) {
            showResult = true
            LaunchedEffect(Unit) {
                AppStorage.storage.saveGameResult("meta_learning_quiz", quizScore * 10, quizScore * 15)
            }
            return
        }
        val q = quizzes[quizIndex]
        Column(modifier = Modifier.fillMaxSize()) {
            TopAppBar(
                title = { Text("Meta-Learning Quiz") },
                navigationIcon = { IconButton(onClick = { showQuiz = false }) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back") } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Surface, titleContentColor = OnBackground)
            )
            Column(modifier = Modifier.fillMaxSize().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("${quizIndex + 1}/${quizzes.size}", color = OnSurfaceVariant, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = (quizIndex + 1).toFloat() / quizzes.size,
                    modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                    color = MetaLearningColor, trackColor = SurfaceVariant
                )
                Spacer(modifier = Modifier.height(24.dp))
                Text(q.question, fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = OnBackground, textAlign = TextAlign.Center)
                Spacer(modifier = Modifier.height(24.dp))
                q.options.forEachIndexed { i, opt ->
                    val bg = when { answerState == 1 && i == q.correctIndex -> SuccessColor.copy(alpha = 0.2f)
                        answerState == 0 && i == selectedAnswer -> ErrorColor.copy(alpha = 0.2f)
                        selectedAnswer == i -> Primary.copy(alpha = 0.15f) else -> Surface }
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                            .clickable(enabled = answerState == -1) { selectedAnswer = i; answerState = if (i == q.correctIndex) 1 else 0; if (i == q.correctIndex) quizScore++ },
                        colors = CardDefaults.cardColors(containerColor = bg),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            if (answerState != -1 && i == q.correctIndex) Icon(Icons.Default.Check, "Correct", tint = SuccessColor)
                            else if (answerState == 0 && i == selectedAnswer) Icon(Icons.Default.Close, "Wrong", tint = ErrorColor)
                            else Spacer(modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(opt, color = OnSurface, modifier = Modifier.weight(1f))
                        }
                    }
                }
                if (answerState != -1) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Card(colors = CardDefaults.cardColors(containerColor = Surface), modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
                        Text(q.explanation, color = OnSurfaceVariant, fontSize = 13.sp, modifier = Modifier.padding(12.dp))
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = { selectedAnswer = null; answerState = -1; quizIndex++ },
                        colors = ButtonDefaults.buttonColors(containerColor = Primary),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("Next", fontWeight = FontWeight.Bold) }
                }
            }
        }
        return
    }

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("Meta-Learning") },
            navigationIcon = { IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back") } },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Surface, titleContentColor = OnBackground)
        )

        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Spacer(modifier = Modifier.height(4.dp))

            Card(
                colors = CardDefaults.cardColors(containerColor = Surface),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("🧠", fontSize = 36.sp)
                    Text("Learn How to Learn", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = OnBackground)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Evidence-based learning strategies backed by cognitive science", color = OnSurfaceVariant, fontSize = 13.sp, textAlign = TextAlign.Center)
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = { showQuiz = true },
                        colors = ButtonDefaults.buttonColors(containerColor = MetaLearningColor),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("Take Knowledge Quiz", fontWeight = FontWeight.Bold) }
                }
            }

            Text("Six Evidence-Based Learning Techniques", fontWeight = FontWeight.Bold, color = OnBackground, fontSize = 16.sp)

            techniques.forEachIndexed { idx, tech ->
                val expanded = selectedTechnique == idx
                Card(
                    modifier = Modifier.fillMaxWidth().clickable { selectedTechnique = if (expanded) null else idx },
                    colors = CardDefaults.cardColors(containerColor = Surface),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(tech.icon, fontSize = 24.sp)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(tech.name, fontWeight = FontWeight.Bold, color = OnBackground)
                                Text("Effect: ${tech.effectSize}", color = MetaLearningColor, fontSize = 11.sp)
                            }
                            Text(if (expanded) "▲" else "▼", color = OnSurfaceVariant)
                        }
                        if (expanded) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(tech.description, color = OnSurface, fontSize = 14.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Procedure", fontWeight = FontWeight.SemiBold, color = OnSurfaceVariant, fontSize = 12.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(tech.procedure, color = OnSurface, fontSize = 13.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(tech.citation, color = OnSurfaceVariant, fontSize = 11.sp)
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun MetaQuizResultScreen(score: Int, total: Int, onBack: () -> Unit, onRetry: () -> Unit) {
    val pct = if (total > 0) (score * 100) / total else 0
    Column(modifier = Modifier.fillMaxSize().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Text("📊", fontSize = 64.sp)
        Spacer(modifier = Modifier.height(16.dp))
        Text("Quiz Complete!", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = OnBackground)
        Spacer(modifier = Modifier.height(24.dp))
        Card(colors = CardDefaults.cardColors(containerColor = Surface), shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("$score / $total", fontSize = 48.sp, fontWeight = FontWeight.Bold, color = Primary)
                Text("$pct% accuracy", color = OnSurfaceVariant)
                Spacer(modifier = Modifier.height(12.dp))
                Text("+${score * 15} XP", color = SuccessColor, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onRetry, modifier = Modifier.fillMaxWidth().height(50.dp), colors = ButtonDefaults.buttonColors(containerColor = Primary), shape = RoundedCornerShape(12.dp)) {
            Text("Retry Quiz", fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedButton(onClick = onBack, modifier = Modifier.fillMaxWidth().height(50.dp), shape = RoundedCornerShape(12.dp)) {
            Text("Back", color = OnBackground)
        }
    }
}
