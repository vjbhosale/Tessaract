package com.example.myapplication.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.listener.OnItemClickListener
import com.example.myapplication.model.AppList
import kotlinx.android.synthetic.main.chat_single_row.view.*

class AppAdapter(
    val context: Context?,
    val listener: OnItemClickListener,
    var allApps: List<AppList>?
):  RecyclerView.Adapter<AppAdapter.ViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.chat_single_row,
                parent,
                false
            ))
    }

    override fun getItemCount(): Int {
        if (allApps==null)
        {
            return 0
        }else{
            return allApps?.size!!
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.itemView.ic_app.setImageDrawable(allApps?.get(position)?.loadIcon)
        holder.itemView.appTV.text=allApps?.get(position)?.appName
        holder.itemView.appCard.setOnClickListener {

            listener.onClick(position,holder.itemView,allApps?.get(position)!!)
        }
    }

    fun addData(data: List<AppList>) {
        allApps=data.sortedBy { it.appName }
        notifyDataSetChanged()
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // Holds the TextView that will add each animal to
    }
}