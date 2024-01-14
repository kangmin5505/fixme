package kr._42.seoul.client;

import kr._42.seoul.AsynchronousClient;
import kr._42.seoul.enums.Command;
import kr._42.seoul.handler.WriteHandler;
import kr._42.seoul.input.Console;
import kr._42.seoul.parser.ParameterParser;

import java.io.IOException;
import java.nio.ByteBuffer;

public class AsynchronousBrokerClient extends AsynchronousClient {
    private static final ParameterParser paramParser = new ParameterParser();

    public AsynchronousBrokerClient(String hostname, int port) throws IOException {
        super(hostname, port);
    }

    @Override
    protected void runImpl() {
        while (true) {
            Console.usage();
            String line = Console.readLine();
            String[] strings = line.split("\\s+");

            paramParser.parse(strings);
            if (!paramParser.isValid()) {
                Console.invalidInput();
                continue;
            }

            if (paramParser.getCommand() == Command.EXIT) {
                Console.exit();
                break;
            }

            Attachment attachment = new Attachment(this.client, this.bufferCapacity);
            ByteBuffer buffer = attachment.getBuffer();

            // test
            buffer.put(line.getBytes());
            buffer.flip();

            this.client.write(buffer, attachment, new WriteHandler());
        }
    }
}
