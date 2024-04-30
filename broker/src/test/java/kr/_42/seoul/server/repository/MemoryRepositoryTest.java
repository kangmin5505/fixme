package kr._42.seoul.server.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import kr._42.seoul.enums.BrokerCommandType;
import kr._42.seoul.enums.OrderStatus;
import kr._42.seoul.server.OrderDetail;

public class MemoryRepositoryTest {
    private final Repository memoryRepository = MemoryRepository.getInstance();

    @Test
    void test() {
        memoryRepository.addOrderDetail(OrderDetail.builder().orderType(BrokerCommandType.BUY)
                .orderStatus(OrderStatus.PENDING).instrument("instrument").quantity(100).price(1000)
                .market("market").build());
        memoryRepository.addOrderDetail(OrderDetail.builder().orderType(BrokerCommandType.BUY)
                .orderStatus(OrderStatus.PENDING).instrument("instrument").quantity(100).price(1000)
                .market("market").build());
        memoryRepository.addOrderDetail(OrderDetail.builder().orderType(BrokerCommandType.BUY)
                .orderStatus(OrderStatus.PENDING).instrument("instrument").quantity(100).price(1000)
                .market("market").build());

        assertEquals(memoryRepository.getOrderDetails().size(), 3);
    }
}
