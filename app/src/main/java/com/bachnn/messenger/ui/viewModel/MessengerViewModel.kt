package com.bachnn.messenger.ui.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.bachnn.messenger.base.BaseViewModel
import com.bachnn.messenger.constants.FirebaseConstants
import com.bachnn.messenger.data.model.Message
import com.bachnn.messenger.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class MessengerViewModel @Inject constructor(
    val auth: FirebaseAuth,
    private val fireStore: FirebaseFirestore
) : BaseViewModel() {

    private lateinit var currentUser: User

    private var _messages = MutableLiveData<ArrayList<Message>>()

    val messages : LiveData<ArrayList<Message>> = _messages

    private var _isVisibilitySending = MutableLiveData<Boolean>()

    val isVisibilitySending: LiveData<Boolean> = _isVisibilitySending

    lateinit var group: String

    init {

        viewModelScope.launch {

            val doc = fireStore.collection(FirebaseConstants.pathUser)
                .document(auth.currentUser?.uid.toString()).get().await()
            if (doc.exists()) {
                val name = doc.getString(FirebaseConstants.name).toString()
                val email = doc.getString(FirebaseConstants.email).toString()
                val photoUrl = doc.getString(FirebaseConstants.photoUrl).toString()
                val emailVerified = doc.getString(FirebaseConstants.emailVerified).toString()

                currentUser = User(
                    doc.id,
                    name,
                    email,
                    photoUrl,
                    emailVerified,
                    ""
                )
            }

        }
    }


    fun setIdMessage(idGroup: String) {
        val current: String = auth.currentUser?.uid!!
        group = if (current > idGroup) {
            "${current}-${idGroup}"
        } else {
            "${idGroup}-${current}"
        }

        fireStore.collection(FirebaseConstants.pathMessages).document(group).collection(group)
            .limit(30)
            .orderBy(FirebaseConstants.timestamp, Query.Direction.DESCENDING)
            .get().addOnCompleteListener {it ->

                val docs = it.result.documents

                var listMessages = ArrayList<Message>()

                if (docs.size > 0) {
                    docs.forEach { doc ->
                        val message = Message(
                            doc.data?.get("idFrom").toString(),
                            doc.data?.get("idTo").toString(),
                            doc.data?.get("timestamp").toString(),
                            doc.data?.get("content").toString(),
                            doc.data?.get("type").toString()
                        )
                        listMessages.add(message)
                    }
                    _messages.postValue(listMessages)
                }

            }

    }


    fun sendMessage(toID: String, content: String, type: String,timestamps: String, token: String) {

        val messageData : MutableMap<String, Any> = HashMap()

        messageData[FirebaseConstants.idFrom] = auth.currentUser?.uid!!
        messageData[FirebaseConstants.idTo] = toID
        messageData[FirebaseConstants.timestamp] = timestamps
        messageData[FirebaseConstants.content] = content
        messageData[FirebaseConstants.type] = type

        // todo push message to firebase.
        fireStore.collection(FirebaseConstants.pathMessages).document(group).collection(group).document(timestamps)
            .set(messageData).addOnCompleteListener{
                // todo push notification..
                _isVisibilitySending.postValue(false)
            }

    }

    fun setVisibilitySending(isVisible: Boolean) {
        _isVisibilitySending.postValue(isVisible)
    }

    fun setListenerMessage() {
        fireStore.collection(FirebaseConstants.pathMessages).document(group).collection(group)
            .orderBy(FirebaseConstants.timestamp, Query.Direction.DESCENDING)
            .addSnapshotListener(
                EventListener { value, error ->
                    val updateMessages = ArrayList<Message>()
                    val docs = value?.documents
                    if (docs?.size != null) {
                        docs?.forEach { doc ->
                            val message = Message(
                                doc.data?.get("idFrom").toString(),
                                doc.data?.get("idTo").toString(),
                                doc.data?.get("timestamp").toString(),
                                doc.data?.get("content").toString(),
                                doc.data?.get("type").toString()
                            )
                            updateMessages.add(message)
                            _messages.postValue(updateMessages)
                        }
                    }
                }
            )
    }

}