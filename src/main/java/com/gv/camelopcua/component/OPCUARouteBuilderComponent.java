package com.gv.camelopcua.component;

import com.gv.camelopcua.config.Config;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.camel.AggregationStrategy;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;
import org.springframework.stereotype.Component;

import java.util.Arrays;

import static org.apache.camel.component.milo.MiloConstants.HEADER_NODE_IDS;

@Component
@RequiredArgsConstructor
@Log4j2
public class OPCUARouteBuilderComponent extends RouteBuilder {
    private final Config config;

    /**
     * <b>Called on initialization to build the routes using the fluent builder syntax.</b>
     * <p/>
     * This is a central method for RouteBuilder implementations to implement
     * the routes using the Java fluent builder syntax.
     */
    @Override
    public void configure() {
        log.debug("Configuring route for endpoint: {}", config.getEndpoint());

		from(config.getEndpoint())
				.routeId("Route for node=(ns=3;i=1002)")
				.process(exchange -> {
					String routeId = exchange.getFromRouteId();
					DataValue data = exchange.getIn().getBody(DataValue.class);
					log.info("Route '{}': Status: {}, Value: {}",
							routeId,
							data.getStatusCode().toString(), data.getValue().getValue());
				})
                .log("Processing ${id}")
                .to("direct:start");
    }
}
