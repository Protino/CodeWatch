package io.github.protino.codewatch.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v4.content.FileProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import io.github.protino.codewatch.R;

/**
 * For more details check out the helpful  -
 * <a href="http://stackoverflow.com/a/30172792/3225001">StackOverflow answer</a>
 *
 * @author Gurupad Mamadapur
 */
public final class FileProviderUtils {

    private static final String CACHE_DIR_NAME = "images";

    /**
     * Image file name is set to constant to overwrite new images
     * and hence save space
     */
    private static final String IMAGE_EXTENSION = ".png";

    private static final String FILE_PROVIDER_AUTHORITY = "io.github.protino.codewatch.fileprovider";

    public static void shareBitmap(Context context, Bitmap bitmap) throws IOException {
        final String imageName = "/"+String.valueOf(System.currentTimeMillis());

        //First save the bitmap to cache directory
        File directory = new File(context.getCacheDir(), CACHE_DIR_NAME);
        if (!directory.mkdirs()) {
            //delete all data under this folder
            deleteRecursive(directory);
            //recreate the directory
            directory = new File(context.getCacheDir(), CACHE_DIR_NAME);
            directory.mkdirs();
        }
        FileOutputStream stream = new FileOutputStream(directory + imageName + IMAGE_EXTENSION);
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        stream.flush();
        stream.close();

        //Now create uri through FileProvider
        File newFile = new File(directory, imageName + IMAGE_EXTENSION);
        Uri uri = FileProvider.getUriForFile(context, FILE_PROVIDER_AUTHORITY, newFile);

        if (uri != null) {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_SEND);
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(uri, context.getContentResolver().getType(uri));
            intent.setType("image/png");
            intent.putExtra(Intent.EXTRA_STREAM, uri);
            context.startActivity(Intent.createChooser(intent, context.getString(R.string.choose_app)));
        }
    }

    private static void deleteRecursive(File directory) {
        if (directory.isDirectory()) {
            for (File child : directory.listFiles()) {
                deleteRecursive(child);
            }
        }
        directory.delete();
    }
}
