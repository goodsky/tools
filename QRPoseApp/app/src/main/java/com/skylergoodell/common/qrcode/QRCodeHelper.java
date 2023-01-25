package com.skylergoodell.common.qrcode;

import android.media.Image;
import android.util.Log;

import com.google.ar.core.Frame;
import com.google.ar.core.Pose;
import com.google.ar.core.exceptions.NotYetAvailableException;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.FormatException;
import com.google.zxing.LuminanceSource;
import com.google.zxing.NotFoundException;
import com.google.zxing.PlanarYUVLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.ResultPoint;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;

import org.opencv.calib3d.Calib3d;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDouble;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.MatOfPoint3f;
import org.opencv.core.Point;
import org.opencv.core.Point3;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.opencv.calib3d.Calib3d.SOLVEPNP_EPNP;
import static org.opencv.calib3d.Calib3d.SOLVEPNP_ITERATIVE;
import static org.opencv.calib3d.Calib3d.SOLVEPNP_P3P;
import static org.opencv.core.CvType.CV_32F;

public class QRCodeHelper {
    private static final String TAG = QRCodeHelper.class.getSimpleName();

    private static final QRCodeReader Reader = new QRCodeReader();

    /**
     * TODO: Read this from a configuration
     */
    private Map<String, QRCodeMetadata> KnownQRCodes = new HashMap<String, QRCodeMetadata>();

    public QRCodeHelper() {
        KnownQRCodes.put("www.skylergoodell.com/hello/skyler",
            new QRCodeMetadata("www.skylergoodell.com/hello/skyler", 3, 0.1f));
    }

    /**
     * Detect QR Code in the current frame.
     * @param frame The YUV image frame from the Android system.
     * @return A detected QR code if one is in frame, null otherwise.
     */
    public DetectedQRCode detectQRCodes(Frame frame) {
        Image image = null;
        Result result = null;
        try {
            image = frame.acquireCameraImage();
            LuminanceSource luminance = convertToLumninanceSource(image);

            BinaryBitmap bitmap = new BinaryBitmap(
                    new HybridBinarizer(luminance));

            result = Reader.decode(bitmap);
        }
        catch (NotYetAvailableException ex) {
            Log.w(TAG, "Frame not yet available");
        } catch (FormatException e) {
            Log.e(TAG, "Failed to process frame.", e);
        } catch (NotFoundException | ChecksumException e) {
            // This happens when we fail to find a QR Code in the image - no need to spew the error
        } finally {
            if (image != null) {
                image.close();
            }
            Reader.reset();
        }

        if (result != null)
        {
            String qrText = result.getText();
            BarcodeFormat resultFormat = result.getBarcodeFormat();
            ResultPoint[] resultPoints = result.getResultPoints();

            if (resultFormat != BarcodeFormat.QR_CODE ||
                resultPoints.length < 3)
            {
                Log.i(TAG, String.format("Detected invalid QR Code. Type: %s; Points: %d;", resultFormat, resultPoints.length));
            }

            ArrayList<QRPoint2D> points = new ArrayList<QRPoint2D>();

            // ZXing returns points in the order of bottom left, top left, top right, bottom right
            points.add(new QRPoint2D(
                    resultPoints[0].getX(),
                    resultPoints[0].getY(),
                    QRCodePointPosition.BottomLeftFinderPatternCenter));
            points.add(new QRPoint2D(
                    resultPoints[1].getX(),
                    resultPoints[1].getY(),
                    QRCodePointPosition.TopLeftFinderPatternCenter));
            points.add(new QRPoint2D(
                    resultPoints[2].getX(),
                    resultPoints[2].getY(),
                    QRCodePointPosition.TopRightFinderPatternCenter));

            if (resultPoints.length == 4)
            {
                points.add(new QRPoint2D(
                        resultPoints[3].getX(),
                        resultPoints[3].getY(),
                        QRCodePointPosition.BottomRightAlignmentPatternCenter));
            }

            Log.i(TAG, String.format("QR Code Detected! Points: %d; Text: \"%s\"", points.size(), qrText));
            if (KnownQRCodes.containsKey(qrText)) {
                QRCodeMetadata metadata = KnownQRCodes.get(qrText);
                Log.i(TAG, String.format("This is a known QR code: Version: %d; SizeInMeters: %f", metadata.Version, metadata.SizeMeters));
                return new DetectedQRCode(points, metadata);
            }
        }

        return null;
    }

