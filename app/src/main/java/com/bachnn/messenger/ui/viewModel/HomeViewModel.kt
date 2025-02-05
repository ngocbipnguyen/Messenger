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
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
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

                _currentUser.postValue(
                    User(
                        doc.id.toString(),
                        name,
                        email,
                        photoUrl,
                        emailVerified,
                        token
                    )
                )
            }


            val userDocs = fireStore.collection(FirebaseConstants.pathUser).get().await()

            if (userDocs.documents.size > 0) {
                val listUser = ArrayList<User>()
                userDocs.documents.forEach { it ->
                    if (it.id != currentUser.value?.uid) {
                        val name = it.getString(FirebaseConstants.name).toString()
                        val email = it.getString(FirebaseConstants.email).toString()
                        val photoUrl = it.getString(FirebaseConstants.photoUrl).toString()
                        val emailVerified = it.getString(FirebaseConstants.emailVerified).toString()
                        val token = it.getString(FirebaseConstants.token).toString()
                        listUser.add(
                            User(
                                it.id,
                                name,
                                email,
                                photoUrl,
                                emailVerified,
                                token
                            )
                        )
                    }

                }

                _users.postValue(listUser)
            }

        }


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

}