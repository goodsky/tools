package com.skylergoodell.common.qrcode;

import java.util.ArrayList;
import java.util.HashSet;

public class DetectedQRCode {
    public ArrayList<QRPoint2D> Points;
    public QRCodeMetadata Metadata;

    public DetectedQRCode(ArrayList<QRPoint2D> points)
    {
        Points = points;
        Metadata = new QRCodeMetadata();
    }

    public void setData(String data) {
        Metadata.Data = data;
    }

    public void setVersion(int version) {
        Metadata.Version = version;
    }

    public void setSizeInMeters(float sizeInMeters) {
        Metadata.SizeMeters = sizeInMeters;
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
