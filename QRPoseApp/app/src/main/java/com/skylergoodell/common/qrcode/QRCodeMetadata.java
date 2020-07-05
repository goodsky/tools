package com.skylergoodell.common.qrcode;

public class QRCodeMetadata {
    public String Data;
    public int Version;
    public float SizeMeters;

    public QRCodeMetadata() {
        Data = "";
        Version = -1;
        SizeMeters = -1f;
    }

    public QRCodeMetadata(String data, int version, float sizeInMeters) {
        Data = data;
        Version = version;
        SizeMeters = sizeInMeters;
    }

    public int numModules() {
        if (Version <= 0) {
            return 0;
        }

        // Modules are the small black/white squares that a QR code is made of.
        // The number of modules in a QR code is a function of the Version by design.
        // https://www.thonky.com/qr-code-tutorial/module-placement-matrix
        return (Version - 1) * 4 + 21;
    }

    public float moduleSizeMeters()
    {
        if (Version <= 0 || SizeMeters <= 0)
        {
            return 0.0f;
        }

        return SizeMeters / numModules();
    }
}
