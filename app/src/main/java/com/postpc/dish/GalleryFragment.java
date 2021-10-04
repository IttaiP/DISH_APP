package com.postpc.dish;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class GalleryFragment extends Fragment {

    private GalleryViewModel mViewModel;
    private RecyclerView recyclerView;
    private GalleryImageAdapter adapter;
    private ImageView realDishPhoto;
    private TextView dishName;
    private DishApplication app;

    public static GalleryFragment newInstance() {
        return new GalleryFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.gallery_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        adapter = new GalleryImageAdapter();
        recyclerView = view.findViewById(R.id.photos_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        recyclerView.setAdapter(adapter);

        app = (DishApplication)getActivity().getApplication().getApplicationContext();

        TextView no_photos_uploaded = view.findViewById(R.id.no_photos_uploaded);
        no_photos_uploaded.setVisibility(View.GONE);
        Bundle arguments = getArguments();
        DishItem dish = (DishItem) arguments.getSerializable("dish");
        String dishId = arguments.getString("dishId");

        realDishPhoto = view.findViewById(R.id.dish_image);
        dishName = view.findViewById(R.id.dish_name);

        dishName.setText(dish.name);
        int resourceId = getContext().getResources().getIdentifier(dish.photo, "drawable",
                getContext().getPackageName());
        realDishPhoto.setImageResource(resourceId);
        app.info.database.collection("all-dishes").document(dishId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot documentSnapshot = task.getResult();
                List<String> uris = (List<String>) documentSnapshot.get("photos");
                if((uris != null) && (!uris.isEmpty())) {
                    for (String uri : uris) {
                        Log.e("CURRENT URI", uri.toString());
                        adapter.addUri(uri);
                    }
                    adapter.notifyDataSetChanged();
                }
                else {
                    no_photos_uploaded.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(GalleryViewModel.class);
        // TODO: Use the ViewModel
    }


}