package com.intuisoft.moderncalc.omdbapi.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.intuisoft.moderncalc.omdbapi.R;
import com.intuisoft.moderncalc.omdbapi.data.SearchResult;

import java.util.ArrayList;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
    private ArrayList<SearchResult> results;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public TextView title;
        public TextView pilot;
        public TextView year;
        public TextView type;
        public ViewHolder(View v) {
            super(v);
            imageView = v.findViewById(R.id.imageView);
            title = v.findViewById(R.id.title);
            pilot = v.findViewById(R.id.pilot);
            year = v.findViewById(R.id.year);
            type = v.findViewById(R.id.type);
        }
    }

    public RecyclerViewAdapter(ArrayList<SearchResult> results) {
        this.results = results;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent,
                                         int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_result_row, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        SearchResult result = results.get(position);
        holder.title.setText(result.Title);
        holder.pilot.setText(result.Title);
        holder.year.setText(result.Year);

        if(result.Type.equals("series") || result.Type.equals("movie"))
            holder.type.setText(result.Type.equals("series") ? "TV Series" : "Movie");
        else
            holder.type.setVisibility(View.GONE);

        Glide.with(holder.imageView.getContext())
                .load(result.Poster)
                .apply(new RequestOptions()
                .placeholder(R.drawable.no_img)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .fitCenter())
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return results.size();
    }
}
