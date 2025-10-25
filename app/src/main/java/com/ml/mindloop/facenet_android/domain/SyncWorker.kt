package com.ml.mindloop.facenet_android.domain

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class SyncWorker(appContext: Context, params: WorkerParameters) :
    CoroutineWorker(appContext, params), KoinComponent {

    // Inyectamos el UseCase que tiene la lógica de la base de datos
    private val workLogUseCase: WorkLogUseCase by inject()

    override suspend fun doWork(): Result {
        // 1. Coger los logs pendientes de ObjectBox
        val pendingLogs = workLogUseCase.getUnsyncedLogs()

        if (pendingLogs.isEmpty()) {
            return Result.success() // No hay nada que hacer
        }

        val db = Firebase.firestore
        val batch = db.batch()

        // 2. Añadir todos los logs pendientes a un "batch" de Firestore
        pendingLogs.forEach { log ->
            // Creamos un nuevo documento con ID automático en la colección "work_logs"
            val docRef = db.collection("work_logs").document()
            batch.set(docRef, log)
        }

        return try {
            // 3. Intentar subir el batch a la nube
            batch.commit().await()
            
            // 4. Si TODO salió bien, marcarlos como 'synced' en ObjectBox
            workLogUseCase.updateLogsAsSynced(pendingLogs)
            
            Result.success()
        } catch (e: Exception) {
            // Si algo falla (ej. se vuelve a caer el internet),
            // WorkManager lo reintentará automáticamente más tarde
            Result.retry()
        }
    }
}
