package com.gv.camelopcua.camel.milo.opcua.core.types;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OpcUaNode {
    OpcUaNodeId nodeId;
    OpcUaDataType dataType;
    Object value;
}
