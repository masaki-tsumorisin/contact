package jp.whitening.contact

import java.io.Serializable
import java.util.ArrayList

class Room(val theme: String, val detail: String, val host: String, val uid: String, val roomUid: String, bytes: ByteArray, val messages: ArrayList<Message>) : Serializable {
    val imageBytes: ByteArray

    init {
        imageBytes = bytes.clone()
    }
}