package laserheightcalculator.trees.nickmcummins.com.lasertreeheightcalculator.storage;

import android.os.Environment;
import android.util.Log;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class FileStorage {
    private static final String LOG_TAG = "LaserTreeHeight";
    private static final String TREES_FILE_STORE = "Trees.txt";

    public static void addTree(
            double latitude,
            double longitude,
            double distanceToHeight,
            double distanceToBase,
            double calculatedHeight)
    {
        String dateString = new SimpleDateFormat("EEE MMM dd hh:mm:ss yyyy").format(new Date());
        String line = String.format(Locale.getDefault(), "%s\t(%f,%f)\t%f\t%f,%f",
                dateString, latitude, longitude, calculatedHeight, distanceToBase, distanceToHeight);
        writeToFile(line);

    }

    public static void writeToFile(String line) {
        File path = getExternalStorageDir();
        String treesFileStore = String.format("%s/%s", path.getAbsolutePath(), TREES_FILE_STORE);
        String currentContents;
        try {
            currentContents = FileUtils.fileRead(treesFileStore);
        } catch (IOException e) {
            currentContents = "";
            Log.i(LOG_TAG, "Exception reading trees file store", e);
        }
        try {
            FileUtils.fileWrite(treesFileStore, String.format("%s\n%s", currentContents, line));
        } catch (Exception e) {
            Log.e(LOG_TAG, "Exception writing line entry to file storage", e);
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
