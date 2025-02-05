package com.bachnn.messenger.ui.service

import com.bachnn.messenger.constants.FirebaseConstants
import com.bachnn.messenger.data.model.User
import com.bachnn.messenger.ui.notification.PushNotification
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MessageService : FirebaseMessagingService() {

    val auth = FirebaseAuth.getInstance()

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        val uid = auth.currentUser?.uid
        val mapUser: MutableMap<String, Any> = HashMap()
        mapUser[FirebaseConstants.token] = token

        FirebaseFirestore.getInstance().collection(FirebaseConstants.pathUser).document(uid!!)
            .update(mapUser)
            .addOnCompleteListener(OnCompleteListener {

            })
    }


    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        val uid = message.data["uid"].toString()
        val displayName = message.data["displayName"].toString()
        val currentToken = message.data["currentToken"].toString()
        val photoUrl = message.data["photoUrl"].toString()
        val user = User(uid,displayName,"",photoUrl,"", currentToken)



        PushNotification.showNotification(baseContext,  message.notification?.title!!,message.notification?.body!!, user )


    }

}