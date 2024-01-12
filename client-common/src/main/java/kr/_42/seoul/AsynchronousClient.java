package kr._42.seoul;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.Future;

public abstract class AsynchronousClient implements AutoCloseable {
    protected static final Logger logger = LoggerFactory.getLogger(AsynchronousClient.class);
    protected AsynchronousSocketChannel channel;
    protected String hostname;
    protected int port;


    protected AsynchronousClient(String hostname, int port) throws IOException {
        this.channel = AsynchronousSocketChannel.open();
        this.hostname = hostname;
        this.port = port;
    }

    protected void init() {
        logger.debug("Try to init client");

        Attachment attachment = new Attachment(this.channel, 1024);

        Future<Void> connect = this.channel.connect(new InetSocketAddress(this.hostname, this.port));


        try {
            logger.debug("Before connecting");
            Void unused = connect.get();
            logger.debug("After connecting");

            SocketAddress remoteAddress = this.channel.getRemoteAddress();
            System.out.println(remoteAddress );

            this.channel.read(attachment.getBuffer(), attachment, new CompletionHandler<Integer, Attachment>() {
                @Override
                public void completed(Integer result, Attachment attachment) {
                    ByteBuffer buffer = attachment.getBuffer();
                    String trim = new String(buffer.array()).trim();
                    System.out.println("trim : " + trim);
                }

                @Override
                public void failed(Throwable exc, Attachment attachment) {

                }
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


//        this.channel.connect(new InetSocketAddress(this.hostname, this.port), attachment, new CompletionHandler<Void, Attachment>() {
//            @Override
//            public void completed(Void result, Attachment attachment) {
//                logger.debug("Try to read");
//                AsynchronousSocketChannel channel1 = attachment.getChannel();
//                ByteBuffer buffer = attachment.getBuffer();
//                channel1.read(buffer, attachment, new CompletionHandler<Integer, Attachment>() {
//                    @Override
//                    public void completed(Integer result, Attachment attachment) {
//                        logger.debug("Try to read");
//                        ByteBuffer buffer = attachment.getBuffer();
//                        buffer.flip();
//                        System.out.println("ID : " + new String(buffer.array()).trim());
//                        buffer.clear();
//                        logger.debug("Success to read");
//                    }
//
//                    @Override
//                    public void failed(Throwable exc, Attachment attachment) {
//
//                    }
//                });
//            }
//
//            @Override
//            public void failed(Throwable exc, Attachment attachment) {
//
//            }
//        });

            logger.debug("Success to init client");
    }

    protected void run() {
        logger.debug("Running client");
        this.init();
//        this.getID();

        runImpl();
    }

    protected abstract void getID();

    protected abstract void runImpl();

    @Override
    public void close() {
        IOUtils.close(channel);
    }

    public static class Attachment {
        private final AsynchronousSocketChannel channel;
        private final ByteBuffer buffer;

        public Attachment(AsynchronousSocketChannel channel, int bufferCapacity) {
            this.channel = channel;
            this.buffer = ByteBuffer.allocate(bufferCapacity);
        }

        public AsynchronousSocketChannel getChannel() {
            return channel;
        }

        public ByteBuffer getBuffer() {
            return buffer;
        }
    }
}