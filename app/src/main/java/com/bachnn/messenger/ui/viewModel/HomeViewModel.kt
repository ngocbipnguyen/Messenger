package com.bachnn.messenger.ui.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.bachnn.messenger.base.BaseViewModel
import com.bachnn.messenger.constants.FirebaseConstants
import com.bachnn.messenger.data.model.User
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

                _currentUser.postValue(
                    User(
                        doc.id.toString(),
                        name,
                        email,
                        photoUrl,
                        emailVerified,
                        ""
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
                        listUser.add(
                            User(
                                it.id,
                                name,
                                email,
                                photoUrl,
                                emailVerified,
                                ""
                            )
                        )
                    }

                }

                _users.postValue(listUser)
            }

        }


    }

}