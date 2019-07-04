import java.io.*;
import java.net.*;

public class Client {
    private static final int PORT = 20000;
    private static final int LOCAL_PORT = 20001;
    public static void main(String[] args) throws IOException {
        Socket socket = createSocket();
        initSocket(socket);

        //

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

        // 无代理模式
        // Socket socket = new Socket(Proxy.NO_PROXY);

        // 新建一份具有http代理的套接字，传输数据将通过www.baodu.com：8088端口转发
        // Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(Inet4Address.getByName("www.baidu.com"), 8080));
        // socket = new Socket(proxy);

        // 新建一个套接字，并且直接链接到本地20000服务器上
        // socket = new Socket("localhost", 20000);
        // 新建一个套接字，并且直接链接到本地20000服务器上
        // socket = new Socket(Inet4Address.getLocalHost(), PORT);

        // 新建一个套接字，并且直接连接到本地20000的服务器上，并且绑定到本地20001端口上
        // socket = new Socket("localhost", PORT, Inet4Address.getLocalHost(), LOCAL_PORT);
        // socket = new Socket(Inet4Address.getLocalHost(), PORT, Inet4Address.getLocalHost(), LOCAL_PORT);

        Socket socket = new Socket();
        socket.bind(new InetSocketAddress(Inet4Address.getLocalHost(), LOCAL_PORT));

        return socket;
    }

    private static void todo(Socket client) throws IOException{
        // 构建键盘输入流
        InputStream in = System.in;
        BufferedReader input = new BufferedReader(new InputStreamReader(in));

        // 得到Socket输出流，并转换为打印流
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
