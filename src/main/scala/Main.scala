import java.time.LocalTime
import scala.io.StdIn.readLine
object Main  {
  def handleTypos(input: String): String = {
  val commonTypos = Map(
    // General words
    "hello" -> List("helo", "hallo", "heloo", "hel", "elo", "heo", "helloo", "hlo"),
    "hi" -> List("hiii", "hai", "hhi", "hii", "hee", "hy"),
    "how" -> List("hoow", "hwo", "howw", "ho", "howare", "howr"),
    "are" -> List("ar", "re", "rae", "aare", "arre"),
    "you" -> List("yuo", "yo", "yoo", "yu", "youu"),
    "what" -> List("wht", "waht", "wat", "whaat", "whatt", "whut"),
    "is" -> List("si", "iss", "iis", "s", "iz"),
    "the" -> List("teh", "th", "tge", "hte", "thhe", "da"),
    "food" -> List("fod", "foood", "fud", "foood", "fod", "fode"),
    "cuisine" -> List("cusine", "cuisin", "cuisne", "cuisene", "cuisnie", "quisine"),
    "bye" -> List("bi","bey", "byee"),
    "vegetarian" -> List("vegitarian", "vegeterian", "veggie", "vegatarian", "veggitarian"),
    "ingredients" -> List("ingridients", "ingreedients", "ingrediants", "ingridiants", "ingrs"),
    "quiz" -> List("quizz", "quize", "quz", "quizzze", "qiz"),
    "dish" -> List("dsh", "dissh", "dich", "dissh", "dishh"),   
    "egyptian" -> List("egyption","egiptian", "egiptian", "egypitan", "egyptian", "egypitian"),
    "french" -> List("frensh", "frenche", "frnch", "frensh", "frnch"),
    "lebanese" -> List("lebanes", "lebanesee", "lebanes", "lebanessee", "lebanes"),
    "korean" -> List("koreean", "koreaan", "koreean", "koreean", "korean"),
    "quit" -> List("quitt", "quitt", "quitte", "quitt", "quit","quizt","qui","qut"),
    "sure" -> List("shure", "suree", "surr", "shure", "shur"),
    "please" -> List("plese", "pleas", "pleese", "pleas", "plese"),
    "thank" -> List("thx", "thnks", "thanx", "thxk", "thx"),
    "thanks" -> List("thx", "thnks", "thanx", "thxk", "thx"),
    "welcome" -> List("welcom", "welcme", "welcom", "welccome", "welcom"),
    "recipe" -> List("recpie", "recip", "recipie", "recpiee", "recipie","reciepe?", "recipie"),
    "make" -> List("mak", "maek", "maake", "mke", "mae"),
    "cook" -> List("cok", "cokk", "cokie", "cokkk", "cok"),
    "tell" -> List("tel", "tll", "tll", "tll", "tll"),
    "about" -> List("abou", "abaut", "abouut", "abouut", "abou"),
    "yes" -> List("yea", "yess", "yees", "yesss", "ye"),
    "no" -> List("noo", "n", "nooo", "nno", "nnoo"),

    // Egyptian food terms
    "koshari" -> List("koshary", "koshare", "koshari", "kushari", "kosheri"),
    "ful medames" -> List("foul medames", "ful medammes", "fool medames", "ful mudammes"),
    "molokhia" -> List("mulukhiya", "molohiya", "molokhiya", "mulukhia", "melokhia"),
    "taameya" -> List("tameya", "ta'meya", "tamiya", "taameia", "taamya"),
    "mahshi" -> List("mashi", "mahshy", "mahshe", "mashy", "mahshie"),
    "fatta" -> List("fata", "fattah", "fatah", "fatte", "fataa"),
    
    // French food terms
    "ratatouille" -> List("ratatui", "ratatooie", "ratatouile", "ratatouillee", "ratatouil"),
    "coq au vin" -> List("coqauvain", "coq au vain", "coqovin", "coq a vin", "coqauvin"),
    "quiche lorraine" -> List("quish lorraine", "quiche loren", "quiche lorain", "quiche lorrraine"),
    "tarte tatin" -> List("tart tatin", "tarte tatinne", "tarte tatin", "tartetatin", "tart tatin"),
    "croissant" -> List("crescent", "croisant", "cruasant", "croisont", "croissante"),

    // ===== French CUISINE INGREDIENTS =====
    "zucchini" -> List("zuchini", "zukini", "zucini", "zucchinni", "zuchinni"),
    "eggplant" -> List("egplant", "eggplnt", "eggplent", "epplant", "eplant"),
    "tomato" -> List("tomatoe", "tomatto", "tomate", "tomaato", "tomat"),
    "garlic" -> List("garlik", "garlick", "garlc", "garlik", "garlik"),
    "olive oil" -> List("oliveoil", "oliv oil", "olive oyl", "oliveoyl", "oliveoill"),
    "chicken" -> List("chiken", "chickn", "chikken", "chickenn", "chikn"),
    "red wine" -> List("redwine", "red vine", "red whine", "redwien", "redwinee"),
    "mushrooms" -> List("mushroms", "mushromms", "mushrms", "mushrroms", "mushroomss"),
    "bacon" -> List("bacn", "bakon", "bcon", "baccon", "becon"),
    "thyme" -> List("time", "thime", "thym", "thhyme", "thyyme"),
    "butter" -> List("buter", "buttter", "buttar", "buttur", "buterr"),
    "pastry" -> List("pastrey", "pastery", "pasty", "pastri", "paastry"),
    "cream" -> List("creme", "creamm", "creem", "crem", "creaam"),
    "sugar" -> List("suger", "suggar", "sugr", "sugre", "sugarr"),
    "flour" -> List("flower", "flur", "floour", "florr", "flouer"),

    // ===== EGYPTIAN CUISINE INGREDIENTS =====
    "lentils" -> List("lentiles", "lentills", "lentls", "lentilss", "lentl"),
    "chickpeas" -> List("chikpeas", "chickpease", "chickpeeas", "chicpeas", "chikpease"),
    "tomato sauce" -> List("tomatosauce", "tomatoe sauce", "tomatosause", "tomatosauze", "tomatosouce"),
    "fava beans" -> List("fava beens", "favabeans", "fava beens", "fava beanz", "favabeenz"),
    "jute leaves" -> List("juteleaves", "jute leeves", "joot leaves", "jute leavs", "jutleaves"),
    "coriander" -> List("coriandar", "corriander", "coriender", "corriandar", "coriandor"),
    "spices" -> List("spyces", "spices", "spicess", "spycess", "speces"),
    "yogurt" -> List("yoghurt", "yogourt", "yogurt", "yoghourt", "yogurtt"),
    "semolina" -> List("semolena", "semolinaa", "semolinna", "semolna", "semolena"),
    "coconut" -> List("cocnut", "coconutt", "cocconut", "cocunut", "coconutty"),
    "rice" -> List("ryce", "ricce", "ricc", "rize", "ric"),
    "cabbage" -> List("cabage", "cabbege", "cabagee", "cabbege", "cabbbage"), 
    

    // ===== GENERAL INGREDIENTS =====
    "salt" -> List("sault", "saltt", "slt", "sallt", "saalt"),
    "pepper" -> List("peper", "peppar", "peppr", "peppre", "peppur"),
    "onion" -> List("onions", "onionn", "onnion", "onionns", "onion"),
    "rice" -> List("ryce", "ris", "ricee", "ricce", "ryce"),
    "lemon" -> List("lemmon", "lemn", "lemonn", "lemmonn", "lemonjuice"),
    "herbs" -> List("herbbes", "herbes", "herbss", "herbz", "herbbz"),
    "cheese" -> List("cheeze", "chese", "cheesee", "cheez", "chesee"),
    "eggs" -> List("egs", "egges", "eggss", "egggs", "eggsy"),
    "bread" -> List("bred", "breade", "breadd", "breaad", "brad"),
    "carrots" -> List("carots", "carrotts", "carotts", "carots", "carrotss"),
    "potatoes" -> List("potatos", "potattoes", "potatotes", "pottatoes", "potatoss"),
    "lamb" -> List("lam", "lambb", "lambe", "lamby", "lambchop"),
    "beef" -> List("beefs", "beff", "beeff", "beefy", "beefsteak"),
    "fish" -> List("fisch", "fishh", "fishe", "fishy", "feesh"),
    "milk" -> List("milkk", "mylk", "milke", "mylkk", "milkys")
  )
  val lowerInput = input.toLowerCase

  // Find the correct word if the input matches any typo
  // find is higher function for Map 
  commonTypos.find { 
    case (correct, typos) =>typos.foldLeft(false) { (found, typ) =>
    if (found) true else typ.toLowerCase == lowerInput} }.map(_._1).getOrElse(input)
 }
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
  val correctedTokens = tokens.map(handleTypos) // Correct typos in each token
  
