import java.time.LocalTime
import scala.io.StdIn.readLine
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
  val tokens = input.toLowerCase.replaceAll("""[^\w\s]""", "").split("\\s+").toList
  val correctedTokens = tokens.map(Typos.handleTypos)
  
  // Identify command type based on keywords
  val command = correctedTokens match {
    // Quiz commands
    case t if t.contains("quiz") || t.contains("test") || t.contains("question") => "quiz"
    
    // Information requests
    case t if (t.contains("tell") || t.contains("about") || t.contains("what") || 
               t.contains("explain") || t.contains("describe")) && 
              (t.contains("ingredient") || t.contains("make") || t.contains("contains")) => "ingredients"
              
    case t if (t.contains("how") && t.contains("make")) || 
              t.contains("recipe") || 
              (t.contains("steps") && (t.contains("make") || t.contains("prepare"))) => "recipe"
              
    case t if t.contains("knowledge") || t.contains("fact") || 
              t.contains("trivia") || t.contains("interesting") => "trivia"

    // INGREDIENT SEARCH - Updated to better catch ingredient queries
    case t if (t.contains("with") || t.contains("containing") || t.contains("has") || 
               t.contains("uses") || t.contains("made with") || 
               (t.contains("ingredient") && (t.contains("find") || t.contains("search") || t.contains("using")))) => 
               "search_by_ingredient"
    
    // Dish information
    case t if (t.contains("what") || t.contains("tell") || t.contains("about")) && 
              !t.contains("ingredient") && !t.contains("recipe") => "dish_info"
    
    // Exit commands
    case t if t.contains("bye") || t.contains("exit") || t.contains("quit") || t.contains("stop") => "bye"
    
    case _ => "unknown"
  }
  
  // Extract keywords while filtering out common words
  val keywords = correctedTokens.filterNot(token =>
    Set(
      // Basic question words
      "what", "which", "how", "can", "could", "would", 
      "is", "are", "was", "were", "does", "do", "did",
      // Request verbs
      "tell", "give", "show", "explain", "describe", 
      "list", "provide", "share", "know", "find",
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
      "has", "uses", "made", "find", "search",
      // Punctuation
      "?", "!", ".", ","
    ).contains(token.toLowerCase)
  )

  (command, keywords)
}
    // Function to handle user input and provide responses
    // This function will be called when the user provides input
    // It will parse the input and check for valid commands   
  
  def handleUserInput(input: String): Unit = 
  {
  val (command, tokens) = parseInput(input)
  
  command match {
    case "bye" => 
      println("Goodbye! Happy cooking!")
    case "quiz" =>
      Quizez.startquiz(tokens.mkString(" "),Typos.handleTypos)
    case "recipe" =>
      handleRecipeRequest(tokens)
    case "ingredients" => 
      println("I can help you with that!")
      handleRecipeRequest(tokens)
    case "trivia" =>
      println("I don't have trivia yet, but stay tuned!")
    case "dish_info" =>
      handleDishRequest(tokens)
    case "search_by_ingredient" => 
      println("going there")
      //handleSearchByIngredient(Left(tokens))
    case "unknown" => // yerg3 llchat tany 
      val corrected = Typos.handleTypos(input)
      println(s"Did you mean: $corrected? Or try asking about a cuisine or type 'quiz'.") //need to handle this type pf error 
    }
  }
  def handleCuisineRequest(tokens: List[String]): Unit = {
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

  // 4. Check for ingredient-based requests using the same pattern
  val ingredientMatch = tokens.collectFirst {
      case t => allDishes.find(_.ingredients.exists(i => 
        Typos.handleTypos(i) == Typos.handleTypos(t)))
    }.flatten

    if (ingredientMatch.isDefined) return (ingredientMatch, "ingredient")

  // 5. Try partial matches as fallback / when no exact matches are found 
    val partialMatch = tokens.collectFirst {
      case t => allDishes.find(d => 
        d.name.toLowerCase.contains(Typos.handleTypos(t)) ||
        d.ingredients.exists(i => i.toLowerCase.contains(Typos.handleTypos(t))))
    }.flatten  //tries to find partial dish match or partical dish match

    partialMatch match {
      case Some(d) => (Some(d), "partial")
      case None => (None, "unknown")
    }
}

