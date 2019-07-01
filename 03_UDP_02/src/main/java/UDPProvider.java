import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.UUID;

/**
 * Created by asus on 2019/4/24.
 */
public class UDPProvider {
    public static void main(String[] args) throws IOException {
        String sn = UUID.randomUUID().toString();
        Provider socket = new Provider(sn);
        socket.start();

        System.in.read();
        socket.exit();
    }

    private static class Provider extends Thread {
        private final String sn;
        private boolean done = false;
        private DatagramSocket ds = null;

        public Provider(String sn) {
         super();
         this.sn = sn;
        }
        @Override
        public void run() {
            super.run();

            System.out.println("UDPProvider Started");

            try {

                // 作为接受者，指定一个端口用于数据接收
                ds = new DatagramSocket(20000);

                while (!done) {

                    // 构建接收实体
                    final byte[] buf = new byte[512];
                    DatagramPacket receivePack = new DatagramPacket(buf, buf.length);


                    System.out.println("UDPProvider 准备接收");
                    // 接收
                    ds.receive(receivePack);

                    System.out.println("UDPProvider 成功接收");
                    // 打印接收到的信息与发送者的信息
                    // 发送者的IP地址
                    String ip = receivePack.getAddress().getHostAddress();
                    int port = receivePack.getPort();
                    int dataLen = receivePack.getLength();
                    String data = new String(receivePack.getData(), 0, dataLen);
                    System.out.println("UDPProvider receive form ip:" + ip + "\tport：" + port + "\tdata：" + data);

                    int responsePort = MessageCreator.parsePort(data);

                    if(responsePort != -1) {

                        // 构建一份回送数据
                        String responseData = MessageCreator.buildWidthSn(sn);
                        byte[] responseDataBytes = responseData.getBytes();

                        /**
                         *  直接根据发送者构建按一份回送信息
                         *  Datagram: 数据电报
                         *  Packet: 数据包
                         *  @param buffer
                         *  @param buffer length
                         *  @param host
                         *  @param port
                         */
                        DatagramPacket responsePacket = new DatagramPacket(
                                responseDataBytes,
                                responseDataBytes.length,
                                receivePack.getAddress(),
                                responsePort
                        );
                        ds.send(responsePacket);
                    }

                    // 关闭需要等待一段时间，否则对面接收不到。
                    // ds.close();
                }
            }catch (Exception error) {
            }finally {
                close();
            }


            // 完成
            System.out.println("UDPProvider Finished");
        }

        private void close() {
            if(ds != null) {
                System.out.println("退出！");
                ds.close();
                ds = null;
            }
        }

        void exit() {
            System.out.println("关闭！");
            done = true;
            close();
        }
    }
}
