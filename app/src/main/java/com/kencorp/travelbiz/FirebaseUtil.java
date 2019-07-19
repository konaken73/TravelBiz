package com.kencorp.travelbiz;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FirebaseUtil {

    private static final int RC_SIGN_IN = 1;
    public static FirebaseDatabase mFirebaseDatabase;

    public static DatabaseReference mDatabaseReference;

    public static FirebaseAuth mFirebaseAuth;

    public  static FirebaseAuth.AuthStateListener authStateListener;
    private static FirebaseUtil firebaseUtil;

    public static ArrayList<TravelDeals> dealsArrayList;

    public static MainActivity called;

    public static boolean isAdmin;

    private FirebaseUtil(){} ;

    public static void openFirebaseReference(String ref , MainActivity calledActivity) {

        if(firebaseUtil == null) {
            firebaseUtil = new FirebaseUtil();

            mFirebaseDatabase = FirebaseDatabase.getInstance();

            mFirebaseAuth = FirebaseAuth.getInstance();

            called = calledActivity;

            authStateListener = new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                    if(firebaseAuth.getCurrentUser() == null)
                    {
                        FirebaseUtil.signin();
                    }
                    else{
                        String userId = firebaseAuth.getUid();
                        
                        checkAdmin(userId);
                    }
                    Toast.makeText(called.getApplicationContext(),"Welcome back",Toast.LENGTH_LONG).show();

                }
            };

            dealsArrayList = new ArrayList<TravelDeals>();

        }
        mDatabaseReference = mFirebaseDatabase.getReference(ref);

    }

    private static void checkAdmin(String userId) {

        FirebaseUtil.isAdmin = false;
        DatabaseReference ref = mFirebaseDatabase.getReference().child("administrators")
                .child(userId);

        ChildEventListener listener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                FirebaseUtil.isAdmin = true;
                Log.d("checkAdmin","You are an Administrator");

                called.showMenu();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        ref.addChildEventListener(listener);

    }

    public static void attachListener()
    {
        mFirebaseAuth.addAuthStateListener(authStateListener);

    }

    public static void detachListener()
    {
        mFirebaseAuth.removeAuthStateListener(authStateListener);
    }

    private static void signin()
    {

        // Choose authentication providers
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
               // new AuthUI.IdpConfig.PhoneBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build()
              //  new AuthUI.IdpConfig.FacebookBuilder().build(),
             //   new AuthUI.IdpConfig.TwitterBuilder().build()
        );

// Create and launch sign-in intent
        called.startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build(),
                RC_SIGN_IN);

    }

    public static void signOut()
    {
        AuthUI.getInstance()
                .signOut(called.getApplicationContext())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        attachListener();
                        Log.d("signOut","User logged out");
                    }
                });
    }
}
