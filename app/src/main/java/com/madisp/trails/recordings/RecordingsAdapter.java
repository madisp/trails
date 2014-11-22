package com.madisp.trails.recordings;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.madisp.trails.App;
import com.madisp.trails.R;
import com.madisp.trails.data.Recording;

import java.util.List;

public class RecordingsAdapter extends RecyclerView.Adapter<RecordingsAdapter.ViewHolder> {

    public interface Callback {
        void onShare(Recording rec);
        void onView(Recording rec);
        void onDelete(Recording rec);
    }

    private final List<Recording> recordings;
    private final Callback callback;

    public RecordingsAdapter(List<Recording> recordings, Callback callback) {
        this.recordings = recordings;
        this.callback = callback;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        LayoutInflater inflater = (LayoutInflater) viewGroup.getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.recording, viewGroup, false);
        return new ViewHolder(v, callback);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        viewHolder.bind(recordings.get(i));
    }

    @Override
    public int getItemCount() {
        return recordings.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final Button share;
        private final Button delete;
        private final TextView text;
        private final ImageView image;
        private final int imgWidth;
        private final int imgHeight;
        private final Callback callback;

        private Recording recording;

        public ViewHolder(View v, Callback callback) {
            super(v);

            this.callback = callback;

            text = (TextView) v.findViewById(R.id.name);
            image = (ImageView) v.findViewById(R.id.thumb);
            share = (Button) v.findViewById(R.id.share);
            delete = (Button) v.findViewById(R.id.delete);

            DisplayMetrics dm = v.getResources().getDisplayMetrics();
            // shave horizontal paddings
            imgWidth = dm.widthPixels - v.getResources().getDimensionPixelSize(R.dimen.totalHorizPadding);
            imgHeight = v.getResources().getDimensionPixelSize(R.dimen.thumbHeight);
        }

        public void bind(final Recording recording) {
            this.recording = recording;
            text.setText(recording.getName());
            App.picasso.load(recording.getPicassouri()).resize(imgWidth, imgHeight)
                    .centerCrop().into(image);
            share.setOnClickListener(this);
            image.setOnClickListener(this);
            delete.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (v == share) {
                callback.onShare(recording);
            } else if (v == image) {
                callback.onView(recording);
            } else if (v == delete) {
                callback.onDelete(recording);
            }
        }
    }
}
