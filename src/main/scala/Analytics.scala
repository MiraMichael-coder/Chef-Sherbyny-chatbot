import scala.io.StdIn.readLine
  
import java.nio.file.{Paths, Files}
import scala.io.Source
import scala.util.{Try, Success, Failure}
import java.io.{PrintWriter, FileWriter}

object Analytics {
  private var interactionLog: List[(Int, String, String)] = List()
  private var quizLog: List[(Int, String, String, String, Boolean, String)] = List()
  private var sequence: Int = 1
  private var userPreferences: Map[String, Map[String, Int]] = Map()
  private var generalPreferences: Map[String, String] = Map()

  //change file path to your own
  val preferencesFilePath = "C:\\Users\\Mira\\Desktop\\uni\\year 2\\Semster 2\\Advanced Prog\\Project\\chatbot\\src\\main\\scala\\data\\user_data.txt"

  // Load user preferences from file
  def loadPreferencesFromFile(): Unit = {
    Try {
      generalPreferences = Map()
      userPreferences = Map()
      
      if (Files.exists(Paths.get(preferencesFilePath))) {
        val source = Source.fromFile(preferencesFilePath)
        try {
          source.getLines().drop(1).foreach { line =>
            val parts = line.split("\\|", -1).map(_.trim)
            if (parts.length >= 3) {
              val userName = parts(0)
              val dishPrefs = parts(1).split(",").map(_.trim).filter(_.nonEmpty)
              val cuisinePrefs = parts(2).split(",").map(_.trim).filter(_.nonEmpty)
              
              // Reconstruct generalPreferences
              val prefStrings = List(
                dishPrefs.map(d => s"Favorite Dish: $d"),
                cuisinePrefs.map(c => s"Favorite Cuisine: $c")
              ).flatten
              
              if (prefStrings.nonEmpty) {
                generalPreferences += (userName -> prefStrings.mkString("; "))
              }
              
              // Reconstruct userPreferences counts
              val currentCounts = userPreferences.getOrElse(userName, Map())
              val updatedCounts = currentCounts ++ Map(
                "dish" -> (currentCounts.getOrElse("dish", 0) + dishPrefs.length),
                "cuisine" -> (currentCounts.getOrElse("cuisine", 0) + cuisinePrefs.length)
              )
              userPreferences += (userName -> updatedCounts)
            }
          }
        } finally {
          //println(s"Loaded ${generalPreferences.size} user preferences")
          source.close()
        }
      }
    } match {
      case Failure(e) => 
        println(s"Error loading preferences: ${e.getMessage}")
        if (!Files.exists(Paths.get(preferencesFilePath))) {
          savePreferencesToFile() // Create empty file if doesn't exist
        }
      case Success(_) => 
    }
  }
   // Save all preferences (maintains existing data)
  def savePreferencesToFile(): Unit = {
    Try {
      val writer = new PrintWriter(preferencesFilePath)
      try {
        writer.println("name|dishpref|cuisinepref")
        
        generalPreferences.foreach { case (userName, prefs) =>
          val dishPrefs = prefs.split(";")
            .filter(_.trim.startsWith("Favorite Dish:"))
            .map(_.split(":").last.trim)
            .mkString(",")
          
          val cuisinePrefs = prefs.split(";")
            .filter(_.trim.startsWith("Favorite Cuisine:"))
            .map(_.split(":").last.trim)
            .mkString(",")
          
          writer.println(s"$userName|$dishPrefs|$cuisinePrefs")
        }
        println(s"Successfully saved ${generalPreferences.size} preferences")
      } finally {
        writer.close()
      }
    }.recover {
      case e => println(s"Error saving preferences: ${e.getMessage}")
    }
  }

