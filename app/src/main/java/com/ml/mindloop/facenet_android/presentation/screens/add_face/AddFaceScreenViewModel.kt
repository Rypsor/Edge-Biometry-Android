package com.ml.mindloop.facenet_android.presentation.screens.add_face

import android.net.Uri
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ml.mindloop.facenet_android.domain.PersonUseCase
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@KoinViewModel
class AddFaceScreenViewModel : ViewModel(), KoinComponent {
    private val personUseCase: PersonUseCase by inject()

    var personNameState = mutableStateOf("")
    var selectedImageURIs = mutableStateOf<List<Uri>>(emptyList())
    var isProcessingImages = mutableStateOf(false)
    var numImagesProcessed = mutableStateOf(0)

    fun addImages(personId: Long? = null) {
        if (selectedImageURIs.value.isNotEmpty() && personNameState.value.isNotEmpty()) {
            viewModelScope.launch {
                isProcessingImages.value = true
                val uris = selectedImageURIs.value
                if (personId != null) {
                    // Editing existing person
                    personUseCase.updatePersonImages(personId, uris)
                } else {
                    // Adding new person
                    personUseCase.addPerson(personNameState.value, uris)
                }
                numImagesProcessed.value = uris.size
                isProcessingImages.value = false
                // Reset state
                personNameState.value = ""
                selectedImageURIs.value = emptyList()
            }
        }
    }

    fun loadPerson(personId: Long) {
        viewModelScope.launch {
            val person = personUseCase.getPerson(personId)
            if (person != null) {
                personNameState.value = person.personName
            }
        }
    }
}