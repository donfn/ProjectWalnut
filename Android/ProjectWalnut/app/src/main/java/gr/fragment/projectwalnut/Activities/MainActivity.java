package gr.fragment.projectwalnut.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import gr.fragment.projectwalnut.Core.API;
import gr.fragment.projectwalnut.Events.Entrance;
import gr.fragment.projectwalnut.Events.Event;
import gr.fragment.projectwalnut.Events.Intrusion;
import gr.fragment.projectwalnut.R;
import gr.fragment.projectwalnut.RecylerViews.Adapters.EventAdapter;
import gr.fragment.projectwalnut.RecylerViews.TouchListeners.TouchListener;

public class MainActivity extends AppCompatActivity {

    private EventAdapter mAdapter;
    private List<Event> items = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gr.fragment.projectwalnut.Core.System.init(this);

        API.init("test@test.com", "test123");

        for (int i=0; i < 5; i++){
            items.add(new Entrance("", "Stayros entered the house", "Sun 14 Sept 17:32", "Stayros Kiagias"));
        }

        SwitchCompat switchCompat = findViewById(R.id.arm_switch);
        TextView arm_text = findViewById(R.id.arm_text);
        RecyclerView recyclerView = findViewById(R.id.recycle_view);

        switchCompat.setChecked(API.getArmed());
        switchCompat.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked) arm_text.setText(R.string.system_armed);
            else arm_text.setText(R.string.system_disarmed);
            API.armHouse(isChecked);
        });

        //RecyclerView Settings
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        mAdapter = new EventAdapter(items);
        recyclerView.setAdapter(mAdapter);

        recyclerView.addOnItemTouchListener(new TouchListener(this, recyclerView, new TouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
//                Intent i = new Intent(System.mContext, DeviceItemActivity.class);
//                i.putExtra("device", items.get(position));
//                System.mContext.startActivity(i);
            }

            @Override
            public void onLongClick(View view, final int position) {

            }
        }));

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) {
                    // Scrolling up
                }
                else {
                    // Scrolling down
                }
            }
        });

    }

}