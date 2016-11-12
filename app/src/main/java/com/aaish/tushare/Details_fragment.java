package com.aaish.tushare;

/**
 * Created by aaishsindwani on 13/09/16.
 */

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Details_fragment extends Fragment implements View.OnClickListener {
    ImageView img;
    EditText name;
    String uid;
    static String year_str, dept_str, type_str, name_str;
    static String[] opt2;
    static int alertid;
    static TextView type, dept, year;
    Button saveit;
    FirebaseAuth currauth;
    FirebaseUser curruser;
    FirebaseDatabase userDb;
    DatabaseReference useRef;
    static Fielddetails alldetails;

    @Nullable
    @Override

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View detailsview = inflater.inflate(R.layout.details_fragment, container, false);
        img = (ImageView) detailsview.findViewById(R.id.frag_image);
        img.setImageResource(R.mipmap.details);
        name = (EditText) detailsview.findViewById(R.id.frag_name);
        type = (TextView) detailsview.findViewById(R.id.frag_type);
        dept = (TextView) detailsview.findViewById(R.id.frag_dept);
        year = (TextView) detailsview.findViewById(R.id.frag_year);
        saveit = (Button) detailsview.findViewById(R.id.save);
        currauth = FirebaseAuth.getInstance();
        curruser = currauth.getCurrentUser();
        uid = curruser.getUid();
        name.setCursorVisible(false);
        userDb = FirebaseDatabase.getInstance();
        alldetails = new Fielddetails();
        useRef = userDb.getReference("users").child(uid).child("details");
        dept.setOnClickListener(this);
        year.setOnClickListener(this);
        type.setOnClickListener(this);
        saveit.setOnClickListener(this);
        final Context context = this.getActivity().getApplicationContext();
        name.setOnClickListener(this);
        useRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                alldetails = dataSnapshot.getValue(Fielddetails.class);
                name.setText(alldetails.getName());
                type.setText(alldetails.getType());
                dept.setText(alldetails.getDept());
                if (alldetails.getType().equals("teacher")) {
                    year.setVisibility(View.INVISIBLE);
                } else {
                    year.setVisibility(View.VISIBLE);
                    year.setText(alldetails.getYear());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show();
            }
        });
        return detailsview;
    }

    @Override
    public void onClick(View view) {
        final Context con = this.getActivity().getApplicationContext();
        name.setCursorVisible(false);
        switch (view.getId()) {
            case R.id.save:
                name_str = name.getText().toString();
                if (TextUtils.isEmpty(name_str)) {
                    Toast.makeText(con, "Enter Name", Toast.LENGTH_SHORT).show();
                } else {
                    alldetails.setName(name_str);
                    useRef.setValue(alldetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (!task.isSuccessful()) {
                                Toast.makeText(con, "Error", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(con, "Details updated", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                break;
            case R.id.frag_type:
                alertid = 1;
                showDialog();
                break;
            case R.id.frag_dept:
                showDialog();
                alertid = 2;
                break;
            case R.id.frag_year:
                alertid = 3;
                showDialog();
                break;
            case R.id.frag_name:
                name.setCursorVisible(true);
                break;
        }

    }

    void showDialog() {
        DialogFragment newFragment = MyAlertDialogFragment.newInstance(1);
        newFragment.show(getFragmentManager(), "dialog");
    }

    public static class MyAlertDialogFragment extends DialogFragment {

        public static MyAlertDialogFragment newInstance(int title) {
            MyAlertDialogFragment frag = new MyAlertDialogFragment();
            Bundle args = new Bundle();
            args.putInt("title", title);
            frag.setArguments(args);
            return frag;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            int title = getArguments().getInt("title");
            if (alertid == 1) {
                opt2 = new String[]{"Student", "Teacher"};
                return new AlertDialog.Builder(getActivity())
                        .setIcon(R.mipmap.program)
                        .setTitle("Select Type")
                        .setItems(opt2, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                type_str = opt2[i];
                                type.setText(opt2[i]);
                                if (type_str == opt2[0]) {
                                    year.setVisibility(View.VISIBLE);
                                } else {
                                    year.setVisibility(View.INVISIBLE);
                                    year_str = "0";
                                }
                                alldetails.setType(type_str);
                                alldetails.setYear(year_str);
                            }
                        }).create();
            } else if (alertid == 2) {
                opt2 = new String[]{"Computer Science", "Chemical", "Civil", "Biotechnology", "Electronics and Comm.",
                        "Electrical and Instrumentation", "Mechanical"};
                return new AlertDialog.Builder(getActivity())
                        .setIcon(R.mipmap.branch)
                        .setTitle("Select Department")
                        .setItems(opt2, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dept_str = opt2[i];
                                dept.setText(opt2[i]);
                                alldetails.setDept(dept_str);
                            }
                        }).create();
            } else {
                opt2 = new String[]{"1", "2", "3", "4"};
                return new AlertDialog.Builder(getActivity())
                        .setIcon(R.mipmap.program)
                        .setTitle("Select Year")
                        .setItems(opt2, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                year.setText(opt2[i]);
                                year_str = opt2[i];
                                alldetails.setYear(year_str);
                            }
                        }).create();
            }


        }
    }
}

