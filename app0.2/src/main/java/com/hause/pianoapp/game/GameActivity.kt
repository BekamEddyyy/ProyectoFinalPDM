package com.tayyar.tiletap.game

import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import com.tayyar.tiletap.R

/**
 * GameActivity es la actividad que maneja la lógica y la visualización del juego.
 * Configura las opciones del juego, ajusta la velocidad de los tiles y maneja la interfaz de usuario del juego.
 */
class GameActivity : AppCompatActivity() {

    private lateinit var gameView: GameView
    private lateinit var img: View

    /**
     * onCreate es llamado cuando la actividad es creada. Aquí se inicializan las opciones del juego,
     * se configura la velocidad de los tiles según la resolución de la pantalla, se agrega la vista del juego
     * y se maneja la visibilidad del botón de reinicio.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        // Obtener las opciones personalizables del intent
        val speed = intent.getStringExtra("speed")
        val music = intent.getBooleanExtra("music", true)
        val vibration = intent.getBooleanExtra("vibration", true)
        val speedIncrease = intent.getBooleanExtra("speedIncrease", false)

        // Configurar las opciones en GameView y Tile
        GameView.music = music
        GameView.vibration = vibration
        Tile.speedIncrease = speedIncrease

        // Configurar la velocidad de los tiles según la resolución de la pantalla
        val displayMetrics = DisplayMetrics()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            display?.getRealMetrics(displayMetrics)
        } else {
            @Suppress("DEPRECATION")
            windowManager.defaultDisplay.getMetrics(displayMetrics)
        }
        val height = displayMetrics.heightPixels
        Tile.speed = speed!!.toDouble() * height / 1280
        GameView.initialSpeed = speed.toInt()

        // Agregar la vista del juego
        val screen = (findViewById<View>(android.R.id.content) as ViewGroup).getChildAt(0) as ViewGroup
        gameView = GameView(this)
        gameView.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        screen.addView(gameView)

        // Configurar el botón de reinicio
        img = layoutInflater.inflate(R.layout.centered_image, screen, false)
        img.visibility = View.GONE
        img.setOnClickListener {
            gameView.restart()
        }
        screen.addView(img)

        // Eliminar la barra de notificaciones
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
    }

    /**
     * showReplayButton muestra el botón de reinicio en la interfaz de usuario.
     * Este método se llama desde el hilo principal.
     */
    fun showReplayButton() {
        this@GameActivity.runOnUiThread {
            img.visibility = View.VISIBLE
        }
    }

    /**
     * hideReplayButton oculta el botón de reinicio en la interfaz de usuario.
     * Este método se llama desde el hilo principal.
     */
    fun hideReplayButton() {
        this@GameActivity.runOnUiThread {
            img.visibility = View.GONE
        }
    }

    /**
     * onDestroy es llamado cuando la actividad está a punto de ser destruida.
     * Aquí se llama al método destroy() de GameView para liberar recursos.
     */
    override fun onDestroy() {
        gameView.destroy()
        super.onDestroy()
    }
}
