package co.intecdl.platewatch.ui.navigation
import androidx.compose.runtime.Composable
import androidx.navigation.compose.*
import co.intecdl.platewatch.ui.watchedplates.WatchedPlatesRoute
import co.intecdl.platewatch.ui.observedplates.ObservedPlatesRoute
import co.intecdl.platewatch.ui.detections.DetectionsRoute
@Composable fun AppNavigation(){ val nav=rememberNavController(); NavHost(nav,startDestination="dashboard"){ composable("dashboard"){ DashboardScreen({nav.navigate("watched")},{nav.navigate("observed")},{nav.navigate("detections")}) }; composable("watched"){WatchedPlatesRoute{nav.popBackStack()}};composable("observed"){ObservedPlatesRoute{nav.popBackStack()}};composable("detections"){DetectionsRoute{nav.popBackStack()}} } }
