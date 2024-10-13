package com.example.prollecto0real

import Question
import QuestionsRequest
import QuestionsResponse
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class QuestionActivity : AppCompatActivity() {
    private lateinit var sessionId: String // Almacena el sessionId
    private lateinit var questionTextView: TextView
    private lateinit var answersRadioGroup: RadioGroup
    private lateinit var submitAnswerButton: Button
    private lateinit var timerTextView: TextView // Añadir para el temporizador

    private var currentQuestionIndex = 0 // Índice de la pregunta actual
    private var correctAnswersCount = 0 // Contador de respuestas correctas

    private var preguntas: List<Question> = emptyList() // Almacena las preguntas obtenidas
    private var userAnswers: MutableList<Int> = mutableListOf() // Respuestas del usuario

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_question)

        // Inicializar las vistas
        questionTextView = findViewById(R.id.questionTextView)
        answersRadioGroup = findViewById(R.id.answersRadioGroup)
        submitAnswerButton = findViewById(R.id.submitAnswerButton)
        timerTextView = findViewById(R.id.timerTextView) // Inicializar el temporizador

        // Obtener las preguntas desde el servidor
        fetchQuestions(15) // Puedes ajustar el número de preguntas

        // Configurar el botón para enviar respuestas
        submitAnswerButton.setOnClickListener {
            handleSubmitAnswer()
        }
    }

    private fun fetchQuestions(num: Int) {
        val request = QuestionsRequest(num)

        val call = RetrofitClient.apiService.getQuestions(request)
        call.enqueue(object : Callback<QuestionsResponse> {
            override fun onResponse(call: Call<QuestionsResponse>, response: Response<QuestionsResponse>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        sessionId = responseBody.sessionId
                        preguntas = responseBody.preguntes ?: emptyList()

                        if (preguntas.isNotEmpty()) {
                            displayQuestion(preguntas[currentQuestionIndex])
                        } else {
                            showToast("No hay preguntas disponibles.")
                            finish()
                        }
                    } else {
                        showToast("Error al recibir las preguntas.")
                        finish()
                    }
                } else {
                    showToast("Error al obtener las preguntas: ${response.code()}")
                    finish()
                }
            }

            override fun onFailure(call: Call<QuestionsResponse>, t: Throwable) {
                showToast("Error de conexión: ${t.message}")
                finish()
            }
        })
    }

    private fun displayQuestion(question: Question) {
        // Mostrar el texto de la pregunta
        questionTextView.text = question.pregunta

        // Limpiar selecciones previas
        answersRadioGroup.clearCheck()

        // Asignar las respuestas a los RadioButtons
        val answers = listOf(
            question.respostes[0],
            question.respostes[1],
            question.respostes[2],
            question.respostes[3]
        ).shuffled() // Puedes mezclar las respuestas

        answersRadioGroup.removeAllViews() // Limpiar respuestas previas

        // Crear y agregar RadioButtons dinámicamente
        answers.forEachIndexed { index, answer ->
            val radioButton = RadioButton(this)
            radioButton.id = View.generateViewId()
            radioButton.text = answer
            answersRadioGroup.addView(radioButton)
        }

        // Cargar y mostrar la imagen de la pregunta (si aplica)
        // Si no tienes imagenes en la pregunta, omite esto
        // Glide.with(this).load(question.imatge).into(imageView)
    }

    private fun handleSubmitAnswer() {
        // Obtener el RadioButton seleccionado
        val selectedId = answersRadioGroup.checkedRadioButtonId
        if (selectedId == -1) {
            showToast("Por favor selecciona una respuesta.")
            return
        }

        // Obtener el RadioButton seleccionado
        val selectedRadioButton = findViewById<RadioButton>(selectedId)
        val selectedAnswer = selectedRadioButton.text.toString()

        // Encontrar el índice de la respuesta seleccionada en las respuestas originales
        val selectedIndex = preguntas[currentQuestionIndex].respostes.indexOf(selectedAnswer)
        if (selectedIndex == -1) {
            showToast("Respuesta inválida seleccionada.")
            return
        }

        // Guardar la respuesta del usuario
        userAnswers.add(selectedIndex)

        // Verificar si la respuesta es correcta
        if (selectedIndex == preguntas[currentQuestionIndex].resposta_correcta) {
            correctAnswersCount++
        }

        // Avanzar a la siguiente pregunta o finalizar el juego
        currentQuestionIndex++
        if (currentQuestionIndex < preguntas.size) {
            displayQuestion(preguntas[currentQuestionIndex])
        } else {
            submitAnswers()
        }
    }

    private fun submitAnswers() {
        val request = FinalAnswersRequest(sessionId, userAnswers)

        val call = RetrofitClient.apiService.submitAnswers(request)
        call.enqueue(object : Callback<FinalAnswersResponse> {
            override fun onResponse(call: Call<FinalAnswersResponse>, response: Response<FinalAnswersResponse>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        showResults(responseBody.success, responseBody.total)
                    } else {
                        showToast("Error al procesar los resultados.")
                        finish()
                    }
                } else {
                    showToast("Error al enviar las respuestas: ${response.code()}")
                    finish()
                }
            }

            override fun onFailure(call: Call<FinalAnswersResponse>, t: Throwable) {
                showToast("Error de conexión: ${t.message}")
                finish()
            }
        })
    }

    private fun showResults(correct: Int, total: Int) {
        val intent = Intent(this, ResultActivity::class.java).apply {
            putExtra("TOTAL_QUESTIONS", total)
            putExtra("SUCCESSES", correct)
        }
        startActivity(intent)
        finish()
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
