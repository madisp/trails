package com.madisp.trails;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.madisp.trails.data.RecordRequest;
import com.madisp.trails.data.ServiceState;

import java.util.HashSet;
import java.util.Set;

public class CaptureService extends Service {
    private final Set<Listener> listeners = new HashSet<>();
    private ServiceState state = new ServiceState.Builder().build();

    public class LocalBinder extends Binder {
        public CaptureService getService() {
            return CaptureService.this;
        }
    }

    public interface Listener {
        void stateUpdated(ServiceState state);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new LocalBinder();
    }

    public void record(RecordRequest request) {
        // start self
        // show ongoing notification
        state = new ServiceState.Builder(state).recording(true).build();
        notifyStateChange();
    }

    public void stop() {
        state = new ServiceState.Builder(state).recording(false).build();
        notifyStateChange();
    }

    public void addListener(Listener listener) {
        synchronized (listeners) {
            listener.stateUpdated(state);
            listeners.add(listener);
        }
    }

    public void removeListener(Listener listener) {
        synchronized (listeners) {
            listeners.remove(listener);
        }
    }

    private void notifyStateChange() {
        synchronized (listeners) {
            for (Listener l : listeners) {
                l.stateUpdated(state);
            }
        }
    }
}
