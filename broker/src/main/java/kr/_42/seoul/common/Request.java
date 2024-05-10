package kr._42.seoul.common;

import kr._42.seoul.enums.BrokerCommand;
import kr._42.seoul.enums.MsgType;

public class Request {
    private BrokerCommand command;
    private MsgType msgType;
    private String instrument;
    private int quantity;
    private int price;
    private String market;

    private Request() {}

    public static RequestBuilder builder() {
        return new RequestBuilder();
    }

    public BrokerCommand getCommand() {
        return command;
    }

    public MsgType getMsgType() {
        return msgType;
    }

    public String getInstrument() {
        return instrument;
    }

    public int getQuantity() {
        return quantity;
    }

    public int getPrice() {
        return price;
    }

    public String getMarket() {
        return market;
    }

    public static class RequestBuilder {
        private Request request = new Request();

        public RequestBuilder command(BrokerCommand command) {
            request.command = command;
            return this;
        }

        public RequestBuilder msgType(MsgType msgType) {
            request.msgType = msgType;
            return this;
        }

        public RequestBuilder market(String market) {
            request.market = market;
            return this;
        }

        public RequestBuilder instrument(String instrument) {
            request.instrument = instrument;
            return this;
        }

        public RequestBuilder price(int price) {
            request.price = price;
            return this;
        }

        public RequestBuilder quantity(int quantity) {
            request.quantity = quantity;
            return this;
        }            
        public Request build() {
            return request;
        }
    }

    @Override
    public String toString() {
        return "Request [command=" + command + ", msgType=" + msgType + ", instrument=" + instrument
                + ", quantity=" + quantity + ", price=" + price + ", market=" + market + "]";
    }
}
