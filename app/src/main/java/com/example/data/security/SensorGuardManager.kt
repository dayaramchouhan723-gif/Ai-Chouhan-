package com.example.data.security

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlin.math.sqrt

class SensorGuardManager(
    context: Context,
    private val onBreachDetected: () -> Unit
) : SensorEventListener {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as? SensorManager
    private val accelerometer = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

    private var isArmed = false
    private var lastX = 0f
    private var lastY = 0f
    private var lastZ = 0f
    private var isFirstReading = true

    // Sensitivity threshold (m/s^2)
    var threshold = 2.5f

    fun setSensitivity(sensitivity: String) {
        threshold = when (sensitivity.lowercase()) {
            "low" -> 4.5f
            "high" -> 1.8f
            "hyper-sensitive" -> 0.8f
            else -> 2.5f // Medium
        }
    }

    fun arm() {
        if (!isArmed && accelerometer != null) {
            isFirstReading = true
            sensorManager?.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI)
            isArmed = true
        }
    }

    fun disarm() {
        if (isArmed) {
            sensorManager?.unregisterListener(this)
            isArmed = false
            isFirstReading = true
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (!isArmed || event == null) return
        val x = event.values[0]
        val y = event.values[1]
        val z = event.values[2]

        if (isFirstReading) {
            lastX = x
            lastY = y
            lastZ = z
            isFirstReading = false
            return
        }

        val deltaX = x - lastX
        val deltaY = y - lastY
        val deltaZ = z - lastZ

        lastX = x
        lastY = y
        lastZ = z

        val totalMovement = sqrt((deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ).toDouble()).toFloat()

        if (totalMovement > threshold) {
            onBreachDetected()
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit
}
