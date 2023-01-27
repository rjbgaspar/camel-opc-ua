package com.gv.camelopcua.web.rest;


import com.gv.camelopcua.service.dto.NodeIdDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.spring.SpringCamelContext;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.StatusCode;
import org.eclipse.milo.opcua.stack.core.types.builtin.Variant;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

//@RestController
@RequestMapping("/api")
@Log4j2
@RequiredArgsConstructor
public class OpcUaCamelMiloResource {
    private static final String ENDPOINT_URI = "milo-client:opc.tcp://VAL031.gv.local:53530/OPCUA/SimulationServer?allowedSecurityPolicies=None&samplingInterval=500";

    final SpringCamelContext camelContext;

    @PostMapping("/nodes")
    public ResponseEntity<Object> writeNodeId(@RequestBody NodeIdDTO nodeIdDTO) {
        log.debug("REST request to save CadErpBinding : {}", nodeIdDTO);
        try {
            var nodeId = nodeIdDTO.toNodeId();
            var producerTemplate = camelContext.createProducerTemplate();
            var endpointUri = String.format(ENDPOINT_URI + "&defaultAwaitWrites=true&node=RAW(ns=%d;i=%d)",
                    nodeIdDTO.getNamespaceIndex(),
                    nodeIdDTO.getIdentifier());



//            var result = producerTemplate.requestBody(endpointUri, params, "await", true, Variant.class);
//            var o = producerTemplate.requestBody(endpointUri, new Variant(nodeIdDTO.getValue()), Variant.class);




//            producerTemplate.


            // If defaultAwaitWrites is true the class org.eclipse.milo.opcua.stack.core.types.builtin.StatusCode
            var o = producerTemplate.requestBody(endpointUri, new Variant(nodeIdDTO.getValue()), StatusCode.class);




            return ResponseEntity
                    .created(new URI("/api/nodes"))
//                    .headers()
                    .body(  Map.of(

                            "nodeId", nodeId,
                            "value", new Variant(nodeIdDTO.getValue()),
                                    "statusCode", o
                            ));
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
