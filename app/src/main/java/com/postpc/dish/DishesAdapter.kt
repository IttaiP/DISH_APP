package com.postpc.dish

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.DiffUtil
import com.postpc.dish.DishItem
import com.postpc.dish.DishHolder
import android.view.ViewGroup
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ListAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.postpc.dish.R

class DishesAdapter(val listener2: ContentListener) :
ListAdapter<DishItem, DishHolder>(DishDiffCallBack()) {

    private lateinit var linearLayout: LinearLayout
    private lateinit var order: LinearLayout
    private var dishes = emptyList<DishItem>()
    private var context: Context? = null
    private lateinit var app: DishApplication

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
        holder.bind(dishes[position])
        order = holder.order
        linearLayout = holder.linearLayout
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
        linearLayout.setOnClickListener() {
            listener2.onItemClicked(dishes[position])
        }
    }

    interface ContentListener {
        fun onItemClicked(item: DishItem)
    }


    override fun getItemCount() = dishes.size

}

private class DishDiffCallBack : DiffUtil.ItemCallback<DishItem>() {
    override fun areItemsTheSame(oldItem: DishItem, newItem: DishItem): Boolean =
            oldItem == newItem

    override fun areContentsTheSame(oldItem: DishItem, newItem: DishItem): Boolean =
            oldItem.name == newItem.name
}