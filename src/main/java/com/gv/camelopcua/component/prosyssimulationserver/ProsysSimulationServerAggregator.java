package com.gv.camelopcua.component.prosyssimulationserver;

import com.gv.camelopcua.milo.processor.OpcUaToDtoProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.camel.AggregationStrategy;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Value;

import java.util.Arrays;

import static org.apache.camel.component.milo.MiloConstants.HEADER_AWAIT;
import static org.apache.camel.component.milo.MiloConstants.HEADER_NODE_IDS;
import static org.apache.camel.component.rest.RestConstants.CONTENT_TYPE;

//@Component
@RequiredArgsConstructor
@Log4j2
public class ProsysSimulationServerAggregator extends RouteBuilder {

    @Value("${com.gv.component.milo-client.prosys-simulation-server.aggregator.input}")
    String input;
    @Value("${com.gv.component.milo-client.prosys-simulation-server.aggregator.output}")
    String output;
    @Value("${com.gv.component.milo-client.prosys-simulation-server.aggregator.enricher}")
    String enricher;

    @Value("${com.gv.component.milo-client.prosys-simulation-server.aggregator.enricher-header-node-ids}")
    String[] enricherHeaderNodeIds;

    @Value("${com.gv.component.milo-client.prosys-simulation-server.aggregator.route-id}")
    String routeId;

    final OpcUaToDtoProcessor processor;

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
                .log("Processing aggregation ${body}")
                .setHeader(HEADER_NODE_IDS, constant(enricherHeaderNodeIds))
				.setHeader(HEADER_AWAIT, constant(true)) // await: parameter "defaultAwaitWrites"
                .enrich(enricher, new AggregationStrategy() {
                    @Override
                    public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
                        return newExchange;
                    }
                })
                .process(processor)
                .log("Process message with body= ${body}")
                .setHeader(CONTENT_TYPE, constant("Application/json"))
//                    .setBody(
//                            simple("{ "property": "value"}")
//                    )
                .to(output);

    }
}
