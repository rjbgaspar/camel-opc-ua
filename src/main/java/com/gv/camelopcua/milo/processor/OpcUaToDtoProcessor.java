package com.gv.camelopcua.milo.processor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;

import static org.apache.camel.component.milo.MiloConstants.HEADER_NODE_IDS;

@Component
@RequiredArgsConstructor
@Log4j2
public class OpcUaToDtoProcessor implements Processor {
    private final ObjectMapper objectMapper;

    @Override
    public void process(Exchange exchange) throws Exception {
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
    }
}
