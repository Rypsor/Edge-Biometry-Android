package com.ml.mindloop.facenet_android.presentation.screens.detect_screen

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.ml.mindloop.facenet_android.data.RecognitionMetrics
import com.ml.mindloop.facenet_android.domain.ImageVectorUseCase
import com.ml.mindloop.facenet_android.domain.PersonUseCase
import com.ml.mindloop.facenet_android.domain.WorkLogUseCase
import org.koin.android.annotation.KoinViewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.concurrent.atomic.AtomicBoolean

@KoinViewModel
class DetectScreenViewModel(
    val personUseCase: PersonUseCase,
    val imageVectorUseCase: ImageVectorUseCase,
) : ViewModel(), KoinComponent {
    val faceDetectionMetricsState = mutableStateOf<RecognitionMetrics?>(null)
    val recognitionSuccessful = mutableStateOf(false)
    val recognitionConfidence = mutableStateOf(0f)
    val recognizedWorkerName = mutableStateOf("")
    private val workLogUseCase: WorkLogUseCase by inject()
    private val recognitionHandled = AtomicBoolean(false)

    fun getNumPeople(): Long = personUseCase.getCount()

    fun onRecognitionSuccess(workerName: String, eventType: String, confidence: Float) {
        if (recognitionHandled.compareAndSet(false, true)) {
            workLogUseCase.addWorkLog(workerName, eventType)
            recognizedWorkerName.value = workerName
            recognitionConfidence.value = confidence
            recognitionSuccessful.value = true
        }
    }

    fun resetRecognitionState() {
        recognitionSuccessful.value = false
        recognitionConfidence.value = 0f
        recognizedWorkerName.value = ""
        recognitionHandled.set(false)
    }
}
