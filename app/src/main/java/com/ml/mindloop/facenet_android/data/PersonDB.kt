package com.ml.mindloop.facenet_android.data

import io.objectbox.Box
import io.objectbox.BoxStore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import org.koin.core.annotation.Single
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@Single
class PersonDB : KoinComponent {
    private val boxStore: BoxStore by inject()
    private val personBox: Box<PersonRecord> = boxStore.boxFor(PersonRecord::class.java)

    fun addPerson(person: PersonRecord): Long = personBox.put(person)

    fun removePerson(id: Long) {
        personBox.remove(id)
    }

    fun getAll(): Flow<List<PersonRecord>> = callbackFlow {
        val subscription = personBox.query().build().subscribe().observer { data ->
            trySend(data)
        }
        awaitClose { subscription.cancel() }
    }

    fun getCount(): Long = personBox.count()

    fun getPerson(id: Long): PersonRecord? = personBox.get(id)
}
