import java.time.LocalTime
import scala.io.StdIn.readLine
import scala.util.Random
import scala.annotation.tailrec
import scala.util.{Try, Success, Failure}
import java.nio.file.{Paths, Files}
import javax.sound.sampled.{AudioSystem, Clip}
import java.io.File
object UserState {
  // This object is used to store user preferences and last trivia cuisine
  // It contains methods to set and get the user's name and last trivia cuisine
private var userName: String = "friend"
  private var lastTriviaCuisine: String = "" // this is used if user responded to trivia request if he wants to take a quiz or not or want to take it later 
  def setName(name: String): Unit = {
    userName = name
    println(s"Got it! I'll call you $name from now on.")
  }
  def setLastTriviaCuisine(cuisine: String): Unit = {
    lastTriviaCuisine = cuisine
  }
  def getName: String = userName
  def getLastTriviaCuisine: String = lastTriviaCuisine

}
object Personality {
  private val greetings = List(  // List of greetings
  "Hello, my friend! What can I cook up for you today? 🍽️",
  "Hey there!",
  "Hello! Ready to chat about food?",
  "Salam! What dish are you craving today?",
  "Ciao bello! Want to cook up something delizioso today? 🇮🇹",
  "Ahla w sahla! What's on your plate today? Mezze? Croissant? Cannoli? 🥐🍰",
  "Bonjour! Let's turn your cravings into cuisine 😍",
  "Yo foodie! What's cooking? 😋",
  "Greetings, fellow flavor explorer! 🌍🍲",
  "Knock knock... who's hungry? 😄",
  "Hey hey! If you could eat *anything* right now, what would it be?",
  "Beep boop! Foodbot online and ready to serve! 🤖🍴",
  "Sup, snack seeker? 😏 What's tempting your tastebuds today?",
  "I'm back—and so is your appetite! What shall we discuss? 😁",
  "Habibi, you came to the right kitchen. What are we cooking today? 🍳",

)

  val respondtohiw = List(   // List of responses to "How are you?"
  "I'm doing great, thanks for asking! How about you? 😊",
  "Feeling fantastic! What about you? 😄",
  "Just peachy! How's your day going? 🍑",
  "I'm just about keeping it together 😂 but actually doing great!",
  "I'm doing well, thanks! How about you? 😊",
  "Beep boop 🤖 All systems operational. How's human life treating you?",
  "I'm running at 100%—unless you count emotional RAM 😂 How about you?",
  "I'm feeling like a Friday at 5pm 🕔—how's your vibe?",
  "Doing so well I might need a software update to handle it! 😁",
  "Running smooth… until someone asks me to do math 🫠",
  "Living the dream… or at least a very convincing simulation! 😉",
  "I'm well, thanks! Now processing your feelings... complete! How are you? 😜",
  "Not sure if caffeinated or just hyper-coded today ☕🤖 How's your energy?"
)

   val jokes = List( // List of jokes
    "Why did the tomato turn red? Because it saw the salad dressing! 🍅",
    "What do you call cheese that isn't yours? Nacho cheese! 🧀",
    "Why don't eggs tell each other secrets? Because they might crack up! 🥚",
    "I'm on a seafood diet. I see food and I eat it. 😆",
    "ليش الفول بيزعل؟ عشان محدش بيقوله بحبك من غير عيش 🤣",
    "ليه الجبنة ما بتلعبش كورة؟ عشان دايمًا بتقع في الشباك! 🧀⚽",
    " 🤣 مره لمونة جريت مالجامع كانت خايفة من صلاة العصر",
    " Soda مره بيبسى و كوكاكولا اتخانقوا واحده قالت للتانية هخلى ليلتك",
    "Why did the hummus break up with the pita? It found someone a little more 'dip'-licious!",
    "Why did the Lebanese chef get kicked out of school? Because he couldn't stop 'spicing' things up!",
    "Why did the tabouleh go to therapy? It had too many 'parsley' issues!",
    "Why did the kimchi break up with the tofu? It found someone a little more 'fermented'!",
    "Why did the rice cake go to therapy? It had too many 'sticky' issues!"

  )
//respond to greeting
  def respondToGreeting(): Unit = {
    println(greetings(scala.util.Random.nextInt(greetings.length)))
  }
//respond to tell joke
  def tellJoke(): Unit = {
    println(jokes(scala.util.Random.nextInt(jokes.length)))
  }

//respond to how are you 
  def askHowUserIs(): Unit = {
    println(respondtohiw(scala.util.Random.nextInt(respondtohiw.length)))
  }
}

