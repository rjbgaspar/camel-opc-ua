package com.gv.camelopcua.camel.milo.opcua.core.types;

public enum OpcUaDataType {
    /**
     * Keep the data type as it is, no additional conversions are required.
     */
    RETAIN(null),
    USHORT(org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UShort.class),
    UINTEGER(org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UInteger.class),
    ULONG(org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.ULong.class);

    private final Class<?> dataType;

    OpcUaDataType(Class<?> dataType) {
        this.dataType = dataType;
    }

    public Class<?> getDataType() {
        return dataType;
    }
}
