import scala.io.Source
sealed trait MatchType
case object Contains extends MatchType
case object StartsWith extends MatchType

case class KeywordRule(keyword: String, matchType: MatchType)
case class CommandRule(priority: Int, command: String, patterns: List[String])


object Prase {
  // Load command rules from a CSV file
  // The first line of the file is expected to be a header
  
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
    
}
// clean input by removing special characters and extra spaces
// This function takes a string input and cleans it by removing special characters
  private def cleanInput(input: String): String = {
  input.toLowerCase
    .replaceAll("""[^\w\s']""", "") // keep apostrophes
    .replaceAll("\\s+", " ")
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
  "and", "or", "but", "containing", "using", "contains","laugh",
  "not", "no", // Added from exit pattern

  // Special terms
  "best", "right", "good", "day", "up", "are",
  "exit", "quit", "see", "go", "want", "information","made","like best","like","favorite",
    "prefer", "like", "love", "adore", "enjoy", "save", "add", "remember", 
  "choose", "select", "pick", "go for", "opt for", "fancy", "dig",
  
  // Nouns
  "preference", "favorite", "choice", "dish", "cuisine", "food", "meal",
  
  // Phrases
  "i'm", "i am", "i'd", "i would", "i usually", "i normally", "i generally",
  "i mostly", "i typically", "as my", "to my", "that i", "please", "could you",
  "can you", "would you", "make a note", "keep as", "store as", "mark as",
  "note as", "nothing beats", "crazy about", "obsessed with", "fond of",
  "partial to", "not a fan of", "don't care for", "avoid", "pass on", "skip",
  
  // Connectors
  "as", "to", "for", "about", "with", "of","new","this","that","these","those",

    )
    
    
    cleanedInput.split("\\s+")
      .map(Typos.handleTypos) // Handle typos
      .filterNot(stopWords.contains)
      .toList
  }

   // This function takes a string input and a list of command rules
  // It cleans the input, tokenizes it, and checks against the rules
  // It returns a tuple containing the command and a list of keywords
   def parseInput( rules: List[CommandRule])(input:String): (String, List[String]) = {
    val clean = cleanInput(input)
      //println(s"DEBUG: Cleaned input: '$clean'") // Debug 1
    val tokens = clean.split("\\s+").toList
    val corrrectedTokens = tokens.map { token =>Typos.handleTypos(token)}
    val cleaned= corrrectedTokens.mkString(" ")
    
      val sortedRules = rules.sortBy(_.priority)
    
    val command = sortedRules.find { rule =>
      //println(s"DEBUG: Checking rule '${rule.command}' with patterns: ${rule.patterns}")

      rule.patterns.exists { pattern =>
        if (pattern.startsWith("^")) {
          cleaned.startsWith(pattern.substring(1)) // exact startsWith match
        } else {
          cleaned.contains(pattern) // simple contains match
        }
      
    }
  }.map(_.command).getOrElse("unknown") 
    //println(s"DEBUG: Final command: $command") // Debug 4
 
    val keywords = extractKeywords(cleaned)
    (command, keywords)
  } 
}

 /*  def parseInput(input: String): (String, List[String]) = {
    val clean = input.toLowerCase.replaceAll("""[^\w\s]""", "").trim
    val tokens = clean.split("\\s+").toList
    val corrrectedTokens = tokens.map { token =>
      // Handle typos
      Typos.handleTypos(token)
    }
      val cleaned= corrrectedTokens.mkString(" ")
  
  
    val command = cleaned match {
      case msg if (
        msg.contains("cuisine") || msg.contains("category") ||
        msg.contains("dish") || msg.contains("dishes") ||
        msg.contains("food") || msg.contains("meal") ||
        msg.startsWith("show me") || msg.startsWith("give me") ||
        msg.startsWith("tell me") || msg.startsWith("list")
      ) => "Cusine"
    case msg if (
    // Direct ingredient questions
    ( msg.contains("ingredients") ||
      msg.startsWith("what do i need") ||
      msg.contains("items needed") ||
      msg.contains("components of") ||
      msg.contains("what does it take to make") ||
      msg.startsWith("what's in") ||
      msg.contains("what are the contents") ||
      msg.contains("what goes into") ||
      msg.contains("what is needed for") ||
      msg.contains("what are the items required") ||
      msg.contains("what do you need to make"))||
  
    // "What is X made of" variations
    (msg.startsWith("what is") && (
      msg.contains("made of") ||
      msg.contains("made with") ||
      msg.contains("made from") ||
      msg.contains("contains") ||
      msg.contains("comprises") ||
      msg.contains("consists of") ||
      msg.contains("include") ||
      msg.contains("involve") ||
      msg.contains("require")
    )) ||
    
    // Recipe requirements
    msg.contains("recipe calls for") ||
    msg.contains("recipe requires") ||
    msg.contains("recipe ingredients") ||
    msg.contains("ingredients list") ||
    msg.contains("list of ingredients") ||
    msg.contains("what goes into") ||
    msg.contains("what goes in") ||
    
    // Dish composition questions
    msg.contains("made up of") ||msg.contains("composition of") ||msg.contains("what's inside") ||
    msg.contains("whats in") || msg.contains("what is in") ||
    // Finding dishes by ingredient
    msg.contains("dishes with") ||msg.contains("meals with") ||msg.contains("recipes with") ||
    msg.contains("food with") ||msg.contains("meals using") ||msg.contains("dishes containing") ||
    msg.contains("what has") ||msg.contains("what can i make with") ||msg.contains("what uses") ||
    msg.contains("contains") ||
    // Shopping/preparation questions
    msg.contains("grocery list for") ||
    msg.contains("items to buy for") ||
    msg.contains("prep for") 
  ) => "ingredients"
  
  
    //recipe request
    case msg if (
    msg.startsWith("how ") && msg.contains("make") ||
    (msg.contains("recipe") || msg.contains("steps") || msg.contains("instructions") || 
     msg.contains("method") || msg.contains("way")) &&
    (msg.contains(" for ") || msg.contains(" of ") || msg.contains(" to ")) ||
    (msg.contains("make") || msg.contains("prepare") || msg.contains("cook") || 
     msg.contains("create") || msg.contains("do")) &&
    (msg.contains("dish") || msg.contains("food") || msg.contains("meal")) ||
    msg.contains("step by step") ||
    msg.startsWith("can you show") || msg.startsWith("can you explain") ||
    msg.startsWith("what's the best way to") || msg.startsWith("whats the best way to") || 
    msg.startsWith("what is the best way to") || msg.startsWith("what's the right way to") ||
    msg.startsWith("whats the right way to") || msg.startsWith("what is the right way to") ||
    msg.contains("walk me through") ||
    (msg.startsWith("i need to know") || msg.startsWith("i need to learn")) &&
    (msg.contains("to make") || msg.contains("to cook"))
  ) => "recipe"
  
  // Quiz/Test
  case msg if (
    msg.contains("quiz") ||
    ((msg.contains("test") || msg.contains("question") || msg.contains("challenge")) && 
     (msg.contains("me") || msg.contains("my knowledge"))) ||
    msg.startsWith("i want to try quiz") || msg.startsWith("i want to take quiz") ||
    msg.startsWith("can we do quiz") || msg.startsWith("can we do test")
  ) => "quiz"
  
  // Trivia/Facts
  case msg if(
    msg.contains("trivia") || msg.contains("fact") || msg.contains("interesting") || 
    msg.contains("fun") || msg.contains("did you know") ||
    msg.contains("history") || msg.contains("origin") || msg.contains("story") || 
    msg.contains("background") && msg.contains("of") ||
    (msg.startsWith("what's the") || msg.startsWith("what is the")) && 
    (msg.contains("story") || msg.contains("history")) ||
    (msg.contains("know") || msg.contains("share")) && 
    (msg.contains("about") && (msg.contains("cuisine") || msg.contains("dish")))
  ) => "trivia"
  
  // Dish Information
  case msg if (
    msg.startsWith("what is") ||
    msg.startsWith("tell me about") ||
    msg.contains("describe") || msg.contains("explain") || msg.contains("define") ||
    msg.startsWith("i want to know about") ||
    msg.contains("information") && (msg.contains("dish") || msg.contains("food"))
  ) => "dish_info"
  
  case msg if (
    msg.contains("preference") || msg.contains("favorite") ||
    msg.contains("like best") || msg.contains("save as favorite")|| msg.contains("add to favorites") ||msg.contains("save")||msg.contains("like")
  ) => "preference"
  
  // Exit
  case msg if (
    msg.contains("bye") || msg.contains("goodbye") || msg.contains("see you") || 
    msg.contains("quit") || msg.contains("exit") || msg.startsWith("later") || 
    msg.startsWith("maybe next time") ||
    (msg.startsWith("i'll go") || msg.startsWith("i will go") || msg.startsWith("i go")) ||
    msg.contains("enough") || msg.contains("done") || msg.contains("finished")
  ) => "bye"
  
  // Positive Response
  case msg if (
    msg.contains("yes") || msg.contains("yeah") || msg.contains("yep") || 
    msg.contains("sure") || msg.contains("ok") || msg.contains("okay") ||
    msg.contains("please") ||
    msg.contains("continue") || msg.contains("next") || msg.contains("move on") ||
    (msg.startsWith("i'd like") || msg.startsWith("i would like") || 
     msg.startsWith("i'd love") || msg.startsWith("i would love") ||
     msg.startsWith("i'd want") || msg.startsWith("i would want")) ||
    msg.contains("ready") || msg.contains("begin") || msg.contains("start")
  ) => "log"
  
  // Greetings
      case msg if (
        msg.contains("hello") || msg.contains("hi") || msg.contains("hey") || msg.contains("greetings") ||
          msg.contains("marhaba") || msg.contains("salam") || msg.contains("ciao")||
          msg.contains("bonjour") || msg.contains("hola") || msg.contains("namaste") || msg.contains("konnichiwa") || msg.contains("ola")
          
  ) => "greet"
  
  case msg if(
    msg.contains("how are you") || msg.contains("how are u") ||msg.contains("good wbu ")
  ||
    msg.contains("how are you doing") || msg.contains("how are u doing") ||
    msg.contains("how's it going") || msg.contains("how is it going") ||
    msg.contains("how's everything") || msg.contains("how is everything") ||
    msg.contains("how's life") || msg.contains("how is life") ||
    msg.contains("what's up") || msg.contains("whats up") ||
    msg.contains("what's new") || msg.contains("whats new")
  ) => "ask_how_user_is"
   case msg if (
    msg.contains("joke") || msg.contains("funny") || msg.contains("laugh") ||
    msg.contains("tell me a joke") || msg.contains("make me laugh") ||
    msg.contains("crack me up") || msg.contains("humor me") ||
    msg.contains("lighten the mood") || msg.contains("bring on the laughs") ||
    msg.contains("give me a laugh") 
   ) => "joke"
  case _ => "unknown"
    }
    val keywords = corrrectedTokens.filterNot(token =>
      Set(
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
     "and", "or", "but", "containing", "using", "contains","laugh",
     "not", "no", // Added from exit pattern
   
     // Special terms
     "best", "right", "good", "day", "up", "are",
     "exit", "quit", "see", "go", "want", "information","made","like best","like","favorite",
       "prefer", "like", "love", "adore", "enjoy", "save", "add", "remember", 
     "choose", "select", "pick", "go for", "opt for", "fancy", "dig",
     
     // Nouns
     "preference", "favorite", "choice", "dish", "cuisine", "food", "meal",
     
     // Phrases
     "i'm", "i am", "i'd", "i would", "i usually", "i normally", "i generally",
     "i mostly", "i typically", "as my", "to my", "that i", "please", "could you",
     "can you", "would you", "make a note", "keep as", "store as", "mark as",
     "note as", "nothing beats", "crazy about", "obsessed with", "fond of",
     "partial to", "not a fan of", "don't care for", "avoid", "pass on", "skip",
     
     // Connectors
     "as", "to", "for", "about", "with", "of","new","this","that","these","those",
   
      ).contains(token.toLowerCase)
    )
  
    // Identify command type based on keywords
  
    (command, keywords)
  } */
