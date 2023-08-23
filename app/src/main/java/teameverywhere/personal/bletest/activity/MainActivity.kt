package teameverywhere.personal.bletest.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.ListActivity
import android.app.Service
import android.bluetooth.*
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import teameverywhere.personal.bletest.R
import teameverywhere.personal.bletest.adapter.LeDeviceListAdapter
import teameverywhere.personal.bletest.databinding.ActivityMainBinding
import teameverywhere.personal.bletest.util.BluetoothUtils.Companion.findResponseCharacteristic
import java.util.*

private const val SCAN_PERIOD: Long = 10000
private const val TAG = "MainActivity"


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    val SERVICE = "0000fff0-0000-1000-8000-00805f9b34fb"
    val READ_UUID = "0000fff2-0000-1000-8000-00805f9b34fb"


    private lateinit var leDeviceListAdapter: LeDeviceListAdapter
    lateinit var bluetoothLeScanner : BluetoothLeScanner

    lateinit var mGatt: BluetoothGatt
    fun connect(){
        if (ActivityCompat.checkSelfPermission(this@MainActivity, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {  }
        val service = mGatt.getService(UUID.fromString(SERVICE))
        val characteristic = service.getCharacteristic(UUID.fromString(READ_UUID))

        val command = "testCommand"
        val writeByte = command.toByteArray()
        characteristic.setValue(writeByte)
        mGatt.writeCharacteristic(characteristic)


    }

    fun disConnect(){
        if (ActivityCompat.checkSelfPermission(this@MainActivity, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {  }
        mGatt?.disconnect()
    }


    //스캔에 필요한 코드 정의
    private val REQUEST_CODE_BLUETOOTH_SCAN = 823

    //블루투스 어댑터를 정의
    private val bluetoothAdapter: BluetoothAdapter? by lazy(LazyThreadSafetyMode.NONE) {
        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothManager.adapter
    }


    private val bluetoothManager: BluetoothManager by lazy {
        getSystemService(BluetoothManager::class.java)
    }


    private fun PackageManager.missingSystemFeature(name: String): Boolean = !hasSystemFeature(name)



    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMainBinding.inflate(layoutInflater)

        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        checkPermissionBluetooth()

        //BLE 지원하지 않으면 앱 종료
        packageManager.takeIf { it.missingSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE) }?.also {
            Toast.makeText(this, "R.string.ble_not_supported", Toast.LENGTH_SHORT).show()
            finish()
        }



        bluetoothLeScanner = bluetoothAdapter!!.bluetoothLeScanner
        val scanSettings: ScanSettings = ScanSettings.Builder()
            .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
            .build()
        bluetoothLeScanner?.startScan(null, scanSettings, scanCallback)


    }

    private fun checkPermissionBluetooth() {

        //권한 체크 및 요청
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH_SCAN
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Toast.makeText(this@MainActivity,"BLE 탐색을 위한 권한이 필요합니다.",Toast.LENGTH_SHORT).show()
            Log.d(TAG,"BLE 권한 요청")
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.BLUETOOTH_SCAN), REQUEST_CODE_BLUETOOTH_SCAN)
            return
        } else {

        }


    }

    //스캔 결과값을 받아올 콜백
    val scanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult?) {
            if (ActivityCompat.checkSelfPermission(this@MainActivity, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) { return }
            val device: BluetoothDevice? = result?.device
            val deviceName: String = device?.name ?: "not device name"
            val deviceAddress: String = device?.address ?: "not address"
            val rssi: Int = result?.rssi ?: 0


        }

        override fun onScanFailed(errorCode: Int) {
            // 스캔 실패 처리
        }
    }




    var readMsg = ""
    val gattCallback = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                Log.d(TAG,"연결성공")
                if (ActivityCompat.checkSelfPermission(this@MainActivity, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {  }
                mGatt?.discoverServices()
            }
            else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                Log.d(TAG,"연결해제")
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            when(status){
                BluetoothGatt.GATT_SUCCESS -> {
                    Log.d(TAG,"블루투스 셋팅완료")
                    if (ActivityCompat.checkSelfPermission(this@MainActivity, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {  }

                    val respCharacteristic = gatt?.let {
                        findResponseCharacteristic(it)
                    }
                    if( respCharacteristic == null ) {
                        Log.e(TAG, "블루투스 커맨드를 찾지 못하였습니다.")
                        return
                    }
                    gatt.setCharacteristicNotification(respCharacteristic, true)
                    val descriptor:BluetoothGattDescriptor = respCharacteristic.getDescriptor(UUID.fromString("00002902-0000-1000-8000-00805f9b34fb"))
                    descriptor.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                    gatt.writeDescriptor(descriptor)
                }
                else -> {
                    Log.e(TAG,"블루투스 셋팅실패")
                }
            }
        }

        override fun onCharacteristicWrite(gatt: BluetoothGatt?, characteristic: BluetoothGattCharacteristic?, status: Int) {
            super.onCharacteristicWrite(gatt, characteristic, status)
            when(status){
                BluetoothGatt.GATT_SUCCESS -> {
                    Log.d(TAG,"데이터 보내기 성공")
                }
                else -> {
                    Log.d(TAG,"데이터 보내기 실패")
                }
            }
        }

        //안드로이드 13이상 호출
        override fun onCharacteristicChanged(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic, value: ByteArray) {
            super.onCharacteristicChanged(gatt, characteristic, value)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
                Log.d(TAG,"블루투스 수신성공")
                readMsg = String(value)
            }
        }

        //안드로이드 12까지 호출
        override fun onCharacteristicChanged(gatt: BluetoothGatt?, characteristic: BluetoothGattCharacteristic?) {
            super.onCharacteristicChanged(gatt, characteristic)
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S) {
                Log.d(TAG,"블루투스 수신성공")
                readMsg = characteristic?.getStringValue(0).toString()
            }
        }
    }



    override fun onDestroy() {
        super.onDestroy()
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) { }
        bluetoothLeScanner?.stopScan(scanCallback)
    }
}