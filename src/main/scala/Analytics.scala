  case class Interaction(id: Int, userInput: String, chatresponse: String)
  case class Quizlog(
    totalQuestions: Int,
    correctAnswers: Int,
    categories: Map[String, (Int, Int)]  )// (correct, total) per category) 
  case class AnalyticsState(
    interactions: List[Interaction] = Nil,
    nextId: Int = 1
  )
object Analytics {

  private var currentState: AnalyticsState = AnalyticsState()
    
    // Thread-safe access using synchronized (simplified for example)
    def getState: AnalyticsState = synchronized { currentState }
    
    def updateState(f: AnalyticsState => AnalyticsState): Unit = synchronized {
      currentState = f(currentState)
    }
  
    def logInteraction(userInput: String, response: String =" "): AnalyticsState = 
    {
    val newState = getState.copy(
      interactions = Interaction(
        id = getState.nextId,
        userInput = userInput,
        chatresponse = response
      ) :: getState.interactions,
      nextId = getState.nextId + 1
    )
    updateState(_ => newState)
    newState
  }
  def getlog: List[Interaction] = getState.interactions.reverse










    
}
