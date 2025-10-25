package com.ml.mindloop.facenet_android.domain

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.ml.mindloop.facenet_android.data.WorkLog
import com.ml.mindloop.facenet_android.data.WorkLog_ // <-- Importa el archivo generado
import io.objectbox.Box
import io.objectbox.BoxStore
import org.koin.core.annotation.Single
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@Single
class WorkLogUseCase : KoinComponent {
    private val boxStore: BoxStore by inject()
    
    // Inyectamos el Context que necesitamos para WorkManager
    // (Asegúrate de añadir 'androidContext(this@MainApplication)' en tu MainApplication.kt)
    private val context: Context by inject() 
    
    private val workLogBox: Box<WorkLog> = boxStore.boxFor(WorkLog::class.java)

    fun addWorkLog(workerName: String, eventType: String) {
        // Creamos el log con 'synced = false' por defecto
        val log = WorkLog(workerName = workerName, eventType = eventType, synced = false)
        workLogBox.put(log)
        
        // ¡Disparamos la tarea de sincronización!
        scheduleSync()
    }

    fun getAllWorkLogs(): List<WorkLog> {
        return workLogBox.all.sortedByDescending { it.timestamp }
    }
    
    // ---- NUEVAS FUNCIONES PARA EL WORKER ---

    internal fun getUnsyncedLogs(): List<WorkLog> {
        // Busca todos los logs donde 'synced' sea 'false'
        return workLogBox.query(WorkLog_.synced.equal(false)).build().find()
    }

    internal fun updateLogsAsSynced(logs: List<WorkLog>) {
        // Mapea la lista a logs actualizados y los guarda
        val updatedLogs = logs.map { it.copy(synced = true) }
        workLogBox.put(updatedLogs)
    }

    private fun scheduleSync() {
        // Define el requisito: SOLO ejecutar cuando haya INTERNET
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        // Crea la tarea de tipo "una sola vez"
        val syncRequest = OneTimeWorkRequestBuilder<SyncWorker>()
            .setConstraints(constraints)
            .build()

        // Pone la tarea en la cola.
        // Si ya hay una tarea igual pendiente, WorkManager es lo
        // suficientemente inteligente para no duplicarla.
        WorkManager.getInstance(context).enqueueUniqueWork("sync_work_logs", ExistingWorkPolicy.KEEP, syncRequest)
    }
}
