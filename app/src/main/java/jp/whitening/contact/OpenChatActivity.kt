package jp.whitening.contact

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.preference.PreferenceManager
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_open_chat.*

class OpenChatActivity : AppCompatActivity(), View.OnClickListener, DatabaseReference.CompletionListener {

    private lateinit var mRoom: Room
    private lateinit var mAdapter: OpenChatListAdapter
    private lateinit var mMessageRef: DatabaseReference

    private val mEventListener = object : ChildEventListener {

        override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
            val map = dataSnapshot.value as Map<*, *>

            val messageUid = dataSnapshot.key ?: ""

            for (message in mRoom.messages) {
                if (messageUid == message.messageUid) {
                    return
                }
            }

            val body = map["body"] as? String ?: ""
            val name = map["name"] as? String ?: ""
            val uid = map["uid"] as? String ?: ""

            val message = Message(body, name, uid, messageUid)
            mRoom.messages.add(message)
            mAdapter.notifyDataSetChanged()
        }

        override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
            TODO("Not yet implemented")
        }


        override fun onCancelled(error: DatabaseError) {
            TODO("Not yet implemented")
        }

        override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
            TODO("Not yet implemented")
        }


        override fun onChildRemoved(snapshot: DataSnapshot) {
            TODO("Not yet implemented")
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_open_chat)

        val extras = intent.extras
        mRoom = extras!!.get("room") as Room

        title = mRoom.theme
        //Snackbar.make(findViewById(android.R.id.content), title.toString() + "へようこそ", Snackbar.LENGTH_SHORT).show()

        mAdapter = OpenChatListAdapter(this, mRoom)
        chatListView.adapter = mAdapter
        mAdapter.notifyDataSetChanged()

        val databaseReference = FirebaseDatabase.getInstance().reference
        mMessageRef = databaseReference.child(ContentsPATH).child(RoomsPATH).child(mRoom.roomUid).child(MessagesPATH)
        mMessageRef.addChildEventListener(mEventListener)

        messageSendButton.setOnClickListener(this)
    }


    override fun onClick(v: View?) {
        val im = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        im.hideSoftInputFromWindow(v!!.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)

        val databaseReference = FirebaseDatabase.getInstance().reference
        val messageRef = databaseReference.child(ContentsPATH).child(RoomsPATH).child(mRoom.roomUid).child(
            MessagesPATH)

        val data = HashMap<String, String>()

        data["uid"] = FirebaseAuth.getInstance().currentUser!!.uid

        val sp = PreferenceManager.getDefaultSharedPreferences(this)
        val name = sp.getString(NameKEY, "")
        data["name"] = name!!

        val message = messageInput.text.toString()

        if (message.isEmpty()) {
            return
        }
        data["body"] = message

        progressBar2.visibility = View.VISIBLE
        messageRef.push().setValue(data, this)

        mAdapter.notifyDataSetChanged()
    }

    override fun onComplete(databaseError: DatabaseError?, databaseReference: DatabaseReference) {
        progressBar2.visibility = View.GONE

        if (databaseError == null) {
            messageInput.editableText.clear()
            mAdapter.notifyDataSetChanged()

        } else {
            Snackbar.make(findViewById(android.R.id.content), "メッセージの送信に失敗しました", Snackbar.LENGTH_LONG).show()
        }
    }
}