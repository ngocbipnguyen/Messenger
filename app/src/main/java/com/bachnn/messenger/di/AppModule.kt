package com.bachnn.messenger.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class AppModule {

    @Provides
    @Singleton
    fun getFirebaseAuth() : FirebaseAuth = FirebaseAuth.getInstance()


    @Provides
    @Singleton
    fun getFireStore(): FirebaseFirestore = FirebaseFirestore.getInstance()

}