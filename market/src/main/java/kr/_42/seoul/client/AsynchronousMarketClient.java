package kr._42.seoul.client;

import kr._42.seoul.AsynchronousClient;
import kr._42.seoul.IOUtils;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.ExecutionException;

public class AsynchronousMarketClient extends AsynchronousClient {
    public AsynchronousMarketClient(String hostname, int port) throws IOException {
        super(hostname, port);
    }

    @Override
    protected void runImpl() {
        ByteBuffer buffer = ByteBuffer.allocate(this.bufferCapacity);

        while (true) {
            logger.debug("Try to read from server");
            try {

                Integer result = this.client.read(buffer).get();
                if (result == IOUtils.EOF) {
                    logger.debug("Server is closed");
                    break;
                }
                System.out.println("Do something");
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
