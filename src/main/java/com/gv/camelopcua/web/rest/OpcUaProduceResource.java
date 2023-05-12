package com.gv.camelopcua.web.rest;


import com.gv.camelopcua.camel.milo.opcua.service.OpcUaProducerService;
import com.gv.camelopcua.camel.milo.opcua.service.dto.OpcUaRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.Unsigned.ushort;


/**
 * Send message exchanges to endpoints
 */
@RestController
@RequestMapping("/api")
@Slf4j
@RequiredArgsConstructor
public class OpcUaProduceResource {
    private final OpcUaProducerService opcUaProducerService;


    @PostMapping("/camel-milo")
    public ResponseEntity<Object> write(@RequestBody OpcUaRequestDto opcUaRequestDto) {
        log.debug("REST request send message exchanges : {}", opcUaRequestDto.getProduceUri());
        return ResponseEntity.ok().body(opcUaProducerService.send(opcUaRequestDto));
    }
}