def handleRecipeRequest(tokens: List[String]): Unit = 
  {
    val (dish, searchType) = analyzeRecipeRequest(tokens)

    dish match {
      case Some(foundDish) =>
        showRecipe(foundDish)
        //askForFeedback(foundDish.name)
        // Could save user preference here if needed
      case None =>
        searchType match {
          case "ingredient" | "ingredients" => // Handle both singular and plural
              if (tokens.isEmpty) {
                println("Please specify an ingredient to search for")
              } else {
                println(s"🔍 Searching for dishes with: ${tokens.mkString(", ")}")
                
                val foundDishes = tokens.flatMap { token =>
                  FoodDB.findDishesByIngredient(token)
                }.distinct

                if (foundDishes.nonEmpty) {
                  println("🍴 Found these dishes:")
                  foundDishes.foreach { dish =>
                    val matchingIngredients = dish.ingredients.filter { ing =>
                      tokens.exists(t => 
                        Typos.handleTypos(ing.toLowerCase).contains(Typos.handleTypos(t.toLowerCase))
                      )
                    }
                    println(s"🍽️  ${dish.name} (contains: ${matchingIngredients.mkString(", ")})")
                  }
                  
                  println("\nWould you like to:")
                  println("1. See a specific recipe")
                  println("2. Search again")
                  println("3. Return to main menu")
                  
                  readLine().trim match {
                    case "1" =>
                      println("Enter the exact dish name:")
                      handleUserInput(readLine())
                    case "2" =>
                      println("Enter new ingredient to search:")
                      handleUserInput(readLine())
                    case _ => // Return to main menu
                  }
                } 
                else {
                  println(s"😕 No dishes found with ${tokens.mkString(" or ")}")
                  
                  // Show similar ingredients
                  val allIngredients = FoodDB.getAllDishes.flatMap(_.ingredients).distinct
                  tokens.foreach { token =>
                    val similar = allIngredients.filter(_.toLowerCase.contains(token.toLowerCase))
                    if (similar.nonEmpty) {
                      println(s"Did you mean: ${similar.take(3).mkString(", ")}?")
                    }
                  }
                }
              }

          case "vegetarian" =>
              val vegDishes = FoodDB.getVegetarianDishes
              if (vegDishes.isEmpty) {
                println("🌱 We currently don't have any vegetarian dishes in our database.")
              } else {
                println("🌱 Here are our vegetarian dishes:")
                vegDishes.foreach(d => println(s"🍽️  ${d.name} (Ingredients: ${d.ingredients.mkString(", ")})"))
                
                println("\nWould you like:")
                println("1. See a specific recipe")
                println("2. Get a random vegetarian suggestion")
                println("3. Go back to main menu")
                
                readLine().trim match {
                  case "1" =>
                    println("Which recipe would you like? (Type the exact name)")
                    handleUserInput(readLine())
                  case "2" =>
                    val randomDish = scala.util.Random.shuffle(vegDishes).head
                    showRecipe(randomDish)
                  case _ =>
                    println("Returning to main menu...")
                }
              }

            
          case "unknown" =>
            println(s"🔍 I couldn't find '${tokens.mkString(" ")}'. Did you mean one of these?")
            showSimilarDishes(tokens)
            println("Please try again or type 'help' for options")
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
    println("\nWould you like to see a recipe for one of these? (Type the dish name or 'no')")
    val userChoice = readLine().trim
    
    if (userChoice.nonEmpty && Typos.handleTypos(userChoice) != "no") {
      // Find the selected dish (with typo handling)
      val selectedDish = similar.find(_.name.equalsIgnoreCase(userChoice))
        .orElse(allDishes.find(_.name.equalsIgnoreCase(userChoice)))
      
      selectedDish match {
        case Some(dish) => showRecipe(dish)
        case None => println("⚠️ Dish not found. Please try again with the exact name.")
      }
    } else {
      println("Okay, let me know if you change your mind!")
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
      println(s"Sorry, no information available for $cuisine cuisine.")
  }
}
  // Function to handle dish requests and show details
  // This function will be called when the user asks about a dish
  def handleDishRequest(tokens: List[String]): Unit = {
  // Try to find dish using the tokens first
  FoodDB.getDish(tokens) match {
    case Some(dish) =>
      showDishDetails(dish)
    
    case None =>
      // If no dish found, show helpful suggestions
      println(s"\nI couldn't find '${tokens.mkString(" ")}'. Here are some options:")
      
      // Show dishes that contain any of the tokens (up to 5)
      val suggestions = FoodDB.getAllDishes.filter { dish =>
        tokens.exists(token => dish.name.toLowerCase.contains(token.toLowerCase))
      }.take(5)
      
      if (suggestions.nonEmpty) {
        println("\nSimilar dishes:")
        suggestions.foreach(dish => println(s"- ${dish.name}"))
      } else {
        // Fallback to showing some random dishes if no suggestions
        println("\nSome available dishes:")
        FoodDB.getAllDishes.take(5).foreach(dish => println(s"- ${dish.name}"))
      }
      
      // Get user input with prompt
      print("\nWhich dish would you like to know about? > ")
      val dishName = readLine().trim.toLowerCase
      val correctedDishName = Typos.handleTypos(dishName)
      
      // Try again with the corrected input
      if (correctedDishName.nonEmpty) {
        FoodDB.getDish(List(correctedDishName)) match {
          case Some(dish) => showDishDetails(dish)
          case None => println(s"\nSorry, I still couldn't find '$correctedDishName'")
        }
      }
  }
}
def handleSearchByIngredient(tokens: String): Unit = {
  // 1. Normalize and clean the input tokens
  def handleSearchByIngredient(tokensOrInput: Either[List[String], String]): Unit = {
  // Determine if we received pre-parsed tokens or raw input
  val ingredients = tokensOrInput match {
    case Left(tokens) => 
      // Already tokenized input (from initial search)
      tokens.map(Typos.handleTypos).map(_.toLowerCase.trim).filter(_.nonEmpty).distinct
    
    case Right(input) =>
      // Raw input (from case "2" - search again)
      input.split(",")
        .map(_.trim)
        .filter(_.nonEmpty)
        .flatMap { ingredient =>
          if (ingredient.contains(" ")) List(ingredient)
          else ingredient.split("\\s+").toList
        }
        .map(Typos.handleTypos)
        .map(_.toLowerCase.trim)
        .distinct
        .toList
  }

  if (ingredients.isEmpty) {
    println("Please specify at least one ingredient to search for.")
    return
  }

  // Rest of the function remains the same...
  println(s"\n🔍 Searching for dishes containing: ${ingredients.mkString(", ")}")

  val exactMatches = FoodDB.getAllDishes.filter { dish =>
    ingredients.forall(ing => 
      dish.ingredients.exists(dishIng => 
        Typos.handleTypos(dishIng.toLowerCase) == ing
      )
    )
  }
  // 3. Find partial matches (ANY ingredients) if no exact matches
  val partialMatches = if (exactMatches.isEmpty) {
    FoodDB.getAllDishes.filter { dish =>
      ingredients.exists(ing => 
        dish.ingredients.exists(dishIng => 
          dishIng.toLowerCase.contains(ing)
      ) )
    }
  } else {
    Nil
  }

  // 4. Display results
  if (exactMatches.nonEmpty) {
    println("\n🍽️ Dishes containing ALL these ingredients:")
    exactMatches.foreach { dish =>
      println(s"- ${dish.name} (${dish.ingredients.mkString(", ")})")
    }
  } else if (partialMatches.nonEmpty) {
    println("\n🍽️ Dishes containing SOME of these ingredients:")
    partialMatches.foreach { dish =>
      val matchingIngredients = ingredients.filter(ing => 
        dish.ingredients.exists(dishIng => dishIng.toLowerCase.contains(ing)))
      println(s"- ${dish.name} (contains: ${matchingIngredients.mkString(", ")})")
    }
  } else {
    println("\n😕 No dishes found with these ingredients.")
    // Show similar ingredients
    val allIngredients = FoodDB.getAllIngredients
    ingredients.foreach { ing =>
      val similar = allIngredients.filter(_.toLowerCase.contains(ing)).take(3)
      if (similar.nonEmpty) {  // Fixed condition
        println(s"  Similar ingredient: ${similar.mkString(", ")}")
      }
    }
  }

  // 5. Follow-up interaction (for both exact and partial matches)
  if (exactMatches.nonEmpty || partialMatches.nonEmpty) {
    println("\nWould you like to:")
    println("1. See a recipe for one of these dishes")
    println("2. Search again")
    println("3. Return to main menu")
    
    readLine().trim match {
      case "1" =>
        println("Enter the dish name you're interested in:")
        val dishName = readLine()
        FoodDB.getDish(List(dishName)) match {
          case Some(dish) => showRecipe(dish)
          case None => println("Dish not found. Please try again.")
        }
      case "2" =>
        println("Enter new ingredients to search (separate with commas for multi-word ingredients):")
        val newInput = readLine()  // Store the input first
        if (newInput.nonEmpty) {
          handleSearchByIngredient(Right(readLine()))
        } else {
          println("No ingredients entered. Returning to main menu...")
        }
      case _ => 
        println("Returning to main menu...")
    }
  } else {
    // Only for no matches case
    println("\nWould you like to:")
    println("1. Search again")
    println("2. Return to main menu") 
    
    readLine().trim match {
      case "1" =>
        println("Enter new ingredients to search (separate with commas for multi-word ingredients):")
        val newInput = readLine()
        if (newInput.nonEmpty) {
          handleSearchByIngredient(Right(readLine()))
        } else {
          println("No ingredients entered. Returning to main menu...")
        }
      case _ =>
        println("Returning to main menu...")
    }
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
    println("I didn't understand that. Please answer with 'yes' or 'no'.") // need to rewrite this part
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
    println("Okay, let me know if you need anything else!")
  } else if (corrected == "yes" || corrected == "y"|| corrected == "sure"|| corrected == "yep"|| corrected=="please") {
    println(s"Recipe link: ${dish.recipeLink}")
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
