package com.aaish.tushare;

import android.*;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity implements MediaPlayer.OnCompletionListener, FirebaseAuth.AuthStateListener {
    VideoView v1;
    boolean res = false;
    FirebaseAuth myauth;
    ImageView internet;
    SharedPreferences tusharepref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {

            if ((ActivityCompat.checkSelfPermission(this,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)

                    || (ActivityCompat.checkSelfPermission(this,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)

                    || (ActivityCompat.checkSelfPermission(this,
                    android.Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED)

                    || (ActivityCompat.checkSelfPermission(this,
                    android.Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED)
                    ) {

                String[] perms = new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        android.Manifest.permission.INTERNET, android.Manifest.permission.ACCESS_NETWORK_STATE
                };
                ActivityCompat.requestPermissions(this, perms, 10);
                return;
            } else {
                maininitialisation();
            }
        } else {
            maininitialisation();
        }
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        Intent toLogin = new Intent(this, log_screen.class);
        startActivity(toLogin);
        finish();
    }

    public void maininitialisation() {
        tusharepref=getSharedPreferences("authstate",MODE_PRIVATE);
        SharedPreferences.Editor tusharepref_ed= tusharepref.edit();
        tusharepref_ed.putBoolean("logscreen",false);
        tusharepref_ed.commit();
        internet = (ImageView) findViewById(R.id.imageView8);
        myauth = FirebaseAuth.getInstance();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (!prefs.getBoolean("first", false)) {
            // run your one time code
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("first", true);
            editor.commit();
            //SharedPreferences sp=PreferenceManager.getDefaultSharedPreferences(this);
            //SharedPreferences.Editor ed1=sp.edit();
            //ed1.putInt("register",0);
            //ed1.commit();
            v1 = (VideoView) findViewById(R.id.videoView);
            Uri uri_vid = Uri.parse("android.resource://com.aaish.tushare/" + R.raw.animate);
            v1.setVideoURI(uri_vid);
            v1.setOnCompletionListener(MainActivity.this);
            v1.start();
        } else {
            if (!checkInternetConnection()) {
                internet.setImageResource(R.mipmap.nointernet);
                internet.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!checkInternetConnection()) {

                        } else {
                            myauth.addAuthStateListener(MainActivity.this);
                        }
                    }
                });
            } else {
                myauth.addAuthStateListener(this);

            }
        }
    }

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        SharedPreferences tu_pref = getSharedPreferences("authstate", MODE_PRIVATE);
        Boolean log_scr = tu_pref.getBoolean("logscreen", false);
        if (!log_scr) {
            FirebaseUser myuser = firebaseAuth.getCurrentUser();
            if (myuser == null) {
                Intent in = new Intent(this, log_screen.class);
                startActivity(in);
                finish();
            } else {
                FirebaseDatabase userRef = FirebaseDatabase.getInstance();
                String uid = myuser.getUid().toString();
                DatabaseReference userDb = userRef.getReference("users").child(uid).child("check_details");
                userDb.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        boolean val = dataSnapshot.getValue(boolean.class);
                        if (val == true) {
                            //Toast.makeText(MainActivity.this, "True", Toast.LENGTH_SHORT).show();
                            Intent home = new Intent(MainActivity.this, Home.class);
                            startActivity(home);
                            finish();
                        } else {
                            Intent in_details = new Intent(MainActivity.this, Details.class);
                            startActivity(in_details);
                            finish();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(MainActivity.this, "error", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }

    public boolean checkInternetConnection() {

        ConnectivityManager connectivity = (ConnectivityManager) getApplicationContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivity != null) {
            NetworkInfo[] inf = connectivity.getAllNetworkInfo();
            if (inf != null)
                for (int i = 0; i < inf.length; i++)
                    if (inf[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }

        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        res = false;

        for (int i = 0; i < grantResults.length; i++) {

            Log.e("firetest", String.valueOf(grantResults[i]));

            if (grantResults[i] != 0) {
                break;
            } else {
                res = true;
            }


        }

        if (res) {
            maininitialisation();

        } else {
            Toast.makeText(this, "App will not work without permissions", Toast.LENGTH_LONG).show();
            finish();
        }

    }
}
