package com.jhealth.diabetesapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import androidx.core.view.isVisible
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.jhealth.diabetesapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private val CURRENT_DESTINATION_ID = "current destination"
    private lateinit var navController: NavController
    private lateinit var binding: ActivityMainBinding
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var currentDestination: NavDestination

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        super.onSaveInstanceState(outState, outPersistentState)
        outState.putInt(CURRENT_DESTINATION_ID,currentDestination.id)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val destinationId = savedInstanceState?.getInt(CURRENT_DESTINATION_ID,0) ?: 0

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        appBarConfiguration = AppBarConfiguration(

            setOf(
                R.id.homeFragment,
                R.id.articleHomeFragment,
                R.id.recipesFragment,
                R.id.forumFragment
            )
        )

        val fragHost = supportFragmentManager.findFragmentById(R.id.frag_host) as NavHostFragment
        navController = fragHost.findNavController()




        navController.addOnDestinationChangedListener { _, destination, _ ->
            currentDestination = destination
            when (destination.id) {
                R.id.authFragment,R.id.welcomeFragment -> {
                    binding.bottomNav.isVisible = false
                }
                else -> {
                    binding.bottomNav.isVisible = appBarConfiguration.topLevelDestinations.contains(destination.id)
                }
            }
        }

        binding.bottomNav.setupWithNavController(navController)
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}