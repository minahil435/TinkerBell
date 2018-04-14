package com.shuvro.barcodescanner;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Created by Admin on 1/24/2018.
 */

public class Listview extends AppCompatActivity
{



    private FirebaseDatabase FB;
    private DatabaseReference AR;

    String visitorkey;

    SwipeMenuListView listview;
    visitorAdaptor KUCHB;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainlistview);

        FB = FirebaseDatabase.getInstance();
        AR = FB.getReference().child("visitor");

        listview = (SwipeMenuListView) findViewById(R.id.Rootview);
        final ArrayList<kepingVisitorsRecord> zoom = new ArrayList<>();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String ID = user.getUid();

        AR.child(ID).orderByKey().limitToLast(20).addValueEventListener(new ValueEventListener()
        {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                final Iterable<DataSnapshot> children = dataSnapshot.getChildren();

                for (DataSnapshot child : children)

                {

                    kepingVisitorsRecord value = child.getValue(kepingVisitorsRecord.class);
                    zoom.add(new kepingVisitorsRecord(value.getVisitor(), value.getTime(), value.getUri(),child.getKey()));

                }
                Collections.reverse(zoom);
                KUCHB = new visitorAdaptor(Listview.this, zoom);
                listview.setAdapter(KUCHB);





            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        final SwipeMenuCreator creator = new SwipeMenuCreator()
        {

            @Override
            public void create(SwipeMenu menu)
            {

                // create "delete" item
                SwipeMenuItem deleteItem = new SwipeMenuItem(
                        getApplicationContext());
                // set item background
                deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,
                        0x3F, 0x25)));
                // set item width
                deleteItem.setWidth(170);
                // set a icon
                deleteItem.setIcon(R.drawable.delete);
                // add to menu
                menu.addMenuItem(deleteItem);
            }


        };
        listview.setMenuCreator(creator);
        listview.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index)
            {

                kepingVisitorsRecord deleted = zoom.get(position);
                String visitorkey=deleted.getKeyvisitor();

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                String currentuser = user.getUid().toString();
                zoom.clear();
                AR.child(currentuser).child(visitorkey).setValue(null);

                KUCHB.notifyDataSetChanged();

                return false;
            }
        });

    }

}
