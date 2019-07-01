import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * Created by asus on 2019/4/24.
 */
public class UDPSearcher {
    public static void main(String[] args) throws IOException {
        System.out.println("UDPSearcher Started");

        // 随机获取端口号
        DatagramSocket ds = new DatagramSocket();

        // 构建一份请求数据
        String requestData = "Hello word!";
        byte[] requestDataBytes = requestData.getBytes();
        // 直接构建packet
        DatagramPacket requestPacket = new DatagramPacket(
                requestDataBytes,
                requestDataBytes.length,
                // InetAddress.getLocalHost(),
                InetAddress.getByName("227.1.1.1"),
                10024
        );

        // 发送到本机20000端口
        // requestPacket.setAddress();
        // requestPacket.setPort(20000);

        // 发送
        ds.send(requestPacket);

        // ---------------------------------------------------------------------------------------------------
        System.out.println("UDPSearcher 构建接收实体");
        // 构建接收实体
        final byte[] buf = new byte[512];
        DatagramPacket receivePack = new DatagramPacket(buf, buf.length);
        System.out.println("UDPSearcher receive");
        // 接收
        ds.receive(receivePack);
        System.out.println("UDPSearcher getAddress");
        // 打印接收到的信息与发送者的信息
        // 发送者的IP地址
        String ip = receivePack.getAddress().getHostAddress();
        int port = receivePack.getPort();
        int dataLen = receivePack.getLength();
        String data = new String(receivePack.getData(), 0, dataLen);
        System.out.println("UDPSearcher receive form ip:" + ip + "\tport" + port + "\tdata" + data);


        // 完成
        System.out.println("UDPSearcher Finished");
        ds.close();
    }
}
