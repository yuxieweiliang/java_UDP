import java.io.*;
import java.net.Inet4Address;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Created by asus on 2019/4/24.
 */
public class Client {
    public static void main(String[] args) throws IOException {
        Socket socket = new Socket();
        socket.setSoTimeout(3000);

        socket.connect(new InetSocketAddress(Inet4Address.getLocalHost(), 2000), 3000);

        System.out.print("已发起服务器连接，并进入后续流程~");
        System.out.print("客户端信息：" + socket.getLocalAddress() + "p:" + socket.getLocalPort());
        System.out.print("服务端信息：" + socket.getInetAddress() + "p:" + socket.getPort());

        try{
            todo(socket);
        } catch (Exception e) {
            System.out.print("异常关闭");
        }
        socket.close();
        System.out.print("客户端已退出！");
    }

    private static void todo(Socket client) throws IOException{
        InputStream in = System.in;
        BufferedReader input = new BufferedReader(new InputStreamReader(in));

        OutputStream outputStream = client.getOutputStream();
        PrintStream socketPrintStream = new PrintStream(outputStream);

        // 得到Socket输入流，并转换为BufferedReader
        InputStream inputStream = client.getInputStream();
        BufferedReader socketBufferedReader = new BufferedReader(new InputStreamReader(inputStream));

        boolean flag = true;
        do{
            // 键盘读取一行
            String str = input.readLine();
            // 发送到服务器
            socketPrintStream.println(str);

            // 送服务器读物一行
            String echo = socketBufferedReader.readLine();
            if("bye".equalsIgnoreCase(echo)) {
                flag = false;
            }else {
                System.out.println(echo);
            }
        }while (flag);

    }
}
