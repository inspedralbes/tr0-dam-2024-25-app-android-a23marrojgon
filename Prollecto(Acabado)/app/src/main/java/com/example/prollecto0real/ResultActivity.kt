package com.example.prollecto0real

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ResultActivity : AppCompatActivity() {

    private lateinit var totalQuestionsTextView: TextView
    private lateinit var successesTextView: TextView
    private lateinit var restartButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        // Inicializa las vistas
        totalQuestionsTextView = findViewById(R.id.resultTextView)
        successesTextView = findViewById(R.id.scoreTextView)
        restartButton = findViewById(R.id.retryButton)

        // Recibe los datos del Intent
        val totalQuestions = intent.getIntExtra("TOTAL_QUESTIONS", 0)
        val successes = intent.getIntExtra("SUCCESSES", 0)

        // Muestra los resultados
        totalQuestionsTextView.text = "Total de preguntas: $totalQuestions"
        successesTextView.text = "Aciertos: $successes"

        // Configura el botón para reiniciar el juego
        restartButton.setOnClickListener {
            restartGame()
        }
    }

    private fun restartGame() {
        // Inicia StartActivity para reiniciar el juego
        val intent = Intent(this, StartActivity::class.java)
        startActivity(intent)
        finish() // Finaliza la actividad actual
    }
}
