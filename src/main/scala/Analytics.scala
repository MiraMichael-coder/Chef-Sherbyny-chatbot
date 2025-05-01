import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import scala.annotation.tailrec

object Analytics {
  // Immutable case class to represent a single log entry
  case class LogEntry(
    sequenceNumber: Int,
    timestamp: String,
    userInput: String,
    chatbotResponse: String,
    quizDetails: Option[QuizDetails] = None
  )

  // Case class for quiz-specific details
  case class QuizDetails(
    cuisine: String,
    question: String,
    userAnswer: String,
    correctAnswer: String,
    isCorrect: Boolean
  )

  // Immutable log storage
  private var _log: List[LogEntry] = Nil
  private var _sequenceCounter: Int = 0

  // Thread-safe logging with immutable append
  private def appendToLog(newEntry: LogEntry): Unit = synchronized {
    _log = _log :+ newEntry
    _sequenceCounter += 1
  }

  /**
   * Logs a user interaction with the chatbot
   * @param userInput The user's input message
    @param chatbotResponse The chatbot's response
   */
  def logInteraction(user: String, chat: String): Unit = {
    val timestamp = LocalDateTime.now.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
    val newEntry = LogEntry(
      sequenceNumber = _sequenceCounter + 1,
      timestamp = timestamp,
      userInput = user,
      chatbotResponse = chat
    )
    appendToLog(newEntry)
  }

  /**
   * Logs a quiz interaction with additional details
   * @param userInput The user's input (answer)
   * @param chatbotResponse The chatbot's feedback
   * @param quizType The type of quiz (e.g., "egyptian", "general")
   * @param question The quiz question
   * @param userAnswer The user's answer
   * @param correctAnswer The correct answer
   * @param isCorrect Whether the answer was correct
   */
  def logQuizInteraction(
    userInput: String,
    chatbotResponse: String,
    c: String,
    question: String,
    userAnswer: String,
    correctAnswer: String,
    isCorrect: Boolean
  ): Unit = {
    val timestamp = LocalDateTime.now.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
    val quizDetails = QuizDetails(
      cuisine = c,
      question = question,
      userAnswer = userAnswer,
      correctAnswer = correctAnswer,
      isCorrect = isCorrect
    )
    val newEntry = LogEntry(
      sequenceNumber = _sequenceCounter + 1,
      timestamp = timestamp,
      userInput = userInput,
      chatbotResponse = chatbotResponse,
      quizDetails = Some(quizDetails)
    )
    appendToLog(newEntry)
  }

  /**
   * Retrieves the full interaction log
   * @return List of all log entries
   */
  def getInteractionLog(): List[LogEntry] = _log

  /**
   * Analyzes all interactions to produce summary statistics
   * @return Formatted analysis string
   */
  def analyzeInteractions(): String = {
    val totalInteractions = _log.length
    val uniqueUsers = _log.map(_.userInput).distinct.size // Simple approximation
    
    // Count interaction types using pattern matching
    val interactionTypes = _log.foldLeft(Map.empty[String, Int]) { (acc, entry) =>
      val interactionType = entry.chatbotResponse match {
        case r if r.contains("recipe") => "Recipe Request"
        case r if r.contains("ingredient") => "Ingredient Search"
        case r if r.contains("quiz") => "Quiz Interaction"
        case r if r.contains("trivia") => "Trivia Request"
        case r if r.contains("dish") => "Dish Information"
        case _ => "General Inquiry"
      }
      acc + (interactionType -> (acc.getOrElse(interactionType, 0) + 1))
    }
    
    // Format interaction type counts
    val typeCounts = interactionTypes.toList.sortBy(-_._2).map {
      case (t, c) => s"$t: $c (${(c.toDouble / totalInteractions * 100).round}%)"
    }.mkString("\n  ")
    
    s"""|Interaction Analysis:
        |----------------------
        |Total interactions: $totalInteractions
        |Approximate unique users: $uniqueUsers
        |
        |Interaction Type Breakdown:
        |  $typeCounts
        |""".stripMargin
  }

  /**
   * Analyzes quiz performance specifically
   * @return Formatted quiz analysis string
   */
  def analyzeQuizPerformance(): String = {
    val quizEntries = _log.filter(_.quizDetails.isDefined)
    val totalQuizQuestions = quizEntries.length
    
    if (totalQuizQuestions == 0) {
      return "No quiz questions have been answered yet."
    }
    
    val correctAnswers = quizEntries.count(_.quizDetails.get.isCorrect)
    val accuracyRate = (correctAnswers.toDouble / totalQuizQuestions * 100).round
    
    // Analyze by quiz type
    val byQuizType = quizEntries.groupBy(_.quizDetails.get.cuisine)
      .map { case (quizType, entries) =>
        val correct = entries.count(_.quizDetails.get.isCorrect)
        val total = entries.length
        val rate = (correct.toDouble / total * 100).round
        (quizType, correct, total, rate)
      }
    
    // Find most frequently missed questions
    val missedQuestions = quizEntries.filterNot(_.quizDetails.get.isCorrect)
      .groupBy(_.quizDetails.get.question)
      .map { case (question, entries) => (question, entries.length) }
      .toList
      .sortBy(-_._2)
      .take(3) // Top 3 most missed
    
    // Format quiz type analysis
    val quizTypeAnalysis = byQuizType.map { 
      case (t, c, tot, r) => s"$t: $c/$tot correct ($r%)"
    }.mkString("\n  ")
    
    // Format missed questions
    val missedQuestionsStr = if (missedQuestions.nonEmpty) {
      missedQuestions.map { case (q, c) => s"'$q' (missed $c times)" }.mkString("\n  ")
    } else {
      "No frequently missed questions identified"
    }
    
    s"""|Quiz Performance Analysis:
        |--------------------------
        |Total quiz questions answered: $totalQuizQuestions
        |Overall accuracy: $accuracyRate%
        |
        |Accuracy by Quiz Type:
        |  $quizTypeAnalysis
        |
        |Most Frequently Missed Questions:
        |  $missedQuestionsStr
        |""".stripMargin
  }

  /**
   * Gets recent interactions (last N)
   * @param n Number of recent interactions to return
   * @return List of recent log entries
   */
  def getRecentInteractions(n: Int): List[LogEntry] = _log.takeRight(n)

  /**
   * Clears the log (for testing/reset purposes)
   */
  def clearLog(): Unit = synchronized {
    _log = Nil
    _sequenceCounter = 0
  }
}