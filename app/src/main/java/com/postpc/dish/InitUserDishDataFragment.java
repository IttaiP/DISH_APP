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
    private CardStackLayoutManager manager;
    private card_dish_adapter adapter;
    private FirebaseFirestore database;
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
        dishes = new ArrayList<>();
        CardStackView card_dish_view = view.findViewById(R.id.dish_card_view);
        manager = new CardStackLayoutManager(context, new CardStackListener() {
            @Override
            public void onCardDragging(Direction direction, float ratio) {
                Log.d("DRAGGED", direction.name() + " RATIO " + ratio);
            }

            @Override
            public void onCardSwiped(Direction direction) {
                Log.d("MSG:", "onCardSwiped: p=" + manager.getTopPosition() + "d=" + direction);
                if(direction == Direction.Right) {
                    Toast.makeText(getContext(), "Direction Right", Toast.LENGTH_SHORT).show();
                }
                if(direction == Direction.Left) {
                    Toast.makeText(getContext(), "Direction Left", Toast.LENGTH_SHORT).show();
                }

                if(manager.getTopPosition() == adapter.getItemCount() - 5) {
                    paginate();
                }
            }

            @Override
            public void onCardRewound() {

            }

            @Override
            public void onCardCanceled() {

            }

            @Override
            public void onCardAppeared(View view, int position) {

            }

            @Override
            public void onCardDisappeared(View view, int position) {

            }
        });
        manager.setDirections(Direction.HORIZONTAL);
        manager.setStackFrom(StackFrom.None);
        manager.setVisibleCount(3);
        manager.setTranslationInterval(8.0f);
        manager.setScaleInterval(0.95f);
        manager.setSwipeThreshold(0.3f);
        manager.setMaxDegree(20.0f);
        manager.setDirections(Direction.FREEDOM);
        manager.setCanScrollHorizontal(true);
        manager.setSwipeableMethod(SwipeableMethod.Manual);
        manager.setOverlayInterpolator(new LinearInterpolator());
        adapter = new card_dish_adapter(addList());
        card_dish_view.setLayoutManager(manager);
        card_dish_view.setAdapter(adapter);
        card_dish_view.setItemAnimator(new DefaultItemAnimator());
    }

    private void paginate() {
        List<DishItem> oldList = adapter.getDishes();
        List<DishItem> newList = new ArrayList<>(addList());
        CardStackCallback callback = new CardStackCallback(oldList, newList);
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(callback);
        adapter.setDishes(newList);
        diffResult.dispatchUpdatesTo(adapter);
    }

    private List<DishItem> addList() {
        database.collection("restaurants").whereEqualTo("name", "nam")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Restaurant restaurant1 = document.toObject(Restaurant.class);
                        int resourceId = context.getResources().getIdentifier(restaurant1.code, "drawable",
                                context.getPackageName());
                        Log.d("name", restaurant1.name);
                        document.getReference().collection("dishes").orderBy("name").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if(task.isSuccessful()) {
                                    dishes = (ArrayList<DishItem>) Objects.requireNonNull(task.getResult()).toObjects(DishItem.class);
                                    adapter.setDishes(dishes);
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
        return dishes;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(InitUserDishDataViewModel.class);
        // TODO: Use the ViewModel
    }

}