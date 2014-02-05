package it.sistemisnc.turbine.utils;

/**
 * Created by squid on 05/02/14.
 */
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayOutputStream;

import java.io.IOException;
import java.security.Key;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

public class CompressUtil {

    private static final String ALGO = "AES";
    private static final byte[] keyValue = "Ad0#2s!3oGyRq!5F".getBytes();
    private static Key key;


    public static byte[] compress(byte[] data) throws IOException, Exception {
        Deflater deflater = new Deflater();
        deflater.setLevel(Deflater.BEST_COMPRESSION);
        deflater.setInput(data);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);

        deflater.finish();
        byte[] buffer = new byte[1024];
        while (!deflater.finished()) {
            int count = deflater.deflate(buffer); // returns the generated code... index
            outputStream.write(buffer, 0, count);
        }
        outputStream.close();
        byte[] output = outputStream.toByteArray();

        output = encrypt(output);


        return output;
    }

    public static byte[] decompress(byte[] data) throws IOException, DataFormatException, Exception {

        data = decrypt(data);
        Inflater inflater = new Inflater();

        inflater.setInput(data);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
        byte[] buffer = new byte[1024];
        while (!inflater.finished()) {
            int count = inflater.inflate(buffer);
            outputStream.write(buffer, 0, count);
        }
        outputStream.close();
        byte[] output = outputStream.toByteArray();




        return output;
    }



    public static byte[] encrypt(byte[] Data) throws Exception {
        Key key = generateKey();
        Cipher c = Cipher.getInstance(ALGO);
        c.init(Cipher.ENCRYPT_MODE, key);
        byte[] encVal = c.doFinal(Data);
        //String encryptedValue = new BASE64Encoder().encode(encVal);
        return encVal;
    }

    public static byte[] decrypt(byte[] encryptedData) throws Exception {
        Key key = generateKey();
        Cipher c = Cipher.getInstance(ALGO);
        c.init(Cipher.DECRYPT_MODE, key);

        byte[] decValue = c.doFinal(encryptedData);
        return decValue;
    }

    private static Key generateKey() throws Exception {
        if (key == null)
            key = new SecretKeySpec(keyValue, ALGO);

        return key;
    }
}
