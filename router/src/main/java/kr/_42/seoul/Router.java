package kr._42.seoul;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Router {
    private static final Logger logger = LoggerFactory.getLogger(Router.class);

    private static final int BROKER_PORT = 5000;
    private static final int MARKET_PORT = 5001;
    public static void main(String[] args) {
        ExecutorService executorService = Executors.newFixedThreadPool(2);

        executorService.submit(() -> {
            try {
                Selector selector = Selector.open();

                ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
                serverSocketChannel.configureBlocking(false);
                serverSocketChannel.bind(new InetSocketAddress(BROKER_PORT));
                serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

                while (true) {
                    selector.select();
                    Set<SelectionKey> selectedKeys = selector.selectedKeys();

                    selectedKeys.forEach(key -> {
                        try {

                            if (key.isAcceptable()) {
                                // Accept the connection
                                ServerSocketChannel server = (ServerSocketChannel) key.channel();
                                SocketChannel client = server.accept();

                                logger.debug("Accept the connection from {}", client);

                                // Non-blocking mode for the new channel
                                client.configureBlocking(false);

                                client.register(selector, SelectionKey.OP_READ);
                                // Register the new channel with the selector for read operations
                                String welcomeMessage = "1234";

                                try {
                                    Thread.sleep(2000);
                                } catch (InterruptedException e) {
                                    throw new RuntimeException(e);
                                }
                                // Send the unique ID to the client
                                ByteBuffer buffer = ByteBuffer.wrap(welcomeMessage.getBytes());
                                Socket socket = client.socket();
                                new PrintWriter(socket.getOutputStream(), true).println(welcomeMessage);
                                logger.debug("END acceptable : " + key);

                            } else if (key.isReadable()) {
                                // Read data from the client
                                SocketChannel client = (SocketChannel) key.channel();
                                ByteBuffer buffer = ByteBuffer.allocate(1024);
                                logger.debug("Read data from {}", client);
                                int bytesRead = client.read(buffer);

                                if (bytesRead == -1) {
                                    // Client has closed the connection
                                    client.close();
                                } else if (bytesRead > 0) {
                                    // Make buffer ready for reading
                                    buffer.flip();

                                    // Echo the data back to the client
                                    client.write(buffer);

                                    // Make buffer ready for writing again
                                    buffer.clear();
                                }
                            }
                        } catch (IOException e) {
                        }
                    });
                    selectedKeys.clear();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        });
//        executorService.submit(() -> {
//        });

        executorService.shutdown();

        // 5000 port(thread A), 5001 port(thread B) 생성
        //
        // non-blocking 소켓 생성
        // 메시지 검사
    }

}
