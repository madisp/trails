package com.madisp.trails.data;

import android.content.Intent;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Parcel;
import android.os.Parcelable;

public final class RecordRequest implements Parcelable {
    private long durationMs; // -1 means indefinitely
    private int resultCode;
    private Intent projectionIntent;

    private RecordRequest() {
    }

    public long getDurationMs() {
        return durationMs;
    }

    public MediaProjection getProjection(MediaProjectionManager manager) {
        return manager.getMediaProjection(resultCode, projectionIntent);
    }

    public static class Builder {
        private long durationMs = -1;
        private final int resultCode;
        private Intent projectionIntent;

        public Builder(int resultCode, Intent intent) {
            this.resultCode = resultCode;
            this.projectionIntent = intent;
        }

        public Builder durationMs(long millis) {
            this.durationMs = millis;
            return this;
        }

        public RecordRequest build() {
            RecordRequest request = new RecordRequest();
            request.durationMs = durationMs;
            request.resultCode = resultCode;
            request.projectionIntent = projectionIntent;
            return request;
        }
    }

    @Override
    public int describeContents() {
        // just in case, no idea what projectionIntent may contain
        return projectionIntent.describeContents();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(durationMs);
        dest.writeInt(resultCode);
        dest.writeParcelable(projectionIntent, flags);
    }

    public static final Parcelable.Creator<RecordRequest> CREATOR =
            new Parcelable.Creator<RecordRequest>() {

        @Override
        public RecordRequest createFromParcel(Parcel source) {
            RecordRequest request = new RecordRequest();
            request.durationMs = source.readLong();
            request.resultCode = source.readInt();
            request.projectionIntent = source.readParcelable(Intent.class.getClassLoader());
            return request;
        }

        @Override
        public RecordRequest[] newArray(int size) {
            return new RecordRequest[size];
        }
    };
}
