package kr._42.seoul.client;

import kr._42.seoul.AsynchronousClient;
import kr._42.seoul.enums.Command;
import kr._42.seoul.handler.WriteHandler;
import kr._42.seoul.input.Console;
import kr._42.seoul.parser.ParameterParser;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class AsynchronousBrokerClient extends AsynchronousClient {
    private static final ParameterParser paramParser = new ParameterParser();
    private static final int BUFFER_CAPACITY = 1024;
    private String id;

    public AsynchronousBrokerClient(String hostname, int port) throws IOException {
        super(hostname, port);
    }

    @Override
    protected void getID() {
        logger.debug("Try to get ID");

        Attachment attachment = new Attachment(this.channel, BUFFER_CAPACITY);
        ByteBuffer buffer = attachment.getBuffer();

        Future<Integer> read = this.channel.read(buffer);

        try {
            logger.debug("wait");
            read.get();
            logger.debug("done");
            System.out.println("ID : " + new String(buffer.array()).trim());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
//        Future<Integer> readResult = this.channel.read(buffer);
//
//        try {
//            Integer i = readResult.get();
//            System.out.println("result  :"  + i);
//            this.id = new String(buffer.array()).trim();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }


        logger.debug("Success to get ID({})", this.id);
    }

    @Override
    protected void runImpl() {
        Console.usage();

        while (true) {
            String line = Console.readLine();
            String[] strings = line.split("\\s+");

            paramParser.parse(strings);
            if (!paramParser.isValid()) {
                Console.invalidInput();
                Console.usage();
                continue;
            }

            if (paramParser.getCommand() == Command.EXIT) {
                Console.exit();
                break;
            }

            Attachment attachment = new Attachment(this.channel, BUFFER_CAPACITY);
            ByteBuffer buffer = attachment.getBuffer();
            buffer.put(line.getBytes(StandardCharsets.UTF_8));
            buffer.flip();

            this.channel.write(buffer, attachment, new WriteHandler());
        }
    }

}
