package com.immanuel.handcontrol

import android.annotation.SuppressLint
import android.bluetooth.*
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import java.util.*

class MainActivity : AppCompatActivity() {

    private val SERVICE_UUID = UUID.fromString("6e400001-b5a3-f393-e0a9-e50e24dcca9e")
    private val CHAR_UUID_RX = UUID.fromString("6e400002-b5a3-f393-e0a9-e50e24dcca9e")

    private var bluetoothGatt: BluetoothGatt? = null
    private var rxCharacteristic: BluetoothGattCharacteristic? = null
    private var isScanning = false

    private lateinit var statusLabel: TextView
    private lateinit var statusIcon: ImageView

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        statusLabel = findViewById(R.id.statusLabel)
        statusIcon = findViewById(R.id.statusIcon)
        val btnConnect = findViewById<Button>(R.id.btnConnect)
        val btnOpen = findViewById<CardView>(R.id.btnOpen)
        val btnClose = findViewById<CardView>(R.id.btnClose)

        checkBlePermissions()

        btnConnect.setOnClickListener {
            if (!isScanning) {
                statusLabel.text = "Searching..."
                startBLEScan()
            }
        }

        btnOpen.setOnClickListener { sendCommand("OPEN") }
        btnClose.setOnClickListener { sendCommand("CLOSE") }
    }

    private fun checkBlePermissions() {
        val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            arrayOf(android.Manifest.permission.BLUETOOTH_SCAN, android.Manifest.permission.BLUETOOTH_CONNECT, android.Manifest.permission.ACCESS_FINE_LOCATION)
        } else {
            arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION)
        }
        val missing = permissions.filter { ActivityCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED }
        if (missing.isNotEmpty()) ActivityCompat.requestPermissions(this, missing.toTypedArray(), 101)
    }

    @SuppressLint("MissingPermission")
    private fun startBLEScan() {
        val adapter = (getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager).adapter
        val scanner = adapter.bluetoothLeScanner ?: return
        isScanning = true
        val settings = ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY).build()
        val callback = object : ScanCallback() {
            override fun onScanResult(callbackType: Int, result: ScanResult) {
                val name = result.device.name
                if (name != null && name.contains("ESP32")) {
                    isScanning = false
                    scanner.stopScan(this)
                    connectGatt(result.device)
                }
            }
        }
        scanner.startScan(null, settings, callback)
    }

    @SuppressLint("MissingPermission")
    private fun connectGatt(device: BluetoothDevice) {
        bluetoothGatt = device.connectGatt(this, false, object : BluetoothGattCallback() {
            override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
                if (newState == BluetoothProfile.STATE_CONNECTED) {
                    gatt.discoverServices()
                } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                    runOnUiThread {
                        statusLabel.text = "Status: Disconnected"
                        statusIcon.setColorFilter(Color.parseColor("#D32F2F"))
                    }
                }
            }
            override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
                val service = gatt.getService(SERVICE_UUID)
                rxCharacteristic = service?.getCharacteristic(CHAR_UUID_RX)
                runOnUiThread {
                    statusLabel.text = "Status: Connected"
                    statusIcon.setColorFilter(Color.parseColor("#4CAF50"))
                }
            }
        }, BluetoothDevice.TRANSPORT_LE)
    }

    @SuppressLint("MissingPermission")
    private fun sendCommand(cmd: String) {
        val gatt = bluetoothGatt
        val char = rxCharacteristic
        if (gatt != null && char != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                gatt.writeCharacteristic(char, cmd.toByteArray(), BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT)
            } else {
                char.value = cmd.toByteArray()
                gatt.writeCharacteristic(char)
            }
        }
    }
}