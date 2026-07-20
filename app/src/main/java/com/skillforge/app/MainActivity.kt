package com.skillforge.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import com.skillforge.app.data.LocalStorage
import com.skillforge.app.ui.AppStorage
import com.skillforge.app.ui.SkillForgeApp
import com.skillforge.app.ui.theme.SkillForgeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LocalStorage.init(applicationContext)
        AppStorage.storage = LocalStorage
        setContent {
            SkillForgeTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    SkillForgeApp()
                }
            }
        }
    }
}
