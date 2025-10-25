package com.ml.mindloop.facenet_android.data

import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import java.util.Date

@Entity
data class WorkLog(
    @Id var id: Long = 0,
    val workerName: String,
    val eventType: String,
    val timestamp: Date = Date(),
    var synced: Boolean = false
)
