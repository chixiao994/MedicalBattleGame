package com.medical.battle.models

import java.util.*

// 枚举定义
enum class PlayerRole { DOCTOR, DISEASE }
enum class School { CLASSICAL_FORMULA, WARM_DISEASE, GOLDEN_NEEDLE, EARTH_TONIFYING }
enum class HerbProperty { COLD, COOL, NEUTRAL, WARM, HOT }
enum class DiseaseType { ILLNESS, FATIGUE, INJURY }
enum class GameResult { WIN, LOSE, DRAW, PENDING }
enum class Rarity { COMMON, RARE, EPIC, LEGENDARY }

// 基础卡牌
abstract class Card(val name: String, val level: Int = 1, val rarity: Rarity = Rarity.COMMON) {
    val id: String = UUID.randomUUID().toString()
    abstract fun getType(): String
}

// 药材卡
class HerbCard(name: String, val property: HerbProperty, val channel: String, val basePower: Int) 
    : Card(name) {
    override fun getType() = "HERB"
}

// 症状卡
class SymptomCard(name: String, val type: DiseaseType, val baseDamage: Int) 
    : Card(name) {
    override fun getType() = "SYMPTOM"
}

// 方剂卡
class FormulaCard(name: String, val herbs: List<HerbCard>) : Card(name, herbs.size, Rarity.RARE) {
    val effect: Int = herbs.sumOf { it.basePower } * 2
    
    override fun getType() = "FORMULA"
}

// 玩家
class Player(val name: String, val role: PlayerRole, val school: School) {
    var level = 1
    var health = 100
    val maxHealth = 100
    val handCards = mutableListOf<Card>()
    val deck = mutableListOf<Card>()
    
    fun isAlive() = health > 0
    
    fun takeDamage(damage: Int) {
        health -= damage
        if (health < 0) health = 0
    }
    
    fun heal(amount: Int) {
        health += amount
        if (health > maxHealth) health = maxHealth
    }
    
    fun drawCards(count: Int) {
        repeat(count) {
            if (deck.isNotEmpty()) {
                handCards.add(deck.removeAt(0))
            }
        }
    }
}

// 医案
class MedicalCase(val description: String, val symptoms: List<SymptomCard>)
