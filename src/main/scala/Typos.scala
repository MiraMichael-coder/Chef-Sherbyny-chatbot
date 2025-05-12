
import scala.io.Source
import scala.util.{Try, Success, Failure}
import java.io.File

object Typos {
  // Use forward slashes for better cross-platform compatibility
  val filename = "C:\\Users\\Mira\\Desktop\\uni\\year 2\\Semster 2\\Advanced Prog\\Project\\chatbot\\src\\main\\scala\\data\\typos.txt"

  case class WordWithTypos(correctWord: String, commonTypos: List[String])
  
  // Load typos from a file
  // This function reads a file containing common typos and their correct forms.
  // Each line should be in the format "correctWord: typo1, typo2, ..."
  // It returns a list of WordWithTypos objects.
  // If the file is not found or an error occurs, it returns an empty list.
  // The function also handles empty lines and comments (lines starting with #).
  def loadTyposFromFile(filePath: String): List[WordWithTypos] = {
    // First check if file exists
    val file = new File(filePath)
    if (!file.exists()) {
      //println(s"Error: File not found at path: $filePath")
      return List.empty
    }

    Try {
      val source = Source.fromFile(file, "UTF-8")
      try {
        val lines = source.getLines().toList  // Read all lines at once for better debugging
        val processed = lines.zipWithIndex.flatMap { case (line, idx) =>
          val trimmed = line.trim
          if (trimmed.isEmpty || trimmed.startsWith("#")) {
            None  // Skip empty lines and comments
          } else if (!trimmed.contains(":")) {
            //println(s"Warning: Invalid format at line ${idx + 1}: '$line'")
            None
          } else {
            Try {
              val parts = trimmed.split(":").map(_.trim)
              if (parts.length < 2) {
                throw new Exception("Missing colon separator")
              }
              WordWithTypos(
                correctWord = parts(0).toLowerCase,
                commonTypos = parts(1).split(",").map(_.trim.toLowerCase).toList
              )
            }.recover {
              case e: Exception =>
                null  // Will be filtered out
            }.toOption
          }
        }.filter(_ != null)
      
        processed
      } finally {
        source.close()
      }
    }.recover {
      case e: Exception =>
        List.empty
    }.get
  }
  val loadedTypos: List[WordWithTypos] = loadTyposFromFile(filename)
  def handleTypos(input: String): String = {
    val commonTypos = loadedTypos
    if (commonTypos.isEmpty) {
      //println("Warning: No typos loaded - returning original input")
      return input
    }
   

    val lowerInput = input.toLowerCase
    commonTypos.collectFirst {
      case WordWithTypos(correct, typos) if typos.contains(lowerInput) => correct
    }.getOrElse(input)
  }
}

    //https://www.internetmarketingninjas.com/tools/online-keyword-typo-generator/  generate typoss