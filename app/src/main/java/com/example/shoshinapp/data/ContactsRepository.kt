package com.example.shoshinapp.data

import android.content.Context
import android.provider.ContactsContract
import com.example.shoshinapp.data.models.UserSummary
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ContactsRepository(private val context: Context) {

    suspend fun fetchContacts(): List<UserSummary> = withContext(Dispatchers.IO) {
        val contacts = mutableListOf<UserSummary>()
        val contentResolver = context.contentResolver
        val cursor = contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            null,
            null,
            null,
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC"
        )

        cursor?.use {
            val nameIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
            val numberIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
            
            while (it.moveToNext()) {
                val name = it.getString(nameIndex)
                val number = it.getString(numberIndex)
                
                // Create a UserSummary placeholder for the contact
                contacts.add(
                    UserSummary(
                        userId = number, // Use phone number as temporary ID
                        userName = name,
                        profilePictureUrl = null,
                        currentStreak = 0,
                        totalCheckpoints = 0,
                        badgeCount = 0,
                        activityStatus = "In contacts",
                        lastCheckpointDate = 0
                    )
                )
            }
        }
        
        // Remove duplicates (a contact can have multiple numbers)
        contacts.distinctBy { it.userName }
    }
}
