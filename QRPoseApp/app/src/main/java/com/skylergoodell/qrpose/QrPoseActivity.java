package com.skylergoodell.qrpose;

import android.graphics.Color;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Toast;

import com.google.ar.core.Anchor;
import com.google.ar.core.ArCoreApk;
import com.google.ar.core.Camera;
import com.google.ar.core.CameraIntrinsics;
import com.google.ar.core.Config;
import com.google.ar.core.Frame;
import com.google.ar.core.HitResult;
import com.google.ar.core.LightEstimate;
import com.google.ar.core.Plane;
import com.google.ar.core.Point;
import com.google.ar.core.PointCloud;
import com.google.ar.core.Pose;
import com.google.ar.core.Session;
import com.google.ar.core.Trackable;
import com.google.ar.core.TrackingState;
import com.google.ar.core.exceptions.CameraNotAvailableException;
import com.google.ar.core.exceptions.UnavailableApkTooOldException;
import com.google.ar.core.exceptions.UnavailableArcoreNotInstalledException;
import com.google.ar.core.exceptions.UnavailableDeviceNotCompatibleException;
import com.google.ar.core.exceptions.UnavailableSdkTooOldException;
import com.google.ar.core.exceptions.UnavailableUserDeclinedInstallationException;
import com.skylergoodell.common.helpers.CameraPermissionHelper;
import com.skylergoodell.common.helpers.DisplayRotationHelper;
import com.skylergoodell.common.helpers.FullScreenHelper;
import com.skylergoodell.common.helpers.SnackbarHelper;
import com.skylergoodell.common.helpers.TapHelper;
import com.skylergoodell.common.helpers.TrackingStateHelper;
import com.skylergoodell.common.qrcode.DetectedQRCode;
import com.skylergoodell.common.qrcode.QRCodeHelper;
import com.skylergoodell.common.qrcode.QRCodeMetadata;
import com.skylergoodell.common.rendering.BackgroundRenderer;
import com.skylergoodell.common.rendering.ObjectRenderer;
import com.skylergoodell.common.rendering.PlaneRenderer;
import com.skylergoodell.common.rendering.PointCloudRenderer;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;

import java.io.IOException;
import java.util.ArrayList;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Application Activity for using QR Codes to measure poses.
 * Activity Lifecycle Reference:
 *      https://developer.android.com/reference/android/app/Activity.html#ActivityLifecycle
 *
 * GLSurfaceView Reference:
 *      https://developer.android.com/reference/android/opengl/GLSurfaceView.Renderer
 */
public class QrPoseActivity extends AppCompatActivity implements GLSurfaceView.Renderer {
    private static final String TAG = QrPoseActivity.class.getSimpleName();
    private static final String SEARCHING_PLANE_MESSAGE = "Searching for surfaces...";

    private final BackgroundRenderer backgroundRenderer = new BackgroundRenderer();
    private final ObjectRenderer objectRenderer = new ObjectRenderer();
    private final PlaneRenderer planeRenderer = new PlaneRenderer();
    private final PointCloudRenderer pointCloudRenderer = new PointCloudRenderer();
    private final SnackbarHelper messageSnackbarHelper = new SnackbarHelper();
    private final TrackingStateHelper trackingStateHelper = new TrackingStateHelper(this);
    private final QRCodeHelper qrCodeHelper = new QRCodeHelper();

    private DisplayRotationHelper displayRotationHelper;
    private GLSurfaceView surfaceView;
    private Session session;
    private TapHelper tapHelper;

    private boolean installRequested;

    private static final float scaleFactor = 1.0f;
    private static final int maxAnchors = 20;

    private static class ColoredAnchor {
        public final Anchor anchor;
        public final float[] color;

        public ColoredAnchor(Anchor a, float[] color4f) {
            this.anchor = a;
            this.color = color4f;
        }
    }

    private static final float[] DEFAULT_COLOR = new float[] {0f, 0f, 0f, 0f};
    private static final float[] BLUE_COLOR = new float[] {66.0f, 133.0f, 244.0f, 255.0f};
    private static final float[] GREEN_COLOR = new float[] {139.0f, 195.0f, 74.0f, 255.0f};
    private final ArrayList<ColoredAnchor> anchors = new ArrayList<ColoredAnchor>();

