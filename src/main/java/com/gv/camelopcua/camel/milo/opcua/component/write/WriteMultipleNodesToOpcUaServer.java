package com.gv.camelopcua.camel.milo.opcua.component.write;

import org.apache.camel.builder.RouteBuilder;
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.Variant;
import org.eclipse.milo.opcua.stack.core.types.structured.WriteValue;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.apache.camel.component.milo.MiloConstants.HEADER_AWAIT;
import static org.apache.camel.component.milo.MiloConstants.HEADER_NODE_IDS;
import static org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.Unsigned.uint;
import static org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.Unsigned.ushort;

/**
 * !!! Could not make it to work !!!!
 */
@Component
public class WriteMultipleNodesToOpcUaServer extends RouteBuilder {
    // Define a list of node IDs to write to
    private static final String[] NODES = {"ns=4;i=4", "ns=4;i=5", "ns=4;i=6"};

    private static final int MIN = 1;
    private static final int MAX = 100;

    @Override
    public void configure() throws Exception {

        // set up a route that generates an event every 2 seconds:
        from("timer://timerName?fixedRate=true&period=2000")
        .process(exchange -> {
            // Create a list of WriteValues for each node ID
            var writeValues = Arrays.stream(NODES)
                    .map(nodeId -> {
                        var random = MIN + (int)(Math.random() * ((MAX - MIN) + 1));

                        Variant v = new Variant(ushort(random));

                        // don't write status
                        // DataValue dataValue = new DataValue(v, null, new DateTime());
                        // don't write StatusCode or timestamp, most servers don't support it
                        // return new DataValue(v, null, null);

                        //                         var node = NodeId.parse(nodeId);
//                        return new WriteValue(
//                                NodeId.parse(nodeId),
//                                uint(13), //AttributeId.Value
//                                null,
//                                new DataValue(v, null, null)
//                        );

                        return new DataValue(v, null, null);


                    })
                    .collect(Collectors.toList());

            exchange.getIn().setHeader(HEADER_NODE_IDS, NODES);
            exchange.getIn().setHeader(HEADER_AWAIT, constant(true)); // await: parameter "defaultAwaitWrites"

            exchange.getIn().setBody(new DataValue(new Variant(ushort(98)), null, null));
        })
//        .to("milo-client:opc.tcp://172.16.32.159:4840?allowedSecurityPolicies=None&node=RAW(ns=4;i=4)")
//        .to("milo-client:opc.tcp://172.16.32.159:4840?allowedSecurityPolicies=None&node=RAW(ns=4;i=4)&node=RAW(ns=4;i=5)")
        .to("milo-client:opc.tcp://172.16.32.159:4840?allowedSecurityPolicies=None")
        .log("Wrote ${body} to OPC UA server");
    }
}
