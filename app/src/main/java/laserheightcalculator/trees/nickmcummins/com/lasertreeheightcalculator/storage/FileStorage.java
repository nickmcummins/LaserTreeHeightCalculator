package laserheightcalculator.trees.nickmcummins.com.lasertreeheightcalculator.storage;

import android.os.Environment;
import android.util.Log;

import org.apache.commons.io.FileUtils;


import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class FileStorage {
    private static final String LOG_TAG = "LaserTreeHeight";

    public static void addTree(
            double latitude,
            double longitude,
            double distanceToHeight,
            double distanceToBase,
            double calculatedHeight)
    {
        String dateString = new SimpleDateFormat("EEE MMM dd hh:mm:ss yyyy").format(new Date());
        String line = String.format(Locale.getDefault(), "%s\t(%f,%f)\t%f\t%f,%f\n",
                dateString, latitude, longitude, calculatedHeight, distanceToBase, distanceToHeight);
        writeToFile(line);

    }

    public static void writeToFile(String line) {
        File path = getExternalStorageDir();
        File file = new File(path, "Trees.txt");
        try {
            path.mkdirs();
            FileUtils.writeStringToFile(file, line, Charset.defaultCharset(), true);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Exception when trying to write to file", e);
        }
    }

    private static File getExternalStorageDir() {
        File file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
        if (!file.mkdirs()) {
            Log.e(LOG_TAG, "Error retrieving public storage directory " + file);
        }
        return file;
    }
}
