package com.aaish.tushare;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

/**
 * Created by aaishsindwani on 27/09/16.
 */
public class Mainhome extends Fragment {
    FirebaseDatabase firebaseDatabase;
    DatabaseReference docDatabase;
    int i;
    private ArrayList<Uploaddbreference> docList = new ArrayList<Uploaddbreference>();
    private Document_adapter docAdapter;
    private RecyclerView newrecycle;

    Mainhome_listener activityCommander;

    public interface Mainhome_listener {
        public void selectresult();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            activityCommander = (Mainhome_listener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString());
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View homeview = inflater.inflate(R.layout.mainhome, container, false);
        firebaseDatabase = FirebaseDatabase.getInstance();
        docDatabase = firebaseDatabase.getReference("documents");
        newrecycle = (RecyclerView) homeview.findViewById(R.id.newrecycle);
        docAdapter = new Document_adapter(getActivity(), docList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this.getActivity().getApplicationContext());
        newrecycle.setLayoutManager(mLayoutManager);
        newrecycle.setAdapter(docAdapter);
        docAdapter.setEventListener(new Document_adapter.Document_Adapter_eventlistener() {
            @Override
            public void onEventAccured() {
                activityCommander.selectresult();
            }
        });
        docDatabase.addChildEventListener(childeventlistener);
        return homeview;
    }

    ChildEventListener childeventlistener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            Uploaddbreference newdoc = dataSnapshot.getValue(Uploaddbreference.class);
            docList.add(newdoc);
            docAdapter.notifyDataSetChanged();
            i++;
            String slog = String.valueOf(i);
            Log.e("No of objects", slog);
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
