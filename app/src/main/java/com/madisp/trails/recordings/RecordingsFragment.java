package com.madisp.trails.recordings;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Loader;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.madisp.trails.R;
import com.madisp.trails.data.Recording;

import java.io.File;
import java.util.List;

public class RecordingsFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<Recording>>, RecordingsAdapter.Callback {
    private RecyclerView recycler;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.recordings_list, container, false);
        recycler = (RecyclerView) v.findViewById(R.id.recordingsList);
        recycler.setLayoutManager(new LinearLayoutManager(getActivity()));

        File path = getActivity().getExternalFilesDir("recorded");
        Bundle args = new Bundle();
        args.putString("path", path.getAbsolutePath());
        getLoaderManager().initLoader(R.id.recordingsLoader, args, this);

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
        File path = getActivity().getExternalFilesDir("recorded");
        Bundle args = new Bundle();
        args.putString("path", path.getAbsolutePath());
        getLoaderManager().restartLoader(R.id.recordingsLoader, args, this);
    }
}
