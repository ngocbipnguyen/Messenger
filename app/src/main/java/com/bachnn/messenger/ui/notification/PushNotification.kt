package com.bachnn.messenger.ui.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.navigation.NavDeepLinkBuilder
import com.bachnn.messenger.R
import com.bachnn.messenger.constants.Constants
import com.bachnn.messenger.constants.FirebaseConstants
import com.bachnn.messenger.data.model.User
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.auth.oauth2.GoogleCredentials

class PushNotification {

    //AIzaSyCooIOS9rSqJm41IHnldkAWVhhDr-IMPNs

    companion object {
        fun getAccessToken(context: Context): String {
            val googleCredentials = GoogleCredentials.fromStream(context.assets.open("messenger.json"))
                .createScoped("https://www.googleapis.com/auth/firebase.messaging")
            try {
                googleCredentials.refresh()
            } catch (e: Exception) {
                Log.e("PushNotification", "exception : ${e.toString()}")
            }

            return googleCredentials.accessToken.tokenValue
        }


        fun createNotificationChannel(context: Context) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val name = context.getString(R.string.channel_name)
                val descriptionText = context.getString(R.string.channel_description)
                val importance = NotificationManager.IMPORTANCE_DEFAULT
                val channel = NotificationChannel(Constants.CHANNEL_ID,name, importance).apply {
                    description = descriptionText
                }

                val notificationManager: NotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.createNotificationChannel(channel)
            }
        }


        fun showNotification(context: Context, name: String, message: String, user: User, idNotification: Int) {

//            val bundle = Bundle()
//            bundle.putSerializable("user_notification",user)
//
//            val pendingIntent = NavDeepLinkBuilder(context)
//                .setComponentName(MainActivity::class.java)
//                .setGraph(R.navigation.main_nav_graph)
//                .setDestination(R.id.messageFragment)
//                .setArguments(bundle)
//                .createPendingIntent()
//

            val pendingIntent = NavDeepLinkBuilder(context)
                .setGraph(R.navigation.main_nav_graph)
                .addDestination(R.id.messengerFragment)
                .setArguments(Bundle().apply {
                    putSerializable("userArg", user)
                })
                .createPendingIntent()

            var builder = NotificationCompat.Builder(context, Constants.CHANNEL_ID)
                .setSmallIcon(R.drawable.icons_message)
                .setContentTitle(name)
                .setContentText(message)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)

            with(NotificationManagerCompat.from(context)) {
                if (ActivityCompat.checkSelfPermission(
                        context,
                        android.Manifest.permission.POST_NOTIFICATIONS
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    // TODO: Consider calling
                    // ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    // public fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>,
                    //                                        grantResults: IntArray)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.

                    return@with
                }
                // notificationId is a unique int for each notification that you must define.
                notify(idNotification, builder.build())
            }
        }

    }

}