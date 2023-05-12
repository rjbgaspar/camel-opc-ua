package com.gv.camelopcua.camel.milo.opcua.core.types;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;
import org.eclipse.milo.opcua.stack.core.types.builtin.StatusCode;

import java.util.Map;

@Getter
public class OpcUaMessage {
    @JsonIgnore
    OpcUaNode node;

    @JsonIgnore
    final DataValue result;

    OpcUaNodeId nodeId;
    Object value;

    /**
     * A map containing the status code, source time and server time
     */
    Object response;


    /**
     * Create a new OpcUaMessage
     * @param node - the node id
     * @param result - any result output body from send the body to an endpoint
     */
    public OpcUaMessage(OpcUaNode node, DataValue result) {
        assert node != null;
        assert result != null;

        this.node = node;
        this.result = result;
        postConstruct();
    }

    private void postConstruct() {
        assert result.getStatusCode() != null;
        assert result.getSourceTime() != null;

        this.nodeId = node.getNodeId();
        this.value = node.getValue();

        this.response = Map.of(
            "statusCode", quality(result.getStatusCode()),
            "sourceTime", result.getSourceTime().getUtcTime(),
            "serverTime", result.getServerTime().getUtcTime()
        );
    }

    /**
     * Gets the statusCode quality as a string
     * See quality(StatusCode) method in {@link org.eclipse.milo.opcua.stack.core.types.builtin.StatusCode}
     */
    private static String quality(StatusCode statusCode) {
        if (statusCode.isGood()) {
            return "good";
        } else if (statusCode.isBad()) {
            return "bad";
        } else if (statusCode.isUncertain()) {
            return "uncertain";
        } else {
            return "unknown";
        }
    }
}
