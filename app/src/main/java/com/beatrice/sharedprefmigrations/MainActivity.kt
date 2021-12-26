package com.beatrice.sharedprefmigrations

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import androidx.datastore.migrations.SharedPreferencesMigration
import androidx.datastore.migrations.SharedPreferencesView
import androidx.datastore.preferences.SharedPreferencesMigration
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.lifecycleScope
import com.beatrice.sharedprefmigrations.data.User
import com.beatrice.sharedprefmigrations.data.UserProfileSerializer
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

const val NAME_KEY = "name"
const val EMAIL_KEY = "email"
const val NICKNAME_KEY = "Nickname"
const val SETTINGS_NAME = "settings"

val NAME_KEYY = stringPreferencesKey("name")

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = SETTINGS_NAME,
    produceMigrations = { context ->
        listOf(SharedPreferencesMigration(context, SETTINGS_NAME))

    })

val Context.protoDataStore: DataStore<UserProfileOuterClass.UserProfile> by dataStore(
    fileName = "settings.pb",
    serializer = UserProfileSerializer,
    produceMigrations = { context ->
        listOf(
            SharedPreferencesMigration(
                context,
                SETTINGS_NAME
            ){
                    sharedPrefs: SharedPreferencesView, currentData: UserProfileOuterClass.UserProfile ->
                currentData.toBuilder()
                    .setEmail(sharedPrefs.getString(EMAIL_KEY))
                    .setName(sharedPrefs.getString(NAME_KEY))
                    .setNickname(sharedPrefs.getString(NICKNAME_KEY))
                    .build()
            }
        )

    }
)


class MainActivity : AppCompatActivity() {
    lateinit var prefs: SharedPreferences


    //    val sharedPrefs = getPreferences(MODE_PRIVATE)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        prefs = getSharedPreferences(SETTINGS_NAME, MODE_PRIVATE)
        writeProfile()

        val name = prefs.getString(NAME_KEY, "Joe")
        val textView = findViewById<TextView>(R.id.helloTextView)
        textView.text = name

        readProtoDataStore()


    }

    fun writeName() {
        with(prefs.edit()) {
            putString(NAME_KEY, "Violet Gakii")
                .apply()
        }
    }

    fun readDataStore() {
        lifecycleScope.launch {
            dataStore.data.map {
                it[NAME_KEYY]
            }.collect {
                Log.d("PREFS", "are $it")

            }
        }

    }

    fun readProtoDataStore(){
        lifecycleScope.launch {
            protoDataStore.data.map {
                val user = User(
                    name =  it.name,
                    email =  it.email,
                    nickname = it.nickname
                )
                user
            }.collect {
                Log.d("PREFS", "are ${it.email} ${it.name} ${it.nickname}")
                val textView = findViewById<TextView>(R.id.secondTextView)
                textView.text = it.nickname
            }
        }
    }

    fun writeProfile() {
        with(prefs.edit()) {
            putString(NAME_KEY, "Kiki")
            putString(EMAIL_KEY, "K@b.com")
            putString(NICKNAME_KEY, "Kinya")
            apply()
        }
    }
}

