package test1;
import java.awt.Graphics2D;  
import java.awt.image.BufferedImage;  
import java.io.File;  
import java.io.IOException;  
import java.util.HashMap;  
import java.util.Map;  
  
import javax.imageio.ImageIO;  
  
import com.google.zxing.BarcodeFormat;  
import com.google.zxing.BinaryBitmap;  
import com.google.zxing.DecodeHintType;  
import com.google.zxing.EncodeHintType;  
import com.google.zxing.MultiFormatReader;  
import com.google.zxing.MultiFormatWriter;  
import com.google.zxing.NotFoundException;  
import com.google.zxing.Result;  
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;  
import com.google.zxing.client.j2se.MatrixToImageConfig;  
import com.google.zxing.client.j2se.MatrixToImageWriter;  
import com.google.zxing.common.BitMatrix;  
import com.google.zxing.common.HybridBinarizer;  
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;  
  
/** 
 * ZXing������ 
 * @see ----------------------------------------------------------------------------------------------------------------------- 
 * @see ��ҳ--https://code.google.com/p/zxing 
 * @see ����--���ڽ������ָ�ʽ������(EAN-13)�Ͷ�ά��(QRCode)�Ŀ�ԴJava���,���ṩ�˶���Ӧ�õ����,��javase/jruby/cpp/csharp/android 
 * @see ˵��--���ص���ZXing-2.2.zip������Դ��,������JavaSE��ʹ��ʱ���õ���core��javase������ 
 * @see      ��ֱ������������Դ�뵽��Ŀ��,����������Ϊjar������,�����ұ���õģ�http://download.csdn.net/detail/jadyer/6245849 
 * @see ----------------------------------------------------------------------------------------------------------------------- 
 * @see ������:��΢��ɨ��GBK��������Ķ�ά��ʱ��������,��UTF-8����ʱ΢�ſ�����ʶ�� 
 * @see       ����MultiFormatWriter.encode()ʱ������hints������ָ��UTF-8��������ʱ,΢��ѹ���Ͳ�ʶ�������ɵĶ�ά�� 
 * @see       ��������ʹ�õ������ַ�ʽnew String(content.getBytes("UTF-8"), "ISO-8859-1") 
 * @see ----------------------------------------------------------------------------------------------------------------------- 
 * @see ��logoͼƬ�����ά���м�ʱ,��ע�����¼��� 
 * @see 1)���ɶ�ά��ľ������������ߵȼ�H,�����������Ӷ�ά�����ȷʶ������(�Ҳ��Թ�,�����ü���ʱ,��ά�빤���޷���ȡ���ɵĶ�ά��ͼƬ) 
 * @see 2)ͷ���С��ò�Ҫ������ά�뱾���С��1/5,����ֻ�ܷ������м䲿λ,�������ڶ�ά�뱾��ṹ��ɵ�(��Ͱ�������ͼƬˮӡ��) 
 * @see 3)�ڷ�����Ѷ΢���ڶ�ά����������װ�ο�,��ôһ��Ҫ��װ�ο�Ͷ�ά��֮�������ױ�,����Ϊ�˶�ά��ɱ�ʶ�� 
 * @see ----------------------------------------------------------------------------------------------------------------------- 
 * @version v1.0 
 * @history v1.0-->�����½�,Ŀǰ��֧�ֶ�ά������ɺͽ���,���ɶ�ά��ʱ֧�����logoͷ�� 
 * @editor Sep 10, 2013 9:32:23 PM 
 * @create Sep 10, 2013 2:08:16 PM 
 * @author ����<http://blog.csdn.net/jadyer> 
 */  
public class ZXingUtil {  
    private ZXingUtil(){}  
      
