package com.kencorp.travelbiz;

import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class DealActivity extends AppCompatActivity {

    private static final int PICTURE_RESULT = 42;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;

    EditText txtTitle;
    EditText txtPrice;
    EditText txtDescription;

    TravelDeals travelDeals;
    private Button btnImage;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mFirebaseDatabase = FirebaseDatabase.getInstance();

        mDatabaseReference = mFirebaseDatabase.getReference().child("traveldeals");

        txtTitle = findViewById(R.id.txtTitle);
        txtPrice = findViewById(R.id.txtPrice);
        txtDescription = findViewById(R.id.txtDescription);

        imageView = findViewById(R.id.image);

        btnImage = findViewById(R.id.btnImage);

        btnImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent= new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/jpeg");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY,true);
                startActivityForResult(intent.createChooser(intent,"Insert Picture"), PICTURE_RESULT);
            }
        });


        if(FirebaseUtil.isAdmin)
        {
            btnImage.setVisibility(View.VISIBLE);


        }else{

            btnImage.setVisibility(View.INVISIBLE);

        }

        Intent intent = getIntent();

        TravelDeals travelDeals = intent.getParcelableExtra("Deal");

        if(travelDeals == null)
        {
            travelDeals = new TravelDeals();
        }

        this.travelDeals = travelDeals ;

        txtTitle.setText(travelDeals.getTitle());
        txtDescription.setText(travelDeals.getDescription());
        txtPrice.setText(travelDeals.getPrice());

        showImage(travelDeals.getImageUrl());

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void clean() {

        txtTitle.setText(" ");
        txtDescription.setText(" ");
        txtPrice.setText(" ");

        txtTitle.requestFocus();
    }

    private void backToList()
    {
        startActivity(new Intent(this,MainActivity.class));
    }

    private void editDeal()
    {

        travelDeals.setDescription(txtDescription.getText().toString());
        travelDeals.setPrice(txtPrice.getText().toString());
        travelDeals.setTitle(txtTitle.getText().toString());

        if(travelDeals.getId() == null)
        {
            mDatabaseReference.push().setValue(travelDeals);
        }else{
            mDatabaseReference.child(travelDeals.getId()).setValue(travelDeals);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.save_menu,menu);

        if(FirebaseUtil.isAdmin)
        {
            menu.findItem(R.id.delete_menu).setVisible(true);
            menu.findItem(R.id.edit_menu).setVisible(true);
            editTextEnabled(true);
        }else{

            menu.findItem(R.id.delete_menu).setVisible(false);
            menu.findItem(R.id.edit_menu).setVisible(false);
            editTextEnabled(false);
        }

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if(id == R.id.edit_menu)
        {
            editDeal();

            Toast.makeText(this,"Deal edit",Toast.LENGTH_LONG).show();

            clean();
            backToList();
            return true;
        }else if(id == R.id.delete_menu)
        {

            deleteDeal();
            Toast.makeText(this,"Deal remove",Toast.LENGTH_LONG).show();
            backToList();
        }

        return super.onOptionsItemSelected(item);
    }

    private void editTextEnabled(boolean isEnabled)
    {
        txtTitle.setEnabled(isEnabled);
        txtPrice.setEnabled(isEnabled);
        txtDescription.setEnabled(isEnabled);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICTURE_RESULT && resultCode == RESULT_OK)
        {

            try{
                Uri imageUri = null;
                if (data != null) {
                    imageUri = data.getData();
                }


                final StorageReference ref = FirebaseUtil.mStorageRef.child(imageUri.getLastPathSegment());

                UploadTask task = ref.putFile(imageUri);


                Task<Uri> urlTask = task.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }

                        // Continue with the task to get the download URL
                        return ref.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            Uri downloadUri = task.getResult();

                            String url = downloadUri.toString();

                            String pictureName = downloadUri.getPath();

                            Toast.makeText(getApplicationContext(),url,Toast.LENGTH_LONG).show();
                            Log.d("onActivityResul","Url "+ url +" pictureName "+ pictureName);
                            travelDeals.setImageUrl(url);
                            travelDeals.setImageName(pictureName);

                            showImage(url);

                        } else {
                            // Handle failures
                            // ...
                        }
                    }
                });


                Log.d("onActivityResul",data.toString()+" ---++-- "+ imageUri.toString());

            }
            catch (Exception e)
            {
                Log.d("onActivityResul", e.getMessage());
            }


        }

    }

    private void showImage(String url)
    {
        if(url != null && url.isEmpty() == false){

            // get a screen width
            int width = Resources.getSystem().getDisplayMetrics().widthPixels;

            Picasso.with(this)
                    .load(url)
                    .resize(width,width*2/3)
                    .centerCrop()
                    .into(imageView) ;


            // get a screen height

            int height = Resources.getSystem().getDisplayMetrics().heightPixels;
        }
    }


    private void deleteDeal(){
        if(travelDeals == null){
            Toast.makeText(getApplicationContext(),"Please save the deal before deleting",
                    Toast.LENGTH_LONG).show();
            return;
        }

        mDatabaseReference.child(travelDeals.getId()).removeValue();

        if(travelDeals.getImageName() != null && !travelDeals.getImageName().isEmpty())
        {
           StorageReference picRef = FirebaseUtil.mStorageRef.child(travelDeals.getImageName());

           picRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
               @Override
               public void onSuccess(Void aVoid) {

                 Log.d("Delete Image","Image successful delete");
               }
           }).addOnFailureListener(new OnFailureListener() {
               @Override
               public void onFailure(@NonNull Exception e) {


                   Log.d("Delete Image",e.getMessage());

               }
           });
        }

    }
}
