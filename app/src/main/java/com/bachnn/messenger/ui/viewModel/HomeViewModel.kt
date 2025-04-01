package com.bachnn.messenger.ui.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.bachnn.messenger.base.BaseViewModel
import com.bachnn.messenger.constants.FirebaseConstants
import com.bachnn.messenger.data.model.User
import com.google.android.gms.tasks.OnCompleteListener
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
class HomeViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val fireStore: FirebaseFirestore
) : BaseViewModel() {

    private var _currentUser = MutableLiveData<User>()
    val currentUser: LiveData<User> = _currentUser


    private var _users = MutableLiveData<List<User>>()
    val users: LiveData<List<User>> = _users

    init {
        viewModelScope.launch {
            val doc = fireStore.collection(FirebaseConstants.pathUser)
                .document(auth.currentUser?.uid.toString()).get().await()
            if (doc.exists()) {
                val name = doc.getString(FirebaseConstants.name).toString()
                val email = doc.getString(FirebaseConstants.email).toString()
                val photoUrl = doc.getString(FirebaseConstants.photoUrl).toString()
                val emailVerified = doc.getString(FirebaseConstants.emailVerified).toString()
                val token = doc.getString(FirebaseConstants.token).toString()
                val openTime = doc.getString(FirebaseConstants.OPEN_TALK_TIME).toString()

                _currentUser.postValue(
                    User(
                        doc.id.toString(),
                        name,
                        email,
                        photoUrl,
                        emailVerified,
                        token,
                        openTime,
                        ""
                    )
                )
            }


            val userDocs = fireStore.collection(FirebaseConstants.pathUser)
                .orderBy(FirebaseConstants.timestamp, Query.Direction.DESCENDING)
                .get().await()

            if (userDocs.documents.size > 0) {
                val listUser = ArrayList<User>()
                userDocs.documents.forEach { it ->
                    if (it.id != currentUser.value?.uid) {
                        val name = it.getString(FirebaseConstants.name).toString()
                        val email = it.getString(FirebaseConstants.email).toString()
                        val photoUrl = it.getString(FirebaseConstants.photoUrl).toString()
                        val emailVerified = it.getString(FirebaseConstants.emailVerified).toString()
                        val token = it.getString(FirebaseConstants.token).toString()
                        val openTime = it.getString(FirebaseConstants.OPEN_TALK_TIME).toString()
                        listUser.add(
                            User(
                                it.id,
                                name,
                                email,
                                photoUrl,
                                emailVerified,
                                token,
                                openTime,
                                ""
                            )
                        )
                    }

                }

                _users.postValue(listUser)
            }

        }


    }

    fun setListenerUser() {
        fireStore.collection(FirebaseConstants.pathUser)
            .orderBy(FirebaseConstants.timestamp, Query.Direction.DESCENDING)
            .addSnapshotListener(EventListener { value, error ->
                val updateUser = ArrayList<User>()
                val docs = value?.documents
                if (docs?.size != null) {
                    docs.forEach { doc ->
                        if (doc.id != currentUser.value?.uid) {
                            val name = doc.getString(FirebaseConstants.name).toString()
                            val email = doc.getString(FirebaseConstants.email).toString()
                            val photoUrl = doc.getString(FirebaseConstants.photoUrl).toString()
                            val emailVerified =
                                doc.getString(FirebaseConstants.emailVerified).toString()
                            val token = doc.getString(FirebaseConstants.token).toString()
                            val openTime = doc.getString(FirebaseConstants.OPEN_TALK_TIME).toString()
                            updateUser.add(
                                User(
                                    doc.id,
                                    name,
                                    email,
                                    photoUrl,
                                    emailVerified,
                                    token,
                                    openTime,
                                    ""
                                )
                            )
                        }
                    }
                    _users.postValue(updateUser)
                }
            })
    }



    fun sendRegistrationToServer(token: String) {
        val uid = auth.currentUser?.uid

        val mapUser: MutableMap<String, Any> = HashMap()
        mapUser[FirebaseConstants.token] = token

        fireStore.collection(FirebaseConstants.pathUser).document(uid!!).update(mapUser).addOnCompleteListener(
            OnCompleteListener {
                if (it.isSuccessful) {
                    Log.e("HomeViewModel", "sendRegistrationToServer successful!")
                } else {
                    Log.e("HomeViewModel", "sendRegistrationToServer failure!")
                }
            })
    }

    fun updateOpenTime(uid: String) {
        viewModelScope.launch {
            val date = Date()
            val mapUser: MutableMap<String, Any> = HashMap()
            mapUser[FirebaseConstants.OPEN_TALK_TIME] = date.time.toString()
            Log.e("updateUnread", "updateOpenTime : ${date.time} ")
            fireStore.collection(FirebaseConstants.pathUser).document(uid).update(mapUser).await()
        }
    }

    fun updateUnread(updateUnread:() -> Unit) {

        viewModelScope.launch {
            _users.value?.forEach {it ->
                val group = if (auth.currentUser?.uid!! > it.uid) {
                    "${_currentUser.value?.uid}-${it.uid}"
                } else {
                    "${it.uid}-${_currentUser.value?.uid}"
                }

                val docs = fireStore.collection(FirebaseConstants.pathMessages).document(group)
                    .collection(group)
                    .whereGreaterThan(FirebaseConstants.timestamp, it.openTime).limit(30)
                    .orderBy(FirebaseConstants.timestamp, Query.Direction.DESCENDING)
                    .get().await()

                it.numberUnread = docs.size().toString()
                updateUnread()
            }
        }
    }


}