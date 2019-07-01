import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Inet4Address;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.nio.ByteBuffer;

public class Client {
    private static final int PORT = 20000;
    private static final int LOCAL_PORT = 20001;
    public static void main(String[] args) throws IOException {
        Socket socket = createSocket();
        initSocket(socket);

        socket.connect(new InetSocketAddress(Inet4Address.getLocalHost(), PORT), 3000);

        try{
            todo(socket);
        }catch (Exception e) {
            System.out.println("异常关闭");
        }
    }

    private static void initSocket(Socket socket) throws SocketException{
        // 设置读取超时时间为2秒，链接超时和读取超时，但是连接有相关超时，所以只对应读取
        socket.setSoTimeout(2000);

        // 是否复用未完全关闭的Socket地址，对于指定bind操作后的套接字有效
        socket.setReuseAddress(true);

        // 是否开启Nagle算法
        // socket.setTcpNoDelay(false);

        // 是否需要在长时间无数据相应时发送确认数据（类似心跳包），时间大约为2小时
        socket.setKeepAlive(true);

        // 对于close关闭操作行为进行怎样的处理；默认为false，0
        // false、0：默认，关闭时立即返回，底层系统接管输出流，将缓冲区内的数据发送完成
        // true、0：关闭时立即返回，缓冲区数据抛弃，直接发送RST结束命令到对方，并无需经过2MSL等待
        // true、200：关闭时最长阻塞200毫秒，随后按第二情况处理
        socket.setSoLinger(true, 20);

        // 是否让紧急数据内敛，默认为false，紧急数据通过 socket.sendUrgentData(1); 发送
        // socket.setOOBInline(true);

        // 设置接收发送缓冲器大小
        socket.setReceiveBufferSize(64 * 1024 * 1024);
        socket.setSendBufferSize(64 * 1024 * 1024);

        // 设置性能参数：端连接，延迟，带宽的相对重要性
        socket.setPerformancePreferences(1, 2, 1);

    }

    private static Socket createSocket() throws IOException {

        Socket socket = new Socket();
        socket.bind(new InetSocketAddress(Inet4Address.getLocalHost(), LOCAL_PORT));

        return socket;
    }

    private static void todo(Socket client) throws IOException{

        // 得到Socket输出流，并转换为打印流
        OutputStream outputStream = client.getOutputStream();

        // 得到Socket输入流，并转换为BufferedReader
        InputStream inputStream = client.getInputStream();
        byte[] buffer = new byte[256];
        ByteBuffer byteBuffer = ByteBuffer.wrap(buffer);

        // byte
        byteBuffer.put((byte) 126);

        // char
        char c = 'c';
        byteBuffer.put((byte) c);

        // int
        int i = 32543532;
        byteBuffer.putInt(i);

        // bool
        boolean b = true;
        byteBuffer.put(b?(byte) 1 : (byte) 0);

        // Long
        long l = 126;
        byteBuffer.putLong(l);

        // float
        float f = 12.353f;
        byteBuffer.putFloat(f);

        // double
        double p = 3.1415926;
        byteBuffer.putDouble(p);

        // String
        String str = "hello 你好！";
        byteBuffer.put(str.getBytes());

        outputStream.write(buffer, 0, byteBuffer.position());

        int read = inputStream.read(buffer);
        System.out.println("收到数量：" + read );

        // 释放资源
        outputStream.close();
        inputStream.close();

    }


}
