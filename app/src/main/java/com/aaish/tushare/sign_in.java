package com.aaish.tushare;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class sign_in extends AppCompatActivity implements View.OnClickListener {
    Button signin;
    EditText email, password;
    ImageView img;
    TextView forgot;
    FirebaseAuth myauth;
    View dotloader;
    String e, p;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        Log.e("Activity: ","sign_in");
        signin = (Button) findViewById(R.id.button6);
        email = (EditText) findViewById(R.id.editText);
        password = (EditText) findViewById(R.id.editText2);
        img = (ImageView) findViewById(R.id.imageView2);
        img.setImageResource(R.mipmap.signin);
        forgot = (TextView) findViewById(R.id.textView3);
        dotloader=(View)findViewById(R.id.text_dot_loader);
        dotloader.setVisibility(View.INVISIBLE);
        forgot.setOnClickListener(this);
        signin.setOnClickListener(this);
        myauth = FirebaseAuth.getInstance();
        SharedPreferences sp=getSharedPreferences("download_file", Context.MODE_PRIVATE);
        SharedPreferences.Editor edt= sp.edit();
        edt.putBoolean("retry",false);
        edt.commit();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button6:
                dotloader.setVisibility(View.VISIBLE);
                e = email.getText().toString();
                p = password.getText().toString();
                if (TextUtils.isEmpty(e) || TextUtils.isEmpty(p)) {
                    Toast.makeText(this, "Fill all details", Toast.LENGTH_SHORT).show();
                    dotloader.setVisibility(View.INVISIBLE);
                } else {
                    myauth.signInWithEmailAndPassword(e, p).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (!task.isSuccessful()) {
                                Toast.makeText(sign_in.this, "Cannot Login, Please check your credentials and network c" +
                                        "onnection", Toast.LENGTH_SHORT).show();
                                dotloader.setVisibility(View.INVISIBLE);
                            } else {
                                FirebaseUser myuser = myauth.getCurrentUser();
                                String uid = myuser.getUid().toString();
                                FirebaseDatabase userRef = FirebaseDatabase.getInstance();
                                DatabaseReference userDb = userRef.getReference("users").child(uid).child("check_details");
                                userDb.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        boolean val = dataSnapshot.getValue(boolean.class);
                                        if (val == true) {
                                            //Toast.makeText(sign_in.this, "True", Toast.LENGTH_SHORT).show();
                                            Intent home2 = new Intent(sign_in.this, Home.class);
                                            startActivity(home2);
                                            dotloader.setVisibility(View.INVISIBLE);
                                            finish();
                                        } else {
                                            Intent in_details = new Intent(sign_in.this, Details.class);
                                            startActivity(in_details);
                                            dotloader.setVisibility(View.INVISIBLE);
                                            finish();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                        Toast.makeText(sign_in.this, "error", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    });
                }
                break;
            case R.id.textView3:
                Intent forgo = new Intent(sign_in.this, verify.class);
                forgo.putExtra("where", 2);
                startActivity(forgo);
                break;
        }
    }
}
