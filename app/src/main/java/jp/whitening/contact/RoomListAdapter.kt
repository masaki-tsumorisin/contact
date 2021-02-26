package jp.whitening.contact

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import kotlinx.android.synthetic.main.list_rooms.view.*
import java.util.ArrayList

class RoomListAdapter(context: Context) : BaseAdapter() {
    private var mLayoutInflater: LayoutInflater
    private var mRoomArrayList = ArrayList<Room>()

    init {
        mLayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    }

    override fun getCount(): Int {
        return mRoomArrayList.size
    }

    override fun getItem(position: Int): Any {
        return mRoomArrayList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }


    override fun getView(position: Int, view: View?, parent: ViewGroup?): View {

        var convertView = view

        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.list_rooms, parent, false)
        }

        val themeText = convertView!!.themeTextView as TextView
        themeText.text = mRoomArrayList[position].theme

        val hostText = convertView.hostTextView as TextView
        hostText.text = mRoomArrayList[position].host

        val detailText = convertView.detailTextView as TextView
        detailText.text = mRoomArrayList[position].detail


        val bytes = mRoomArrayList[position].imageBytes
        if (bytes.isNotEmpty()) {
            val image = BitmapFactory.decodeByteArray(bytes, 0, bytes.size).copy(Bitmap.Config.ARGB_8888, true)
            val imageView = convertView.roomImage as ImageView
            imageView.setImageBitmap(image)
        }

        return convertView
    }

    fun setRoomArrayList(roomArrayList: ArrayList<Room>) {
        mRoomArrayList = roomArrayList
    }
}