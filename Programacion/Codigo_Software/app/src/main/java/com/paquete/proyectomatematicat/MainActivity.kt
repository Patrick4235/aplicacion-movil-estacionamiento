package com.paquete.proyectomatematicat

import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat


class MainActivity : AppCompatActivity() {

    private val REQUEST_CODE_ENABLE_ADMIN = 1

    private lateinit var overlayPermissionLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Hacer que la actividad sea de pantalla completa
        /*window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
             //WindowInsetsController.hide(int) with WindowInsets.Type.statusBars()
        )*/

        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
            
        }

        // Registrar el lanzador del resultado de actividad
        overlayPermissionLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (Settings.canDrawOverlays(this)) {
                // El permiso fue otorgado
                Toast.makeText(this, "Permiso de superposición otorgado", Toast.LENGTH_SHORT).show()
                // Aquí puedes mostrar la ventana superpuesta
                showOverlayWindow()
            } else {
                // El permiso fue denegado
                Toast.makeText(this, "Permiso de superposición denegado", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun startLockTaskMode() {
        if (isDeviceOwner()) {
            startLockTask()
            Toast.makeText(this, "Modo de Pantalla Fija Activado", Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(this, "Modo de Pantalla Fija Activado", Toast.LENGTH_LONG).show()
        }
    }

    fun checkSystemAlertWindowPermission(view: View) {
        if (!Settings.canDrawOverlays(this)) {
            // Si no tiene el permiso, redirigimos al usuario a la configuración
            requestSystemAlertWindowPermission()
        } else {
            // Ya tiene el permiso, puedes realizar acciones con SYSTEM_ALERT_WINDOW aquí
            Toast.makeText(this, "Permiso otorgado", Toast.LENGTH_SHORT).show()
        }
    }

    fun requestSystemAlertWindowPermission() {
        val intent = Intent(
            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
            Uri.parse("package:$packageName")
        )

        overlayPermissionLauncher.launch(intent)
    }

    fun showOverlayWindow() {
        val windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        // Cargar el layout de la ventana superpuesta
        val overlayView: View = inflater.inflate(R.layout.activity_overlay_layout, null)

        // Configurar los parámetros de la ventana
        val layoutParams = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            else
                WindowManager.LayoutParams.TYPE_PHONE,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )

        layoutParams.gravity = Gravity.TOP or Gravity.LEFT
        layoutParams.x = 0
        layoutParams.y = 100

        // Mostrar la ventana superpuesta
        windowManager.addView(overlayView, layoutParams)

        // Ejemplo de cómo modificar elementos en la vista
        val textView = overlayView.findViewById<TextView>(R.id.overlayTextView)
        textView.text = "Ventana Superpuesta"
    }

    private fun isDeviceOwner(): Boolean {
        // Aquí deberías implementar la lógica que verifica si la aplicación es el propietario del dispositivo
        // En un entorno real, esto puede requerir permisos especiales o verificaciones adicionales
        return true // Cambia esto según tu lógica
    }


    /*override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_ENABLE_ADMIN) {
            if (resultCode == Activity.RESULT_OK) {
                // El usuario otorgó el permiso
                Toast.makeText(this, "Permiso de administrador otorgado", Toast.LENGTH_SHORT).show()
            } else {
                // El usuario rechazó el permiso
                Toast.makeText(this, "Permiso de administrador denegado", Toast.LENGTH_SHORT).show()
            }
        }
    }*/

    /*fun lockDevice(view: View) {
        val devicePolicyManager = getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
        val componentName = ComponentName(this, MyDeviceAdminReceiver::class.java)

        if (devicePolicyManager.isAdminActive(componentName)) {
            devicePolicyManager.lockNow()  // Esto bloqueará la pantalla inmediatamente
        }
    }*/

    fun EmpezarLeccion(view: View){
        //startLockTaskMode()
        startActivity(Intent(this, ExercisesActivity::class.java))
        //this.finish()
    }

    fun EmpezarLeccion2(view: View){
        val fragment = MissedNumberFragment()
        supportFragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).commit()
    }

}