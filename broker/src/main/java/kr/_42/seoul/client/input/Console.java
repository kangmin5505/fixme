package kr._42.seoul.client.input;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.channels.SocketChannel;
import java.util.Scanner;

public class Console implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(Console.class);
    private final SocketChannel client;

    public Console(SocketChannel client) {
        Thread.currentThread().setName("Console");
        this.client = client;
    }

    @Override
    public void run() {
        logger.debug("Running console");

        Scanner scanner = new Scanner(System.in);
        while (true) {
            this.usage();
            this.inputSymbol();

            String line = scanner.nextLine();

            String[] stringArray = line.split("\\s+");

            if (stringArray.length == 0) {
                continue;
            }


        }
    }

    private void usage() {
        System.out.println();

        System.out.println("Usage");
        System.out.println("\t[sell|buy] [instrument] [quantity] [market] [price]");
        System.out.println("\tmarkets");
        System.out.println("\tmarket [market]");
        System.out.println("\texit");

        System.out.println();
    }

    private void inputSymbol() {
        System.out.println("> ");
    }

}
