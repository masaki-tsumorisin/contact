package jp.whitening.contact

import android.content.Intent
import android.os.Bundle
import android.util.Base64
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var mDatabaseReference: DatabaseReference
    private lateinit var mRoomArrayList: ArrayList<Room>
    private lateinit var mAdapter: RoomListAdapter

    private var mRoomRef: DatabaseReference? = null

    private val mEventListener = object : ChildEventListener {
        override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {

            val map = dataSnapshot.value as Map<String, String>
            val theme = map["theme"] ?: ""
            val detail = map["detail"] ?: ""
            val host = map["host"] ?: ""
            val uid = map["uid"] ?: ""
            val imageString = map["roomIcon"] ?: ""
            val bytes =
                if (imageString.isNotEmpty()) {
                    Base64.decode(imageString, Base64.DEFAULT)
                }else {
                    byteArrayOf()
                }

            val messageArrayList = ArrayList<Message>()
            val messageMap = map["messages"] as Map<String, String>?
            if (messageMap != null) {
                for (key in messageMap.keys) {
                    val temp = messageMap[key] as Map<String, String>
                    val messageBody = temp["body"] ?: ""
                    val messageName = temp["name"] ?: ""
                    val messageUid = temp["uid"] ?: ""
                    val message = Message(messageBody, messageName, messageUid, key)
                    messageArrayList.add(message)
                }
            }

            val room = Room(theme, detail, host, uid, dataSnapshot.key ?: "",
                bytes, messageArrayList)
            mRoomArrayList.add(room)
            mAdapter.notifyDataSetChanged()
        }

        override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {
            val map = dataSnapshot.value as Map<String, String>

            for (room in mRoomArrayList) {
                if (dataSnapshot.key.equals(room.roomUid)) {
                    room.messages.clear()
                    val messageMap = map["messages"] as Map<String, String>
                    for (key in messageMap.keys) {
                        val temp = messageMap[key] as Map<String, String>
                        val messageBody = temp["body"] ?: ""
                        val messageName = temp["name"] ?: ""
                        val messageUid = temp["uid"] ?: ""
                        val message = Message(messageBody, messageName, messageUid, key)
                        room.messages.add(message)
                    }
                }
            }

            mAdapter.notifyDataSetChanged()
        }


        override fun onChildRemoved(snapshot: DataSnapshot) {
            TODO("Not yet implemented")
        }

        override fun onCancelled(error: DatabaseError) {
            TODO("Not yet implemented")
        }

        override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
            TODO("Not yet implemented")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        title = "チャットルーム"

        // fabにClickリスナーを登録
        fab2.setOnClickListener { _ ->
            // ログイン済みのユーザーを取得する
            val user = FirebaseAuth.getInstance().currentUser

            // ログインしていなければログイン画面に遷移させる
            if (user == null) {
                val intent = Intent(applicationContext, LoginActivity::class.java)
                startActivity(intent)
            } else {
                val intent = Intent(applicationContext, RoomCreateActivity::class.java)
                startActivity(intent)
            }
        }

        mDatabaseReference = FirebaseDatabase.getInstance().reference
        mRoomRef = mDatabaseReference.child(ContentsPATH).child(RoomsPATH)

        mRoomArrayList = ArrayList<Room>()
        mAdapter = RoomListAdapter(this)
        RoomListView.adapter = mAdapter

        mAdapter.setRoomArrayList(mRoomArrayList)

        mRoomRef!!.addChildEventListener(mEventListener)

        RoomListView.setOnItemClickListener{parent, view, position, id ->

            val intent = Intent(applicationContext, OpenChatActivity::class.java)
            intent.putExtra("room", mRoomArrayList[position])
            startActivity(intent)
        }
    }
}