package kr._42.seoul.client;

import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Queue;
import kr._42.seoul.BrokerMediator;
import kr._42.seoul.common.Request;
import kr._42.seoul.common.Response;
import kr._42.seoul.enums.BrokerCommand;

public class BrokerClient {
    private final RequestHandler requestHandler;
    private final ResponseHandler responseHandler;
    private BrokerMediator brokerMediator;
    private final Queue<Response> queue = new LinkedList<>();

    public BrokerClient(RequestHandler requestHandler,
            ResponseHandler responseHandler) {
        this.requestHandler = requestHandler;
        this.responseHandler = responseHandler;
    }

    public void registerBrokerMediator(BrokerMediator brokerMediator) {
        this.brokerMediator = brokerMediator;
    }

    public void run() {
        while (true) {
            try {
                Request request = requestHandler.getRequest();
                BrokerCommand command = request.getCommand();
                
                if (command == BrokerCommand.ORDER) {
                    brokerMediator.sendToBrokerServer(request);
                } else if (command == BrokerCommand.MESSAGE) {
                    this.handleResponse();
                } else {
                    System.exit(0);
                }
            } catch (NoSuchElementException e) { // EOF
                System.exit(0);
            } catch (Exception e) {
                responseHandler.error("[Failed to request]\n\t" + e.getMessage());
                continue;
            }
        }
    }

    public void handleResponse() {
        while (this.queue.isEmpty() == false) {
            Response response = this.queue.poll();
            responseHandler.handle(response);
        }
    }

    public void receive(Response response) {
        this.queue.add(response);        
    }
}
