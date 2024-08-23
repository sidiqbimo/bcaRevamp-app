package com.synrgyseveneight.bcarevamp

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    private lateinit var fab: FloatingActionButton
    private lateinit var bottomAppBar: BottomAppBar

    @SuppressLint("CutPasteId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar?.hide()

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        fab = findViewById(R.id.fab)
        val bottomNavigationViewMy = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomAppBar = findViewById(R.id.bottomAppBar)

        // NAVIGATION
        bottomNavigationViewMy.setupWithNavController(navController)

        // Handle FloatingActionButton click
        fab.setOnClickListener {
            navController.navigate(R.id.QRISCameraFragment)
        }

        // Mencegah navigasi redundant
        bottomNavigationViewMy.setOnNavigationItemSelectedListener { menuItem ->
            if (menuItem.itemId != navController.currentDestination?.id) {
                when (menuItem.itemId) {

                    R.id.homeFragment -> {
                        navController.navigate(R.id.homeFragment)
                    }
                    R.id.mutasiFragment -> {
                        navController.navigate(R.id.mutationFragment)
                    }
                    R.id.notifikasiFragment -> {
                        navController.navigate(R.id.notifikasiFragment)
                    }
                    R.id.profileFragment -> {
                        navController.navigate(R.id.profileFragment)
                    }

                }
            }
            true
        }

        // Hide Bottom navigation in submenu
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.homeFragment, R.id.notifikasiFragment, R.id.profileFragment -> {
                    bottomNavigationViewMy.visibility = View.VISIBLE
                    fab.visibility = View.VISIBLE
                    bottomAppBar.visibility = View.VISIBLE
                }

                else -> {
                    bottomNavigationViewMy.visibility = View.GONE
                    fab.visibility = View.GONE
                    bottomAppBar.visibility = View.GONE
                }
            }
        }

    }

}