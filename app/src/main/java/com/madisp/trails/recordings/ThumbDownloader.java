package com.madisp.trails.recordings;

import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;

import com.squareup.picasso.Downloader;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class ThumbDownloader implements Downloader {
    @Override
    public Response load(Uri uri, boolean localCacheOnly) throws IOException {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            // need to unescape the scheme
            URI unescaped = new URI(uri.toString().replace("http://", "file:///"));
            retriever.setDataSource(new File(unescaped).getAbsolutePath());
            Bitmap bitmap = retriever.getFrameAtTime(8 * 1000 * 1000); // 8 seconds into the video
            return new Response(bitmap, false, -1);
        } catch (URISyntaxException e) {
            throw new IOException(e);
        }
    }
}