object Main  {
  def playStartupSound(): Unit = {
    Try {
      val soundPath = "C:\\Users\\Mira\\Desktop\\uni\\year 2\\Semster 2\\Advanced Prog\\Project\\chatbot\\src\\main\\scala\\data\\Intro.wav"  // Replace with actual path
      val audioInputStream = AudioSystem.getAudioInputStream(new File(soundPath))
      val clip = AudioSystem.getClip()
      clip.open(audioInputStream)
      clip.start()
    }.recover {
      case e: Exception => 
        println("🔇 Couldn't play startup sound: " + e.getMessage)
    }
  }
  val rules = Prase.loadRules("C:\\Users\\Mira\\Desktop\\uni\\year 2\\Semster 2\\Advanced Prog\\Project\\chatbot\\src\\main\\scala\\data\\command.csv")
  implicit val parseForAnalytics: String => (String, List[String]) = Prase.parseInput(rules)
  
  //Function Welcome the user and greet him
  def greetUser(): String = {
  val chefHat = "👨‍🍳"
  val sparkles = "🌟"
  val fire = "🔥"
  val wave = "👋"

  s"""$wave Come habibi! It's Chef Sherbyny here—welcome to my kitchen! 🍳
     |I've got fun facts, tasty tips, and cool stories
     |So, what's on your mind? Ask me anything—or type 'quiz' if you're feeling adventurous!  $fire""".stripMargin
}
// Function to show dish details for a specific cusine
  def showDishes(category: String): Unit = {
    
     val userName = UserState.getName
      val capitalizedCategory = category.capitalize
     Analytics.updateUserSearchLog(userName, s"Cuisine:${category.capitalize}")

  // Log the user's category search
      Analytics.logInteraction(
    s"User requested dishes in category: $capitalizedCategory",
    "Searching for dishes in this category",
    userName
  ) 
      val dishes = FoodDB.getDishesByCategory(category)
      if (dishes.isEmpty) {
        Analytics.logInteraction(
        s"No dishes found in category: $capitalizedCategory",
        "Displayed no-results message",
        userName
      )
        println(s"No dishes found for $category cuisine")
      } else {
      println(s"\n=== ${category.capitalize} Dishes ===")
      dishes.foreach { dish =>
        val vegStatus = if (dish.isVegetarian) "Vegetarian" else "Non-vegetarian"
        println(s"◎ ${dish.name} ($vegStatus)")
        println(s"description: ${dish.shortDescription}")
      }
    }
  }
    // Function to handle user input and provide responses
    // This function will be called when the user provides input
    // It will parse the input and check for valid commands   
  
  def handleUserInput(input: String): Unit = 
  {
  val (command, tokens) = Prase.parseInput(rules)(input)
  //println( s" DEBUG from filess:Command: $command, Tokens: ${tokens}") // Debugging line to check command and tokens
  command match {
    case "bye" =>   // Exit command
      
      println(" ")
    
    case "quiz" =>   // Quiz command
      
      if (tokens.isEmpty) {
        if (UserState.getLastTriviaCuisine.nonEmpty && tokens.isEmpty) {  // if user has asked about specific cusine trivia before
          println(s"Let's test your knowledge about ${UserState.getLastTriviaCuisine.capitalize} cuisine.")
          Quizez.startquiz(UserState.getLastTriviaCuisine, Typos.handleTypos)
        }
        else if (UserState.getLastTriviaCuisine.isEmpty) { // if empty start a general category quiz
          Quizez.startquiz("general", Typos.handleTypos)
        }
      }else {
        Quizez.startquiz(tokens.mkString(" "), Typos.handleTypos)  // or use the tokens provided by the user
      }
    case "Cusine" => // Cuisine command
      //println("DEBUG:Going to cusine")
      if (tokens.isEmpty) {
        println("Please specify a cuisine category!")
      } else {
        val category = tokens.mkString(" ")
        showCuisineInformation(category)
      }
    case "recipe" => //recipe command
     if (isIngredientSearch(tokens)) {   // Check if the user is looking for a specific ingredient
        handleIngredientSearchResults(tokens) // If so, handle it
      }else {
        handleRecipeRequest(tokens) //else handle the recipe request
      }
    case "ingredients" =>  // Ingredients command
      //println("Debug: Going to ingredients")
      handleIngredientSearchResults(tokens) // Handle ingredient search
    case "trivia" => // Trivia command
      TriviaRequest(tokens.mkString(" ").toLowerCase) // Show trivia for the specified cuisine

    case "dish_info" => handleDishRequest(tokens) // Handle dish information request

    case "greet" =>  Personality.respondToGreeting()   // Respond to greeting

    case "ask_how_user_is" => Personality.askHowUserIs() // respond to how are you

    case "preference" => preference(tokens) // Handle user preferences

    case "log" =>  // used if he were asked to take a quiz after getting less than 40 % or after being asked for triviaa request and he said yes
      //println("Debug:Going to log")
      val logs = Analytics.getInteractionLog()
        logs.lastOption match {
          case Some(log) =>
            Analytics.processLastLog(log,tokens,Typos.handleTypos)
          case None =>
            println("No interactions logged yet.")
  }
    case "joke" =>  Personality.tellJoke()  // Tell a joke

    case "unknown" => // yerg3 llchat tany 
      val corrected = Typos.handleTypos(input) 
      println(s"Did you mean: $corrected? Or try asking about a cuisine or type 'quiz'.") 
    }
  }

