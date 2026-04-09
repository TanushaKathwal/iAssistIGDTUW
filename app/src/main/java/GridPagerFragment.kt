package com.example.iassistdatabase

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.navigation.fragment.findNavController
import com.example.iassistdatabase.GridItem
import com.example.iassistdatabase.GridItemAdapter

class GridPagerFragment : Fragment() {

    private var items: List<GridItem> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        items = arguments?.getParcelableArrayList(ARG_ITEMS) ?: emptyList()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val recyclerView = RecyclerView(requireContext()).apply {
            layoutManager = GridLayoutManager(context, 2)
            adapter = GridItemAdapter(items) { item: GridItem ->
                when (item.title) {
                    "Event Finder" -> findNavController().navigate(R.id.navigation_eventfinder)
                    "Lost and Found" -> findNavController().navigate(R.id.navigation_lostandfound)
                    "Room Finder" -> findNavController().navigate(R.id.navigation_roomfinder)
                    "SGPA Calculator" -> findNavController().navigate(R.id.navigation_sgpacalculator)
                    "Goal Tracker" -> findNavController().navigate(R.id.navigation_goaltracker)
                    "Academic Corner" -> findNavController().navigate(R.id.navigation_academiccorner)
                    "Circulars" -> findNavController().navigate(R.id.navigation_circulars)
                    "Life In Motion" -> findNavController().navigate(R.id.navigation_lifeinmotion)
                    else -> {
                        // Optional fallback
                    }
                }
            }
        }
        return recyclerView
    }

    companion object {
        private const val ARG_ITEMS = "items"

        fun newInstance(items: List<GridItem>): GridPagerFragment {
            val fragment = GridPagerFragment()
            val args = Bundle().apply {
                putParcelableArrayList(ARG_ITEMS, ArrayList(items))
            }
            fragment.arguments = args
            return fragment
        }
    }
}