package com.example.prollecto0real

data class FinalAnswersRequest(
    val sessionId: String,
    val userAnswers: List<Int> // Lista de índices de respuestas seleccionadas
)