  // Append or update single user's preferences
  def appendOrUpdateUserPreferences(userName: String): Unit = {
  loadPreferencesFromFile()
  
  generalPreferences.get(userName) match {
    case Some(_) =>
      // User exists - update the file completely
      savePreferencesToFile()
      //println(s"Updated preferences for $userName")
    
    case None =>
      // New user - append to file
      generalPreferences.get(userName) match {
        case Some(prefs) =>
          Try {
            val writer = new FileWriter(preferencesFilePath, true)
            try {
              // Check if file is empty to write header
              if (Files.size(Paths.get(preferencesFilePath)) == 0) {
                writer.write("name|dishpref|cuisinepref\n")
              }
              
              val dishPrefs = prefs.split(";")
                .filter(_.trim.startsWith("Favorite Dish:"))
                .map(_.split(":").last.trim)
                .mkString(",")
              
              val cuisinePrefs = prefs.split(";")
                .filter(_.trim.startsWith("Favorite Cuisine:"))
                .map(_.split(":").last.trim)
                .mkString(",")
              
              writer.write(s"$userName|$dishPrefs|$cuisinePrefs\n")
            } finally {
              writer.close()
            }
            println(s"Appended preferences for $userName")
          }.recover {
            case e => println(s"Error appending preferences: ${e.getMessage}")
          }
        case None =>
          println(s"No preferences found for $userName to append")
      }
  }
}

  // Add or update single user's preferences
  def updateUserPreferences(userName: String, newPrefs: String): Unit = {
    // Update in-memory preferences
    val currentPrefs = generalPreferences.getOrElse(userName, "")
    val updatedPrefs = if (currentPrefs.contains(newPrefs)) {
      currentPrefs
    } else {
      if (currentPrefs.isEmpty) newPrefs else s"$currentPrefs; $newPrefs"
    }
    generalPreferences += (userName -> updatedPrefs)
    
    // Update counts
    val prefType = if (newPrefs.toLowerCase.contains("cuisine")) "cuisine" 
                  else if (newPrefs.toLowerCase.contains("dish")) "dish"
                  else "general"
    val currentCounts = userPreferences.getOrElse(userName, Map())
    userPreferences += (userName -> (currentCounts + (prefType -> (currentCounts.getOrElse(prefType, 0) + 1))))
    
    // Save to file
    appendOrUpdateUserPreferences(userName)
  }


  // Log user interactions with username
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

  val key = if (searchQuery.toLowerCase.startsWith(s"${tag.toLowerCase}:"))
  searchQuery.capitalize
else
  s"$tag:${searchQuery.capitalize}"

