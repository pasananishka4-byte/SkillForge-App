package com.skillforge.app.data

import kotlin.math.floor
import kotlin.math.pow

data class Skill(
    val id: Long,
    val name: String,
    val category: String,
    val description: String,
    val icon: String,
    val sortOrder: Int,
    val maxLevel: Int = 50,
    val protocol: String = "",
    val scientificBasis: String = ""
)

data class TrainingProtocol(
    val name: String,
    val description: String,
    val procedure: String,
    val citation: String,
    val effectSize: String
)

data class Challenge(
    val id: Long,
    val skillId: Long,
    val difficulty: String,
    val question: String,
    val options: List<String>,
    val correctAnswerIndex: Int,
    val explanation: String,
    val xpReward: Int
)

data class Achievement(
    val id: Long,
    val name: String,
    val description: String,
    val icon: String,
    val category: String,
    val requirementType: String,
    val requirementValue: Int,
    val xpReward: Int
)

fun xpForCurrentLevel(level: Int): Long {
    var total = 0L
    var xpNeeded = 100L
    for (i in 1 until level) {
        total += xpNeeded
        xpNeeded = (xpNeeded * 1.2).toLong()
    }
    return total
}

fun xpForNextLevel(level: Int): Long {
    var xpNeeded = 100L
    for (i in 1 until level) {
        xpNeeded = (xpNeeded * 1.2).toLong()
    }
    return xpNeeded
}

fun calculateLevelFromXP(xp: Long): Int {
    var level = 1
    var xpNeeded = 100L
    var remainingXP = xp
    while (remainingXP >= xpNeeded) {
        remainingXP -= xpNeeded
        level++
        xpNeeded = (xpNeeded * 1.2).toLong()
    }
    return level
}

object SeedData {

    val protocols = mapOf(
        1L to TrainingProtocol(
            name = "Dual N-Back",
            description = "Simultaneous auditory-visual working memory updating",
            procedure = "Respond when current stimulus matches one presented N steps back in the sequence",
            citation = "Jaeggi et al. (2008). Improving fluid intelligence with training on working memory. PNAS.",
            effectSize = "g = 0.24 (Au et al., 2014 meta-analysis)"
        ),
        2L to TrainingProtocol(
            name = "Task Switching + Inhibition",
            description = "Rapidly alternate between cognitive sets while suppressing interference",
            procedure = "Switch between two tasks on alternating trials; measure switch cost and inhibition accuracy",
            citation = "Miyake et al. (2000). The unity and diversity of executive functions. Cognitive Psychology.",
            effectSize = "d = 0.33-0.67 (Karbach & Kray, 2009)"
        ),
        3L to TrainingProtocol(
            name = "Matrix Reasoning",
            description = "Identify patterns and rules in abstract visual matrices",
            procedure = "Complete matrix patterns by identifying underlying relational rules",
            citation = "Raven's Progressive Matrices; COGITO study (Schmiedek et al., 2010)",
            effectSize = "Transfer to Gf: g = 0.17-0.43"
        ),
        4L to TrainingProtocol(
            name = "Choice Reaction Time",
            description = "Speeded decision-making with multiple stimulus-response mappings",
            procedure = "Respond as quickly as possible to different stimuli with corresponding responses",
            citation = "Ball et al. (2007). Speed of processing training in ACTIVE study. J Gerontol.",
            effectSize = "d = 0.88 (trained tasks); d = 0.36 (near transfer)"
        ),
        5L to TrainingProtocol(
            name = "Stroop + Sustained Attention",
            description = "Interference control and vigilance maintenance",
            procedure = "Name ink color of incongruent color words; maintain focus over extended periods",
            citation = "MacLeod (1991). Half a century of research on the Stroop effect. Psychological Bulletin.",
            effectSize = "d = 0.45-0.85 (attention training effects)"
        )
    )

    val skills = listOf(
        Skill(id = 1, name = "Working Memory", category = "Core Cognition", description = "Capacity to hold and manipulate information in mind over short intervals", icon = "🧠", sortOrder = 1, protocol = "Dual N-Back", scientificBasis = "Dual-task WM updating with auditory-visual streams; strongest transfer to fluid intelligence (Au et al., 2014)"),
        Skill(id = 2, name = "Executive Control", category = "Core Cognition", description = "Cognitive flexibility, inhibition, and goal-directed behavior switching", icon = "⚙️", sortOrder = 2, protocol = "Task Switching + Inhibition", scientificBasis = "Miyake's unity/diversity framework; transfer to executive function and academic performance (Karbach & Kray, 2009)"),
        Skill(id = 3, name = "Fluid Reasoning", category = "Core Cognition", description = "Novel problem-solving through pattern recognition and relational integration", icon = "🔮", sortOrder = 3, protocol = "Matrix Reasoning", scientificBasis = "Raven's matrices paradigm; relational integration training shows transfer to Gf (COGITO study)"),
        Skill(id = 4, name = "Processing Speed", category = "Core Cognition", description = "Speed of perceptual encoding, decision-making, and response execution", icon = "⚡", sortOrder = 4, protocol = "Choice Reaction Time", scientificBasis = "ACTIVE study: speed training largest effect size of all cognitive interventions (Ball et al., 2007)"),
        Skill(id = 5, name = "Attentional Control", category = "Core Cognition", description = "Selective, sustained, and divided attention with interference resolution", icon = "🎯", sortOrder = 5, protocol = "Stroop + Sustained Attention", scientificBasis = "Stroop interference control and vigilance paradigms; core EF subcomponent (Miyake, 2000)")
    )

