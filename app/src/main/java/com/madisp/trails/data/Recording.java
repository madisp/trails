package com.madisp.trails.data;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;

import java.io.File;

public class Recording implements Parcelable, Comparable<Recording> {
    private final File file;

    public Recording(File f) {
        file = f;
    }

    public String getName() {
        return file.getName();
    }

    public Uri getPicassouri() {
        // escaped to go through Picasso's downloader
        return Uri.parse(Uri.fromFile(file).toString().replace("file:///", "http://"));
    }

    private Uri getContentUri(Context context) {
        return FileProvider.getUriForFile(context, "com.madisp.trails.fileprovider", file);
    }

    public Intent getViewIntent(Context context) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(getContentUri(context));
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
        return intent;
    }

    public Intent getShareIntent(Context context) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_STREAM, getContentUri(context));
        intent.setType("video/*");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
        return intent;
    }

    public boolean delete() {
        return file.delete();
    }

    @Override
    public int compareTo(@NonNull Recording another) {
        long lhs = file.lastModified();
        long rhs = another.file.lastModified();
        return Long.compare(rhs, lhs);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(file.getAbsolutePath());
    }

    public static final Parcelable.Creator<Recording> CREATOR = new Parcelable.Creator<Recording>() {
        @Override
        public Recording createFromParcel(Parcel source) {
            return new Recording(new File(source.readString()));
        }

        @Override
        public Recording[] newArray(int size) {
            return new Recording[size];
        }
    };
}
