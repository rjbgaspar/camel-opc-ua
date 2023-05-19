package com.gv.camelopcua.camel.milo.opcua.service;

import com.gv.camelopcua.camel.milo.opcua.core.types.OpcUaMessage;
import com.gv.camelopcua.camel.milo.opcua.core.types.OpcUaDataType;
import com.gv.camelopcua.camel.milo.opcua.service.dto.OpcUaRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.spring.SpringCamelContext;
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;
import org.eclipse.milo.opcua.stack.core.types.builtin.Variant;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import static org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.Unsigned.*;


@Service
@RequiredArgsConstructor
@Slf4j
public class OpcUaProducerService {
    final ConcurrentHashMap<String, ProducerTemplate> producers;

    final SpringCamelContext camelContext;


    /**
     * Send the body to an endpoint returning any result output body.
     * @param opcUaRequestDto
     * @return
     */
    public Map<String, Object> send(OpcUaRequestDto opcUaRequestDto) {
        log.debug("Request to send body to an endpoint : {}", opcUaRequestDto);

        var producerTemplate = get(opcUaRequestDto.getProduceUri());
        var list = opcUaRequestDto.getPayload().stream()
                .map(it -> {
                    final String endpoint = String.format(producerTemplate.getDefaultEndpoint() + "&defaultAwaitWrites=true&node=RAW(ns=%d;i=%d)",
                            it.getNodeId().getNamespaceIndex(), it.getNodeId().getIdentifier());

                    // StatusCode{name=Bad_TypeMismatch, value=0x80740000, quality=bad}
                    // ushort(45)
                    Variant v = convert(it.getValue(), it.getDataType());

                    // don't write status
//                     DataValue dataValue = new DataValue(v, null, new DateTime());
                    // don't write StatusCode or timestamp, most servers don't support it
                    DataValue dataValue = new DataValue(v, null, null);

                    var serverResponse = producerTemplate.requestBody(endpoint, dataValue, DataValue.class);
                    log.trace("Write \"ns={};i={}\" status : {}",
                            it.getNodeId().getNamespaceIndex(),
                            it.getNodeId().getIdentifier(),
                            serverResponse.getStatusCode().toString());
                    return new OpcUaMessage(it, serverResponse);
                }).toList();

        var allGood = list.stream().allMatch(it -> {
            assert it.getResult().getStatusCode() != null;
            return it.getResult().getStatusCode().isGood();
        });

        return Map.of(
                "allGood", allGood,
                "payload", list
        );
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

    private Variant convert(final Object value, OpcUaDataType dataType) {
        return new Variant(
            switch (dataType) {
                case UINTEGER -> uint((Integer) value);
                case ULONG -> ulong((Integer) value);
                case USHORT -> ushort((Integer)value);
                default -> value;
            }
        );
    }
}
