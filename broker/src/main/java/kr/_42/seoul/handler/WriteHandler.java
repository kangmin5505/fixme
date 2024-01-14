package kr._42.seoul.handler;

import kr._42.seoul.AsynchronousClient;
import kr._42.seoul.Broker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

public class WriteHandler implements CompletionHandler<Integer, AsynchronousClient.Attachment> {
    private static final Logger logger = LoggerFactory.getLogger(WriteHandler.class);

    @Override
    public void completed(Integer result, AsynchronousClient.Attachment attachment) {
        logger.debug("Success to write");

        AsynchronousSocketChannel channel = attachment.getChannel();
        ByteBuffer buffer = attachment.getBuffer();
        buffer.clear();

        channel.read(buffer, attachment, new ReadHandler());
    }

    @Override
    public void failed(Throwable exc, AsynchronousClient.Attachment attachment) {
        System.err.println("Fail to write");
        System.exit(Broker.FAIL_STATUS);
    }
}
