import java.io.File; 
import java.io.FileInputStream; 
import java.io.FileNotFoundException; 
import java.io.IOException; 
import java.util.HashMap; 
import java.util.Map; 
  
import javax.imageio.ImageIO; 
  
import com.google.zxing.BarcodeFormat; 
import com.google.zxing.BinaryBitmap; 
import com.google.zxing.EncodeHintType; 
import com.google.zxing.MultiFormatReader; 
import com.google.zxing.MultiFormatWriter; 
import com.google.zxing.NotFoundException; 
import com.google.zxing.Result; 
import com.google.zxing.WriterException; 
import com.google.zxing.client.j2se.BufferedImageLuminanceSource; 
import com.google.zxing.client.j2se.MatrixToImageWriter; 
import com.google.zxing.common.BitMatrix; 
import com.google.zxing.common.HybridBinarizer; 
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel; 
  
public class QRCode { 
  
    public static void main(String[] args) 
        throws WriterException, 
               IOException, 
               NotFoundException 
    { 
  
        // The data that the QR code will contain 
        String data = "www.skylergoodell.com/hello/skyler"; 
  
        // The path where the image will get saved 
        String path = "D:\\Data\\QRCodes\\helloskyler.jpg"; 

        // Encoding charset 
        String charset = "UTF-8"; 
  
        Map<EncodeHintType, Object> 
            hints 
            = new HashMap<EncodeHintType, Object>(); 
  
        hints.put( 
            EncodeHintType.ERROR_CORRECTION, 
            ErrorCorrectionLevel.L); 
  
        hints.put(
            EncodeHintType.MARGIN,
            0);

        // Create the QR code and save 
        // in the specified folder 
        // as a jpg file 
        // 386 x 386px ~= 10cm image
        createQRCode(data, path, 
                     charset, hints, 
                     386, 386);

        System.out.println( 
            "QR Code Generated!!! "); 
    } 
  
    // Function to create the QR code 
    public static void createQRCode( 
        String data, String path, 
        String charset, Map hints, 
        int height, int width) 
        throws WriterException, IOException 
    { 
        BitMatrix matrix 
            = new MultiFormatWriter().encode( 
                new String( 
                    data.getBytes(charset), 
                    charset), 
                BarcodeFormat.QR_CODE,
                width,
                height,
                hints); 
  
        MatrixToImageWriter 
            .writeToFile( 
                matrix, 
                path.substring( 
                    path.lastIndexOf('.') + 1), 
                new File(path)); 
    } 
} 