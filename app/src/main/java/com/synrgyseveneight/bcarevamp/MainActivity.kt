package com.synrgyseveneight.bcarevamp

//import com.google.android.material.bottomappbar.BottomAppBar
import android.os.Bundle
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
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var bottomAppBar: BottomAppBar

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
        bottomNavigationView = findViewById(R.id.bottomNavigationView)
        bottomAppBar = findViewById(R.id.bottomAppBar)


//        bottomNavigationView.setupWithNavController(navController)
        // NAVIGATION


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
            navController.navigate(R.id.QRISCameraFragment)
            true
        }

        // Hide Bottom navigation in submenu
        val navigasiBarBottom = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        navigasiBarBottom.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.homeFragment -> {
                    bottomNavigationView.visibility = View.VISIBLE
                    fab.visibility = View.VISIBLE
                    bottomAppBar.visibility = View.VISIBLE}

                else -> {
                    bottomNavigationView.visibility = View.GONE
                    fab.visibility = View.GONE
                    bottomAppBar.visibility = View.GONE
                    }
            }
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