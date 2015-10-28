package com.zagros.quiver.sample.comms.flickr.parsers;

import android.util.Log;

import com.zagros.quiver.rest.core.parser.ParserException;
import com.zagros.quiver.sample.comms.flickr.api.FlickrConstants;
import com.zagros.quiver.sample.comms.flickr.models.FlickrPhoto;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mostafa on 25/10/2015.
 */
public class FlickrPhotosParser extends FlickrApiParser {
    private static final String TAG = FlickrPhotosParser.class.getSimpleName();
    public List<FlickrPhoto> photos;

    @Override
    public void parse() throws ParserException {
        super.parse();
        try {
            photos = new ArrayList<>();

            JSONObject jsonData = getRestObject();

            JSONArray jsonItems = jsonData.getJSONArray(FlickrConstants.PARSER_ITEMS);
            for (int i = 0; i < jsonItems.length(); i++) {
                JSONObject jsonObject = jsonItems.getJSONObject(i);
                String title = jsonObject.getString(FlickrConstants.PARSER_TITLE);
                String author = jsonObject.getString(FlickrConstants.PARSER_AUTHOR);
                String authorId = jsonObject.getString(FlickrConstants.PARSER_AUTHOR_ID);
                String link = jsonObject.getString(FlickrConstants.PARSER_LINK);
                String tags = jsonObject.getString(FlickrConstants.PARSER_TAGS);

                JSONObject jsonMedia = jsonObject.getJSONObject(FlickrConstants.PARSER_MEDIA);
                String photoLink = jsonMedia.getString(FlickrConstants.PARSER_PHOTO_URL);

                FlickrPhoto photo = new FlickrPhoto(title, author, authorId, link, tags, photoLink);

                photos.add(photo);
            }

            for (FlickrPhoto single : photos) {
                Log.v(TAG, single.toString());
            }
        } catch (JSONException je) {
            je.printStackTrace();
            Log.e(TAG, "", je);
        }
    }
}
