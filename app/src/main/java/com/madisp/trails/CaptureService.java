package com.madisp.trails;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.hardware.display.VirtualDisplay;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.view.Surface;

import com.madisp.trails.data.RecordRequest;
import com.madisp.trails.data.ServiceState;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashSet;
import java.util.Set;

public class CaptureService extends Service {
    private final Set<Listener> listeners = new HashSet<>();
    private ServiceState state = new ServiceState.Builder().build();
    private Handler uiHandler;

    private MediaProjectionManager projectionManager;
    private MediaProjection projection;
    private boolean running = false;

    public CaptureService() {
        uiHandler = new Handler(Looper.getMainLooper());
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //noinspection ResourceType
        this.projectionManager = (MediaProjectionManager) getSystemService(MEDIA_PROJECTION_SERVICE);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new LocalBinder();
    }

    public void record(RecordRequest request) {
        // notify about state change
        state = new ServiceState.Builder(state).recording(request).build();
        notifyStateChange();
        // start self
        startService(new Intent(this, getClass()));
        // show ongoing notification
        Notification.Builder builder = new Notification.Builder(this);
        builder.setOngoing(true);
        builder.setSmallIcon(android.R.drawable.stat_notify_more);
        builder.setContentTitle("Trails recording in progress");
        startForeground(R.id.notification, builder.build());
        // kick off the recording
        projection = request.getProjection(projectionManager);
        record();
        // stop after duration passes
        if (request.getDurationMs() > 0) {
            uiHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    stop();
                }
            }, request.getDurationMs());
        }
    }

    private void record() {
        DisplayMetrics metrics = getResources().getDisplayMetrics();

        int width = 720;
        int height = 1280;

        int flags = 0;
        try {

            MediaFormat format = MediaFormat.createVideoFormat("video/avc", width, height);
            format.setInteger(MediaFormat.KEY_BIT_RATE, 8000000);
            format.setInteger(MediaFormat.KEY_BITRATE_MODE, MediaCodecInfo.EncoderCapabilities.BITRATE_MODE_CBR);
            format.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface);
            format.setFloat(MediaFormat.KEY_FRAME_RATE, 30.0f);
            format.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 15);

            final MediaCodec avc = MediaCodec.createEncoderByType("video/avc");
            avc.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
            final Surface surface = avc.createInputSurface();
            avc.start();

            File out = new File(getExternalFilesDir("media"), "asdf.mp4");
            if (out.exists()) {
                out.delete();
            }
            final MediaMuxer muxer = new MediaMuxer(out.getAbsolutePath(), MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);

            running = true;

            new Thread(new Runnable() {
                @Override
                public void run() {
                    MediaCodec.BufferInfo info = new MediaCodec.BufferInfo();
                    int track = -1;
                    while (running) {
                        int index = avc.dequeueOutputBuffer(info, 10000);
                        if (index == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                            if (track != -1) {
                                throw new RuntimeException("format changed twice");
                            }
                            track = muxer.addTrack(avc.getOutputFormat());
                            muxer.start();
                        } else if (index >= 0) {
                            if ((info.flags & MediaCodec.BUFFER_FLAG_CODEC_CONFIG) != 0) {
                                // ignore codec config
                                info.size = 0;
                            }
                            if (track != -1) {
                                ByteBuffer out = avc.getOutputBuffer(index);
                                out.position(info.offset);
                                out.limit(info.offset + info.size);
                                muxer.writeSampleData(track, out, info);
                                avc.releaseOutputBuffer(index, false);
                            }
                        }
                    }
                    avc.stop();
                    avc.release();
                    projection.stop();
                    surface.release();
                    muxer.stop();
                    muxer.release();
                }
            }).start();

            projection.createVirtualDisplay("trails", width, height, metrics.densityDpi, flags, surface, new VirtualDisplay.Callback() {}, uiHandler);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        // stop recording
        running = false;
        // notify state change
        state = new ServiceState.Builder(state).recording(null).build();
        notifyStateChange();
        // dismiss notification
        stopForeground(true);
        // stop self
        stopSelf();
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

    public class LocalBinder extends Binder {
        public CaptureService getService() {
            return CaptureService.this;
        }
    }

    public interface Listener {
        void stateUpdated(ServiceState state);
    }

    private void notifyStateChange() {
        synchronized (listeners) {
            for (Listener l : listeners) {
                l.stateUpdated(state);
            }
        }
    }
}
