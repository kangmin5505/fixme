package kr._42.seoul.client;

import kr._42.seoul.AsynchronousClient;
import kr._42.seoul.IOUtils;
import kr._42.seoul.enums.Command;
import kr._42.seoul.fix.ParameterToFIX;
import kr._42.seoul.input.Console;
import kr._42.seoul.parser.ParameterParser;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.ExecutionException;

public class AsynchronousBrokerClient extends AsynchronousClient {
    private static final ParameterParser paramParser = new ParameterParser();
    private final ByteBuffer buffer = ByteBuffer.allocate(bufferCapacity);

    public AsynchronousBrokerClient(String hostname, int port) throws IOException {
        super(hostname, port);
    }

    @Override
    protected void runImpl() {
        while (true) {
            Console.usage();
            String line = Console.readLine();
            String[] parameters = line.split("\\s+");

            paramParser.parse(parameters);

            if (!paramParser.isValid()) {
                Console.invalidInput();
                continue;
            }

            if (paramParser.getCommand() == Command.EXIT) {
                Console.exit();
                break;
            }

            buffer.clear();
            // TODO: check buffer overflow
            buffer.put(ParameterToFIX.convert(this.getId(), paramParser));
            buffer.flip();

            try {
                this.client.write(buffer).get();
                buffer.clear();

                Integer result = this.client.read(buffer).get();
                if (result == IOUtils.EOF) {
                    System.err.println("Fail to read from server");
                    break;
                }

                buffer.flip();
                logger.debug("Success to read from server : ({})", new String(buffer.array()).trim());
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
