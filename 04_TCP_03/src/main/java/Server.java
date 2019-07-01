import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.net.Inet4Address;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by asus on 2019/6/1.
 */
public class Server {
    private static final int PORT = 20000;
    public static void main(String[] args) throws IOException {
        ServerSocket server = createServerSocket();
        initServerSocket(server);

        // 绑定到本地端口上
        server.bind(new InetSocketAddress(Inet4Address.getLocalHost(), PORT), 50);

        System.out.println("服务器中被就绪！");
        System.out.println("服务器信息：" + server.getInetAddress() + " P: " + server.getLocalPort());

        for(;;) {
            Socket client = server.accept();
            // 客户端构建异步线程
            ClientHandler clientHander = new ClientHandler(client);

            // 启动线程
            clientHander.start();

        }
    }
    private static ServerSocket createServerSocket() throws IOException {
        ServerSocket serverSocket = new ServerSocket();

        // serverSocket.bind(new InetSocketAddress(Inet4Address.getLocalHost(), PORT), 50);

        // 绑定到本地端口号20000上，并且设置当前可允许等待连接的队列为50个
        // serverSocket = new ServerSocket(PORT);

        // 等效于上面的方案， 队列设置为50个
        // serverSocket = new SererSocket(PORT, 50);

        // 同上
        // serverSocket = new ServerSocket(PORT, 50, Inet4Address.getLocalHost());

        return serverSocket;
    }

    private static void initServerSocket(ServerSocket serverSocket) throws IOException {
        // 是否复用未完全关闭的端口
        serverSocket.setReuseAddress(true);

        // 等效于Socket#setREceiveBufferSize
        serverSocket.setReceiveBufferSize(64 * 1024 * 1024);

        // 设置serverSocket # accept 超时
        // serverSocket.setSoTimeout(2000);

        // 设置性能参数：短链接，延迟， 带宽的相对重要性
        serverSocket.setPerformancePreferences(1, 2, 1);
    }

    private static class ClientHandler extends Thread {
        private Socket socket;
        private boolean flag = true;
        ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            super.run();
            System.out.println("新客户端连接：" + socket.getInetAddress() + "p:" + socket.getPort());
            try{
                OutputStream outputStream = socket.getOutputStream();
                InputStream inputStream = socket.getInputStream();

                byte[] buffer = new byte[128];

                int readCount = inputStream.read(buffer);

                if(readCount > 0) {
                    int value = Tools.byteArrayToInt(buffer);
                    System.out.println("收到数量：" + readCount + " 数据：" + value);
                    outputStream.write(buffer, 0, readCount);
                } else {
                    System.out.println("没有收到：" + readCount );
                    outputStream.write(new byte[]{0});
                }

                // 释放资源
                outputStream.close();
                inputStream.close();
            }catch (Exception e) {
                System.out.println("连接异常断开");
            } finally {
                try{
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            System.out.println("客户端已退出" + socket.getInetAddress() + "p:" + socket.getPort());
        }
    }
}
