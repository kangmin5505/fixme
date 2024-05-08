package kr._42.seoul.common;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ServerSocket {
    private final Logger logger = LoggerFactory.getLogger(ServerSocket.class);
    private final static int BUFFER_SIZE = 1024;
    protected final ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
    protected ServerSocketChannel serverSocketChannel;
    protected Selector selector;
    
    public void open() throws IOException {
        this.serverSocketChannel = ServerSocketChannel.open();
        this.selector = Selector.open();
    }

    public void bind(int port) throws IOException {
        this.serverSocketChannel.bind(new InetSocketAddress(port));
        this.serverSocketChannel.configureBlocking(false);
        this.serverSocketChannel.register(this.selector, SelectionKey.OP_ACCEPT);
    }
    
    public void run() throws IOException {
        while (true) {
            try {
                this.selector.select();

                for (SelectionKey key : this.selector.selectedKeys()) {
                    if (!key.isValid()) {
                        continue;
                    }

                    if (key.isAcceptable()) {
                        this.accept(key);
                    } else if (key.isReadable()) {
                        this.read(key);
                    } else if (key.isWritable()) {
                        this.write(key);
                    }
                }

                this.selector.selectedKeys().clear();
            } catch (IOException e) {
                this.logger.error("Error occurred while selecting keys", e);
                e.printStackTrace();
            }
        }
    }

    protected abstract void accept(SelectionKey key) throws IOException;
    protected abstract void write(SelectionKey key) throws IOException;
    protected abstract void read(SelectionKey key) throws IOException;
}
