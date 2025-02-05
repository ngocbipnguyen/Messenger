package com.bachnn.messenger.ui.viewModel

import android.content.Context
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import com.bachnn.messenger.R
import com.bachnn.messenger.base.BaseViewModel
import com.bachnn.messenger.constants.FirebaseConstants
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import dagger.hilt.android.lifecycle.HiltViewModel
import java.security.MessageDigest
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val auth: FirebaseAuth, private val firebaseStore : FirebaseFirestore): BaseViewModel() {

    suspend fun signIn(
        context: Context,
        doOnSuccess: (String) -> Unit,
        doOnError: (String) -> Unit,
    ) {


        // Generate a nonce (a random number used once)
        val ranNonce: String = UUID.randomUUID().toString()
        val bytes: ByteArray = ranNonce.toByteArray()
        val md: MessageDigest = MessageDigest.getInstance("SHA-256")
        val digest: ByteArray = md.digest(bytes)
        val hashedNonce: String = digest.fold("") { str, it -> str + "%02x".format(it) }


        val googleIdOption: GetGoogleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(context.getString(R.string.default_web_client_id))
            .setNonce(hashedNonce)
            .build()
        val request: GetCredentialRequest = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        val credentialManager = CredentialManager
            .create(context)


        try {
            val result = credentialManager.getCredential(
                context = context,
                request = request
            )
            val credential = result.credential

            val googleTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)

            val googleToken = googleTokenCredential.idToken

            val authCredential =
                GoogleAuthProvider.getCredential(googleToken, null)

            val authResult =
                auth.signInWithCredential(authCredential).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Task is complete and successful, get the current user
                        val user = task.result.user

                        firebaseStore.collection(FirebaseConstants.pathUser)
                            .whereEqualTo(FirebaseConstants.uid, user?.uid)
                            .get()
                            .addOnCompleteListener(OnCompleteListener<QuerySnapshot?> { task ->
                                if (task.isSuccessful) {
                                    val document = task.result.documents
                                    if (document.size == 0) {
                                        Log.e("okkk", "google document.size 0 ${document.size}")
                                        // add new user.
                                        val mapUser: MutableMap<String, Any> = HashMap()
                                        mapUser[FirebaseConstants.name] = user?.displayName!!
                                        mapUser[FirebaseConstants.email] = user.email!!
                                        mapUser[FirebaseConstants.photoUrl] = user.photoUrl!!
                                        mapUser[FirebaseConstants.emailVerified] = user.photoUrl!!

                                        firebaseStore.collection(FirebaseConstants.pathUser)
                                            .document(user.uid).set(mapUser)
                                            .addOnSuccessListener { it ->
                                                Log.e("okkk", "google document : $it")

                                                doOnSuccess("dasdasd")
                                            }.addOnFailureListener { exception ->
                                                doOnError(exception.toString())
                                                Log.e(
                                                    "okkk",
                                                    "google doOnError : ${exception.toString()}"
                                                )
                                            }
                                    } else {
                                        val doc = document[0]
                                        if (doc.exists()) {
                                            // update params.
                                            Log.e("okkk", "google exists")
                                            doOnSuccess("exites")
                                        } else {
                                            // add new user.
                                            val mapUser: MutableMap<String, Any> = HashMap()
                                            mapUser[FirebaseConstants.name] = user?.displayName!!
                                            mapUser[FirebaseConstants.email] = user.email!!
                                            mapUser[FirebaseConstants.photoUrl] = user.photoUrl!!
                                            mapUser[FirebaseConstants.emailVerified] =
                                                user.photoUrl!!

                                            firebaseStore.collection(FirebaseConstants.pathUser)
                                                .document(user.uid).set(mapUser)
                                                .addOnSuccessListener { it ->
                                                    Log.e("okkk", "google document : $it")

                                                    doOnSuccess("dasdasd")
                                                }.addOnFailureListener { exception ->
                                                    Log.e(
                                                        "okkk",
                                                        "google doOnError : ${exception.toString()}"
                                                    )
                                                    doOnError(exception.toString())
                                                }
                                        }
                                    }
                                } else {
                                    doOnError("task failure!")
                                }
                            })

                    } else {
                        // Task failed, handle the error
                    }
                }

            Log.e("okkk", "google token : $googleToken")
        } catch (e: Exception) {
            Log.e("okkk", " e : ${e.toString()}")
            doOnError(e.toString())
        }

    }

}