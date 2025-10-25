package com.ml.mindloop.facenet_android.domain

import android.net.Uri
import com.ml.mindloop.facenet_android.data.PersonDB
import com.ml.mindloop.facenet_android.data.PersonRecord
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import org.koin.core.annotation.Single
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@Single
class PersonUseCase(
    private val personDB: PersonDB,
) : KoinComponent {
    private val imageVectorUseCase: ImageVectorUseCase by inject()

    suspend fun addPerson(
        name: String,
        imageUris: List<Uri>,
    ) {
        val personId = personDB.addPerson(
            PersonRecord(
                personName = name,
                numImages = imageUris.size.toLong(),
                addTime = System.currentTimeMillis(),
            ),
        )
        for (uri in imageUris) {
            imageVectorUseCase.addImage(personId, name, uri)
        }
    }

    suspend fun updatePersonImages(personId: Long, imageUris: List<Uri>) {
        val person = getPerson(personId)
        if (person != null) {
            // Remove old images
            imageVectorUseCase.removeImages(personId)
            // Add new images
            for (uri in imageUris) {
                imageVectorUseCase.addImage(personId, person.personName, uri)
            }
            // Update person record
            person.numImages = imageUris.size.toLong()
            personDB.addPerson(person) // This will update the existing record
        }
    }

    fun removePerson(id: Long) {
        personDB.removePerson(id)
        imageVectorUseCase.removeImages(id)
    }

    fun getAll(): Flow<List<PersonRecord>> = personDB.getAll()

    fun getCount(): Long = personDB.getCount()

    suspend fun getPerson(personId: Long): PersonRecord? = withContext(Dispatchers.IO) {
        personDB.getPerson(personId)
    }
}