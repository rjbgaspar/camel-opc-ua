package com.gv.camelopcua.web.rest;


import com.gv.camelopcua.core.types.builtin.MiloClientMessage;
import com.gv.camelopcua.service.ApacheCamelProducerService;
import com.gv.camelopcua.service.dto.MiloClientProduceRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
@RequiredArgsConstructor
public class MiloClientProduceResource {
    private final ApacheCamelProducerService apacheCamelProducerService;


    @PostMapping("/camel-milo")
    public ResponseEntity<Object> write(@RequestBody MiloClientProduceRequestDto miloClientProduceRequestDto) {
        log.debug("REST request send message exchanges : {}", miloClientProduceRequestDto.getProduceUri());
        return ResponseEntity.ok().body(apacheCamelProducerService.send(miloClientProduceRequestDto));
    }
}
