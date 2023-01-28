package com.gv.camelopcua.core.types.builtin;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;
import org.eclipse.milo.opcua.stack.core.types.builtin.StatusCode;

import java.util.Map;

@Getter
public class MiloClientMessage {
    @JsonIgnore
    MiloClientValue miloClientValue;


    @JsonIgnore
    final DataValue dataValue;


    MiloClientNodeId nodeId;
    Object value;


    Object response;

    public MiloClientMessage(MiloClientValue miloClientValue, DataValue dataValue) {
        assert miloClientValue != null;
        assert dataValue != null;

        this.miloClientValue = miloClientValue;
        this.dataValue = dataValue;
        postConstruct();
    }

    private void postConstruct() {
        assert dataValue.getStatusCode() != null;
        assert dataValue.getSourceTime() != null;

        this.nodeId = miloClientValue.getNodeId();
        this.value = miloClientValue.getValue();

        this.response = Map.of(
            "statusCode", quality(dataValue.getStatusCode()),
            "sourceTime", dataValue.getSourceTime().getUtcTime(),
            "serverTime", dataValue.getServerTime().getUtcTime()
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
