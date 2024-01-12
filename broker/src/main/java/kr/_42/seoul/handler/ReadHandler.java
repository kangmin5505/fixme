package kr._42.seoul.handler;

import kr._42.seoul.client.AsynchronousBrokerClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.nio.channels.CompletionHandler;

public class ReadHandler implements CompletionHandler<Integer, AsynchronousBrokerClient.Attachment> {
    private static final Logger logger = LoggerFactory.getLogger(WriteHandler.class);

    @Override
    public void completed(Integer result, AsynchronousBrokerClient.Attachment attachment) {
        logger.debug("read completed");

        ByteBuffer buffer = attachment.getBuffer();
        String s = new String(buffer.array());

        logger.debug("response : {}", s);
    }

    @Override
    public void failed(Throwable exc, AsynchronousBrokerClient.Attachment attachment) {
        logger.debug("read failed");
    }
}
