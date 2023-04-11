package io.github.k_tomaszewski.discovery.server;

import io.github.k_tomaszewski.discovery.JsonSerialization;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.function.Supplier;

public class DiscoveryServerBeanTest {

    private final String appName = "test app";
    private final DiscoveryServerParams params = new DiscoveryServerParams();
    private final JsonSerialization jsonSerialization = new JsonSerialization();
    private final Supplier<String> testHostnameSupplier = () -> "test-host";

    @Test
    public void shouldStartAndStopThread() {
        // given
        Thread threadMock = Mockito.mock(Thread.class);
        ServerThreadFactory threadFactoryMock = Mockito.mock(ServerThreadFactory.class);
        Mockito.when(threadFactoryMock.create(Mockito.any(), Mockito.any())).thenReturn(threadMock);

        DiscoveryServerBean theBean = new DiscoveryServerBean(appName, params, jsonSerialization, testHostnameSupplier, threadFactoryMock);

        // when
        boolean started = theBean.start();
        boolean startedAgain = theBean.start();
        boolean stopped = theBean.stop();
        boolean stoppedAgain = theBean.stop();

        // then
        Assertions.assertTrue(started);
        Assertions.assertTrue(stopped);
        Assertions.assertFalse(startedAgain);
        Assertions.assertFalse(stoppedAgain);

        Mockito.verify(threadFactoryMock, Mockito.times(1)).create(Mockito.eq(params), Mockito.any());
        Mockito.verify(threadMock, Mockito.times(1)).start();
        Mockito.verify(threadMock, Mockito.times(1)).interrupt();
    }
}