    val challenges = listOf(
        // ========== WORKING MEMORY (skillId = 1) ==========
        Challenge(id = 1, skillId = 1, difficulty = "Easy", question = "What mnemonic technique involves associating items with specific locations along a familiar route?", options = listOf("Acronym method", "Method of Loci (Memory Palace)", "Chunking", "Rehearsal"), correctAnswerIndex = 1, explanation = "The Method of Loci, or Memory Palace, involves placing items you want to remember at specific locations along a mental route.", xpReward = 10),
        Challenge(id = 2, skillId = 1, difficulty = "Medium", question = "According to Ebbinghaus's forgetting curve, when does most forgetting occur?", options = listOf("Gradually over months", "Immediately after learning, then levels off", "Only after a week", "At a constant rate"), correctAnswerIndex = 1, explanation = "The forgetting curve shows that memory decays exponentially, with the steepest drop occurring right after learning.", xpReward = 20),
        Challenge(id = 3, skillId = 1, difficulty = "Hard", question = "The 'spacing effect' in memory research suggests that:", options = listOf("Studying in a quiet space improves recall", "Distributing study over time is more effective than cramming", "Physical spacing between learners improves memory", "Larger text is easier to remember"), correctAnswerIndex = 1, explanation = "The spacing effect shows that information is better retained when study sessions are spaced out over time rather than concentrated in one session.", xpReward = 30),
        Challenge(id = 4, skillId = 1, difficulty = "Easy", question = "What does 'chunking' help with in memory?", options = listOf("Breaking bad habits", "Grouping information into manageable units for better recall", "Sleeping better", "Eating faster"), correctAnswerIndex = 1, explanation = "Chunking is a memory technique where you break information into smaller groups or 'chunks' to make it easier to remember.", xpReward = 10),
        Challenge(id = 5, skillId = 1, difficulty = "Medium", question = "In the classic N-back task, what does '2-back' mean?", options = listOf("Press when the stimulus matches the one 2 steps ago", "Press 2 times for each stimulus", "Wait 2 seconds before responding", "Remember the last 2 items in order"), correctAnswerIndex = 0, explanation = "In 2-back, participants must indicate whether the current stimulus matches the one presented two trials earlier, taxing WM updating.", xpReward = 20),
        Challenge(id = 6, skillId = 1, difficulty = "Hard", question = "Dual N-Back training has been shown in meta-analyses to produce what level of transfer to fluid intelligence?", options = listOf("No transfer (g = 0.00)", "Small but significant (g = 0.24)", "Large transfer (g = 0.80)", "Negative transfer (g = -0.10)"), correctAnswerIndex = 1, explanation = "Au et al.'s 2014 meta-analysis found a small but significant effect of dual N-back training on fluid intelligence (g = 0.24).", xpReward = 30),

        // ========== EXECUTIVE CONTROL (skillId = 2) ==========
        Challenge(id = 7, skillId = 2, difficulty = "Easy", question = "If all roses are flowers and some flowers fade quickly, can we conclude that some roses fade quickly?", options = listOf("Yes", "No", "Cannot be determined", "Only in spring"), correctAnswerIndex = 2, explanation = "The second premise says 'some flowers' fade quickly, but those might not be roses. We cannot determine the conclusion.", xpReward = 10),
        Challenge(id = 8, skillId = 2, difficulty = "Medium", question = "What is the contrapositive of 'If it rains, then the ground is wet'?", options = listOf("If it does not rain, the ground is not wet", "If the ground is wet, then it rained", "If the ground is not wet, then it did not rain", "If it rains, the ground is not wet"), correctAnswerIndex = 2, explanation = "The contrapositive of P→Q is ¬Q→¬P. So 'If the ground is not wet, then it did not rain' is correct.", xpReward = 20),
        Challenge(id = 9, skillId = 2, difficulty = "Hard", question = "In propositional logic, which of the following is a tautology?", options = listOf("P ∧ ¬P", "P ∨ ¬P", "P → ¬P", "P ∧ Q"), correctAnswerIndex = 1, explanation = "P ∨ ¬P (a statement or its negation) is always true regardless of the truth value of P, making it a tautology.", xpReward = 30),
        Challenge(id = 10, skillId = 2, difficulty = "Easy", question = "What is 'cognitive flexibility'?", options = listOf("Having flexible joints", "The ability to switch between different mental tasks or rules", "Being able to remember more things", "Thinking very quickly"), correctAnswerIndex = 1, explanation = "Cognitive flexibility is the ability to shift between different tasks, rules, or mental sets, a key component of executive control.", xpReward = 10),
        Challenge(id = 11, skillId = 2, difficulty = "Medium", question = "What is the 'sunk cost fallacy'?", options = listOf("Quit early to cut losses", "Continue investing because of past investment despite poor future outlook", "Always choose the cheapest option", "Ignore past data when making decisions"), correctAnswerIndex = 1, explanation = "The sunk cost fallacy causes people to continue an endeavor because of previously invested resources rather than future value.", xpReward = 20),
        Challenge(id = 12, skillId = 2, difficulty = "Hard", question = "The 'Stroop effect' measures what cognitive function?", options = listOf("Working memory capacity", "Inhibitory control and interference resolution", "Visual acuity", "Reaction speed"), correctAnswerIndex = 1, explanation = "The Stroop effect requires inhibiting the prepotent reading response to name the ink color, measuring inhibitory control.", xpReward = 30),
        Challenge(id = 13, skillId = 2, difficulty = "Easy", question = "What is a 'syllogism'?", options = listOf("A type of poem", "A form of logical reasoning with two premises and a conclusion", "A mathematical equation", "A type of puzzle"), correctAnswerIndex = 1, explanation = "A syllogism is a form of deductive reasoning consisting of a major premise, a minor premise, and a conclusion.", xpReward = 10),
        Challenge(id = 14, skillId = 2, difficulty = "Medium", question = "Task-switching cost refers to:", options = listOf("The time needed to complete a single task", "The performance decrement when switching between tasks vs repeating", "The cost of learning a new skill", "The energy consumed during multitasking"), correctAnswerIndex = 1, explanation = "Task-switching cost is the slower and more error-prone performance when switching between different tasks compared to repeating the same task.", xpReward = 20),
        Challenge(id = 15, skillId = 2, difficulty = "Hard", question = "In Miyake's model, the three core executive functions are:", options = listOf("Memory, attention, perception", "Updating, shifting, inhibition", "Planning, reasoning, problem-solving", "Reading, writing, arithmetic"), correctAnswerIndex = 1, explanation = "Miyake et al. (2000) identified updating (WM), shifting (cognitive flexibility), and inhibition as the three core EFs.", xpReward = 30),
        Challenge(id = 16, skillId = 2, difficulty = "Easy", question = "What is 'satisficing' in decision-making?", options = listOf("Choosing the absolute best option after exhaustive search", "Choosing the first option that meets minimum criteria", "Avoiding decisions entirely", "Making decisions based solely on emotions"), correctAnswerIndex = 1, explanation = "Satisficing is a decision-making strategy that aims for a satisfactory or adequate result rather than the optimal solution.", xpReward = 10),
        Challenge(id = 17, skillId = 2, difficulty = "Easy", question = "When solving a complex problem, what is the first recommended step?", options = listOf("Jump to the easiest solution", "Break the problem into smaller parts", "Ask someone else to solve it", "Ignore the problem"), correctAnswerIndex = 1, explanation = "Breaking a complex problem into smaller, manageable parts is the recommended first step in problem-solving.", xpReward = 10),
        Challenge(id = 18, skillId = 2, difficulty = "Easy", question = "Parkinson's Law in time management states:", options = listOf("Work expands to fill the time available", "The most productive people work the longest hours", "Breaks reduce productivity", "Multitasking improves efficiency"), correctAnswerIndex = 0, explanation = "Parkinson's Law states that work expands to fill the time allotted, which is why setting tight deadlines can improve efficiency.", xpReward = 10),
        Challenge(id = 19, skillId = 2, difficulty = "Easy", question = "The 2-minute rule in time management says:", options = listOf("If a task takes more than 2 minutes, delegate it", "If a task takes less than 2 minutes, do it immediately", "Take a 2-minute break every 20 minutes", "Spend only 2 minutes planning each day"), correctAnswerIndex = 1, explanation = "David Allen's 2-minute rule says if a task can be completed in 2 minutes or less, do it immediately rather than deferring it.", xpReward = 10),
        Challenge(id = 20, skillId = 2, difficulty = "Medium", question = "The 'Eisenhower Matrix' helps with:", options = listOf("Cooking recipes", "Prioritizing tasks by urgency and importance", "Exercise routines", "Financial planning"), correctAnswerIndex = 1, explanation = "The Eisenhower Matrix categorizes tasks into four quadrants based on urgency and importance to guide prioritization.", xpReward = 20),
        Challenge(id = 21, skillId = 2, difficulty = "Medium", question = "Emotional regulation is a key component of:", options = listOf("Processing speed", "Executive control and self-regulation", "Working memory", "Visual perception"), correctAnswerIndex = 1, explanation = "Emotional regulation draws heavily on executive control systems, especially inhibitory control and cognitive shifting.", xpReward = 20),

        // ========== FLUID REASONING (skillId = 3) ==========
        Challenge(id = 22, skillId = 3, difficulty = "Easy", question = "You have 3 apples and you give 2 away. How many apples do you have?", options = listOf("1", "2", "3", "0"), correctAnswerIndex = 0, explanation = "You started with 3 and gave away 2, so you have 1 left.", xpReward = 10),
        Challenge(id = 23, skillId = 3, difficulty = "Medium", question = "A bat and a ball cost $1.10 in total. The bat costs $1.00 more than the ball. How much does the ball cost?", options = listOf("$0.10", "$0.05", "$0.15", "$0.01"), correctAnswerIndex = 1, explanation = "If the ball costs $0.05, the bat costs $1.05, totaling $1.10. The intuitive answer of $0.10 is wrong.", xpReward = 20),
        Challenge(id = 24, skillId = 3, difficulty = "Hard", question = "You have two ropes. Each takes exactly 1 hour to burn completely, but they burn at non-uniform rates. How do you measure exactly 45 minutes?", options = listOf("Light both ends of one rope and one end of the other", "Light one rope at both ends and the other at one end, then light the second rope's other end when the first finishes", "Cut one rope in half and burn sequentially", "This is impossible"), correctAnswerIndex = 1, explanation = "Light rope A at both ends and rope B at one end. When A burns out (30 min), light B's other end. B finishes 15 min later. Total: 45 minutes.", xpReward = 30),
        Challenge(id = 25, skillId = 3, difficulty = "Easy", question = "Which of the following is an opinion rather than a fact?", options = listOf("Water boils at 100°C at sea level", "The Earth orbits the Sun", "Pizza is the best food ever", "Humans need oxygen to survive"), correctAnswerIndex = 2, explanation = "'Pizza is the best food ever' expresses a personal preference, making it an opinion.", xpReward = 10),
        Challenge(id = 26, skillId = 3, difficulty = "Medium", question = "A study finds that people who eat breakfast perform better on exams. What is the most likely confounding variable?", options = listOf("The type of breakfast", "Socioeconomic status affecting both nutrition and education access", "Time of day the exam is held", "The color of the exam paper"), correctAnswerIndex = 1, explanation = "Socioeconomic status can influence both eating habits and educational resources, acting as a confounding variable.", xpReward = 20),
        Challenge(id = 27, skillId = 3, difficulty = "Hard", question = "In logical reasoning, what is 'survivorship bias'?", options = listOf("Focusing only on successful outcomes while ignoring failures", "Assuming the first information you hear is correct", "Believing that your group is better than others", "Only trusting information from survivors of events"), correctAnswerIndex = 0, explanation = "Survivorship bias occurs when we focus on entities that passed some filter (survived) and overlook those that didn't, leading to false conclusions.", xpReward = 30),
        Challenge(id = 28, skillId = 3, difficulty = "Easy", question = "What gas do plants absorb from the atmosphere during photosynthesis?", options = listOf("Oxygen", "Nitrogen", "Carbon dioxide", "Hydrogen"), correctAnswerIndex = 2, explanation = "Plants absorb carbon dioxide (CO2) and use sunlight to convert it into glucose and oxygen during photosynthesis.", xpReward = 10),
        Challenge(id = 29, skillId = 3, difficulty = "Medium", question = "What planet is known as the Red Planet?", options = listOf("Venus", "Jupiter", "Mars", "Saturn"), correctAnswerIndex = 2, explanation = "Mars is called the Red Planet due to iron oxide (rust) on its surface.", xpReward = 20),
        Challenge(id = 30, skillId = 3, difficulty = "Hard", question = "What is the Heisenberg Uncertainty Principle?", options = listOf("Energy cannot be created or destroyed", "You cannot simultaneously know the exact position and momentum of a particle", "Light is both a wave and a particle", "Entropy always increases"), correctAnswerIndex = 1, explanation = "The Heisenberg Uncertainty Principle states that the more precisely you know a particle's position, the less precisely you can know its momentum.", xpReward = 30),
        Challenge(id = 31, skillId = 3, difficulty = "Easy", question = "What is the powerhouse of the cell?", options = listOf("Nucleus", "Ribosome", "Mitochondria", "Golgi apparatus"), correctAnswerIndex = 2, explanation = "Mitochondria generate most of the cell's supply of ATP, used as a source of chemical energy.", xpReward = 10),
        Challenge(id = 32, skillId = 3, difficulty = "Medium", question = "What is 'confirmation bias'?", options = listOf("Only confirming appointments on time", "Tending to search for information that confirms existing beliefs", "Being biased toward positive outcomes", "Confirming others' opinions"), correctAnswerIndex = 1, explanation = "Confirmation bias is the tendency to search for, interpret, and recall information in a way that confirms one's preexisting beliefs.", xpReward = 20),
        Challenge(id = 33, skillId = 3, difficulty = "Hard", question = "In relational reasoning, what does 'analogical mapping' require?", options = listOf("Memorizing pairs of words", "Identifying structural alignment between two domains", "Statistical analysis of probabilities", "Verbal fluency"), correctAnswerIndex = 1, explanation = "Analogical mapping involves aligning the relational structure of a source domain onto a target domain, a core component of fluid reasoning.", xpReward = 30),
        Challenge(id = 34, skillId = 3, difficulty = "Medium", question = "Which ancient civilization built Machu Picchu?", options = listOf("Aztec", "Maya", "Inca", "Olmec"), correctAnswerIndex = 2, explanation = "Machu Picchu was built in the 15th century by the Inca Empire in present-day Peru.", xpReward = 20),

        // ========== PROCESSING SPEED (skillId = 4) ==========
        Challenge(id = 35, skillId = 4, difficulty = "Easy", question = "What is the largest continent by area?", options = listOf("Africa", "North America", "Asia", "Europe"), correctAnswerIndex = 2, explanation = "Asia is the largest continent, covering approximately 44.58 million square kilometers.", xpReward = 10),
        Challenge(id = 36, skillId = 4, difficulty = "Medium", question = "Which river is the longest in the world?", options = listOf("Amazon", "Nile", "Mississippi", "Yangtze"), correctAnswerIndex = 1, explanation = "The Nile is traditionally considered the longest river at approximately 6,650 km.", xpReward = 20),
        Challenge(id = 37, skillId = 4, difficulty = "Hard", question = "What country has the most time zones?", options = listOf("Russia", "United States", "France", "China"), correctAnswerIndex = 2, explanation = "France has 12 time zones due to its overseas territories, more than any other country.", xpReward = 30),
        Challenge(id = 38, skillId = 4, difficulty = "Easy", question = "What is the smallest country in the world by area?", options = listOf("Monaco", "Vatican City", "San Marino", "Liechtenstein"), correctAnswerIndex = 1, explanation = "Vatican City is the smallest country in the world with an area of approximately 0.44 square kilometers.", xpReward = 10),
        Challenge(id = 39, skillId = 4, difficulty = "Easy", question = "Who painted the Mona Lisa?", options = listOf("Michelangelo", "Leonardo da Vinci", "Raphael", "Donatello"), correctAnswerIndex = 1, explanation = "Leonardo da Vinci painted the Mona Lisa between 1503 and 1519.", xpReward = 10),
        Challenge(id = 40, skillId = 4, difficulty = "Medium", question = "Which musical term means to gradually get louder?", options = listOf("Diminuendo", "Sforzando", "Crescendo", "Staccato"), correctAnswerIndex = 2, explanation = "Crescendo is a gradual increase in volume. It comes from the Italian word meaning 'growing'.", xpReward = 20),
        Challenge(id = 41, skillId = 4, difficulty = "Hard", question = "The literary device 'synecdoche' refers to:", options = listOf("A comparison using 'like' or 'as'", "A part representing the whole or vice versa", "An exaggerated statement not meant literally", "Giving human qualities to non-human things"), correctAnswerIndex = 1, explanation = "Synecdoche is a figure of speech where a part represents the whole (e.g., 'wheels' for car) or the whole represents a part.", xpReward = 30),
        Challenge(id = 42, skillId = 4, difficulty = "Easy", question = "What instrument has 88 keys?", options = listOf("Guitar", "Violin", "Piano", "Flute"), correctAnswerIndex = 2, explanation = "A standard modern piano has 88 keys: 52 white keys and 36 black keys.", xpReward = 10),
        Challenge(id = 43, skillId = 4, difficulty = "Medium", question = "What is 'simple reaction time'?", options = listOf("The time to choose between multiple responses", "The time to respond to a single stimulus", "A measure of memory capacity", "The time to complete a complex task"), correctAnswerIndex = 1, explanation = "Simple reaction time measures how quickly you can respond to a single, predictable stimulus, a basic measure of processing speed.", xpReward = 20),
        Challenge(id = 44, skillId = 4, difficulty = "Hard", question = "In the ACTIVE study, which cognitive training showed the largest effect size?", options = listOf("Memory training", "Reasoning training", "Speed of processing training", "All were equal"), correctAnswerIndex = 2, explanation = "The ACTIVE study found that speed of processing training had the largest effect size (d = 0.88) compared to memory and reasoning training.", xpReward = 30),
        Challenge(id = 45, skillId = 4, difficulty = "Easy", question = "In which year did World War II end?", options = listOf("1943", "1944", "1945", "1946"), correctAnswerIndex = 2, explanation = "World War II ended in 1945 with the surrender of Germany in May and Japan in August/September.", xpReward = 10),

        // ========== ATTENTIONAL CONTROL (skillId = 5) ==========
        Challenge(id = 46, skillId = 5, difficulty = "Easy", question = "How long is a typical 'Pomodoro' focus session?", options = listOf("15 minutes", "25 minutes", "45 minutes", "60 minutes"), correctAnswerIndex = 1, explanation = "The Pomodoro Technique uses 25-minute focused work sessions followed by short breaks.", xpReward = 10),
        Challenge(id = 47, skillId = 5, difficulty = "Medium", question = "What is 'attentional blink'?", options = listOf("When you forget what you were doing", "A brief period after detecting one stimulus where detection of a second stimulus is impaired", "When your eyes physically blink while reading", "A technique to improve focus"), correctAnswerIndex = 1, explanation = "Attentional blink is a phenomenon where processing one target temporarily impairs the ability to detect a second target that appears shortly after.", xpReward = 20),
        Challenge(id = 48, skillId = 5, difficulty = "Hard", question = "Research suggests the average human attention span during focused work is approximately:", options = listOf("3-5 minutes", "20-25 minutes", "45-60 minutes", "2-3 hours"), correctAnswerIndex = 1, explanation = "Studies suggest that sustained attention on a single task typically lasts about 20-25 minutes before a break is needed.", xpReward = 30),
        Challenge(id = 49, skillId = 5, difficulty = "Easy", question = "What is 'active recall'?", options = listOf("Reading notes multiple times", "Actively trying to retrieve information from memory", "Copying notes by hand", "Listening to recordings while sleeping"), correctAnswerIndex = 1, explanation = "Active recall is a study technique where you actively stimulate memory during the learning process by retrieving information without looking at the source.", xpReward = 10),
        Challenge(id = 50, skillId = 5, difficulty = "Medium", question = "The Feynman Technique involves:", options = listOf("Memorizing formulas repeatedly", "Teaching a concept in simple terms to identify gaps in understanding", "Reading textbooks cover to cover", "Taking notes in shorthand"), correctAnswerIndex = 1, explanation = "The Feynman Technique involves explaining a concept in simple language, which helps identify gaps in your understanding.", xpReward = 20),
        Challenge(id = 51, skillId = 5, difficulty = "Hard", question = "Which study method has been shown by research to have the highest effectiveness for long-term retention?", options = listOf("Rereading notes", "Highlighting text", "Practice testing (retrieval practice)", "Summarizing paragraphs"), correctAnswerIndex = 2, explanation = "Research consistently shows that practice testing (retrieval practice) is one of the most effective strategies for long-term retention.", xpReward = 30),
        Challenge(id = 52, skillId = 5, difficulty = "Easy", question = "What is 'interleaving' in study technique?", options = listOf("Writing with both hands", "Mixing different topics or subjects during study sessions", "Studying only one topic at a time", "Taking breaks between every sentence"), correctAnswerIndex = 1, explanation = "Interleaving involves mixing different topics or types of problems during study, which can improve long-term learning and transfer.", xpReward = 10),
        Challenge(id = 53, skillId = 5, difficulty = "Easy", question = "Which of these is most likely to improve focus while working?", options = listOf("Having multiple browser tabs open", "Listening to loud music with lyrics", "Turning off phone notifications", "Working in a noisy environment"), correctAnswerIndex = 2, explanation = "Turning off notifications eliminates a major source of distraction, allowing for deeper focus on the task at hand.", xpReward = 10),
        Challenge(id = 54, skillId = 5, difficulty = "Medium", question = "What is the difference between empathy and sympathy?", options = listOf("They are the same thing", "Empathy involves feeling with someone; sympathy involves feeling for someone", "Sympathy is stronger than empathy", "Empathy requires knowing the person personally"), correctAnswerIndex = 1, explanation = "Empathy means sharing another's feelings (feeling with them), while sympathy is acknowledging their feelings (feeling for them).", xpReward = 20),
        Challenge(id = 55, skillId = 5, difficulty = "Medium", question = "'Cognitive empathy' specifically refers to:", options = listOf("Feeling the same emotion as another person", "Understanding another person's perspective and thoughts", "Showing physical comfort to someone in distress", "Agreeing with someone's point of view"), correctAnswerIndex = 1, explanation = "Cognitive empathy is the ability to understand another person's mental state and perspective.", xpReward = 20),
        Challenge(id = 56, skillId = 5, difficulty = "Hard", question = "Mirror neurons are believed to be linked to empathy because they:", options = listOf("Only fire when we are in pain", "Activate both when performing an action and observing the same action in others", "Only exist in the prefrontal cortex", "Are responsible for all human emotions"), correctAnswerIndex = 1, explanation = "Mirror neurons fire both when we perform an action and when we observe someone else performing the same action.", xpReward = 30),
        Challenge(id = 57, skillId = 5, difficulty = "Easy", question = "Active listening primarily involves:", options = listOf("Thinking about your response while the other person talks", "Fully concentrating, understanding, responding, and remembering what is said", "Only hearing the words without interpretation", "Interrupting to share your opinion"), correctAnswerIndex = 1, explanation = "Active listening requires full attention, comprehension, thoughtful response, and retention of the message.", xpReward = 10),
        Challenge(id = 58, skillId = 5, difficulty = "Medium", question = "In communication, 'I-statements' are preferred because they:", options = listOf("Sound more authoritative", "Express feelings without blaming, reducing defensiveness", "Are shorter and easier to say", "Are always more accurate"), correctAnswerIndex = 1, explanation = "I-statements express the speaker's feelings without directly blaming the listener, reducing defensive reactions.", xpReward = 20),
        Challenge(id = 59, skillId = 5, difficulty = "Hard", question = "Emotional Intelligence (EQ) research by Daniel Goleman suggests that EQ:", options = listOf("Is less important than IQ for career success", "Includes self-awareness, self-regulation, motivation, empathy, and social skills", "Cannot be developed after childhood", "Is solely determined by genetics"), correctAnswerIndex = 1, explanation = "Goleman's model identifies five key components of EQ: self-awareness, self-regulation, internal motivation, empathy, and social skills.", xpReward = 30),
        Challenge(id = 60, skillId = 5, difficulty = "Medium", question = "Self-awareness is best described as:", options = listOf("Knowing your physical appearance", "Understanding your own emotions, strengths, weaknesses, and values", "Being aware of your surroundings", "Knowing how others see you only"), correctAnswerIndex = 1, explanation = "Self-awareness involves recognizing your own emotions, strengths, weaknesses, values, and their impact on others.", xpReward = 20),
        Challenge(id = 61, skillId = 5, difficulty = "Hard", question = "The '7-38-55 rule' of communication suggests that:", options = listOf("7% verbal, 38% vocal tone, 55% body language", "You should listen 70% of the time", "Effective messages are 7 words or less", "Communication has 55 possible channels"), correctAnswerIndex = 0, explanation = "Mehrabian's research suggested that in conveying feelings, 7% of the message comes from words, 38% from vocal tone, and 55% from body language.", xpReward = 30),
        Challenge(id = 62, skillId = 5, difficulty = "Easy", question = "What is 'servant leadership'?", options = listOf("Leading by giving orders", "A leadership philosophy where the leader's main goal is to serve others", "Leadership through fear", "Managing only the most important tasks"), correctAnswerIndex = 1, explanation = "Servant leadership, coined by Robert Greenleaf, prioritizes the growth and well-being of people and communities.", xpReward = 10),
        Challenge(id = 63, skillId = 5, difficulty = "Medium", question = "According to situational leadership theory, the best style depends on:", options = listOf("The leader's personality only", "The followers' readiness and competence level", "The size of the organization", "The industry sector"), correctAnswerIndex = 1, explanation = "Situational leadership theory suggests that effective leaders adapt their style based on followers' maturity and competence.", xpReward = 20),
        Challenge(id = 64, skillId = 5, difficulty = "Hard", question = "Transformational leadership is characterized by:", options = listOf("Maintaining the status quo and existing processes", "Inspiring followers to transcend self-interest for the organization's vision", "Strict enforcement of rules and procedures", "Delegating all decision-making to subordinates"), correctAnswerIndex = 1, explanation = "Transformational leaders inspire and motivate followers to achieve extraordinary outcomes by appealing to higher ideals.", xpReward = 30),
        Challenge(id = 65, skillId = 5, difficulty = "Easy", question = "What is the purpose of a to-do list?", options = listOf("To remember everything perfectly", "To organize and prioritize tasks to be completed", "To avoid doing any work", "To impress your boss"), correctAnswerIndex = 1, explanation = "A to-do list helps organize and prioritize tasks, ensuring important items are addressed.", xpReward = 10),
        Challenge(id = 66, skillId = 5, difficulty = "Hard", question = "The 'attentional network' model (Posner & Petersen) identifies which three networks?", options = listOf("Alerting, orienting, executive attention", "Input, processing, output", "Visual, auditory, tactile", "Short-term, long-term, procedural"), correctAnswerIndex = 0, explanation = "Posner's model identifies alerting (maintaining vigilance), orienting (selecting information), and executive (resolving conflict) attention networks.", xpReward = 30),
        Challenge(id = 67, skillId = 5, difficulty = "Medium", question = "Sustained attention tasks like the Mackworth Clock measure:", options = listOf("Memory span", "Vigilance over time", "Physical endurance", "Social awareness"), correctAnswerIndex = 1, explanation = "Sustained attention tasks measure vigilance - the ability to maintain focused attention over extended periods with infrequent targets.", xpReward = 20),
        Challenge(id = 68, skillId = 5, difficulty = "Medium", question = "Divided attention requires:", options = listOf("Focusing on one thing at a time", "Allocating cognitive resources across multiple tasks simultaneously", "Ignoring all stimuli", "Alternating attention rapidly"), correctAnswerIndex = 1, explanation = "Divided attention involves distributing cognitive resources across multiple tasks or stimuli at the same time.", xpReward = 20),
        Challenge(id = 69, skillId = 5, difficulty = "Easy", question = "The Johari Window is a model used to:", options = listOf("Map physical spaces for mindfulness", "Understand the relationship between self-awareness and others' perception of you", "Plan career development", "Organize daily tasks"), correctAnswerIndex = 1, explanation = "The Johari Window is a tool for understanding self-awareness through four quadrants: open, blind, hidden, and unknown.", xpReward = 10),
        Challenge(id = 70, skillId = 5, difficulty = "Easy", question = "What does 'pros and cons' analysis involve?", options = listOf("Listing only the advantages", "Listing advantages and disadvantages of each option", "Choosing the most expensive option", "Asking friends for advice"), correctAnswerIndex = 1, explanation = "A pros and cons analysis involves listing the advantages and disadvantages of each option to make a balanced decision.", xpReward = 10),

        // ========== META-LEARNING (skillId = 2 - Executive Control extension) ==========
        Challenge(id = 71, skillId = 2, difficulty = "Easy", question = "Which study technique has the HIGHEST average effect size for long-term retention?", options = listOf("Rereading notes", "Retrieval practice", "Highlighting", "Summarizing"), correctAnswerIndex = 1, explanation = "Meta-analyses consistently show retrieval practice (d=0.74) outperforms all other common study strategies.", xpReward = 10),
        Challenge(id = 72, skillId = 2, difficulty = "Medium", question = "What is the key principle behind spaced repetition?", options = listOf("Study everything in one session", "Distribute practice over increasing intervals", "Always study the same topic", "Never review old material"), correctAnswerIndex = 1, explanation = "Spaced repetition uses expanding intervals to strengthen memory at the optimal moment before forgetting occurs.", xpReward = 20),
        Challenge(id = 73, skillId = 2, difficulty = "Hard", question = "Interleaving improves learning by:", options = listOf("Making studying easier", "Forcing discrimination between problem types", "Reducing study time", "Eliminating difficult problems"), correctAnswerIndex = 1, explanation = "Interleaving forces your brain to identify which strategy fits each problem, building flexible knowledge.", xpReward = 30),
        Challenge(id = 74, skillId = 2, difficulty = "Easy", question = "The Feynman Technique is most closely related to which meta-learning strategy?", options = listOf("Spaced repetition", "Interleaving", "Elaboration", "Dual coding"), correctAnswerIndex = 2, explanation = "Explaining concepts in simple terms (Feynman Technique) is a form of elaboration - connecting ideas and identifying gaps.", xpReward = 10),
        Challenge(id = 75, skillId = 2, difficulty = "Medium", question = "Dual coding theory suggests learning is enhanced by:", options = listOf("Studying twice as long", "Combining visual and verbal information", "Listening to music", "Studying alone"), correctAnswerIndex = 1, explanation = "Dual coding leverages both visual and verbal processing channels for stronger, more redundant memory encoding.", xpReward = 20),
        Challenge(id = 76, skillId = 2, difficulty = "Hard", question = "What does the 'forgetting curve' demonstrate?", options = listOf("Memory improves over time", "Most forgetting occurs rapidly after learning", "Forgetting is constant", "Sleep eliminates forgetting"), correctAnswerIndex = 1, explanation = "Ebbinghaus' forgetting curve shows exponential decay - most forgetting happens immediately after learning, then levels off.", xpReward = 30),
        Challenge(id = 77, skillId = 2, difficulty = "Easy", question = "Which is NOT one of the six evidence-based learning strategies?", options = listOf("Retrieval practice", "Spaced repetition", "Highlighting", "Concrete examples"), correctAnswerIndex = 2, explanation = "Highlighting has low effectiveness (d=0.18) compared to strategies like retrieval practice (d=0.74) and spaced repetition (d=0.65).", xpReward = 10),

        // ========== QUICK THINKING / GENERAL KNOWLEDGE (skillId = 4 - Processing Speed extension) ==========
        Challenge(id = 78, skillId = 4, difficulty = "Easy", question = "What is the chemical symbol for gold?", options = listOf("Go", "Gd", "Au", "Ag"), correctAnswerIndex = 2, explanation = "Au comes from the Latin word 'aurum' meaning gold.", xpReward = 10),
        Challenge(id = 79, skillId = 4, difficulty = "Medium", question = "What is the speed of light in vacuum (km/s)?", options = listOf("~200,000", "~250,000", "~300,000", "~350,000"), correctAnswerIndex = 2, explanation = "The speed of light is exactly 299,792,458 m/s, approximately 300,000 km/s.", xpReward = 20),
        Challenge(id = 80, skillId = 4, difficulty = "Hard", question = "How many bones are in the adult human body?", options = listOf("186", "206", "226", "256"), correctAnswerIndex = 1, explanation = "The adult human skeleton consists of 206 bones, though at birth there are about 270.", xpReward = 30),
        Challenge(id = 81, skillId = 4, difficulty = "Easy", question = "What element has atomic number 1?", options = listOf("Helium", "Oxygen", "Hydrogen", "Carbon"), correctAnswerIndex = 2, explanation = "Hydrogen has atomic number 1 - it has just one proton and one electron.", xpReward = 10),
        Challenge(id = 82, skillId = 4, difficulty = "Medium", question = "Who co-founded Apple Inc. with Steve Jobs?", options = listOf("Bill Gates", "Steve Wozniak", "Mark Zuckerberg", "Larry Ellison"), correctAnswerIndex = 1, explanation = "Steve Wozniak co-founded Apple with Steve Jobs and Ronald Wayne in 1976.", xpReward = 20),
        Challenge(id = 83, skillId = 4, difficulty = "Easy", question = "What does 'HTTP' stand for?", options = listOf("Hyper Text Transfer Protocol", "High Transfer Text Protocol", "Hyper Text Transmission Protocol", "High Transmission Text Protocol"), correctAnswerIndex = 0, explanation = "HTTP stands for Hypertext Transfer Protocol, the foundation of data communication on the web.", xpReward = 10),
        Challenge(id = 84, skillId = 4, difficulty = "Hard", question = "What year was the first iPhone released?", options = listOf("2005", "2006", "2007", "2008"), correctAnswerIndex = 2, explanation = "The first iPhone was announced by Steve Jobs on January 9, 2007 and released on June 29, 2007.", xpReward = 30)
    )

