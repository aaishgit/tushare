package com.aaish.tushare;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.OpenableColumns;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.nononsenseapps.filepicker.FilePickerActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by aaishsindwani on 13/09/16.
 */
public class Upload extends Fragment implements View.OnClickListener {
    ImageView img1;
    int x;
    Uploaddbreference uploaddbreference;
    Fielddetails fielddetails;
    EditText docname, subname;
    static UploadTask uploadTask;
    static TextView type;
    Button upload_btn;
    static int dial;
    int prog, m, k;
    static int percent;
    static String percent_str;
    //AlertDialog.Builder ad2;
    static String[] opt1;
    static String type_str;
    String doc_str;
    String s, userid;
    static String filename;
    String sub_str, filedotless;
    Uri uri;
    ImageButton img_button;
    FirebaseStorage firebaseStorage;
    StorageReference fileRef, fileUpref;
    FirebaseDatabase firebaseDatabase;

    DatabaseReference databaseReference;
    DatabaseReference userDb;
    FirebaseUser currUser;
    FirebaseAuth currAuth;

    //AlertDialog.Builder type2;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View uploadView = inflater.inflate(R.layout.upload, container, false);
        img1 = (ImageView) uploadView.findViewById(R.id.imageView7);
        docname = (EditText) uploadView.findViewById(R.id.editText9);
        subname = (EditText) uploadView.findViewById(R.id.editText10);
        type = (TextView) uploadView.findViewById(R.id.textView4);
        upload_btn = (Button) uploadView.findViewById(R.id.button10);
        img_button = (ImageButton) uploadView.findViewById(R.id.imageButton);
        img_button.setOnClickListener(this);
        upload_btn.setOnClickListener(this);
        type.setOnClickListener(this);
        firebaseStorage = FirebaseStorage.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        currAuth = FirebaseAuth.getInstance();
        currUser = currAuth.getCurrentUser();
        fileRef = firebaseStorage.getReferenceFromUrl("gs://application-ca620.appspot.com/").child(currUser.getUid());
        userid = currUser.getUid();
        img1.setImageResource(R.mipmap.upload_frag);
        opt1 = new String[]{"Slides or Course Material", "Class Notes", "Tutorials", "Books"};
        fielddetails = new Fielddetails();
        uploaddbreference = new Uploaddbreference();
        return uploadView;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.textView4:
                /*Context context=this.getActivity().getApplicationContext();
                ad2=new AlertDialog.Builder(context);
                ad2.setTitle("Select Type");
                ad2.setIcon(R.mipmap.document_type);
                opt1= new String[]{"Slides","Class Notes","Tutorials","Books"};
                ad2.setItems(opt1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        type_str=opt1[i];
                        type.setText(type_str);
                    }
                });
                ad2.show();*/
                dial = 1;
                showDialog();
                break;
            case R.id.button10:
                final Context context = this.getActivity().getApplicationContext();
                doc_str = docname.getText().toString();
                sub_str = subname.getText().toString();
                if (TextUtils.isEmpty(s)) {
                    Toast.makeText(this.getActivity().getApplicationContext(), "Pick file first", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(doc_str) || TextUtils.isEmpty(type_str) || TextUtils.isEmpty(sub_str)) {
                    Toast.makeText(this.getActivity().getApplicationContext(), "Enter all details", Toast.LENGTH_SHORT).show();
                } else {
                    /*Snackbar snackbar=Snackbar.make(getView(),"Warning!Click only on Dialog box and not Elsewhere",Snackbar.LENGTH_LONG);
                    View sn = snackbar.getView();
                    TextView sntxt = (TextView) sn.findViewById(android.support.design.R.id.snackbar_text);
                    sntxt.setTextColor(Color.YELLOW);
                    snackbar.show();*/
                    filename = doc_str;
                    filedotless = removedot(filename);
                    //Toast.makeText(context, "Dotless file is " + filedotless, Toast.LENGTH_SHORT).show();
                    fileUpref = fileRef.child(filename);
                    uploadTask = fileUpref.putFile(uri);
                    dial = 2;
                    //AlertDialog.Builder type2;
                    showDialog();
                    x = 10;
                    uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            prog = (int) (100.0 * ((taskSnapshot.getBytesTransferred()) / (taskSnapshot.getTotalByteCount())));
                            m = (int) ((taskSnapshot.getBytesTransferred()) * 100);
                            k = (int) taskSnapshot.getTotalByteCount();
                            percent = m / k;
                            percent_str = String.valueOf(percent);
                            if (percent > x || percent == 100) {
                                x = x + 10;
                                showDialog();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(context, "Cant upload", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(context, "File uploaded", Toast.LENGTH_SHORT).show();
                            databaseReference = firebaseDatabase.getReference("documents").child(userid + "-" + filedotless);
                            userDb = firebaseDatabase.getReference("users").child(currUser.getUid());
                            DatabaseReference detailRef = userDb.child("details");
                            detailRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    fielddetails = dataSnapshot.getValue(Fielddetails.class);
                                    uploaddbreference.setUsername(fielddetails.getName());
                                    uploaddbreference.setLink(currUser.getUid() + "/" + filename);
                                    uploaddbreference.setFilename(filename);
                                    uploaddbreference.setSubject(sub_str);
                                    uploaddbreference.setType(type_str);
                                    uploaddbreference.setUserId(currUser.getUid());
                                    databaseReference.setValue(uploaddbreference);
                                    DatabaseReference userDocs = userDb.child("documents").child(userid + "/" + filedotless);
                                    userDocs.setValue(uploaddbreference).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (!task.isSuccessful()) {
                                                Toast.makeText(context, "Database cant be updated", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(context, "Upload Completed and database updated", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    Toast.makeText(context, "error in database access", Toast.LENGTH_SHORT).show();
                                }
                            });

                        }
                    });
                }
                break;
            case R.id.imageButton:
                selectFile();

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
            frag.setCancelable(false);
            Bundle args = new Bundle();
            args.putInt("title", title);
            frag.setArguments(args);
            return frag;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            int title = getArguments().getInt("title");

