package com.aaish.tushare;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ScrollView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;

public class Home extends AppCompatActivity implements Mainhome.Mainhome_listener, Download_fragment.Download_fragment_listener {
    FirebaseAuth currAuth;
    String email, name, uid;
    FirebaseUser currUser;
    ScrollView ourscroll;
    FirebaseDatabase userRef;
    DatabaseReference userDb;
    Fielddetails userDetails;
    Drawer result;
    Fragment fragment1 = null;
    Class fragmentClass;
    //  Document_adapter document_adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //document_adapter=new Document_adapter(this.getApplicationContext());
        // document_adapter.setEventListener(new Document_adapter.Document_Adapter_eventlistener() {
           /* @Override
            public void onEventAccured() {
                result.setSelection(5);
            }
        });*/
        setContentView(R.layout.activity_home);
        setTitle("TU Share");
        //getActionBar().setIcon(R.mipmap.tushare_logo);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        ourscroll=(ScrollView)findViewById(R.id.flContent);
        setSupportActionBar(toolbar);
        toolbar.setTitle("");
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        final SharedPreferences sp = getSharedPreferences("download_file", Context.MODE_PRIVATE);
        final PrimaryDrawerItem item1 = new PrimaryDrawerItem().withIdentifier(1).withName("Home").withTextColor(Color.BLACK)
                .withIcon(R.mipmap.home).withSelectedTextColor(getResources().getColor(R.color.pink))
                .withSelectedColor(getResources().getColor(R.color.selected)).withSelectedIcon(R.mipmap.home_selected);
        /*final PrimaryDrawerItem item2 = new PrimaryDrawerItem().withIdentifier(2).withName("Search").withTextColor(Color.BLACK)
                .withIcon(R.mipmap.search).withSelectedTextColor(getResources().getColor(R.color.pink))
                .withSelectedColor(getResources().getColor(R.color.selected)).withSelectedIcon(R.mipmap.search_selected);*/
        final PrimaryDrawerItem item3 = new PrimaryDrawerItem().withIdentifier(3).withName("My Profile").withTextColor(Color.BLACK)
                .withIcon(R.mipmap.profile).withSelectedTextColor(getResources().getColor(R.color.pink))
                .withSelectedColor(getResources().getColor(R.color.selected)).withSelectedIcon(R.mipmap.profile_selected);
        final PrimaryDrawerItem item4 = new PrimaryDrawerItem().withIdentifier(4).withName("Uploads").withTextColor(Color.BLACK)
                .withIcon(R.mipmap.uploaded).withSelectedTextColor(getResources().getColor(R.color.pink))
                .withSelectedColor(getResources().getColor(R.color.selected)).withSelectedIcon(R.mipmap.uploaded_selected);
        final PrimaryDrawerItem item5 = new PrimaryDrawerItem().withIdentifier(5).withName("Downloads").withTextColor(Color.BLACK)
                .withIcon(R.mipmap.download).withSelectedTextColor(getResources().getColor(R.color.pink))
                .withSelectedColor(getResources().getColor(R.color.selected)).withSelectedIcon(R.mipmap.download_selected);
        final PrimaryDrawerItem item6 = new PrimaryDrawerItem().withIdentifier(6).withName("Settings").withTextColor(Color.BLACK)
                .withIcon(R.mipmap.settings);
        final PrimaryDrawerItem item7 = new PrimaryDrawerItem().withIdentifier(7).withName("Logout").withTextColor(Color.BLACK)
                .withIcon(R.mipmap.logout);
        //PrimaryDrawerItem item8 = new PrimaryDrawerItem().withIdentifier(6).withName("Exit").withTextColor(Color.BLACK).withDescription("exit the app")
        //      .withIcon(R.mipmap.exit);
        Log.e("Account Header time", ": now");
        SharedPreferences.Editor edt= sp.edit();
        edt.putBoolean("retry",false);
        edt.putBoolean("download",false);
        edt.commit();
        userDetails = new Fielddetails();
        currAuth = FirebaseAuth.getInstance();
        currUser = currAuth.getCurrentUser();
        email = currUser.getEmail().toString();
        userRef = FirebaseDatabase.getInstance();
        uid = currUser.getUid().toString();
        userDb = userRef.getReference("users").child(uid).child("details");
        userDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userDetails = dataSnapshot.getValue(Fielddetails.class);
                name = userDetails.getName();
                AccountHeader headerResult = new AccountHeaderBuilder()
                        .withActivity(Home.this)
                        .withHeaderBackground(R.mipmap.material_heavy_res).withTranslucentStatusBar(true)
                        .addProfiles(
                                new ProfileDrawerItem().withName(name).withEmail(email)
                        ).withProfileImagesVisible(false).withSelectionListEnabledForSingleProfile(false)
                        .build();
                result = new DrawerBuilder().withAccountHeader(headerResult).
                        withSliderBackgroundColor(getResources().getColor(R.color.sliderbg))
                        .withActivity(Home.this)
                        .withToolbar(toolbar)/*.withSliderBackgroundColor(getResources().getColor(R.color.abc)).addStickyDrawerItems(item1,item2)*/
                        .addDrawerItems(item1, item3, item4, item5,
                                new DividerDrawerItem(), item6, item7)
                        .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                            @Override
                            public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                                if (sp.getBoolean("download", false)) {
                                    Toast.makeText(Home.this, "Cannot switch while downloading", Toast.LENGTH_SHORT).show();
                                } else {
                                    switch (position) {
                                        case 1:
                                            fragmentClass = Mainhome.class;
                                            result.closeDrawer();
                                            break;
                                        case 2:
                                            fragmentClass = Details_fragment.class;
                                            result.closeDrawer();
                                            break;
                                        case 3:
                                            fragmentClass = Myuploads.class;
                                            result.closeDrawer();
                                            break;
                                        case 4:
                                            fragmentClass = Download_fragment.class;
                                            result.closeDrawer();
                                            break;
                                        case 5:
                                            result.closeDrawer();
                                            break;
                                        case 6:
                                            Intent in1 = new Intent(Home.this, Apppreference.class);
                                            startActivity(in1);
                                            result.setSelection(1);
                                            result.closeDrawer();
                                            break;
                                        case 7:
                                            result.setSelection(1);
                                            result.closeDrawer();
                                            AlertDialog.Builder ald = new AlertDialog.Builder(Home.this);
                                            ald.setTitle("Confirm Logout");
                                            ald.setIcon(R.mipmap.logout);
                                            ald.setMessage("Do you want to logout");
                                            ald.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    currAuth.signOut();
                                                    Intent log1 = new Intent(Home.this, log_screen.class);
                                                    startActivity(log1);
                                                    finish();

                                                }
                                            });
                                            ald.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    result.setSelection(1);
                                                }
                                            });
                                            ald.show();
                                            break;

                                        default:
                                            fragmentClass = Mainhome.class;
                                            break;
                                    }
                                    /*case 3:
                                        break;
                                    case 4:
                                        break;
                                    case 5:
                                        break;*/
                                    try {
                                        fragment1 = (Fragment) fragmentClass.newInstance();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    // Insert the fragment by replacing any existing fragment
                                    FragmentManager fragmentManager = getSupportFragmentManager();
                                    fragmentManager.beginTransaction().replace(R.id.flContent, fragment1).commit();
                                    ourscroll.smoothScrollTo(0,0);
                                    // }
                                    return true;
                                }
                                return false;
                            }
                        }).build();
                result.setSelection(1);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(Home.this, "error", Toast.LENGTH_SHORT).show();
            }
        });
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!sp.getBoolean("download", false)) {
               /* Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
                    fragmentClass = Upload.class;
                    result.closeDrawer();
                    result.setSelection(0);
                    try {
                        fragment1 = (Fragment) fragmentClass.newInstance();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.flContent, fragment1).commit();

                } else {
                    Toast.makeText(Home.this, "Cant switch during downloads", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        if (result.isDrawerOpen()) {
            result.closeDrawer();
        } else {
            Intent in = new Intent(Intent.ACTION_MAIN);
            in.addCategory(Intent.CATEGORY_HOME);
            in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(in);
            finish();

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater mif = getMenuInflater();
        mif.inflate(R.menu.threedots, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings1:
                Intent in1 = new Intent(this, Apppreference.class);
                startActivity(in1);
                break;
            case R.id.logoutopt:
                AlertDialog.Builder ald = new AlertDialog.Builder(Home.this);
                ald.setTitle("Confirm Logout");
                ald.setIcon(R.mipmap.logout);
                ald.setMessage("Do you want to logout");
                ald.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        currAuth.signOut();
                        Intent log1 = new Intent(Home.this, log_screen.class);
                        startActivity(log1);
                        finish();

                    }
                });
                ald.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        result.setSelection(1);
                    }
                });
                ald.show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void selectresult() {
        result.setSelection(5);
    }
}
