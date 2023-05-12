package com.gv.camelopcua.service;

import com.gv.camelopcua.core.types.builtin.MiloClientMessage;
import com.gv.camelopcua.core.types.builtin.MiloDataType;
import com.gv.camelopcua.service.dto.MiloClientProduceRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.spring.SpringCamelContext;
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;
import org.eclipse.milo.opcua.stack.core.types.builtin.Variant;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UShort;
import org.eclipse.milo.opcua.stack.core.util.ConversionUtil;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import static org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.Unsigned.*;


@Service
@RequiredArgsConstructor
@Slf4j
public class ApacheCamelProducerService {
    final ConcurrentHashMap<String, ProducerTemplate> producers;

    final SpringCamelContext camelContext;


    /**
     * Send the body to an endpoint returning any result output body.
     * @param miloClientProduceRequestDto
     * @return
     */
    public Map<String, Object> send(MiloClientProduceRequestDto miloClientProduceRequestDto) {
        log.debug("Request to send body to an endpoint : {}", miloClientProduceRequestDto);

        var producerTemplate = get(miloClientProduceRequestDto.getProduceUri());
        var list = miloClientProduceRequestDto.getPayload().stream()
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

    private Variant convert(final Object value, MiloDataType dataType) {
        return new Variant(
            switch (dataType) {
                case UInteger -> uint((Integer) value);
                case ULong -> ulong((Integer) value);
                case UShort -> ushort((Integer)value);
                default -> value;
            }
        );
    }
}
