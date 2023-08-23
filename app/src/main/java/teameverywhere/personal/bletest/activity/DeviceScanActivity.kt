package teameverywhere.personal.bletest.activity

import android.annotation.SuppressLint
import android.app.ListActivity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import teameverywhere.personal.bletest.R

private const val SCAN_PERIOD: Long = 10000

/**
 * Activity for scanning and displaying available BLE devices.
 */
class DeviceScanActivity: AppCompatActivity() {
    private fun PackageManager.missingSystemFeature(name: String): Boolean = !hasSystemFeature(name)

    @SuppressLint("MissingPermission")
    override fun onResume() {
        super.onResume()

        packageManager.takeIf { it.missingSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE) }?.also {
            Toast.makeText(this, "R.string.ble_not_supported", Toast.LENGTH_SHORT).show()
            finish()
        }

        bluetoothAdapter?.takeIf { it.isDisabled }?.apply {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
        }

        scanLeDevice(true)

    }

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
    private val scanCallback = object: ScanCallback() {
        override fun onScanFailed(errorCode: Int) {
            super.onScanFailed(errorCode)
            Toast.makeText(this@DeviceScanActivity, "BLE Scan Failed : " + errorCode, Toast.LENGTH_SHORT).show()
        }

        override fun onScanResult(callbackType: Int, result: ScanResult?) {
            result?.let {
                if (!arrayDevices.contains(it.device)) arrayDevices.add(it.device)
            }
        }

        override fun onBatchScanResults(results: MutableList<ScanResult>?) {
            results?.let {
                for (result in it) {
                    if (!arrayDevices.contains(result.device)) arrayDevices.add(result.device)
                }
            }
        }
    }

    private val SCAN_PERIOD = 10000
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

    private fun SelectDevice(position: Int) {
        val device = arrayDevices.get(position)
        val intent = Intent(this, DeviceControlActivity::class.java)
        intent.putExtra("address", device.getAddress())

        if (mScanning) scanLeDevice(false)
        startActivity(intent)
    }

}