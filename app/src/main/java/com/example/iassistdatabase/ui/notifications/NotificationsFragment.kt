package com.example.iassistdatabase.ui.notifications

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.iassistdatabase.R
import com.example.iassistdatabase.databinding.FragmentNotificationsBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class NotificationsFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private val notificationsList = mutableListOf<NotificationItem>()
    private lateinit var adapter: NotificationsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_notifications, container, false)

        recyclerView = view.findViewById(R.id.notificationsRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        adapter = NotificationsAdapter(notificationsList)
        recyclerView.adapter = adapter

        fetchNotifications()

        return view
    }

    private fun fetchNotifications() {
        val dbRef = FirebaseDatabase.getInstance().getReference("notifications")
        dbRef.orderByChild("timestamp")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    notificationsList.clear()
                    for (child in snapshot.children) {
                        val notification = child.getValue(NotificationItem::class.java)
                        if (notification != null) {
                            notification.key = child.key ?: ""  // 🔹 Save Firebase key
                            notificationsList.add(notification)
                        }
                    }

                    notificationsList.sortByDescending { it.timestamp }
                    adapter.notifyDataSetChanged()

                    val hasUnread = notificationsList.any { !it.read }
                    showBadgeOnNotifications(hasUnread)

                    // ✅ Mark as read when loaded
                    if (hasUnread) {
                        markAllAsRead()
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    private fun markAllAsRead() {
        val dbRef = FirebaseDatabase.getInstance().getReference("notifications")
        notificationsList.forEach { notif ->
            if (!notif.read && notif.key.isNotEmpty()) {
                dbRef.child(notif.key).child("read").setValue(true)
            }
        }

        // Remove badge instantly when opened
        val bottomNav = activity?.findViewById<BottomNavigationView>(R.id.nav_view)
        bottomNav?.removeBadge(R.id.navigation_notifications)
    }


    private fun showBadgeOnNotifications(show: Boolean) {
        val bottomNav = activity?.findViewById<BottomNavigationView>(R.id.nav_view)
        val badge = bottomNav?.getOrCreateBadge(R.id.navigation_notifications)
        badge?.isVisible = show
        badge?.number = if (show) notificationsList.count { !it.read } else 0
    }
}

