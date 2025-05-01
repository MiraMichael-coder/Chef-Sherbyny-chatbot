import java.time.LocalTime
import scala.io.StdIn.readLine
import scala.util.Random
import scala.annotation.tailrec

object UserState {
  private var userName: String = "friend"

  def setName(name: String): Unit = {
    userName = name
    println(s"Got it! I'll call you $name from now on.")
  }

  def getName: String = userName
}

object Personality {
  private val greetings = List(
    "Hey there!",
    "Hello! Ready to chat about food?",
    "Salam! What dish are you craving today?",
    "Welcome back, hungry friend! 🍽️"
  )

  private val jokes = List(
    "Why did the tomato turn red? Because it saw the salad dressing! 🍅",
    "What do you call cheese that isn’t yours? Nacho cheese! 🧀",
    "Why don’t eggs tell each other secrets? Because they might crack up! 🥚",
    "I'm on a seafood diet. I see food and I eat it. 😆",
    "ليش الفول بيزعل؟ عشان محدش بيقوله بحبك من غير عيش 🤣",
    "ليه الجبنة ما بتلعبش كورة؟ عشان دايمًا بتقع في الشباك! 🧀⚽",
  )

  def respondToGreeting(): Unit = {
    println(greetings(scala.util.Random.nextInt(greetings.length)))
  }

  def tellJoke(): Unit = {
    println(jokes(scala.util.Random.nextInt(jokes.length)))
  }


  def askHowUserIs(): Unit = {
    println(s"I’m just about keeping it together 😂 but actually doing great!")
  }
}

object Main  {

  implicit val parseForAnalytics: String => (String, List[String]) = parseInput

  def greetUser(): String = {
  val currentHour = LocalTime.now.getHour
  val timeGreeting = currentHour match {
    case h if h >= 5 && h < 12 => "🌞 Good morning"
    case h if h >= 12 && h < 17 => "🌤️ Good afternoon"
    case _ => "🌙 Good evening"
  }

  val availableCuisines = FoodDB.categories.map(_.name.capitalize).mkString(", ")
  val chefHat = "👨‍🍳"
  val sparkles = "🌟"
  val fire = "🔥"
  val wave = "👋"

  s"""$timeGreeting, foodie! $wave I'm *Chef Sherbyny* the Chatbot $chefHat
  
     |Welcome to your personal kitchen assistant! $chefHat
     |I can whip up facts, tips, and fun about these cuisines: $availableCuisines
     |
     |Ask me about a dish, explore ingredients, or type 'quiz' to test your food knowledge! $fire""".stripMargin
}
  def showDishes(category: String): Unit = {
    val dishes = FoodDB.getDishesByCategory(category)
    if (dishes.isEmpty) {
      println(s"No dishes found for $category cuisine")
    } else {
      println(s"\n=== ${category.capitalize} Dishes ===")
      dishes.foreach { dish =>
        val vegStatus = if (dish.isVegetarian) "Vegetarian" else "Non-vegetarian"
        println(s"◎ ${dish.name} ($vegStatus)")
        println(s"   Ingredients: ${dish.ingredients.mkString(", ")}")
      }
    }
  }
  // taking input and checking for typos
  // and returning a valid command type and tokens
  
