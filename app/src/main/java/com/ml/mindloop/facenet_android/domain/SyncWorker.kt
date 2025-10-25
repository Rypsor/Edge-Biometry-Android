package com.ml.mindloop.facenet_android.domain

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class SyncWorker(appContext: Context, params: WorkerParameters) :
    CoroutineWorker(appContext, params), KoinComponent {

    private val workLogUseCase: WorkLogUseCase by inject()
    private val notificationManager =
        appContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    override suspend fun doWork(): Result {
        val pendingLogs = workLogUseCase.getUnsyncedLogs()

        if (pendingLogs.isEmpty()) {
            return Result.success() // No hay nada que hacer
        }

        val db = Firebase.firestore
        val batch = db.batch()

        pendingLogs.forEach { log ->
            val docRef = db.collection("work_logs").document()
            batch.set(docRef, log)
        }

        return try {
            batch.commit().await()
            workLogUseCase.updateLogsAsSynced(pendingLogs)

            // ¡Mostrar notificación de éxito!
            showSyncSuccessNotification(pendingLogs.size)

            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }

    private fun showSyncSuccessNotification(logCount: Int) {
        val channelId = "sync_channel"
        val channelName = "Sincronización de Datos"

        // Crear canal de notificación para Android 8.0+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Notificaciones sobre la sincronización de datos con la nube"
            }
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setContentTitle("Sincronización completada")
            .setContentText("$logCount registros fueron subidos a la nube.")
            .setSmallIcon(android.R.drawable.ic_menu_upload) // Icono estándar de Android
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        // Usamos un ID único para la notificación
        notificationManager.notify(1, notification)
    }
}
