package com.aaish.tushare;

/**
 * Created by aaishsindwani on 28/09/16.
 */

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class Myuploads_adapter extends RecyclerView.Adapter<Myuploads_adapter.MyViewHolder> {
    List<Uploaddbreference> newList;

    Myuploads_adapter(List<Uploaddbreference> newList) {
        this.newList = newList;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView nametext_upc, subjecttext_upc, kindtext_upc, usertext_upc;
        public ImageView cardimage_upc;

        public MyViewHolder(View view) {
            super(view);
            nametext_upc = (TextView) view.findViewById(R.id.nametext_up);
            subjecttext_upc = (TextView) view.findViewById(R.id.subjectext_up);
            kindtext_upc = (TextView) view.findViewById(R.id.kindtext_up);
            usertext_upc = (TextView) view.findViewById(R.id.usertext_up);
            cardimage_upc = (ImageView) view.findViewById(R.id.imagecard_up);
        }
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.myuploads_cardview, parent, false);
        return new MyViewHolder(view);
    }

    static private String getfiletype(String p) {
        /*
        int length = p.length();
        int spaceIndex = p.indexOf(".");
        if (spaceIndex != -1) {
            p = p.substring(spaceIndex + 1, length);
        }
        return p;*/
        String extension = p.substring(p.lastIndexOf(".")+1);
        return extension;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Uploaddbreference upd = newList.get(position);
        holder.nametext_upc.setText(upd.getFilename());
        holder.subjecttext_upc.setText("Subject: " + upd.getSubject());
        holder.kindtext_upc.setText("Kind: " + upd.getType());
        holder.usertext_upc.setText("By: " + upd.getUsername());
        String filetype = getfiletype(upd.getFilename());
        if (filetype.equals("pdf")) {
            holder.cardimage_upc.setImageResource(R.mipmap.pdf_file);
        } else if (filetype.equals("txt")) {
            holder.cardimage_upc.setImageResource(R.mipmap.text_file);
        } else if (filetype.equals("ppt")) {
            holder.cardimage_upc.setImageResource(R.mipmap.ppt_text);
        } else if (filetype.equals("png") || filetype.equals("jpg") || filetype.equals("jpeg")) {
            holder.cardimage_upc.setImageResource(R.mipmap.image_file);
        } else if (filetype.equals("doc") || filetype.equals("docx")) {
            holder.cardimage_upc.setImageResource(R.mipmap.word_file);
        } else {
            holder.cardimage_upc.setImageResource(R.mipmap.raw_file);
        }
    }

    @Override
    public int getItemCount() {
        return newList.size();
    }

}

