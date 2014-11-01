package com.madisp.trails;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;

public class Main extends Activity {
	private CaptureService service;
	private MediaProjectionManager projectionManager;

	private ServiceConnection serviceConn = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName name, IBinder binder) {
			service = ((CaptureService.LocalBinder)binder).getService();
			reveal();
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {

		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.main);

		//noinspection ResourceType
		projectionManager = (MediaProjectionManager) getSystemService(MEDIA_PROJECTION_SERVICE);
		bindService(new Intent(this, CaptureService.class), serviceConn, BIND_AUTO_CREATE);

		hide();
		findViewById(R.id.record).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivityForResult(projectionManager.createScreenCaptureIntent(), 0);
			}
		});
	}

	public void reveal() {
		// service is connected, we're good to go
	}

	public void hide() {
		// service has disconnected, clear the views
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		MediaProjection projection = projectionManager.getMediaProjection(resultCode, data);
		service.record(projection);
	}
}
