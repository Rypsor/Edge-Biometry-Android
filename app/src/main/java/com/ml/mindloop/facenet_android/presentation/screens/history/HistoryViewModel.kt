package com.ml.mindloop.facenet_android.presentation.screens.history

import androidx.lifecycle.ViewModel
import com.ml.mindloop.facenet_android.data.WorkLog
import com.ml.mindloop.facenet_android.domain.WorkLogUseCase
import org.koin.android.annotation.KoinViewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@KoinViewModel
class HistoryViewModel : ViewModel(), KoinComponent {
    private val workLogUseCase: WorkLogUseCase by inject()

    fun getWorkLogs(): List<WorkLog> {
        return workLogUseCase.getAllWorkLogs()
    }
}