  //Function shows the trivia / fun fact about the cusine
  def TriviaRequest(cuisine: String): Unit = {
  val userName = UserState.getName
      Analytics.updateUserSearchLog(userName, s"Cuisine:${cuisine.capitalize}")
  // Log the trivia request initiation
  Analytics.logInteraction(
    s"User requested trivia for: ${cuisine.capitalize} cuisine",
    "Searching for trivia fact",
    userName
  )
  val trivia =FoodDB.getRandomTrivia(cuisine)  // Call the trivia function

  trivia match {
  case Some(triviaFact) =>  
     // Log successful trivia found
      Analytics.logInteraction(
        s"Trivia found for: ${cuisine.capitalize}",
        s"Trivia fact: $triviaFact",
        userName
      )
      UserState.setLastTriviaCuisine(cuisine)

    println(s"""
      |✨*Did you know?* ✨
      |$cuisine cuisine is known for its:
      |${"-" * (cuisine.length + 12)}
      |💡 $triviaFact
      |Pretty fascinating, right!
      |would you like a quiz to test ur knowledge?😊  
      |""".stripMargin)
 // this is where last trivia cusine orlog needed for !
 
  case None =>
    // Log no trivia found
     Analytics.logInteraction(
        s"No trivia found for: ${cuisine.capitalize}",
        "Suggested other available cuisines",
        userName
      )
    println(s"""
      |🤖 *Chef's Note* 🤖
      |Hmm, my recipe books don't seem to have fun facts about $cuisine...
      |But I know lots about:
      |${FoodDB.categories.map(_.name.capitalize).mkString(" • ")}
      |So, how about we explore one of those instead?
      |""".stripMargin)
}
}
// Function to show user prefrences
  def preference(tokens: List[String]): Unit = {
  val userName = UserState.getName
  val input = tokens.mkString(" ").toLowerCase

  if (tokens.isEmpty) {
    showCurrentPreferences(userName)
  } else {
    handlePreferenceUpdate(userName, tokens)
  }
}
  //function to show if user has any stored preferences
  def showCurrentPreferences(userName: String): Unit = {
  Analytics.getUserPreferences(userName) match {
    case Some(prefs) if prefs.nonEmpty =>
      val formatted = prefs.split(";").map(_.trim).mkString("\n- ")
      println(s"🌟 Your current preferences:\n- $formatted")
    case _ =>
      println("You haven't set any preferences yet. Try commands like:")
      println("- 'I love Egyptian cuisine'")
      println("- 'Add Koshari to my favorites'")
      println("- 'Save pasta as my favorite dish'")
  }
}
// Function to handle update or save from scartch user preferences

  def handlePreferenceUpdate(userName: String, tokens: List[String]): Unit = {
  val corrected = tokens.map(t => Typos.handleTypos(t.toLowerCase))
  val possibleCuisine = corrected.find(t => FoodDB.isValidCuisine(t))
  val bestDishMatch = FoodDB.findBestDishMatch(corrected)

  (possibleCuisine, bestDishMatch) match {
    case (Some(cuisine), None) => 
      Analytics.storeUserPreferences(userName, s"Favorite Cuisine: ${cuisine.capitalize}")
            println(s"🌍 Wonderful! I've noted your love for ${cuisine.capitalize} cuisine.")
            
            // Suggest dishes from this cuisine
            val dishes = FoodDB.getDishesByCategory(cuisine.toString()).take(3)
            if (dishes.nonEmpty) {
              println(s"\nYou might enjoy these ${cuisine.capitalize} dishes:")
              dishes.foreach(d => println(s"- ${d.name}"))
              println("\nAsk me about any of them!")  
            }

    case (None, Some(dish)) =>
      Analytics.storeUserPreferences(userName, s"Favorite Dish: ${dish.name}")
            println(s"🍽️ Excellent choice! I've added ${dish.name} to your favorites.")
            // Show dish details if they want
            println(s"Would you like to see details about ${dish.name}? (yes/no)")
            val input = readLine().trim.toLowerCase 
            val corrected = Typos.handleTypos(input)
            if(corrected.contains("yes")|| corrected.contains("please")||corrected.contains("sure")) 
              showDishDetails(dish)
              else println("No problem! It's saved for future reference.")

    case (Some(cuisine), Some(dish)) =>
      println(s"🍽️ I've noted your love for ${cuisine.capitalize} cuisine and ${dish.name} dish.")
            Analytics.storeUserPreferences(userName, s"Favorite Cuisine: ${cuisine.capitalize}")
            Analytics.storeUserPreferences(userName, s"Favorite Dish: ${dish.name}")
            println("Would you like to see details about this dish? (yes/no)")
            val input = readLine().trim.toLowerCase 
            val corrected = Typos.handleTypos(input)
            if(corrected.contains("yes")|| corrected.contains("please")||corrected.contains("sure")) 
              showDishDetails(dish)
              else println("No problem! It's saved for future reference.")
    case (None, None) =>
      println("I couldn't identify a valid cuisine in your message. Available cuisines are:")
      println(FoodDB.categories.map(_.name.capitalize).mkString(", "))
      println("I couldn't find that dish or that cusine in our database. Try something like:")
      println("- 'Add Koshari to my favorites'")
      println("- 'Save Italian cusine as my favorite'")
  }
}


