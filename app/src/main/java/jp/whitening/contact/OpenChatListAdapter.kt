package jp.whitening.contact

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.list_my_message.view.*
import kotlinx.android.synthetic.main.list_sent_message.view.*

class OpenChatListAdapter(context: Context, private val mRoom: Room) : BaseAdapter(){

    private var mLayoutInflater: LayoutInflater? = null

    init {
        mLayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    }

    override fun getCount(): Int {
        return mRoom.messages.size
    }

    override fun getItem(position: Int): Any {
        return mRoom
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getView(position: Int, view: View?, parent: ViewGroup?): View {
        var convertView = view

        val myUserId = FirebaseAuth.getInstance().currentUser!!.uid

        if (mRoom.messages[position].uid == myUserId) { //自分のメッセージは右側に、相手のメッセージは左側に配置

            convertView = mLayoutInflater!!.inflate(R.layout.list_my_message, parent, false)!!

            val body = mRoom.messages[position].body
            val name = mRoom.messages[position].name

            val bodyTextView = convertView.bodyTextView1 as TextView
            bodyTextView.text = body

            val nameTextView = convertView.nameTextView1 as TextView
            nameTextView.text = name
        }else {

            convertView = mLayoutInflater!!.inflate(R.layout.list_sent_message, parent, false)!!

            val body = mRoom.messages[position].body
            val name = mRoom.messages[position].name

            val bodyTextView = convertView.bodyTextView2 as TextView
            bodyTextView.text = body

            val nameTextView = convertView.nameTextView2 as TextView
            nameTextView.text = name
        }
        return convertView
    }
}