    private static LuminanceSource convertToLumninanceSource(Image image) {
        int width = image.getWidth();
        int height = image.getHeight();

        ByteBuffer buffer = image.getPlanes()[0].getBuffer();
        byte[] data = new byte[buffer.remaining()];
        buffer.get(data);

        return new PlanarYUVLuminanceSource(data, width, height, 0, 0, width, height, false);
    }

    /**
     * Given a detected QR code, attempt to estimate the Pose from the QR code to the camera.
     *
     * Camera intrinsics are represented by a 3x3 matrix, input as a row major array.
     * This method does not support camera distortion. We would need to appropriately
     * fill distortionCoefficientsMat in the method to handle that.
     * @param detectedQRCode The detected QR code.
     * @param rowMajorCameraIntrinsics Camera intrinsics.
     * @return The pose from the QR code to the camera, or null if one could not be estimated
     */
    public Pose tryEstimateQRCodeToCameraPose(DetectedQRCode detectedQRCode, float[] rowMajorCameraIntrinsics)
        throws Exception {
        if (!detectedQRCode.arePointsValid(true)) {
            return null;
        }

        int pointCount = 0;
        Point3[] points3DInMarker = new Point3[4];
        Point[] points2DInCamera = new Point[4];
        for (QRPoint2D point : detectedQRCode.Points) {
            Point3 point3DInMarker = get3DPointInQRCode(point.Position, detectedQRCode.Metadata);
            Point point2DInCamera = new Point(point.X, point.Y);

            points3DInMarker[pointCount] = point3DInMarker;
            points2DInCamera[pointCount] = point2DInCamera;

            /*Log.d(TAG, String.format("[Try Estimate QR Code To Camera] position: %s; point2DInCamera: (%f, %f); point3DInMarker: (%f, %f, %f)",
                    point.Position,
                    point2DInCamera.x,
                    point2DInCamera.y,
                    point3DInMarker.x,
                    point3DInMarker.y,
                    point3DInMarker.z));*/

            pointCount++;
        }

        MatOfPoint3f markerPoints3D = new MatOfPoint3f(points3DInMarker);
        MatOfPoint2f imagePoints2D = new MatOfPoint2f(points2DInCamera);

        Mat cameraMatrix = new Mat(3, 3, CV_32F);
        for (int i = 0; i < 9; ++i) {
            cameraMatrix.put(i / 3, i % 3, rowMajorCameraIntrinsics[i]);
        }

        MatOfDouble distortionCoefficientsMat = new MatOfDouble();
        Mat bestRodriguesRotationQrCodeToCamera = new Mat(1, 3, CV_32F);
        Mat bestTranslationQRCodeToCamera = new Mat(1, 3, CV_32F);

        // We try two methods of pose estimation and then pick the one with the least residual
        int[] solvePnpFlags = { SOLVEPNP_P3P, SOLVEPNP_EPNP };
        double minSquaredResidualPixels = Double.MAX_VALUE;
        for (int solvePnpFlag : solvePnpFlags)
        {
            Mat rodriguesRotationQrCodeToCamera = new Mat(1, 3, CV_32F);
            Mat translationQRCodeToCamera = new Mat(1, 3, CV_32F);

            Calib3d.solvePnP(
                    markerPoints3D,
                    imagePoints2D,
                    cameraMatrix,
                    distortionCoefficientsMat,
                    rodriguesRotationQrCodeToCamera,
                    translationQRCodeToCamera,
                    /*use extrinsics guess to refine*/false,
                    solvePnpFlag);

            Pose qrCodeToCameraPose = convertOpenCVPnPResultToPose(rodriguesRotationQrCodeToCamera, translationQRCodeToCamera);
            // Log.d(TAG, String.format("[Calculated Pose] pose: %s", qrCodeToCameraPose.toString()));

            double squaredResidualPixels = isValidQRCodeToCameraPose(
                        points3DInMarker,
                        points2DInCamera,
                        qrCodeToCameraPose,
                        rowMajorCameraIntrinsics,
                        detectedQRCode.Metadata.moduleSizeMeters());

            // squareResidualPixels may be Double.MAX_VALUE when calculation failed.
            if (squaredResidualPixels < minSquaredResidualPixels)
            {
                minSquaredResidualPixels = squaredResidualPixels;
                bestRodriguesRotationQrCodeToCamera = rodriguesRotationQrCodeToCamera;
                bestTranslationQRCodeToCamera = translationQRCodeToCamera;
            }
        }

        if (minSquaredResidualPixels == Double.MAX_VALUE)
        {
            // Log.i(TAG, "Found no valid QR Code to Camera Poses.");
            return null;
        }

        // Perform a non-linear refinement
        Calib3d.solvePnP(
            markerPoints3D,
            imagePoints2D,
            cameraMatrix,
            distortionCoefficientsMat,
            bestRodriguesRotationQrCodeToCamera,
            bestTranslationQRCodeToCamera,
            true,
            SOLVEPNP_ITERATIVE);

        Pose bestQrCodeToCameraPose = convertOpenCVPnPResultToPose(bestRodriguesRotationQrCodeToCamera, bestTranslationQRCodeToCamera);
        double bestSquaredResidualPixels = isValidQRCodeToCameraPose(points3DInMarker, points2DInCamera, bestQrCodeToCameraPose, rowMajorCameraIntrinsics, detectedQRCode.Metadata.moduleSizeMeters());
        if (bestSquaredResidualPixels == Double.MAX_VALUE)
        {
            Log.i(TAG, "The best pose turned out to be invalid.");
            return null;
        }

        return bestQrCodeToCameraPose;
    }

