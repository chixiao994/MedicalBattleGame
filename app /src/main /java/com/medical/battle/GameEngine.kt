package com.medical.battle

import com.medical.battle.models.*
import kotlin.random.Random

class GameEngine(val doctor: Player, val disease: Player) {
    var currentCase: MedicalCase? = null
    var turn = 1
    var gameOver = false
    var result = GameResult.PENDING
    
    fun startGame() {
        initializeDecks()
        doctor.drawCards(5)
        disease.drawCards(5)
        generateMedicalCase()
    }
    
    private fun initializeDecks() {
        // 医生卡组
        doctor.deck.apply {
            add(HerbCard("麻黄", HerbProperty.WARM, "肺膀胱", 15))
            add(HerbCard("桂枝", HerbProperty.WARM, "心肺膀胱", 12))
            add(HerbCard("石膏", HerbProperty.COLD, "肺胃", 20))
            add(HerbCard("甘草", HerbProperty.NEUTRAL, "心肺脾胃", 10))
        }
        
        // 疾病卡组
        disease.deck.apply {
            add(SymptomCard("发热", DiseaseType.ILLNESS, 15))
            add(SymptomCard("咳嗽", DiseaseType.ILLNESS, 12))
            add(SymptomCard("头痛", DiseaseType.ILLNESS, 10))
            add(SymptomCard("乏力", DiseaseType.FATIGUE, 8))
        }
        
        doctor.deck.shuffle()
        disease.deck.shuffle()
    }
    
    private fun generateMedicalCase() {
        val symptoms = listOf(
            SymptomCard("发热", DiseaseType.ILLNESS, 15),
            SymptomCard("咳嗽", DiseaseType.ILLNESS, 12)
        )
        currentCase = MedicalCase("患者发热3日，咳嗽痰黄", symptoms)
    }
    
    fun playTurn() {
        if (gameOver) return
        
        // 医生行动
        doctorAction()
        if (checkGameOver()) return
        
        // 疾病行动
        diseaseAction()
        checkGameOver()
        
        // 抽牌
        doctor.drawCards(1)
        disease.drawCards(1)
        
        turn++
    }
    
    private fun doctorAction() {
        val random = Random
        
        when (random.nextInt(3)) {
            0 -> useSingleHerb()
            1 -> tryCombineFormula()
            2 -> useAcupoint()
        }
    }
    
    private fun useSingleHerb() {
        val herbs = doctor.handCards.filterIsInstance<HerbCard>()
        if (herbs.isNotEmpty()) {
            val herb = herbs.random()
            val healAmount = herb.basePower * getSchoolMultiplier(doctor.school)
            doctor.heal(healAmount)
            disease.takeDamage(healAmount / 2)
            doctor.handCards.remove(herb)
        }
    }
    
    private fun tryCombineFormula() {
        val herbs = doctor.handCards.filterIsInstance<HerbCard>()
        if (herbs.size >= 2) {
            val selected = herbs.take(2)
            val effect = selected.sumOf { it.basePower } * 2 * getSchoolMultiplier(doctor.school)
            doctor.heal(effect)
            disease.takeDamage(effect)
            doctor.handCards.removeAll(selected)
        }
    }
    
    private fun useAcupoint() {
        // 简化版，直接造成伤害
        disease.takeDamage(20)
    }
    
    private fun diseaseAction() {
        val symptoms = disease.handCards.filterIsInstance<SymptomCard>()
        if (symptoms.isNotEmpty()) {
            val symptom = symptoms.random()
            var damage = symptom.baseDamage
            
            // 流派加成
            if (disease.school == School.WARM_DISEASE && symptom.type == DiseaseType.ILLNESS) {
                damage = (damage * 1.5).toInt()
            }
            
            doctor.takeDamage(damage)
            disease.handCards.remove(symptom)
        }
    }
    
    private fun getSchoolMultiplier(school: School): Int {
        return when (school) {
            School.CLASSICAL_FORMULA -> 2
            School.GOLDEN_NEEDLE -> 3
            School.EARTH_TONIFYING -> 2
            else -> 1
        }
    }
    
    private fun checkGameOver(): Boolean {
        if (!doctor.isAlive()) {
            gameOver = true
            result = GameResult.LOSE
            return true
        }
        
        if (!disease.isAlive()) {
            gameOver = true
            result = GameResult.WIN
            return true
        }
        
        if (turn > 10) {
            gameOver = true
            result = GameResult.DRAW
            return true
        }
        
        return false
    }
}
