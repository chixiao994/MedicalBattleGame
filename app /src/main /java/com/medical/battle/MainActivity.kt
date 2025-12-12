// app/src/main/java/com/puretcm/battle/MainActivity.kt
package com.puretcm.battle

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    
    // 游戏状态
    private var currentChannel = "太阳"
    private var severity = 60
    private var turn = 1
    private var gameOver = false
    
    // 视图
    private lateinit var tvChannel: TextView
    private lateinit var tvSeverity: TextView
    private lateinit var tvTurn: TextView
    private lateinit var tvLog: TextView
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        // 初始化视图
        tvChannel = findViewById(R.id.tv_channel)
        tvSeverity = findViewById(R.id.tv_severity)
        tvTurn = findViewById(R.id.tv_turn)
        tvLog = findViewById(R.id.tv_log)
        
        // 设置按钮点击事件
        setupButtons()
        
        // 更新UI
        updateUI()
    }
    
    private fun setupButtons() {
        // 症状按钮
        val symptomButtons = listOf<Button>(
            findViewById(R.id.btn_symptom1),
            findViewById(R.id.btn_symptom2),
            findViewById(R.id.btn_symptom3),
            findViewById(R.id.btn_symptom4),
            findViewById(R.id.btn_symptom5)
        )
        
        val symptoms = listOf(
            "恶寒发热",
            "但热不寒", 
            "寒热往来",
            "腹满而吐",
            "脉微细"
        )
        
        symptomButtons.forEachIndexed { index, button ->
            button.text = symptoms[index]
            button.setOnClickListener {
                if (!gameOver) {
                    diagnose(symptoms[index])
                }
            }
        }
        
        // 方剂按钮
        val formulaButtons = listOf<Button>(
            findViewById(R.id.btn_formula1),
            findViewById(R.id.btn_formula2),
            findViewById(R.id.btn_formula3),
            findViewById(R.id.btn_formula4),
            findViewById(R.id.btn_formula5),
            findViewById(R.id.btn_formula6)
        )
        
        val formulas = listOf(
            "桂枝汤", "白虎汤", "小柴胡汤",
            "理中丸", "四逆汤", "乌梅丸"
        )
        
        formulaButtons.forEachIndexed { index, button ->
            button.text = formulas[index]
            button.setOnClickListener {
                if (!gameOver) {
                    treat(formulas[index])
                }
            }
        }
        
        // 重新开始按钮
        findViewById<Button>(R.id.btn_restart).setOnClickListener {
            restartGame()
        }
    }
    
    private fun diagnose(symptom: String) {
        // 简单的六经辨证
        currentChannel = when {
            symptom.contains("恶寒") && symptom.contains("发热") -> "太阳"
            symptom.contains("但热不寒") -> "阳明"
            symptom.contains("寒热往来") -> "少阳"
            symptom.contains("腹满") && symptom.contains("吐") -> "太阴"
            symptom.contains("脉微细") -> "少阴"
            else -> "太阳"
        }
        
        addLog("🔍 辨证：$symptom → ${currentChannel}病")
        updateUI()
    }
    
    private fun treat(formula: String) {
        // 正确的方剂对应
        val correctFormula = when(currentChannel) {
            "太阳" -> "桂枝汤"
            "阳明" -> "白虎汤"
            "少阳" -> "小柴胡汤"
            "太阴" -> "理中丸"
            "少阴" -> "四逆汤"
            "厥阴" -> "乌梅丸"
            else -> "桂枝汤"
        }
        
        val effect = when(formula) {
            "桂枝汤" -> 20
            "白虎汤" -> 25
            "小柴胡汤" -> 18
            "理中丸" -> 15
            "四逆汤" -> 22
            "乌梅丸" -> 16
            else -> 10
        }
        
        if (formula == correctFormula) {
            // 方证对应
            severity = maxOf(0, severity - effect)
            addLog("✅ $formula 治疗${currentChannel}病，疗效$effect点")
        } else {
            // 方证不符
            severity = minOf(100, severity + 10)
            addLog("❌ $formula 不适用${currentChannel}病")
        }
        
        // 自然进展
        severity = minOf(100, severity + 5)
        turn++
        
        // 病情传变
        val channels = listOf("太阳", "阳明", "少阳", "太阴", "少阴", "厥阴")
        val currentIndex = channels.indexOf(currentChannel)
        if (currentIndex < channels.size - 1 && turn % 2 == 0) {
            currentChannel = channels[currentIndex + 1]
            addLog("🔄 病邪传变：${channels[currentIndex]} → $currentChannel")
        }
        
        // 检查游戏结束
        if (severity <= 0) {
            gameOver = true
            addLog("🎉 治愈成功！")
        } else if (severity >= 100 || turn > 15) {
            gameOver = true
            addLog("💀 治疗失败")
        }
        
        updateUI()
    }
    
    private fun restartGame() {
        currentChannel = "太阳"
        severity = 60
        turn = 1
        gameOver = false
        tvLog.text = ""
        updateUI()
        addLog("🔄 新游戏开始")
    }
    
    private fun updateUI() {
        tvChannel.text = "当前病位：${currentChannel}病"
        tvSeverity.text = "病情严重度：$severity/100"
        tvTurn.text = "第${turn}回合"
        
        // 更新按钮状态
        findViewById<Button>(R.id.btn_restart).isEnabled = gameOver
    }
    
    private fun addLog(message: String) {
        val current = tvLog.text.toString()
        tvLog.text = "$message\n$current"
    }
}
