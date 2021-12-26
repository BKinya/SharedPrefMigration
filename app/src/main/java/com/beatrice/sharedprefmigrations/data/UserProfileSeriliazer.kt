package com.beatrice.sharedprefmigrations.data

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import androidx.datastore.preferences.protobuf.InvalidProtocolBufferException
import java.io.InputStream
import java.io.OutputStream

object UserProfileSerializer : Serializer<UserProfileOuterClass.UserProfile>{
    override val defaultValue: UserProfileOuterClass.UserProfile
        get() = UserProfileOuterClass.UserProfile.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): UserProfileOuterClass.UserProfile {
        try {
            return UserProfileOuterClass.UserProfile.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }

    override suspend fun writeTo(t: UserProfileOuterClass.UserProfile, output: OutputStream) {
        t.writeTo(output)
    }
}