package kr._42.seoul.client;

import kr._42.seoul.FIXMessage;
import kr._42.seoul.common.Response;
import kr._42.seoul.enums.MsgType;
import kr._42.seoul.field.Tag;

public class ConsoleResponseHandler implements ResponseHandler {

    @Override
    public void handle(Response response) {
        FIXMessage message = response.getFixMessage();

        try {
            String msgTypeString = (String) message.get(Tag.MSG_TYPE).getValue();
            MsgType msgType = MsgType.valueOf(msgTypeString);

            switch (msgType) {
                case EXECUTED:
                    this.handleExecuted(message);
                    break;
                case REJECTED:
                    this.handleRejected(message);
                    break;
                case INVALID:
                    this.handleInvalid(message);
                    break;
                default:
                    this.handleUnknown();
            }
        } catch (Exception e) {
            error("[Fail to handle response]\n\t" + e.getMessage());
        }
    }

    private void handleInvalid(FIXMessage message) {
        System.out.println("[Invalid request]");
    }

    private void handleUnknown() {
        System.out.println("[Unknown message type]");
    }

    @Override
    public void error(String message) {
        System.out.println(message);
    }

    private void handleExecuted(FIXMessage message) {
        String instrument = (String) message.get(Tag.INSTRUMENT).getValue();
        int price = (int) message.get(Tag.PRICE).getValue();
        int quantity = (int) message.get(Tag.QUANTITY).getValue();

        System.out.println("[Order Executed]");
        System.out.println("\tInstrument: " + instrument);
        System.out.println("\tPrice: " + price);
        System.out.println("\tQuantity: " + quantity);
    }

    private void handleRejected(FIXMessage message) {
        System.out.println("[Previous Order Rejected]");
    }
}
