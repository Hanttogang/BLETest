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
import java.util.*

private const val SCAN_PERIOD: Long = 10000
private const val TAG = "MainActivity"


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private lateinit var leDeviceListAdapter: LeDeviceListAdapter
    lateinit var bluetoothLeScanner : BluetoothLeScanner

    private val REQUEST_CODE_BLUETOOTH_SCAN = 823




    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMainBinding.inflate(layoutInflater)

        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        checkPermissionBluetooth()


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




    override fun onDestroy() {
        super.onDestroy()

    }

}