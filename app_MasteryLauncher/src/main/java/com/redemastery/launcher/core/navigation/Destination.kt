package com.redemastery.launcher.core.navigation

import androidx.navigation.NamedNavArgument
import androidx.navigation.NavType
import androidx.navigation.navArgument

sealed class Destination(
    val route: String,
    val arguments: List<NamedNavArgument> = emptyList()
) {
    object TestingStorage : Destination("testing_storage")
    object MissingStorage : Destination("MissingStorage")
    object Login : Destination("login")
    object Launcher : Destination("launcher")

    object AAAA : Destination(
        route = "alarms/edit?id={id}",
        arguments = listOf(
            navArgument("id") {
                type = NavType.LongType
                defaultValue = -1L
            }
        )
    ) {
        operator fun invoke(id: Long?): String {
            val newId = id ?: -1L
            return "alarms/edit?id=$newId"
        }
    }
}