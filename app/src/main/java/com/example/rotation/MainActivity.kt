package com.example.rotation

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import com.example.rotation.databinding.ActivityMainBinding
import kotlin.math.abs
import kotlin.math.sign

class MainActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var sensorManager: SensorManager

    private val rotationMatrix = FloatArray(9)
    private val orientationAngles = FloatArray(3)

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager

        sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)?.also { rotationSensor ->
            sensorManager.registerListener(
                this,
                rotationSensor,
                SensorManager.SENSOR_DELAY_NORMAL,
                SensorManager.SENSOR_DELAY_UI
            )
        }

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null) {
            return
        }

        if (event.sensor.type == Sensor.TYPE_ROTATION_VECTOR) {
            updateOrientationAngles(event.values)
        }
    }

    fun updateOrientationAngles(rotationVector: FloatArray) {
        SensorManager.getRotationMatrixFromVector(rotationMatrix, rotationVector)
        val orientation = SensorManager.getOrientation(rotationMatrix, orientationAngles)


        val azimuthAngle = toDegrees(orientation[0])
        val pitchAngle = toDegrees(orientation[1])
        val rollAngle = toDegrees(orientation[2])

        binding.apply {
            pitchTextview.text = "%+.1f°".format(pitchAngle)
            rollTextview.text = "%+.1f°".format(rollAngle)
            codeTextview.text = generateCode(pitchAngle, rollAngle)
        }

    }

    fun toDegrees(radians: Float): Double {
        return Math.toDegrees(radians.toDouble())
    }

    fun generateCode(pitchAngle: Double, rollAngle: Double): String {
        var x = 0;

        if (abs(rollAngle) > 15) {
            x = 1;
        }
        if (abs(rollAngle) > 30) {
            x = 2;
        }
        x *= sign(rollAngle).toInt()

        var y = 0;
        if (abs(pitchAngle) > 15) {
            y = 1;
        }
        if (abs(pitchAngle) > 30) {
            y = 2;
        }
        y *= sign(pitchAngle).toInt()

        return "%+d%+d".format(x, y)
    }
}