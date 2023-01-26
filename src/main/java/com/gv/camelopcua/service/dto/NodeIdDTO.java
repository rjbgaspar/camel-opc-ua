package com.gv.camelopcua.service.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;

@Data
@NoArgsConstructor
public class NodeIdDTO {
    int namespaceIndex;
    int identifier;
    String value;


    /**
     * Class<?> clazz = Class.forName("java.util.Date");
     */
    String type;

    public NodeId toNodeId() {
        return new NodeId(namespaceIndex, identifier);
    }
}
