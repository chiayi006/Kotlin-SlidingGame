package edu.ivy.kotlin_slidinggame.ui

import android.os.Handler
import android.os.Looper
import android.os.Message

class Timer(looper: Looper) : Handler(looper) {
    private val listeners: ArrayList<TickLisener> = ArrayList()
    private var paused = false
    private val delay: Long = 10

    init {
        sendMessageDelayed(Message.obtain(), 0)
    }

    override fun handleMessage(msg: Message) {
        if (!paused) {
            notifyListeners()
        }
        sendMessageDelayed(Message.obtain(), delay)
    }

    fun register(t: TickLisener) {
        listeners.add(t)
    }

    fun unRegister(t: TickLisener) {
        listeners.remove(t)
    }

    fun clearAll() {
        listeners.clear()
    }

    fun pause() {
        paused = true
    }

    fun unpause() {
        paused = false
    }

    fun notifyListeners() {
        for (listener in listeners) {
            listener.tick()
        }
    }
}
