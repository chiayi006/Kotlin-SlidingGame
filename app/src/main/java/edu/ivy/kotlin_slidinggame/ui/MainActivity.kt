package edu.ivy.kotlin_slidinggame.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {
    private lateinit var myView: MyView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var gameMode = intent.getSerializableExtra("game_mode") as GameMode
        myView = MyView(this)
        myView.setGameMode(gameMode)
        setContentView(myView)
    }

    override fun onResume() {
        super.onResume()
        myView.gotForeground()
    }

    override fun onStop() {
        super.onStop()
        myView.gotBackground()
    }

    override fun onDestroy() {
        super.onDestroy()
        myView.clearBeforeShunDown()
    }
}