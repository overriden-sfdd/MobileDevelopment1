package com.edu.mobileapponeassignment.ui.main.recyclerview

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import com.edu.mobileapponeassignment.R
import com.edu.mobileapponeassignment.databinding.PageButtonItemBinding

// Create the basic adapter extending from RecyclerView.Adapter
// Note that we specify the custom ViewHolder which gives us access to our views
class AppPageAdapter(private val mAppPages: List<AppPage>,
                     private val onItemClicked: (AppPage?) -> Unit
) : RecyclerView.Adapter<AppPageAdapter.ViewHolder>() {

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    inner class ViewHolder(binding: PageButtonItemBinding) : RecyclerView.ViewHolder(binding.root) {
        // Your holder should contain and initialize a member variable
        // for any view that will be set as you render a row
        val messageButton: Button = binding.root.findViewById(R.id.page_fragment_btn)
    }

    // ... constructor and member variables
    // Usually involves inflating a layout from XML and returning the holder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = PageButtonItemBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(binding)
    }

    // Involves populating data into the item through holder
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        // Get the data model based on position
        val appPage: AppPage = mAppPages[position]
        // Set item views based on your views and data model

        viewHolder.messageButton.text = appPage.pageName
        viewHolder.messageButton.isEnabled = appPage.isEnabled
        viewHolder.messageButton.setTextColor(Color.BLACK)

        viewHolder.messageButton.setOnClickListener {
            onItemClicked(appPage)
        }

    }

    // Returns the total count of items in the list
    override fun getItemCount(): Int {
        return mAppPages.size
    }
}