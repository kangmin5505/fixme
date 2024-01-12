package kr._42.seoul.idgenerator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

class BrokerIDGeneratorTest {

    @Test
    @DisplayName("multi-thead test")
    void multiThreadTest() throws InterruptedException {
        int LAST_ID = 1000;
        int THREAD_POOL_SIZE = Runtime.getRuntime().availableProcessors();
        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
        CountDownLatch countDownLatch = new CountDownLatch(LAST_ID);

        IntStream.range(0, LAST_ID)
                .forEach(i -> executorService.execute(() -> {
                    String id = BrokerIDGenerator.generate();
                    countDownLatch.countDown();
                }));

        countDownLatch.await();

        String id = BrokerIDGenerator.generate();
        String expect = String.format("%06d", LAST_ID);

        assertThat(id).isEqualTo(expect);
    }

}