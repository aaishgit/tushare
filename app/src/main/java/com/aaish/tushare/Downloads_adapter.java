package com.aaish.tushare;

import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by aaishsindwani on 01/10/16.
 */
public class Downloads_adapter extends RecyclerView.Adapter<Downloads_adapter.MyViewHolder> {
    List<Downloaded> newList;
    AlertDialog.Builder adb;
    Context context;
    Uri uri1;
    Typeface tf1;
    String file, uid;

    Downloads_adapter(Context context, List<Downloaded> newList) {
        this.newList = newList;
        this.context = context;
    }

        /*public interface Document_Adapter_eventlistener{
            public void onEventAccured();
        }*/

    //private Document_Adapter_eventlistener doc_listener;

        /*public void setEventListener(Document_Adapter_eventlistener doc_listener) {
            this.doc_listener = doc_listener;
        }*/

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView nametext_down, subjecttext_down, kindtext_down, usertext_down;
        public ImageView cardimage_down;
        public ImageButton opendoc;

        public MyViewHolder(View view) {
            super(view);
            nametext_down = (TextView) view.findViewById(R.id.nametext_down);
            subjecttext_down = (TextView) view.findViewById(R.id.subjectext_down);
            kindtext_down = (TextView) view.findViewById(R.id.kindtext_down);
            usertext_down = (TextView) view.findViewById(R.id.usertext_down);
            cardimage_down = (ImageView) view.findViewById(R.id.imagecard_down);
            opendoc = (ImageButton) view.findViewById(R.id.open_down);
        }
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.downloads_cardview, parent, false);
        return new MyViewHolder(view);
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

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final Downloaded upd = newList.get(position);
        holder.opendoc.setImageResource(R.mipmap.view_file);
        tf1 = Typeface.create(Typeface.DEFAULT, Typeface.BOLD);
        holder.nametext_down.setText(upd.getFilename());
        holder.subjecttext_down.setText("Subject: " + upd.getSubject());
        holder.kindtext_down.setText("Kind: " + upd.getType());
        holder.usertext_down.setText("By: " + upd.getUsername());
        String filetype = getfiletype(upd.getFilename());
        if (filetype.equals("pdf")) {
            holder.cardimage_down.setImageResource(R.mipmap.pdf_file);
        } else if (filetype.equals("txt")) {
            holder.cardimage_down.setImageResource(R.mipmap.text_file);
        } else if (filetype.equals("ppt")) {
            holder.cardimage_down.setImageResource(R.mipmap.ppt_text);
        } else if (filetype.equals("png") || filetype.equals("jpg") || filetype.equals("jpeg")) {
            holder.cardimage_down.setImageResource(R.mipmap.image_file);
        } else if (filetype.equals("doc") || filetype.equals("docx")) {
            holder.cardimage_down.setImageResource(R.mipmap.word_file);
        } else {
            holder.cardimage_down.setImageResource(R.mipmap.raw_file);
        }
        holder.opendoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                adb = new AlertDialog.Builder(view.getContext());
                adb.setTitle("Open File").setIcon(R.mipmap.view_file);
                file = upd.getFilename();
                adb.setMessage("Do you want to open " + upd.getFilename());
                adb.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                adb.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        uri1 = upd.getFileuri();
                        Log.e("File uri",String.valueOf(uri1));
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri1);
                        ContentResolver cR = context.getContentResolver();
                        String type=null;
                        //MimeTypeMap mime = MimeTypeMap.getSingleton();
                        String extension=getfiletype(upd.getFilename());
                        //String extension = mime.getExtensionFromMimeType(cR.getType(uri1));
                        if (extension != null) {
                            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
                        }
                        intent.setDataAndType(uri1,type);
                        //Toast.makeText(context, "Type  is" + type, Toast.LENGTH_SHORT).show();
                        /*if(getfiletype(upd.getFilename()).equals("pdf")){
                            intent.setDataAndType(uri1);
                        }
                        else if(getfiletype(upd.getFilename()).equals("png")){
                            intent.setDataAndType(uri1,"application/pdf");
                        }
                        else if(getfiletype(upd.getFilename()).equals("pdf")){
                            intent.setDataAndType(uri1,"application/pdf");
                        }
                        else if(getfiletype(upd.getFilename()).equals("pdf")){
                            intent.setDataAndType(uri1,"application/pdf");
                        }
                        else if(getfiletype(upd.getFilename()).equals("pdf")){
                            intent.setDataAndType(uri1,"application/pdf");
                        }
                        else if(getfiletype(upd.getFilename()).equals("pdf")){
                            intent.setDataAndType(uri1,"application/pdf");
                        }*/
                        context.startActivity(intent);
                            /*String filetype=getfiletype(upd.getFilename());
                            if(filetype.equals("pdf")){
                                download_imagecard.setImageResource(R.mipmap.pdf_file);
                            }
                            else if(filetype.equals("txt")){
                                download_imagecard.setImageResource(R.mipmap.text_file);
                            }
                            else if(filetype.equals("ppt")){
                                download_imagecard.setImageResource(R.mipmap.ppt_text);
                            }
                            else if (filetype.equals("png")||filetype.equals("jpg")||filetype.equals("jpeg")){
                                download_imagecard.setImageResource(R.mipmap.image_file);
                            }
                            else if (filetype.equals("doc")||filetype.equals("docx")){
                                download_imagecard.setImageResource(R.mipmap.word_file);
                            }
                            else{
                                download_imagecard.setImageResource(R.mipmap.raw_file);
                            }*/

                           /*
                            //Bundle args=new Bundle();
                            //args.putString("filename",upd.getFilename());
                            //down.setArguments(args);
                            FragmentTransaction ft=((FragmentActivity)context).getSupportFragmentManager().beginTransaction();
                            ft.replace(R.id.flContent,down).commit();
                            if(doc_listener!=null){
                                doc_listener.onEventAccured();
                            }
                        currAuth=FirebaseAuth.getInstance();
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
                adb.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return newList.size();
    }

    public void updateData(ArrayList<Downloaded> viewModels) {
        newList.clear();
        newList.addAll(viewModels);
        notifyDataSetChanged();
    }

}
