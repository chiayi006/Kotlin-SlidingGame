package edu.ivy.kotlin_slidinggame.ui

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.PointF
import android.graphics.RectF
import edu.ivy.kotlin_slidinggame.R
import edu.ivy.kotlin_slidinggame.logic.Player
import kotlin.concurrent.timer

class Token(res: Resources, size: Int, x:Float, y:Float, var row: Char, var column: Char, player: Player, val myView: MyView):
    TickLisener {

    private val bounds = RectF(x, y, x + size, y + size)
    private val animal: Bitmap
    private val velocity = PointF(0f, 0f)
    private val destination = PointF(x, y)
    private var falling: Boolean = false

    init {
        animal = if(player == Player.X) {
            Bitmap.createScaledBitmap(BitmapFactory.decodeResource(res, R.drawable.crab), size, size, true)
        } else {
            Bitmap.createScaledBitmap(BitmapFactory.decodeResource(res, R.drawable.fish), size, size, true)
        }

    }

    fun drawToken(c: Canvas){
        c.drawBitmap(animal, bounds.left, bounds.top, null)
    }

    fun changeVelocity(x: Float, y: Float) {
        velocity.x = x
        velocity.y = y
    }

    fun setDestination(x:Float, y:Float) {
        destination.x += x
        destination.y += y
    }

    fun isVisible(h: Float): Boolean {
        return bounds.top <= h
    }

    private fun move() {
        if (falling) {
            velocity.y *= 1.25f
        } else if (velocity.x != 0f && destination.x - bounds.left <= 15) {
            //暫停移動token
            changeVelocity(0f, 0f)
            myView.removeMovers()
            if (column > '5') {
                changeVelocity(0f, 10f)
                myView.addMovers()
                falling = true
            }
        } else if (velocity.y != 0f && destination.y - bounds.top <= 15) {
            //暫停移動token
            changeVelocity(0f, 0f)
            myView.removeMovers()
            if (row > 'E') {
                changeVelocity(0f, 10f)
                myView.addMovers()
                falling = true
            }
        }
        bounds.left += velocity.x
        bounds.top += velocity.y
    }

    override fun tick() {
        move()
    }


}