            final Context con = this.getActivity().getApplicationContext();
            if (dial == 1) {
                return new AlertDialog.Builder(getActivity())
                        .setIcon(R.mipmap.document_type)
                        .setTitle("Select Type")
                        .setItems(opt1, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                type_str = opt1[i];
                                type.setText(type_str);
                            }
                        }).create();
            } else {
                if (percent_str.equals("100")) {
                    return new AlertDialog.Builder(getActivity()).setIcon(R.mipmap.uploading)
                            .setTitle("File Uploaded").setMessage("Uploaded " + percent_str + "%").
                                    setCancelable(false).setNegativeButton("Close", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    //uploadTask.cancel();
                                    Toast.makeText(con, "Uploading Completed", Toast.LENGTH_SHORT).show();
                                    dialogInterface.dismiss();
                                    dismissAllDialogs(getFragmentManager());
                                }
                            })/*.setPositiveButton("Close", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                                dismissAllDialogs(getFragmentManager());
                            }
                        })*/.create();
                } else {
                    return new AlertDialog.Builder(getActivity()).setIcon(R.mipmap.uploading)
                            .setTitle("Uploading " + filename).setMessage("Uploaded " + percent_str + "%").
                                    setCancelable(false).setNegativeButton("Close and Cancel Upload", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    uploadTask.cancel();
                                    Toast.makeText(con, "Uploading Cancelled", Toast.LENGTH_SHORT).show();
                                    dialogInterface.dismiss();
                                    dismissAllDialogs(getFragmentManager());
                                }
                            })/*.setPositiveButton("Close", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                                dismissAllDialogs(getFragmentManager());
                            }
                        })*/.create();
                }
            }
        }
    }

    public void selectFile() {
        /*Intent fileintent = new Intent(Intent.ACTION_GET_CONTENT);
        fileintent.setType("gagt/sdf");
        try {
            startActivityForResult(fileintent, 11);
        } catch (ActivityNotFoundException e) {
            Log.e("tag", "No activity can handle picking a file. Showing alternatives.");
        }*/
        // This always works
        Intent i = new Intent(this.getActivity().getApplicationContext(), FilePickerActivity.class);
        // This works if you defined the intent filter
        // Intent i = new Intent(Intent.ACTION_GET_CONTENT);

        // Set these depending on your use case. These are the defaults.
        i.putExtra(FilePickerActivity.EXTRA_ALLOW_MULTIPLE, false);
        i.putExtra(FilePickerActivity.EXTRA_ALLOW_CREATE_DIR, false);
        i.putExtra(FilePickerActivity.EXTRA_MODE, FilePickerActivity.MODE_FILE);

        // Configure initial directory by specifying a String.
        // You could specify a String like "/storage/emulated/0/", but that can
        // dangerous. Always use Android's API calls to get paths to the SD-card or
        // internal memory.
        i.putExtra(FilePickerActivity.EXTRA_START_PATH, Environment.getExternalStorageDirectory().getPath());

        startActivityForResult(i, 15);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 15 && resultCode == Activity.RESULT_OK) {
            if (data.getBooleanExtra(FilePickerActivity.EXTRA_ALLOW_MULTIPLE, false)) {
                // For JellyBean and above
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    ClipData clip = data.getClipData();

                    if (clip != null) {
                        for (int i = 0; i < clip.getItemCount(); i++) {
                            uri = clip.getItemAt(i).getUri();
                            s = uri.toString();
                            filename = getfilename(uri);
                            docname.setText(filename);
                            doc_str = filename;
                        }
                    }
                    // For Ice Cream Sandwich
                } else {
                    ArrayList<String> paths = data.getStringArrayListExtra
                            (FilePickerActivity.EXTRA_PATHS);

                    if (paths != null) {
                        for (String path : paths) {
                            uri = Uri.parse(path);
                            s = uri.toString();
                            filename = getfilename(uri);
                            docname.setText(filename);
                            doc_str = filename;
                            // Do something with the URI
                            //UploadIt();
                        }
                    }
                }

            } else {
                uri = data.getData();
                s = uri.toString();
                filename = getfilename(uri);
                docname.setText(filename);
                doc_str = filename;
                // Do something with the URI
                //UploadIt();
            }
        }
    }

    /*private void getfilename(){
        Pattern pattern = Pattern.compile("/(.*?).");
        Matcher matcher = pattern.matcher(s);
        if (matcher.find())
        {   doc_str=matcher.group(1);
            docname.setText(doc_str);
        }
        else {
            docname.setText(s);
        }
    }*/
    public String getfilename(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Context con = this.getActivity().getApplicationContext();
            Cursor cursor = con.getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    private String removedot(String p) {
        int spaceIndex = p.indexOf(".");
        if (spaceIndex != -1) {
            p = p.substring(0, spaceIndex);
        }
        return p;
    }

    public static int dismissAllDialogs(FragmentManager manager) {
        List<Fragment> fragments = manager.getFragments();

        if (fragments == null) {
            return 0;
        } else {
            for (Fragment fragment : fragments) {
                if (fragment instanceof DialogFragment) {
                    DialogFragment dialogFragment = (DialogFragment) fragment;
                    dialogFragment.dismissAllowingStateLoss();
                }
                FragmentManager childFragmentManager = fragment.getChildFragmentManager();
                if (childFragmentManager != null) {
                    dismissAllDialogs(childFragmentManager);
                } else {
                    return 0;
                }
            }
        }
        return 0;
    }
}


