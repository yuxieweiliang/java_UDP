import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by asus on 2019/4/24.
 */
public class Server {
    public static void main(String[] args) throws IOException {
        ServerSocket server = new ServerSocket(2000); // 服务端监听2000端口

        System.out.print("服务器准备就绪~");
        System.out.print("服务端信息：" + server.getInetAddress() + "p:" + server.getLocalPort());

        // 等待客户端连接
        for(;;) { // 无限循环
            Socket client = server.accept(); // 等待客户端连接2000端口
            ClientHandler clientHandler = new ClientHandler(client);
            clientHandler.start();
        }
    }

    /**
     * 建立异步处理类
     * Thread：新开启一个线程
     * start 为启动一个线程
     * run方法需要重写
     */
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
                PrintStream socketOutput = new PrintStream(socket.getOutputStream());
                BufferedReader socketInput = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                do{
                    String str = socketInput.readLine();
                    if("bye".equalsIgnoreCase(str)){
                        flag = false;
                        // 回送
                        socketOutput.println("bye");
                    }else {
                        System.out.println(str);
                        socketOutput.println("回送：" + str.length());
                    }
                }while (flag);
                socketInput.close();
                socketOutput.close();
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
