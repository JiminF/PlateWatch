package co.intecdl.platewatch
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import co.intecdl.platewatch.ui.screens.PlateWatchRoot
import co.intecdl.platewatch.ui.theme.PlateWatchTheme
import co.intecdl.platewatch.ui.watchedplates.watchedplatesRoute
import co.intecdl.platewatch.ui.observedplates
import co.intecdl.platewatch.ui.detections.DetectionsRoute
class MainActivity: ComponentActivity(){
     override fun onCreate(savedInstanceState: Bundle?){ 
        super.onCreate(savedInstanceState); 
        
        setContent{
             PlateWatchTheme{
                DetectionsRoute(
                    onExti = {}
                )
       
    
   
 } } } }
