package com.aaish.tushare;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class verify extends AppCompatActivity implements View.OnClickListener {
    ImageView back;
    Button reset;
    EditText emailedt;
    FirebaseAuth currentauth;
    View verify_dots;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify);
        Log.e("Activity: ","verify/reset");
        Bundle b1 = getIntent().getExtras();
        int where = b1.getInt("where");
        verify_dots=(View)findViewById(R.id.verify_dots);
        back = (ImageView) findViewById(R.id.imageView5);
        reset = (Button) findViewById(R.id.button8);
        emailedt = (EditText) findViewById(R.id.editText4);
        verify_dots.setVisibility(View.INVISIBLE);
        reset.setOnClickListener(this);
        currentauth = FirebaseAuth.getInstance();
        if (where == 1) {
            back.setImageResource(R.mipmap.verifybg);
            reset.setText("Verify");
        } else if (where == 2) {
            back.setImageResource(R.mipmap.reset1);
            reset.setText("Reset my password");
        }

    }

    @Override
    public void onClick(View v) {
        verify_dots.setVisibility(View.VISIBLE);
        String emails;
        emails = emailedt.getText().toString();
        if (emails.equals("")) {
            Toast.makeText(this, "Email not entered", Toast.LENGTH_SHORT).show();
            verify_dots.setVisibility(View.INVISIBLE);
        } else {
            currentauth.sendPasswordResetEmail(emails).addOnCompleteListener(this, new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (!task.isSuccessful()) {
                        Toast.makeText(verify.this, "User cant be verified: Enter your Registered Email ID", Toast.LENGTH_SHORT).show();
                        verify_dots.setVisibility(View.INVISIBLE);
                    } else {
                        AlertDialog.Builder sent = new AlertDialog.Builder(verify.this);
                        sent.setTitle("Email Sent");
                        sent.setIcon(R.mipmap.emailsent);
                        sent.setMessage("An Email has been sent to your registered Email ID from where you can set your password." +
                                " Kindly check your Email");
                        sent.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent startsign = new Intent(verify.this, sign_in.class);
                                startActivity(startsign);
                            }
                        });
                        sent.show();
                        verify_dots.setVisibility(View.INVISIBLE);
                    }
                }
            });
        }
    }
}
