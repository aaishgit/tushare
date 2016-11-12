package com.aaish.tushare;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class register extends AppCompatActivity implements View.OnClickListener {
    ImageView reg;
    Button signupbtn;
    EditText email;
    private FirebaseAuth currentauth;
    View registerdot;
    private static final String ALPHA_NUMERIC_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Log.e("Activity: ", "register");
        reg = (ImageView) findViewById(R.id.imageView4);
        signupbtn = (Button) findViewById(R.id.button7);
        registerdot = (View) findViewById(R.id.register_dot);
        reg.setImageResource(R.mipmap.reglogo1);
        signupbtn.setOnClickListener(this);
        email = (EditText) findViewById(R.id.editText3);
        currentauth = FirebaseAuth.getInstance();
        registerdot.setVisibility(View.INVISIBLE);
    }

    private String generatePassword() {

        int count = 6;
        StringBuilder builder = new StringBuilder();
        while (count-- != 0) {
            int character = (int) (Math.random() * ALPHA_NUMERIC_STRING.length());
            builder.append(ALPHA_NUMERIC_STRING.charAt(character));
        }
        Log.i("signup", builder.toString());
        return builder.toString();
    }

    @Override
    public void onClick(View v) {
        registerdot.setVisibility(View.VISIBLE);
        String emails, pass;
        emails = email.getText().toString();
        if (emails.equals("")) {
            Toast.makeText(this, "Email not entered", Toast.LENGTH_SHORT).show();
            registerdot.setVisibility(View.INVISIBLE);
        } else {
            pass = generatePassword();
            currentauth.createUserWithEmailAndPassword(emails, pass).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (!task.isSuccessful()) {
                        Toast.makeText(register.this, "User cannot be created", Toast.LENGTH_SHORT).show();
                        registerdot.setVisibility(View.INVISIBLE);
                    } else {
                        FirebaseUser myuser = currentauth.getCurrentUser();
                        String uid = myuser.getUid().toString();
                        FirebaseDatabase userRef = FirebaseDatabase.getInstance();
                        DatabaseReference userDb = userRef.getReference("users").child(uid).child("check_details");
                        userDb.setValue(false).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (!task.isSuccessful()) {
                                    Toast.makeText(register.this, "Reference cant be created", Toast.LENGTH_SHORT).show();
                                    registerdot.setVisibility(View.INVISIBLE);
                                } else {
                                    Toast.makeText(register.this, "User registered", Toast.LENGTH_SHORT).show();
                                    Intent verifyclass = new Intent(register.this, verify.class);
                                    verifyclass.putExtra("where", 1);
                                    currentauth.signOut();
                                    new mytask().execute();
                                    registerdot.setVisibility(View.INVISIBLE);
                                    startActivity(verifyclass);
                                }
                            }
                        });
                    }
                }
            });
        }
    }

    private class mytask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


            return null;
        }
    }
}