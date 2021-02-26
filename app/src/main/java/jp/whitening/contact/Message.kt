package jp.whitening.contact

import java.io.Serializable

class Message(val body: String, val name: String, val uid: String, val messageUid: String): Serializable {
}