package com.gv.camelopcua.camel.milo.opcua.core.types;

public enum OpcUaDataType {
    UShort(org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UShort.class),
    UInteger(org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UInteger.class),
    ULong(org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.ULong.class);

    private final Class<?> dataType;

    OpcUaDataType(Class<?> dataType) {
        this.dataType = dataType;
    }

    public Class<?> getDataType() {
        return dataType;
    }
}
