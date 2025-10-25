package com.ml.mindloop.facenet_android.domain

import com.ml.mindloop.facenet_android.data.WorkLog
import io.objectbox.Box
import io.objectbox.BoxStore
import org.koin.core.annotation.Single
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@Single
class WorkLogUseCase : KoinComponent {
    private val boxStore: BoxStore by inject()
    private val workLogBox: Box<WorkLog> = boxStore.boxFor(WorkLog::class.java)

    fun addWorkLog(workerName: String, eventType: String) {
        val log = WorkLog(workerName = workerName, eventType = eventType)
        workLogBox.put(log)
    }

    fun getAllWorkLogs(): List<WorkLog> {
        return workLogBox.all.sortedByDescending { it.timestamp }
    }
}
