package com.kencorp.travelbiz;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class InsertActivity extends AppCompatActivity {

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;

    EditText txtTitle;
    EditText txtPrice;
    EditText txtDescription;

    TravelDeals travelDeals;

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

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                saveDeal();
                Snackbar.make(view, "Deal saved", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                clean();

            }
        });
        if(FirebaseUtil.isAdmin)
        {
         //   fab.setVisi;

            fab.show();
        }
        else{
           // fab.setVisibility(1);
           fab.hide();
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void clean() {

        txtTitle.setText(" ");
        txtDescription.setText(" ");
        txtPrice.setText(" ");

        txtTitle.requestFocus();
    }

    private void saveDeal() {

        String title = txtTitle.getText().toString();

        String description = txtDescription.getText().toString();

        String price = txtPrice.getText().toString();

        TravelDeals travelDeals = new TravelDeals(title,price,description,"");

        mDatabaseReference.push().setValue(travelDeals);


    }


    private void deleteDeal()
    {

        if(travelDeals == null)
        {
            Toast.makeText(this,"Please save the deal before deleting", Toast.LENGTH_LONG).show();
            return;
        }

        mDatabaseReference.child(travelDeals.getId()).removeValue();
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

            Toast.makeText(this,"Deal Saved",Toast.LENGTH_LONG).show();

            clean();
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
}
