package com.madisp.trails.data;

import android.os.Parcel;
import android.os.Parcelable;

public final class ServiceState implements Parcelable {
    private RecordRequest recordRequest;

    private ServiceState() {
    }

    public boolean isRecording() {
        return recordRequest != null;
    }

    public static class Builder {
        private RecordRequest recordRequest;

        public Builder() {
        }

        public Builder(ServiceState state) {
        }

        public Builder recording(RecordRequest request) {
            this.recordRequest = request;
            return this;
        }

        public ServiceState build() {
            ServiceState state = new ServiceState();
            state.recordRequest = recordRequest;
            return state;
        }
    }

    @Override
    public int describeContents() {
        return recordRequest.describeContents();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(recordRequest, flags);
    }

    public static final Parcelable.Creator<ServiceState> CREATOR =
            new Parcelable.Creator<ServiceState>() {

        @Override
        public ServiceState createFromParcel(Parcel source) {
            ServiceState state = new ServiceState();
            state.recordRequest = source.readParcelable(RecordRequest.class.getClassLoader());
            return state;
        }

        @Override
        public ServiceState[] newArray(int size) {
            return new ServiceState[0];
        }
    };
}
