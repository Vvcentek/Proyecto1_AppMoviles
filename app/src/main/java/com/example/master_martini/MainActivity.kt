package com.example.master_martini

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.master_martini.navigation.MasterMartiniNavHost
import com.example.master_martini.ui.theme.Master_MartiniTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Master_MartiniTheme {
                MasterMartiniNavHost()
            }
        }
    }
}