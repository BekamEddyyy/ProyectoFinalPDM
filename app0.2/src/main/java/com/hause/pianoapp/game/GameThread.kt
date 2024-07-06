package com.tayyar.tiletap.game

import android.graphics.Canvas
import android.view.SurfaceHolder

/**
 * GameThread es el hilo que maneja el ciclo de dibujo del juego.
 * Se encarga de actualizar y renderizar el contenido del juego a una velocidad constante.
 */
class GameThread(private val surfaceHolder: SurfaceHolder, private val gameView: GameView) : Thread() {
    private var running: Boolean = false
    private val targetFPS = 60 // frames per second, la tasa a la que se quiere refrescar el Canvas

    /**
     * setRunning establece el estado de ejecución del hilo.
     * @param isRunning un booleano que indica si el hilo debe estar en ejecución.
     */
    fun setRunning(isRunning: Boolean) {
        this.running = isRunning
    }

    /**
     * run es el método principal del hilo. Maneja el ciclo de dibujo del juego,
     * que incluye bloquear el Canvas, dibujar en él y desbloquearlo.
     */
    override fun run() {
        var startTime: Long
        var timeMillis: Long
        var waitTime: Long
        val targetTime = (1000 / targetFPS).toLong()
        val firstTime = System.nanoTime()

        while (true) {
            if (running) {
                startTime = System.nanoTime()
                canvas = null

                // Medio segundo de retraso antes de que comience el juego
                if (System.nanoTime() - firstTime < 500000000) {
                    canvas = this.surfaceHolder.lockCanvas()
                    synchronized(surfaceHolder) {
                        this.gameView.drawLines(canvas!!)
                        this.gameView.drawScore(canvas!!)
                    }
                    surfaceHolder.unlockCanvasAndPost(canvas)
                    continue
                }

                try {
                    // Bloquear el canvas permite dibujar en él
                    canvas = this.surfaceHolder.lockCanvas()
                    synchronized(surfaceHolder) {
                        this.gameView.draw(canvas!!)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    if (canvas != null) {
                        try {
                            surfaceHolder.unlockCanvasAndPost(canvas)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }

                timeMillis = (System.nanoTime() - startTime) / 1000000
                waitTime = targetTime - timeMillis
                if (waitTime < 0) {
                    waitTime = 0
                }

                try {
                    sleep(waitTime)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    companion object {
        private var canvas: Canvas? = null
    }
}
