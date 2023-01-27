package com.gv.camelopcua.component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gv.camelopcua.config.Config;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.camel.AggregationStrategy;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import static org.apache.camel.component.http.HttpConstants.CONTENT_TYPE;
import static org.apache.camel.component.milo.MiloConstants.HEADER_AWAIT;
import static org.apache.camel.component.milo.MiloConstants.HEADER_NODE_IDS;

@Component
@RequiredArgsConstructor
@Log4j2
public class OPCUAAggregationRouteBuilderComponent extends RouteBuilder {
        private final ObjectMapper objectMapper;
    private final Config config;

    /**
     * <b>Called on initialization to build the routes using the fluent builder syntax.</b>
     * <p/>
     * This is a central method for RouteBuilder implementations to implement
     * the routes using the Java fluent builder syntax.
     */
    @Override
    public void configure() {

        String ENDPOINT_URI = "milo-client:opc.tcp://VAL031.gv.local:53530/OPCUA/SimulationServer?allowedSecurityPolicies=None&samplingInterval=500";
        from("direct:start")
                .log("Processing aggregation ${body}")
                .setHeader(HEADER_NODE_IDS, constant(Arrays.asList("ns=3;i=1002", "ns=3;i=1005")))
				.setHeader(HEADER_AWAIT, constant(true)) // await: parameter "defaultAwaitWrites"
                .enrich(ENDPOINT_URI, new AggregationStrategy() {
                    @Override
                    public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
                        return newExchange;
                    }
                })
                .process(exchange -> {
                    String routeId = exchange.getFromRouteId();

                    ArrayList<String> headers = exchange.getMessage().getHeader(HEADER_NODE_IDS, ArrayList.class);
                    ArrayList<DataValue> list = exchange.getIn().getBody(ArrayList.class);

                    var map = new HashMap<String, Object>();

                    for (int index = 0; index < list.size(); index++) {
                        map.put(headers.get(index), list.get(index).getValue().getValue());
                    }

                    try {
                        var payload = objectMapper.writeValueAsString(map);
                        log.debug("Message body set to {}", payload);
                        exchange.getIn().setBody(payload);
                    } catch (JsonProcessingException swallow) {
                        log.error("Could not apply processor. {}", swallow.getMessage(), swallow);
                    }
                })
                .log("Process message with body= ${body}")
                .setHeader(CONTENT_TYPE, constant("Application/json"))
//                    .setBody(
//                            simple("{ "property": "value"}")
//                    )
                .to("http://172.16.2.73:50001/api/do-post?httpMethod=POST");

    }
}
