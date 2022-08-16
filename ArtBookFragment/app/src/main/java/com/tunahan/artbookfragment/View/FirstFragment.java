package com.tunahan.artbookfragment.View;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tunahan.artbookfragment.Model.Art;
import com.tunahan.artbookfragment.Adapter.ArtAdapter;
import com.tunahan.artbookfragment.Roomdb.ArtDao;
import com.tunahan.artbookfragment.Roomdb.ArtDataBase;
import com.tunahan.artbookfragment.databinding.FragmentFirstBinding;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;


public class FirstFragment extends Fragment {

    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    ArtDataBase artdb;
    ArtDao artDao;
    ArtAdapter artAdapter;
    private  FragmentFirstBinding binding;

     public FirstFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        artdb = Room.databaseBuilder(requireContext(),ArtDataBase.class,"Arts").build();
        artDao = artdb.artDao();

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding= FragmentFirstBinding.inflate(inflater,container,false);
        View view= binding.getRoot();
        return view;



    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        binding.recyclerView.setLayoutManager(layoutManager);
        getData();

    }

    public void getData(){
        compositeDisposable.add(artDao.getArtWithNameAndId()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(FirstFragment.this::handleResponse));
    }

    private void handleResponse(List<Art> artList){
         binding.recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
         artAdapter = new ArtAdapter(artList);
         binding.recyclerView.setAdapter(artAdapter);



    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding=null;
        compositeDisposable.clear();

    }
}