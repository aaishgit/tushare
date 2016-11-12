package com.aaish.tushare;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

/**
 * Created by aaishsindwani on 28/09/16.
 */
public class Myuploads extends Fragment {
    private ArrayList<Uploaddbreference> docList_up = new ArrayList<Uploaddbreference>();
    private Myuploads_adapter docAdapter_up;
    private RecyclerView newrecycle_up;
    ImageView myupload_image;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference docDatabase;
    FirebaseUser currUser;
    FirebaseAuth currAuth;
    String uid;
    int i;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View uploadview = inflater.inflate(R.layout.myuploads_content, container, false);
        myupload_image = (ImageView) uploadview.findViewById(R.id.frag_myuploads_image);
        myupload_image.setImageResource(R.mipmap.myuploads);
        firebaseDatabase = FirebaseDatabase.getInstance();
        currAuth = FirebaseAuth.getInstance();
        currUser = currAuth.getCurrentUser();
        uid = currUser.getUid();
        docDatabase = firebaseDatabase.getReference("users").child(uid).child("documents").child(uid);
        newrecycle_up = (RecyclerView) uploadview.findViewById(R.id.newrecycle_upload);
        docAdapter_up = new Myuploads_adapter(docList_up);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this.getActivity().getApplicationContext());
        newrecycle_up.setLayoutManager(mLayoutManager);
        newrecycle_up.setAdapter(docAdapter_up);
        docDatabase.addChildEventListener(childeventlistener);
        return uploadview;
    }

    ChildEventListener childeventlistener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            Uploaddbreference newdoc = dataSnapshot.getValue(Uploaddbreference.class);
            docList_up.add(newdoc);
            docAdapter_up.notifyDataSetChanged();
            i++;
            String slog = String.valueOf(i);
            Log.e("No of objects in Upload", slog);
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {

        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };
}
