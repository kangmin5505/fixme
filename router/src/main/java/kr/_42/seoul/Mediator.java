package kr._42.seoul;

public class Mediator {

    private BrokerRouter brokerRouter;
    private MarketRouter marketRouter;

    public void run() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'run'");
    }

    public void registerBrokerRouter(BrokerRouter brokerRouter) {
        this.brokerRouter = brokerRouter;
        brokerRouter.registerMediator(this);
    }

    public void registerMarketRouter(MarketRouter marketRouter) {
        this.marketRouter = marketRouter;
        marketRouter.registerMediator(this);
    }

    public void sendMarketRouter(FIXMessage message) {
        marketRouter.sendToMarket(message);
    }
}
