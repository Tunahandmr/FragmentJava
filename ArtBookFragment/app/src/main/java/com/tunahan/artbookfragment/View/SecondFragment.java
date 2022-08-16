package com.tunahan.artbookfragment.View;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.room.Room;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.tunahan.artbookfragment.Model.Art;
import com.tunahan.artbookfragment.R;
import com.tunahan.artbookfragment.Roomdb.ArtDao;
import com.tunahan.artbookfragment.Roomdb.ArtDataBase;
import com.tunahan.artbookfragment.Roomdb.ArtDataBase_Impl;
import com.tunahan.artbookfragment.databinding.FragmentFirstBinding;
import com.tunahan.artbookfragment.databinding.FragmentSecondBinding;

import java.io.ByteArrayOutputStream;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;


public class SecondFragment extends Fragment {

    SQLiteDatabase sqLiteDatabase;
    String info = "";
    private  FragmentSecondBinding binding;
    Bitmap selectedImage;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    ArtDataBase db;
    ArtDao artDao;
    Art selectedArt;
    ActivityResultLauncher<Intent> intentActivityResultLauncher;
    ActivityResultLauncher<String> permissionLauncher;


    public SecondFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startLauncher();

        db = Room.databaseBuilder(requireContext(),ArtDataBase.class,"Arts").build();
        artDao = db.artDao();


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding= FragmentSecondBinding.inflate(inflater,container,false);
        View view = binding.getRoot();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sqLiteDatabase = requireActivity().openOrCreateDatabase("Arts",Context.MODE_PRIVATE,null);

        if (getArguments()!=null){
            info = SecondFragmentArgs.fromBundle(getArguments()).getArtInfoSecond();
        }else{
            info="new";
        }

        binding.saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                save(view);
            }
        });

        binding.selectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickImage( view);

            }

        });

        if (info.equals("new")){
            binding.artNameText.setText("");
            binding.artistNameText.setText("");
            binding.yearText.setText("");

            binding.selectImage.setImageResource(R.drawable.selectimage);
        }else{
            int artId = SecondFragmentArgs.fromBundle(getArguments()).getArtIdSecond();
            binding.saveButton.setVisibility(View.INVISIBLE);

            compositeDisposable.add(artDao.getArtBYId(artId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(SecondFragment.this::handleResponseOldArt));

        }


    }


    private void handleResponseOldArt(Art art){
        selectedArt = art;
        binding.artNameText.setText(art.name);
        binding.artistNameText.setText(art.artistname);
        binding.yearText.setText(art.year);

        Bitmap bitmap = BitmapFactory.decodeByteArray(art.image,0,art.image.length);
        binding.selectImage.setImageBitmap(bitmap);

    }


    public void save(View view){
        String name = binding.artNameText.getText().toString();
        String artistName= binding.artistNameText.getText().toString();
        String year = binding.yearText.getText().toString();
        Bitmap smallerImage = smallImage(selectedImage,300);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        smallerImage.compress(Bitmap.CompressFormat.PNG,50,outputStream);
        byte [] byteArray =outputStream.toByteArray();




        Art art = new Art(name,artistName,year,byteArray);

           compositeDisposable.add(artDao.insert(art)
                 .subscribeOn(Schedulers.io())
                 .observeOn(AndroidSchedulers.mainThread())
                 .subscribe(SecondFragment.this::handleResponse));

    }

    public void onClickImage(View view){
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(),Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Snackbar.make(view,"permission needed for galley", Snackbar.LENGTH_INDEFINITE).setAction("Give Permission", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
                    }
                }).show();

            }else{

                permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
            }

        }else{
            Intent intentGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intentActivityResultLauncher.launch(intentGallery);

        }


    }

    public Bitmap smallImage(Bitmap image,int maximumSize){
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;

        if (bitmapRatio > 1) {
            width = maximumSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maximumSize;
            width = (int) (height * bitmapRatio);
        }

        return Bitmap.createScaledBitmap(image,width,height,true);

    }

    public void startLauncher(){
        intentActivityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == Activity.RESULT_OK){
                    Intent intentFromResult = result.getData();
                    if (intentFromResult!=null){
                        Uri imageData = intentFromResult.getData();

                        try {
                            if (Build.VERSION.SDK_INT >= 28){
                                ImageDecoder.Source source = ImageDecoder.createSource(requireActivity().getContentResolver(),imageData);
                                selectedImage = ImageDecoder.decodeBitmap(source);
                                binding.selectImage.setImageBitmap(selectedImage);

                            }

                        }catch(Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

            }
        });

        permissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
            @Override
            public void onActivityResult(Boolean result) {
                if (result){
                    Intent intentGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intentActivityResultLauncher.launch(intentGallery);
                }else{
                    Toast.makeText(requireActivity(),"permission needed",Toast.LENGTH_LONG).show();
                }

            }
        });

    }
    private void handleResponse(){

        NavDirections action = SecondFragmentDirections.actionSecondFragmentToFirstFragment();
        Navigation.findNavController(requireView()).navigate(action);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
        binding=null;
    }

}