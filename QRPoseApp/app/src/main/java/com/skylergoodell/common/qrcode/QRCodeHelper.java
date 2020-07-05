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

import java.nio.ByteBuffer;
import java.util.ArrayList;

public class QRCodeHelper {
    private static final String TAG = QRCodeHelper.class.getSimpleName();

    private static final QRCodeReader Reader = new QRCodeReader();

    /**
     * Detect QR Code in the current frame.
     * @param frame The YUV image frame from the Android system.
     * @return A detected QR code if one is in frame, null otherwise.
     */
    public static DetectedQRCode DetectQRCodes(Frame frame) {
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

            DetectedQRCode code = new DetectedQRCode(points);
            code.setData(qrText);

            return code;
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
     * @param qrCode The detected QR code.
     * @param rowMajorCameraIntrinsics Camera intrinsics.
     * @return The pose from the QR code to the camera, or null if one could not be estimated
     */
    public static Pose TryEstimateQRCodeToCameraPose(DetectedQRCode qrCode, float[] rowMajorCameraIntrinsics) {
        return null;
    }
}


