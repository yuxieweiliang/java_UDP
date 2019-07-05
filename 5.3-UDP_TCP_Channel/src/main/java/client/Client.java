package client;


import client.bean.ServerInfo;

import java.io.IOException;

public class Client {
    public static void main(String[] args) {
        ServerInfo info = UDPSearcher.searchServer(10000);

        // ServerInfo info = ClientSearcher.searchServer(10000);
        System.out.println("Server:" + (info != null));

        if(info != null) {
            System.out.println("Server:" + info.getAddress() + "\n" + info.getPort());
            try {
                TCPClient.linkWith(info);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
