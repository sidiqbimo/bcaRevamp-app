package com.synrgyseveneight.bcarevamp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.synrgyseveneight.bcarevamp.ui.theme.BCARevampTheme

class MainActivity : AppCompatActivity() {

    private lateinit var bottomAppBar: BottomAppBar
    private lateinit var fab: FloatingActionButton
    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar?.hide()

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.findNavController()

        // Navigation Bar Bottom
        setupActionBarWithNavController(navController)
        bottomAppBar = findViewById(R.id.bottomAppBar)
        fab = findViewById(R.id.fab)
        bottomNavigationView = findViewById(R.id.bottomNavigationView)

        bottomNavigationView.setupWithNavController(navController)


        // Handle navigation item clicks
        bottomNavigationView.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.homeFragmentBottom -> {
                    navController.navigate(R.id.homeFragment)
                    true
                }
                R.id.mutasiFragmentBottom -> {
                    navController.navigate(R.id.mutationFragment)
                    true
                }
                R.id.notifikasiFragmentBottom -> {
                    navController.navigate(R.id.comingsonFragment)
                    true
                }
                R.id.profileFragmentBottom -> {
                    navController.navigate(R.id.comingsonFragment)
                    true
                }
                else -> false
            }
        }

        // Handle FloatingActionButton click
        fab.setOnClickListener {
            navController.navigate(R.id.comingsonFragment)
            true
        }
    }

    private fun setupActionBarWithNavController(navController: Any) {
        TODO("Not yet implemented")
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}