package com.madisp.trails.data;

public final class ServiceState {
    private boolean recording;

    private ServiceState() {
    }

    public boolean isRecording() {
        return recording;
    }

    public static class Builder {
        private boolean recording = false;

        public Builder() {
        }

        public Builder(ServiceState state) {
            this.recording = state.recording;
        }

        public Builder recording(boolean recording) {
            this.recording = recording;
            return this;
        }

        public ServiceState build() {
            ServiceState state = new ServiceState();
            state.recording = recording;
            return state;
        }
    }
}