    /**
     * Called when the activity is first created. This is where you should do all of your normal
     * static set up: create views, bind data to lists, etc. This method also provides you with a
     * Bundle containing the activity's previously frozen state, if there was one.
     * Always followed by onStart().
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_pose);
        surfaceView = findViewById(R.id.surfaceview);
        displayRotationHelper = new DisplayRotationHelper(this);

        // Set up tap listener.
        tapHelper = new TapHelper(this);
        surfaceView.setOnTouchListener(tapHelper);

        // Set up renderer.
        surfaceView.setPreserveEGLContextOnPause(true);
        surfaceView.setEGLContextClientVersion(2);
        surfaceView.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        surfaceView.setRenderer(this);
        surfaceView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
        surfaceView.setWillNotDraw(false);

        installRequested = false;
    }

    /**
     * Called when the activity will start interacting with the user. At this point your activity is
     * at the top of its activity stack, with user input going to it.
     * Always followed by onPause().
     */
    @Override
    protected void onResume() {
        super.onResume();

        if (!OpenCVLoader.initDebug()) {
            Log.e("OpenCV", "Internal OpenCV library not found.");
        } else {
            Log.i("OpenCV", "OpenCV library found inside package. Using it!");
        }

        if (session == null) {
            Exception exception = null;
            String message = null;
            try {
                switch (ArCoreApk.getInstance().requestInstall(this, !installRequested)) {
                    case INSTALLED:
                        break;
                    case INSTALL_REQUESTED:
                        installRequested = true;
                        break;
                }

                // ARCore requires camera permissions to operate. If we did not yet obtain runtime
                // permission on Android M and above, now is a good time to ask the user for it.
                if (!CameraPermissionHelper.hasCameraPermission(this)) {
                    CameraPermissionHelper.requestCameraPermission(this);
                    return;
                }

                session = new Session(this);
                Config config = session.getConfig();
                config.setFocusMode(Config.FocusMode.AUTO);
                config.setDepthMode(Config.DepthMode.DISABLED);
                session.configure(config);
            } catch (UnavailableDeviceNotCompatibleException e) {
                message = "This device does not support AR";
                exception = e;
            } catch (UnavailableUserDeclinedInstallationException |
                     UnavailableArcoreNotInstalledException e) {
                message = "Please install ARCore";
                exception = e;
            } catch (UnavailableSdkTooOldException e) {
                message = "Please update this app";
                exception = e;
            } catch (UnavailableApkTooOldException e) {
                message = "Please update ARCore";
                exception = e;
            }

            if (message != null) {
                messageSnackbarHelper.showError(this, message);
                Log.e(TAG, "Exception creating session", exception);
                return;
            }

            // Order matters - GLSurfaceView must be resumed after the Session. Otherwise,
            // GLSurfaceView may call session.update() and get a SessionPausedException.
            try {
                session.resume();
            } catch (CameraNotAvailableException e) {
                messageSnackbarHelper.showError(this, "Camera not available. Try restarting the app.");
                session = null;
                return;
            }

            surfaceView.onResume();
            displayRotationHelper.onResume();
        }
    }

    /**
     * Called when the activity loses foreground state, is no longer focusable or before transition
     * to stopped/hidden or destroyed state. The activity is still visible to user, so it's
     * recommended to keep it visually active and continue updating the UI. Implementations of this
     * method must be very quick because the next activity will not be resumed until this method
     * returns.
     * Followed by either onResume() if the activity returns back to the front, or onStop() if it
     * becomes invisible to the user.
     */
    @Override
    public void onPause() {
        super.onPause();
        if (session != null)
        {
            // Order matters - GLSurfaceView is paused first so that it does not try
            // to query the session. If Session is paused before GLSurfaceView, GLSurfaceView may
            // still call session.update() and get a SessionPausedException.
            displayRotationHelper.onPause();
            surfaceView.onPause();
            session.pause();
        }
    }

