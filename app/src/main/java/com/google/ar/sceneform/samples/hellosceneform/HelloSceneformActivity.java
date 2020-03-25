/*
 * Copyright 2018 Google LLC. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.ar.sceneform.samples.hellosceneform;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.widget.TextView;
import android.widget.Toast;
import com.google.ar.core.Anchor;
import com.google.ar.core.HitResult;
import com.google.ar.core.Plane;
import com.google.ar.core.Pose;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.TransformableNode;

/**
 * This is an example activity that uses the Sceneform UX package to make common AR tasks easier.
 */
public class HelloSceneformActivity extends AppCompatActivity {
    private static final String TAG = HelloSceneformActivity.class.getSimpleName();
    private static final double MIN_OPENGL_VERSION = 3.0;

    private ArFragment arFragment;
    private ModelRenderable andyRenderable;
    private ModelRenderable andyRenderable1;
    private Pose pose1;
    private Pose pose2;
    private int tapnb = 1;
    private AnchorNode anchorNode1;
    private AnchorNode anchorNode2;
    private TransformableNode andy1;
    private TransformableNode andy2;



    @Override
    @SuppressWarnings({"AndroidApiChecker", "FutureReturnValueIgnored"})
    // CompletableFuture requires api level 24
    // FutureReturnValueIgnored is not valid
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Toast.makeText(this, "test3", Toast.LENGTH_LONG)
                .show();

        if (!checkIsSupportedDeviceOrFinish(this)) {
            return;
        }


        setContentView(R.layout.activity_ux);
        arFragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.ux_fragment);

        // When you build a Renderable, Sceneform loads its resources in the background while returning
        // a CompletableFuture. Call thenAccept(), handle(), or check isDone() before calling get().
        ModelRenderable.builder()
                .setSource(this, R.raw.andy)
                .build()
                .thenAccept(renderable -> andyRenderable = renderable)
                .exceptionally(
                        throwable -> {
                            Toast toast =
                                    Toast.makeText(this, "Unable to load andy renderable", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            return null;
                        });

        ModelRenderable.builder()
                .setSource(this, R.raw.andy1)
                .build()
                .thenAccept(renderable -> andyRenderable1 = renderable)
                .exceptionally(
                        throwable -> {
                            Toast toast =
                                    Toast.makeText(this, "Unable to load andy renderable", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            return null;
                        });

        TextView txtv = findViewById(R.id.textView);
        txtv.setText("test dist");


        arFragment.setOnTapArPlaneListener(
                (HitResult hitResult, Plane plane, MotionEvent motionEvent) -> {
                    if (andyRenderable == null) {
                        return;
                    }

                    Log.e("TEST", "metres");



                    switch (tapnb) {
                        case 1:
                            // Create the Anchor.
                            Anchor anchor1 = hitResult.createAnchor();
                            if (anchorNode1 == null) {
                                // Create the Anchor.
                                anchorNode1 = new AnchorNode(anchor1);
                                anchorNode1.setParent(arFragment.getArSceneView().getScene());
                                // Create the transformable andy and add it to the anchor.
                                andy1 = new TransformableNode(arFragment.getTransformationSystem());
                                andy1.setParent(anchorNode1);
                                andy1.setRenderable(andyRenderable);
                                andy1.select();
                                anchorNode1.addTransformChangedListener(
                                        (Node node, Node originatingNode) -> {
                                            pose1 = ((AnchorNode) node).getAnchor().getPose();
                                            calculedistance();
                                        }
                                );
                            }
                            else
                                anchorNode1.setAnchor(anchor1);
                            pose1 = anchorNode1.getAnchor().getPose();
                            Log.e("11111", "11111");
                            tapnb = 2;
                            break;

                        case 2:
                            Anchor anchor2 = hitResult.createAnchor();
                            if (anchorNode2 == null) {
                                anchorNode2 = new AnchorNode(anchor2);
                                anchorNode2.setParent(arFragment.getArSceneView().getScene());
                                andy2 = new TransformableNode(arFragment.getTransformationSystem());
                                andy2.setParent(anchorNode2);
                                andy2.setRenderable(andyRenderable1);
                                andy2.select();
                                anchorNode2.addTransformChangedListener(
                                        (Node node, Node originatingNode) -> {
                                            pose2 = ((AnchorNode) node).getAnchor().getPose();
                                            calculedistance();
                                        }
                                );
                            }
                            else
                                anchorNode2.setAnchor(anchor2);
                            pose2 = anchorNode2.getAnchor().getPose();
                            Log.e("11111", "22222");
                            tapnb = 1;
                            break;
                    }

                    calculedistance();

                });


    }

    private void calculedistance() {
        TextView txtv = findViewById(R.id.textView);
        if (pose1 != null && pose2 != null) {
            float dx = pose1.tx() - pose2.tx();
            float dy = pose1.ty() - pose2.ty();
            float dz = pose1.tz() - pose2.tz();

            float distanceMeters = (float) Math.sqrt(dx*dx + dy*dy + dz*dz);
            Toast dist =
                    Toast.makeText(this, distanceMeters + "metres", Toast.LENGTH_LONG);
            dist.setGravity(Gravity.CENTER, 0, 0);
            dist.show();
            txtv.setText(String.valueOf(distanceMeters)+"   "+tapnb);
            Log.e("DISTANCE", distanceMeters+"metres");
        }
    }

    /**
     * Returns false and displays an error message if Sceneform can not run, true if Sceneform can run
     * on this device.
     *
     * <p>Sceneform requires Android N on the device as well as OpenGL 3.0 capabilities.
     *
     * <p>Finishes the activity if Sceneform can not run
     */
    public static boolean checkIsSupportedDeviceOrFinish(final Activity activity) {
        if (Build.VERSION.SDK_INT < VERSION_CODES.N) {
            Log.e(TAG, "Sceneform requires Android N or later");
            Toast.makeText(activity, "Sceneform requires Android N or later", Toast.LENGTH_LONG).show();
            activity.finish();
            return false;
        }
        String openGlVersionString =
                ((ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE))
                        .getDeviceConfigurationInfo()
                        .getGlEsVersion();
        if (Double.parseDouble(openGlVersionString) < MIN_OPENGL_VERSION) {
            Log.e(TAG, "Sceneform requires OpenGL ES 3.0 later");
            Toast.makeText(activity, "Sceneform requires OpenGL ES 3.0 or later", Toast.LENGTH_LONG)
                    .show();
            activity.finish();
            return false;
        }
        return true;
    }
}
