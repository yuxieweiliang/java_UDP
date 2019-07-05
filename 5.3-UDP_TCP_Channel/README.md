碰到的问题
client/TCPClient.java: 
socket.setSoTimeout(10000);

调用会在时间过后，向 server 发送 null
而 server 在收到 null 后，会退出。