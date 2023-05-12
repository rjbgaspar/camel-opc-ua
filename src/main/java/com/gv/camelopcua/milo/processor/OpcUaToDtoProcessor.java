package com.gv.camelopcua.milo.processor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gv.camelopcua.core.types.builtin.MiloClientMessage;
import com.gv.camelopcua.core.types.builtin.MiloClientNodeId;
import com.gv.camelopcua.core.types.builtin.MiloClientValue;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Map;

import static org.apache.camel.component.milo.MiloConstants.HEADER_NODE_IDS;

@Component
@RequiredArgsConstructor
@Log4j2
public class OpcUaToDtoProcessor implements Processor {
    private final ObjectMapper objectMapper;

    @Override
    public void process(Exchange exchange) {
        String routeId = exchange.getFromRouteId();

        ArrayList<String> headers = exchange.getMessage().getHeader(HEADER_NODE_IDS, ArrayList.class);
        ArrayList<DataValue> list = exchange.getIn().getBody(ArrayList.class);

        var payload = new ArrayList<MiloClientMessage>();


        for (int index = 0; index < list.size(); index++) {
            payload.add(
                    new MiloClientMessage(
                            new MiloClientValue(
                                    MiloClientNodeId.fromNamespaceIndexAndIdentifier(headers.get(index)),
                                    null,
                                    list.get(index).getValue().getValue()
                                    ),
                            list.get(index)
                    )
            );
        }

        try {
            var body = objectMapper.writeValueAsString(Map.of("payload", payload));
            log.trace("Message body set to {}", body);
            exchange.getIn().setBody(body);
        } catch (JsonProcessingException swallow) {
            log.error("Could not apply processor. {}", swallow.getMessage(), swallow);
        }
    }
}
