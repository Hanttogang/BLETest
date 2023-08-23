package teameverywhere.personal.bletest.adapter


import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import teameverywhere.personal.bletest.R

class LeDeviceListAdapter : BaseAdapter() {
    private val deviceList = mutableListOf<BluetoothDevice>()

    fun addDevice(device: BluetoothDevice) {
        if (!deviceList.contains(device)) {
            deviceList.add(device)
        }
    }

    override fun getCount(): Int {
        return deviceList.size
    }

    override fun getItem(position: Int): Any {
        return deviceList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    @SuppressLint("MissingPermission")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val device = getItem(position) as BluetoothDevice
        val view: View = convertView ?: LayoutInflater.from(parent?.context)
            .inflate(R.layout.item_device, parent, false)

        val deviceNameTextView: TextView = view.findViewById(R.id.deviceNameTextView)
        val deviceAddressTextView: TextView = view.findViewById(R.id.deviceAddressTextView)

        deviceNameTextView.text = device.name ?: "Unknown Device"
        deviceAddressTextView.text = device.address

        return view
    }
}
