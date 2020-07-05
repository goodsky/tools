package com.skylergoodell.common.qrcode;

public class QRPoint2D {
    public float X;
    public float Y;
    public QRCodePointPosition Position;

    public QRPoint2D(float x, float y, QRCodePointPosition position) {
        X = x;
        Y = y;
        Position = position;
    }
}
