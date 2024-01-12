package kr._42.seoul.client;

import kr._42.seoul.ClientMultiplexer;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.util.Set;

public class MarketClient extends ClientMultiplexer {
    public MarketClient(String hostname, int port) throws IOException {
        super(hostname, port);
    }

    @Override
    protected void implRun() {
        while (true) {
            try {
                logger.debug("Try to select");
                this.selector.select();

                Set<SelectionKey> selectedKeys = this.selector.selectedKeys();
                for (SelectionKey key : selectedKeys) {
                    logger.debug("{} {} {}", key.isReadable(), key.isWritable(), key.isConnectable());
                    if (key.isReadable()) {
                        this.read(key);
                    }
                }
                selectedKeys.clear();
                logger.debug("Success to select");
            } catch (IOException e) {
                System.err.println("Error occurred while server is running");
                break;
            }
        }
    }
}
