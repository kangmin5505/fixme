package kr._42.seoul.handler;

import kr._42.seoul.Broker;
import kr._42.seoul.client.AsynchronousBrokerClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.nio.channels.CompletionHandler;

public class ReadHandler implements CompletionHandler<Integer, AsynchronousBrokerClient.Attachment> {
    private static final Logger logger = LoggerFactory.getLogger(ReadHandler.class);

    @Override
    public void completed(Integer result, AsynchronousBrokerClient.Attachment attachment) {
        logger.debug("Success to read");

        ByteBuffer buffer = attachment.getBuffer();
        String response = new String(buffer.array()).trim();

        logger.debug("response : {}", response);
    }

    @Override
    public void failed(Throwable exc, AsynchronousBrokerClient.Attachment attachment) {
        System.err.println("Fail to read");
        System.exit(Broker.FAIL_STATUS);

    }
}
