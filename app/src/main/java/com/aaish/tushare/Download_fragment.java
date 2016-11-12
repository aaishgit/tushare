package com.aaish.tushare;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.aaish.tushare.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;

/**
 * Created by aaishsindwani on 29/09/16.
 */
public class Download_fragment extends Fragment {

    LinearLayout dowanloading_view;
    ImageButton close, redown;

    Context con;

    DbHelper dbHelper;
    Downloaded newdownloaded;
    File file1, folder;
    StorageReference downloadRef;
    //final Downloaded newdownloaded=new Downloaded();
    //static StorageTask<FileDownloadTask.TaskSnapshot> downloadTask;
    StorageTask<FileDownloadTask.TaskSnapshot> fileDownloadTask;
    FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
    StorageReference storageReference;

    int i, prog, percent, m, k;

    private ArrayList<Downloaded> docList = new ArrayList<Downloaded>();
    private Downloads_adapter doc_Adapter;
    private RecyclerView new_recycle;

    ImageView download_imagecard;
    TextView downloading, progress_text;
    ProgressBar progressBar;

    String downfilename, downlink, percent_str;

    Download_fragment_listener activityCommander;

    public interface Download_fragment_listener {
        public void selectresult();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            activityCommander = (Download_fragment_listener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString());
        }
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Nullable
    @Override

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View newview = inflater.inflate(R.layout.download_fragment_content, container, false);
        con = getContext();
        dbHelper = new DbHelper(con, null, null, 1);
        Log.e("I am at", "98");
        newdownloaded = new Downloaded();
        docList = dbHelper.getAllLabels();
        dowanloading_view = (LinearLayout) newview.findViewById(R.id.download_card);
        new_recycle = (RecyclerView) newview.findViewById(R.id.newrecycle_download);
        doc_Adapter = new Downloads_adapter(getActivity(), docList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this.getActivity().getApplicationContext());
        new_recycle.setLayoutManager(mLayoutManager);
        new_recycle.setAdapter(doc_Adapter);

        redown = (ImageButton) newview.findViewById(R.id.retry);
        redown.setImageResource(R.mipmap.retry);
        redown.setEnabled(false);
        redown.setVisibility(View.INVISIBLE);
        download_imagecard = (ImageView) newview.findViewById(R.id.download_imagecard);
        close = (ImageButton) newview.findViewById(R.id.cross);
        downloading = (TextView) newview.findViewById(R.id.download_file);
        progressBar = (ProgressBar) newview.findViewById(R.id.progressBar);
        progress_text = (TextView) newview.findViewById(R.id.progress);
        close.setImageResource(R.mipmap.cancel);


        final SharedPreferences sp = getActivity().getSharedPreferences("download_file", Context.MODE_PRIVATE);
        boolean isdownloadfile = sp.getBoolean("isdownload", false);

