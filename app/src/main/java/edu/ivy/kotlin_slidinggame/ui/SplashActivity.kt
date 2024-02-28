package edu.ivy.kotlin_slidinggame.ui

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import edu.ivy.kotlin_slidinggame.R
import edu.ivy.kotlin_slidinggame.SettingsActivity

class SplashActivity: AppCompatActivity() {
    private lateinit var singlePlayerButton: ImageView
    private lateinit var twoPlayerButton: ImageView
    private lateinit var questionButton: ImageView
    private lateinit var settingButton: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash_activity)
        singlePlayerButton = findViewById(R.id.imageView1)
        twoPlayerButton = findViewById(R.id.imageView2)
        questionButton = findViewById(R.id.question_mark)
        settingButton = findViewById(R.id.setting)

        singlePlayerButton.setOnClickListener{
            val i = Intent(this, MainActivity::class.java)
            i.putExtra("game_mode", GameMode.ONE_PLAYER)
            startActivity(i)
        }

        twoPlayerButton.setOnClickListener{
            val i = Intent(this, MainActivity::class.java)
            i.putExtra("game_mode", GameMode.TWO_PLAYER)
            startActivity(i)
        }

        questionButton.setOnClickListener{
            val ab: AlertDialog.Builder = AlertDialog.Builder(this)
            ab.setTitle(R.string.about_game)
            ab.setMessage(R.string.about_description)
            ab.setCancelable(false)
            ab.setPositiveButton(R.string.okay_button) { _, _ ->
                println("done.")
            }
            ab.create().show()
        }

        settingButton.setOnClickListener{
            startActivity(Intent(this, SettingsActivity::class.java))
        }

    }
}