package kr._42.seoul.client;

import kr._42.seoul.enums.BrokerCommand;
import kr._42.seoul.enums.BrokerMessageType;

public class Request {
    private BrokerCommand command;
    private BrokerMessageType msgType;
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

    public BrokerMessageType getMsgType() {
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
        private BrokerCommand command;
        private BrokerMessageType msgType;
        private String instrument;
        private int quantity;
        private int price;
        private String market;
        
        public RequestBuilder command(BrokerCommand command) {
            this.command = command;
            return this;
        }

        public RequestBuilder msgType(BrokerMessageType msgType) {
            this.msgType = msgType;
            return this;
        }

        public RequestBuilder instrument(String instrument) {
            this.instrument = instrument;
            return this;
        }

        public RequestBuilder quantity(int quantity) {
            this.quantity = quantity;
            return this;
        }

        public RequestBuilder price(int price) {
            this.price = price;
            return this;
        }

        public RequestBuilder market(String market) {
            this.market = market;
            return this;
        }

        public Request build() {
            Request request = new Request();
            request.command = this.command;
            request.msgType = this.msgType;
            request.instrument = this.instrument;
            request.quantity = this.quantity;
            request.price = this.price;
            request.market = this.market;
            return request;
        }
    }

    @Override
    public String toString() {
        return "Request [command=" + command + ", msgType=" + msgType + ", instrument=" + instrument + ", quantity="
                + quantity + ", price=" + price + ", market=" + market + "]";
    }
}
