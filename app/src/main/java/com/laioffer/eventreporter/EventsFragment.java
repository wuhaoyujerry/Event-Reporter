package com.laioffer.eventreporter;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.laioffer.eventreporter.artifacts.Event;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class EventsFragment extends Fragment {
  private ImageView mImageViewAdd;
  private RecyclerView recyclerView;
  private RecyclerView.Adapter mAdapter;
  private RecyclerView.LayoutManager mLayoutManager;
  private DatabaseReference database;
  private List<Event> events;

  public EventsFragment() {
    // Required empty public constructor
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View view = inflater.inflate(R.layout.fragment_events, container, false);

    // Initiate
    mImageViewAdd = (ImageView) view.findViewById(R.id.img_event_add);

    // Add Listener
    mImageViewAdd.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        // Send intent to start activity
        Intent eventReportIntent = new Intent(getActivity(), EventReportActivity.class);
        startActivity(eventReportIntent);
      }
    });

    recyclerView = (RecyclerView) view.findViewById(R.id.event_recycler_view);
    database = FirebaseDatabase.getInstance().getReference();
    recyclerView.setHasFixedSize(true);
    mLayoutManager = new LinearLayoutManager(getContext());
    recyclerView.setLayoutManager(mLayoutManager);
    setAdapter();

    return view;
  }

  /**
   * Set adapter for recycler view to show all events
   */
  public void setAdapter() {
    events = new ArrayList<Event>();
    database.child("events").addListenerForSingleValueEvent(new ValueEventListener() {
      @Override
      public void onDataChange(DataSnapshot dataSnapshot) {
        for (DataSnapshot noteDataSnapshot : dataSnapshot.getChildren()) {
          Event event = noteDataSnapshot.getValue(Event.class);
          events.add(event);
        }

        // Sort the event by reverse chronological order
        Collections.sort(events, new Comparator<Event>() {
          @Override
          public int compare(Event o1, Event o2) {
            if (o1.getTime() == o2.getTime()) {
              return 0;
            }

            return o1.getTime() > o2.getTime() ? -1 : 1;
          }
        });

        mAdapter = new EventListAdapter(events, getContext());
        recyclerView.setAdapter(mAdapter);
      }

      @Override
      public void onCancelled(DatabaseError databaseError) {
        //TODO: do something
      }
    });
  }
}
