package com.mahmoud.dahdouh.musicplayer.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mahmoud.dahdouh.musicplayer.R;
import com.mahmoud.dahdouh.musicplayer.model.SongModel;

import java.util.ArrayList;
import java.util.List;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.SongViewHolder> {

    private List<SongModel> list = new ArrayList<>();

    public void setList(List<SongModel> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SongViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.listitem_song, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull SongViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class SongViewHolder extends RecyclerView.ViewHolder {

        // Declare your views
        private TextView name;
//        private TextView length;
//        private ImageView image;

        public SongViewHolder(@NonNull View itemView) {
            super(itemView);

            // inflate the view
            name = itemView.findViewById(R.id.li_song_name);
//            length = itemView.findViewById(R.id.li_song_length);
//            image = itemView.findViewById(R.id.li_song_image);
        }

        private void bind(int position) {
            // Bind data
            name.setText(list.get(position).getName());
//            length.setText(list.get(position).getLength());
//            image.setImageBitmap(list.get(position).getImage());
        }
    }
}
