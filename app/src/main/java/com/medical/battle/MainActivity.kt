package com.medical.battle

import android.graphics.Color
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.medical.battle.models.*

class MainActivity : AppCompatActivity() {
    
    private lateinit var gameEngine: GameEngine
    private lateinit var gameLogTextView: TextView
    private lateinit var healthTextView: TextView
    private lateinit var turnTextView: TextView
    private lateinit var actionButton: Button
    private lateinit var restartButton: Button
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // 完全用代码创建UI，不使用XML
        createUI()
        
        // 初始化游戏
        initGame()
    }
    
    private fun createUI() {
        // 创建主布局
        val mainLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.WHITE)
            setPadding(16, 16, 16, 16)
        }
        
        // 标题
        val titleTextView = TextView(this).apply {
            text = "🏥 医战游戏 🦠"
            textSize = 24f
            setTextColor(Color.BLACK)
            gravity = android.view.Gravity.CENTER
            setTypeface(null, android.graphics.Typeface.BOLD)
        }
        
        // 回合显示
        turnTextView = TextView(this).apply {
            text = "回合: 1"
            textSize = 18f
            setTextColor(Color.BLUE)
            setPadding(0, 16, 0, 16)
        }
        
        // 生命值显示
        healthTextView = TextView(this).apply {
            text = "医生: 100/100 ❤️\n疾病: 100/100 💀"
            textSize = 16f
            setTextColor(Color.DKGRAY)
            setPadding(0, 8, 0, 16)
        }
        
        // 游戏日志（可滚动）
        val scrollView = ScrollView(this)
        gameLogTextView = TextView(this).apply {
            text = "游戏日志:\n"
            textSize = 14f
            setTextColor(Color.BLACK)
            setBackgroundColor(Color.LTGRAY)
            setPadding(16, 16, 16, 16)
        }
        scrollView.addView(gameLogTextView)
        
        // 按钮布局
        val buttonLayout = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = android.view.Gravity.CENTER
            setPadding(0, 16, 0, 0)
        }
        
        // 行动按钮
        actionButton = Button(this).apply {
            text = "开始治疗"
            setBackgroundColor(Color.GREEN)
            setTextColor(Color.WHITE)
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            setOnClickListener { playTurn() }
        }
        
        // 重启按钮
        restartButton = Button(this).apply {
            text = "重新开始"
            setBackgroundColor(Color.BLUE)
            setTextColor(Color.WHITE)
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            setOnClickListener { restartGame() }
        }
        
        buttonLayout.addView(actionButton)
        buttonLayout.addView(restartButton)
        
        // 将所有视图添加到主布局
        mainLayout.addView(titleTextView)
        mainLayout.addView(turnTextView)
        mainLayout.addView(healthTextView)
        mainLayout.addView(scrollView, LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            0,
            1f
        ))
        mainLayout.addView(buttonLayout)
        
        // 设置内容视图
        setContentView(mainLayout)
    }
    
    private fun initGame() {
        // 创建玩家
        val doctor = Player("张仲景", PlayerRole.DOCTOR, School.CLASSICAL_FORMULA)
        val disease = Player("温邪", PlayerRole.DISEASE, School.WARM_DISEASE)
        
        // 初始化游戏引擎
        gameEngine = GameEngine(doctor, disease)
        gameEngine.startGame()
        
        updateUI()
        appendLog("游戏开始！")
        appendLog("医生: ${doctor.name} (${doctor.school})")
        appendLog("疾病: ${disease.name} (${disease.school})")
        appendLog("医案: ${gameEngine.currentCase?.description}")
    }
    
    private fun playTurn() {
        if (gameEngine.gameOver) {
            appendLog("游戏已结束，请重新开始")
            return
        }
        
        appendLog("\n=== 第 ${gameEngine.turn} 回合 ===")
        
        // 执行回合
        gameEngine.playTurn()
        
        // 更新UI
        updateUI()
        
        // 如果游戏结束，更新按钮状态
        if (gameEngine.gameOver) {
            actionButton.isEnabled = false
            actionButton.setBackgroundColor(Color.GRAY)
            
            when (gameEngine.result) {
                GameResult.WIN -> appendLog("🎉 恭喜！治疗成功！")
                GameResult.LOSE -> appendLog("💀 治疗失败！疾病获胜！")
                GameResult.DRAW -> appendLog("🤝 平局！")
                else -> {}
            }
        }
    }
    
    private fun restartGame() {
        initGame()
        actionButton.isEnabled = true
        actionButton.setBackgroundColor(Color.GREEN)
        actionButton.text = "开始治疗"
        appendLog("\n=== 新游戏开始 ===")
    }
    
    private fun updateUI() {
        turnTextView.text = "回合: ${gameEngine.turn}"
        
        val doctor = gameEngine.doctor
        val disease = gameEngine.disease
        
        healthTextView.text = """
            👨‍⚕️ 医生: ${doctor.health}/100 ${if(doctor.isAlive()) "❤️" else "💀"}
            🦠 疾病: ${disease.health}/100 ${if(disease.isAlive()) "💀" else "✅"}
            
            手牌: 医生(${doctor.handCards.size}) | 疾病(${disease.handCards.size})
        """.trimIndent()
        
        // 更新按钮文本
        if (gameEngine.gameOver) {
            actionButton.text = "游戏结束"
        } else {
            actionButton.text = "第 ${gameEngine.turn} 回合"
        }
    }
    
    private fun appendLog(message: String) {
        gameLogTextView.append("\n$message")
        
        // 自动滚动到底部
        val scrollAmount = gameLogTextView.layout?.getLineTop(gameLogTextView.lineCount) ?: 0
        gameLogTextView.scrollTo(0, scrollAmount)
    }
}
