package com.gv.camelopcua.camel.milo.opcua.service.dto;

import com.gv.camelopcua.camel.milo.opcua.core.types.OpcUaNode;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@NoArgsConstructor
public class OpcUaRequestDto {
    String produceUri;
    List<OpcUaNode> payload;
}
