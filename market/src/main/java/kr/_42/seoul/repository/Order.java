package kr._42.seoul.repository;

public class Order {
    private Long orderID;
    private String brokerID;
    private String instrument;
    private int quantity;
    private int price;

    public Long getOrderID() {
        return this.orderID;
    }
    
    public String getBrokerID() {
        return brokerID;
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

    public void setOrderID(Long orderID) {
        this.orderID = orderID;
    }

    public void updateQuantity(int remianQuantity) {
        this.quantity = remianQuantity;
    }

    public static OrderBuilder builder() {
        return new OrderBuilder();
    }
    
    @Override
    public String toString() {
        return "Order [brokerID=" + brokerID + ", instrument=" + instrument
                + ", quantity=" + quantity + ", price=" + price + "]";
    }

    private Order() {}

    public static class OrderBuilder {
        private Order order = new Order();

        public OrderBuilder brokerID(String brokerID) {
            order.brokerID = brokerID;
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

        public Order build() {
            return order;
        }
    }
}
