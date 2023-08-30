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

    private val REQUEST_CODE_BLUETOOTH_SCAN = 830
    private val BLUETOOTH_CONNECT_PERMISSION_REQUEST_CODE = 111


    private lateinit var leDeviceListAdapter: LeDeviceListAdapter
    lateinit var bluetoothLeScanner : BluetoothLeScanner

    lateinit var mGatt: BluetoothGatt
    fun connect(){
        if (ActivityCompat.checkSelfPermission(this@MainActivity, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {  }

    }
    fun disConnect(){
        if (ActivityCompat.checkSelfPermission(this@MainActivity, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {  }
        mGatt?.disconnect()
    }


    //스캔에 필요한 코드 정의



    private fun PackageManager.missingSystemFeature(name: String): Boolean = !hasSystemFeature(name)







    //블루투스 어댑터를 정의
    private val bluetoothAdapter: BluetoothAdapter? by lazy(LazyThreadSafetyMode.NONE) {
        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothManager.adapter
    }
    private val BluetoothAdapter.isDisabled: Boolean
        get() = !isEnabled
    private val REQUEST_ENABLE_BT = 1000

    private var mScanning: Boolean = false
    private var arrayDevices = ArrayList<BluetoothDevice>()
    private val handler = Handler()

    private val scanCallback = object : ScanCallback() {
        override fun onScanFailed(errorCode: Int) {
            super.onScanFailed(errorCode)
            Log.e(TAG, "onScanFailed - 스캔 코드: $errorCode")
        }

        @SuppressLint("MissingPermission")
        override fun onScanResult(callbackType: Int, result: ScanResult?) {
            result?.device?.let { device ->
                if (device.type == BluetoothDevice.DEVICE_TYPE_LE && !arrayDevices.contains(device)) { //기기 타입이 LE 이고, contains 해보았을 때 없는 기기를 배열에 추가
                    arrayDevices.add(device)
                    Log.d(TAG, "onScanResult: Added BLE device: ${device.name}, ${device.address}")
                }
            }
        }

        @SuppressLint("MissingPermission")
        override fun onBatchScanResults(results: MutableList<ScanResult>?) {
            results?.forEach { result ->
                result.device?.let { device ->
                    if (device.type == BluetoothDevice.DEVICE_TYPE_LE && !arrayDevices.contains(device)) {
                        arrayDevices.add(device)
                        Log.d(TAG, "onBatchScanResults: Added BLE device: ${device.name}, ${device.address}")
                    }
                }
            }
        }
    }


    private val SCAN_PERIOD = 60000
    @SuppressLint("MissingPermission")
    private fun scanLeDevice(enable: Boolean) {
        when (enable) {
            true -> {
                handler.postDelayed({
                    mScanning = false
                    bluetoothAdapter!!.bluetoothLeScanner.stopScan(scanCallback)


                }, SCAN_PERIOD.toLong())

                mScanning = true
                arrayDevices.clear()
                bluetoothAdapter!!.bluetoothLeScanner.startScan(scanCallback)

            }
            else -> {
                mScanning = false
                bluetoothAdapter!!.bluetoothLeScanner.stopScan(scanCallback)

            }
        }
    }
    

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMainBinding.inflate(layoutInflater)

        super.onCreate(savedInstanceState)
        setContentView(binding.root)


// + 이 전에 블루투스 켜져있는지 확인하는 코드 만들기
        //BLE 지원하지 않으면 앱 종료
        Log.d(TAG, "onCreate: 앱이 BLE 지원하는지 체크하는 함수 실행")
        packageManager.takeIf { it.missingSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE) }?.also {
            Toast.makeText(this, "R.string.ble_not_supported", Toast.LENGTH_SHORT).show()
            finish()
        }
        Log.d(TAG, "onCreate: 앱이 BLE 지원하는지 체크하는 함수 종료")

        checkPermissionBluetooth()



        Log.d(TAG, "onCreate: ${BluetoothAdapter.STATE_CONNECTED}")

        //디바이스 스캔 시작
        scanLeDevice(true)

        binding.btnDeviceScan.setOnClickListener {
            scanLeDevice(true)
        }





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
            Toast.makeText(this@MainActivity,"BLE 탐색을 위한 권한이 존재합니다.",Toast.LENGTH_SHORT).show()
        }


        if (ActivityCompat.checkSelfPermission(
                this@MainActivity,
                Manifest.permission.BLUETOOTH_CONNECT
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Toast.makeText(this@MainActivity,"BLE 연결을 위한 권한이 필요합니다.",Toast.LENGTH_SHORT).show()
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.BLUETOOTH_CONNECT),
                BLUETOOTH_CONNECT_PERMISSION_REQUEST_CODE
            )
            return
        } else{
            Toast.makeText(this@MainActivity,"BLE 연결을 위한 권한이 존재합니다.",Toast.LENGTH_SHORT).show()
        }
    }


    private fun selectDevice(position: Int) {
        val device = arrayDevices.get(position)
        val intent = Intent(this, DeviceControlActivity::class.java)
        intent.putExtra("address", device.getAddress())

        if (mScanning) scanLeDevice(false)
        startActivity(intent)
    }








    override fun onDestroy() {
        super.onDestroy()
    }
}