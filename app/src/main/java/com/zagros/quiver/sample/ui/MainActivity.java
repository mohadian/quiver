package com.zagros.quiver.sample.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.VolleyError;
import com.zagros.quiver.rest.core.RestCall;
import com.zagros.quiver.rest.core.listeners.RestResponseListener;
import com.zagros.quiver.sample.R;
import com.zagros.quiver.sample.comms.flickr.FlickrManager;
import com.zagros.quiver.sample.comms.flickr.api.FlickrApi;
import com.zagros.quiver.sample.comms.flickr.api.FlickrConstants;
import com.zagros.quiver.sample.comms.flickr.models.FlickrPhoto;
import com.zagros.quiver.sample.comms.flickr.parsers.FlickrPhotosParser;
import com.zagros.quiver.sample.ui.widgets.FlickrRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends BaseActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private List<FlickrPhoto> photoList = new ArrayList<>();
    private RecyclerView recyclerView;
    private FlickrRecyclerViewAdapter flickrRecyclerViewAdapter;

    private RestResponseListener<FlickrApi.Method> listener = new RestResponseListener<FlickrApi.Method>() {
        @Override
        public void onSuccess(RestCall<FlickrApi.Method> request) {
            FlickrPhotosParser parser = (FlickrPhotosParser) request.getParser();
            flickrRecyclerViewAdapter = new FlickrRecyclerViewAdapter(MainActivity.this, parser.photos);
            recyclerView.setAdapter(flickrRecyclerViewAdapter);
        }

        @Override
        public void onProgressStarted(RestCall<FlickrApi.Method> request) {

        }

        @Override
        public void onProgressFinished(RestCall<FlickrApi.Method> request) {

        }

        @Override
        public void onFailure(RestCall<FlickrApi.Method> request, VolleyError error) {
            Log.d("", error.getMessage());
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        activateToolbar();
        recyclerView = (RecyclerView) findViewById(R.id.main_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        FlickrManager.instance().init(getApplicationContext());

        FlickrManager.instance().getPublicPhotos(listener, null, null, "Android", FlickrConstants.TAG_MODE_ALL, "en");

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = null;// inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }
    }

    protected void onPostExecute(String s) {

    }
}
