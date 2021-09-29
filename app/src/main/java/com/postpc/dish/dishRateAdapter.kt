package com.postpc.dish

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.DiffUtil
import com.postpc.dish.DishItem
import com.postpc.dish.DishHolder
import android.view.ViewGroup
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ListAdapter
import com.google.common.base.Predicates.not
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.postpc.dish.R

class dishRateAdapter(val listener: ContentListener) :
    ListAdapter<DishItem, dishRateHolder>(DishDiffCallBack3()) {

    private var setDishes = mutableSetOf<String>()
    private var dishes = mutableListOf<DishItem>()
    private var context: Context? = null
    private lateinit var app: DishApplication


    fun setApplication(app: DishApplication) {
        this.app = app
    }

    fun setDishesAdapter(dishes: List<DishItem>) {
        this.dishes = dishes as MutableList<DishItem>
    }

    fun getDishesAdapter(): MutableList<DishItem> {
        return this.dishes
    }

    fun addDishes(dish: DishItem) {
//        dish.match = rating
        if (dish.photo !in setDishes) {
            this.dishes.add(dish)
            this.setDishes.add(dish.photo);
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): dishRateHolder {
        context = parent.context
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.single_dish_to_rate, parent, false)
        return dishRateHolder(view)
    }

    override fun onBindViewHolder(holder: dishRateHolder, position: Int) {
        holder.bind(dishes[position], context)
        var rate_dish = holder.rate_dish
        var dish_id = ""
        rate_dish.setOnClickListener() {
            listener.onItemClicked(dishes[position])
        }
    }

    override fun getItemCount() = dishes.size

    interface ContentListener {
        fun onItemClicked(item: DishItem)
    }

}

private class DishDiffCallBack3 : DiffUtil.ItemCallback<DishItem>() {
    override fun areItemsTheSame(oldItem: DishItem, newItem: DishItem): Boolean =
        oldItem == newItem

    override fun areContentsTheSame(oldItem: DishItem, newItem: DishItem): Boolean =
        oldItem.name == newItem.name
}