  // Identify command type based on keywords
  val command = correctedTokens match {
    // Quiz commands
    case t if t.contains("quiz") || t.contains("test") || t.contains("question") => "quiz"
    
    // Information requests
    case t if (t.contains("tell") || t.contains("about") || t.contains("what") || t.contains("explain") || t.contains("describe")) && 
              (t.contains("ingredient") || t.contains("make") || t.contains("contains")) => "ingredients"
              
    case t if (t.contains("how") && t.contains("make")) || 
              t.contains("recipe") || 
              (t.contains("steps") && (t.contains("make") || t.contains("prepare"))) => "recipe"
              
    case t if t.contains("knowledge") || t.contains("fact") || 
              t.contains("trivia") || t.contains("interesting") => "trivia"
    
    // Dish information
    case t if (t.contains("what") || t.contains("tell") || t.contains("about")) && 
              !t.contains("ingredient") && !t.contains("recipe") => "dish_info"
    
    // Exit commands
    case t if t.contains("bye") || t.contains("exit") || t.contains("quit") || t.contains("stop") => "bye"
    
    case _ => "unknown"
  }
  
  // Extract cuisine or dish name from tokens
  val keywords = correctedTokens.filterNot(token => 
    Set("what", "tell", "me", "about", "the", "how", "make", "recipe", 
        "ingredients", "of", "dish", "food", "steps", "to", "prepare",
        "knowledge", "fact", "trivia", "quiz", "test", "question").contains(token) )
  
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
      //(tokens)
    case"recipe" =>
      handleRecipeRequest(tokens)
    case "trivia" =>
      println("I don't have trivia yet, but stay tuned!")
    case "dish_info" =>
      handleDishRequest(tokens)
    case "ingredients" =>
      handleDishRequest(tokens)
    case "unknown" => // yerg3 llchat tany 
      val corrected = handleTypos(input)
      println(s"Did you mean: $corrected? Or try asking about a cuisine or type 'quiz'.") //need to handle this type pf error 
    }
  }
   def handleRecipeRequest(tokens: List[String]): Unit = {
  // Similar to dish request but with recipe-specific response
  val allDishes = FoodDB.getAllDishes
  val maybeDish = tokens.collectFirst {
    case t => allDishes.find(_.name.toLowerCase == handleTypos(t))
  }.flatten
  
  maybeDish match {
    case Some(dish) =>
      showRecipe(dish)
    case None => // need to tetzabt 
      println("Which recipe would you like?")
      // Could show list of available dishes here
      val dishName = scala.io.StdIn.readLine().trim
      val corrected = handleTypos(dishName)
      allDishes.find(_.name.toLowerCase == corrected) match {
        case Some(d) => showRecipe(d)
        case None => println(s"Sorry, I don't have a recipe for '$dishName'")
      }
  }
}
  // Function to handle dish requests and show details
  // This function will be called when the user asks about a dish
  def handleDishRequest(tokens: List[String]): Unit = {
  // Find dish name in tokens
  // If dish is found, show details
  val allDishes = FoodDB.getAllDishes
  val maybeDish = tokens.collectFirst {
    case t => allDishes.find(_.name.toLowerCase == handleTypos(t))
  }.flatten
  
  maybeDish match {
    case Some(dish) =>
      showRecipe(dish)
    case None => // need to tetzabt 
      println("Which recipe would you like?")
      // Could show list of available dishes here
      val dishName = scala.io.StdIn.readLine().trim
      val corrected = handleTypos(dishName)
      allDishes.find(_.name.toLowerCase == corrected) match {
        case Some(d) => showRecipe(d)
        case None => println(s"Sorry, I don't have a recipe for '$dishName'")
      }
  }
}
  def showDishDetails(dish: Dish): Unit = {
  val vegStatus = if (dish.isVegetarian) "Vegetarian" else "Non-vegetarian"
  println(s"\n◎ ${dish.name} ($vegStatus)")
  println(s"   Ingredients: ${dish.ingredients.mkString(", ")}")
  println("\nWould you like me to show you the recipe for this dish?")
  val answer = readLine().trim.toLowerCase
  val corrected= handleTypos(answer)
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
    val corrected= handleTypos(answer)
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
