package lt.sm.demoflickerstreamer;

import org.json.JSONObject;

import lt.smtools.models.JsonModel;

/**
 * Created by Martynas on 2014-07-05.
 */
public class GalleryItem extends JsonModel {

    public GalleryItem(JSONObject json) {
        super(json);
    }

    public String getId() {
        return getString("id");
    }

    public String getFarmId() {
        return getString("farm");
    }

    public String getServerId() {
        return getString("server");
    }

    public String getSecret() {
        return getString("secret");
    }

    public String getTitle() { return getString("title"); }

    public String getOwnerId() { return getString("owner"); }
}
