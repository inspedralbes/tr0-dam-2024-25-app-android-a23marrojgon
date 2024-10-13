
data class QuestionsResponse(
    val sessionId: String,
    val preguntes: List<Question>? // Asegúrate de que sea nullable si esperas que pueda ser null
)

data class Question(
    val id: Int,
    val pregunta: String,
    val respostes: List<String>,
    val resposta_correcta: Int, // Cambiado a Int para el índice de la respuesta correcta
    val imatge: String // Añadido para la imagen de la pregunta
)

data class QuestionsRequest(
    val num: Int // Número de preguntas a solicitar
)

