package lt.sm.demoflickerstreamer;

import android.app.ActionBar;
import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.drm.DrmStore;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import java.util.ArrayList;

import lt.smtools.utils.L;

public class MainActivity extends Activity {

    private ListView listGallery;
    private View backgroundOverlay;
    private SearchView searchView;
    private ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        actionBar = getActionBar();
        actionBar.show();
        listGallery = (ListView) findViewById(R.id.list_gallery);
        backgroundOverlay = findViewById(R.id.background_overlay);
        backgroundOverlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        Flicker.get(this).getGallery(null, new Flicker.GalleryCallback() {
            @Override
            public void run(ArrayList<GalleryItem> items) {
                listGallery.setAdapter(new GalleryAdapter(items, MainActivity.this, null));
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView =
                (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                actionBar.setDisplayHomeAsUpEnabled(true);
                backgroundOverlay.setVisibility(View.VISIBLE);
            }
        });
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                actionBar.setDisplayHomeAsUpEnabled(false);
                backgroundOverlay.setVisibility(View.GONE);
                return false;
            }
        });
        searchView.setQueryHint(getString(R.string.search_hint));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String newText) {
                return true;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {
                Flicker.get(MainActivity.this).getGallery(query, new Flicker.GalleryCallback() {
                    @Override
                    public void run(ArrayList<GalleryItem> items) {
                        L.d("Gallery count: %d.", items.size());
                        listGallery.setAdapter(new GalleryAdapter(items, MainActivity.this, null));
                    }
                });
                return true;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
//        if (id == R.id.action_search) {
//            return true;
//        }
        if (id == android.R.id.home) {
            searchView.setIconified(true);
            return true;
        }

    return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (!searchView.isIconified()) {
            searchView.setIconified(true);
        } else {
            super.onBackPressed();
        }
    }
}