    private static Point3 get3DPointInQRCode(QRCodePointPosition position, QRCodeMetadata metadata)
            throws Exception {
        // A QR code is made of small black and white squares, which are called modules.
        // 'Finder patterns' are the three bigger square structure located in the bottom left, top left and top right.
        // 'Alignment patterns' are special square structures located in all but Version 1 QR Codes.
        // More information about how patterns are placed in QR codes is here -
        // https://www.thonky.com/qr-code-tutorial/module-placement-matrix

        // The center of a Finder pattern is 3.5 modules away from the respective corner in each direction.
        float finderPatternShiftFromCornerInModules = 3.5f;

        // The center of bottom right Alignment pattern is 6.5 modules away from the bottom right corner  in each direction.
        float alignmentPatternShiftFromBottomRightCornerInModules = 6.5f;

        float moduleSizeMeters = metadata.moduleSizeMeters();
        int numModules = metadata.numModules();

        if (moduleSizeMeters <= 1e-6 || numModules <= 0)
        {
            throw new Exception("Invalid qr code metadata.");
        }

        Point3 point = new Point3();
        switch (position)
        {
            case BottomLeftFinderPatternCenter:
                point.x = finderPatternShiftFromCornerInModules * moduleSizeMeters;
                point.y = (numModules - finderPatternShiftFromCornerInModules) * moduleSizeMeters;
                point.z = 0;
                break;
            case TopLeftFinderPatternCenter:
                point.x = finderPatternShiftFromCornerInModules * moduleSizeMeters;
                point.y = finderPatternShiftFromCornerInModules * moduleSizeMeters;
                point.z = 0;
                break;
            case TopRightFinderPatternCenter:
                point.x = (numModules - finderPatternShiftFromCornerInModules) * moduleSizeMeters;
                point.y = finderPatternShiftFromCornerInModules * moduleSizeMeters;
                point.z = 0;
                break;
            case BottomRightAlignmentPatternCenter:
                point.x = (numModules - alignmentPatternShiftFromBottomRightCornerInModules) * moduleSizeMeters;
                point.y = (numModules - alignmentPatternShiftFromBottomRightCornerInModules) * moduleSizeMeters;
                point.z = 0.0f;
                break;
            default:
                throw new Exception("Unknown 2D point position");
        }

        return point;
    }

    private static Pose convertOpenCVPnPResultToPose(Mat rodriguesRotationVector, Mat translationVector) {
        float[] translation = new float[3];
        translationVector.get(0, 0, translation);

        float[] rotation = new float[4];
        rodriguesRotationVector.get(0, 0, rotation);

        return new Pose(translation, rotation);
    }

    private static double isValidQRCodeToCameraPose(
            Point3[] points3DInMarker,
            Point[] points2DInCamera,
            Pose qrCodeToCameraPose,
            float[] rowMajorCameraIntrinsics,
            float qrCodeModuleSizeMeters)
    throws Exception {
        float[] translation = qrCodeToCameraPose.getTranslation();
        double translationMagnitude = Math.sqrt(
                translation[0] * translation[0] +
                translation[1] * translation[1] +
                translation[2] * translation[2]);

        // check that the qr code is not too close to the camera
        final double minAllowedProximityMeters = 0.05;
        if (translationMagnitude < minAllowedProximityMeters)
        {
            Log.i(TAG, "QR code is too close to the camera.");
            return Double.MAX_VALUE;
        }

        // check that the qr code is in front of the camera
        double angleRadians = Math.acos(translation[2] / translationMagnitude);
        if (angleRadians >= Math.PI/2)
        {
            Log.i(TAG, "QR code is behind the camera.");
            return Double.MAX_VALUE;
        }

        float maxAllowedSquaredResidualPixelsPerPoint = computeMaxAllowedResidualPixelsPerPoint(
                qrCodeModuleSizeMeters,
                (float)translationMagnitude,
                rowMajorCameraIntrinsics[0]);

        double squaredResidualPixels = computeResidual(points3DInMarker, points2DInCamera, qrCodeToCameraPose, rowMajorCameraIntrinsics);
        double maxAcceptableSquaredResidualPixels = points3DInMarker.length * maxAllowedSquaredResidualPixelsPerPoint;
        if (squaredResidualPixels > maxAcceptableSquaredResidualPixels)
        {
            // Log.d(TAG, String.format("Residual pixels is larger than max allowed. Squared Residual: %f; Max Allowed: %f;", squaredResidualPixels, maxAcceptableSquaredResidualPixels));
            // return Double.MAX_VALUE;
        }

        return squaredResidualPixels;
    }

