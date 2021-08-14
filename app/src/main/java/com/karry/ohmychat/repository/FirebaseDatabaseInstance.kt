package com.karry.ohmychat.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.*
import com.karry.ohmychat.model.Message
import com.karry.ohmychat.model.User
import com.karry.ohmychat.utils.Constants.KEY_BIO
import com.karry.ohmychat.utils.Constants.KEY_COLLECTION_CHAT
import com.karry.ohmychat.utils.Constants.KEY_COLLECTION_CONVERSATIONS
import com.karry.ohmychat.utils.Constants.KEY_COLLECTION_USERS
import com.karry.ohmychat.utils.Constants.KEY_EMAIL
import com.karry.ohmychat.utils.Constants.KEY_FCM_TOKEN
import com.karry.ohmychat.utils.Constants.KEY_IMAGE
import com.karry.ohmychat.utils.Constants.KEY_IS_SEEN
import com.karry.ohmychat.utils.Constants.KEY_LAST_MESSAGE
import com.karry.ohmychat.utils.Constants.KEY_MESSAGE
import com.karry.ohmychat.utils.Constants.KEY_NAME
import com.karry.ohmychat.utils.Constants.KEY_RECEIVER_ID
import com.karry.ohmychat.utils.Constants.KEY_RECEIVER_IMAGE
import com.karry.ohmychat.utils.Constants.KEY_RECEIVER_STATUS
import com.karry.ohmychat.utils.Constants.KEY_SENDER_ID
import com.karry.ohmychat.utils.Constants.KEY_SENDER_IMAGE
import com.karry.ohmychat.utils.Constants.KEY_SENDER_STATUS
import com.karry.ohmychat.utils.Constants.KEY_STATUS
import com.karry.ohmychat.utils.Constants.KEY_TIMESTAMP
import com.karry.ohmychat.utils.Constants.KEY_USER_ID

class FirebaseDatabaseInstance {
    private val database = FirebaseFirestore.getInstance()

    fun addUserInDatabase(user: User): MutableLiveData<Boolean> {
        val successAddUserToDatabase = MutableLiveData<Boolean>()
        Log.d("FirebaseFirestore", database.toString())
        database.collection(KEY_COLLECTION_USERS).document(user.id).set(user)
            .addOnSuccessListener { successAddUserToDatabase.value = true }
        return successAddUserToDatabase
    }

    fun fetchUser(userId: String): MutableLiveData<User> {
        val currentUser = MutableLiveData<User>()

        database.collection(KEY_COLLECTION_USERS).document(userId).get().addOnCompleteListener {
            if (it.isSuccessful && it.result != null) {
                val id = it.result.getString(KEY_USER_ID)
                val name = it.result.getString(KEY_NAME)
                val email = it.result.getString(KEY_EMAIL)
                val timestamp = it.result.getLong(KEY_TIMESTAMP)
                val imageBase64 = it.result.getString(KEY_IMAGE)
                val bio = it.result.getString(KEY_BIO)
                val status = it.result.getBoolean(KEY_STATUS)

                if (id != null && name != null && email != null && timestamp != null &&
                    imageBase64 != null && bio != null && status != null
                ) {
                    currentUser.value = User(id, name, email, timestamp, imageBase64, bio, status)
                }
            }
        }
        return currentUser
    }