    val achievements = listOf(
        Achievement(id = 1, name = "First Steps", description = "Complete your first challenge", icon = "👣", category = "Progress", requirementType = "challenges_completed", requirementValue = 1, xpReward = 25),
        Achievement(id = 2, name = "Getting Started", description = "Complete 5 challenges", icon = "🌱", category = "Progress", requirementType = "challenges_completed", requirementValue = 5, xpReward = 50),
        Achievement(id = 3, name = "Dedicated Learner", description = "Complete 25 challenges", icon = "📚", category = "Progress", requirementType = "challenges_completed", requirementValue = 25, xpReward = 200),
        Achievement(id = 4, name = "Challenge Master", description = "Complete 50 challenges", icon = "🏆", category = "Progress", requirementType = "challenges_completed", requirementValue = 50, xpReward = 500),
        Achievement(id = 5, name = "Perfect Score", description = "Get 10 correct answers in a row", icon = "💎", category = "Performance", requirementType = "perfect_streak", requirementValue = 10, xpReward = 150),
        Achievement(id = 6, name = "On Fire", description = "Maintain a 5-day login streak", icon = "🔥", category = "Streaks", requirementType = "daily_streak", requirementValue = 5, xpReward = 100),
        Achievement(id = 7, name = "Week Warrior", description = "Maintain a 7-day login streak", icon = "⚔️", category = "Streaks", requirementType = "daily_streak", requirementValue = 7, xpReward = 200),
        Achievement(id = 8, name = "Month Master", description = "Maintain a 30-day login streak", icon = "👑", category = "Streaks", requirementType = "daily_streak", requirementValue = 30, xpReward = 1000),
        Achievement(id = 9, name = "Game Night", description = "Play your first game", icon = "🎮", category = "Games", requirementType = "games_played", requirementValue = 1, xpReward = 25),
        Achievement(id = 10, name = "Game Expert", description = "Play 10 games", icon = "🕹️", category = "Games", requirementType = "games_played", requirementValue = 10, xpReward = 100),
        Achievement(id = 11, name = "High Scorer", description = "Score 500 points in a single game", icon = "⭐", category = "Games", requirementType = "game_score", requirementValue = 500, xpReward = 150),
        Achievement(id = 12, name = "Perfect Gamer", description = "Score 1000 points in a single game", icon = "🌟", category = "Games", requirementType = "game_score", requirementValue = 1000, xpReward = 300),
        Achievement(id = 13, name = "WM Trainer", description = "Complete 10 Working Memory challenges", icon = "🧠", category = "Domains", requirementType = "domain_completed", requirementValue = 10, xpReward = 200),
        Achievement(id = 14, name = "Executive Pro", description = "Complete 10 Executive Control challenges", icon = "⚙️", category = "Domains", requirementType = "domain_completed", requirementValue = 10, xpReward = 200),
        Achievement(id = 15, name = "Reasoning Ace", description = "Complete 10 Fluid Reasoning challenges", icon = "🔮", category = "Domains", requirementType = "domain_completed", requirementValue = 10, xpReward = 200),
        Achievement(id = 16, name = "Speed Demon", description = "Complete 10 Processing Speed challenges", icon = "⚡", category = "Domains", requirementType = "domain_completed", requirementValue = 10, xpReward = 200),
        Achievement(id = 17, name = "Focus Master", description = "Complete 10 Attentional Control challenges", icon = "🎯", category = "Domains", requirementType = "domain_completed", requirementValue = 10, xpReward = 200),
        Achievement(id = 18, name = "Medium Rare", description = "Complete 10 Medium challenges", icon = "🔶", category = "Difficulty", requirementType = "difficulty_completed", requirementValue = 10, xpReward = 200),
        Achievement(id = 19, name = "Hard Worker", description = "Complete 10 Hard challenges", icon = "💪", category = "Difficulty", requirementType = "difficulty_completed", requirementValue = 10, xpReward = 350),
        Achievement(id = 20, name = "Full Spectrum", description = "Complete at least one challenge from every cognitive domain", icon = "🌈", category = "Mastery", requirementType = "all_skills", requirementValue = 5, xpReward = 500)
    )
}
