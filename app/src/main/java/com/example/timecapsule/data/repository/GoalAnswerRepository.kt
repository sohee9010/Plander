package com.example.timecapsule.data.repository

import androidx.lifecycle.LiveData
import com.example.timecapsule.data.local.GoalAnswerDao
import com.example.timecapsule.data.model.GoalAnswer

class GoalAnswerRepository(private val goalAnswerDao: GoalAnswerDao) {

    // 특정 목표의 질문-답변 조회
    fun getAnswersByGoalId(goalId: String): LiveData<List<GoalAnswer>> {
        return goalAnswerDao.getAnswersByGoalId(goalId)
    }

    // 특정 질문 답변 조회
    suspend fun getAnswer(goalId: String, questionNumber: Int): GoalAnswer? {
        return goalAnswerDao.getAnswer(goalId, questionNumber)
    }

    // 시작 답변 저장
    suspend fun insertAnswer(answer: GoalAnswer) {
        goalAnswerDao.insertAnswer(answer)
    }

    // 여러 답변 저장 (목표 생성 시)
    suspend fun insertAnswers(answers: List<GoalAnswer>) {
        goalAnswerDao.insertAnswers(answers)
    }

    // 완료 답변 업데이트
    suspend fun updateEndAnswer(
        goalId: String,
        questionNumber: Int,
        endAnswer: String
    ) {
        val answer = goalAnswerDao.getAnswer(goalId, questionNumber)
        answer?.let {
            val updated = it.copy(
                endAnswer = endAnswer,
                endAnswerDate = System.currentTimeMillis()
            )
            goalAnswerDao.updateAnswer(updated)
        }
    }

    // 답변 삭제
    suspend fun deleteAnswersByGoalId(goalId: String) {
        goalAnswerDao.deleteAnswersByGoalId(goalId)
    }
}