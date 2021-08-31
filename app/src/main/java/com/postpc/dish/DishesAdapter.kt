package com.postpc.dish

import androidx.recyclerview.widget.DiffUtil
import com.postpc.dish.DishItem
import com.postpc.dish.DishHolder
import android.view.ViewGroup
import android.view.LayoutInflater
import androidx.recyclerview.widget.ListAdapter
import com.postpc.dish.R

class DishesAdapter: ListAdapter<DishItem, DishHolder>(DishDiffCallBack()) {



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DishHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.single_dish, parent, false)
        return DishHolder(view)
    }

    override fun onBindViewHolder(holder: DishHolder, position: Int) {
        val dish = getItem(position) // we have "getItem(int)" by inheritance from ListAdapter
        holder.setDishName(dish!!.name)
        holder.dishImage.setImageResource(dish.image)

    }
}

private class DishDiffCallBack : DiffUtil.ItemCallback<DishItem>() {
    override fun areItemsTheSame(oldItem: DishItem, newItem: DishItem): Boolean =
            oldItem == newItem

    override fun areContentsTheSame(oldItem: DishItem, newItem: DishItem): Boolean =
            oldItem.name == newItem.name && oldItem.resturant == newItem.resturant
}