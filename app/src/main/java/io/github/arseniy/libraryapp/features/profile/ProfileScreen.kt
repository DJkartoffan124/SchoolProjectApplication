package io.github.arseniy.libraryapp.features.profile

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.arseniy.libraryapp.domain.model.Role
import io.github.arseniy.libraryapp.domain.model.User

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel
) {
    val uiState by viewModel.state.collectAsStateWithLifecycle()
    var name by remember { mutableStateOf("") }
    var selectedRole by remember { mutableStateOf(Role.READER) }
    var selectedUser by remember(uiState.users, uiState.user) {
        mutableStateOf(uiState.user ?: uiState.users.firstOrNull())
    }
    var expanded by remember { mutableStateOf(false) }
    var userPendingDeletion by remember { mutableStateOf<User?>(null) }
    var deletionMessage by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current

    LaunchedEffect(uiState.users, selectedUser) {
        if (selectedUser != null && selectedUser !in uiState.users) {
            selectedUser = uiState.user ?: uiState.users.firstOrNull()
        }
    }

    val canDeleteSelectedUser = selectedUser?.let(viewModel::canDeleteUser) == true
    val deletionHint = when {
        selectedUser == null -> null
        canDeleteSelectedUser -> null
        uiState.user == null -> "Сначала выберите активный профиль."
        uiState.user!!.role == Role.READER -> "Читатель может удалить только свой профиль."
        else -> "Удаление этого профиля недоступно."
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Профили") })
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(text = "Активный: ${uiState.user?.name ?: "не выбран"}")

            Text("Создание профиля")
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Имя профиля") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Text("Роль")
            Role.entries.forEach { role ->
                Row(modifier = Modifier.clickable { selectedRole = role }) {
                    RadioButton(selected = selectedRole == role, onClick = { selectedRole = role })
                    Text(role.label(), modifier = Modifier.padding(top = 12.dp))
                }
            }

            Button(
                onClick = {
                    viewModel.setActiveUser(name = name, role = selectedRole)
                    name = ""
                },
                enabled = name.isNotBlank()
            ) {
                Text("Создать")
            }

            Text("Выбор профиля")
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = selectedUser?.let { "${it.name} • ${it.role.label()}" } ?: "",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Профиль") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    uiState.users.forEach { user ->
                        DropdownMenuItem(
                            text = { ProfileItem(user = user) },
                            onClick = {
                                selectedUser = user
                                expanded = false
                            }
                        )
                    }
                }
            }

            deletionMessage?.let {
                Text(text = it)
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { selectedUser?.let(viewModel::setActiveUser) },
                    enabled = selectedUser != null,
                    modifier = Modifier.weight(2f)
                ) {
                    Text("Сделать активным")
                }

                if (canDeleteSelectedUser) {
                    Button(
                        onClick = {
                            userPendingDeletion = selectedUser
                            deletionMessage = null
                        },
                        enabled = selectedUser != null,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Удалить")
                    }
                } else {
                    Button(
                        onClick = {
                            Toast.makeText(
                                context,
                                deletionHint ?: "Удаление запрещено для выбранного профиля.",
                                Toast.LENGTH_SHORT
                            ).show()
                        },
                        enabled = selectedUser != null,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
                            contentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Удалить")
                    }
                }
            }
        }
    }

    userPendingDeletion?.let { user ->
        AlertDialog(
            onDismissRequest = { userPendingDeletion = null },
            title = { Text("Удалить профиль?") },
            text = {
                Text("Профиль \"${user.name}\" (${user.role.label()}) будет удалён без возможности восстановления.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val wasDeleted = viewModel.deleteUser(user)
                        deletionMessage = if (wasDeleted) {
                            if (selectedUser?.id == user.id) {
                                selectedUser = uiState.user?.takeIf { it.id != user.id }
                                    ?: uiState.users.firstOrNull { it.id != user.id }
                            }
                            null
                        } else {
                            "Удаление запрещено для выбранного профиля."
                        }
                        userPendingDeletion = null
                    }
                ) {
                    Text("Удалить")
                }
            },
            dismissButton = {
                TextButton(onClick = { userPendingDeletion = null }) {
                    Text("Отмена")
                }
            }
        )
    }
}

@Composable
private fun ProfileItem(user: User) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = user.name,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = user.role.label(),
            fontSize = 12.sp,
            textAlign = TextAlign.End
        )
    }
}

private fun Role.label(): String = when (this) {
    Role.READER -> "читатель"
    Role.LIBRARIAN -> "библиотекарь"
}