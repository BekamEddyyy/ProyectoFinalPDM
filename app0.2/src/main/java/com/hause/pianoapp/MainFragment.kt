package com.tayyar.tiletap

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.tayyar.tiletap.databinding.FragmentMainBinding
import com.tayyar.tiletap.game.GameActivity

/** 
 * MainFragment representa la pantalla principal de la aplicación. 
 * Muestra opciones para configurar el juego y permite iniciar la actividad del juego.
 */
class MainFragment : Fragment() {

    /**
     * onCreateView es llamado para inflar el layout del fragmento y configurar los listeners de los botones.
     * Aquí se manejan las interacciones del usuario para iniciar el juego con las opciones seleccionadas.
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding: FragmentMainBinding = FragmentMainBinding.inflate(inflater, container, false)

        // Configura el listener del botón para iniciar el juego
        binding.startButton.setOnClickListener {
            val speed = binding.speedInput.text.toString()
            val music = binding.musicBox.isChecked
            val vibration = binding.vibrationBox.isChecked
            val speedIncrease = binding.speedIncreaseBox.isChecked

            // Verifica si la velocidad es válida antes de iniciar el juego
            if (speed == "" || speed == "0") {
                Toast.makeText(context, "You have to select a speed", Toast.LENGTH_SHORT).show()
            } else {
                val intent = Intent(context, GameActivity::class.java).apply {
                    putExtra("speed", speed)
                    putExtra("music", music)
                    putExtra("vibration", vibration)
                    putExtra("speedIncrease", speedIncrease)
                }
                startActivity(intent)
            }
        }

        return binding.root
    }
}
