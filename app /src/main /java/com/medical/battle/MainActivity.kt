package com.medical.battle

import android.graphics.Color
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // 创建纯代码UI
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.WHITE)
            setPadding(50, 50, 50, 50)
        }
        
        val title = TextView(this).apply {
            text = "🏥 医战游戏 🦠"
            textSize = 24f
            setTextColor(Color.BLACK)
            gravity = android.view.Gravity.CENTER
        }
        
        val info = TextView(this).apply {
            text = """
                医生: 张仲景 (经方派)
                疾病: 温邪 (温病派)
                状态: 战斗中...
            """.trimIndent()
            textSize = 16f
            setTextColor(Color.DKGRAY)
            setPadding(0, 30, 0, 30)
        }
        
        val status = TextView(this).apply {
            text = "医生: 100/100 ❤️\n疾病: 100/100 💀"
            textSize = 18f
            setTextColor(Color.BLUE)
            setPadding(0, 0, 0, 30)
        }
        
        val button = Button(this).apply {
            text = "开始治疗"
            setBackgroundColor(Color.GREEN)
            setTextColor(Color.WHITE)
            setOnClickListener {
                status.text = "医生: 85/100 ❤️\n疾病: 75/100 💀"
                Toast.makeText(this@MainActivity, "治疗进行中...", Toast.LENGTH_SHORT).show()
            }
        }
        
        layout.addView(title)
        layout.addView(info)
        layout.addView(status)
        layout.addView(button)
        
        setContentView(layout)
    }
}
