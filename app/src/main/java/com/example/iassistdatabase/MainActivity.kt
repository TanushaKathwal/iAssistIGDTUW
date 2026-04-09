package com.example.iassistdatabase

import android.os.Bundle
import android.util.Log
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.example.iassistdatabase.databinding.ActivityMainBinding
import com.example.iassistdatabase.ui.notifications.NotificationItem
import com.google.firebase.messaging.FirebaseMessaging
import com.google.android.material.badge.BadgeDrawable
import com.google.firebase.database.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_activity_main)

        // Top-level destinations
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home,
                R.id.navigation_notifications,
                R.id.navigation_profile
            )
        )

        navView.setupWithNavController(navController)

        // Clear bottom nav selection when navigating to non-tab destinations
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.navigation_home,
                R.id.navigation_notifications,
                R.id.navigation_profile -> {
                    // Keep nav selection
                }
                else -> {
                    navView.menu.setGroupCheckable(0, true, false)
                    for (i in 0 until navView.menu.size()) {
                        navView.menu.getItem(i).isChecked = false
                    }
                    navView.menu.setGroupCheckable(0, true, true)
                }
            }
        }

        // Prevent re-navigating to same destination
        navView.setOnItemSelectedListener { item ->
            val currentDestination = navController.currentDestination?.id

            val options = androidx.navigation.NavOptions.Builder()
                .setPopUpTo(R.id.mobile_navigation, true)
                .build()

            when (item.itemId) {
                R.id.navigation_home -> {
                    if (currentDestination != R.id.navigation_home) {
                        navController.navigate(R.id.navigation_home, null, options)
                    }
                    true
                }
                R.id.navigation_notifications -> {
                    if (currentDestination != R.id.navigation_notifications) {
                        navController.navigate(R.id.navigation_notifications, null, options)
                        removeBadge(navView) // clear badge when opened
                    }
                    true
                }
                R.id.navigation_profile -> {
                    val currentId = navController.currentDestination?.id
                    val resolvedName = try {
                        resources.getResourceEntryName(currentId ?: -1)
                    } catch (e: Exception) {
                        "Unknown"
                    }

                    Log.d("NavDebug", "Current Destination ID: $currentId, Name: $resolvedName")

                    if (currentId != R.id.profileEntryFragment) {
                        navController.navigate(R.id.profileEntryFragment, null, options)
                    }
                    true
                }
                else -> false
            }
        }

        // Subscribe user to "all" topic for notifications
        FirebaseMessaging.getInstance().subscribeToTopic("all")
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("FCM", "Subscribed to 'all' topic")
                } else {
                    Log.e("FCM", "Subscription failed", task.exception)
                }
            }

        // 🔹 Listen for notifications in Firebase and show badge
        database = FirebaseDatabase.getInstance().getReference("appNotifications")
        listenForNewNotifications(navView)
    }

    private fun listenForNewNotifications(navView: BottomNavigationView) {
        // Unified to use same path as NotificationsFragment
        database = FirebaseDatabase.getInstance().getReference("notifications")

        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Count only unread notifications
                val unreadCount = snapshot.children
                    .mapNotNull { it.getValue(NotificationItem::class.java) }
                    .count { !it.read }

                if (unreadCount > 0) {
                    val badge: BadgeDrawable = navView.getOrCreateBadge(R.id.navigation_notifications)
                    badge.isVisible = true
                    badge.number = unreadCount
                } else {
                    removeBadge(navView)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Error loading notifications", error.toException())
            }
        })
    }

    private fun removeBadge(navView: BottomNavigationView) {
        navView.removeBadge(R.id.navigation_notifications)
    }
}
