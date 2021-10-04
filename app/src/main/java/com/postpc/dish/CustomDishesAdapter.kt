package com.postpc.dish

import android.content.Context
import android.util.Log
import androidx.recyclerview.widget.DiffUtil
import com.postpc.dish.DishItem
import com.postpc.dish.DishHolder
import android.view.ViewGroup
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.ListAdapter
import com.chauthai.swipereveallayout.SwipeRevealLayout
import com.chauthai.swipereveallayout.ViewBinderHelper
import com.google.common.base.Predicates.not
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.postpc.dish.R
import java.util.*

class CustomDishesAdapter(val listener3: ContentListener) :
    ListAdapter<DishItem, CustomDishHolder>(DishDiffCallBack2()){

    private lateinit var linearLayout: LinearLayout
    private var setDishes = mutableSetOf<String>()
    private var dishes = mutableListOf<DishItem>()
    private var context: Context? = null
    private var viewBinderHelper = ViewBinderHelper()
    private lateinit var order: LinearLayout
    private lateinit var app: DishApplication
    private lateinit var firestore: FirebaseFirestore

    fun setDishesAdapter(dishes: List<DishItem>) {
        this.dishes = dishes as MutableList<DishItem>
    }

    fun getDishesAdapter(): MutableList<DishItem> {
        return this.dishes
    }

    fun addDishes(dish: DishItem, rating: Float) {
//        dish.match = rating
        if (dish.photo !in setDishes) {
            dish.match = rating
            this.dishes.add(dish)
            this.setDishes.add(dish.photo);
        }

        Log.e("dishes", this.dishes.toString())
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomDishHolder {
        context = parent.context
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.custom_single_dish, parent, false)
        return CustomDishHolder(view)
    }

    override fun onBindViewHolder(holder: CustomDishHolder, position: Int) {
        viewBinderHelper.setOpenOnlyOne(true)
        viewBinderHelper.bind(holder.swipeRevealLayout, dishes[position].name)
        viewBinderHelper.closeLayout(dishes[position].name)
        holder.bind(dishes[position])
        order = holder.order
        linearLayout = holder.linearLayout;
        order.setOnClickListener { it ->
            app = it.context.applicationContext as DishApplication
            app.info.database.collection("all-dishes")
                .whereEqualTo("description", holder._dish_description.text.toString()).get()
                .addOnCompleteListener {
                    if (it.isSuccessful){
                        for (documentSnapshot in it.result.documents){
                            app.info.addDishToRate(documentSnapshot.id)
                            Log.e("ORDERED DISH ID:", documentSnapshot.id) // todo: to delete
                            Toast.makeText(context, "Ordered dish successfully!", Toast.LENGTH_SHORT).show()
                        }
                    }
                    else{
                        Toast.makeText(context, "Dish was not found in database", Toast.LENGTH_SHORT).show()
                    }
                }
        }
        linearLayout.setOnClickListener {
            listener3.onItemClicked(dishes[position])
        }

    }

    override fun getItemCount() = dishes.size


    interface ContentListener {
        fun onItemClicked(item: DishItem)
    }

}

private class DishDiffCallBack2 : DiffUtil.ItemCallback<DishItem>() {
    override fun areItemsTheSame(oldItem: DishItem, newItem: DishItem): Boolean =
        oldItem == newItem

    override fun areContentsTheSame(oldItem: DishItem, newItem: DishItem): Boolean =
        oldItem.name == newItem.name
}

