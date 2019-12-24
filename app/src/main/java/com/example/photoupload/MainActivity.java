package com.example.photoupload;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {


    private FirebaseStorage mStorageRef;
    private StorageReference mPictures;
    private static CircleImageView mProfilePicture;
    private Button btnChange;
    private final int SELECTING_IMAGE = 100;
    private String pictureName;
    private int random;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mStorageRef = FirebaseStorage.getInstance();
        mPictures = mStorageRef.getReference().child("users_pic");

        mProfilePicture = (CircleImageView)findViewById(R.id.profile_picture);
        btnChange = (Button)findViewById(R.id.btn_change);


        btnChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECTING_IMAGE);
            }
        });

        pictureName = "temp";
        mPictures.child(pictureName).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(MainActivity.this).load(uri).into(mProfilePicture);
            }
        });

        Toast.makeText(MainActivity.this, "Please Wait till Profile Photo is Being Loaded", Toast.LENGTH_LONG).show();


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Toast.makeText(MainActivity.this, "i'm here" + requestCode + resultCode+data, Toast.LENGTH_SHORT).show();
        mStorageRef = FirebaseStorage.getInstance();
        mPictures = mStorageRef.getReference().child("users_pic");

        if (requestCode == SELECTING_IMAGE && resultCode == RESULT_OK) {
            Log.d("Image Retrive", "Selected Image");
            //Toast.makeText(getActivity(), " Succesfully Selected Image", Toast.LENGTH_SHORT).show();
            Uri uri = data.getData();

            random = new Random().nextInt(1000) + 1;
            pictureName = "temp";

            StorageReference profilePic = mPictures.child(pictureName);
            profilePic.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Log.d("Image Retrive", "Uploading Image");
                    Toast.makeText(MainActivity.this, "Uploaded Image", Toast.LENGTH_SHORT).show();

                    Task<Uri> downloadPic = taskSnapshot.getMetadata().getReference().getDownloadUrl();

                    Log.d("Image Retrive", "Uri is " + downloadPic);
//                    Glide.with(Fr).load(downloadPic).into(mProfilePicture);
                    Glide.with(MainActivity.this).load(downloadPic).into(mProfilePicture);
                    //Toast.makeText(getActivity(), "Sucessfully set Picture", Toast.LENGTH_SHORT).show();
//                    Log.d("Image Retrive", "Set Image Sucessfull");
                    Toast.makeText(MainActivity.this, "Photo uploaded sucessfully", Toast.LENGTH_LONG).show();

                }

            });
        } else{
            Toast.makeText(MainActivity.this, "Error Selecting Image", Toast.LENGTH_SHORT).show();
            Log.d("Image Retrive", "Error in Uploading Image");
        }
    }


}
