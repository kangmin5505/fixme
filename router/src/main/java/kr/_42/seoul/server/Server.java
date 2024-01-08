package kr._42.seoul.server;

import kr._42.seoul.IOUtils;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.Set;

public abstract class Server implements AutoCloseable, Runnable {

    protected static final int BUFFER_CAPACITY = 1024;
    protected Selector selector;
    protected ServerSocketChannel serverSocket;
    protected final int port;

    protected Server(int port) {
        this.port = port;
    }


    protected static <T extends Server> T open(Class<T> clazz) {
        try {
            Constructor<T> declaredConstructor = clazz.getDeclaredConstructor();
            declaredConstructor.setAccessible(true);

            T server = declaredConstructor.newInstance();

            server.selector = Selector.open();
            server.serverSocket = ServerSocketChannel.open();

            return server;
        } catch (Exception e) {
            throw new RuntimeException("Fail to create instance");
        }
    }

    protected void setup() {
        try {
            this.serverSocket.bind(new InetSocketAddress(this.port));
            this.serverSocket.configureBlocking(false);
            this.serverSocket.register(this.selector, SelectionKey.OP_ACCEPT);
        } catch (IOException e) {
            throw new RuntimeException("Fail to setup market server");
        }
    }

    @Override
    public void close() {
        IOUtils.close(this.serverSocket, this.selector);
    }

    @Override
    public void run() {
        this.setup();

        while (true) {
            try {
                this.selector.select();

                Set<SelectionKey> selectedKeys = this.selector.selectedKeys();
                for (SelectionKey key : selectedKeys) {
                    if (key.isAcceptable()) {
                        this.accept(key);
                    } else if (key.isReadable()) {
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

    protected abstract void accept(SelectionKey key);
    protected abstract void read(SelectionKey key);
}
