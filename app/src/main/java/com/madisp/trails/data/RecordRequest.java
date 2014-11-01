package com.madisp.trails.data;

import android.content.Intent;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;

public final class RecordRequest {
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
}
