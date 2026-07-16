package com.skillforge.app.data.local.seed

import androidx.sqlite.db.SupportSQLiteDatabase

object SeedData {

    fun seed(db: SupportSQLiteDatabase) {
        seedUser(db)
        seedSkills(db)
        seedChallenges(db)
        seedAchievements(db)
    }

    private fun seedUser(db: SupportSQLiteDatabase) {
        db.execSQL(
            "INSERT INTO user (id, name, totalXP, level, currentStreak, longestStreak, lastActiveDate, createdAt) " +
            "VALUES (1, 'Player', 0, 1, 0, 0, '', ${System.currentTimeMillis()})"
        )
    }

    private fun seedSkills(db: SupportSQLiteDatabase) {
        val skills = listOf(
            SkillData(1, "Logic", "Critical Thinking", "Formal and informal logic", "psychology", 0, 1, 50, 1, true, null),
            SkillData(2, "Problem Solving", "Critical Thinking", "Breaking down complex problems", "build", 0, 1, 50, 2, true, null),
            SkillData(3, "Analysis", "Critical Thinking", "Critical examination of information", "analytics", 0, 1, 50, 3, true, null),
            SkillData(4, "Decision Making", "Critical Thinking", "Effective choices under uncertainty", "gavel", 0, 1, 50, 4, true, null),
            SkillData(5, "Science", "General Knowledge", "Physics, chemistry, biology", "science", 0, 1, 50, 1, true, null),
            SkillData(6, "History", "General Knowledge", "World history and civilizations", "history_edu", 0, 1, 50, 2, true, null),
            SkillData(7, "Geography", "General Knowledge", "Countries, capitals, landmarks", "public", 0, 1, 50, 3, true, null),
            SkillData(8, "Culture", "General Knowledge", "Arts, music, literature", "palette", 0, 1, 50, 4, true, null),
            SkillData(9, "Memory", "Meta-Learning", "Techniques to improve recall", "memory", 0, 1, 50, 1, true, null),
            SkillData(10, "Study Techniques", "Meta-Learning", "Effective learning strategies", "school", 0, 1, 50, 2, true, null),
            SkillData(11, "Focus", "Meta-Learning", "Concentration and attention", "center_focus_strong", 0, 1, 50, 3, true, null),
            SkillData(12, "Time Management", "Meta-Learning", "Productivity and scheduling", "schedule", 0, 1, 50, 4, true, null),
            SkillData(13, "Empathy", "Social/Emotional", "Understanding others feelings", "favorite", 0, 1, 50, 1, true, null),
            SkillData(14, "Communication", "Social/Emotional", "Expressing ideas clearly", "chat", 0, 1, 50, 2, true, null),
            SkillData(15, "Leadership", "Social/Emotional", "Guiding and inspiring teams", "military_tech", 0, 1, 50, 3, true, null),
            SkillData(16, "Self-Awareness", "Social/Emotional", "Understanding yourself", "self_improvement", 0, 1, 50, 4, true, null)
        )

        skills.forEach { skill ->
            db.execSQL(
                "INSERT INTO skills (id, name, category, description, icon, currentXP, level, maxLevel, sortOrder, isUnlocked, prerequisiteSkillId) " +
                "VALUES (${skill.id}, '${skill.name}', '${skill.category}', '${skill.description}', '${skill.icon}', ${skill.currentXP}, ${skill.level}, ${skill.maxLevel}, ${skill.sortOrder}, ${if (skill.isUnlocked) 1 else 0}, ${skill.prerequisiteSkillId})"
            )
        }
    }

