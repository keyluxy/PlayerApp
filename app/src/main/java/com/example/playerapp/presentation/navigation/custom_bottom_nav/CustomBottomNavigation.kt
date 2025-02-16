package com.example.playerapp.presentation.navigation.custom_bottom_nav

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.playerapp.presentation.navigation.Screen

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
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center // Центрируем элементы
        ) {
            // Первая кнопка
            CustomBottomNavigationItem(
                screen = screens[0],
                isSelected = currentRoute == screens[0].route,
                onClick = {
                    navController.navigate(screens[0].route) {
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )

            Spacer(modifier = Modifier.width(32.dp))

            CustomBottomNavigationItem(
                screen = screens[1],
                isSelected = currentRoute == screens[1].route,
                onClick = {
                    navController.navigate(screens[1].route) {
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