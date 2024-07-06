package com.tayyar.tiletap.game

import android.content.Context
import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.media.AudioManager
import android.media.SoundPool
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import com.tayyar.tiletap.R
import java.util.*
import java.util.concurrent.CopyOnWriteArrayList

/**
 * GameView es la vista principal del juego donde se dibujan y manejan las tiles.
 * Maneja la lógica del juego y la interacción del usuario.
 */
class GameView(context: Context) : SurfaceView(context), SurfaceHolder.Callback {

    private val thread: GameThread
    private var tiles = LinkedList<Tile>()
    private var tempTiles = CopyOnWriteArrayList<Tile>()
    private var vibrator: Vibrator? = null

    private var blackPaint = Paint()
    private var grayPaint = Paint()
    private var redPaint = Paint()
    private var scorePaint = Paint()

    private var row = -1
    private var lastRow = -1

    private var gameOver = false
    private var gameOverOver = false // true after game over sound is played
    private var tappedWrongTile = -1
    private var startY = -1
    private var endY = -1

    private var touchedX = 0f
    private var touchedY = 0f

    private var scoreSize = 100f
    private var backGroundColor = Color.WHITE

    private var started = false

    private var soundPool: SoundPool? = null
    private var failSound: Int? = null
    private var tileSound: Int? = null
    private var playingSound: Int? = null

    private var frameNo = 0