    fun fetchAllUsers(userId: String): MutableLiveData<ArrayList<User>> {
        val result = MutableLiveData<ArrayList<User>>()
        database.collection(KEY_COLLECTION_USERS).get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful && task.result != null) {
                    val users = ArrayList<User>()
                    for (snapshot in task.result) {
                        if (userId == snapshot.getString(KEY_USER_ID)) {
                            continue
                        }
                        val id = snapshot.getString(KEY_USER_ID)!!
                        val name = snapshot.getString(KEY_NAME)!!
                        val email = snapshot.getString(KEY_EMAIL)!!
                        val timestamp = snapshot.getLong(KEY_TIMESTAMP)!!
                        val imageBase64 = snapshot.getString(KEY_IMAGE)!!
                        val bio = snapshot.getString(KEY_BIO)!!
                        val status = snapshot.getBoolean(KEY_STATUS)!!
                        val user = User(id, name, email, timestamp, imageBase64, bio, status)
//                        Log.d(this.javaClass.toString(), "fetchAllUsers: $user")
                        users.add(user)
                    }
                    result.value = users
                }
            }
        return result
    }

    fun updateToken(userId: String, newToken: String): MutableLiveData<Boolean> {
        val result = MutableLiveData<Boolean>()
        database.collection(KEY_COLLECTION_USERS).document(userId)
            .update(KEY_FCM_TOKEN, newToken)
            .addOnSuccessListener { result.value = true }
        return result
    }

    fun updateImage(userId: String, imageBase64: String): MutableLiveData<Boolean> {
        val result = MutableLiveData<Boolean>()
        database.collection(KEY_COLLECTION_USERS).document(userId)
            .update(KEY_IMAGE, imageBase64)
            .addOnSuccessListener {
                database.collection(KEY_COLLECTION_CONVERSATIONS).whereEqualTo(KEY_SENDER_ID, userId).get()
                    .addOnCompleteListener {
                        if (it.isSuccessful && it.result != null && it.result.documents.size == 1) {
                            val conversionId = it.result.documents[0].id
                            database.collection(KEY_COLLECTION_CONVERSATIONS).document(conversionId)
                                .update(KEY_SENDER_IMAGE, imageBase64)
                                .addOnSuccessListener { result.value = true }
                        } else {
                            result.value = false
                        }
                    }
                database.collection(KEY_COLLECTION_CONVERSATIONS).whereEqualTo(KEY_RECEIVER_ID, userId).get()
                    .addOnCompleteListener {
                        if (it.isSuccessful && it.result != null && it.result.documents.size == 1) {
                            val conversionId = it.result.documents[0].id
                            database.collection(KEY_COLLECTION_CONVERSATIONS).document(conversionId)
                                .update(KEY_RECEIVER_IMAGE, imageBase64)
                                .addOnSuccessListener { result.value = true }
                        } else {
                            result.value = false
                        }
                    }
            }

        return result
    }

    fun updateStatus(userId: String, status: Boolean): MutableLiveData<Boolean> {
        val result = MutableLiveData<Boolean>()
        database.collection(KEY_COLLECTION_USERS).document(userId)
            .update(KEY_STATUS, status)
            .addOnSuccessListener {
                database.collection(KEY_COLLECTION_CONVERSATIONS).whereEqualTo(KEY_SENDER_ID, userId).get()
                    .addOnCompleteListener {
                        if (it.isSuccessful && it.result != null && it.result.documents.size == 1) {
                            val conversionId = it.result.documents[0].id
                            database.collection(KEY_COLLECTION_CONVERSATIONS).document(conversionId)
                                .update(KEY_SENDER_STATUS, status)
                                .addOnSuccessListener { result.value = true }
                        } else {
                            result.value = false
                        }
                    }
                database.collection(KEY_COLLECTION_CONVERSATIONS).whereEqualTo(KEY_RECEIVER_ID, userId).get()
                    .addOnCompleteListener {
                        if (it.isSuccessful && it.result != null && it.result.documents.size == 1) {
                            val conversionId = it.result.documents[0].id
                            database.collection(KEY_COLLECTION_CONVERSATIONS).document(conversionId)
                                .update(KEY_RECEIVER_STATUS, status)
                                .addOnSuccessListener { result.value = true }
                        } else {
                            result.value = false
                        }
                    }
            }
        return result
    }

    fun logout(userId: String): MutableLiveData<Boolean> {
        val result = MutableLiveData<Boolean>()

        database.collection(KEY_COLLECTION_USERS).document(userId)
            .update(KEY_FCM_TOKEN, FieldValue.delete())
            .addOnSuccessListener { result.value = true }
        return result
    }

    fun sendMessage(message: HashMap<String, Any>): MutableLiveData<Boolean> {
        val successSendMessage = MutableLiveData<Boolean>()
        database.collection(KEY_COLLECTION_CHAT).add(message)
            .addOnCompleteListener { successSendMessage.value = true }
        return successSendMessage
    }

    fun eventListenerMessageSend(senderId: String, receiverId: String): MutableLiveData<ArrayList<Message>> {
        val messageChangeLiveData = MutableLiveData<ArrayList<Message>>()
        database.collection(KEY_COLLECTION_CHAT)
            .whereEqualTo(KEY_SENDER_ID, senderId)
            .whereEqualTo(KEY_RECEIVER_ID, receiverId)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    return@addSnapshotListener
                }
                if (value != null) {
                    val messagesAdd = ArrayList<Message>()
                    for (documentChange in value.documentChanges) {
                        if (documentChange.type == DocumentChange.Type.ADDED) {
                            val text = documentChange.document.getString(KEY_MESSAGE) ?: ""
                            val timestamp = documentChange.document.getLong(KEY_TIMESTAMP) ?: 0
                            val isSeen = documentChange.document.getBoolean(KEY_IS_SEEN) ?: false
                            val type = documentChange.document.getString("type") ?: "text"
                            val chat = Message(senderId, receiverId, text, isSeen, type, timestamp)
                            messagesAdd.add(chat)
//                            Log.d("eventListenerMessageSend", chat.toString())
                        }
                    }

                    messageChangeLiveData.value = messagesAdd
                }
            }
        return messageChangeLiveData
    }

    fun checkForConversionRemotely(senderId: String, receiverId: String): MutableLiveData<DocumentSnapshot> {
        val result = MutableLiveData<DocumentSnapshot>()
        database.collection(KEY_COLLECTION_CONVERSATIONS).whereEqualTo(KEY_SENDER_ID, senderId)
            .whereEqualTo(KEY_RECEIVER_ID, receiverId).get()
            .addOnCompleteListener {
                if (it.isSuccessful && it.result != null && it.result.documents.size > 0) {
                    result.value = it.result.documents[0]
                }
            }
        return result
    }

    fun addConversion(conversion: HashMap<String, Any>): MutableLiveData<DocumentReference> {
        val result = MutableLiveData<DocumentReference>()
        database.collection(KEY_COLLECTION_CONVERSATIONS).add(conversion)
            .addOnSuccessListener {
                result.value = it
            }
        return result
    }

    fun updateConversion(conversionId: String, message: String): MutableLiveData<Boolean> {
        val result = MutableLiveData<Boolean>()
        database.collection(KEY_COLLECTION_CONVERSATIONS).document(conversionId)
            .update(KEY_LAST_MESSAGE, message, KEY_TIMESTAMP, System.currentTimeMillis())
            .addOnCompleteListener {
                result.value = true
            }
        return result
    }

    fun fetchConversions(key: String, userId: String): MutableLiveData<List<DocumentChange>> {
        val result = MutableLiveData<List<DocumentChange>>()
        database.collection(KEY_COLLECTION_CONVERSATIONS)
            .whereEqualTo(key, userId)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    return@addSnapshotListener
                }
                if (value != null) {
                    result.value = value.documentChanges
                }
            }
        return result
    }

    fun listenerReceiver(userId: String): MutableLiveData<User> {
        val result = MutableLiveData<User>()
        database.collection(KEY_COLLECTION_USERS).document(userId).addSnapshotListener { value, error ->
            if (error != null) {
                return@addSnapshotListener
            }
            if (value != null) {
                val id = value.getString(KEY_USER_ID)
                val name = value.getString(KEY_NAME)
                val email = value.getString(KEY_EMAIL)
                val timestamp = value.getLong(KEY_TIMESTAMP)
                val imageBase64 = value.getString(KEY_IMAGE)
                val bio = value.getString(KEY_BIO)
                val status = value.getBoolean(KEY_STATUS)
                if (id != null && name != null && email != null && timestamp != null &&
                    imageBase64 != null && bio != null && status != null
                ) {
                    result.value = User(id, name, email, timestamp, imageBase64, bio, status)
                }
            }
        }
        return result
    }
}