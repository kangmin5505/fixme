package kr._42.seoul;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ClientSocket {
    private final Logger logger = LoggerFactory.getLogger(ClientSocket.class);
    protected SocketChannel socketChannel;
    protected Selector selector;

    public void open() throws IOException {
        this.socketChannel = SocketChannel.open();
        this.selector = Selector.open();
    }

    public void connect(String hostname, int port) throws IOException {
        this.socketChannel.connect(new InetSocketAddress(hostname, port));
        this.socketChannel.configureBlocking(false);
        this.socketChannel.register(this.selector, SelectionKey.OP_READ);
    }

    public void run() {
        while (true) {
            try {
                this.selector.select();

                for (SelectionKey key : this.selector.selectedKeys()) {
                    if (!key.isValid()) {
                        continue;
                    }

                    if (key.isWritable()) {
                        this.write(key);
                    } else if (key.isReadable()) {
                        this.read(key);
                    }
                }
    
                this.selector.selectedKeys().clear();
            } catch (IOException e) {
                this.logger.error("Error occurred while selecting keys", e);
                e.printStackTrace();
            }            
        }
    }
    
    abstract protected void write(SelectionKey key) throws IOException;
    abstract protected void read(SelectionKey key) throws IOException;
}