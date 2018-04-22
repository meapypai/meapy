package project.meapy.meapy.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;

import static com.google.android.gms.internal.zzahn.runOnUiThread;

/**
 * Created by yassi on 09/04/2018.
 */

public class RetrieveImage {

    public static void glide(StorageReference ref, final Context context, final ImageView imageView) {
        Glide.with(context).using(new FirebaseImageLoader()).load(ref).asBitmap().centerCrop().into(new BitmapImageViewTarget(imageView) {
            @Override
            protected void setResource(Bitmap resource) {
                final RoundedBitmapDrawable circularBitmapDrawable =
                        RoundedBitmapDrawableFactory.create(context.getResources(), resource);
                circularBitmapDrawable.setCircular(true);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        imageView.setImageDrawable(circularBitmapDrawable);
                    }
                });
            }
        });
    }
}