    /**
     * Callback for the result from requesting permissions. This method is invoked for every call on
     * requestPermissions(java.lang.String[], int).
     * @param requestCode The request code passed into requestPermissions(...)
     * @param permissions The requested permissions. Never null.
     * @param results The grant results for the corresponding permissions which is either
     *                PackageManager.PERMISSION_GRANTED or PackageManager.PERMISSION_DENIED.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] results) {
        super.onRequestPermissionsResult(requestCode, permissions, results);
        if (!CameraPermissionHelper.hasCameraPermission(this)) {
            Toast.makeText(this, "Camera permission is needed to run this application", Toast.LENGTH_LONG)
                    .show();
            if (!CameraPermissionHelper.shouldShowRequestPermissionRationale(this)) {
                // Permission denied with checking "Do not ask again".
                CameraPermissionHelper.launchPermissionSettings(this);
            }
            finish();
        }
    }

    /**
     * Called when the current Window of the activity gains or loses focus.
     * This is the best indicator of whether this activity is the entity with which the user actively interacts.
     * @param hasFocus Whether the window of this activity has focus.
     */
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        FullScreenHelper.setFullScreenOnWindowFocusChanged(this, hasFocus);
    }

    /**
     * Called when the surface is created or recreated.
     * @param gl The GL interface.
     * @param config the EGLConfig of the created surface. Can be used to create matching pbuffers.
     */
    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES20.glClearColor(0.1f, 0.1f, 0.1f, 1.0f);

        // Prepare the rendering objects. This involves reading shaders, so may throw an IO Exception.
        try {
            backgroundRenderer.createOnGlThread(this, -1);
            planeRenderer.createOnGlThread(this, "models/trigrid.png");
            pointCloudRenderer.createOnGlThread(this);

            objectRenderer.createOnGlThread(this, "models/pin.obj", "models/pin.png");
            objectRenderer.setBlendMode(ObjectRenderer.BlendMode.AlphaBlending);
            objectRenderer.setMaterialProperties(0.0f, 2.0f, 0.5f, 6.0f);

        } catch (IOException e) {
            Log.e(TAG, "Failed to read an asset file", e);
        }
    }

    /**
     * Called when the surface changed size.
     * @param gl The GL interface.
     * @param width The width of the screen.
     * @param height The height of the screen.
     */
    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        displayRotationHelper.onSurfaceChanged(width, height);
        GLES20.glViewport(0, 0, width, height);
    }

    /**
     * Called to draw the current frame.
     * @param gl The GL interface.
     */
    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        if (session == null) {
            return;
        }

        // Notify ARCore session that the view size changed so that the perspective matrix and
        // the video background can be properly adjusted.
        displayRotationHelper.updateSessionIfNeeded(session);

        try {
            session.setCameraTextureName(backgroundRenderer.getTextureId());

            // Obtain the current frame from ARSession. When the configuration is set to
            // UpdateMode.BLOCKING (it is by default), this will throttle the rendering to the
            // camera framerate.
            Frame frame = session.update();
            Camera camera = frame.getCamera();

            // Handle one tap per frame
            handleTap(frame, camera);

            // If frame is ready, render camera preview image to the GL surface.
            backgroundRenderer.draw(frame);

            // Keep the screen unlocked while tracking, but allow it to lock when tracking stops.
            trackingStateHelper.updateKeepScreenOnFlag(camera.getTrackingState());

            // If not tracking, don't draw 3D objects, show tracking failure reason instead.
            if (camera.getTrackingState() == TrackingState.PAUSED) {
                messageSnackbarHelper.showMessage(this, TrackingStateHelper.getTrackingFailureReasonString(camera));
                return;
            }

            // No tracking error at this point. If we detected any plane, then hide the
            // message UI, otherwise show searchingPlane message.
            if (hasTrackingPlane()) {
                messageSnackbarHelper.hide(this);
            } else {
                messageSnackbarHelper.showMessage(this, SEARCHING_PLANE_MESSAGE);
            }

            // Returns a projection matrix for rendering virtual content on top of the camera image.
            // Note that the projection matrix reflects the current display geometry and display rotation.
            // v: the near clip plane, in meters
            // v1: the far clip plane, in meters
            float[] projmtx = new float[16];
            camera.getProjectionMatrix(projmtx, 0, 0.1f, 100.0f);

            // Returns the view matrix for the camera for this frame. Note that the view matrix
            // incorporates the display orientation.
            // This is equivalent to: camera.getDisplayOrientedPose().inverse().asMatrix()
            float[] viewmtx = new float[16];
            camera.getViewMatrix(viewmtx, 0);

            // Visualize tracked points.
            // Use try-with-resources to automatically release the point cloud.
            try (PointCloud pointCloud = frame.acquirePointCloud()) {
                pointCloudRenderer.update(pointCloud);
                pointCloudRenderer.draw(viewmtx, projmtx);
            }

            // Visualize planes.
            planeRenderer.drawPlanes(session.getAllTrackables(Plane.class), camera.getDisplayOrientedPose(), projmtx);

            // Compute lighting from average intensity of the image.
            // The first three components are color scaling factors.
            // The last one is the average pixel intensity in gamma space.
            final float[] colorCorrectionRgba = new float[4];
            LightEstimate lightEstimate = frame.getLightEstimate();
            lightEstimate.getColorCorrection(colorCorrectionRgba, 0);

            // Visualize anchors.
            for (ColoredAnchor coloredAnchor : anchors) {
                if (coloredAnchor.anchor.getTrackingState() != TrackingState.TRACKING) {
                    continue;
                }

                // Get the current pose of the local anchor in world space. The pose is updated
                // during calls to session.update() as ARCore refines its estimate of the world.
                float[] anchorMatrix = new float[16];
                coloredAnchor.anchor.getPose().toMatrix(anchorMatrix, 0);

                // Update and draw the model
                objectRenderer.updateModelMatrix(anchorMatrix, scaleFactor);
                objectRenderer.draw(viewmtx, projmtx, colorCorrectionRgba, coloredAnchor.color);
            }

            // Check for a QR Code
            DetectedQRCode code = qrCodeHelper.detectQRCodes(frame);
            if (code != null) {
                float[] rowMajorCameraIntrinsics = getCameraIntrinsics(camera);
                Pose qrCodeToCameraPose = qrCodeHelper.tryEstimateQRCodeToCameraPose(code, rowMajorCameraIntrinsics);
                if (qrCodeToCameraPose != null) {
                    Log.i(TAG, String.format("I GOT A POSE!!! %s", qrCodeToCameraPose.toString()));
                }
            }
        }
        catch (Throwable t) {
            Log.e(TAG, "Exception on the OpenGL thread", t);
        }
    }

    private void handleTap(Frame frame, Camera camera) {
        MotionEvent tap = tapHelper.poll();
        if (tap != null && camera.getTrackingState() == TrackingState.TRACKING) {
            for (HitResult hit : frame.hitTest(tap)) {
                // Check if any plane was hit, and if it was hit inside the plane polygon
                Trackable trackable = hit.getTrackable();
                Pose hitPose = hit.getHitPose();

                // Hits are sorted by depth. Consider only the closest hit on a plane or oriented point.
                // Creates a local anchor if a plane or an oriented point was hit.
                if ((trackable instanceof Plane &&
                        ((Plane)trackable).isPoseInPolygon(hitPose) &&
                        (PlaneRenderer.calculateDistanceToPlane(hitPose, camera.getPose()) > 0))
                    || (trackable instanceof Point &&
                        ((Point)trackable).getOrientationMode() == Point.OrientationMode.ESTIMATED_SURFACE_NORMAL)) {
                    // Cap the number of objects created. This avoids overloading both the
                    // rendering system and ARCore.
                    if (anchors.size() > maxAnchors) {
                        anchors.get(0).anchor.detach();
                        anchors.remove(0);
                    }

                    // Assign a color to the object for rendering based on the trackable type
                    // this anchor is attached to.
                    float[] objColor = DEFAULT_COLOR;
                    if (trackable instanceof Point) {
                        objColor = BLUE_COLOR;
                    } else if (trackable instanceof Plane) {
                        objColor = GREEN_COLOR;
                    }

                    // Creating an Anchor tells ARCore that it should track this position in
                    // space. This anchor is created on the Plane to place the 3D model
                    // in the correction position relative both to the world and to the plane.
                    Anchor arCoreAnchor = hit.createAnchor();
                    anchors.add(new ColoredAnchor(arCoreAnchor, objColor));

                    break;
                }
            }
        }
    }

    private boolean hasTrackingPlane() {
        for (Plane plane : session.getAllTrackables(Plane.class)) {
            if (plane.getTrackingState() == TrackingState.TRACKING) {
                return true;
            }
        }
        return false;
    }

    private float[] getCameraIntrinsics(Camera camera) {
        CameraIntrinsics intrinsics = camera.getImageIntrinsics();
        float fx = intrinsics.getFocalLength()[0];
        float fy = intrinsics.getFocalLength()[1];
        float cx = intrinsics.getPrincipalPoint()[0];
        float cy = intrinsics.getPrincipalPoint()[1];
        float s = 0f;

        return new float[] {
                fx, s, cx,
                0f, fy, cy,
                0f, 0f, 1f,
        };
    }
}
