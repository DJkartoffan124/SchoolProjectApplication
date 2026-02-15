package com.golozhopikistudio.schoolprojectapplication.features.profile

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.golozhopikistudio.schoolprojectapplication.domain.model.Role

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel
) {
    val uiState by viewModel.state.collectAsStateWithLifecycle()
    var name by remember(uiState.user?.name) { mutableStateOf(uiState.user?.name.orEmpty()) }
    var selectedRole by remember(uiState.user?.role) { mutableStateOf(uiState.user?.role ?: Role.READER) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(text = "Профиль")
        Text(text = "Активный: ${uiState.user?.name ?: "не выбран"}")

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Имя") },
            singleLine = true
        )

        Text("Роль")
        Role.entries.forEach { role ->
            Row(modifier = Modifier.clickable { selectedRole = role }) {
                RadioButton(selected = selectedRole == role, onClick = { selectedRole = role })
                Text(role.name, modifier = Modifier.padding(top = 12.dp))
            }
        }

        Button(
            onClick = { viewModel.setActiveUser(name = name, role = selectedRole) },
            enabled = name.isNotBlank()
        ) {
            Text("Сделать активным")
        }
    }
}