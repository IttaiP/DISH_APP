package com.postpc.dish;

import androidx.lifecycle.ViewModelProvider;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DiffUtil;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.yuyakaido.android.cardstackview.CardStackLayoutManager;
import com.yuyakaido.android.cardstackview.CardStackListener;
import com.yuyakaido.android.cardstackview.CardStackView;
import com.yuyakaido.android.cardstackview.Direction;
import com.yuyakaido.android.cardstackview.StackFrom;
import com.yuyakaido.android.cardstackview.SwipeableMethod;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class InitUserDishDataFragment extends Fragment {
    private final double FABULOUS = 5;
    private final double SATISFYING = 3.5;
    private final double FINE = 2;
    private final double NEVER_AGAIN = 0.5;

    private InitUserDishDataViewModel mViewModel;
    private FirebaseFirestore database;
    private card_dish_adapter adapter;
    private CardStackLayoutManager layoutManager;
    private List<DishItem> dishes;
    private Context context;
    private String current_dish;
    private String current_category = "italian";
    private ChipGroup category;
    private Chip italian, burger, breakfast, mexican, asian;


    public static InitUserDishDataFragment newInstance() {
        return new InitUserDishDataFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.init_user_dish_data_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        category = view.findViewById(R.id.categoryChipsView);
        category.setOnCheckedChangeListener(new ChipGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(ChipGroup group, int checkedId) {
                Chip current = (Chip) group.getChildAt(checkedId);
                current_category = current.getText().toString();
                addList(current_category);
            }
        });
        context = getContext();
        database = FirebaseFirestore.getInstance();
        CardStackView cardStackView = view.findViewById(R.id.stack_view);
        layoutManager = new CardStackLayoutManager(context, new CardStackListener() {

            @Override
            public void onCardDragging(Direction direction, float ratio) {
                Log.d("MESSAGE", "onCardDragging: d=" + direction.name() + " ratio=" + ratio);
            }

            @Override
            public void onCardSwiped(Direction direction) {
                Log.d("MESSAGE", "onCardSwiped: p=" + layoutManager.getTopPosition() + " d=" + direction);
                if (direction == Direction.Right){
                    update_dish(current_dish, FABULOUS);
                    Toast.makeText(context, "Fabulous!", Toast.LENGTH_SHORT).show();
                }
                if (direction == Direction.Left){
                    update_dish(current_dish, NEVER_AGAIN);
                    Toast.makeText(context, "Never Again!", Toast.LENGTH_SHORT).show();
                }
                if (direction == Direction.Top){
                    update_dish(current_dish, SATISFYING);
                    Toast.makeText(context, "Satisfying", Toast.LENGTH_SHORT).show();
                }
                if (direction == Direction.Bottom){
                    update_dish(current_dish, FINE);
                    Toast.makeText(context, "Fine", Toast.LENGTH_SHORT).show();
                }


                // Paginating
                if (layoutManager.getTopPosition() == adapter.getItemCount()){
                    paginate();
                }
            }

            @Override
            public void onCardRewound() {
                Log.d("MESSAGE", "onCardRewound: " + layoutManager.getTopPosition());
            }

            @Override
            public void onCardCanceled() {
                Log.d("MESSAGE", "onCardCanceled: " + layoutManager.getTopPosition());
            }

            @Override
            public void onCardAppeared(View view, int position) {
                TextView dish_name = view.findViewById(R.id.dish_name);
                current_dish = dish_name.getText().toString();
                Log.d("MESSAGE", "onCardAppeared: " + position + ", name: " + dish_name.getText());
            }

            @Override
            public void onCardDisappeared(View view, int position) {
                TextView tv = view.findViewById(R.id.dish_name);
                Log.d("MESSAGE", "onCardDisappeared: " + position + ", name: " + tv.getText());
            }
        });
        layoutManager.setStackFrom(StackFrom.None);
        layoutManager.setVisibleCount(3);
        layoutManager.setTranslationInterval(8.0f);
        layoutManager.setScaleInterval(0.95f);
        layoutManager.setSwipeThreshold(0.3f);
        layoutManager.setMaxDegree(20.0f);
        layoutManager.setDirections(Direction.FREEDOM);
        layoutManager.setCanScrollHorizontal(true);
        layoutManager.setSwipeableMethod(SwipeableMethod.Manual);
        layoutManager.setOverlayInterpolator(new LinearInterpolator());
        adapter = new card_dish_adapter();
//        add_all_dishes();
//        add_restaurant();
        addList(current_category);
        cardStackView.setLayoutManager(layoutManager);
        cardStackView.setAdapter(adapter);
        cardStackView.setItemAnimator(new DefaultItemAnimator());
        database = FirebaseFirestore.getInstance();
    }

    public void update_dish(String dish_name, double rating) {
        String user_id = DishApplication.
    }

    public void add_restaurant() {
        String restaurant_name = "Pizza Lila";
        database.collection("restaurants").whereEqualTo("name", restaurant_name)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Restaurant restaurant1 = document.toObject(Restaurant.class);
                        Log.d("name", restaurant1.name);
                        document.getReference().collection("dishes").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if(task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document2 : task.getResult()) {
                                        DocumentReference ref = document2.getReference();
                                        ref.update("category", "italian");
                                    }
                                } else {
                                    Log.d("Not Found", "Error: " + task.getException().getMessage());
                                }
                            }
                        });
                    }
                }
                else {
                    Log.d("Error", "Error trying to receive");
                }
            }
        });
    }

    public void add_all_dishes() {
        String restaurant_name = "Pizza Lila";
        database.collection("restaurants").orderBy("name")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        document.getReference().collection("dishes").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if(task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document2 : task.getResult()) {
                                        DishItem dish = document2.toObject(DishItem.class);
                                        DocumentReference ref = document2.getReference();
                                        database.collection("all-dishes").document(ref.getId()).set(dish);
                                    }
                                } else {
                                    Log.d("Not Found", "Error: " + task.getException().getMessage());
                                }
                            }
                        });
                    }
                }
                else {
                    Log.d("Error", "Error trying to receive");
                }
            }
        });
    }

    private void paginate() {
        List<DishItem> oldList = adapter.getDishes();
        List<DishItem> newList = new ArrayList<>(addList("italian"));
        CardStackCallback callback = new CardStackCallback(oldList, newList);
        DiffUtil.DiffResult results = DiffUtil.calculateDiff(callback);
        adapter.setDishes(newList);
        results.dispatchUpdatesTo(adapter);
    }

    private List<DishItem> addList(String catgeory) {
        readData(new MyCallback() {
            @Override
            public void onCallback(List<DishItem> dishesList) {
                adapter.setDishes(dishesList);
            }
        }, catgeory);
        return dishes;
    }

    public interface MyCallback {
        void onCallback(List<DishItem> dishesList);
    }

    public void readData(MyCallback myCallback, String category) {
        database.collection("all-dishes").whereEqualTo("category", category)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    Log.d("HERE", "im here");
//                    for (QueryDocumentSnapshot document: task.getResult()) {
//                        DocumentReference documentReference = document.getReference();
//                        documentReference.update("restaurantName", FieldValue.delete());
//                    }
                    dishes = Objects.requireNonNull(task.getResult()).toObjects(DishItem.class);
                    Log.d("Name is", dishes.get(0).name);
                    myCallback.onCallback(dishes);
                                    Log.d("dishes", dishes.get(0).name);
                                    adapter.setDishes(dishes);
                } else {
                    Log.d("Not Found", "Error: " + task.getException().getMessage());
                }
            }
        });
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(InitUserDishDataViewModel.class);
        // TODO: Use the ViewModel
    }

}