    private fun seedChallenges(db: SupportSQLiteDatabase) {
        val all = mutableListOf<ChallengeData>()
        var id = 1L

        // Logic
        all += ch(id++, 1, "Easy", "If all roses are flowers and some flowers fade quickly, can we conclude all roses fade quickly?", "Yes", "No", "Cannot be determined", "Only in spring", 1, "This is an invalid syllogism. The conclusion doesn't follow from the premises.", 10)
        all += ch(id++, 1, "Easy", "Which pattern completes: 2, 6, 12, 20, ?", "28", "30", "32", "25", 1, "Pattern: n(n+1). 5*6=30", 10)
        all += ch(id++, 1, "Easy", "If A > B and B > C, which is true?", "C > A", "A > C", "A = C", "Cannot determine", 1, "Transitive property of inequality.", 10)
        all += ch(id++, 1, "Easy", "All dogs are animals. Rex is a dog. Therefore:", "Rex might be an animal", "Rex is definitely an animal", "Rex is not an animal", "Cannot determine", 1, "Valid syllogism - Rex must be an animal.", 10)
        all += ch(id++, 1, "Easy", "Which is NOT a logical fallacy?", "Ad hominem", "Straw man", "Modus ponens", "Slippery slope", 2, "Modus ponens is a valid logical argument form, not a fallacy.", 10)
        all += ch(id++, 1, "Easy", "If it rains, the ground is wet. The ground is wet. It must have:", "Rained", "Not rained", "We cannot be certain it rained", "Been cleaned", 2, "Affirming the consequent - the ground could be wet for other reasons.", 10)
        all += ch(id++, 1, "Easy", "Which number is the odd one out: 2, 3, 5, 9, 11, 13?", "2", "3", "9", "13", 2, "9 is not prime; all others are prime numbers.", 10)
        all += ch(id++, 1, "Easy", "If no teachers are lazy and some lazy people are musicians, can we conclude anything about teachers and musicians?", "Some teachers are musicians", "No teachers are musicians", "All teachers are musicians", "Nothing can be concluded", 3, "No valid logical connection between the premises.", 10)
        all += ch(id++, 1, "Hard", "In propositional logic, what does P -> Q evaluate to when P is false?", "True", "False", "Undefined", "Depends on Q", 0, "A conditional with a false antecedent is vacuously true.", 20)
        all += ch(id++, 1, "Hard", "The Monty Hall Problem: after switching, what is the probability of winning?", "1/3", "1/2", "2/3", "3/4", 2, "Switching gives 2/3 chance; staying gives 1/3.", 20)

        // Problem Solving
        all += ch(id++, 2, "Medium", "A bat and ball cost $1.10 together. The bat costs $1 more than the ball. How much does the ball cost?", "$0.10", "$0.05", "$0.15", "$0.20", 1, "If ball = $0.05, bat = $1.05, total = $1.10. Common mistake is $0.10.", 15)
        all += ch(id++, 2, "Medium", "You have 8 identical balls. One is heavier. Using a balance scale, what is the minimum weighings needed?", "1", "2", "3", "4", 1, "Divide into 3 groups of 3, 3, 2. Two weighings can identify the heavy ball.", 15)
        all += ch(id++, 2, "Medium", "Three switches control three bulbs in another room. You can check once. How do you determine which switch controls which?", "Turn on all, wait, turn off one, check", "Turn on one, wait, turn off, turn on another, check", "Turn on switches one at a time", "Impossible with one check", 1, "Use heat: on for 10min, off, check hot+on, warm+off, cold+off.", 15)
        all += ch(id++, 2, "Medium", "A farmer needs to cross a river with a fox, chicken, and grain. Fox eats chicken, chicken eats grain. How many crossings minimum?", "5", "7", "9", "11", 1, "The classic river crossing puzzle requires 7 one-way crossings.", 15)
        all += ch(id++, 2, "Medium", "If you can only use addition, how many times must you add 6 to get 30?", "4", "5", "6", "30", 1, "6+6+6+6+6 = 30, so 5 times.", 15)
        all += ch(id++, 2, "Medium", "You have two ropes. Each takes exactly 1 hour to burn but burns unevenly. How do you measure 45 minutes?", "Light both ends of first, one end of second", "Light one end of each", "Cut one in half", "Impossible", 0, "Light rope 1 from both ends (30 min) and rope 2 from one end. When rope 1 finishes, light rope 2's other end (15 more min).", 15)
        all += ch(id++, 2, "Medium", "A clock shows 3:15. What is the angle between hour and minute hands?", "0 degrees", "7.5 degrees", "15 degrees", "22.5 degrees", 1, "At 3:15, minute hand is at 90 deg, hour hand at 97.5 deg. Difference = 7.5 deg.", 15)
        all += ch(id++, 2, "Hard", "The base rate fallacy is:", "Ignoring starting conditions", "Ignoring general statistics in favor of specific information", "Using too many variables", "Calculating incorrectly", 1, "Base rate fallacy occurs when people ignore general probability.", 20)
        all += ch(id++, 2, "Hard", "Kahneman System 1 thinking is:", "Slow and deliberate", "Fast and automatic", "Mathematical", "Creative only", 1, "System 1 is fast, automatic, and intuitive thinking.", 20)

        // Science
        all += ch(id++, 5, "Easy", "What is the powerhouse of the cell?", "Nucleus", "Mitochondria", "Ribosome", "Golgi apparatus", 1, "Mitochondria produce ATP through cellular respiration.", 10)
        all += ch(id++, 5, "Easy", "What planet is known as the Red Planet?", "Venus", "Mars", "Jupiter", "Saturn", 1, "Mars appears red due to iron oxide on its surface.", 10)
        all += ch(id++, 5, "Easy", "What is the speed of light approximately?", "300,000 km/s", "150,000 km/s", "500,000 km/s", "100,000 km/s", 0, "Light travels at approximately 299,792 km/s.", 10)
        all += ch(id++, 5, "Easy", "What gas do plants absorb from the atmosphere?", "Oxygen", "Nitrogen", "Carbon Dioxide", "Hydrogen", 2, "Plants use CO2 in photosynthesis.", 10)
        all += ch(id++, 5, "Easy", "What is the chemical symbol for gold?", "Go", "Gd", "Au", "Ag", 2, "Au comes from the Latin aurum.", 10)
        all += ch(id++, 5, "Easy", "How many bones does an adult human have?", "186", "206", "226", "196", 1, "Adults have 206 bones; babies have about 270.", 10)
        all += ch(id++, 5, "Easy", "What is the hardest natural substance on Earth?", "Gold", "Iron", "Diamond", "Quartz", 2, "Diamond scores 10 on the Mohs hardness scale.", 10)
        all += ch(id++, 5, "Easy", "What is the closest star to Earth?", "Proxima Centauri", "Sirius", "The Sun", "Alpha Centauri", 2, "The Sun is about 150 million km away, the closest star.", 10)
        all += ch(id++, 5, "Easy", "What is the boiling point of water at sea level in Celsius?", "90C", "100C", "110C", "120C", 1, "Water boils at exactly 100C at standard atmospheric pressure.", 10)
        all += ch(id++, 5, "Easy", "What type of rock is formed by volcanic activity?", "Sedimentary", "Metamorphic", "Igneous", "Limestone", 2, "Igneous rocks form from cooled magma or lava.", 10)

        // History
        all += ch(id++, 6, "Easy", "In which year did World War II end?", "1943", "1944", "1945", "1946", 2, "WWII ended in 1945 with the surrender of Japan.", 10)
        all += ch(id++, 6, "Easy", "Who was the first President of the United States?", "Thomas Jefferson", "John Adams", "George Washington", "Benjamin Franklin", 2, "George Washington served as the first president.", 10)
        all += ch(id++, 6, "Easy", "The Renaissance began in which country?", "France", "Germany", "Italy", "Spain", 2, "The Renaissance originated in Italy in the 14th century.", 10)
        all += ch(id++, 6, "Easy", "Which ancient civilization built the pyramids of Giza?", "Roman", "Greek", "Egyptian", "Persian", 2, "The Great Pyramid was built around 2560 BC.", 10)
        all += ch(id++, 6, "Easy", "What was the Magna Carta?", "A type of weapon", "A charter of rights (1215)", "A Roman road", "A type of ship", 1, "The Magna Carta limited the power of the English king.", 10)
        all += ch(id++, 6, "Easy", "Who painted the Mona Lisa?", "Michelangelo", "Raphael", "Leonardo da Vinci", "Donatello", 2, "Leonardo da Vinci painted it between 1503 and 1519.", 10)
        all += ch(id++, 6, "Easy", "The French Revolution began in which year?", "1776", "1789", "1799", "1804", 1, "The French Revolution started in 1789.", 10)
        all += ch(id++, 6, "Easy", "Which empire was the largest contiguous land empire in history?", "Roman Empire", "Mongol Empire", "British Empire", "Ottoman Empire", 1, "The Mongol Empire covered about 24 million km2.", 10)

        // Geography
        all += ch(id++, 7, "Easy", "What is the largest continent by area?", "Africa", "North America", "Asia", "Europe", 2, "Asia covers about 44.58 million km2.", 10)
        all += ch(id++, 7, "Easy", "What is the longest river in the world?", "Amazon", "Nile", "Mississippi", "Yangtze", 1, "The Nile is approximately 6,650 km long.", 10)
        all += ch(id++, 7, "Easy", "Which country has the most population?", "India", "USA", "China", "Indonesia", 2, "China has had the largest population for decades.", 10)
        all += ch(id++, 7, "Easy", "What is the capital of Australia?", "Sydney", "Melbourne", "Canberra", "Brisbane", 2, "Canberra is the capital, not the largest city.", 10)
        all += ch(id++, 7, "Easy", "Mount Everest is on the border of which two countries?", "India and China", "Nepal and China", "Nepal and India", "China and Pakistan", 1, "Everest sits on the Nepal-Tibet border.", 10)
        all += ch(id++, 7, "Easy", "What is the smallest country in the world?", "Monaco", "Vatican City", "San Marino", "Liechtenstein", 1, "Vatican City is only about 0.44 km2.", 10)

        // Culture
        all += ch(id++, 8, "Easy", "Who wrote Romeo and Juliet?", "Charles Dickens", "William Shakespeare", "Jane Austen", "Mark Twain", 1, "Shakespeare wrote it around 1594-1596.", 10)
        all += ch(id++, 8, "Easy", "What instrument has 88 keys?", "Guitar", "Violin", "Piano", "Flute", 2, "A standard piano has 88 keys.", 10)
        all += ch(id++, 8, "Easy", "Which art movement did Picasso help found?", "Impressionism", "Cubism", "Surrealism", "Pop Art", 1, "Picasso co-founded Cubism with Georges Braque.", 10)
        all += ch(id++, 8, "Easy", "What is haiku?", "A type of dance", "A short Japanese poem of 17 syllables", "A martial art", "A food dish", 1, "Haiku follows 5-7-5 syllable pattern.", 10)
        all += ch(id++, 8, "Easy", "Which country originated sushi?", "China", "Korea", "Japan", "Thailand", 2, "Sushi originated in Japan.", 10)
        all += ch(id++, 8, "Easy", "Who composed The Four Seasons?", "Bach", "Mozart", "Vivaldi", "Beethoven", 2, "Antonio Vivaldi composed it around 1720.", 10)

        // Memory
        all += ch(id++, 9, "Medium", "What is the spacing effect in learning?", "Studying while walking", "Distributing study over time", "Studying in groups", "Using colored notes", 1, "The spacing effect shows distributed practice is more effective.", 15)
        all += ch(id++, 9, "Medium", "The method of loci is also known as:", "The forget method", "Memory palace", "The block method", "Pattern linking", 1, "Method of loci uses spatial memory by associating items with locations.", 15)
        all += ch(id++, 9, "Medium", "How many items can most people hold in working memory?", "3-4", "5-9", "10-15", "20+", 1, "Miller's Law suggests 7 plus or minus 2 items.", 15)
        all += ch(id++, 9, "Medium", "What is chunking in memory?", "Breaking information into groups", "Memorizing chunks of food", "Forgetting small details", "Speed reading", 0, "Chunking groups individual pieces into larger, meaningful units.", 15)
        all += ch(id++, 9, "Medium", "Which activity best improves long-term memory retention?", "Re-reading notes", "Self-testing", "Highlighting", "Listening to lectures", 1, "Active recall through self-testing strengthens memory.", 15)
        all += ch(id++, 9, "Medium", "The forgetting curve was discovered by:", "Freud", "Ebbinghaus", "Skinner", "Piaget", 1, "Hermann Ebbinghaus mapped how memory fades over time.", 15)

        // Study Techniques
        all += ch(id++, 10, "Medium", "What is the Pomodoro Technique?", "Eating tomatoes while studying", "Working in 25-min focused intervals", "Studying only in the morning", "Using red pens", 1, "Pomodoro uses 25-minute focus sessions with 5-minute breaks.", 15)
        all += ch(id++, 10, "Medium", "What is active recall?", "Calling friends to study", "Actively retrieving information from memory", "Reading textbooks aloud", "Using highlighters", 1, "Active recall means testing yourself rather than passively reviewing.", 15)
        all += ch(id++, 10, "Medium", "The Feynman Technique involves:", "Learning physics only", "Explaining concepts in simple terms", "Memorizing formulas", "Teaching only advanced topics", 1, "Feynman Technique: explain simply, identify gaps, review, simplify.", 15)
        all += ch(id++, 10, "Medium", "Which is most effective for long-term learning?", "Cramming before tests", "Distributed practice", "Copying notes word for word", "Reading in bed", 1, "Distributed practice is consistently more effective.", 15)
        all += ch(id++, 10, "Medium", "What is interleaving in study?", "Taking breaks between sessions", "Mixing different topics during study", "Studying with others", "Using colored pens", 1, "Interleaving involves practicing different skills in the same session.", 15)
        all += ch(id++, 10, "Medium", "What does metacognition mean?", "Thinking about thinking", "Studying at night", "Memorizing facts", "Multi-tasking", 0, "Metacognition is awareness of your own thought processes.", 15)

        // Focus
        all += ch(id++, 11, "Medium", "What is deep work?", "Working underground", "Focused, distraction-free cognitive effort", "Working late nights", "Heavy mental load", 1, "Coined by Cal Newport, deep work is focused work without distractions.", 15)
        all += ch(id++, 11, "Medium", "How long is a typical attention span for focused tasks?", "2-3 hours", "25-45 minutes", "5-10 minutes", "1-2 hours", 1, "Focused attention naturally wanes after about 25-45 minutes.", 15)
        all += ch(id++, 11, "Medium", "What is attention residue?", "Distracting residue on desk", "Mental load from previous tasks affecting current focus", "Leftover thoughts from dreams", "Brain fatigue", 1, "Switching tasks leaves residue that impairs focus.", 15)
        all += ch(id++, 11, "Medium", "To improve focus, you should:", "Multitask frequently", "Eliminate or minimize distractions proactively", "Work in noisy environments", "Check phone every 10 minutes", 1, "Environmental design is the most effective way to sustain focus.", 15)
        all += ch(id++, 11, "Medium", "The flow state requires:", "Total boredom", "Challenge matched to skill level", "Complete rest", "External pressure", 1, "Flow occurs when challenge level matches your skill level.", 15)

        // Time Management
        all += ch(id++, 12, "Medium", "What is the Eisenhower Matrix?", "A math formula", "Urgent/important prioritization grid", "A scheduling app", "A type of calendar", 1, "The Eisenhower Matrix categorizes tasks by urgency and importance.", 15)
        all += ch(id++, 12, "Medium", "Time blocking means:", "Blocking distracting websites", "Scheduling specific tasks into calendar slots", "Working non-stop", "Ignoring deadlines", 1, "Time blocking dedicates specific time periods to specific tasks.", 15)
        all += ch(id++, 12, "Medium", "Parkinsons law states:", "Work expands to fill available time", "Parks need more time", "Law about parking", "Tasks always take less time", 0, "Work expands to fill the time available for completion.", 15)
        all += ch(id++, 12, "Medium", "The 2-minute rule from GTD means:", "Never work for 2 minutes", "If it takes less than 2 minutes, do it now", "Take 2-minute breaks", "Set 2-minute timers", 1, "The 2-minute rule helps prevent small tasks from accumulating.", 15)
        all += ch(id++, 12, "Medium", "Batch processing in time management means:", "Doing everything at once", "Grouping similar tasks together for efficiency", "Working in batches of 10", "Processing food", 1, "Batching similar tasks reduces context-switching overhead.", 15)
        all += ch(id++, 12, "Medium", "The Pareto Principle in productivity means:", "80% of results come from 20% of efforts", "Work 80 hours per week", "Spend 80% of time planning", "Use 80% effort", 0, "The 80/20 rule suggests focusing on the vital few tasks.", 15)

        // Empathy
        all += ch(id++, 13, "Easy", "A friend cancels plans last minute. What is the most empathetic first response?", "That is so inconsiderate", "I hope everything is okay", "You always do this", "Fine, whatever", 1, "Empathetic responses assume positive intent and show concern.", 10)
        all += ch(id++, 13, "Easy", "What is cognitive empathy?", "Understanding others emotions logically", "Feeling others pain physically", "Being emotional in debates", "Ignoring others feelings", 0, "Cognitive empathy is understanding another's perspective.", 10)
        all += ch(id++, 13, "Easy", "Active listening involves:", "Thinking about your response", "Maintaining eye contact and paraphrasing", "Checking your phone", "Giving immediate advice", 1, "Active listening shows engagement through non-verbal cues.", 10)
        all += ch(id++, 13, "Easy", "What is emotional contagion?", "Getting sick from others", "Catching emotions from people around you", "Spreading rumors", "Being emotional at work", 1, "Emotional contagion is catching others emotions through mirror neurons.", 10)
        all += ch(id++, 13, "Easy", "When someone shares a problem, the best initial response is:", "Offer solutions immediately", "Listen and validate their feelings", "Change the subject", "Tell them about your similar experience", 1, "People usually need to feel heard before they're ready for solutions.", 10)
        all += ch(id++, 13, "Easy", "What does perspective-taking help with?", "Better grades", "Understanding others viewpoints", "Physical strength", "Mathematical ability", 1, "Perspective-taking is seeing situations from another's point of view.", 10)

        // Communication
        all += ch(id++, 14, "Medium", "I statements in communication sound like:", "You always do this", "I feel frustrated when...", "Nobody cares", "Whatever", 1, "I-statements express your feelings without blaming others.", 15)
        all += ch(id++, 14, "Medium", "What is non-violent communication?", "Avoiding physical violence", "A method of expressing needs without blame", "Silent treatment", "Passive-aggressive behavior", 1, "NVC involves observations, feelings, needs, and requests.", 15)
        all += ch(id++, 14, "Medium", "Mirroring in conversation means:", "Arguing back", "Reflecting what someone said", "Looking in a mirror", "Being sarcastic", 1, "Mirroring involves repeating or paraphrasing to show understanding.", 15)
        all += ch(id++, 14, "Medium", "What is the most common barrier to effective communication?", "Too much talking", "Not listening actively", "Speaking too quietly", "Using big words", 1, "Poor listening is the biggest communication barrier.", 15)
        all += ch(id++, 14, "Medium", "Assertive communication is:", "Being aggressive", "Being passive", "Expressing needs respectfully while considering others", "Not speaking up", 2, "Assertive communication balances self-expression with respect.", 15)
        all += ch(id++, 14, "Medium", "Reading between the lines means:", "Reading slowly", "Understanding implied meaning", "Skimming text", "Writing summaries", 1, "It means interpreting the unstated or implied meaning.", 15)

        // Analysis
        all += ch(id++, 3, "Medium", "What is confirmation bias?", "Confirming appointments", "Favoring information that supports existing beliefs", "Being indecisive", "Checking facts twice", 1, "Confirmation bias is seeking information that confirms pre-existing beliefs.", 15)
        all += ch(id++, 3, "Medium", "In a logical argument, a premise is:", "The conclusion", "Supporting evidence or assumption", "A counter-argument", "An emotional appeal", 1, "Premises are the foundational statements that support the conclusion.", 15)
        all += ch(id++, 3, "Medium", "What makes a correlation NOT imply causation?", "Small sample size", "A third variable may explain both", "Its peer-reviewed", "Statistics are involved", 1, "Correlation can exist without causation due to confounding variables.", 15)
        all += ch(id++, 3, "Medium", "What is a straw man argument?", "A weak argument", "Misrepresenting someones position to attack it", "Building a case slowly", "An argument with no evidence", 1, "Straw man involves distorting an opponents argument.", 15)
        all += ch(id++, 3, "Medium", "Anecdotal evidence is:", "Very reliable", "Based on personal stories, not systematic data", "Always wrong", "The strongest type of evidence", 1, "Anecdotal evidence is based on individual stories.", 15)
        all += ch(id++, 3, "Medium", "What is Occams Razor?", "A cutting tool", "The simplest explanation is usually best", "Always choose complex answers", "A type of logical fallacy", 1, "Occams Razor suggests preferring the explanation with fewer assumptions.", 15)

        // Decision Making
        all += ch(id++, 4, "Hard", "What is sunk cost fallacy?", "Investing wisely", "Continuing something because of past investment, not future value", "Losing money", "Budgeting poorly", 1, "Sunk cost fallacy ignores that past costs are irrecoverable.", 20)
        all += ch(id++, 4, "Hard", "The 10-10-10 rule means considering impact in:", "10 seconds, 10 minutes, 10 hours", "10 days, 10 months, 10 years", "10 minutes, 10 days, 10 months", "10 people, 10 groups, 10 societies", 2, "10-10-10 considers 10 minutes, 10 days, and 10 months from now.", 20)
        all += ch(id++, 4, "Hard", "What is analysis paralysis?", "Being good at analysis", "Overthinking to the point of no decision", "A medical condition", "Paralysis from studying", 1, "Analysis paralysis occurs when overthinking prevents making a decision.", 20)
        all += ch(id++, 4, "Hard", "A decision matrix helps by:", "Making decisions faster", "Systematically evaluating options against criteria", "Eliminating all risk", "Avoiding decisions", 1, "Decision matrices score options against weighted criteria.", 20)
        all += ch(id++, 4, "Hard", "Second-order thinking means:", "Thinking twice about everything", "Considering consequences of consequences", "Only thinking about obvious outcomes", "Asking a second opinion", 1, "Second-order thinking asks and then what to anticipate cascading effects.", 20)

        // Leadership
        all += ch(id++, 15, "Medium", "What is servant leadership?", "Leading from behind", "Putting team members needs first", "Bossing people around", "Leading by title only", 1, "Servant leadership prioritizes the growth and well-being of team members.", 15)
        all += ch(id++, 15, "Medium", "A good leader handles failure by:", "Blaming the team", "Taking responsibility and learning", "Pretending it didn't happen", "Quitting", 1, "Accountable leaders own failures and extract lessons.", 15)
        all += ch(id++, 15, "Medium", "What is emotional intelligence in leadership?", "Being emotional at work", "Managing your own and others emotions effectively", "Ignoring emotions", "Being overly sensitive", 1, "EI involves self-awareness, self-regulation, empathy, and social skills.", 15)
        all += ch(id++, 15, "Medium", "Delegation is important because:", "Leaders are lazy", "It develops team skills and multiplies capacity", "It reduces leaders workload permanently", "Teams prefer being told what to do", 1, "Effective delegation builds team capability while scaling impact.", 15)
        all += ch(id++, 15, "Medium", "What is transformational leadership?", "Changing the office layout", "Inspiring and motivating followers to achieve beyond expectations", "Making major organizational changes", "Leadership through fear", 1, "Transformational leaders inspire through vision and personal growth.", 15)

        // Self-Awareness
        all += ch(id++, 16, "Easy", "What is emotional regulation?", "Suppressing all emotions", "Managing emotional responses effectively", "Being emotionless", "Crying at work", 1, "Emotional regulation means managing (not suppressing) your responses.", 10)
        all += ch(id++, 16, "Easy", "A growth mindset believes:", "Intelligence is fixed", "Abilities can be developed through effort", "Only talent matters", "Failure means you should quit", 1, "Growth mindset sees abilities as developable through dedication.", 10)
        all += ch(id++, 16, "Easy", "What is self-efficacy?", "Being efficient alone", "Belief in your ability to succeed in specific situations", "Selfishness", "Working solo", 1, "Self-efficacy is your confidence in your ability to accomplish tasks.", 10)
        all += ch(id++, 16, "Easy", "Mindfulness helps with self-awareness by:", "Making you sleepy", "Helping you observe thoughts and feelings without judgment", "Increasing anxiety", "Blocking emotions", 1, "Mindfulness cultivates non-judgmental awareness.", 10)
        all += ch(id++, 16, "Easy", "What is triggers awareness?", "Knowing gun safety", "Recognizing what situations provoke strong reactions", "Setting off alarms", "Being trigger-happy", 1, "Trigger awareness means understanding what causes emotional reactions.", 10)

        all.forEach { c ->
            val opts = "[\"${c.o1}\",\"${c.o2}\",\"${c.o3}\",\"${c.o4}\"]"
            db.execSQL(
                "INSERT INTO challenges (id, skillId, difficulty, type, question, options, correctAnswerIndex, explanation, xpReward) " +
                "VALUES (${c.id}, ${c.skillId}, '${c.diff}', 'multiple_choice', '${c.q.replace("'", "''")}', '${opts.replace("'", "''")}', ${c.correct}, '${c.expl.replace("'", "''")}', ${c.xp})"
            )
        }
    }

    private fun ch(id: Long, skillId: Long, diff: String, q: String, o1: String, o2: String, o3: String, o4: String, correct: Int, expl: String, xp: Int) =
        ChallengeData(id, skillId, diff, q, o1, o2, o3, o4, correct, expl, xp)

    private fun seedAchievements(db: SupportSQLiteDatabase) {
        val achievements = listOf(
            AchievementData(1, "First Steps", "Complete your first challenge", "emoji_events", "progress", "challenges_completed", 1, 10),
            AchievementData(2, "Getting Started", "Complete 10 challenges", "emoji_events", "progress", "challenges_completed", 10, 25),
            AchievementData(3, "Dedicated Learner", "Complete 50 challenges", "emoji_events", "progress", "challenges_completed", 50, 50),
            AchievementData(4, "Scholar", "Complete 100 challenges", "emoji_events", "progress", "challenges_completed", 100, 100),
            AchievementData(5, "Perfect Score", "Get 10 correct in a row", "star", "streak", "perfect_streak", 10, 30),
            AchievementData(6, "On Fire", "Maintain a 7-day streak", "local_fire_department", "streak", "daily_streak", 7, 50),
            AchievementData(7, "Unstoppable", "Maintain a 30-day streak", "whatshot", "streak", "daily_streak", 30, 200),
            AchievementData(8, "Critical Thinker", "Reach level 5 in any CT skill", "psychology", "skill", "skill_level", 5, 75),
            AchievementData(9, "Knowledge Seeker", "Reach level 5 in any GK skill", "school", "skill", "skill_level", 5, 75),
            AchievementData(10, "Mind Master", "Reach level 5 in any ML skill", "brain", "skill", "skill_level", 5, 75),
            AchievementData(11, "Emotionally Intelligent", "Reach level 5 in any SE skill", "favorite", "skill", "skill_level", 5, 75),
            AchievementData(12, "Speed Demon", "Complete a challenge in under 10 seconds", "speed", "speed", "fast_completion", 10, 20),
            AchievementData(13, "Well Rounded", "Complete challenges in all 4 categories", "balance", "variety", "categories_covered", 4, 100),
            AchievementData(14, "Level 10", "Reach overall level 10", "trending_up", "level", "overall_level", 10, 150),
            AchievementData(15, "Level 25", "Reach overall level 25", "rocket_launch", "level", "overall_level", 25, 300),
            AchievementData(16, "Daily Devotee", "Complete daily challenges for 3 days", "event", "daily", "daily_completed", 3, 30),
            AchievementData(17, "Daily Master", "Complete daily challenges for 7 days", "event_available", "daily", "daily_completed", 7, 75),
            AchievementData(18, "Century Club", "Earn 1000 total XP", "diamond", "xp", "total_xp", 1000, 50),
            AchievementData(19, "XP Hunter", "Earn 5000 total XP", "military_tech", "xp", "total_xp", 5000, 150),
            AchievementData(20, "XP Legend", "Earn 10000 total XP", "workspace_premium", "xp", "total_xp", 10000, 500),
            AchievementData(21, "Game Night", "Play your first mini game", "videogame_asset", "games", "games_played", 1, 15),
            AchievementData(22, "Gamer", "Play 10 mini games", "sports_esports", "games", "games_played", 10, 40),
            AchievementData(23, "Arcade Master", "Play 50 mini games", "emoji_events", "games", "games_played", 50, 100),
            AchievementData(24, "Code Cracker", "Win Code Breaker on Hard", "lock", "games", "code_breaker_hard", 1, 50),
            AchievementData(25, "Speed Demon Gamer", "Score 200+ in Speed Round", "bolt", "games", "speed_round_high", 200, 50),
            AchievementData(26, "Pattern Genius", "Complete all Pattern Puzzle levels", "psychology", "games", "pattern_complete", 12, 40),
            AchievementData(27, "Memory Champion", "Win Memory Match in under 30 moves", "grid_on", "games", "memory_fast", 30, 40),
            AchievementData(28, "Simon Master", "Reach round 10 in Simon Says", "videogame_asset", "games", "simon_round", 10, 45),
            AchievementData(29, "Math Wizard", "Score 150+ in Math Duel", "calculate", "games", "math_high", 150, 45),
            AchievementData(30, "Word Smith", "Unscramble 10 words in Word Scramble", "text_fields", "games", "words_ten", 10, 35)
        )

        achievements.forEach { a ->
            db.execSQL(
                "INSERT INTO achievements (id, name, description, icon, category, requirementType, requirementValue, xpReward) " +
                "VALUES (${a.id}, '${a.name}', '${a.description}', '${a.icon}', '${a.category}', '${a.requirementType}', ${a.requirementValue}, ${a.xpReward})"
            )
        }
    }

    data class SkillData(
        val id: Long, val name: String, val category: String,
        val description: String, val icon: String,
        val currentXP: Long, val level: Int, val maxLevel: Int,
        val sortOrder: Int, val isUnlocked: Boolean, val prerequisiteSkillId: Long?
    )

    data class ChallengeData(
        val id: Long, val skillId: Long, val diff: String,
        val q: String, val o1: String, val o2: String, val o3: String, val o4: String,
        val correct: Int, val expl: String, val xp: Int
    )

    data class AchievementData(
        val id: Long, val name: String, val description: String,
        val icon: String, val category: String,
        val requirementType: String, val requirementValue: Int,
        val xpReward: Int
    )
}
