package kr._42.seoul.handler;

import kr._42.seoul.client.AsynchronousBrokerClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

public class WriteHandler implements CompletionHandler<Integer, AsynchronousBrokerClient.Attachment> {
    private static final Logger logger = LoggerFactory.getLogger(WriteHandler.class);

    @Override
    public void completed(Integer result, AsynchronousBrokerClient.Attachment attachment) {
        logger.debug("write completed");

        AsynchronousSocketChannel channel = attachment.getChannel();
        ByteBuffer buffer = attachment.getBuffer();
        buffer.clear();

        channel.read(buffer, attachment, new ReadHandler());
    }

    @Override
    public void failed(Throwable exc, AsynchronousBrokerClient.Attachment attachment) {
        logger.debug("write failed");
    }
}
