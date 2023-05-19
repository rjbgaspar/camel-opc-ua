package com.gv.camelopcua.camel.milo.opcua.service.dto;

import com.gv.camelopcua.camel.milo.opcua.core.types.OpcUaNode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class OpcUaRequestDto {
    String produceUri;
    List<OpcUaNode> payload;
}
