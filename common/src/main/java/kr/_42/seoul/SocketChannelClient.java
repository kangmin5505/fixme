package kr._42.seoul;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Set;

public abstract class SocketChannelClient implements AutoCloseable, Runnable {
    protected static final int BUFFER_CAPACITY = 1024;
    protected Selector selector;
    protected SocketChannel client;
    protected  ByteBuffer buffer;
    protected String id;


    protected static <T extends SocketChannelClient> T open(String hostname, int port, Class<T> clazz) {
        try {
            Constructor<T> declaredConstructor = clazz.getDeclaredConstructor();
            declaredConstructor.setAccessible(true);

            T client = declaredConstructor.newInstance();
            client.selector = Selector.open();
            client.client = SocketChannel.open(new InetSocketAddress(hostname, port));

            return client;
        } catch (IOException e) {
            throw new RuntimeException("Fail to open socket channel");
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Fail to create instance");
        }
    }

    @Override
    public void close() {
        IOUtils.close(this.client, this.selector);
    }

    @Override
    public void run() {
        this.setup();

        while (true) {
            try {
                this.selector.select();

                Set<SelectionKey> selectedKeys = this.selector.selectedKeys();
                for (SelectionKey key : selectedKeys) {
                    if (key.isReadable()) {
                        this.read(key);
                    }
                }
                selectedKeys.clear();
            } catch (IOException e) {
                System.err.println("Error occurred while server is running");
                break;
            }
        }
    }

    protected void read(SelectionKey key) {
        SocketChannel client = (SocketChannel) key.channel();

        try {
            int readBytes = client.read(this.buffer);

            if (readBytes == IOUtils.EOF) {
                client.close();
            } else if (readBytes > 0) {
                this.buffer.flip();
                client.write(this.buffer);
                this.buffer.clear();
            }
        } catch (IOException e) {
            throw new RuntimeException("Fail to read data from client");
        }

    }

    protected void setup() {
        try {
            this.buffer = ByteBuffer.allocate(BUFFER_CAPACITY);
            this.client.read(this.buffer);
            this.id = new String(this.buffer.array()).trim();
            this.buffer.clear();

            this.client.configureBlocking(false);
            this.client.register(this.selector, SelectionKey.OP_WRITE);

        } catch (IOException e) {
            throw new RuntimeException("Fail to setup");
        }

    }
}
