package app.voqal.com

import androidx.compose.ui.uikit.LocalUIViewController
import androidx.compose.ui.window.ComposeUIViewController
import app.voqal.com.core.presentation.util.ImagePickerFactory

fun MainViewController() = ComposeUIViewController { App(
    imagePicker = ImagePickerFactory(LocalUIViewController.current).createPicker()
) }