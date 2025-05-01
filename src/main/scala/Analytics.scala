object Analytics {
  private var interactionLog: List[(Int, String, String)] = List()
  private var quizLog: List[(Int, String, String, String, Boolean, String)] = List()
  private var sequence: Int = 1
  private var userPreferences: Map[String, Map[String, Int]] = Map()

  // Log user messages and chatbot responses
  /*def logInteraction(userInput: String, chatbotResponse: String): Unit = {
    interactionLog :+= (sequence, userInput, chatbotResponse)
    sequence += 1
  }*/
  // In Analytics.scala
  def logInteraction(userInput: String, chatbotResponse: String, userName: String): Unit = {
    interactionLog :+= (sequence, s"[$userName] $userInput", chatbotResponse)
    sequence += 1
  }

  // Log quiz question results with username
  def logQuizInteraction(question: String, userAnswer: String, correctAnswer: String, correct: Boolean, userName: String): Unit = {
    quizLog :+= (sequence, question, userAnswer, correctAnswer, correct, userName)
    sequence += 1
  }
  def getInteractionLog(): List[(Int, String, String)] = interactionLog

  // Log user preferences (search keywords)
  def updateUserSearchLog(userName: String, searchQuery: String): Unit = {
  val userLog = userPreferences.getOrElse(userName, Map())

  // Smart tagging based on keywords
  val tag = if (searchQuery.toLowerCase.contains("cuisine:")) "Cuisine" 
            else if (searchQuery.toLowerCase.contains("dish:")) "Dish"
            else if (searchQuery.split("\\s+").length == 1) "Dish"
            else "General"

  val key = s"$tag:${searchQuery.capitalize}"

  val updatedLog = userLog + (key -> (userLog.getOrElse(key, 0) + 1))
  userPreferences += (userName -> updatedLog)
}


  // Analyze preferred cuisines and dishes by user
  def analyzeUserPreferences(userName: String): Unit = {
    userPreferences.get(userName) match {
      case Some(preferences) =>
        val mostSearchedCuisine = preferences.filter(_._1.contains("Cuisine")).maxByOption(_._2)
        val mostSearchedDish = preferences.filter(_._1.contains("Dish")).maxByOption(_._2)

        println(s"\n📊 User Preferences Analytics for $userName:")
        mostSearchedCuisine match {
           case Some((cuisine, count)) if count>0 =>
            println(s"🥇 Most Preferred $cuisine with $count searches.")
          case None => println("No preferred cuisine data found.")
        }

       
        mostSearchedDish match {
          case Some((dish, count)) if count>0 =>
            println(s"🥇 Most Preferred $dish with $count searches.")
          case None => println("No preferred dish data found.")
        }
      case None =>
        println(s"No search data found for user: $userName")
    }
  }

  // Analyze user-specific quiz performance
  def analyzeQuizPerformance(userName: String): Unit = {
    val userQuizLog = quizLog.filter(_._6 == userName)

    val totalQuestions = userQuizLog.size
    val correctAnswers = userQuizLog.count(_._5)
    val incorrectAnswers = totalQuestions - correctAnswers

    println(s"\n📊 Quiz Performance Analysis for $userName:")
    println(s"Total Questions Attempted: $totalQuestions")
    println(s"Correct Answers: $correctAnswers")
    println(s"Incorrect Answers: $incorrectAnswers")

    val correctPercentage = if (totalQuestions > 0) (correctAnswers.toDouble / totalQuestions) * 100 else 0.0
    println(f"Correct Answer Percentage: $correctPercentage%.2f%%")
  }

  // Show both preferences and quiz analytics
  def handleUserRequestForAnalytics(userName: String): Unit = {
    println(s"\n🧑‍💻 Showing analytics for user: $userName...")
    analyzeUserPreferences(userName)
    analyzeQuizPerformance(userName)
  }

  // Show overall interaction type usage
  def analyzeInteractions()(implicit parseFunc: String => (String, List[String])): Unit = {
    println(s"\n📊 Total interactions: ${interactionLog.length}")

    val processed = interactionLog.map { case (_, _, userMsg) =>
      parseFunc(userMsg)
    }

    val commandCounts = processed.map(_._1).groupBy(identity).view.mapValues(_.size).toMap

    println("\n📈 Interaction types breakdown:")
    commandCounts.toList.sortBy(-_._2).foreach { case (cmd, count) =>
      println(f" - $cmd%-12s: $count")
    }
  }
}

/* //for cuisine
  val userName = UserState.getName
  val capitalizedCategory = category.capitalize
     Analytics.updateUserSearchLog(userName, s"Cuisine:${category.capitalize}")

//for dishes
  val userName = UserState.getName
      val searchQuery = tokens.mkString(" ")
      
      // Log the initial dish search
      Analytics.updateUserSearchLog(userName, s"Dish:${searchQuery.capitalize}") */  