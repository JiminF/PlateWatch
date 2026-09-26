package co.intecdl.platewatch
import android.Manifest
import android.content.pm.PackageManager
import andorid.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import co.intecdl.platewatch.notifications.AlertManager
import co.intecdl.platewatch.ui.navigation.AppNavigation
import co.intecdl.platewatch.ui.screens.PlateWatchRoot
import co.intecdl.platewatch.ui.theme.PlateWatchTheme
import co.intecdl.platewatch.ui.watchedplates.watchedplatesRoute
import co.intecdl.platewatch.ui.observedplates
import co.intecdl.platewatch.ui.navigation.AppNavigation
import co.intecdl.platewatch.ui.detections.DetectionsRoute
import co.intecdl.platewatch.notifications.AlertManager

class MainActivity: ComponentActivity(){
    private val notificatioPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ){
        //Si el usuario rechaza el permiso, la vigilancia puede continuar,
        //pero Android no mostrará las notificaciones.

    }

     override fun onCreate(savedInstanceState: Bundle?){ 
        super.onCreate(savedInstanceState); 

        AlertManager(applicationContext).createChannel()
        
        setContent{
             PlateWatchTheme{
                PlateWatchTheme{
                    AppNavigation()
                }
        } 
    } 
 }
 private fun requestNotificationPermissionIfRequired(){
    if(
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
ContextCompat.checkSelfPermission(
this,
Manifest.permission.POST_NOTIFICATIONS
    )!= PackageManager.PERMISSION_GRANTED
) {
notificationPermissionLauncher.launch(
Manifest.permission.POST_NOTIFICATIONS
)


 }
  }
}