    init {
        // Agrega el callback del SurfaceHolder
        holder.addCallback(this)

        // Instancia el hilo del juego
        thread = GameThread(holder, this)

        score = 0
        row = (0..3).random()

        // Objetos del juego
        tiles.add(Tile(blackPaint, grayPaint, redPaint, row))
        lastRow = row

        // Colores de las tiles
        blackPaint.color = Color.BLACK
        grayPaint.color = Color.GRAY
        redPaint.color = Color.RED
        scorePaint.color = Color.CYAN
        scorePaint.textSize = scoreSize

        // Carga los sonidos si la música está habilitada
        if (music && soundPool == null) {
            soundPool = SoundPool(20, AudioManager.STREAM_MUSIC, 0)
            if (failSound == null) {
                failSound = soundPool?.load(context, R.raw.failsound, 1)
            }
            if (tileSound == null) {
                tileSound = soundPool?.load(context, R.raw.a, 1)
            }
        }

        // Configura el vibrador si está habilitado
        if (vibration) {
            vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
    }

    companion object {
        var score = 0
        val screenWidth = Resources.getSystem().displayMetrics.widthPixels
        val screenHeight = Resources.getSystem().displayMetrics.heightPixels
        var music = true
        var vibration = true
        var initialSpeed = 30
    }

    /**
     * surfaceCreated es llamado cuando el SurfaceView está listo para ser utilizado.
     * Inicia el hilo del juego.
     */
    override fun surfaceCreated(surfaceHolder: SurfaceHolder) {
        thread.setRunning(true)
        if (!started) {
            // Inicia el hilo del juego
            thread.start()
            started = true
        }
    }

    override fun surfaceChanged(surfaceHolder: SurfaceHolder, i: Int, i1: Int, i2: Int) {
        // No se necesita implementar
    }

    /**
     * surfaceDestroyed es llamado cuando el SurfaceView está siendo destruido.
     * Guarda la puntuación si es un récord y detiene el hilo del juego.
     */
    override fun surfaceDestroyed(p0: SurfaceHolder) {
        saveIfHighScore(initialSpeed, score)
        thread.setRunning(false)
    }

    /**
     * destroy libera los recursos del SoundPool.
     */
    fun destroy() {
        soundPool?.release()
        soundPool = null
    }

    /**
     * restart reinicia el estado del juego para comenzar de nuevo.
     */
    fun restart() {
        if (playingSound != null) {
            soundPool?.stop(playingSound!!)
        }
        (context as GameActivity).hideReplayButton()
        Tile.speed = initialSpeed.toDouble()
        tiles.clear()
        score = 0
        tappedWrongTile = -1
        row = (0..3).random()
        // Objetos del juego
        tiles.add(Tile(blackPaint, grayPaint, redPaint, row))
        lastRow = row
        gameOver = false
        gameOverOver = false
        thread.setRunning(true)
    }

    /**
     * saveIfHighScore guarda la puntuación si es mayor que la mejor puntuación.
     */
    private fun saveIfHighScore(speed: Int, score: Int) {
        val sharedPref = context?.getSharedPreferences(
            context.getString(R.string.shared_preferences_name),
            Context.MODE_PRIVATE
        ) ?: return
        val highScore = sharedPref.getInt(speed.toString(), 0)
        if (highScore < score) {
            with(sharedPref.edit()) {
                putInt(speed.toString(), score)
                apply()
            }
        }
    }

    /**
     * draw dibuja todos los elementos del juego en el Canvas.
     */
    override fun draw(canvas: Canvas) {
        super.draw(canvas)
        frameNo++

        // Detener el juego
        if (false) {
            playingSound = soundPool?.play(failSound!!, 1f, 1f, 0, 0, 1f)
            Tile.speed = 0.0
            thread.setRunning(false)
            saveIfHighScore(initialSpeed, score)
            (context as GameActivity).showReplayButton()
            gameOverOver = true
        }

        drawLines(canvas)

        // Eliminar las tiles que están fuera de la pantalla
        if (tiles.first.outOfScreen) {
            tiles.poll()
        }
        // Dibujar nuevas tiles cuando la última está en la pantalla
        if (tiles.last.startY >= 0) {
            do {
                row = (0..3).random()
            } while (row == lastRow)

            tiles.add(Tile(blackPaint, grayPaint, redPaint, row))
            lastRow = row
        }
        // Actualizar y dibujar todas las tiles
        for (tile in tiles) {
            tile.update(frameNo)
            tile.draw(canvas)
            if (tile.gameOver) {
                gameOver = true
            }
        }
        // Dibujar tile roja si se presionó la tile equivocada
        when (tappedWrongTile) {
            0 -> canvas.drawRect(Rect(0, startY, screenWidth / 4, endY), redPaint)
            1 -> canvas.drawRect(Rect(screenWidth / 4, startY, screenWidth / 2, endY), redPaint)
            2 -> canvas.drawRect(Rect(screenWidth / 2, startY, screenWidth * 3 / 4, endY), redPaint)
            3 -> canvas.drawRect(Rect(screenWidth * 3 / 4, startY, screenWidth, endY), redPaint)
        }
        drawScore(canvas)
    }

    /**
     * drawLines dibuja las líneas de alineación en el Canvas.
     */
    fun drawLines(canvas: Canvas) {
        // Pintar el fondo
        canvas.drawColor(backGroundColor)

        // Líneas de alineación
        canvas.drawLine(
            screenWidth.toFloat() / 4,
            0f,
            screenWidth.toFloat() / 4,
            screenHeight.toFloat(),
            blackPaint
        )
        canvas.drawLine(
            screenWidth.toFloat() / 2,
            0f,
            screenWidth.toFloat() / 2,
            screenHeight.toFloat(),
            blackPaint
        )
        canvas.drawLine(
            3 * screenWidth.toFloat() / 4,
            0f,
            3 * screenWidth.toFloat() / 4,
            screenHeight.toFloat(),
            blackPaint
        )
    }

    /**
     * drawScore dibuja la puntuación en el Canvas.
     */
    fun drawScore(canvas: Canvas) {
        // Actualizar puntuación
        canvas.drawText(score.toString(), screenWidth / 2 - scoreSize / 2, scoreSize, scorePaint)
    }

    /**
     * onTouchEvent maneja los eventos táctiles para interactuar con las tiles.
     */
    override fun onTouchEvent(event: MotionEvent): Boolean {
        event.actionMasked.let { action ->
            if (action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_POINTER_DOWN) {
                event.actionIndex.let { index ->
                    if (Tile.speed > 0) {
                        touchedX = event.getX(index)
                        touchedY = event.getY(index)
                        tempTiles = CopyOnWriteArrayList(tiles)
                        for (tile in tempTiles) {
                            if (tile.checkTouch(touchedX, touchedY)) {
                                playingSound = soundPool?.play(tileSound!!, 1f, 1f, 0, 0, 1f)
                                if (Build.VERSION.SDK_INT >= 26) {
                                    vibrator?.vibrate(
                                        VibrationEffect.createOneShot(
                                            40,
                                            VibrationEffect.DEFAULT_AMPLITUDE
                                        )
                                    )
                                } else {
                                    vibrator?.vibrate(40)
                                }
                                break
                            } else if (!tile.pressed && touchedY < tile.endY && touchedY > tile.startY) {
                                // Presionó en el lugar equivocado
                                tappedWrongTile = when {
                                    (touchedX < screenWidth / 4) -> 0
                                    (touchedX < screenWidth / 2) -> 1
                                    (touchedX < 3 * screenWidth / 4) -> 2
                                    else -> 3
                                }
                                startY = tile.startY
                                endY = tile.endY
                                gameOver = true
                            }
                        }
                    }
                }
            }
        }
        return true
    }
}
