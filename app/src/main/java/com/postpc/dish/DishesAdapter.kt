package com.postpc.dish

import android.content.Context
import androidx.recyclerview.widget.DiffUtil
import com.postpc.dish.DishItem
import com.postpc.dish.DishHolder
import android.view.ViewGroup
import android.view.LayoutInflater
import androidx.recyclerview.widget.ListAdapter
import com.postpc.dish.R

class DishesAdapter: ListAdapter<DishItem, DishHolder>(DishDiffCallBack()) {

    private var dishes: List<DishItem>? = null
    private var context: Context? = null

    fun dishes_adapter(dishes: List<DishItem>) {
        this.dishes = dishes
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DishHolder {
        context = parent.context
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.single_dish, parent, false)
        return DishHolder(view)
    }

    override fun onBindViewHolder(holder: DishHolder, position: Int) {
        holder.bind(dishes?.get(position), context)
    }

    fun setAdapter(new_dishes: List<DishItem>) {
        this.dishes = new_dishes
    }

}

private class DishDiffCallBack : DiffUtil.ItemCallback<DishItem>() {
    override fun areItemsTheSame(oldItem: DishItem, newItem: DishItem): Boolean =
            oldItem == newItem

    override fun areContentsTheSame(oldItem: DishItem, newItem: DishItem): Boolean =
            oldItem.name == newItem.name
}