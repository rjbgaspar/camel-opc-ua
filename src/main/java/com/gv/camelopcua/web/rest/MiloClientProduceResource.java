package com.gv.camelopcua.web.rest;


import com.gv.camelopcua.core.types.builtin.MiloClientMessage;
import com.gv.camelopcua.service.dto.MiloClientProduceRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.spring.SpringCamelContext;
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;
import org.eclipse.milo.opcua.stack.core.types.builtin.DateTime;
import org.eclipse.milo.opcua.stack.core.types.builtin.Variant;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import static org.eclipse.milo.opcua.stack.core.Identifiers.UInt16;
import static org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.Unsigned.ushort;


/**
 * Send message exchanges to endpoints
 */
@RestController
@RequestMapping("/api")
@Log4j2
@RequiredArgsConstructor
public class MiloClientProduceResource {
    final ConcurrentHashMap<String, ProducerTemplate> producers;

    final SpringCamelContext camelContext;


    @PostMapping("/camel-milo")
    public ResponseEntity<Object> write(@RequestBody MiloClientProduceRequestDto miloClientProduceRequestDto) {
        log.debug("REST request send message exchanges : {}", miloClientProduceRequestDto.getProduceUri());


        var producerTemplate = get(miloClientProduceRequestDto.getProduceUri());
        var list = miloClientProduceRequestDto.getPayload().stream()
                .map(it -> {
                    final String endpoint = String.format(producerTemplate.getDefaultEndpoint() + "&defaultAwaitWrites=true&node=RAW(ns=%d;i=%d)",
                            it.getNodeId().getNamespaceIndex(), it.getNodeId().getIdentifier());


                    Variant v = new Variant(it.getValue());

                    // don't write status
//                     DataValue dataValue = new DataValue(v, null, new DateTime());
                    // don't write StatusCode or timestamp, most servers don't support it
                    DataValue dataValue = new DataValue(v, null, null);

                    var serverResponse = producerTemplate.requestBody(endpoint, dataValue, DataValue.class);
                    log.info("Write \"ns={};i={}\" status : {}",
                            it.getNodeId().getNamespaceIndex(),
                            it.getNodeId().getIdentifier(),
                            serverResponse.getStatusCode().toString());
                    return new MiloClientMessage(it, serverResponse);
                }).toList();

        var allGood = list.stream().allMatch(it -> {
            assert it.getDataValue().getStatusCode() != null;
            return it.getDataValue().getStatusCode().isGood();
        });

        return ResponseEntity
                .ok()
                .body(Map.of(
                        "allGood", allGood,
                        "payload", list
                ));

    }

    private ProducerTemplate get(String produceUri) {
        return Optional.ofNullable(producers.get(produceUri))
                .orElseGet(() -> {
                    var producerTemplate = camelContext.createProducerTemplate();
                    producerTemplate.setDefaultEndpointUri(produceUri);

                    producers.put(produceUri, producerTemplate);
                    return producerTemplate;
                });
    }

    private <T> T convert(final Object value, final Class<T> clazz) {
        return camelContext.getTypeConverter().convertTo(clazz, new DataValue(new Variant(value)));
    }
}
