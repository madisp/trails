package com.madisp.trails;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.madisp.trails.data.RecordRequest;

public class CaptureService extends Service {
    public class LocalBinder extends Binder {
        public CaptureService getService() {
            return CaptureService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new LocalBinder();
    }

    public void record(RecordRequest request) {
        // start self
        // show ongoing notification
    }
}
