package com.madisp.trails;

import android.app.Service;
import android.content.Intent;
import android.media.projection.MediaProjection;
import android.os.Binder;
import android.os.IBinder;

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

	public void record(MediaProjection projection) {
		// start self
		// show ongoing notification
	}
}