  def parseInput(input: String): (String, List[String]) = {
  val cleaned = input.toLowerCase.replaceAll("""[^\w\s]""", "").trim
  val tokens = cleaned.split("\\s+").toList
  val corrrectedTokens = tokens.map { token =>
    // Handle typos
    Typos.handleTypos(token)
  }
  //Analytics.logInteraction(corrrectedTokens.mkString(" ")) // Log the interaction 

  val command = cleaned match {
    case msg if (
    (msg.startsWith("what are the ingredients") ||
    msg.startsWith("what ingredients are in") ||
    msg.startsWith("what is") && (
      msg.contains("made of") ||
      msg.contains("contains") ) ||
    msg.startsWith("what do i need for") ||
    msg.contains("recipe calls for")
  )) => "ingredients"

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
case msg if (
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

// Exit
case msg if (
  msg.contains("bye") || msg.contains("goodbye") || msg.contains("see you") || 
  msg.contains("quit") || msg.contains("exit") ||
  msg.startsWith("no") || msg.startsWith("not now") || msg.startsWith("later") || 
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
  msg.contains("hello") || msg.contains("hi") || msg.contains("hey") || 
  msg.contains("greetings") ||
  msg.contains("what's up") || msg.contains("how are you") ||
  msg.startsWith("good ") && msg.contains("day") ||
  msg.contains("welcome back")
) => "greet"

case _ => "unknown"
  }
  val keywords = corrrectedTokens.filterNot(token =>
    Set(
     // Basic question words
  "what", "which", "how", "can", "could", "would", 
  "is", "are", "was", "were", "does", "do", "did",
  "has", "have", "had", "will", "shall", "may",
  "might", "must", "should", "need", "want", "yes", "no",
  "what's", "whats", // Added from patterns

  // Request verbs
  "tell", "give", "show", "explain", "describe", 
  "list", "provide", "share", "know", "find","walk",
  "define", // Added from dish_info pattern
  "try", "take", // Added from quiz pattern

  // Food-related verbs
  "make", "cook", "prepare", "create", "bake", 
  "fry", "grill", "boil", "steam", "roast",
  "require", "include", // Added from ingredients pattern

  // Prepositions/articles
  "about", "of", "for", "to", "from", "with", 
  "without", "in", "on", "at", "the", "a", "an", 
  "me", "as", "by", "through", // Added from patterns

  // General food terms
  "dish", "dishes", "food", "cuisine", "meal", "recipe",
  "ingredient", "step", "steps", "direction", "instructions", "instruction",
  "method", "way", // Added from patterns
  "calls", "required", // Added from ingredients

  // Quiz/Test terms
  "quiz", "test", "question", "challenge", 
  "knowledge", // Added from patterns

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
  "exit", "quit", "see", "go", "want", "information"

    ).contains(token.toLowerCase)
  )

  // Identify command type based on keywords

  (command, keywords)
}

/* def greeting(): Unit = {
  val helloo=("hello", "hallo", "hola", "bonjour", "greetings", "salutations", "hi there", "hey", "howdy", "hi", "good day",
  "ciao", "namaste", "salaam","konichiwa", "ahoy", "aloha", "hullo")
  println(Random.shuffle(helloo).head)

} */

    // Function to handle user input and provide responses
    // This function will be called when the user provides input
    // It will parse the input and check for valid commands   
  
  def handleUserInput(input: String): Unit = 
  {
  val (command, tokens) = parseInput(input)
  println( s" DEBUG:Command: $command, Tokens: ${tokens}") // Debugging line to check command and tokens
  command match {
    case "bye" => 
       println("""
          |👋 Until we meet again! 
          |Next time you visit, I'll have:
          |🍲 New recipes to share
          |🌶️ Spicy trivia to compare
          |👨‍🍳 More cooking tips to prepare!""".stripMargin)  
    case "quiz" =>
      if (tokens.isEmpty) {
      Quizez.startquiz("general", Typos.handleTypos)
      } else {
      Quizez.startquiz(tokens.mkString(" "), Typos.handleTypos) 
      }
    case "recipe" =>
      println("DEBUG:Going to reciepe")
      if (isIngredientSearch(tokens)) {
        handleIngredientSearchResults(tokens)
      }else {
        handleRecipeRequest(tokens)
      }
    case "ingredients" => 
      //println("I can help you with that!")
      handleIngredientSearchResults(tokens)
    case "trivia" =>
      TriviaRequest(tokens.mkString(" ").toLowerCase)
    case "dish_info" => handleDishRequest(tokens)
    case "greet" => 
      //greet function!
      Personality.respondToGreeting()
    case "log" =>
      val logs = Analytics.getInteractionLog()
      logs.foreach { case (seq, user, bot) =>
        println(s"\n# $seq\n👤: $user\n🤖: $bot")
      }
      //botResponseBuffer.append(s"🗂 Displayed ${logs.length} interactions.")

      
    case "unknown" => // yerg3 llchat tany 
      val corrected = Typos.handleTypos(input)
      println(s"Did you mean: $corrected? Or try asking about a cuisine or type 'quiz'.") //need to handle this type pf error 
    }
  }

  def TriviaRequest(cuisine: String): Unit = {
  val trivia =FoodDB.getRandomTrivia(cuisine)  // Call the trivia function

  trivia match {
  case Some(triviaFact) =>
    println(s"""
      |✨*Did you know?* ✨
      |$cuisine cuisine is known for its:
      |${"-" * (cuisine.length + 12)}
      |💡 $triviaFact
      |
      |Pretty fascinating, right!
      |would you like a quiz to test ur knowledge?😊
      |""".stripMargin)

  case None =>
    println(s"""
      |🤖 *Chef's Note* 🤖
      |
      |Hmm, my recipe books don't seem to have fun facts about $cuisine...
      |But I know lots about:
      |${FoodDB.categories.map(_.name.capitalize).mkString(" • ")}
      |
      |Ask me about one of these cuisines instead! 👨‍🍳
      |""".stripMargin)
}
}
// it checks all ingredents in the database and returns true if any of them match the token 
  // it also handles typos in the ingredient names
  // this function is used in the handleIngredientSearchResults function to check if the user is looking for a specific ingredient or not 
  def isIngredientSearch(tokens: List[String]): Boolean = {
    tokens.exists { token =>
      val normalizedToken = Typos.handleTypos(token.toLowerCase)
      FoodDB.getAllIngredients.exists { ing =>
        Typos.handleTypos(ing.toLowerCase) == normalizedToken
      }
    }
  }

  def searchByIngredient(tokens: List[String]): (List[Dish], String) = {
    if (tokens.isEmpty) return (Nil, "no-ingredients-specified")
    
    val allDishes = FoodDB.getAllDishes
    val normalizedTokens = tokens.map(t => Typos.handleTypos(t.toLowerCase))
    
    // Precompute normalized ingredients for each dish
    val dishesWithNormalized = allDishes.map { dish =>
      (dish, dish.ingredients.map(i => Typos.handleTypos(i.toLowerCase)))
    }

    // Check matches in order of specificity
    val exactAll = dishesWithNormalized.filter { case (_, dishIngs) =>
      normalizedTokens.forall(token => dishIngs.contains(token))
    }
    //returns all the dishes that have all the ingredients in the list
    if (exactAll.nonEmpty) return (exactAll.map(_._1), "exact-all-ingredients")

    val exactAny = dishesWithNormalized.filter { case (_, dishIngs) =>
      normalizedTokens.exists(token => dishIngs.contains(token))
    }
    if (exactAny.nonEmpty) return (exactAny.map(_._1), "exact-any-ingredient")

    val partialAll = dishesWithNormalized.filter { case (dish, dishIngs) =>
      normalizedTokens.forall(token => 
        dishIngs.exists(_.contains(token)) || 
        Typos.handleTypos(dish.name.toLowerCase).contains(token)
      )
    }
    if (partialAll.nonEmpty) return (partialAll.map(_._1), "partial-all-ingredients")

    val partialAny = dishesWithNormalized.filter { case (dish, dishIngs) =>
      normalizedTokens.exists(token => 
        dishIngs.exists(_.contains(token)) || 
        Typos.handleTypos(dish.name.toLowerCase).contains(token)
      )
    }
    
    if (partialAny.nonEmpty) 
      (partialAny.map(_._1), "partial-any-ingredient")
    else 
      (Nil, "no-matches-found")
  }

  def handleIngredientSearchResults(tokens: List[String]): Unit = {
    val ingredients = tokens.filter { token =>
    val normalizedToken = Typos.handleTypos(token.toLowerCase)
    FoodDB.getAllIngredients.exists { ing =>
      Typos.handleTypos(ing.toLowerCase) == normalizedToken
      }
    }

    if (ingredients.isEmpty) {
      println("I couldn't identify any ingredients in your request, maybe rephrseas " + tokens.mkString(" "))
      return
    }

    val (dishes, matchType) = searchByIngredient(tokens)
    
    val header = matchType match {
      case "no-ingredients-specified" =>
        println("Please specify ingredients you'd like to search for!")
        
      case "exact-all-ingredients" => s"🍽️ *Dishes with ${ingredients.mkString(" and ")}* 🍽️"
      case "exact-any-ingredient" => s"🍴 *Dishes containing ${ingredients.mkString(" or ")}* 🍴"
      case _ => s"🔍 *Possible matches for ${ingredients.mkString(", ")}* 🔍"
    }
  
    println(s"""
      |$header
      |
      |${dishes.map(d => 
          s"◎ ${d.name} (${if (d.isVegetarian) "Vegetarian" else "Non-vegetarian"})\n" +
          s"   Ingredients: ${d.ingredients.take(5).mkString(", ")}${if (d.ingredients.size > 5) "..." else ""}"
        ).mkString("\n\n")}
      |
      |Type the exact name of the dish you want, or 'none' to cancel.
      |""".stripMargin)

    // Handle user selection
    val selection = Typos.handleTypos(scala.io.StdIn.readLine().trim.toLowerCase)
    selection match {
      case name if dishes.exists(_.name.toLowerCase == name) =>
        showRecipe(dishes.find(_.name.toLowerCase == name).get)
      case "none" | "no" | "exit" | "cancel"|"non"|"nonee"|"nun" | ""=>
        println("No problem! Let me know if you need anything else.")
      case _ =>
        println(s"Sorry, I didn't find '$selection' in the results. Please try again.")
        handleIngredientSearchResults(tokens) // Retry
    }
  }

  // Function to analyze recipe requests and return dish or vegetarian request
  def analyzeRecipeRequest(tokens: List[String]): (Option[Dish], String) = 
    {
      val allDishes = FoodDB.getAllDishes

      // 1. First try exact single-word matches using collectFirst pattern
      // In analyzeRecipeRequest, change the exact match condition:
      val exactMatch = tokens.collectFirst {
        case t => allDishes.find(d => 
          Typos.handleTypos(d.name.toLowerCase) == Typos.handleTypos(t))
      }.flatten

      if (exactMatch.isDefined) return (exactMatch, "exact") //checks if match is found returns exact match early on 
    //isdefined , returns tuple immediatly containg (exactmatch , "string label indicatinng match")

      // 2. Check for multi-word dish names (like "coq au vin")
      if (tokens.size > 1) {  //means in token has more than 1 word 
        val combined = tokens.mkString(" ")
        val combinedMatch = allDishes.find(d => 
          Typos.handleTypos(combined) == d.name.toLowerCase)
        if (combinedMatch.isDefined) return (combinedMatch, "combined")
      } 

    // 3. Check for vegetarian requests , //
      if (tokens.exists(t => Typos.handleTypos(t).toLowerCase == "vegetarian")) {
        val isVegetarianRequest = tokens.foldLeft(false) { (found, token) =>
          if (found) true else Typos.handleTypos(token).toLowerCase == "vegetarian"
        }
        if (isVegetarianRequest) {
          return (None, "vegetarian")
        }
      }
    // 4. Try partial matches as fallback / when no exact matches are found 
      val partialMatch = tokens.collectFirst {
        case t => allDishes.find(d => 
          d.name.toLowerCase.contains(Typos.handleTypos(t))) 
      }.flatten  //tries to find partial dish match or partical dish match

      partialMatch match {
        case Some(d) => (Some(d), "partial")
        case None => (None, "unknown")
      }
  }

  // Function to handle recipe requests and show details
  // This function will be called when the user asks for a recipe
  def handleRecipeRequest(tokens: List[String]): Unit = {
    val (dish, searchType) = analyzeRecipeRequest(tokens) //check validity of dish name and return the type of search
  
    dish match {
    case Some(foundDish) =>
      showRecipe(foundDish)
      dish_suggestion()
      // askForFeedback(foundDish.name)
      
    case None =>
      searchType match {
        case "vegetarian" =>
          val allCategories = FoodDB.dishesByCategory.map(x => x._1)
          val maybeCategory = allCategories.find(cat => tokens.contains(cat))
          
          val vegDishes = maybeCategory match {
            case Some(cat) => 
              FoodDB.getDishesByCategory(cat).filter(_.isVegetarian)
            case None => 
              FoodDB.getVegetarianDishes
          }

          if (vegDishes.isEmpty) {
            val message = maybeCategory match {
              case Some(cat) => s"🌿 *Veggie Alert* 🌿\n\nNo $cat vegetarian dishes in my cookbook yet!"
              case None => "🌿 *Plant-Based Update* 🌿\n\nMy vegetarian recipe collection is empty right now!"
            }
            println(s"""
              |$message
              |
              |But I have great ${allCategories.take(3).mkString(", ")} options!
              |Want me to check those? 😊
              |""".stripMargin)
          } 
          else 
          {
            val header = maybeCategory match{
                case Some(cat) => s"🌱 *${cat.capitalize} Vegetarian Specials* 🌱"
                case None => "🌿 *Vegetarian Menu* 🌿"
              }
            println(header)
            Random.shuffle(vegDishes).take(5).foreach { d =>
                println(s"🍽  ${d.name} (${d.ingredients.take(5).mkString(", ")}${if (d.ingredients.size > 5) "..." else ""})")}
            vegDishes.foreach(d => println(s"🍽️  ${d.name} (${d.ingredients.mkString(", ")})"))
            
            if (vegDishes.size == 1) showRecipe(vegDishes.head)
          }
          println("🌱Type the exact name of the dish you want, or 'none' to cancel.🌱")
          val selection = Typos.handleTypos(scala.io.StdIn.readLine().trim.toLowerCase)
          selection match {
            case name if vegDishes.exists(_.name.toLowerCase == name) =>
              showRecipe(vegDishes.find(_.name.toLowerCase == name).get)
            case "none" | "no" | "exit" | "cancel"|"non"|"nonee"|"nun"| "thanks"|" " =>
              println("No problem! Let me know if you need anything else.")
            case _ =>
              println(s"Sorry, I didn't find '$selection' in the results. Please try again.")
          }
          dish_suggestion()
        case "ingredient" =>
          handleIngredientSearchResults(tokens)
        case "unknown" | _ =>  // Add this catch-all case
          println(s"I couldn't find any recipes matching '${tokens.mkString(" ")}'")
          showSimilarDishes(tokens)
      }
  }
}
    
  //when no dish is found , i can suggest those dishes 
  def showSimilarDishes(tokens: List[String]): Unit = 
  {
    val allDishes = FoodDB.getAllDishes
    val similar = allDishes.filter { dish =>   //finds simliarty in dish name or ingedient 
      tokens.exists(t => 
        dish.name.toLowerCase.contains(Typos.handleTypos(t)) ||
        dish.ingredients.exists(i => 
          i.toLowerCase.contains(Typos.handleTypos(t))))
    }
    
    if (similar.nonEmpty) {
      println("🔍 I found these similar dishes:")
      similar.take(5).foreach(d => println(s"👉 ${d.name}"))
    } else {
    
      println("🌟 Here are some popular dishes:")
      Random.shuffle(allDishes).take(5).foreach(d => println(s"👉 ${d.name}"))

    }
    
    //follow-up interaction if he wants recipe to be shown 
    println("\n👨‍🍳 Would you like to learn how to cook any of these dishes? Type the name or 'no' to explore something else! 🍳")
    val userChoice = readLine().trim
    
    if (userChoice.nonEmpty && Typos.handleTypos(userChoice) != "no") {
      // Find the selected dish (with typo handling)
      val selectedDish = similar.find(_.name.equalsIgnoreCase(userChoice))
        .orElse(allDishes.find(_.name.equalsIgnoreCase(userChoice)))
      
      selectedDish match {
        case Some(dish) => showRecipe(dish)
        case None => println("👨‍🍳 Oops! I couldn't find that dish. Please type the exact name as shown above! 🔍")
      
    }
    }
     else {
      println("🌟 No problem at all! Feel free to explore other dishes whenever you're ready! 👨‍🍳")
    }
  }
  
  // This function would show cuisine details (for example: dishes belonging to a particular cuisine)
  def showCuisineInformation(cuisine: String): Unit = {
  // Find the category for the cuisine
  val category = FoodDB.categories.find(_.name.toLowerCase == cuisine.toLowerCase)
  
  category match {
    case Some(c) =>
      println(s"Details for ${c.name.capitalize} cuisine:")
      val dish=FoodDB.getDishesByCategory(c.name)
      dish.foreach(d => println(s"- ${d.name}"))
    case None =>
      println(s"👨‍🍳 *Chef's confession*: My $cuisine knowledge is still simmering!")
  }
}

    def handleDishRequest(tokens: List[String]): Unit = {
      val userName = UserState.getName
      val searchQuery = tokens.mkString(" ")
      
      // Log the initial dish search
      Analytics.updateUserSearchLog(userName, searchQuery)
      
      FoodDB.getDish(tokens) match {
        case Some(dish) =>
          // Log successful dish found
           Analytics.logInteraction(s"Search for dish: ${tokens.mkString(" ")}", 
            "Processing dish request",
            userName
            )
          showDishDetails(dish)
          dish_suggestion()
          
        case None =>
          // Log dish not found
          Analytics.logInteraction(
            s"Search for dish: $searchQuery", 
            "Dish not found, showing alternatives", 
            userName
          )
          
          println(s"👨‍🍳 Oops! '$searchQuery' isn't in my database yet. But here are some tasty alternatives:")

          val suggestions = FoodDB.getAllDishes.filter { dish =>
            tokens.exists(token => dish.name.toLowerCase.contains(token.toLowerCase))
          }.take(5)

          if (suggestions.nonEmpty) {
            println("\n🔍 Similar dishes you might like:")
            suggestions.foreach(d => println(s"- ${d.name}"))
          } else {
            println("\n🍽️ Here are some delicious options for you:")
            Random.shuffle(FoodDB.getAllDishes).take(5).foreach(dish => println(s"- ${dish.name}"))
          }

          // Prompt user to enter a dish or type "no"
          print("\n👨‍🍳 Which dish would you like to explore? > ")
          val input = scala.io.StdIn.readLine().trim.toLowerCase
          val corrected = Typos.handleTypos(input)

          // Log user's selection
          Analytics.logInteraction(
            s"User selected: $input", 
            s"Corrected to: $corrected", 
            userName
          )

          if (corrected != "no" && input.nonEmpty) {
            val dish = FoodDB.getDish(List(corrected))
            dish match {
              case Some(d) => 
                // Log successful follow-up dish found
                Analytics.logInteraction(
                  s"Follow-up search: $corrected", 
                  s"Found dish: ${d.name}", 
                  userName
                )
                showDishDetails(d)
                
              case None =>
                // Log follow-up dish not found
                Analytics.logInteraction(
                  s"Follow-up search: $corrected", 
                  "Dish not found, showing similar dishes", 
                  userName
                )
                println(s"👨‍🍳 Oops! I couldn't find '$input'. Let's try again.")
                showSimilarDishes(List(corrected))
            }
          } else if (corrected == "no" || corrected == "none" || corrected == "cancel" || 
                    corrected == "exit" || corrected == "quit" || corrected == "thanks" || 
                    input.isEmpty) {
            // Log user exit
            Analytics.logInteraction(
              "User ended dish exploration", 
              "Showing farewell message", 
              userName
            )
            println("""
              |👨‍🍳 No worries, foodie! 
              |I'm always here to help you discover delicious dishes! 
              |Just let me know what you'd like to explore next! 🍽️
              |""".stripMargin)
          } else {
            val corrected = Typos.handleTypos(input)
            FoodDB.getDish(List(corrected)) match {
              case Some(dish) =>
                // Log successful dish found after retry
                Analytics.logInteraction(
                  s"Retry search: $corrected", 
                  s"Found dish: ${dish.name}", 
                  userName
                )
                showDishDetails(dish)
                
              case None =>
                // Log final dish not found
                Analytics.logInteraction(
                  s"Final search attempt: $corrected", 
                  "Dish still not found", 
                  userName
                )
                println(s"\nSorry, I still couldn't find '$input'. try to rephrase it.")
            }
          }
      }
    }
    
  def showDishDetails(dish: Dish): Unit = {
    val dishtype= dish.Dish_Type match {
      case "main" => "Main Course"
      case "appetizer" => "Appetizer"
      case "dessert" => "Dessert"
      case "salad" => "Salad"
      case "soup" => "Soup"
      case "breakfast" => "Breakfast"
      case _ => "Dish"
    }
    val vegStatus = if (dish.isVegetarian) "Vegetarian" else "Non-vegetarian"
    println(s"\n◎ ${dish.name}: $dishtype ($vegStatus) ")
    println(s"   Ingredients: ${dish.ingredients.mkString(", ")} ")
    println("\nWould you like me to show you the recipe f" +
      "or this dish?" )
    val answer = readLine().trim.toLowerCase
    val corrected= Typos.handleTypos(answer)
    if (corrected == "no" || corrected == "exit" || corrected == "quit" || corrected == "thanks" || answer.isEmpty) {
      println("Okay, let me know if you need anything else!")
    } else if (corrected == "yes" || corrected == "y"|| corrected == "sure"|| corrected=="please") {
      println("Fetching the recipe...")
      showRecipe(dish)
    } else {
      println("I didn't understand that.Could you rephrase?") //y/n
    }
  }

  def showRecipe(dish: Dish): Unit = {
  // This would need actual recipe data - you could add this to your Dish case class
  println(s"\nRecipe for ${dish.name}:")
  dish.recipeSteps.foreach { step =>
    println(s"• $step")
  }
  println("\nWould you like a recipe link? ")
  val answer = readLine().trim.toLowerCase
    val corrected= Typos.handleTypos(answer)
  if (corrected == "no" || corrected == "exit" || corrected == "quit" || corrected == "thanks" || answer.isEmpty) {
    println("👨‍🍳 No problem at all! I'm always here to share more recipes and food knowledge!")
      
  } else if (corrected == "yes" || corrected == "sure"|| corrected=="please") {
    println(s"📜 Recipe link: ${dish.recipeLink}")
  } 
  println("Enjoy your cooking!") // after this part return to the main chat 
}

  def mainChat(): Unit = {
 println(greetUser()) // Time-based greeting
  Personality.respondToGreeting()

  println("Before we begin, what should I call you?")
  val name = readLine().trim
  if (name.nonEmpty) UserState.setName(name)

  Personality.askHowUserIs()

  chatLoop()
}

  @tailrec
  def chatLoop(): Unit = {
  print(s"\n${UserState.getName.capitalize}: ")
  val input = readLine().trim.toLowerCase

  if (input.isEmpty) {
    println("👨‍🍳 Oops! The kitchen seems quiet... Type something or 'quit' to leave!")
    chatLoop()

  } else if (input.contains("joke")) {
    Personality.tellJoke()
    chatLoop()

  } else if (input.contains("how are you") || input.contains("what's up")) {
    Personality.askHowUserIs()
    chatLoop()

  } else if (input.contains("analytics") || input.contains("my preferences")) {
    val userName = UserState.getName
    Analytics.handleUserRequestForAnalytics(userName)
    chatLoop()

  } else if (input.contains("interaction report") || input.contains("usage summary")) {
    Analytics.analyzeInteractions()
    chatLoop()

  } else if (input.contains("chat log") || input.contains("conversation history") || input.contains("full log")) {
    val logs = Analytics.getInteractionLog()
    logs.foreach { case (seq, user, bot) =>
      println(s"\n# $seq\n👤: $user\n🤖: $bot")
    }
    println(s"\n🗂 Displayed ${logs.length} interactions.")
    chatLoop()

  } else {
    val (command, _) = parseInput(input)

    if (command == "bye") {
      println(s"""
        |👋 Goodbye, ${UserState.getName.capitalize}! 
        |Come back anytime for:
        |🍲 More recipes
        |🌶 Fresh trivia
        |🍽 And a good laugh!""".stripMargin)
    } else {
      handleUserInput(input)
      chatLoop()
    }
  }
}
  def dish_suggestion(initialCall: Boolean = true): Unit = {
  val suggestions = FoodDB.getRandomDishSuggestions()
  
  if (initialCall) {
    println("\nBefore you go, would you be interested in these dishes?")
  } else {
    println("\nHere are some more suggestions:")
  }
  
  suggestions.foreach(d => println(s"🍽️ ${d.name}"))
  
  println("\nWould you like to know more about any of these? (enter dish name, or 'no' to exit)")
  val answer = readLine().trim.toLowerCase
  
  // Check for explicit exit commands
  val corrected = Typos.handleTypos(answer)
  if (corrected == "no" || corrected == "n" || corrected == "exit" || corrected == "quit"|| answer =="") {
    println("Alright, have a delicious day! 👨‍🍳")
    return
  } 
  
  // Try to find matching dish
  val selectedDish = suggestions.find(d => 
    Typos.handleTypos(d.name.toLowerCase) == corrected
  )
  
  selectedDish match {
    case Some(dish) =>
      showDishDetails(dish)
      println("\nWould you like another suggestion? (yes/no)")
      val continue = readLine().trim.toLowerCase
      if (Typos.handleTypos(continue) == "yes") {
        dish_suggestion(initialCall = false)
      } else {
        println("Alright, have a delicious day! 👨‍🍳")
      }
      
    case None =>
      println(s"Sorry, I didn't recognize '$answer'. Here are your options:")
      dish_suggestion(initialCall = false) // Show suggestions again
  }
}
  def main(args: Array[String]): Unit = {
 mainChat()
  }
}   
  // Function to handle dish requests and show details
  // This function will be called when the user asks about a dish
  /*def handleDishRequest(tokens: List[String]): Unit = {
    FoodDB.getDish(tokens) match {
      case Some(dish) =>
        showDishDetails(dish)
        dish_suggestion()
        return
      case None =>
        println(s"👨‍🍳 Oops! '${tokens.mkString(" ")}' isn't in my database yet. But here are some tasty alternatives:")

        val suggestions = FoodDB.getAllDishes.filter { dish =>
          tokens.exists(token => dish.name.toLowerCase.contains(token.toLowerCase))
        }.take(5)

        if (suggestions.nonEmpty) {
          println("\n🔍 Similar dishes you might like:")
            suggestions.foreach(d => println(s"- ${d.name}"))
        } else {
          println("\n🍽️ Here are some delicious options for you:")
          Random.shuffle(FoodDB.getAllDishes).take(5).foreach(dish => println(s"- ${dish.name}"))
        }
      }
        // Prompt user to enter a dish or type "no"
        print("\n👨‍🍳 Which dish would you like to explore? > ")
        val input = scala.io.StdIn.readLine().trim.toLowerCase
        val corrected = Typos.handleTypos(input)

        if (corrected != "no" && input.nonEmpty) {
          // Check if the user entered a valid dish name or ingredient
          val dish = FoodDB.getDish(List(corrected))
          dish match {
            case Some(d) => showDishDetails(d)
            case None =>
              println(s"👨‍🍳 Oops! I couldn't find '$input'. Let's try again.")
              showSimilarDishes(List(corrected)) // Show similar dishes
          }
        } else if (corrected == "no" | corrected == "none" || corrected == "cancel" || corrected == "exit" || corrected == "quit" || corrected == "thanks" || input.isEmpty) {
          println("""
            |👨‍🍳 No worries, foodie! 
            |I'm always here to help you discover delicious dishes! 
            |Just let me know what you'd like to explore next! 🍽️
            |""".stripMargin)
        } else
        {
          val corrected = Typos.handleTypos(input)
          FoodDB.getDish(List(corrected)) match {
            case Some(dish) => showDishDetails(dish)
            case None =>
              println(s"\nSorry, I still couldn't find '$input'. try to rephrase it.")
            }
          }
      
  
    }*/