package com.example.shoshinapp.data

import android.content.Context
import android.provider.ContactsContract
import com.example.shoshinapp.data.models.UserSummary
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
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

    suspend fun matchContactsWithShoshinUsers(
        contacts: List<UserSummary>,
        firestore: com.google.firebase.firestore.FirebaseFirestore
    ): Pair<List<UserSummary>, List<UserSummary>> {

        // Normalize phone numbers for matching
        val normalizedContacts = contacts.map { contact ->
            val normalized = contact.userId
                .replace(Regex("[^0-9]"), "")
                .takeLast(10) // Last 10 digits
            contact.copy(userId = normalized)
        }

        val shoshinUsers = mutableListOf<UserSummary>()
        val nonShoshinContacts = mutableListOf<UserSummary>()

        // Check each contact against Firestore users
        // Batch in groups of 10 (Firestore whereIn limit)
        normalizedContacts.chunked(10).forEach { batch ->
            try {
                val phoneNumbers = batch.map { it.userId }
                val snapshot = firestore
                    .collection("users")
                    .whereIn("phoneNumber", phoneNumbers)
                    .get()
                    .await()

                val matchedNumbers = snapshot.documents.map { doc ->
                    doc.getString("phoneNumber")
                        ?.replace(Regex("[^0-9]"), "")
                        ?.takeLast(10) ?: ""
                }

                batch.forEach { contact ->
                    if (contact.userId in matchedNumbers) {
                        // This contact is on Shoshin!
                        val matchedDoc = snapshot.documents.firstOrNull { doc ->
                            doc.getString("phoneNumber")
                                ?.replace(Regex("[^0-9]"), "")
                                ?.takeLast(10) == contact.userId
                        }
                        shoshinUsers.add(contact.copy(
                            userId = matchedDoc?.id ?: contact.userId,
                            currentStreak = matchedDoc?.getLong("currentStreak")?.toInt() ?: 0,
                            activityStatus = "On Shoshin"
                        ))
                    } else {
                        nonShoshinContacts.add(contact)
                    }
                }
            } catch (e: Exception) {
                nonShoshinContacts.addAll(batch)
            }
        }

        return Pair(shoshinUsers, nonShoshinContacts)
    }
}
