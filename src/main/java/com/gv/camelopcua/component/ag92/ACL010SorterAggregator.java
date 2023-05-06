package com.gv.camelopcua.component.ag92;

import com.gv.camelopcua.milo.processor.OpcUaToDtoProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import static org.apache.camel.component.milo.MiloConstants.HEADER_AWAIT;
import static org.apache.camel.component.milo.MiloConstants.HEADER_NODE_IDS;
import static org.apache.camel.component.rest.RestConstants.CONTENT_TYPE;

@Component
@RequiredArgsConstructor
@Slf4j
public class ACL010SorterAggregator extends RouteBuilder {

    final OpcUaToDtoProcessor processor;
    @Value("${com.gv.component.milo-client.acl-010-sorter.aggregator.input}")
    String input;
    @Value("${com.gv.component.milo-client.acl-010-sorter.aggregator.output}")
    String output;
    @Value("${com.gv.component.milo-client.acl-010-sorter.aggregator.enricher}")
    String enricher;
    @Value("${com.gv.component.milo-client.acl-010-sorter.aggregator.enricher-header-node-ids}")
    String[] enricherHeaderNodeIds;
    @Value("${com.gv.component.milo-client.acl-010-sorter.aggregator.route-id}")
    String routeId;

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
                .enrich(enricher, (oldExchange, newExchange) -> newExchange)
                .process(processor)
                .log("Process message with body= ${body}")
                .setHeader(CONTENT_TYPE, constant("Application/json"))
//                    .setBody(
//                            simple("{ "property": "value"}")
//                    )
                .to(output);

    }
}
