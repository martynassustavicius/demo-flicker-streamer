package lt.sm.demoflickerstreamer;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import lt.smtools.Config;
import lt.smtools.network.NetworkManager;
import lt.smtools.network.NetworkRequest;
import lt.smtools.network.NetworkResponse;
import lt.smtools.utils.L;

/**
 * Created by Martynas on 2014-07-05.
 */
public class Flicker {

    private static Flicker instance;

    public static Flicker get(Context context) {
        if (instance == null) {
            instance = new Flicker(context);
        }
        return instance;
    }

    private NetworkManager networkManager;

    private Flicker(Context context) {
        Config cfg = new Config();
        networkManager = new NetworkManager(context, new Handler(), cfg);
    }

    public interface GalleryCallback {
        public void run(ArrayList<GalleryItem> items);
    }
    public void getGallery(String search, final GalleryCallback callback) {
        NetworkRequest nr = new NetworkRequest(
            search == null ? "https://api.flickr.com/services/rest/?method=flickr.photos.getRecent&api_key=edddd386d179232120b943f36c7f9809&format=json&nojsoncallback=1" :
                String.format("https://api.flickr.com/services/rest/?method=flickr.photos.search&api_key=8be87d1f7faf94e7c4b4c5f005f5ceee&text=%s&format=json&nojsoncallback=1", search));
        networkManager.executeAsync(nr, new NetworkManager.StringRunnable() {
            @Override
            public void run(String s, NetworkResponse networkResponse) {
                ArrayList<GalleryItem> items = new ArrayList<GalleryItem>();
                if (networkResponse.isValid()) {
                    try {
                        JSONObject json = new JSONObject(s);
                        JSONObject photosObject = json.getJSONObject("photos");
                        JSONArray photosArr = photosObject.getJSONArray("photo");
                        for (int i = 0; i < photosArr.length(); i++) {
                            JSONObject photoObject = photosArr.getJSONObject(i);
                            GalleryItem item = new GalleryItem(photoObject);
                            items.add(item);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                callback.run(items);
            }
        });
    }

    public interface GalleryImageCallback {
        public void run(Bitmap image);
    }
    public void getImage(GalleryItem item, final GalleryImageCallback callback) {
        NetworkRequest nr = new NetworkRequest(String.format("https://farm%s.staticflickr.com/%s/%s_%s.jpg", item.getFarmId(), item.getServerId(), item.getId(), item.getSecret()));
        nr.setCacheTimeout(60 * 60 * 1000);
        networkManager.executeAsync(nr, new NetworkManager.ImageRunnable() {
            @Override
            public void run(Bitmap bitmap, NetworkResponse networkResponse) {
                callback.run(bitmap);
            }
        });
    }
    public void getImage(String url, final GalleryImageCallback callback) {

        NetworkRequest nr = new NetworkRequest(url);
        nr.setCacheTimeout(60 * 60 * 1000);
        networkManager.executeAsync(nr, new NetworkManager.ImageRunnable() {
            @Override
            public void run(Bitmap bitmap, NetworkResponse networkResponse) {
                callback.run(bitmap);
            }
        });
    }

    public interface ImageOwnerCallback {
        public void run(String owner, String avatar);
    }
    public void getOwnerNameAndAvatar(String ownerId, final ImageOwnerCallback callback) {
        NetworkRequest nr = new NetworkRequest(String.format("https://api.flickr.com/services/rest/?method=flickr.people.getInfo&api_key=edddd386d179232120b943f36c7f9809&user_id=%s&format=json&nojsoncallback=1", ownerId));
        nr.setCacheTimeout(60 * 60 * 1000);
        networkManager.executeAsync(nr, new NetworkManager.StringRunnable() {
            @Override
            public void run(String s, NetworkResponse networkResponse) {
                if (networkResponse.isValid()) {
                    try {
                        JSONObject json = new JSONObject(s);
                        JSONObject person = json.getJSONObject("person");
                        String username = person.getJSONObject("username").getString("_content");
                        if (!person.has("iconfarm") || !person.has("iconserver") || !person.has("nsid")) {
                            callback.run(username, null);
                            return;
                        }
                        String avatarUrl = String.format("http://farm%d.staticflickr.com/%s/buddyicons/%s.jpg",
                                person.getInt("iconfarm"), person.getString("iconserver"), person.getString("nsid"));
                        callback.run(username, avatarUrl);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        callback.run("", null);
                    }
                } else {
                    callback.run("", null);
                }
            }
        });
    }
}
