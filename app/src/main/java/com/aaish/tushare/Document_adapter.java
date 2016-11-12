package com.aaish.tushare;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.List;

/**
 * Created by aaishsindwani on 14/09/16.
 */
public class Document_adapter extends RecyclerView.Adapter<Document_adapter.MyViewHolder> {
    List<Uploaddbreference> newList;
    AlertDialog.Builder adb;
    AlertDialog.Builder downprog;
    int prog, m, k, x, percent;
    String percent_str;
    Context context;
    Uri uri1;
    String file, uid;
    Typeface tf1;
    FirebaseStorage fs = FirebaseStorage.getInstance();
    StorageReference storage;
    FirebaseAuth currAuth;
    FirebaseUser currUser;

    Document_adapter(Context context, List<Uploaddbreference> newList) {
        this.newList = newList;
        this.context = context;
    }

    public interface Document_Adapter_eventlistener {
        public void onEventAccured();
    }

    private Document_Adapter_eventlistener doc_listener;

    public void setEventListener(Document_Adapter_eventlistener doc_listener) {
        this.doc_listener = doc_listener;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView nametext, subjecttext, kindtext, usertext;
        public ImageView cardimage;
        public ImageButton downloadit, info_doc;

        public MyViewHolder(View view) {
            super(view);
            nametext = (TextView) view.findViewById(R.id.nametext);
            subjecttext = (TextView) view.findViewById(R.id.subjectext);
            kindtext = (TextView) view.findViewById(R.id.kindtext);
            usertext = (TextView) view.findViewById(R.id.usertext);
            cardimage = (ImageView) view.findViewById(R.id.imagecard);
            downloadit = (ImageButton) view.findViewById(R.id.downloadimagebutton);
            info_doc = (ImageButton) view.findViewById(R.id.infodoc);
        }
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cardview, parent, false);
        return new MyViewHolder(view);
    }

    static private String getfiletype(String p) {
        /*int length = p.length();
        int spaceIndex = p.indexOf(".");
        if (spaceIndex != -1) {
            p = p.substring(spaceIndex + 1, length);
        }
        return p;*/
        String extension = p.substring(p.lastIndexOf(".")+1);
        return extension;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final Uploaddbreference upd = newList.get(position);
        storage = fs.getReferenceFromUrl("gs://application-ca620.appspot.com/");
        holder.downloadit.setImageResource(R.mipmap.download_file_image);
        tf1 = Typeface.create(Typeface.DEFAULT, Typeface.BOLD);
        holder.info_doc.setImageResource(R.mipmap.info_doc);
        holder.info_doc.setVisibility(View.INVISIBLE);
        holder.info_doc.setEnabled(false);
        holder.nametext.setText(upd.getFilename());
        holder.subjecttext.setText("Subject: " + upd.getSubject());
        holder.kindtext.setText("Kind: " + upd.getType());
        holder.usertext.setText("By: " + upd.getUsername());
        String filetype = getfiletype(upd.getFilename());
        if (filetype.equals("pdf")) {
            holder.cardimage.setImageResource(R.mipmap.pdf_file);
        } else if (filetype.equals("txt")) {
            holder.cardimage.setImageResource(R.mipmap.text_file);
        } else if (filetype.equals("ppt")) {
            holder.cardimage.setImageResource(R.mipmap.ppt_text);
        } else if (filetype.equals("png") || filetype.equals("jpg") || filetype.equals("jpeg")) {
            holder.cardimage.setImageResource(R.mipmap.image_file);
        } else if (filetype.equals("doc") || filetype.equals("docx")) {
            holder.cardimage.setImageResource(R.mipmap.word_file);
        } else {
            holder.cardimage.setImageResource(R.mipmap.raw_file);
        }
        holder.downloadit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                adb = new AlertDialog.Builder(view.getContext());
                adb.setTitle("Download Confirmation");
                file = upd.getFilename();
                adb.setMessage("Do you want to download \n" + upd.getFilename());
                adb.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                adb.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Download_fragment down = new Download_fragment();
                        SharedPreferences sp = context.getSharedPreferences("download_file", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putBoolean("isdownload", true);
                        editor.putBoolean("retry", false);
                        editor.putString("file_name", upd.getFilename());
                        editor.putString("link", upd.getLink());
                        editor.putString("subject", upd.getSubject());
                        editor.putString("type", upd.getType());
                        editor.putString("username", upd.getUsername());
                        editor.commit();
                        //Bundle args=new Bundle();
                        //args.putString("filename",upd.getFilename());
                        //down.setArguments(args);
                        FragmentTransaction ft = ((FragmentActivity) context).getSupportFragmentManager().beginTransaction();
                        ft.replace(R.id.flContent, down).commit();
                        if (doc_listener != null) {
                            doc_listener.onEventAccured();
                        }
                        /*currAuth=FirebaseAuth.getInstance();
                        currUser=currAuth.getCurrentUser();
                        uid=currUser.getUid();
                        StorageReference fileRef=storage.child(upd.getLink());
                        //File localFile= null;
                        File file1  = new File(Environment.getExternalStorageDirectory(), upd.getFilename());
                        uri1 = Uri.fromFile(file1);*/


          /*  try {
                localFile = File.createTempFile("file1","pdf");
            } catch (IOException e) {
                e.printStackTrace();
            }*/      /*   downprog=new AlertDialog.Builder(view.getContext()).setTitle("Downloading file")
                                .setIcon(R.mipmap.download_confirmation)
                                .setMessage("Downloaded "+percent_str+"% of file \n"+upd.getFilename())
                                .setNegativeButton("Cancel Download", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        FileDownloadTask
                                    }
                                });
                        downprog.show();
                        fileRef.getFile(file1).addOnProgressListener(new OnProgressListener<FileDownloadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                prog =(int)(100.0 * ((taskSnapshot.getBytesTransferred()) /(taskSnapshot.getTotalByteCount())));
                                m=(int)((taskSnapshot.getBytesTransferred())*100);
                                k=(int)taskSnapshot.getTotalByteCount();
                                percent=m/k;
                                percent_str=String.valueOf(percent);
                                downprog.setMessage("Downloaded "+percent_str+"% of file \n"+upd.getFilename());*/
                        /*Download_fragment down=new Download_fragment();
                        Bundle args=new Bundle();
                        args.putString("filename",upd.getFilename());
                        down.setArguments(args);*/
                        //context.getFragmentManager().beginTransaction().replace(R.id.flContent, down).commit();
                    }
                });
                adb.setIcon(R.mipmap.download_confirmation);
                adb.show();
            }
        });
        /*holder.info_doc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(view.getContext(), " Info of " + position, Toast.LENGTH_SHORT).show();
            }
        });*/
    }

    @Override
    public int getItemCount() {
        return newList.size();
    }

}
