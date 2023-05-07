package com.chocobi.groot.view.sensor

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.chocobi.groot.R
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import android.widget.TextView


class SensorActivity : AppCompatActivity(), SensorEventListener {
    private val TAG = "SensorActivity"
    private var sensorManager: SensorManager? = null
    private var lightSensor: Sensor? = null
    private var humiditySensor: Sensor? = null
    private var temperatureSensor: Sensor? = null
    private lateinit var lightValueText: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sensor)
        lightValueText = findViewById(R.id.lightValueText)

        // 센서 매니저 인스턴스 생성
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        var deviceSensors: List<Sensor> = sensorManager!!.getSensorList(Sensor.TYPE_ALL)

        for (i in deviceSensors) {
        Log.d(TAG, "deviceSensor: $i")

        }


        // 조도 센서 인스턴스 생성
        lightSensor = sensorManager!!.getDefaultSensor(Sensor.TYPE_LIGHT)

        // 습도 센서 인스턴스 생성
        humiditySensor = sensorManager!!.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY)

        // 온도 센서 인스턴스 생성
        temperatureSensor = sensorManager!!.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE)

        // 센서 등록
        sensorManager!!.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL)
        sensorManager!!.registerListener(this, humiditySensor, SensorManager.SENSOR_DELAY_NORMAL)
        sensorManager!!.registerListener(this, temperatureSensor, SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null) return
        Log.d(
            "Sensor",
            "onSensorChanged(): sensorType=${event.sensor.type}, values=${event.values.joinToString()}"
        )

        // 센서 타입 확인
        when (event.sensor.type) {
            Sensor.TYPE_LIGHT -> {
                val lightValue = event.values[0]
                Log.d(TAG, "조도: $lightValue")
                lightValueText.text = lightValue.toString()
            }

            Sensor.TYPE_RELATIVE_HUMIDITY -> {
                val humidityValue = event.values[0]
                Log.d(TAG, "습도: $humidityValue")
            }

            Sensor.TYPE_AMBIENT_TEMPERATURE -> {
                val temperatureValue = event.values[0]
                Log.d(TAG, "온도: $temperatureValue")
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // 필요 없는 코드
    }

    override fun onPause() {
        super.onPause()

        // 센서 해제
        sensorManager!!.unregisterListener(this)
    }

    override fun onResume() {
        super.onResume()
        // 센서 등록
        sensorManager!!.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL)
        sensorManager!!.registerListener(this, humiditySensor, SensorManager.SENSOR_DELAY_NORMAL)
        sensorManager!!.registerListener(this, temperatureSensor, SensorManager.SENSOR_DELAY_NORMAL)
    }
}

