package com.postpc.dish

import android.content.Context
import android.util.Log
import androidx.recyclerview.widget.DiffUtil
import com.postpc.dish.DishItem
import com.postpc.dish.DishHolder
import android.view.ViewGroup
import android.view.LayoutInflater
import androidx.recyclerview.widget.ListAdapter
import com.postpc.dish.R

class DishesAdapter: ListAdapter<DishItem, DishHolder>(DishDiffCallBack()) {

    private var dishes = emptyList<DishItem>()
    private var context: Context? = null

    fun setDishesAdapter(dishes: List<DishItem>) {
        this.dishes = dishes
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DishHolder {
        context = parent.context
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.single_dish, parent, false)
        return DishHolder(view)
    }

    override fun onBindViewHolder(holder: DishHolder, position: Int) {
        Log.d("here", "im here")
//        ?.get(position)?.let { Log.d("Dish", it.name) }
        holder.bind(dishes[position])
    }

    override fun getItemCount() = dishes.size

}

private class DishDiffCallBack : DiffUtil.ItemCallback<DishItem>() {
    override fun areItemsTheSame(oldItem: DishItem, newItem: DishItem): Boolean =
            oldItem == newItem

    override fun areContentsTheSame(oldItem: DishItem, newItem: DishItem): Boolean =
            oldItem.name == newItem.name
}