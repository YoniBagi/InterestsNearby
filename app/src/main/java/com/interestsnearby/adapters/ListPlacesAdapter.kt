package com.interestsnearby.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.interestsnearby.R
import com.interestsnearby.dataModel.Place
import com.interestsnearby.dataModel.WayPoint
import com.interestsnearby.databinding.AdapterListPlacesBinding

class ListPlacesAdapter(private val listPlaces: List<Place>, val listener: ListPlacesCallBack) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = DataBindingUtil.inflate<AdapterListPlacesBinding>(
            LayoutInflater.from(parent.context),
            R.layout.adapter_list_places,
            parent,
            false
        )
        return ListPlacesViewHolder(binding)
    }

    override fun getItemCount(): Int = listPlaces.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder as ListPlacesViewHolder
        holder.onBind(listPlaces[position])
    }

    inner class ListPlacesViewHolder(private val binding: AdapterListPlacesBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun onBind(place: Place) {
            binding.listPlacesViewHolder = this
            binding.place = place
            binding.executePendingBindings()
        }

        fun onClickItem(location: WayPoint) {
            listener.onClickItem(location)
        }
    }

    interface ListPlacesCallBack {
        fun onClickItem(location: WayPoint)
    }
}