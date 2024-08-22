package com.synrgyseveneight.bcarevamp

//import com.google.android.material.bottomappbar.BottomAppBar
import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

//    private lateinit var bottomAppBar: BottomAppBar
    private lateinit var fab: FloatingActionButton
    private lateinit var bottomAppBar: BottomAppBar

    @SuppressLint("CutPasteId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar?.hide()

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.findNavController()

        // Navigation Bar Bottom
//        setupActionBarWithNavController(navController)
//        bottomAppBar = findViewById(R.id.bottomAppBar)
        fab = findViewById(R.id.fab)
        val bottomNavigationViewMy = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomAppBar = findViewById(R.id.bottomAppBar)


//        bottomNavigationView.setupWithNavController(navController)
        // NAVIGATION
        bottomNavigationViewMy.setupWithNavController(navController)

        // Handle navigation item clicks
        bottomNavigationViewMy.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.homeFragmentBottom -> {
                    findNavController(R.id.nav_host_fragment).apply {
                        Log.d("MainActivity", "Home Fragment")
                        popBackStack(R.id.homeFragment, false)
                        navigate(R.id.homeFragment)
                    }
                    true
                }
                R.id.mutasiFragmentBottom -> {
                    Log.d("MainActivity", "Mutation Fragment")
                    findNavController(R.id.nav_host_fragment).apply {
                        Log.d("MainActivity", "Mutation Fragment")
                        popBackStack(R.id.mutationFragment, false)
                        navigate(R.id.mutationFragment)
                    }
                    true
                }
                R.id.notifikasiFragmentBottom -> {
                    findNavController(R.id.nav_host_fragment).apply {
                        popBackStack(R.id.comingsonFragment, false)
                        navigate(R.id.comingsonFragment)
                    }
                    true
                }
                R.id.profileFragmentBottom -> {
                    findNavController(R.id.nav_host_fragment).apply {
                        popBackStack(R.id.comingsonFragment, false)
                        navigate(R.id.comingsonFragment)
                    }
                    true
                }
                else -> false
            }
        }
//        bottomNavigationView.setOnNavigationItemSelectedListener { menuItem ->
//            when (menuItem.itemId) {
//                R.id.homeFragmentBottom -> {
//                    Log.d("MainActivity", "Home Fragment")
//                    navController.navigate(R.id.homeFragment)
//                    true
//                }
//                R.id.mutasiFragmentBottom -> {
//                    Log.d("MainActivity", "Mutation Fragment")
//                    navController.navigate(R.id.mutationFragment)
//                    true
//                }
//                R.id.notifikasiFragmentBottom -> {
//                    Log.d("MainActivity", "Notification Fragment")
//                    navController.navigate(R.id.comingsonFragment)
//                    true
//                }
//                R.id.profileFragmentBottom -> {
//                    Log.d("MainActivity", "Profile Fragment")
//                    navController.navigate(R.id.comingsonFragment)
//                    true
//                }
//                else -> false
//            }
//        }

        // Handle FloatingActionButton click
        fab.setOnClickListener {
            navController.navigate(R.id.QRISCameraFragment)
            true
        }

        // Hide Bottom navigation in submenu
        val navigasiBarBottom = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        navigasiBarBottom.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.homeFragment -> {
                    bottomNavigationViewMy.visibility = View.VISIBLE
                    fab.visibility = View.VISIBLE
                    bottomAppBar.visibility = View.VISIBLE}

                else -> {
                    bottomNavigationViewMy.visibility = View.GONE
                    fab.visibility = View.GONE
                    bottomAppBar.visibility = View.GONE
                    }
            }
        }

    }



    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}