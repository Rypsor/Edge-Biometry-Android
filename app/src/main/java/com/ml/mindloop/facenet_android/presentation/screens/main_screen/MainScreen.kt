package com.ml.mindloop.facenet_android.presentation.screens.main_screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ml.mindloop.facenet_android.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    onRegisterEntryClick: () -> Unit,
    onRegisterExitClick: () -> Unit,
    onDatabaseClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text(stringResource(id = R.string.main_screen_title)) })
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Button(onClick = onRegisterEntryClick) {
                Text(stringResource(id = R.string.main_screen_register_entry))
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onRegisterExitClick) {
                Text(stringResource(id = R.string.main_screen_register_exit))
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onDatabaseClick) {
                Text(stringResource(id = R.string.main_screen_database))
            }
        }
    }
}
