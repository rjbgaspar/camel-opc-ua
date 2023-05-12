package com.gv.camelopcua.camel.milo.opcua.core.types;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bouncycastle.util.Strings;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OpcUaNodeId {

    private static final String NAMESPACE_INDEX = "ns";
    private static final String IDENTIFIER = "i";

    int namespaceIndex;
    int identifier;


    /**
     * Build a new OpcUaNodeId
     *
     * @param s - e.g. "ns=3;i=10002"
     * @return a namespaceIndex/identifier for the Node ID
     */
    public static OpcUaNodeId fromNamespaceIndexAndIdentifier(String s) {
        assert s != null;

        var array = Strings.split(s, ';');

        if (array.length != 2) {
            throw new IllegalArgumentException("Could not split into namespace and node for [" + s + "]");
        } else if (!array[0].startsWith(NAMESPACE_INDEX + "=")) {
            throw new IllegalArgumentException("Could not find namespace for [" + s + "]");
        } else if (!array[1].startsWith(IDENTIFIER + "=")) {
            throw new IllegalArgumentException("Could not find node for [" + s + "]");
        }

        return new OpcUaNodeId(
                Integer.parseInt(array[0].substring(NAMESPACE_INDEX.length() + 1)),
                Integer.parseInt(array[1].substring(IDENTIFIER.length() + 1))
        );
    }
}
