package teameverywhere.personal.bletest.service

import android.annotation.SuppressLint
import android.app.Service
import android.bluetooth.*
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import java.util.*

class BluetoothLeService: Service() {
    val STATE_DISCONNECTED = 0
    val STATE_CONNECTING = 1
    val STATE_CONNECTED = 2

    var connectionState = STATE_DISCONNECTED
    var bluetoothGatt: BluetoothGatt? = null
    var deviceAddress: String = ""
    val bluetoothAdapter: BluetoothAdapter by lazy(LazyThreadSafetyMode.NONE) {
        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothManager.adapter
    }

    companion object {
        val ACTION_GATT_CONNECTED = "ACTION_GATT_CONNECTED"
        val ACTION_GATT_DISCONNECTED = "ACTION_GATT_DISCONNECTED"
        val ACTION_GATT_SERVICES_DISCOVERED = "ACTION_GATT_DISCOVERED"
        val ACTION_DATA_AVAILABLE = "ACTION_DATA_AVAILABLE"
        val EXTRA_DATA = "EXTRA_DATA"

        //UUID 설정하기
        val UUID_DATA_NOTIFY = UUID.fromString("0000fff1-0000-1000-80000-00805f9b34fb")
        val UUID_DATA_WRITE = UUID.fromString("0000fff2-0000-1000-80000-00805f9b34fb")
        val CLIENT_CHARACTERISTIC_CONFIG = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")
    }

    val gattCallback = object: BluetoothGattCallback() {
        override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
            var intentAction = ""
            when (newState) {
                BluetoothProfile.STATE_CONNECTED -> {
                    connectionState = STATE_CONNECTED
                    broadcastUpdate(ACTION_GATT_CONNECTED)
                }
                BluetoothProfile.STATE_DISCONNECTED -> {
                    connectionState = STATE_DISCONNECTED
                    broadcastUpdate(ACTION_GATT_DISCONNECTED)
                }
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
            when (status) {
                BluetoothGatt.GATT_SUCCESS -> broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED)
                else -> {}
            }
        }
    }

    private fun broadcastUpdate(action: String) {
        sendBroadcast(Intent(action))
    }







    inner class LocalBinder: Binder() {
        val service = this@BluetoothLeService
    }

    val binder = LocalBinder()
    override fun onBind(intent: Intent?): IBinder? = binder

    @SuppressLint("MissingPermission")
    fun connect(address: String): Boolean {
        bluetoothGatt?.let {
            if (address.equals(deviceAddress)) {
                if (it.connect()) {
                    connectionState = STATE_CONNECTING
                    return true
                } else return false
            }
        }

        val device = bluetoothAdapter.getRemoteDevice(address)
        bluetoothGatt = device.connectGatt(this, false, gattCallback)
        deviceAddress = address;
        connectionState = STATE_CONNECTING
        return true
    }
}