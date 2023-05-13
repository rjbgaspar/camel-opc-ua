package com.gv.camelopcua.camel.milo.opcua.component.prosyssimulationserver;

import lombok.RequiredArgsConstructor;
import org.apache.camel.builder.RouteBuilder;
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

//@Component
@RequiredArgsConstructor
public class ProsysSimulationServerConsumer extends RouteBuilder {
    @Value("${com.gv.component.milo-client.prosys-simulation-server.consumer.input}")
    String input;

    @Value("${com.gv.component.milo-client.prosys-simulation-server.consumer.output}")
    String output;

    @Value("${com.gv.component.milo-client.prosys-simulation-server.consumer.route-id}")
    String routeId;
    String clientId;

    /**
     * <b>Called on initialization to build the routes using the fluent builder syntax.</b>
     * <p/>
     * This is a central method for RouteBuilder implementations to implement
     * the routes using the Java fluent builder syntax.
     */
    @Override
    public void configure() {
        log.debug("Configuring route {} for endpoint: {}", routeId, input);

        from(input)
                .routeId(routeId)
                .process(exchange -> {
                    String routeId = exchange.getFromRouteId();
                    DataValue data = exchange.getIn().getBody(DataValue.class);
                    log.info("Route '{}': Status: {}, Value: {}",
                            routeId,
                            data.getStatusCode().toString(), data.getValue().getValue());
                })
                .log("Processing ${id}")
                .to(output);
    }
}
