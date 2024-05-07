package kr._42.seoul;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ClientSocket {
    private final Logger logger = LoggerFactory.getLogger(ClientSocket.class);
    private final static int BUFFER_SIZE = 1024;
    protected final ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
    protected String id;
    protected SocketChannel socketChannel;
    protected Selector selector;

    public void open() throws IOException {
        this.socketChannel = SocketChannel.open();
        this.selector = Selector.open();
    }

    public void connect(String hostname, int port) throws IOException {
        this.socketChannel.connect(new InetSocketAddress(hostname, port));

        this.setID();
        this.socketChannel.configureBlocking(false);
        this.socketChannel.register(this.selector, SelectionKey.OP_READ);

        this.logger.info("Connected to server with ID: {}", this.id);
    }

    protected void close() {
        try {
            this.socketChannel.close();
            this.selector.close();
        } catch (IOException e) {
            this.logger.error("Error occurred while closing socket channel", e);
        }
    }

    private void setID() throws IOException {
        this.socketChannel.read(this.buffer);
        this.buffer.flip();
        this.id = new String(this.buffer.array()).trim();
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