    private static double computeResidual(
            Point3[] points3DInMarker,
            Point[] points2DInCamera,
            Pose qrCodeToCameraPose,
            float[] rowMajorCameraIntrinsics)
        throws Exception {
        double squaredResidualPixels = 0.0;

        int numPoints = points3DInMarker.length;
        if (numPoints != points2DInCamera.length)
        {
            throw new Exception("Number of marker points should be equal to camera points");
        }

        float fx = rowMajorCameraIntrinsics[0];
        float fy = rowMajorCameraIntrinsics[4];
        float cx = rowMajorCameraIntrinsics[2];
        float cy = rowMajorCameraIntrinsics[5];

        for (int i=0; i < numPoints; i++)
        {
            Point3 markerPoint = points3DInMarker[i];
            float[] markerPointArray = new float[] { (float)markerPoint.x, (float)markerPoint.y, (float)markerPoint.z };
            float[] markerPointInCameraArray = qrCodeToCameraPose.transformPoint(markerPointArray);
            Point3 markerPointInCamera = new Point3(markerPointInCameraArray[0], markerPointInCameraArray[1], markerPointInCameraArray[2]);
            if(markerPointInCamera.z < 1e-4)
            {
                Log.i(TAG, "Marker point is too close to camera.");
                return Double.MAX_VALUE;
            }

            Point projectedMarkerPointInCamera = new Point((float)(markerPointInCamera.x/markerPointInCamera.z), (float)(markerPointInCamera.y/markerPointInCamera.z));
            Point projectedMarkerPointInCameraPixels = new Point(projectedMarkerPointInCamera.x * fx + cx, projectedMarkerPointInCamera.y * fy + cy);
            squaredResidualPixels += Math.pow(points2DInCamera[i].x - projectedMarkerPointInCameraPixels.x, 2);
            squaredResidualPixels += Math.pow(points2DInCamera[i].y - projectedMarkerPointInCameraPixels.y, 2);

            /*Log.d(TAG, String.format("[ProjectedMarkerPoint] markerPoint: (%f, %f, %f); projectedMarkerPointInCamera: (%f, %f);",
                    markerPoint.x,
                    markerPoint.y,
                    markerPoint.z,
                    projectedMarkerPointInCamera.x,
                    projectedMarkerPointInCamera.y));*/

            Log.d(TAG, String.format("[Calculating Residual] point2DInCamera: (%f, %f); projectedMarkerPointInCamera: (%f, %f); delta: (%f, %f)",
                    points2DInCamera[i].x,
                    points2DInCamera[i].y,
                    projectedMarkerPointInCameraPixels.x,
                    projectedMarkerPointInCameraPixels.y,
                    Math.pow(points2DInCamera[i].x - projectedMarkerPointInCameraPixels.x, 2),
                    Math.pow(points2DInCamera[i].y - projectedMarkerPointInCameraPixels.y, 2)));
        }

        return squaredResidualPixels;
    }

    static private float computeMaxAllowedResidualPixelsPerPoint(float qrCodeModuleSizeMeters, float distanceOfQRCodeToCamera, float focalLengthPixels)
    {
        // Ideally the max allowed residual is (moduleSizeMeters / 2 * depthOfQRCode) * focalLengthPixels
        // The reasoning behind this formula: Module size is the area of confusion for detection. Assume that the module is along the principal axis
        // and project it using the intrinsics of pin hole camera.
        // We do not want a pose that beings the QR code very close to allow very large residual, hence we use a min distance of 1m.
        // To account for image noise and blur, we allow for double the residual of the above formula.
        return qrCodeModuleSizeMeters * focalLengthPixels / Math.min(1.0f, distanceOfQRCodeToCamera);
    }
}


