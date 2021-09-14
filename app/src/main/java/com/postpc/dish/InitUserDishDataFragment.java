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

    private InitUserDishDataViewModel mViewModel;
    private FirebaseFirestore database;
    private card_dish_adapter adapter;
    private CardStackLayoutManager layoutManager;
    private List<DishItem> dishes;
    private Context context;

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

                    Toast.makeText(context, "Fabulous!", Toast.LENGTH_SHORT).show();
                }
                if (direction == Direction.Left){
                    Toast.makeText(context, "Never Again!", Toast.LENGTH_SHORT).show();
                }
                if (direction == Direction.Top){
                    Toast.makeText(context, "Satisfying", Toast.LENGTH_SHORT).show();
                }
                if (direction == Direction.Bottom){
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
                TextView tv = view.findViewById(R.id.dish_name);
                Log.d("MESSAGE", "onCardAppeared: " + position + ", name: " + tv.getText());
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
        addList();
        cardStackView.setLayoutManager(layoutManager);
        cardStackView.setAdapter(adapter);
        cardStackView.setItemAnimator(new DefaultItemAnimator());
        database = FirebaseFirestore.getInstance();
    }

    private void paginate() {
        List<DishItem> oldList = adapter.getDishes();
        List<DishItem> newList = new ArrayList<>(addList());
        CardStackCallback callback = new CardStackCallback(oldList, newList);
        DiffUtil.DiffResult results = DiffUtil.calculateDiff(callback);
        adapter.setDishes(newList);
        results.dispatchUpdatesTo(adapter);
    }

    private List<DishItem> addList() {
        readData(new MyCallback() {
            @Override
            public void onCallback(List<DishItem> dishesList) {
                adapter.setDishes(dishesList);
            }
        });
//        database.collection("restaurants").whereEqualTo("name", "Pizza Lila")
//                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                if(task.isSuccessful()) {
//                    for (QueryDocumentSnapshot document : task.getResult()) {
//                        Restaurant restaurant1 = document.toObject(Restaurant.class);
//                        Log.d("name", restaurant1.name);
//                        document.getReference().collection("dishes").orderBy("name").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                            @Override
//                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                                if(task.isSuccessful()) {
//                                    dishes = Objects.requireNonNull(task.getResult()).toObjects(DishItem.class);
//                                    Log.d("dishes", dishes.get(0).name);
////                                    adapter.setDishes(dishes);
//                                } else {
//                                    Log.d("Not Found", "Error: " + task.getException().getMessage());
//                                }
//                            }
//                        });
//                    }
//                }
//                else {
//                    Log.d("Error", "Error trying to receive");
//                }
//            }
//        });
        return dishes;
    }

    public interface MyCallback {
        void onCallback(List<DishItem> dishesList);
    }

    public void readData(MyCallback myCallback) {
        database.collection("restaurants").whereEqualTo("name", "Taqueria")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Restaurant restaurant1 = document.toObject(Restaurant.class);
                        Log.d("name", restaurant1.name);
                        document.getReference().collection("dishes").orderBy("name").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if(task.isSuccessful()) {
                                    dishes = Objects.requireNonNull(task.getResult()).toObjects(DishItem.class);
                                    myCallback.onCallback(dishes);
//                                    Log.d("dishes", dishes.get(0).name);
//                                    adapter.setDishes(dishes);
                                } else {
                                    Log.d("Not Found", "Error: " + task.getException().getMessage());
                                }
                            }
                        });
                    }
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