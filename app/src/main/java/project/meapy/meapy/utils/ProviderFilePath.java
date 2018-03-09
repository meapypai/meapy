package project.meapy.meapy.utils;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.Toast;

/**
 * Created by yassi on 06/03/2018.
 */

public class ProviderFilePath {

    private Context context;

    public ProviderFilePath(Context context) {
        this.context = context;
    }

    /**
     *
     * @param uri : the uri of the file
     * @return file's path
     */
    public  String getPath(Uri uri) {
        String path = "";
        String autority = uri.getAuthority();
        Toast.makeText(context,"okkkkk",Toast.LENGTH_SHORT).show();

        if(autority.equals("com.android.providers.media.documents")) {
            String[] split = uri.getLastPathSegment().split(":"); //on recupere le dernier segment de l'uri ( image:48414)
            String type = split[0];
            String id   = split[1];

            System.out.println(type);
            System.out.println(id);

            //si le  document est une image
            if(type.equals("image")) {
                Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new String[] {MediaStore.Images.Media.DATA},"_id=?", new String[]{id},null);
                if(cursor.moveToNext()) {
                    path = cursor.getString(0);
                }
            }
        }
        return path;
    }
}