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

        val roomId = intent.getStringExtra("roomId")

        setContent {
            App(
                imagePicker = ImagePickerFactory().createPicker(),
                initialRoomId = roomId
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