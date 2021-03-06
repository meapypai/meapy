package project.meapy.meapy.utils;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.DocumentsContract;
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
    public  String getPathFromUri(Uri uri) {
        String path = "";
        String autority = uri.getAuthority();

        if (DocumentsContract.isDocumentUri(context, uri)) {
            if (autority.equals("com.android.providers.media.documents")) {
                String[] split = uri.getLastPathSegment().split(":"); //on recupere le dernier segment de l'uri ( image:48414)
                String type = split[0];
                String id = split[1];

                //si le  document est une image
                if (type.equals("image")) {
                    path = getData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new String[]{MediaStore.Images.Media.DATA}, "_id=?", new String[]{id});
                }
            }
            else if (autority.equals("com.android.providers.downloads.documents")) {
                String idDoc = DocumentsContract.getDocumentId(uri); //on récupère l'id du document récupéré
                Uri uri2 = Uri.withAppendedPath(Uri.parse("content://downloads/public_downloads"), idDoc);
                path = getData(uri2, new String[]{"_data"}, null, null);
            }
            else if (autority.equals("com.android.externalstorage.documents")) {
                String docId = DocumentsContract.getDocumentId(uri);
                String[] split = docId.split(":");
                String type = split[0];
//                path =  Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + uri.
                Toast.makeText(context,"Ajout de fichier depuis la carte non possible pour le moment",Toast.LENGTH_LONG).show();
            }
            return path;
        }
        return null;
    }

    public String getData(Uri uri,String[] columns, String selection,String[] args) {
        String res = "";
        Cursor cursor = context.getContentResolver().query(uri,new String[]{"_data"},selection,args,null);
        if(cursor.moveToNext()) {
            res = cursor.getString(0);
        }
        return res;
    }
}
