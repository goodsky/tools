package com.skylergoodell.common.qrcode;

import java.util.ArrayList;
import java.util.HashSet;

public class DetectedQRCode {
    public ArrayList<QRPoint2D> Points;
    public QRCodeMetadata Metadata;

    public DetectedQRCode(ArrayList<QRPoint2D> points) {
        Points = points;
        Metadata = new QRCodeMetadata();
    }

    public DetectedQRCode(ArrayList<QRPoint2D> points, QRCodeMetadata metadata) {
        Points = points;
        Metadata = metadata;
    }

    public boolean arePointsValid(boolean forPoseComputation)
    {
        if (Points.size() > 4 || Points.size() < 3 || (forPoseComputation && Points.size() != 4))
        {
            return false;
        }

        HashSet<QRCodePointPosition> allowedPositions = new HashSet<QRCodePointPosition>();
        allowedPositions.add(QRCodePointPosition.BottomLeftFinderPatternCenter);
        allowedPositions.add(QRCodePointPosition.TopLeftFinderPatternCenter);
        allowedPositions.add(QRCodePointPosition.TopRightFinderPatternCenter);
        if (Points.size() == 4)
        {
            allowedPositions.add(QRCodePointPosition.BottomRightAlignmentPatternCenter);
        }

        for (QRPoint2D point : Points)
        {
            if (!allowedPositions.contains(point.Position))
            {
                return false;
            }
            allowedPositions.remove(point.Position);
        }
        return true;
    }
}
