package com.ndejje.nduupdates

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ndejje.nduupdates.view.LoginScreen
import com.ndejje.nduupdates.view.RegisterScreen
import com.ndejje.nduupdates.view.WelcomeScreen
import com.ndejje.nduupdates.view.dashboard.AdminDashboardScreen
import com.ndejje.nduupdates.view.dashboard.LecturerDashboardScreen
import com.ndejje.nduupdates.view.dashboard.StudentDashboardScreen
import com.ndejje.nduupdates.view.notice.CreateNoticeScreen
import com.ndejje.nduupdates.viewmodel.AuthViewModel
import com.ndejje.nduupdates.viewmodel.NoticeViewModel
import com.ndejje.nduupdates.viewmodel.ViewModelFactory

object Routes {
    const val WELCOME = "welcome"
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val STUDENT_DASHBOARD = "student_dashboard"
    const val LECTURER_DASHBOARD = "lecturer_dashboard"
    const val ADMIN_DASHBOARD = "admin_dashboard"
    const val CREATE_NOTICE = "create_notice"
}

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController(),
    factory: ViewModelFactory
) {
    val authViewModel: AuthViewModel = viewModel(factory = factory)
    val noticeViewModel: NoticeViewModel = viewModel(factory = factory)

    NavHost(navController = navController, startDestination = Routes.WELCOME) {
        composable(Routes.WELCOME) { WelcomeScreen(navController) }
        composable(Routes.LOGIN) { LoginScreen(navController, authViewModel) }
        composable(Routes.REGISTER) { RegisterScreen(navController, authViewModel) }
        composable(Routes.STUDENT_DASHBOARD) { StudentDashboardScreen(navController, noticeViewModel, authViewModel) }
        composable(Routes.LECTURER_DASHBOARD) { LecturerDashboardScreen(navController, noticeViewModel, authViewModel) }
        composable(Routes.ADMIN_DASHBOARD) { AdminDashboardScreen(navController, noticeViewModel, authViewModel) }
        composable(Routes.CREATE_NOTICE) { CreateNoticeScreen(navController, noticeViewModel, authViewModel) }
    }
}
