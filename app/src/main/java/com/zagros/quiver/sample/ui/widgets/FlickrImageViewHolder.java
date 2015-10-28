package com.zagros.quiver.sample.ui.widgets;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.zagros.quiver.sample.R;


/**
 * Created by Mostafa.Hadian on 2/13/15.
 */
public class FlickrImageViewHolder extends RecyclerView.ViewHolder{
    protected ImageView thumbnail;
    protected TextView title;

    public FlickrImageViewHolder(View itemView) {
        super(itemView);
        this.thumbnail = (ImageView) itemView.findViewById(R.id.browse_thumbnail);
        this.title = (TextView) itemView.findViewById(R.id.browse_title);
    }
}
