package com.my.golftrainer.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.currentBackStackEntryAsState
import com.my.golftrainer.R

@Composable
fun BottomBar(
    navController: NavController,
    navigateToScreen: (String) -> Unit
) {
    val bottomBarScreens = listOf(Screen.Home, Screen.History)
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    if (currentDestination?.route in bottomBarScreens.map { it.name })
        Column(Modifier.fillMaxWidth()) {
            Divider(
                modifier = Modifier.fillMaxWidth(),
                thickness = 2.dp,
                color = Color(235, 235, 235, 255)
            )
            Row(
                Modifier
                    .height(60.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {

                bottomBarScreens.forEach { screen ->
                    val selected =
                        currentDestination?.hierarchy?.any { it.route == screen.name } == true
                    val color =
                        if (selected) Color(34, 34, 34, 255)
                        else Color(104, 104, 104, 255)
                    Column(
                        modifier = Modifier
                            .clickableWithoutAnimation(onClick = {
                                navigateToScreen(
                                    screen.name
                                )
                            })
                            .weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Icon(
                            modifier = Modifier.size(20.dp),
                            painter = painterResource(id = screen.icon),
                            tint = color,
                            contentDescription = null
                        )
                        HeightSpacer(height = 5.dp)
                        Text(
                            text = stringResource(id = screen.label),
                            color = color,
                            fontSize = 12.sp,
                            fontFamily = FontFamily(Font(R.font.rubik)),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

            }
        }
}
