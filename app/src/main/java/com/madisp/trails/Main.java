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

import com.madisp.trails.data.RecordRequest;
import com.madisp.trails.data.ServiceState;

public class Main extends Activity implements CaptureService.Listener {
    private CaptureService service;
    private ServiceState state;
    private MediaProjectionManager projectionManager;

    private ServiceConnection serviceConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            service = ((CaptureService.LocalBinder)binder).getService();
            service.addListener(Main.this);
            reveal();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            hide();
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
//                startActivityForResult(projectionManager.createScreenCaptureIntent(), R.id.requestProjection);
                service.record(new RecordRequest.Builder().build());
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        service.removeListener(this);
        unbindService(serviceConn);
    }

    public void reveal() {
        // service is connected, we're good to go
        findViewById(R.id.content).setVisibility(View.VISIBLE);
        findViewById(R.id.loading).setVisibility(View.GONE);
        if (state.isRecording()) {
            findViewById(R.id.record).setVisibility(View.GONE);
            findViewById(R.id.inProgress).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.record).setVisibility(View.VISIBLE);
            findViewById(R.id.inProgress).setVisibility(View.GONE);
        }
    }

    public void hide() {
        // service has disconnected, clear the views
        findViewById(R.id.content).setVisibility(View.GONE);
        findViewById(R.id.loading).setVisibility(View.VISIBLE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == R.id.requestProjection) {
            MediaProjection projection = projectionManager.getMediaProjection(resultCode, data);
            service.record(new RecordRequest.Builder().build());
        }
    }

    @Override
    public void stateUpdated(ServiceState state) {
        this.state = state;
        reveal();
    }
}
