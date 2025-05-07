import scala.io.Source
sealed trait MatchType
case object Contains extends MatchType
case object StartsWith extends MatchType

case class KeywordRule(keyword: String, matchType: MatchType)
case class CommandRule(priority: Int, command: String, patterns: List[String])

object Prase {
  def loadRules(filePath: String): List[CommandRule] = {
  Source.fromFile(filePath)
    .getLines()
    .drop(1)
    .map { line =>
      val cols = line.split(",", -1) // Keep empty values
      CommandRule(
        cols(0).toInt,
        cols(1),
        cols(2).replaceAll("\"", "").split(";").map(_.trim).toList
      )
    }.toList
    .sortBy(-_.priority) // Higher priority first
}
  private def cleanInput(input: String): String = {
  input.toLowerCase
    .replaceAll("""[^\w\s]""", "") // keep letters/numbers/spaces
    .replaceAll("\\s+", " ")       // normalize multiple spaces
    .trim
}

  // Extract meaningful keywords
  private def extractKeywords(cleanedInput: String): List[String] = {
    val stopWords = Set(
     // Basic question words
  "what", "which", "how", "can", "could", "would", 
  "is", "are", "was", "were", "does", "do", "did",
  "has", "have", "had", "will", "shall", "may",
  "might", "must", "should", "need", "want", "no",
  "what's", "whats", "items","item","preparing","needed","components","say","something",// Added from patterns

  // Request verbs
  "tell", "give", "show", "explain", "describe", "funny",
  "list", "provide", "share", "know", "find","walk",
  "define", // Added from dish_info pattern
  "try", "take","uses","grocery","whats", // Added from quiz pattern

  // Food-related verbs
  "make", "cook", "prepare", "create", "bake", 
  "fry", "grill", "boil", "steam", "roast",
  "save","add","like","require", "include", "use", "uses",
  "add", "contain", "contains", "made", "calls", "required", // Added from recipe pattern
   // Added from ingredients pattern

  // Prepositions/articles
  "about", "of", "for", "to", "from", "with", 
  "without", "in", "on", "at", "the", "a", "an", "it",
  "me", "as", "by", "through", // Added from patterns

  // General food terms
  "dish", "dishes", "food", "cuisine", "meal", "recipe",
  "ingredient", "step", "steps", "direction", "instructions", "instruction",
  "method", "way", // Added from patterns
  "calls", "required","ingredients","ingredient","buy" ,"prep",// Added from ingredients

  // Quiz/Test terms
  "quiz", "test", "question", "challenge", 
  "knowledge","preference","save","favorite", // Added from patterns

  // Trivia terms
  "trivia", "fact", "fun", "interesting", 
  "did", "history", "origin", "story", "background", // Added from patterns

  // Polite terms
  "please", "thanks", "thank", "hello", "hi", "hey",
  "greetings", "goodbye", "bye", // Added from patterns
  "welcome", "back", // Added from greet pattern

  // Response terms
  "sure", "ok", "okay", "yeah", "yep",
  "continue", "move", "on", "like", "love", 
  "ready", "begin", "start", // Added from log pattern

  // Pronouns/identifiers
  "i", "we", "my", "our", "you",

  // Time references
  "now", "later", "maybe", "next", "time",
  "today", "soon", "enough", "finished", "done",

  // Quantifiers
  "some", "any", "many", "few", "several", "all",

  // Connectors
  "and", "or", "but", "containing", "using", "contains",
  "not", "no", // Added from exit pattern

  // Special terms
  "best", "right", "good", "day", "up", "are",
  "exit", "quit", "see", "go", "want", "information","made","like best","like","favorite"

    )
    
    
    cleanedInput.split("\\s+")
      .map(Typos.handleTypos) // Handle typos
      .filterNot(stopWords.contains)
      .toList
  }


  // Match command based on loaded rules
  def parseInput(input: String, rules: List[CommandRule]): (String, List[String]) = {
    val clean = cleanInput(input)
      println(s"DEBUG: Cleaned input: '$clean'") // Debug 1
    val tokens = clean.split("\\s+").toList
    val corrrectedTokens = tokens.map { token =>Typos.handleTypos(token)}
    val cleaned= corrrectedTokens.mkString(" ")
    
      val sortedRules = rules.sortBy(_.priority)
      println(s"DEBUG: Sorted rules: ${sortedRules.map(_.command)}") // Debug 2
    
    val command = sortedRules.find { rule =>
    rule.patterns.exists { pattern =>
      if (pattern.startsWith("^")) {
        cleaned.startsWith(pattern.substring(1)) // exact startsWith match
      } else {
        cleaned.contains(pattern) // simple contains match
      }
    }
  }.map(_.command).getOrElse("unknown")

   println(s"DEBUG: Final command: $command") // Debug 4
 
    val keywords = extractKeywords(cleaned)
    (command, keywords)
  }
}

