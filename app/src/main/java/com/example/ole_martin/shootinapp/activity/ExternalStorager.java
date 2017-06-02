package com.example.ole_martin.shootinapp.activity;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by ole-martin on 02.06.2017.
 */

public class ExternalStorager {

    public static void saveScoreToExternal(Context context, String result) {

        String state = Environment.getExternalStorageState();

        if (Environment.MEDIA_MOUNTED.equals(state)) {
            File root = Environment.getExternalStorageDirectory();
            File dir = new File(root.getAbsolutePath() + "/resultater");
            if (!dir.exists()) {
                dir.mkdir();
            }
            File file = new File(dir, "resultater.txt");
            try {
                FileOutputStream fileOutputStream = new FileOutputStream(file, true);
                fileOutputStream.write(result.getBytes());
                Toast.makeText(context, "Lagret i " + file.getAbsolutePath(), Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                String s = e.toString();
            }
        } else

        {
            Toast.makeText(context, "Har ikke tillatelse å lagre til SD kort", Toast.LENGTH_LONG).show();
        }


    }
}
