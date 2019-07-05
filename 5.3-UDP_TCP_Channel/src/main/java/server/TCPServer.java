package server;

import com.sun.jmx.remote.internal.ClientListenerInfo;
import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;
import server.handle.ClientHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * 创建一个 TCP 服务
 */
public class TCPServer {
    private final int port;
    private ClientListener mListener;
    private final List<ClientHandler> clientHandlerList = new ArrayList<>();

    /**
     * 传入 TCP 端口号
     */
    public TCPServer(int port) { this.port = port;}

    /**
     * 初始化一个 TCP 服务
     * 并且启动
     */
    public boolean start() {
        try {
            ClientListener listener = new ClientListener(port);
            mListener = listener;
            listener.start();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 退出 TCP 服务
     */
    public void stop() {
        if(mListener != null) {
            mListener.exit();
        }

        // 循环退出所有
        for( ClientHandler clientHandler : clientHandlerList) {
            clientHandler.exit();
        }

        // 清空
        clientHandlerList.clear();
    }

    /**
     * 发送广播 | 循环发送
     */
    public void broadCase(String str) {
        for( ClientHandler clientHandler : clientHandlerList) {
            clientHandler.send(str);
        }
    }

    /**
     * 封装 TCP 服务
     * 服务在单独的一个线程
     */
    private class ClientListener extends Thread {
        private ServerSocket server;
        private boolean done = false;

        /**
         * 初始化一个 ServerSocket 服务器
         * Socket类代表一个客户端套接字，即任何时候连接到一个远程服务器应用时构建所需的 socket 实例。
         * ServerSocket:
         * 是一个 Socket 服务器
         * 是等待客户端的请求，一旦获得一个连接请求，就创建一个 socket 实例来与客户端进行通信。
         */
        private ClientListener(int port) throws IOException {
            server = new ServerSocket(port);
            System.out.println("服务器 ServerSocket(TCP) 信息：" + server.getInetAddress() + "P：" + server.getLocalPort());
        }

        /**
         * 启动
         */
        @Override
        public void run() {
            super.run();

            do {
                Socket client;
                try {
                    System.out.println("服务器 ServerSocket(TCP) 准备就绪，等待客户端连接......");
                    /// 启动监听，等待连接
                    client = server.accept();
                }catch (IOException e) {
                    continue;
                }

                System.out.println("服务器 ServerSocket(TCP) 接收数据......");

                try {

                    /// 构建异步线程 | 启动 读写分离实例 （第一种写法等同于第二种写法）
                    ClientHandler clientHandler = new ClientHandler(client, handler -> clientHandlerList.remove(handler));
                    /*ClientHandler clientHandler = new ClientHandler(client, new ClientHandler.CloseNotify() {
                        @Override
                        public void onSelfClosed(ClientHandler handler) {
                            clientHandlerList.remove(handler);
                        }
                    });*/

                    /// 添加一个新的到列表
                    clientHandlerList.add(clientHandler);

                    // 启动线程
                    clientHandler.readToPrint();

                } catch (IOException e) {
                    e.printStackTrace();
                    System.out.println("客户端连接异常");
                }
            } while (!done);

            System.out.println("服务器已关闭！");
        }
        void exit() {
            done = true;
            try {
                server.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
