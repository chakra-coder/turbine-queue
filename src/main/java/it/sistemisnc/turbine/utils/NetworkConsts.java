package it.sistemisnc.turbine.utils;

import com.google.gson.Gson;
import it.sistemisnc.turbine.data.Message;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.zip.GZIPOutputStream;

/**
 * Created by squid on 05/02/14.
 */
public class NetworkConsts{

    public static int RECV_BUFFER_SIZE = 8192;
    public static int SERVER_PORT = 8903;
    public static int SERVER_MAX_CONNECTIONS = 10;


    public static Gson gson = new Gson();




    public static byte[] getCompressedByteArrayMessage(byte[] message) throws Exception
    {



        byte[] compressed = CompressUtil.compress(message);


        ByteBuffer buffer = ByteBuffer.allocate(compressed.length + 4);
        buffer.putInt(compressed.length);
        buffer.put(compressed);


        return buffer.array();

    }


    public static byte[] covertMessageToByteArray(Message message)
    {
        try
        {

            String json = gson.toJson(message);
            byte[] compressed = NetworkConsts.getCompressedByteArrayMessage(json.getBytes());

            return compressed;
        }
        catch (Exception ex)
        {

        }
        return null;

    }



}
