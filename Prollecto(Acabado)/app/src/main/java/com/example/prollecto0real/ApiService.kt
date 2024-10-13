package com.example.prollecto0real

import QuestionsRequest
import QuestionsResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("/getPregunta")
    fun getQuestions(@Body request: QuestionsRequest): Call<QuestionsResponse>

    @POST("/finalista")
    fun submitAnswers(@Body request: FinalAnswersRequest): Call<FinalAnswersResponse>
}
