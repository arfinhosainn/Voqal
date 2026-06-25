package app.voqal.com

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import app.voqal.com.core.di.initKoin
import app.voqal.com.core.presentation.util.ImagePickerFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            App(
                ImagePickerFactory().createPicker()
            )
        }
    }
}
//
//@Preview
//@Composable
//fun AppAndroidPreview() {
//    App()
//}