        if (isdownloadfile) {
            Log.e("I am at", "inside is downloadfile");
            storageReference = firebaseStorage.getReferenceFromUrl("gs://application-ca620.appspot.com/");
            downfilename = sp.getString("file_name", "nothing");
            downlink = sp.getString("link", "nothing");

            if (downfilename.equals("nothing") || downlink.equals("nothing")) {
                Toast.makeText(this.getActivity(), "Error in getting filename or link", Toast.LENGTH_SHORT).show();
                Log.e("I am inside", "downfilename.equals(nothing)");
                ((ViewGroup) dowanloading_view.getParent()).removeView(dowanloading_view);
            } else if (sp.getBoolean("retry", false)) {
                Log.e("I am inside", " retry=true");
                String filetype = getfiletype(downfilename);
                if (filetype.equals("pdf")) {
                    download_imagecard.setImageResource(R.mipmap.pdf_file);
                } else if (filetype.equals("txt")) {
                    download_imagecard.setImageResource(R.mipmap.text_file);
                } else if (filetype.equals("ppt")) {
                    download_imagecard.setImageResource(R.mipmap.ppt_text);
                } else if (filetype.equals("png") || filetype.equals("jpg") || filetype.equals("jpeg")) {
                    download_imagecard.setImageResource(R.mipmap.image_file);
                } else if (filetype.equals("doc") || filetype.equals("docx")) {
                    download_imagecard.setImageResource(R.mipmap.word_file);
                } else {
                    download_imagecard.setImageResource(R.mipmap.raw_file);
                }
                redown.setEnabled(true);
                redown.setVisibility(View.VISIBLE);
                progress_text.setText("");
                progressBar.setProgress(0);
                progressBar.setProgressTintList(ColorStateList.valueOf(getResources().getColor(R.color.bgColor)));
                downloading.setText("Downloading Unsuccessful: " + downfilename);
                downloadRef = storageReference.child(downlink);
                String folder_main = "TU_Share";
                File folder = new File(Environment.getExternalStorageDirectory() + "/tuShare");
                boolean success = false;
                if (!folder.exists()) {
                    success = folder.mkdir();
                }
                if (success) {
                    Log.e("Folder", "created");
                } else {
                    Log.e("Folder", "not created");
                }
                file1 = new File(folder, downfilename);
                /*folder = new File(Environment.getExternalStorageDirectory()+"/TUShare");
                if (!folder.exists()) {
                    folder.mkdirs();
                }
                file1 = new File(folder, downfilename);
                try {
                    FileOutputStream fos=new FileOutputStream(file1);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }*/
                Uri urifile = Uri.fromFile(file1);
                Log.e("I am at", "Setting up newdownloaded in retry");
                newdownloaded.setFileuri(urifile);
                newdownloaded.setUsername(sp.getString("username", "noone"));
                newdownloaded.setSubject(sp.getString("subject", "no"));
                newdownloaded.setFilename(downfilename);
                newdownloaded.setType(sp.getString("type", "nothing"));


            } else {
                Log.e("I am inside", " normal download");
                String filetype = getfiletype(downfilename);
                if (filetype.equals("pdf")) {
                    download_imagecard.setImageResource(R.mipmap.pdf_file);
                } else if (filetype.equals("txt")) {
                    download_imagecard.setImageResource(R.mipmap.text_file);
                } else if (filetype.equals("ppt")) {
                    download_imagecard.setImageResource(R.mipmap.ppt_text);
                } else if (filetype.equals("png") || filetype.equals("jpg") || filetype.equals("jpeg")) {
                    download_imagecard.setImageResource(R.mipmap.image_file);
                } else if (filetype.equals("doc") || filetype.equals("docx")) {
                    download_imagecard.setImageResource(R.mipmap.word_file);
                } else {
                    download_imagecard.setImageResource(R.mipmap.raw_file);
                }
                downloading.setText("Downloading file: " + downfilename);
                downloadRef = storageReference.child(downlink);
                //file1 = new File(Environment.getExternalStorageDirectory(), downfilename);
                File folder = new File(Environment.getExternalStorageDirectory() + "/TUShare");
                boolean success = false;
                if (!folder.exists()) {
                    success = folder.mkdir();
                }
                if (success) {
                    Log.e("Folder", "created");
                } else {
                    Log.e("Folder", "not created");
                }
                file1 = new File(folder, downfilename);
                Uri uri1 = Uri.fromFile(file1);
                Log.e("I am setting", " newdownloaded in normal");
                newdownloaded.setFileuri(uri1);
                newdownloaded.setUsername(sp.getString("username", "noone"));
                newdownloaded.setSubject(sp.getString("subject", "no"));
                newdownloaded.setFilename(downfilename);
                newdownloaded.setType(sp.getString("type", "nothing"));
                progressBar.setProgressTintList(ColorStateList.valueOf(getResources().getColor(R.color.bgColor)));
                SharedPreferences.Editor ed = sp.edit();
                ed.putBoolean("download", true);
                ed.putBoolean("successlisten", true);
                ed.commit();
                fileDownloadTask = downloadRef.getFile(file1).addOnProgressListener(new OnProgressListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        prog = (int) (100.0 * ((taskSnapshot.getBytesTransferred()) / (taskSnapshot.getTotalByteCount())));
                        m = (int) ((taskSnapshot.getBytesTransferred()) * 100);
                        k = (int) taskSnapshot.getTotalByteCount();
                        percent = m / k;
                        percent_str = String.valueOf(percent);
                        //if (percent>50){
                          //  fileDownloadTask.cancel()
                        //}
                        progressBar.setProgress(percent);
                        progress_text.setText(percent_str + "% Downloaded");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        redown.setEnabled(true);
                        redown.setVisibility(View.VISIBLE);
                        progress_text.setText("");
                        progressBar.setProgress(0);
                        downloading.setText("Downloading Unsuccessful: " + downfilename);
                        SharedPreferences.Editor ed = sp.edit();
                        ed.putBoolean("retry", true);
                        ed.putBoolean("download", false);
                        ed.commit();
                    }
                }).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        ((ViewGroup) dowanloading_view.getParent()).removeView(dowanloading_view);
                        if(sp.getBoolean("isdownload",false)){
                        Toast.makeText(con, "Downloaded Successfully", Toast.LENGTH_SHORT).show();}
                        Log.e("I am inside", " success in normal");
                        if (sp.getBoolean("successlisten", true)) {
                            setDbDetails(newdownloaded);

                            //doc_Adapter.updateData(docList);
                            //doc_Adapter = new Downloads_adapter(getActivity(),docList);
                            doc_Adapter.notifyDataSetChanged();
                            SharedPreferences.Editor ed = sp.edit();
                            ed.putBoolean("isdownload", false);
                            ed.putBoolean("retry", false);
                            ed.putBoolean("download", false);
                            ed.putBoolean("successlisten", false);
                            ed.commit();

                            //new_recycle.setAdapter(doc_Adapter);
                            activityCommander.selectresult();
                            Log.e("Position1", "I am here");
                        }
                    }
                });
                //progressBar.setProgress(35);
                //progressBar.setProgressTintList(ColorStateList.valueOf(getResources().getColor(R.color.bgColor)));
                //progress_text.setText("35% Downloaded");

                redown.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        redown.setVisibility(View.INVISIBLE);
                        redown.setEnabled(false);
                        downloading.setText("Downloading file: " + downfilename);
                       // fileDownloadTask = downloadRef.getFile(file1);
                        SharedPreferences.Editor ed = sp.edit();
                        Log.e("I am in ", " redown on click in normal");
                        ed.putBoolean("download", true);
                        ed.commit();
                        progressBar.setProgressTintList(ColorStateList.valueOf(getResources().getColor(R.color.bgColor)));
                        fileDownloadTask=downloadRef.getFile(file1).addOnProgressListener(new OnProgressListener<FileDownloadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                prog = (int) (100.0 * ((taskSnapshot.getBytesTransferred()) / (taskSnapshot.getTotalByteCount())));
                                m = (int) ((taskSnapshot.getBytesTransferred()) * 100);
                                k = (int) taskSnapshot.getTotalByteCount();
                                percent = m / k;
                                percent_str = String.valueOf(percent);
                                progressBar.setProgress(percent);
                                progress_text.setText(percent_str + "% Downloaded");
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                redown.setEnabled(true);
                                redown.setVisibility(View.VISIBLE);
                                progress_text.setText("");
                                progressBar.setProgress(0);
                                downloading.setText("Downloading Unsuccessful: " + downfilename);
                                SharedPreferences.Editor ed = sp.edit();
                                ed.putBoolean("download", false);
                                ed.putBoolean("retry", true);
                                ed.commit();
                            }
                        }).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                ((ViewGroup) dowanloading_view.getParent()).removeView(dowanloading_view);
                                if(sp.getBoolean("isdownload",false)){
                                Toast.makeText(con, "Downloaded Successfully", Toast.LENGTH_SHORT).show();}
                                SharedPreferences.Editor ed = sp.edit();
                                ed.putBoolean("isdownload", false);
                                ed.putBoolean("download", false);
                                ed.putBoolean("retry", false);
                                ed.commit();
                                Log.e("I am in ", " positive,redown on click in normal");
                                setDbDetails(newdownloaded);
                                //doc_Adapter.updateData(docList);
                                //doc_Adapter = new Downloads_adapter(getActivity(),docList);
                                doc_Adapter.notifyDataSetChanged();
                                //new_recycle.setAdapter(doc_Adapter);
                                activityCommander.selectresult();
                            }
                        });
                        //progressBar.setProgress(35);
                        //progressBar.setProgressTintList(ColorStateList.valueOf(getResources().getColor(R.color.bgColor)));

                    }
                });

                close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.e("I am inside of", "close.onclick in retry");
                        final AlertDialog.Builder adb = new AlertDialog.Builder(con);
                        adb.setMessage("Are you sure, You want to cancel download");
                        adb.setIcon(R.mipmap.cancel);
                        adb.setTitle("Cancel Download");
                        adb.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //if (!sp.getBoolean("retry", false)) {
                                    Log.e("inside","cancel");
                                    fileDownloadTask.pause();
                                    //downloadRef.delete();
                                Log.e("ispaused",String.valueOf(fileDownloadTask.isPaused()));
                                    fileDownloadTask.cancel();
                                    Log.e("iscancelled",String.valueOf(fileDownloadTask.isCanceled()));
                                //}
                                Log.e("Value of retry",String.valueOf(sp.getBoolean("retry",false)));
                                ((ViewGroup) dowanloading_view.getParent()).removeView(dowanloading_view);
                                Log.e("I am inside", "dialog of close in close in retry");
                                Toast.makeText(con, "Download Cancelled", Toast.LENGTH_SHORT).show();
                                SharedPreferences.Editor ed = sp.edit();
                                ed.putBoolean("isdownload", false);
                                ed.putBoolean("download", false);
                                ed.putBoolean("retry",false);
                                ed.commit();
                            }
                        });
                        adb.setNegativeButton("Keep downloading", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        });
                        adb.show();

                    }
                });
            }
        } else {
            Log.e("I am in ", " else of isdownloaded");
            ((ViewGroup) dowanloading_view.getParent()).removeView(dowanloading_view);
        }
        //String value = getArguments().getString("filename");
        return newview;
    }

    static private String getfiletype(String p) {
        String extension = p.substring(p.lastIndexOf(".")+1);
        return extension;
        /*int length = p.length();
        int spaceIndex = p.indexOf(".");
        if (spaceIndex != -1) {
            p = p.substring(spaceIndex + 1, length);
        }
        return p;*/
    }

    public void setDbDetails(Downloaded down1) {
        dbHelper.addFile(down1);
        Log.e("I am in ", " set Db details");
    }

}
