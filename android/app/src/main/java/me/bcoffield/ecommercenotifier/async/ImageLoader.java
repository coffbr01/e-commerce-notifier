package me.bcoffield.ecommercenotifier.async;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import java.io.InputStream;
import java.lang.ref.WeakReference;

public class ImageLoader extends AsyncTask<String, Void, Bitmap> {
    private final WeakReference<ImageView> imageViewRef;

    public ImageLoader(ImageView imageView) {
        super();
        this.imageViewRef = new WeakReference<>(imageView);
    }

    @Override
    protected Bitmap doInBackground(String... urls) {
        Log.i(getClass().getSimpleName(), "starting image load");
        String url = urls[0];
        Bitmap bimage = null;
        try {
            InputStream in = new java.net.URL(url).openStream();
            bimage = BitmapFactory.decodeStream(in);
        } catch (Exception e) {
            Log.e("Error Message", e.getMessage());
            e.printStackTrace();
        }
        return bimage;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        super.onPostExecute(bitmap);
        Log.i(getClass().getSimpleName(), "setting image bitmap");
        imageViewRef.get().setImageBitmap(bitmap);
    }
}
