package teameverywhere.personal.bletest.activity

import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattService
import android.content.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.widget.Toast
import teameverywhere.personal.bletest.R
import teameverywhere.personal.bletest.service.BluetoothLeService

class DeviceControlActivity : AppCompatActivity() {

    private var deviceAddress: String = ""
    private var bluetoothService: BluetoothLeService? = null

    private val serviceConnection = object: ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName?) {
            bluetoothService = null
        }

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            bluetoothService = (service as BluetoothLeService.LocalBinder).service
            bluetoothService?.connect(deviceAddress)
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_device_control)

        deviceAddress = intent.getStringExtra("address", "").toString()

        val gattServiceIntent = Intent(this, BluetoothLeService::class.java)
        bindService(gattServiceIntent, serviceConnection, Context.BIND_AUTO_CREATE)

    }

    var connected: Boolean = false
    val gattUpdateReceiver = object: BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val action = intent?.action
            when (action) {

                BluetoothLeService.ACTION_GATT_CONNECTED -> connected = true
                BluetoothLeService.ACTION_GATT_DISCONNECTED -> {
                    connected = false
                    Toast.makeText(this@DeviceControlActivity, "BLE: Disconnected to device", Toast.LENGTH_SHORT).show()
                }
                BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED -> {
                    bluetoothService?.let {
                        SelectCharacteristicData(bluetoothService.getSupportedGattServices())
                    }
                }

                BluetoothLeService.ACTION_DATA_AVAILABLE -> {
                    val resp: String = intent.getStringExtra(BluetoothLeService.EXTRA_DATA)!!
                    // resp 처리 구현
                }
            }
        }




    }

    private var writeCharacteristic: BluetoothGattCharacteristic? = null
    private var notifyCharacteristic: BluetoothGattCharacteristic? = null

    private fun SelectCharacteristicData(gattServices: List<BluetoothGattService>): Boolean {
        for (gattService in gattServices) {
            var gattCharacteristics: List<BluetoothGattCharacteristic> = gattService.characteristics

            for (gattCharacteristic in gattCharacteristics) {
                when (gattCharacteristic.uuid) {
                    BluetoothLeService.UUID_DATA_WRITE -> writeCharacteristic = gattCharacteristic
                    BluetoothLeService.UUID_DATA_NOTIFY -> notifyCharacteristic = gattCharacteristic
                }
            }
        }
    }

    private fun SendData(data: String) {
        writeCharacteristic?.let {
            if (it.properties or BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE > 0) {
                bluetoothService?.writeCharacteristic(it, data)
            }
        }

        notifyCharacteristic?.let {
            if (it.properties or BluetoothGattCharacteristic.PROPERTY_NOTIFY > 0) {
                bluetoothService?.setCharacteristicNotification(it, true)
            }
        }
    }


    override fun onPause() {
        super.onPause()
        unregisterReceiver(gattUpdateReceiver)
    }

    override fun onDestroy() {
        super.onDestroy()
        unbindService(serviceConnection)
        bluetoothService = null
    }


}