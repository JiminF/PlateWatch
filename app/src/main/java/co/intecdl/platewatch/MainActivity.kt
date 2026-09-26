package co.intecdl.platewatch

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import co.intecdl.platewatch.ui.navigation.PlateWatchApp
import co.intecdl.platewatch.ui.theme.PlateWatchTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { PlateWatchTheme { PlateWatchApp() } }
    }
}
