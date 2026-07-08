package app.voqal.com.feature.permission.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import app.voqal.com.core.permissions.domain.PermissionType
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun PermissionRoot(
    permissionType: PermissionType,
    emoji: String,
    title: String,
    description: String,
    onPermissionHandled: () -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel = koinViewModel<PermissionViewModel> {
        parametersOf(permissionType, emoji, title, description)
    }
    val state by viewModel.state.collectAsState()

    LaunchedEffect(viewModel.events) {
        viewModel.events.collect { event ->
            when (event) {
                is PermissionScreenEvent.PermissionHandled -> {
                    onPermissionHandled()
                }
            }
        }
    }

    PermissionScreen(
        state = state,
        onAction = viewModel::onAction,
        modifier = modifier
    )
}