    /** 
     * Ϊ��ά��ͼƬ����logoͷ�� 
     * @see ��ԭ��������ͼƬ��ˮӡ 
     * @param imagePath ��ά��ͼƬ���·��(���ļ���) 
     * @param logoPath  logoͷ����·��(���ļ���) 
     */  
    private static void overlapImage(String imagePath, String logoPath) throws IOException {  
        BufferedImage image = ImageIO.read(new File(imagePath));  
        int logoWidth = image.getWidth()/5;   //����logoͼƬ���Ϊ��ά��ͼƬ�����֮һ  
        int logoHeight = image.getHeight()/5; //����logoͼƬ�߶�Ϊ��ά��ͼƬ�����֮һ  
        int logoX = (image.getWidth()-logoWidth)/2;   //����logoͼƬ��λ��,�����������  
        int logoY = (image.getHeight()-logoHeight)/2; //����logoͼƬ��λ��,�����������  
        Graphics2D graphics = image.createGraphics();  
        graphics.drawImage(ImageIO.read(new File(logoPath)), logoX, logoY, logoWidth, logoHeight, null);  
        graphics.dispose();  
        ImageIO.write(image, imagePath.substring(imagePath.lastIndexOf(".") + 1), new File(imagePath));  
    }  
  
      
    /** 
     * ���ɶ�ά�� 
     * @param content   ��ά������ 
     * @param charset   �����ά������ʱ���õ��ַ���(��nullʱĬ�ϲ���UTF-8����) 
     * @param imagePath ��ά��ͼƬ���·��(���ļ���) 
     * @param width     ���ɵĶ�ά��ͼƬ��� 
     * @param height    ���ɵĶ�ά��ͼƬ�߶� 
     * @param logoPath  logoͷ����·��(���ļ���,������logo��null����) 
     * @return ���ɶ�ά����(true or false) 
     */  
    public static boolean encodeQRCodeImage(String content, String charset, String imagePath, int width, int height, String logoPath) {  
        Map<EncodeHintType, Object> hints = new HashMap<EncodeHintType, Object>();  
        //ָ�������ʽ  
        //hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");  
        //ָ��������(L--7%,M--15%,Q--25%,H--30%)  
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);  
        //��������,��������(����ָ��Ϊ��ά��),����ͼƬ���,����ͼƬ�߶�,���ò���  
        BitMatrix bitMatrix = null;  
        try {  
            bitMatrix = new MultiFormatWriter().encode(new String(content.getBytes(charset==null?"UTF-8":charset), "ISO-8859-1"), BarcodeFormat.QR_CODE, width, height, hints);  
        } catch (Exception e) {  
            System.out.println("��������ɶ�ά��ͼƬ���ı�ʱ�����쳣,��ջ�켣����");  
            e.printStackTrace();  
            return false;  
        }  
        //���ɵĶ�ά��ͼƬĬ�ϱ���Ϊ��ɫ,ǰ��Ϊ��ɫ,�����ڼ���logoͼ���ᵼ��logoҲ��Ϊ�ڰ�ɫ,������ʲôԭ��û����ϸȥ������Դ��  
        //������������һ��������ɫ��ZXingĬ�ϵ�ǰ��ɫ0xFF000000��΢����һ��0xFF000001,����Ч��Ҳ�ǰ�ɫ������ɫǰ���Ķ�ά��,��logo��ɫ����ԭ�в���  
        MatrixToImageConfig config = new MatrixToImageConfig(0xFF000001, 0xFFFFFFFF);  
        //����Ҫ��ʽָ��MatrixToImageConfig,���򻹻ᰴ��Ĭ�ϴ���logoͼ��Ҳ��Ϊ�ڰ�ɫ(��������logo�Ļ�,��֮���봫MatrixToImageConfig����)  
        try {  
            MatrixToImageWriter.writeToFile(bitMatrix, imagePath.substring(imagePath.lastIndexOf(".") + 1), new File(imagePath), config);  
        } catch (IOException e) {  
            System.out.println("���ɶ�ά��ͼƬ[" + imagePath + "]ʱ�����쳣,��ջ�켣����");  
            e.printStackTrace();  
            return false;  
        }  
        //��ʱ��ά��ͼƬ�Ѿ�������,ֻ����û��logoͷ��,���Խ��������ݴ����logoPath�����������Ƿ��logoͷ��  
        if(null == logoPath){  
            return true;  
        }else{  
            //�����ʱ�������ɵĶ�ά�벻��������Ҫ��,��ô������չMatrixToImageConfig��(����ZXing�ṩ��Դ��)  
            //��չʱ������д��writeToFile����,���䷵��toBufferedImage()���������ɵ�BufferedImage����(������������δ���ܽ��Ϊ��,�������ʵ���龰����)  
            //Ȼ���滻����overlapImage()����ĵ�һ��BufferedImage image = ImageIO.read(new File(imagePath));  
            //��private static void overlapImage(BufferedImage image, String imagePath, String logoPath)  
            try {  
                //���ﲻ��Ҫ�ж�logoPath�Ƿ�ָ����һ��������ļ�,��Ϊ�����龰��overlapImage����IO�쳣  
                overlapImage(imagePath, logoPath);  
                return true;  
            } catch (IOException e) {  
                System.out.println("Ϊ��ά��ͼƬ[" + imagePath + "]���logoͷ��[" + logoPath + "]ʱ�����쳣,��ջ�켣����");  
                e.printStackTrace();  
                return false;  
            }  
        }  
    }  
      
      
    /** 
     * ������ά�� 
     * @param imagePath ��ά��ͼƬ���·��(���ļ���) 
     * @param charset   �����ά������ʱ���õ��ַ���(��nullʱĬ�ϲ���UTF-8����) 
     * @return �����ɹ��󷵻ض�ά���ı�,���򷵻ؿ��ַ��� 
     */  
    public static String decodeQRCodeImage(String imagePath, String charset) {  
        BufferedImage image = null;  
        try {  
            image = ImageIO.read(new File(imagePath));  
        } catch (IOException e) {  
            e.printStackTrace();  
            return "";  
        }  
        if(null == image){  
            System.out.println("Could not decode QRCodeImage");  
            return "";  
        }  
        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(new BufferedImageLuminanceSource(image)));  
        Map<DecodeHintType, String> hints = new HashMap<DecodeHintType, String>();  
        hints.put(DecodeHintType.CHARACTER_SET, charset==null ? "UTF-8" : charset);  
        Result result = null;  
        try {  
            result = new MultiFormatReader().decode(bitmap, hints);  
            return result.getText();  
        } catch (NotFoundException e) {  
            System.out.println("��ά��ͼƬ[" + imagePath + "]����ʧ��,��ջ�켣����");  
            e.printStackTrace();  
            return "";  
        }  
    }  
    
    public static void main(String[] args) { 
        encodeQRCodeImage("http://w3.zaiyiqiba.com/love.php?make=1&id", null, "/Users/luofan/Desktop/e.jpg", 400, 400, null); 
       // System.out.println(decodeQRCodeImage("E:/download/myQRCodeImage.jpg", null)); 
    	
    	System.out.println(System.getProperty("user.dir"));
    }
}  