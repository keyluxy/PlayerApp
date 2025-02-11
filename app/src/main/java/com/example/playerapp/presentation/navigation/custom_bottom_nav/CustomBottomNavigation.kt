

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
//import androidx.navigation.NavController
import com.example.playerapp.presentation.navigation.Screen
import com.example.playerapp.presentation.navigation.custom_bottom_nav.CustomBottomNavigationItem

@Composable
fun CustomBottomNavigation(
    navController: NavController,
    currentRoute: String?,
    screens: List<Screen>
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            screens.forEach { screen ->
                CustomBottomNavigationItem(
                    screen = screen,
                    isSelected = currentRoute == screen.route,
                    onClick = {
                        navController.navigate(screen.route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    }
}