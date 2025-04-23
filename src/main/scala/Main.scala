import java.time.LocalTime
import scala.io.StdIn.readLine
import scala.util.Random
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
    // Handle typos using the Typos object
    Typos.handleTypos(token)
  }
  val command = cleaned match {
    case msg if msg.startsWith("how do i") ||msg.contains("make")|| msg.contains("steps") || msg.contains("prepare") || msg.contains("recipe") =>
      "recipe"
    case msg if msg.startsWith("what are") || msg.contains("ingredients") || msg.contains("made of")||
    msg.contains("with") || msg.contains("containing") || msg.contains("using") => "ingredients"
    case msg if msg.contains("quiz") || msg.contains("test") || msg.contains("question") =>
      "quiz"
    case msg if msg.contains("trivia") || msg.contains("fact") || msg.contains("interesting") =>
      "trivia"
    case msg if msg.contains("tell me about") || msg.contains("describe") =>
      "dish_info"
    case msg if msg.contains("bye") || msg.contains("quit") || msg.contains("exit") =>
      "bye"
    case _ =>
      "unknown"
  }
  val keywords = corrrectedTokens.filterNot(token =>
    Set(
      // Basic question words
      "what", "which", "how", "can", "could", "would", 
      "is", "are", "was", "were", "does", "do", "did",
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
      "has", "uses", "made", "find", "search","about","quiz","trivia",
      // Punctuation
      "?", "!", ".", ","
    ).contains(token.toLowerCase)
  )

  // Identify command type based on keywords

  (command, keywords)
}

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
      Quizez.startquiz(tokens.mkString(" "),Typos.handleTypos)
    case "recipe" =>
      println("DEBUG:Going to reciepe")
      handleRecipeRequest(tokens)
    case "ingredients" => 
      //println("I can help you with that!")
      handleRecipeRequest(tokens)
    case "trivia" =>
      TriviaRequest(tokens.mkString(" ").toLowerCase)
    case "dish_info" => handleDishRequest(tokens)
    case "unknown" => // yerg3 llchat tany 
      val corrected = Typos.handleTypos(input)
      println(s"Did you mean: $corrected? Or try asking about a cuisine or type 'quiz'.") //need to handle this type pf error 
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
  def TriviaRequest(cuisine: String): Unit = {
  val trivia =FoodDB.getRandomTrivia(cuisine)  // Call the trivia function

 trivia match {
  case Some(triviaFact) =>
    println(s"""
      |✨ *Did you know?* ✨
      |
      |About $cuisine cuisine:
      |${"-" * (cuisine.length + 12)}
      |💡 $triviaFact
      |
      |Pretty fascinating, right! 😊
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

  def searchByIngredient(tokens: List[String]): (List[Dish], String) = {
    val allDishes = FoodDB.getAllDishes
    val normalizedTokens = tokens.map(t => Typos.handleTypos(t.toLowerCase))

    // 1. Find ALL dishes containing ALL ingredients (exact match)
    val exactMatches = allDishes.filter(dish =>
      normalizedTokens.forall(token =>
        dish.ingredients.exists(dishIng =>
          Typos.handleTypos(dishIng.toLowerCase) == token
        )
      )
    )
    
    if (exactMatches.nonEmpty) return (exactMatches, "exact-ingredients-match")

    // 2. Find ALL dishes containing ANY ingredient (partial match)
    val partialMatches = allDishes.filter(dish =>
      normalizedTokens.exists(token =>
        dish.ingredients.exists(dishIng =>
          Typos.handleTypos(dishIng.toLowerCase).contains(token)
        ) ||
        Typos.handleTypos(dish.name.toLowerCase).contains(token)
      )
    )

    if (partialMatches.nonEmpty) {
      (partialMatches, "partial-ingredients-match")
    } else {
      (Nil, "no-matches-found")
    }
  }
  def handleRecipeRequest(tokens: List[String]): Unit = 
  {
      val (dish, searchType) = analyzeRecipeRequest(tokens)
      val (dishs,matchtype)=searchByIngredient(tokens)
      matchtype match {
         case "exact-ingredients-match" =>
      println(s"""
        |✨ *Perfect Match!* ✨
        |
        |Here are dishes containing ${tokens.mkString(", ")}:
        |${dishs.map(d => s"🍲 ${d.name} (Ingredients: ${d.ingredients.take(3).mkString(", ")}${if (d.ingredients.size > 3) "..." else ""})").mkString("\n")}
        |
        |Which one would you like to explore? Or ask for more details! 👨‍🍳
        |""".stripMargin)
      return

        case "partial-ingredients-match"=>
          println(s"""
        |🔍 *Close Matches* 🔍
        |
        |I found dishes with some of ${tokens.mkString(", ")}:
        |${dishs.map(d => s"🥗 ${d.name} (Partial match: ${d.ingredients.intersect(tokens).mkString(", ")})").mkString("\n")}
        |
        |Would you like the full recipe for any of these? 😊
        |""".stripMargin)
      return 
        
        case "no-matches-found" => 
           println(s"""
        |🤖 *Chef's Note* 🤖
        |
        |Hmm, I couldn't find dishes with ${tokens.mkString(", ")}...
        |
        |Try these options:
        |• Check your spelling 🖊️
        |• Try more common ingredients
        |
        |I believe in your culinary creativity! 💡
        |""".stripMargin)
  }
          
      
      dish match {
        case Some(foundDish) =>
          showRecipe(foundDish)
          //askForFeedback(foundDish.name)
          // Could save user preference here if needed
        case None =>
          searchType match {

            case "vegetarian" =>

                val allCategories = FoodDB.dishesByCategory.map(x=> x._1)
                val maybeCategory = allCategories.find(cat => 
                  tokens.contains(cat))
                
                val vegDishes = maybeCategory match {
                  case Some(cat) => 
                    FoodDB.getDishesByCategory(cat).filter(_.isVegetarian)
                  case None => 
                    FoodDB.getVegetarianDishes
                }

                // Display results
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
                } else {
                  val header = maybeCategory match {
                    case Some(cat) => s"🌱 *${cat.capitalize} Vegetarian Specials* 🌱"
                    case None => "🌿 *Vegetarian Menu* 🌿"
                  }
                  println(header)
                  vegDishes.foreach(d => println(s"🍽️  ${d.name} (${d.ingredients.mkString(", ")})"))
                  
                  // Auto-show single result
                  if (vegDishes.size == 1) showRecipe(vegDishes.head)
                  }

              case "unknown" =>
                 println(s"""
                            |🤔 *Recipe Radar* 🤔
                            |
                            |I couldn't find '${tokens.mkString(" ")}' in my cookbooks...
                            |
                            |Similar options:
                            |${showSimilarDishes(tokens)}
                            |
                            |Try these or ask differently! (Tip: Be specific - e.g., 'vegetarian pasta') 🍝
                            |""".stripMargin)
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
      allDishes.take(5).foreach(d => println(s"👉 ${d.name}"))

    }
    
    //follow-up interaction if he wants recipe to be shown 
    println("\nType a dish name for recipe or 'no'.")
    val userChoice = readLine().trim
    
    if (userChoice.nonEmpty && Typos.handleTypos(userChoice) != "no") {
      // Find the selected dish (with typo handling)
      val selectedDish = similar.find(_.name.equalsIgnoreCase(userChoice))
        .orElse(allDishes.find(_.name.equalsIgnoreCase(userChoice)))
      
      selectedDish match {
        case Some(dish) => showRecipe(dish)
        case None => println("⚠️ Dish not found. Try exact name")
      }
    } else {
      println("Okay, let me know if you change your mind!")
    }
  }// This function would show cuisine details (for example: dishes belonging to a particular cuisine)
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
        print("\n👨‍🍳 Which dish would you like to explore? (or type 'no') > ")
        val input = scala.io.StdIn.readLine().trim.toLowerCase

        if (input != "no") 
        {
          val corrected = Typos.handleTypos(input)
          FoodDB.getDish(List(corrected)) match {
            case Some(dish) => showDishDetails(dish)
            case None =>
              println(s"\nSorry, I still couldn't find '$input'.")
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
    if (corrected == "no" || corrected == "n" || corrected == "nope" || corrected == "nah") {
      println("Okay, let me know if you need anything else!")
    } else if (corrected == "yes" || corrected == "y"|| corrected == "sure"|| corrected == "yep"|| corrected=="please") {
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
  println("\nWould you like a recipe link? (yes/no)")
  val answer = readLine().trim.toLowerCase
    val corrected= Typos.handleTypos(answer)
  if (corrected == "no" || corrected == "n" || corrected == "nope" || corrected == "nah") {
    println("Okay, let me know if you need anything else! Away ready to help.")
  } else if (corrected == "yes" || corrected == "y"|| corrected == "sure"|| corrected == "yep"|| corrected=="please") {
    println(s"📜 Recipe link: ${dish.recipeLink}")
  } 
  println("Enjoy your cooking!") // after this part return to the main chat 
}
  def mainChat(): Unit = {
  println(greetUser()) // Show initial greeting
  
  var shouldContinue = true
  
  while (shouldContinue) {
    print("\nWhat would you like to know? > ")
    val input = readLine().trim
    
    if (input.isEmpty) {
      println("Please enter a command or type 'quit' to exit")
    } else {
      val (command, _) = parseInput(input)
      
      // First check if it's a quit command
      if (command == "bye") {
        println("Goodbye! Happy cooking!")
        shouldContinue = false
      } else {
        handleUserInput(input)
      }
    }
  }
}

  def main(args: Array[String]): Unit = {
 mainChat()
    
  }
  }
