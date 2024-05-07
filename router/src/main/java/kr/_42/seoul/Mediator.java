package kr._42.seoul;

import java.nio.ByteBuffer;
import kr._42.seoul.field.Tag;

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

    public void sendToMarketRouter(ByteBuffer byteBuffer) {
        FIXMessage message = new FIXMessage(byteBuffer);
        String marketID = (String) message.get(Tag.ID).getValue();

        marketRouter.sendToMarket(byteBuffer, marketID);
    }

    public void sendToBrokerRouter(ByteBuffer byteBuffer) {
        FIXMessage message = new FIXMessage(byteBuffer);
        String brokerID = (String) message.get(Tag.BROKER_ID).getValue();

        brokerRouter.sendToBroker(byteBuffer, brokerID);
    }
}
