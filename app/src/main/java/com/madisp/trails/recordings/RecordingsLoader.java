package com.madisp.trails.recordings;

import android.content.AsyncTaskLoader;
import android.content.Context;

import com.madisp.trails.data.Recording;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RecordingsLoader extends AsyncTaskLoader<List<Recording>> {
    private File path;

    public RecordingsLoader(Context context, File path) {
        super(context);
        this.path = path;
    }

    @Override
    public List<Recording> loadInBackground() {
        File[] files = path.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String filename) {
                return filename.endsWith("mp4");
            }
        });
        ArrayList<Recording> ret = new ArrayList<>();
        for (File f : files) {
            ret.add(new Recording(f));
        }
        // sort dem files
        Collections.sort(ret);
        return ret;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }
}
