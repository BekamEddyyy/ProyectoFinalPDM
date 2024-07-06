package com.tayyar.tiletap

import android.os.Bundle
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.tayyar.tiletap.databinding.ActivityMainBinding

/**
 * MainActivity es la actividad principal de la aplicación que maneja la navegación
 * y la interacción con el drawer layout.
 */
class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var imm: InputMethodManager

    /**
     * onCreate es llamado cuando la actividad es creada. Aquí se inicializa el View Binding,
     * el controlador de navegación, el drawer layout y el InputMethodManager. Además,
     * se configura la navegación y el comportamiento del drawer.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager

        // Configuración del drawer y el controlador de navegación
        navController = findNavController(R.id.pianoTilesNavHostFragment)
        drawerLayout = binding.drawerLayout
        NavigationUI.setupActionBarWithNavController(this, navController, drawerLayout)
        NavigationUI.setupWithNavController(binding.navView, navController)

        // Bloquea o desbloquea el drawer según la pantalla actual
        navController.addOnDestinationChangedListener { controller, destination, _ ->
            if (destination.id == controller.graph.startDestination) {
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
            } else {
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
            }
        }
    }

    /**
     * dispatchTouchEvent es llamado para interceptar toques en la pantalla. Si se toca fuera de un
     * EditText, se oculta el teclado y se quita el foco del EditText.
     */
    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (currentFocus is EditText) {
            imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
            currentFocus?.clearFocus()
        }
        return super.dispatchTouchEvent(ev)
    }

    /**
     * onSupportNavigateUp maneja la navegación hacia arriba en la jerarquía de navegación
     * utilizando el controlador de navegación y el drawer layout.
     */
    override fun onSupportNavigateUp(): Boolean {
        return NavigationUI.navigateUp(navController, drawerLayout)
    }
}
