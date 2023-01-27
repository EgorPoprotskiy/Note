package com.egorpoprotskiy.note

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.egorpoprotskiy.note.databinding.ActivityMainBinding
import androidx.navigation.ui.NavigationUI.setupActionBarWithNavController

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // 6.1 В MainActivity.kt, переопределить onCreate()метод настройки навигационного контроллера. Получить экземпляр NavController от NavHostFragment.
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        // 6.2 отображается название всех фрагментов и кнопка "назад". (Название фрагментов можно изменить в nav_graph в пункте label)
        setupActionBarWithNavController(this, navController)
    }
    // 6.3 Реализация кнопки назад(сразу на всех фрагментах)
    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}