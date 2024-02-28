package edu.ivy.kotlin_slidinggame.ui

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.RectF
import edu.ivy.kotlin_slidinggame.R

class Buttons(res: Resources, label: Char, size: Int, x: Float, y: Float) {
    private val unpressedImage: Bitmap = Bitmap.createScaledBitmap(
        BitmapFactory.decodeResource(
            res,
            R.drawable.unpressed_button,
            null
        ), size, size, true
    )
    private val pressedImage: Bitmap = Bitmap.createScaledBitmap(
        BitmapFactory.decodeResource(
            res,
            R.drawable.pressed_button,
            null
        ), size, size, true
    )
    private var pressed: Boolean = false
    private var char: Char = label
    private val bounds: RectF = RectF(x, y, x + size, y + size)

    fun drawButtons(c: Canvas) {
        if (pressed) {
            c.drawBitmap(pressedImage, bounds.left, bounds.top, null)
        } else {
            c.drawBitmap(unpressedImage, bounds.left, bounds.top, null)
        }
    }

    fun contains(x: Float, y: Float): Boolean {
        return bounds.contains(x, y)
    }

    fun press() {
        pressed = true
    }

    fun unpress() {
        pressed = false
    }

    fun getX(): Float {
        return bounds.left
    }

    fun getY(): Float {
        return bounds.top
    }

    fun getChar(): Char {
        return char
    }

    fun isColumnButton(): Boolean {
        return char == '1' || char == '2' || char == '3' || char == '4' || char == '5'
    }
}