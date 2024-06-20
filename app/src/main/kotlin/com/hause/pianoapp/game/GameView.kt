package com.tayyar.tiletap.game

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.SurfaceHolder
import android.view.SurfaceView

class GameView(context: Context) : SurfaceView(context), SurfaceHolder.Callback {

    private val thread: GameThread

    private val blackPaint = Paint()

    init {
        holder.addCallback(this)
        thread = GameThread(holder, this)

        blackPaint.color = Color.BLACK
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        thread.setRunning(true)
        thread.start()
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        thread.setRunning(false)
    }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)
        canvas.drawColor(Color.WHITE)
        canvas.drawLine(
            width.toFloat() / 4,
            0f,
            width.toFloat() / 4,
            height.toFloat(),
            blackPaint
        )
        canvas.drawLine(
            width.toFloat() / 2,
            0f,
            width.toFloat() / 2,
            height.toFloat(),
            blackPaint
        )
        canvas.drawLine(
            3 * width.toFloat() / 4,
            0f,
            3 * width.toFloat() / 4,
            height.toFloat(),
            blackPaint
        )
    }
}
