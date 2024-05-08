package kr._42.seoul.common;

import java.nio.ByteBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import kr._42.seoul.FIXMessage;
import kr._42.seoul.broker.BrokerRouter;
import kr._42.seoul.field.Tag;
import kr._42.seoul.market.MarketRouter;
import kr._42.seoul.validator.BrokerRoutingValidator;
import kr._42.seoul.validator.ChecksumValidator;
import kr._42.seoul.validator.MarketRoutingValidator;
import kr._42.seoul.validator.Validator;

public class Mediator {
    private static final Logger logger = LoggerFactory.getLogger(Mediator.class);
    private BrokerRouter brokerRouter;
    private MarketRouter marketRouter;
    private Validator brokerValidator;
    private Validator marketValidator;

    public Mediator() {
        Validator brokerRoutingValidator = new BrokerRoutingValidator();
        Validator brokerChecksumValidator = new ChecksumValidator();
        brokerChecksumValidator.setNextValidator(brokerRoutingValidator);
        this.brokerValidator = brokerChecksumValidator;

        Validator marketRoutingValidator = new MarketRoutingValidator();
        Validator marketChecksumValidator = new ChecksumValidator();
        marketChecksumValidator.setNextValidator(marketRoutingValidator);
        this.marketValidator = marketChecksumValidator;
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
        if (this.brokerValidator.validate(byteBuffer) == false) {
            this.sendToBrokerInvalidMessage(byteBuffer);
            return;
        }

        FIXMessage message = new FIXMessage(byteBuffer);
        String marketID = (String) message.get(Tag.MARKET).getValue();
        marketRouter.sendToMarket(byteBuffer, marketID);
    }
    public void sendToBrokerRouter(ByteBuffer byteBuffer) {
        if (this.marketValidator.validate(byteBuffer) == false) {
            this.sendToMarketInvalidMessage(byteBuffer);
            return;
        }

        FIXMessage message = new FIXMessage(byteBuffer);
        String brokerID = (String) message.get(Tag.BROKER_ID).getValue();
        brokerRouter.sendToBroker(byteBuffer, brokerID);
    }

    private void sendToBrokerInvalidMessage(ByteBuffer byteBuffer) {
        FIXMessage fixMessage = new FIXMessage(byteBuffer);
        String brokerID = (String) fixMessage.get(Tag.ID).getValue();
        ByteBuffer invalidMessage = FIXMessage.builder().msgType("INVALID").build()
                .toByteBuffer();
        brokerRouter.sendToBroker(invalidMessage, brokerID);

        logger.info("Sending invalid message to broker client: {}", brokerID);
    }


    private void sendToMarketInvalidMessage(ByteBuffer byteBuffer) {
        FIXMessage fixMessage = new FIXMessage(byteBuffer);
        String marketID = (String) fixMessage.get(Tag.ID).getValue();
        ByteBuffer invalidMessage = FIXMessage.builder().msgType("INVALID").build()
                .toByteBuffer();
        marketRouter.sendToMarket(invalidMessage, marketID);

        logger.info("Sending invalid message to market client: {}", marketID);
    }
}
