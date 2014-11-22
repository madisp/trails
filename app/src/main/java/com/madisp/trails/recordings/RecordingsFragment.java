package com.madisp.trails.recordings;

import android.app.Activity;
import android.app.Fragment;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.madisp.trails.CaptureService;
import com.madisp.trails.R;
import com.madisp.trails.data.Recording;
import com.madisp.trails.data.ServiceState;

import java.io.File;
import java.util.List;

public class RecordingsFragment extends Fragment implements LoaderCallbacks<List<Recording>>,
        RecordingsAdapter.Callback, CaptureService.Listener {
    private RecyclerView recycler;

    private CaptureService service;
    private ServiceState state;

    private ServiceConnection serviceConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            service = ((CaptureService.LocalBinder)binder).getService();
            service.addListener(RecordingsFragment.this);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            service = null;
            stateUpdated(null);
        }
    };

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        getActivity().bindService(new Intent(getActivity(), CaptureService.class),
                serviceConn, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (service != null) {
            service.removeListener(this);
        }
        getActivity().unbindService(serviceConn);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.recordings_list, container, false);
        recycler = (RecyclerView) v.findViewById(R.id.recordingsList);
        recycler.setLayoutManager(new LinearLayoutManager(getActivity()));

        getLoaderManager().initLoader(R.id.recordingsLoader, loaderArgs(), this);

        return v;
    }

    @Override
    public Loader<List<Recording>> onCreateLoader(int id, Bundle args) {
        return new RecordingsLoader(getActivity(), new File(args.getString("path")));
    }

    @Override
    public void onLoadFinished(Loader<List<Recording>> loader, List<Recording> data) {
        recycler.swapAdapter(new RecordingsAdapter(data, this), true);
    }

    @Override
    public void onLoaderReset(Loader<List<Recording>> loader) {
        recycler.swapAdapter(null, true);
    }

    @Override
    public void onShare(Recording rec) {
        getActivity().startActivity(rec.getShareIntent(getActivity()));
    }

    @Override
    public void onView(Recording rec) {
        getActivity().startActivity(rec.getViewIntent(getActivity()));
    }

    @Override
    public void onDelete(Recording rec) {
        rec.delete();
        getLoaderManager().restartLoader(R.id.recordingsLoader, loaderArgs(), this);
    }

    @Override
    public void stateUpdated(ServiceState newState) {
        if (state != null && state.isRecording() && !newState.isRecording()) {
            // just finished recording, nudge the loader
            getLoaderManager().restartLoader(R.id.recordingsLoader, loaderArgs(), this);
        }
        state = newState;
    }

    private Bundle loaderArgs() {
        File path = getActivity().getExternalFilesDir("recorded");
        Bundle args = new Bundle();
        args.putString("path", path.getAbsolutePath());
        return args;
    }
}
