package edu.ivy.kotlin_slidinggame.ui

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.media.MediaPlayer
import android.os.Looper
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatImageView
import edu.ivy.kotlin_slidinggame.R
import edu.ivy.kotlin_slidinggame.SettingsActivity
import edu.ivy.kotlin_slidinggame.logic.GameBoard
import edu.ivy.kotlin_slidinggame.logic.Player
import kotlin.concurrent.timer

class MyView(c: Context) : AppCompatImageView(c), TickLisener {
    private val p = Paint()
    private val p2 = Paint()
    private var sideMargin = 0f
    private var verticalMargin = 0f
    private var gridLength = 0f
    private var mathDone = false
    private var w = 0f
    private var h = 0f
    private val buttons: ArrayList<Buttons> = ArrayList()
    private val tokens: ArrayList<Token> = ArrayList()
    private val timer: Timer = Timer(Looper.getMainLooper())
    private var engine = GameBoard()
    private var player1WinCount = 0
    private var player2WinCount = 0
    private lateinit var mode: GameMode
    private var soundtrack: MediaPlayer? = null
    private var player: Player = Player.X
    private var movers: Int = 0

    init {
        p2.textSize = 60f
        p2.color = Color.BLACK
        setImageResource(R.drawable.background)
        scaleType = ScaleType.FIT_XY
        soundtrack = MediaPlayer.create(context, R.raw.music)
        soundtrack!!.isLooping = true
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (!mathDone) {
            p.strokeWidth = width * 0.005f
            sideMargin = width * 0.2f
            verticalMargin = (height - width + 2 * sideMargin) / 2
            gridLength = (width - 2 * sideMargin) / 5
            mathDone = true
            w = width.toFloat()
            h = height.toFloat()
            makeButtons()

            timer.register(this)
        }
        // 繪製Grid
        drawGrid(canvas)
        // 繪製Button
        for (button in buttons) {
            button.drawButtons(canvas)
        }
        // 檢查有無掉出螢幕的token
        for (token in tokens) {
            if (!token.isVisible(h)) {
                token.changeVelocity(0f, 0f)
                movers--
                timer.unRegister(token)
                tokens.remove(token)
                break
            }
        }
        // 繪製Token
        for (token in tokens) {
            token.drawToken(canvas)
        }
        // 寫出勝利次數
        canvas.drawText(
            "${resources.getString(R.string.player_one_win_counts)} $player1WinCount",
            50f,
            150f,
            p2
        )
        canvas.drawText(
            "${resources.getString(R.string.player_two_win_counts)} $player2WinCount",
            50f,
            250f,
            p2
        )
        // Token靜止時
        if (!isAnyTokenMoving()) {
            val winner = engine.checkWinners()
            if (winner != Player.BLANK) {
                timer.pause()
                val ab = AlertDialog.Builder(context)
                ab.setTitle(R.string.gameOverTitle)
                when (winner) {
                    Player.TIE -> {
                        ab.setMessage(R.string.tie_game)
                    }

                    Player.X -> {
                        ab.setMessage(R.string.player_one_wins)
                    }

                    else -> {
                        ab.setMessage(R.string.player_two_wins)
                    }
                }
                ab.setCancelable(false)
                ab.setPositiveButton(R.string.yes) { _, _ ->
                    restartGame()
                }
                ab.setNegativeButton(R.string.no) { _, _ ->
                    (context as Activity).finish()
                }
                ab.create().show()

                if (winner == Player.X) {
                    player1WinCount++
                } else if (winner == Player.O) {
                    player2WinCount++
                }
            }
            // AI下棋的時間點是在token靜止、game winner is blank、mode is one player、current player is AI
            else if (mode == GameMode.ONE_PLAYER && player == Player.O) {
                val choice = engine.aiMove()
                val button = buttons[choice]
                engine.submitMove(button.getChar())

                // 0,1,2,3,4 是橫向的buttons, 5,6,7,8,9 是A,B,C,D,E的buttons
                val token = if (choice < 5) {
                    Token(resources, gridLength.toInt(), button.getX(), button.getY(), ('A'.code - 1).toChar(), button.getChar(), player, this)
                } else {
                    Token(resources, gridLength.toInt(), button.getX(), button.getY(), button.getChar(), '0', player, this)
                }
                changePlayer()

                tokens.add(token)
                timer.register(token)
                val neighbors: ArrayList<Token> = ArrayList()
                neighbors.add(token)
                if (button.isColumnButton()) {
                    moveVerticalNeighbors(button, neighbors)
                } else {
                    moveHorizontalNeighbors(button, neighbors)
                }


            }
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (isAnyTokenMoving()) {
            for (button in buttons) {
                button.unpress()
            }
            return true
        }
        if (event.action == MotionEvent.ACTION_DOWN) {
            var buttonTouching = false
            for (button in buttons) {
                if (button.contains(event.x, event.y)) {
                    engine.submitMove(button.getChar())
                    buttonTouching = true
                    button.press()

                    val token: Token = if (button.isColumnButton()) {
                        Token(resources, gridLength.toInt(), button.getX(), button.getY(), ('A'.code - 1).toChar(), button.getChar(), player, this)
                    } else {
                        Token(resources, gridLength.toInt(), button.getX(), button.getY(), button.getChar(), '0', player, this)
                    }
                    changePlayer()
                    tokens.add(token)
                    timer.register(token)
                    val neighbors: ArrayList<Token> = ArrayList()
                    neighbors.add(token)
                    if (button.isColumnButton()) {
                        moveVerticalNeighbors(button, neighbors)
                    } else {
                        moveHorizontalNeighbors(button, neighbors)
                    }
                    break
                }
            }
            if (!buttonTouching) {
                Toast.makeText(
                    context,
                    resources.getString(R.string.button_notification),
                    Toast.LENGTH_SHORT
                ).show()
            }
        } else if (event.action == MotionEvent.ACTION_UP) {

        }
        invalidate()
        return true
    }

    private fun makeButtons() {
        val charArray1 = arrayOf('1', '2', '3', '4', '5')
        for (i in 0..4) {
            buttons.add(
                Buttons(
                    resources,
                    charArray1[i],
                    gridLength.toInt(),
                    sideMargin + i * gridLength,
                    verticalMargin - gridLength
                )
            )

        }
        val charArray2 = arrayOf('A', 'B', 'C', 'D', 'E')
        for (i in 0..4) {
            buttons.add(
                Buttons(
                    resources,
                    charArray2[i],
                    gridLength.toInt(),
                    sideMargin - gridLength,
                    verticalMargin + i * gridLength
                )
            )
        }

    }

    private fun drawGrid(c: Canvas) {
        for (i in 0..5) {
            c.drawLine(
                sideMargin,
                verticalMargin + i * gridLength,
                w - sideMargin,
                verticalMargin + i * gridLength,
                p
            )
        }
        for (i in 0..5) {
            c.drawLine(
                sideMargin + i * gridLength,
                verticalMargin,
                sideMargin + i * gridLength,
                h - verticalMargin,
                p
            )
        }
    }

    private fun moveVerticalNeighbors(buttons: Buttons, neighborList: ArrayList<Token>) {
        val rowLetters = arrayOf('A', 'B', 'C', 'D', 'E')
        for (i in rowLetters.indices) {
            val animal: Token? = findAnimal(rowLetters[i], buttons.getChar())
            if (animal != null) {
                neighborList.add(animal)
            } else {
                break
            }
        }
        for (token in neighborList) {
            token.setDestination(0f, gridLength)
            token.changeVelocity(0f, 10f)
            movers++

            token.row = (token.row + 1)
        }
    }

    private fun moveHorizontalNeighbors(buttons: Buttons, neighborList: ArrayList<Token>) {
        val columnLetters = arrayOf('1', '2', '3', '4', '5')
        for (i in columnLetters.indices) {
            val animal: Token? = findAnimal(buttons.getChar(), columnLetters[i])
            if (animal != null) {
                neighborList.add(animal)
            } else {
                break
            }
        }
        for (token in neighborList) {
            token.setDestination(gridLength, 0f)
            token.changeVelocity(10f, 0f)
            movers++

            token.column = (token.column + 1)
        }
    }

    private fun findAnimal(row: Char, column: Char): Token? {
        for (token in tokens) {
            if (token.row == row && token.column == column) {
                return token
            }
        }
        return null
    }

    private fun restartGame() {
        player = Player.X
        timer.clearAll()
        timer.unpause()
        tokens.clear()
        buttons.clear()
        mathDone = false
        engine = GameBoard()
        invalidate()
    }

    override fun tick() {
        invalidate()
    }

    fun gotBackground() {
        timer.pause()
        if (soundtrack!!.isPlaying) {
            soundtrack!!.pause()
        }
    }

    fun gotForeground() {
        timer.unpause()
        if (SettingsActivity.isSoundOn(context)) {
            soundtrack!!.start()
        }
    }

    fun clearBeforeShunDown() {
        soundtrack!!.release()
        soundtrack = null
    }

    fun setGameMode(m: GameMode) {
        mode = m
    }

    fun addMovers() {
        movers++
    }

    fun removeMovers() {
        movers--
    }

    private fun changePlayer() {
        player = if (player == Player.X) Player.O else Player.X
    }

    private fun isAnyTokenMoving(): Boolean {
        return movers > 0
    }
}