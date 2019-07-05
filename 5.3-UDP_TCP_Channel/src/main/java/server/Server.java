package server;

import constants.TCPConstants;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * 服务端 启动入口
 */
public class Server {
    public static void main(String[] args) throws IOException {
        // 端口号 30401
        TCPServer tcpServer = new TCPServer(TCPConstants.PORT_SERVER);
        // TCP服务 启动
        boolean isSucceed = tcpServer.start();
        // 如果失败 -》 打印并退出
        if(!isSucceed) {
            System.out.println("Start TCP server failed!");
            return;
        }

        // UDP服务 启动 | 端口号 30401
        UDPProvider.start(TCPConstants.PORT_SERVER);

        // 创建一个可读流
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        String str;

        // UDP 接收到消息，然后发送给所有的 TCP 客户端
        do {
            // 读取一行
            str = bufferedReader.readLine();
            // 发送广播
            tcpServer.broadCase(str);
        } while (!"00bye00".equalsIgnoreCase(str));


        UDPProvider.stop();
        tcpServer.stop();
    }
}