  // Function to check if the user is looking for a specific ingredient or not
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
  //Function to get list of dishes by ingredient and matchtype 
  // it takes a list of tokens and returns a list of dishes that match the tokens
  // it also handles typos in the ingredient names
  // it also handles multi-word ingredients
  def searchByIngredient(tokens: List[String]): (List[Dish], String) = {
    if (tokens.isEmpty) return (Nil, "no-ingredients-specified")
    
    val allDishes = FoodDB.getAllDishes
    val normalizedTokens = tokens.map(t => Typos.handleTypos(t.toLowerCase))
    
    // Detect multi-word ingredients and filter out those from tokens
    val detectedPhrases = FoodDB.detectMultiWordIngredients(tokens)
    val searchTerms = detectedPhrases ++ tokens.filterNot(token => 
        detectedPhrases.exists(_.split(" ").contains(token))
    )
    val normalizedTerms = searchTerms.map(t => Typos.handleTypos(t.toLowerCase))



    // Precompute normalized ingredients for each dish
    val dishesWithNormalized = allDishes.map { dish =>
      (dish, dish.ingredients)
    }

    // Check matches in order of specificity
    val exactAll = dishesWithNormalized.filter { case (_, dishIngs) =>
        normalizedTerms.forall(term => dishIngs.contains(term))
    }
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
  //function to check if user want the ingredients of a specific dish and return boolean 
  def dish_ingredients(tokens:List[String]):Boolean=
    {
      FoodDB.getDish(tokens) match {
      case Some(dish) =>
        Analytics.updateUserSearchLog(UserState.getName, s"Dish:${tokens.mkString(" ").capitalize}")
        println(s"\n◎ ${dish.name}: ${if (dish.isVegetarian) "Vegetarian" else "Non-vegetarian"}")
        println(s"   Ingredients: ${dish.ingredients.mkString(", ")}")
        println(s"Would you like to see the recipe for this dish?")
        val answer = scala.io.StdIn.readLine().trim.toLowerCase
        val corrected = Typos.handleTypos(answer)
        if (Set("yes", "sure", "ok", "please").contains(corrected)) {
          showRecipe(dish)
          
        } else {
          println("No problem! Let me know if you need another dish or ingredients.")

        }
        true
      case None => false    // No exact dish match found
}

    }
  
  //Function that handle indgredient coomand
  // serach for ingredients with a specific ingredients ( one or more ingredient )
  // it handle multiwprded ingredients and typos
  def handleIngredientSearchResults(tokens: List[String]): Unit = {
    val userName = UserState.getName
    val searchQuery = tokens.mkString(" ")
    
    if(dish_ingredients(tokens)) return // If a dish is found, exit the function

    // Log the initial ingredient search
    

    Analytics.logInteraction(
      s"Ingredient search: $searchQuery",
      "Processing ingredient search request",
      userName
    )
    val ingredients = tokens.filter { token =>
    val normalizedToken = Typos.handleTypos(token.toLowerCase)
    FoodDB.getAllIngredients.exists { ing =>
      Typos.handleTypos(ing.toLowerCase) == normalizedToken
      }
    }
    // Check if any ingredients were found
    if (ingredients.isEmpty) {
      // Log failed ingredient recognition
      Analytics.logInteraction(
        s"Ingredient search: $searchQuery",
        "No recognized ingredients found",
        userName
      )
      println("I couldn't identify any ingredients in your request, maybe rephrseas " + tokens.mkString(" "))
      return
    }
    //search for dish list and match type (exact, partial, etc.)
    val (dishes, matchType) = searchByIngredient(tokens)

    // Log search results
    Analytics.logInteraction(
      s"Ingredient search results for: ${ingredients.mkString(", ")}",
      s"Found ${dishes.size} dishes (match type: $matchType)",
      userName
    )

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
          s"   Ingredients: ${d.ingredients.mkString(", ")}}"
        ).mkString("\n\n")}
      |
      |Type the exact name of the dish you want, or 'none' to cancel.
      |""".stripMargin)

    // Handle user selection if he wants a specific dish from printetd list 

    val selection = Typos.handleTypos(scala.io.StdIn.readLine().trim.toLowerCase)
    if(selection.contains("none")||selection.contains("no")||selection.contains("exit")||selection.contains("cancel")||selection.contains("thanks")||selection == "") {
      println("No problem! Let me know if you need anything else.")
      return
    }
    else{
    selection match {
      case name if dishes.exists(_.name.toLowerCase == name) =>
        showRecipe(dishes.find(_.name.toLowerCase == name).get)
      case _ =>
        println(s"Sorry, I didn't find '$selection' in the results. Please try again.")
        handleIngredientSearchResults(tokens) // Retry
    }
  }
}

  // Function to analyze recipe requests and return dish or vegetarian request
  // This function checks if the user is looking for a specific dish or vegetarian options
  // handle mu;tiworded dish names and typos
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

    // 3. Check for vegetarian requests 
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
  // It will analyze the request and show the recipe or suggest alternatives
  // It will also log the interaction for analytics
  // It will also handle vegetarian requests and show vegetarian options
  def handleRecipeRequest(tokens: List[String]): Unit = {
    val userName = UserState.getName
    val searchQuery = tokens.mkString(" ")
    
    // Log the initial recipe search
    Analytics.updateUserSearchLog(userName, s"Dish:${searchQuery.capitalize}")
    Analytics.logInteraction(
      s"Recipe search: $searchQuery",
      "Processing recipe request",
      userName
    )

    // Check if the user is looking for a specific dish or vegetarian options
    val (dish, searchType) = analyzeRecipeRequest(tokens)
  
    dish match {
      case Some(foundDish) =>
        // Log successful recipe found
        Analytics.logInteraction(
          s"Found recipe: ${foundDish.name}",
          s"Showing recipe for ${foundDish.name}",
          userName
        )
        showRecipe(foundDish)
        dish_suggestion()
      
      case None =>
        // Log no direct match found
        Analytics.logInteraction(
          s"No direct match for: $searchQuery",
          s"Trying alternative search type: $searchType",
          userName
        )
        
        searchType match {
          case "vegetarian" =>
            // Log vegetarian search
            Analytics.logInteraction(
              "Vegetarian search",
              "Looking for vegetarian options",
              userName
            )
            //check if its specific cusine or not
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
              
              // Log no vegetarian dishes found
              Analytics.logInteraction(
                "Vegetarian search",
                "No vegetarian dishes available",
                userName
              )
              
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
              
              // Log vegetarian dishes found
              Analytics.logInteraction(
                "Vegetarian search",
                s"Found ${vegDishes.size} vegetarian options",
                userName
              )
              // Show a 5 random selection of vegetarian dishes
              println(header)
              Random.shuffle(vegDishes).take(5).foreach { d =>
                println(s"🍽  ${d.name} (${d.ingredients.take(5).mkString(", ")}${if (d.ingredients.size > 5) "..." else ""}")
              }
              
              if (vegDishes.size == 1) showRecipe(vegDishes.head)
            }
            
            println("🌱Type the exact name of the dish you want, or 'none' to cancel.🌱")
            val selection = Typos.handleTypos(scala.io.StdIn.readLine().trim.toLowerCase)
            
            // Log user selection
            Analytics.logInteraction(
              "User vegetarian dish selection",
              s"Selected: $selection",
              userName
            )
            
            selection match {
              case name if vegDishes.exists(_.name.toLowerCase == name) =>
                val selectedDish = vegDishes.find(_.name.toLowerCase == name).get
                Analytics.logInteraction(
                  s"Selected vegetarian dish: ${selectedDish.name}",
                  "Showing recipe",
                  userName
                )
                showRecipe(selectedDish)
                dish_suggestion()
                
              case "none" | "no" | "exit" | "cancel"|"non"|"nonee"|"nun"| "thanks"|" " =>
                Analytics.logInteraction(
                  "User canceled vegetarian search",
                  "No dish selected",
                  userName
                )
                println("No problem! Let me know if you need anything else.")
                

              case _ =>
                Analytics.logInteraction(
                  "Invalid vegetarian dish selection",
                  s"User entered: $selection",
                  userName
                )
                println(s"Sorry, I didn't find '$selection' in the results. Please try again.")
            }
            
            
          case "ingredient" =>
            Analytics.logInteraction(
              "Falling back to ingredient search",
              "Redirecting to ingredient search",
              userName
            )
            handleIngredientSearchResults(tokens)
            
          case "unknown" | _ =>
            Analytics.logInteraction(
              "Unknown recipe request",
              s"Could not process: ${tokens.mkString(" ")}",
              userName
            )
            println(s"I couldn't find any recipes matching '${tokens.mkString(" ")}'")
            showSimilarDishes(tokens)
        }
    }
}
  //when no dish is found , i can suggest those dishes

  def showSimilarDishes(tokens: List[String]): Unit = {
  val userName = UserState.getName
  val searchQuery = tokens.mkString(" ").toLowerCase
  val allDishes = FoodDB.getAllDishes

  // 🔍 Log dish search intent
  Analytics.updateUserSearchLog(userName, s"Dish:${searchQuery.capitalize}")

  val similar = allDishes.filter { dish =>
    tokens.exists(t =>
      dish.name.toLowerCase.contains(Typos.handleTypos(t)) ||
      dish.ingredients.exists(i =>
        i.toLowerCase.contains(Typos.handleTypos(t)))
    )
  }

  if (similar.nonEmpty) {
    val suggestions = similar.take(5).map(_.name).mkString(", ")
    println("🔍 I found these similar dishes:")
    similar.take(5).foreach(d => println(s"👉 ${d.name}"))

    // ✅ Log successful similarity suggestions
    Analytics.logInteraction(
      s"Search: $searchQuery",
      s"Similar dishes suggested: $suggestions",
      userName
    )
  } else {
    val fallbackSuggestions = Random.shuffle(allDishes).take(5).map(_.name).mkString(", ")
    println("🌟 Here are some popular dishes:")
    Random.shuffle(allDishes).take(5).foreach(d => println(s"👉 ${d.name}"))

    // ❌ Log fallback due to no matches
    Analytics.logInteraction(
      s"Search: $searchQuery",
      s"No matches found. Fallback suggestions: $fallbackSuggestions",
      userName
    )
  }

  // Prompt for follow-up
  println("\n👨‍🍳 Would you like to learn how to cook any of these dishes? Type the name or 'no' to explore something else! 🍳")
  val userChoice = scala.io.StdIn.readLine().trim
  val correctedChoice = Typos.handleTypos(userChoice.toLowerCase)

  // Log user choice
  Analytics.logInteraction(
    s"User input: $userChoice",
    s"Corrected to: $correctedChoice",
    userName
  )

  if (correctedChoice != "no" && correctedChoice.nonEmpty) {
    val selectedDish = similar.find(_.name.equalsIgnoreCase(correctedChoice))
      .orElse(allDishes.find(_.name.equalsIgnoreCase(correctedChoice)))

    selectedDish match {
      case Some(dish) =>
        // ✅ Log selected dish and show recipe
        Analytics.logInteraction(
          s"User selected: $correctedChoice",
          s"Found and displaying recipe: ${dish.name}",
          userName
        )
        showRecipe(dish)

      case None =>
        // ❌ Log dish not found
        Analytics.logInteraction(
          s"User selected: $correctedChoice",
          "Dish not found in suggestions. Prompting again.",
          userName
        )
        println("👨‍🍳 Oops! I couldn't find that dish. Please type the exact name as shown above! 🔍")
    }
  } else {
    // 🙅 Log user exited
    Analytics.logInteraction(
      "User declined to choose a suggested dish",
      "Ending suggestion flow",
      userName
    )
    println("🌟 No problem at all! Feel free to explore other dishes whenever you're ready! 👨‍🍳")
  }
}
  

  // It takes a cuisine name as input and shows the details of that cuisine
  def showCuisineInformation(cuisine: String): Unit = {
  val userName = UserState.getName
  val capitalizedCuisine = cuisine.capitalize
 
  val category = FoodDB.categories.find(_.name.toLowerCase == cuisine.toLowerCase)

  category match {
    case Some(c) =>
      Analytics.updateUserSearchLog(userName, s"Cuisine:$capitalizedCuisine")
      println(s"Details for ${c.name.capitalize} cuisine:")
      val dishes = FoodDB.getDishesByCategory(c.name)
 
      dishes.foreach(d => println(s"- ${d.name} - ${d.shortDescription} " ))
    case None =>
      println(s"👨‍🍳 *Chef's confession*: My $cuisine knowledge is still simmering!")
  }
}
// Function to show details for a specific dish
  //it will be called directly from handleUserInput()
    def handleDishRequest(tokens: List[String]): Unit = {
        val userName = UserState.getName
        val searchQuery = tokens.mkString(" ")
      
      // Log the initial dish search
        Analytics.updateUserSearchLog(userName, s"Dish:${searchQuery.capitalize}")

      
        FoodDB.getDish(tokens) match {
        case Some(dish) =>
          // Log successful dish found
            // Log successful dish found
           Analytics.logInteraction(s"Search for dish: ${tokens.mkString(" ")} Dish", 
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
              |I'm here to help with more tasty dishes.
              |What's next? 🍽️
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
  val userName = UserState.getName

  val dishtype = dish.Dish_Type match {
    case "main"      => "Main Course"
    case "appetizer" => "Appetizer"
    case "dessert"   => "Dessert"
    case "salad"     => "Salad"
    case "soup"      => "Soup"
    case "breakfast" => "Breakfast"
    case _           => "Dish"
  }

  val vegStatus = if (dish.isVegetarian) "Vegetarian" else "Non-vegetarian"

  println(s"\n◎ ${dish.name}: $dishtype ($vegStatus)")
  println(s"   ${dish.shortDescription}")

  println("\nWould you like to see the ingredients?")
  val ingredientAnswer = readLine().trim.toLowerCase
  val correctedIngredient = Typos.handleTypos(ingredientAnswer)

  

  if (Set("yes", "y", "sure", "please").contains(correctedIngredient)) {
    println(s"   Ingredients: ${dish.ingredients.mkString(", ")}")
    Analytics.logInteraction(
      s"Ingredients requested for: ${dish.name}",
      "Showing ingredients",
      userName
    )
    println("\nWould you like to check out the recipe too?")
    val recipeAnswer = readLine().trim.toLowerCase
    val correctedRecipe = Typos.handleTypos(recipeAnswer)


    if (Set("yes", "y", "sure", "please").contains(correctedRecipe)) {
      println("Fetching the recipe...")
      Analytics.logInteraction(
        s"Recipe request accepted for: ${dish.name}",
        "Showing recipe",
        userName
      )
      showRecipe(dish)
    } else {
      println("Alright! Let me know if you get curious or hungry later.")
    }

  } else {
    println("No problem! Let me know if you'd like to explore something else.")
  }
}


// Function to show the recipe for a specific dish
  // It takes a dish object as input and shows the recipe steps
  // It also logs the interaction for analytics
  // It also handles user response for recipe link
  def showRecipe(dish: Dish): Unit = {
    val userName = UserState.getName
    
    // Log recipe viewing start
    Analytics.logInteraction(
      s"Viewing recipe: ${dish.name}", 
      s"Showing recipe steps (${dish.recipeSteps.length} steps)",
      userName
    )

    println(s"\nRecipe for ${dish.name}:")
    dish.recipeSteps.zipWithIndex.foreach { case (step, index) =>
      println(s"${index + 1}. $step")
    }

    // Log recipe steps shown
    Analytics.logInteraction(
      s"Displayed recipe steps for ${dish.name}",
      s"Total steps: ${dish.recipeSteps.length}",
      userName
    )

    println("\nHow about a recipe link to keep the kitchen vibes going?")
    val answer = readLine().trim.toLowerCase
    val corrected = Typos.handleTypos(answer)

    // Log user response for recipe link
    Analytics.logInteraction(
      s"User response for recipe link: $answer",
      s"Corrected to: $corrected",
      userName
    )

    if (corrected.contains("no") || corrected.contains("exit") || corrected.contains( "quit") || corrected.contains("thanks") || answer.isEmpty) {
      // Log user declined recipe link
      Analytics.logInteraction(
        "User declined recipe link",
        "Showing closing message",
        userName
      )
      println("👨‍🍳Mafish mushkila! Wana hena 3ashan nsharek al-wasafat w al-ma3lomat el-laziza!")
    } else if (corrected.contains("yes")|| corrected.contains("sure") || corrected.contains("please")) {
      // Log user requested recipe link
      Analytics.logInteraction(
        "User requested recipe link",
        s"Provided link: ${dish.recipeLink}",
        userName
      )
      println(s"📜 Recipe link: ${dish.recipeLink}")
      
      // Log recipe completion
      Analytics.logInteraction(
        s"Completed recipe viewing: ${dish.name}",
        "User received all recipe information",
        userName
      )
    } else {
      // Log invalid response
      Analytics.logInteraction(
         s"User entered: $answer",
        "Invalid response for recipe link",
        userName
      )
      println("I didn't understand that. Please type 'yes' or 'no' next time.")
    }
    
    //println("Enjoy your cooking!")
    
    // Log final interaction
    Analytics.logInteraction(
      "Recipe session ended",
      s"Completed interaction for ${dish.name}",
      userName
    )
  }
 
  // Function to greet the user and start the chat
  def mainChat(): Unit = {
    Analytics.loadPreferencesFromFile()
    playStartupSound()
    println(greetUser())
    println("Ooh, what's your name, so I can shout it across the kitchen? 🍳😄")
    val name = readLine().trim
    if (name.nonEmpty) UserState.setName(name)
    chatLoop()
}
  @tailrec
  // Function to handle the chat loop
  // It will keep asking for user input until the user types "bye" or "exit"
  def chatLoop(): Unit = {
    
  print(s"\n${UserState.getName.capitalize}: ")
  val l = readLine().trim.toLowerCase
  val input= Typos.handleTypos(l)
  if (input.isEmpty) {
    println("👨‍🍳 Where'd ya go, chef? Drop me a message—or type 'quit' to hang up the apron")
    chatLoop()

  } else if (input.contains("joke")) {
    Personality.tellJoke()
    chatLoop()

  } 
    else if (input.contains("analytics") || input.contains("show analytics")) {  //TO SHOW ANALYTICS
    
    val userName = UserState.getName
    Analytics.handleUserRequestForAnalytics(userName)
    chatLoop()

  } else if (input.contains("interaction report") || input.contains("usage summary")) {  // to analyze user interactions
    Analytics.analyzeInteractions()
    chatLoop()

  } else if (input.contains("chat log") || input.contains("conversation history") || input.contains("full log")) { // to show all chat log
    val logs = Analytics.getInteractionLog().reverse
    logs.foreach { case (seq, user, bot) =>
      println(s"\n# $seq\n👤: $user\n🤖: $bot")
    }
     println(s"\n🗂 Displayed ${logs.length} interactions.")
    chatLoop()
  }
   else {
    val (command, _) = Prase.parseInput(rules)(input)

    if (command == "bye") {  // Exit command
      Analytics.logInteraction(
        "User exited chat",
        "Goodbye message displayed",
        UserState.getName
      )
      println(s"""
      |👋  See ya later, ${UserState.getName.capitalize}!  
      |Don't be a stranger—come back for:  
      |🍲 Tasty recipes  
      |🌶 Spicy trivia  
      |🍽 And a good ol' foodie laugh! 😄""".stripMargin)
      Analytics.logInteraction(
        "User exited chat",
        "Goodbye message displayed",
        UserState.getName
      )
      Analytics.savePreferencesToFile() // save new prferences user | dish name| cusine name
    } else {
      handleUserInput(input) //start logic for handling user input
      chatLoop()
    }
  }
  }
  // Function to suggest dishes based on user preferences
  // It will show a list of dishes and ask the user if they want to know more
  def dish_suggestion(initialCall: Boolean = true): Unit = 
  {
    val userName = UserState.getName
    val suggestions = FoodDB.getRandomDishSuggestions()
    
    // Log suggestion event
    Analytics.logInteraction(
        "Dish suggestions shown", 
        s"Suggested dishes: ${suggestions.map(_.name).mkString(", ")}", 
        userName
    )

    if (initialCall) {
        println("\nBefore you head out, curious about these dishes? just write down their name  if any of them sound good!!")
    } else {
        println("\nHere are some more suggestions:")
    }
    
    suggestions.foreach(d => println(s"🍽️ ${d.name}"))
    
    println("\nWould you like to know more about any of these?")
    val answer = readLine().trim.toLowerCase
    
    // Log user response
    Analytics.logInteraction(
        "User response to suggestions",
        s"Entered: $answer",
        userName
    )
    
    // Check for explicit exit commands
    val corrected = Typos.handleTypos(answer)
    if (corrected.contains("no") || corrected.contains("exit") || corrected .contains( "quit")||corrected.contains("thanks") || answer .isEmpty)  {
        // Log user exit
        Analytics.logInteraction(
            "User exited suggestions",
            "Ended dish suggestion flow",
            userName
        )
        println("Alright, have a delicious day! 👨‍🍳")
        return
    } 
    
    // Try to find matching dish
    val selectedDish = suggestions.find(d => 
        Typos.handleTypos(d.name.toLowerCase) == corrected
    )
    
    selectedDish match {
        case Some(dish) =>
            // Log dish selection
            Analytics.logInteraction(
                "User selected dish from suggestions",
                s"Selected: ${dish.name}",
                userName
            )
            showDishDetails(dish)
            
            println("\nWould you like another suggestion? (yes/no)")
            val continue = readLine().trim.toLowerCase
            
            // Log continuation response
            Analytics.logInteraction(
                "User continuation choice",
                s"Response: $continue",
                userName
            )
            
            if (Typos.handleTypos(continue) == "yes"|| Typos.handleTypos(continue) =="please"|| Typos.handleTypos(continue) == "sure") {
                dish_suggestion(initialCall = false)
            } else {
                // Log final exit
                Analytics.logInteraction(
                    "User ended suggestions",
                    "Final exit from dish suggestions",
                    userName
                )
                println("Alright, have a delicious day! 👨‍🍳")
            }
            
        case None =>
            // Log unrecognized input
            Analytics.logInteraction(
                "Unrecognized dish input",
                s"User entered: $answer",
                userName
            )
            println(s"Sorry, I didn't recognize '$answer'. Try to rephrase")
            //dish_suggestion(initialCall = false) // Show suggestions again
    }
  }
  def main(args: Array[String]): Unit = {
  
    mainChat()
    
  }
}