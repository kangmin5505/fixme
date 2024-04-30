package kr._42.seoul.server;

import java.time.LocalDateTime;
import kr._42.seoul.enums.BrokerCommandType;

public class OrderDetail {
    private BrokerCommandType orderType;
    private OrderStatus orderStatus;
    private String instrument;
    private int quantity;
    private int price;
    private String market;
    private LocalDateTime orderDateTime;
    private LocalDateTime updateDateTime;

    private OrderDetail() {}

    public static OrderBuilder builder() {
        return new OrderBuilder();
    }

    public void updateDateTime() {
        updateDateTime = LocalDateTime.now();
    }

    public static class OrderBuilder {
        private OrderDetail order = new OrderDetail();

        public OrderBuilder orderType(BrokerCommandType orderType) {
            order.orderType = orderType;
            return this;
        }

        public OrderBuilder orderStatus(OrderStatus orderStatus) {
            order.orderStatus = orderStatus;
            return this;
        }

        public OrderBuilder instrument(String instrument) {
            order.instrument = instrument;
            return this;
        }

        public OrderBuilder quantity(int quantity) {
            order.quantity = quantity;
            return this;
        }

        public OrderBuilder price(int price) {
            order.price = price;
            return this;
        }

        public OrderBuilder market(String market) {
            order.market = market;
            return this;
        }

        public OrderDetail build() {
            order.orderDateTime = LocalDateTime.now();
            return order;
        }
    }

    public BrokerCommandType getOrderType() {
        return orderType;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
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

    @Override
    public String toString() {
        return "OrderDetail [orderType=" + orderType + ", orderStatus=" + orderStatus
                + ", instrument=" + instrument + ", quantity=" + quantity + ", price=" + price
                + ", market=" + market + ", orderDateTime=" + orderDateTime + ", updateDateTime="
                + updateDateTime + "]";
    }

}
