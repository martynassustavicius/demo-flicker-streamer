package lt.sm.demoflickerstreamer;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import lt.smtools.Config;
import lt.smtools.network.NetworkManager;
import lt.smtools.utils.ImageHelper;
import lt.smtools.utils.L;

/**
 * Created by Martynas on 2014-07-05.
 */
public class GalleryAdapter extends BaseAdapter {

    private ArrayList<GalleryItem> items;
    private Context context;
    private NetworkManager networkManager;
    private String search;

    public GalleryAdapter(ArrayList<GalleryItem> items, Context context, String search) {
        Config cfg = new Config();
        networkManager = new NetworkManager(context, new Handler(), cfg);
        this.items = items != null ? items : new ArrayList<GalleryItem>();
        this.context = context;
        this.search = search;
    }

    @Override
    public int getCount() {
        return items.size() / 4 + (items.size() % 4 == 0 ? 0 : 1);
    }

    @Override
    public Object getItem(int i) {
        return items.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = View.inflate(context, R.layout.view_feed_item, null);
        }
        int startPos = i * 4;
        setupImage(view, R.id.pb_top, R.id.text_top_author, R.id.text_top_title, R.id.image_top, startPos);
        setupImage(view, R.id.pb_left, R.id.text_left_author, R.id.text_left_title, R.id.image_left, startPos + 1);
        setupImage(view, R.id.pb_right_top, R.id.text_right_top_author, R.id.text_right_top_title, R.id.image_right_top, startPos + 2);
        setupImage(view, R.id.pb_right_bottom, R.id.text_right_bottom_author, R.id.text_right_bottom_title, R.id.image_right_bottom, startPos + 3);
        return view;
    }

    public void setupImage(final View view, int progressBar, int author, int title, int image, final int pos) {
        if (view == null) {
            return;
        }
        final ProgressBar pb = (ProgressBar) view.findViewById(progressBar);
        final ImageView img = (ImageView) view.findViewById(image);
        final TextView textAuthor = (TextView) view.findViewById(author);
        textAuthor.setText("");
        img.setOnClickListener(null);
        img.setImageBitmap(null);
        ((TextView) view.findViewById(title)).setText("");
        pb.setVisibility(View.GONE);
        if (pos % 4 == 0) {
            ((ImageView) view.findViewById(R.id.image_avatar)).setVisibility(View.GONE);
        }
        if (items.size() <= pos) {
            return;
        }
        pb.setVisibility(View.VISIBLE);
        final GalleryItem item = items.get(pos);
        ((TextView) view.findViewById(title)).setText(item.getTitle());
        img.setTag(item.getTitle());
        Flicker.get(context).getImage(item, new Flicker.GalleryImageCallback() {
            @Override
            public void run(final Bitmap image) {
                if (img.getTag().equals(item.getTitle()) && image != null) {
                    img.setImageBitmap(image);
                    img.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(context, PreviewActivity.class);
                            ByteArrayOutputStream stream = new ByteArrayOutputStream();
                            image.compress(Bitmap.CompressFormat.PNG, 100, stream);
                            intent.putExtra(PreviewActivity.PARAM_IMAGE, stream.toByteArray());
                            context.startActivity(intent);
                        }
                    });
                    pb.setVisibility(View.GONE);
                }
            }
        });
        textAuthor.setTag(item.getTitle());
        Flicker.get(context).getOwnerNameAndAvatar(item.getOwnerId(), new Flicker.ImageOwnerCallback() {
            @Override
            public void run(String owner, final String url) {
                if (textAuthor.getTag().equals(item.getTitle())) {
                    textAuthor.setText(owner);
                    if (url != null && pos % 4 == 0) {
                        final ImageView avatar = (ImageView) view.findViewById(R.id.image_avatar);
                        avatar.setTag(url);
                        Flicker.get(context).getImage(url, new Flicker.GalleryImageCallback() {
                                    @Override
                                    public void run(Bitmap image) {
                                        if (avatar.getTag().equals(url) && image != null) {
                                            avatar.setVisibility(View.VISIBLE);
                                            image = ImageHelper.resize(image, 128, 128);
                                            avatar.setImageBitmap(ImageHelper.applyCircleMask(image, 3, Color.WHITE));
                                        }
                                    }
                                }
                        );
                    }
                }
            }
        });
    }
}
