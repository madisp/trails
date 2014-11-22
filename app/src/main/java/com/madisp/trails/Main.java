package com.madisp.trails;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.projection.MediaProjectionManager;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.ImageButton;

import com.madisp.trails.data.RecordRequest;
import com.madisp.trails.data.ServiceState;
import com.madisp.trails.recordings.RecordingsFragment;

public class Main extends Activity implements CaptureService.Listener {
    private CaptureService service;
    private ServiceState state;
    private MediaProjectionManager projectionManager;
    private ImageButton recordBtn;

    private ServiceConnection serviceConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            service = ((CaptureService.LocalBinder)binder).getService();
            service.addListener(Main.this);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            service = null;
            stateUpdated(null);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);

        //noinspection ResourceType
        projectionManager = (MediaProjectionManager) getSystemService(MEDIA_PROJECTION_SERVICE);
        bindService(new Intent(this, CaptureService.class), serviceConn, BIND_AUTO_CREATE);

        recordBtn = (ImageButton) findViewById(R.id.record);
        if (savedInstanceState == null) {
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.add(R.id.main, new RecordingsFragment()).commit();
        }
        findViewById(R.id.record).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (state.isRecording()) {
                    service.stop();
                } else {
                    startActivityForResult(projectionManager.createScreenCaptureIntent(),
                            R.id.requestProjection);
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (service != null) {
            service.removeListener(this);
        }
        unbindService(serviceConn);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_CANCELED) {
            return;
        }
        if (requestCode == R.id.requestProjection) {
            service.record(new RecordRequest.Builder(resultCode, data).durationMs(-1).build());
        }
    }

    @Override
    public void stateUpdated(ServiceState state) {
        this.state = state;
        if (service == null || state == null) {
            return;
        }
        if (state.isRecording()) {
            recordBtn.setImageResource(R.drawable.ic_stop_white);
        } else {
            recordBtn.setImageResource(R.drawable.ic_videocam_white);
        }
    }
}
