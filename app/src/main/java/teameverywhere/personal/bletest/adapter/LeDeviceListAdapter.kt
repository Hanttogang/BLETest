package teameverywhere.personal.bletest.adapter


import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import teameverywhere.personal.bletest.R
import teameverywhere.personal.bletest.activity.MainActivity

class LeDeviceListAdapter (
    var context: MainActivity,
    var deviceNameList: ArrayList<String>,
    var deviceAddressList: ArrayList<String>,
    var onItemClicked: OnItemClicked
) : RecyclerView.Adapter<LeDeviceListAdapter.DeviceManageViewHolder>() {

    var selectedItemPosition = RecyclerView.NO_POSITION

    interface OnItemClicked{
        fun selectDevice(p:Int)
    }

    inner class DeviceManageViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_device, parent, false)) {
        val deviceName: TextView = itemView.findViewById(R.id.tvDeviceName)
        val deviceAddress: TextView = itemView.findViewById(R.id.tvDeviceAddress)
        val imgBtnSelectDevice: ImageButton = itemView.findViewById(R.id.imgBtnSelectDevice)

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeviceManageViewHolder {
        return DeviceManageViewHolder(parent)
    }

    override fun getItemCount(): Int {
        return deviceNameList.size
    }

    override fun onBindViewHolder(holder: DeviceManageViewHolder, position: Int) {
        val deviceName = deviceNameList[position]
        val deviceAddress = deviceAddressList[position]

        holder.deviceName.text = deviceName
        holder.deviceAddress.text = deviceAddress

        holder.imgBtnSelectDevice.setOnClickListener {
            val previousItemPosition = selectedItemPosition
            selectedItemPosition = position

            if (previousItemPosition != RecyclerView.NO_POSITION) {
                notifyItemChanged(previousItemPosition)
            }

            onItemClicked.selectDevice(selectedItemPosition)
        }
    }

}
