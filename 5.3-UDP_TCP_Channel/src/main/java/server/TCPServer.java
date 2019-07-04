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
 * Created by asus on 2019/7/4.
 */
public class TCPServer {
    private final int port;
    private ClientListener mListener;
    private final List<ClientHandler> clientHandlerList = new ArrayList<>();

    public TCPServer(int port) { this.port = port;}

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

    public void stop() {
        if(mListener != null) {
            mListener.exit();
        }

        // 退出
        for( ClientHandler clientHandler : clientHandlerList) {
            clientHandler.exit();
        }

        // 清空
        clientHandlerList.clear();
    }


    public void broadCase(String str) {
        for( ClientHandler clientHandler : clientHandlerList) {
            clientHandler.send(str);
        }
    }

    private class ClientListener extends Thread {
        private ServerSocket server;
        private boolean done = false;

        private ClientListener(int port) throws IOException {
            server = new ServerSocket(port);
            System.out.println("服务器信息：" + server.getInetAddress() + "P：" + server.getLocalPort());
        }

        @Override
        public void run() {
            super.run();

            System.out.println("服务器准备就绪~");
            do {
                Socket client;
                try {
                    client = server.accept();
                }catch (IOException e) {
                    continue;
                }

                try {
                    /**
                     * 客户端构建异步线程
                     * 第一种写法等同于第二种写法
                     */
                    ClientHandler clientHandler = new ClientHandler(client, handler -> clientHandlerList.remove(handler));
                    /*ClientHandler clientHandler = new ClientHandler(client, new ClientHandler.CloseNotify() {
                        @Override
                        public void onSelfClosed(ClientHandler handler) {
                            clientHandlerList.remove(handler);
                        }
                    });*/


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
