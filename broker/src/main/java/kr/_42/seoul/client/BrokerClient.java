package kr._42.seoul.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class BrokerClient implements AutoCloseable {
    private static final Logger logger = LoggerFactory.getLogger(BrokerClient.class);
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private String id;

    private BrokerClient() {}


    public static BrokerClient open(String hostname, int port) {
        logger.debug("Try to connect server({}:{})", hostname, port);

        try {
            BrokerClient brokerClient = new BrokerClient();
            brokerClient.socket = new Socket(hostname, port);

            return brokerClient;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public void close() {
        logger.debug("Try to close broker client");

        try {
            if (this.in != null) {
                this.in.close();
            }
        } catch (IOException ignored) {}

        if (this.out != null) {
            this.out.close();
        }

        try {
            if (this.socket != null) {
                this.socket.close();
            }
        } catch (IOException ignored) {}

    }

    public void setupForRequest() {
        logger.debug("Try to setup for request");

        try {
            this.out = new PrintWriter(this.socket.getOutputStream(), true);
            this.in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
            this.id = this.in.readLine();

            logger.debug("Broker client id is {}", this.id);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String request() {
        logger.debug("Try to request message");

        this.out.println("I want to buy something at A market");

        try {
            return this.in.readLine();
        } catch (IOException e) {
            throw new RuntimeException("Fail to receive response from server");
        }
    }
}
