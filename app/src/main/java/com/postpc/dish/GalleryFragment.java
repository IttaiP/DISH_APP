package com.postpc.dish;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.storage.StorageReference;

public class GalleryFragment extends Fragment {

    private GalleryViewModel mViewModel;
    private RecyclerView recyclerView;
//    private GalleryImageAdapter adapter;
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
//        adapter = new GalleryImageAdapter();
        recyclerView = view.findViewById(R.id.photos_by_users);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
//        recyclerView.setAdapter(adapter);

        app = (DishApplication)getActivity().getApplication().getApplicationContext();

        StorageReference storageReference = app.info.firebaseStorage.getReference();

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(GalleryViewModel.class);
        // TODO: Use the ViewModel
    }

}