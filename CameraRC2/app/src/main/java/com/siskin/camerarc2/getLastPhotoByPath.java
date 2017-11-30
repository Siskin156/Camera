package com.siskin.camerarc2;

import android.database.Cursor;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.text.TextUtils;

import java.io.File;

/**
 * Created by header on 2017/11/29.
 */

public class getLastPhotoByPath {
    public static String getLastPhotoByPath(Fragment fragment) {
        String path="/storage/emulated/0/DCIM/Camera";


        if (!TextUtils.isEmpty(path)) {
            path=String.valueOf(path.toLowerCase().hashCode());
        }else {
            String cameraPath= Environment.getExternalStorageDirectory().toString() + "/DCIM/Camera";
            path=String.valueOf(cameraPath.toLowerCase().hashCode());
        }
        Cursor myCursor = null;
        String pathLast = "";
        // Create a Cursor to obtain the file Path for the large image
        String[] largeFileProjection = {
                MediaStore.Images.ImageColumns._ID,
                MediaStore.Images.ImageColumns.DATA,
                MediaStore.Images.ImageColumns.ORIENTATION,
                MediaStore.Images.ImageColumns.DATE_TAKEN};
        String largeFileSort = MediaStore.Images.ImageColumns._ID + " DESC";
        myCursor =
//                  BaseApplication.getInstance().
                fragment.getActivity().getContentResolver().query(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        largeFileProjection, null, null, largeFileSort);

        if (myCursor.getCount() < 1) {
            myCursor.close();
            return pathLast;
        }
        while (myCursor.moveToNext()) {
            String data = myCursor.getString(myCursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA));
            File f = new File(data);
            if (f.exists()) {//第一个图片文件，就是最近一次拍照的文件；
                pathLast = f.getPath();
               // System.out.println("f.getPath() = " + pathLast);
                myCursor.close();
                return pathLast;
            }
        }
        myCursor.close();
        return pathLast;

    }

}
