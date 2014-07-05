package lt.sm.demoflickerstreamer;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.ortiz.touch.TouchImageView;

import lt.smtools.utils.ImageHelper;

/**
 * Created by Martynas on 2014-07-05.
 */
public class PreviewActivity extends Activity {

    public static final String PARAM_IMAGE = "param_image";

    @Override public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Bitmap img = ImageHelper.convert(getIntent().getExtras().getByteArray(PARAM_IMAGE));
        TouchImageView tv = new TouchImageView(this);
        tv.setImageBitmap(img);
        tv.setBackgroundColor(Color.BLACK);
        setContentView(tv);
    }
}
