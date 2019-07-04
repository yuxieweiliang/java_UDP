import org.omg.CORBA.PRIVATE_MEMBER;
import org.omg.CosNaming.NamingContextExtPackage.StringNameHelper;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * Created by asus on 2019/4/24.
 */
public class UDPSearcher {
    private static  final int LISTEN_PORT = 30000;

    public static void main(String[] args) throws IOException, InterruptedException {
        System.out.println("UDPSearcher Started");

        Listener listener = listen();
        sendBroadCast();

        // 完成

        // 读取任意信息，然后结束
        System.in.read();
        List<Device> devices = listener.getDevicesAndClose();

        for (Device device : devices) {
            System.out.println("Devices: " + device.toString());

        }
        System.out.println("UDPSearcher Finished");


    }

    private static Listener listen() throws InterruptedException {
        System.out.println("UDPSearcher start listen");
        CountDownLatch countDownLatch = new CountDownLatch(1);
        Listener listener = new Listener(LISTEN_PORT, countDownLatch);
        listener.start();
        countDownLatch.await();
        return listener;
    }

    private static class Device {
        final int port;
        final String ip;
        final String sn;

        private Device(int port, String ip, String sn) {
            super();
            this.port = port;
            this.ip = ip;
            this.sn = sn;
        }

        @Override
        public String toString() {
            return "Devices{" +
                    "port=" + port +
                    ", ip='" + ip + "\'" +
                    ", sn='" + sn + "\'" +
                    "}";
        }
    }
    private static class Listener extends Thread {
        private final int listenPort;
        private final CountDownLatch countDownLatch;
        private final List<Device> devices = new ArrayList<>();
        private boolean done = false;
        private DatagramSocket ds = null;

        public Listener(int listenPort, CountDownLatch countDownLatch) {
            super();
            this.listenPort = listenPort;
            this.countDownLatch = countDownLatch;
        }

        @Override
        public void run() {
            super.run();

            // 通知已启动
            countDownLatch.countDown();
            try{

                System.out.println("UDPSearcher 构建接收实体");
                // 监听端口
                ds = new DatagramSocket(listenPort);

                System.out.println("UDPSearcher 已关闭：" + done);
                while (!done) {
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

                    String sn = MessageCreator.parseSn(data);
                    if(sn != null) {
                        Device device = new Device(port, ip, sn);
                        devices.add(device);
                    }
                }
            }catch (Exception error) {
                System.out.println(error.getMessage());
            }finally {
                close();
            }
            System.out.println("UDPSearcher listener finished");
        }

        private void close() {
            if(ds != null) {
                ds.close();
                ds = null;
            }
        }
        List<Device> getDevicesAndClose() {
            done = true;
            close();
            return devices;
        }
    }

    /**
     *
     * @throws IOException
     */
    private static void sendBroadCast() throws IOException {
        System.out.println("UDPSearcher sendBroadCast Started.");

        // 作为搜索方，放系统自动分配端口
        DatagramSocket ds = new DatagramSocket();

        // 构建一份请求数据
        String requestData = MessageCreator.buildWidthPort(LISTEN_PORT);
        byte[] requestDataBytes = requestData.getBytes();
        // 直接构建packet
        DatagramPacket requestPacket = new DatagramPacket(
                requestDataBytes,
                requestDataBytes.length
        );

        System.out.println("发送数据"+ new String(requestDataBytes,"UTF-8"));
        // 20000端口
        // InetAddress.getByName("255.255.255.255") 广播端口
        requestPacket.setAddress(InetAddress.getByName("255.255.255.255"));
        requestPacket.setPort(20000);

        // 发送
        ds.send(requestPacket);
        // 不能直接关闭，否则发送不成功；
        // ds.close();

        // 完成
        System.out.println("sendBroadCast Finished 发送广播已完成！");
    }
}