  // Update the log with the new search
  val updatedLog = userLog + (key -> (userLog.getOrElse(key, 0) + 1))
  userPreferences += (userName -> updatedLog)
}

  // Log user preferences (general)
  def storeUserPreferences(userName: String, preference: String): Unit = {
  // Get existing preferences
  val currentPrefs = generalPreferences.getOrElse(userName, "")
  
  // Append new preference (avoid duplicates)
  val updatedPrefs = if (currentPrefs.contains(preference)) {
    currentPrefs
  } else {
    if (currentPrefs.isEmpty) preference else s"$currentPrefs; $preference"
  }
  
  generalPreferences += (userName -> updatedPrefs)
  
  // Track in user preferences map
  val prefType = if (preference.toLowerCase.contains("cuisine")) "cuisine" 
                else if (preference.toLowerCase.contains("dish")) "dish"
                else "general"
  
  val currentCounts = userPreferences.getOrElse(userName, Map())
  val updatedCounts = currentCounts + (prefType -> (currentCounts.getOrElse(prefType, 0) + 1))
  userPreferences += (userName -> updatedCounts)
  
  logInteraction(s"User updated $prefType preferences", s"Stored: $preference", userName)
}
  // Retrieve user preferences Cusine 
  def getFavoriteCuisine(userName: String): Option[String] = {
  generalPreferences.get(userName).flatMap { prefs =>
    prefs.split(";").collectFirst {
      case pref if pref.toLowerCase.contains("cuisine") => 
        pref.split(":").last.trim
    }
  }
}
  // Retrieve user preferences Dish
  def getFavoriteDishes(userName: String): List[String] = {
  generalPreferences.get(userName).map { prefs =>
    prefs.split(";").collect {
      case pref if pref.toLowerCase.contains("dish") => 
        pref.split(":").last.trim
    }.toList
  }.getOrElse(Nil)
}
    // Retrieve user preferences (general)
    def getUserPreferences(userName: String): Option[String] = {
      generalPreferences.get(userName) match {
        case Some(pref) => 
          logInteraction("Requested user preferences", s"Found preferences: $pref", userName)
          Some(pref)
        case None =>
          logInteraction("Requested user preferences", "No preferences found", userName)
          None
      }
    }

  // Analyze preferred cuisines and dishes by user

  def analyzeUserPreferences(userName: String): Unit = {
    userPreferences.get(userName) match {
      case Some(preferences) =>
        val mostSearchedCuisine = preferences.filter(_._1.contains("Cuisine")).maxByOption(_._2)
        val mostSearchedDish = preferences.filter(_._1.contains("Dish")).maxByOption(_._2)

        println(s"\n📊 User Preferences Analytics for $userName:")
        (mostSearchedCuisine, mostSearchedDish) match {
          case (Some((cuisine, count)), None) =>
            println(s"🥇 Most Preferred $cuisine with $count searches.")
          
          case (None, Some((dish, count))) =>
            println(s"🥇 Most Preferred $dish with $count searches.")

          case (Some((cuisine, count1)), Some((dish, count2))) =>
            println(s"🥇 Most Preferred $cuisine with $count1 searches.")
            println(s"🥇 Most Preferred $dish with $count2 searches.")
            
          case (None, None) =>
            println(s"No search data found for user: $userName")
    }
      case None =>
        println(s"No search data found for user: $userName")
    }
  }


  // Analyze user-specific quiz performance
  def analyzeQuizPerformance(userName: String,handleTypos:String=>String): Unit = {
    val userQuizLog = quizLog.filter(_._6 == userName)
    if (userQuizLog.nonEmpty) {
      val totalQuestions = userQuizLog.size
      val correctAnswers = userQuizLog.count(_._5)
      val incorrectAnswers = totalQuestions - correctAnswers

      println(s"\n📊 Quiz Performance Analysis for $userName:")
      println(s"Total Questions Attempted: $totalQuestions")
      println(s"Correct Answers: $correctAnswers")
      println(s"Incorrect Answers: $incorrectAnswers")

      val correctPercentage = if (totalQuestions > 0) (correctAnswers.toDouble / totalQuestions) * 100 else 0.0
      println(f"Correct Answer Percentage: $correctPercentage%.2f%%")
      if (correctPercentage <= 40) {
        println("Wanna try again")
        val input = readLine().trim.toLowerCase
        if (input == "yes" || input == "yeah")
          val logs = getInteractionLog()
          logs.lastOption match {
            case Some(log) =>
              processLastLog(log, tokens = input.split("\\s+").toList, Typos.handleTypos)
            case None =>
              println("No interactions logged yet.")
          }
      }
    }
    else
      println("No quizez taken yet....")
  }
  // Show both preferences and quiz analytics
  def handleUserRequestForAnalytics(userName: String): Unit = {
    println(s"\n🧑‍💻 Showing analytics for user: $userName...")
    analyzeUserPreferences(userName)
    analyzeQuizPerformance(userName,Typos.handleTypos)
  }

  // Show overall interaction type usage
  def analyzeInteractions()(implicit parseFunc:String => (String, List[String])): Unit = {
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
  // Proccess the last log entry
  // This function takes the last log entry, user input, and bot response
  // It checks if the user input contains Trivia or last quiz then if the user input contains a valid response
  // It extracts the cuisine from the user input and starts a quiz based on that cuisine
  // If no specific cuisine is detected, it prints a message
  def processLastLog(log: (Int, String, String),tokens:List[String],handleTypos: String => String): Unit = {
  val (seq, userInput, botResponse) = log
  println(s"\nWorking on last log #$seq:")
  println(s"👤 User said: $userInput")
  println(s"🤖 Bot replied: $botResponse")
  
  val corrected = tokens.map(handleTypos)
  val validYesResponses = Set("yes", "sure", "ok", "okay", "yeah", "surely", "certainly")
  if(userInput.contains("Trivia" )&& validYesResponses.exists(corrected.contains)) {
    
  val cuisine = userInput.toLowerCase.split(":").lastOption.map(_.trim).getOrElse("")
  // Check if cuisine is found
  if (cuisine.nonEmpty) {
    println(s"🍽️ Cuisine detected: $cuisine")
    Quizez.startquiz(cuisine, handleTypos)
    
  } else {
    println("No specific cuisine detected.")
  }

  
}
  else if(userInput.contains("quiz" )&& validYesResponses.exists(corrected.contains))
    {
      val cuisine = userInput.toLowerCase.split(":").lastOption.map(_.trim).getOrElse("")
      // Check if cuisine is found
      if (cuisine.nonEmpty) {
        println(s"🍽️ Cuisine detected: $cuisine")
        Quizez.startquiz(cuisine, handleTypos)

      } else {
        println("No specific cuisine detected.")
      }


    }
}
}
