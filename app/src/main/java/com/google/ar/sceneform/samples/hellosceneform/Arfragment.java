package com.google.ar.sceneform.samples.hellosceneform;

import com.google.ar.core.Config;
import com.google.ar.core.Frame;
import com.google.ar.core.Session;
import com.google.ar.sceneform.FrameTime;
import com.google.ar.sceneform.ux.ArFragment;


public class Arfragment extends ArFragment {
    FrameListener listener = null;

    public interface FrameListener {
        void onFrame(FrameTime frameTime, Frame frame);
    }

    @Override
    public void onUpdate(FrameTime frameTime) {
        super.onUpdate(frameTime);

        Frame arFrame = getArSceneView().getArFrame();
        if (listener != null) {
            listener.onFrame(frameTime, arFrame);
        }
    }

    public void setOnFrameListener(FrameListener listener) {
        this.listener = listener;
    }

    @Override
    protected Config getSessionConfiguration(Session session) {
        Config config = new Config(session);
        config.setLightEstimationMode(Config.LightEstimationMode.ENVIRONMENTAL_HDR);
        return config;
    }

}
