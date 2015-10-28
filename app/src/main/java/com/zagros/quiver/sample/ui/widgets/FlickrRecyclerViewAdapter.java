package com.zagros.quiver.sample.ui.widgets;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;
import com.zagros.quiver.sample.R;
import com.zagros.quiver.sample.comms.flickr.models.FlickrPhoto;

import java.util.List;


public class FlickrRecyclerViewAdapter extends RecyclerView.Adapter<FlickrImageViewHolder> {

    private List<FlickrPhoto> photoList;
    private Context context;

    public FlickrRecyclerViewAdapter(Context context, List<FlickrPhoto> photoList) {
        this.photoList = photoList;
        this.context = context;
    }

    @Override
    public FlickrImageViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item_browse, null);
        FlickrImageViewHolder flickrImageViewHolder = new FlickrImageViewHolder(view);
        return flickrImageViewHolder;
    }

    @Override
    public void onBindViewHolder(FlickrImageViewHolder flickrImageViewHolder, int i) {
        FlickrPhoto photo = photoList.get(i);
        Picasso.with(context).load(photo.getImage()).error(R.drawable.placeholder).placeholder(R.drawable.placeholder).into(flickrImageViewHolder.thumbnail);
        flickrImageViewHolder.title.setText(photo.getTitle());
    }

    @Override
    public int getItemCount() {
        if (photoList!=null){
            return photoList.size();
        } else {
            return 0;
        }
    }
}
