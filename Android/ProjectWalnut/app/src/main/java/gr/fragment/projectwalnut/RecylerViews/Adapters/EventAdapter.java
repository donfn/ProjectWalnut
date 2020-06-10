package gr.fragment.projectwalnut.RecylerViews.Adapters;

import android.annotation.SuppressLint;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import gr.fragment.projectwalnut.Events.Event;
import gr.fragment.projectwalnut.R;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.ViewHolder>{

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView img;
        TextView title, time;

        ViewHolder(View view) {
            super(view);
            img = view.findViewById(R.id.single_event_image);
            title = view.findViewById(R.id.single_event_title);
            time = view.findViewById(R.id.single_event_time);
        }
    }

    private List<Event> items;

    public EventAdapter(List<Event> list){
        this.items = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.single_event, viewGroup, false);

        return new ViewHolder(itemView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        Event item = items.get(i);
        //viewHolder.img.setImageResource(item.image);
        viewHolder.title.setText(item.title);
        viewHolder.time.setText(item.time);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
