package com.aaish.tushare;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class log_screen extends AppCompatActivity implements View.OnClickListener {
    ImageView img;
    Button yes, cancel;
    TextView verifytitle, verifydesc;
    Button ver, signup, signin;
    View newview;
    boolean status;
    CoordinatorLayout coordinator;
    Snackbar snack;
    SharedPreferences t_pref;
    AlertDialog.Builder ad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_screen);
        Log.e("Activity: ","log screen");
        t_pref=getSharedPreferences("authstate",MODE_PRIVATE);
        SharedPreferences.Editor tpref_ed= t_pref.edit();
        tpref_ed.putBoolean("logscreen",true);
        tpref_ed.commit();
        img = (ImageView) findViewById(R.id.imageView);
        img.setImageResource(R.mipmap.tushare_text);
        signin = (Button) findViewById(R.id.button);
        ver = (Button) findViewById(R.id.button2);
        signup = (Button) findViewById(R.id.button3);
        signin.setOnClickListener(this);
        signup.setOnClickListener(this);
        ver.setOnClickListener(this);
        coordinator = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
        status = false;
        status = checkInternetConnection();
        snack = Snackbar.make(coordinator, "No Internet Connectivity", Snackbar.LENGTH_INDEFINITE);
        View sn = snack.getView();
        TextView sntxt = (TextView) sn.findViewById(android.support.design.R.id.snackbar_text);
        sntxt.setTextColor(Color.YELLOW);
        if (!status) {
            snack.show();
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void onClick(View v) {
        status = checkInternetConnection();
        Log.e("click status", String.valueOf(status));
        if (!status) {
            snack.show();
        } else {
            snack.dismiss();
            switch (v.getId()) {
                case R.id.button:
                    Intent signin1 = new Intent(this, sign_in.class);
                    startActivity(signin1);
                    //finish();
                    break;
                case R.id.button2:
                    /*ad=new AlertDialog.Builder(this);
                    ad.setTitle("Registration Verification");
                    verifydesc=new TextView(this);
                    verifydesc.setText("Did your register your account?");
                    verifydesc.setTextSize((float) 16.0);
                    verifydesc.setTextColor(Color.BLACK);
                    verifydesc.setGravity(Gravity.CENTER_HORIZONTAL);
                    ad.setPositiveButton("Yes",yes);
                    ad.setNegativeButton("No, let me register first",cancel);
                    ad.setView(verifydesc);
                    ad.show();*/
                    showAlert();
                    break;
                case R.id.button3:
                    Intent regis = new Intent(this, register.class);
                    startActivity(regis);
                    //finish();
                    break;

            }
        }
    }

    /*DialogInterface.OnClickListener yes= new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {

        }
    };

    DialogInterface.OnClickListener cancel= new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {

        }
    };*/
    public void showAlert() {
        LayoutInflater inflater = log_screen.this.getLayoutInflater();
        View newview = inflater.inflate(R.layout.alertdialog, null);
        verifydesc = (TextView) newview.findViewById(R.id.textView2);
        cancel = (Button) newview.findViewById(R.id.button4);
        yes = (Button) newview.findViewById(R.id.button5);
        verifytitle = (TextView) newview.findViewById(R.id.textView);
        final AlertDialog ad = new AlertDialog.Builder(this).create();
        yes.setText("YES");
        cancel.setText("NO, let me register first");
        verifytitle.setText("Registration Confirmation");
        verifydesc.setText("Did you register your E-mail?");
        ad.setView(newview);
        ad.show();
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent veri = new Intent(log_screen.this, verify.class);
                veri.putExtra("where", 1);
                startActivity(veri);
                ad.dismiss();
                //finish();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ad.dismiss();
            }
        });
    }

    //public void showlogo(){
    //  getSupportActionBar().setDisplayShowHomeEnabled(true);
    // getSupportActionBar().setLogo(R.mipmap.ic_launcher);
    // getSupportActionBar().setDisplayUseLogoEnabled(true);
    //}

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
}