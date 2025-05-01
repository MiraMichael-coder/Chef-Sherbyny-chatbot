import java.time.LocalTime
import scala.io.StdIn.readLine
import scala.util.Random
import scala.annotation.tailrec
object Main  {

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

// Ingredients
case msg if (
  msg.startsWith("what is") && (
    msg.contains("ingredient") ||
    msg.contains("made of") ||
    msg.contains("in it") ||
    msg.contains("contains")
  ) ||
  (msg.contains("use") || msg.contains("need") || msg.contains("require")) && 
  msg.contains("to make") ||
  msg.contains("contain") || msg.contains("include") || msg.contains("have") || msg.contains("take") ||
  msg.startsWith("what do i need for") ||
  msg.contains("recipe calls for") || msg.contains("need for") || msg.contains("required for")
) => "ingredients"

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
      "might", "must", "should", "need", "want","yes","no","please",
      // Request verbs
      "tell", "give", "show", "explain", "describe", 
      "list", "provide", "share", "know", "find","fun","fact",
      // Food-related verbs
      "make", "cook", "prepare", "create", "bake", 
      "fry", "grill", "boil", "steam", "roast",
      // Prepositions/articles
      "about", "of", "for", "to", "from", "with", 
      "without", "in", "on", "at", "the", "a", "an", "me", "as",
      // General food terms
      "dish", "dishes", "food", "cuisine", "meal", "recipe",
      "ingredient", "step", "direction", "instruction",
      // Polite terms
      "please", "thanks", "thank", "hello", "hi",
      // Quantifiers
      "some", "any", "many", "few", "several",
      // Common connectors
      "and", "or", "but", "with", "without", "containing", "using", "contains",
      // Ingredient search terms (added)
      "has", "uses", "made", "find", "search","about","quiz","trivia","i",
      // Punctuation
      "?", "!", ".", ","
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
      println("Hello! How can I assist you today?")
      
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
          
        case "ingredient" =>
          handleIngredientSearchResults(tokens)
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
  // Function to handle dish requests and show details
  // This function will be called when the user asks about a dish
  def handleDishRequest(tokens: List[String]): Unit = {
    FoodDB.getDish(tokens) match {
      case Some(dish) =>
        showDishDetails(dish)
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
      
  
    }
    
  def showDishDetails(dish: Dish): Unit = {
    val vegStatus = if (dish.isVegetarian) "Vegetarian" else "Non-vegetarian"
    println(s"\n◎ ${dish.name} ($vegStatus)")
    println(s"   Ingredients: ${dish.ingredients.mkString(", ")}")
    println("\nWould you like me to show you the recipe for this dish?")
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
  println(greetUser()) // Show initial greeting
  chatLoop()
}

@tailrec
 def chatLoop(): Unit = {
  print("\nWhat would you like to know? > ")
  val input = readLine().trim
  
  if (input.isEmpty) {
    println("""
    |👨‍🍳 Oops! The kitchen seems quiet...
    |Share your food curiosity or type 'quit' to leave!🍽️
    |""".stripMargin)
    chatLoop()
  } else {
    val (command, _) = parseInput(input)
    
    if (command == "bye") {
      println("""
                |👋 Until we meet again! 
                |Next time you visit, I'll have:
                |🍲 New recipes to share
                |🌶️ Spicy trivia to compare
                |👨‍🍳 More cooking tips to prepare!""".stripMargin)
      //println(Analytics.getlog)
    } else {
      handleUserInput(input)
      chatLoop()
    }
  }
}

  def main(args: Array[String]): Unit = {
 mainChat()
  
   
    
  }
  }

 /*  def handleCuisineRequest(tokens: List[String]): Unit = {
  val maybeCuisine = tokens.collectFirst {
    case t if FoodDB.categories.exists(_.name == Typos.handleTypos(t)) => Typos.handleTypos(t)
  }

  maybeCuisine match {
    case Some(cuisine) =>
      showCuisineInformation(cuisine)  // Show cuisine details if found
    case None =>
      println("Which cuisine would you like to know about?")
      FoodDB.categories.foreach(c => println(c.name.capitalize))  // Display available cuisines
      val cuisine = scala.io.StdIn.readLine().trim.toLowerCase
      val corrected = Typos.handleTypos(cuisine)
      if (FoodDB.categories.exists(_.name == corrected)) {
        showCuisineInformation(corrected)  // Show details of the corrected cuisine
      } else {
        println(s"Sorry, '$cuisine' is not available.")
      }
  }
} */     
