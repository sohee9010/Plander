package com.example.timecapsule.data.local

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.timecapsule.data.model.GoalAnswer

@Dao
interface GoalAnswerDao {

    // 특정 목표의 모든 질문-답변 조회
    @Query("SELECT * FROM goal_answers WHERE goalCapsuleId = :goalId ORDER BY questionNumber")
    fun getAnswersByGoalId(goalId: String): LiveData<List<GoalAnswer>>

    // 특정 질문 답변 조회
    @Query("SELECT * FROM goal_answers WHERE goalCapsuleId = :goalId AND questionNumber = :qNum")
    suspend fun getAnswer(goalId: String, qNum: Int): GoalAnswer?

    // 답변 저장 (시작 시)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnswer(answer: GoalAnswer)

    // 여러 답변 한 번에 저장
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnswers(answers: List<GoalAnswer>)

    // 완료 시 답변 업데이트
    @Update
    suspend fun updateAnswer(answer: GoalAnswer)

    // 특정 목표의 답변 모두 삭제
    @Query("DELETE FROM goal_answers WHERE goalCapsuleId = :goalId")
    suspend fun deleteAnswersByGoalId(goalId: String)

    // 모든 답변 삭제
    @Query("DELETE FROM goal_answers")
    suspend fun deleteAll()
}