package com.gv.camelopcua.camel.milo.opcua.component.write;

import org.apache.camel.builder.RouteBuilder;
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;
import org.eclipse.milo.opcua.stack.core.types.builtin.Variant;
import org.springframework.stereotype.Component;

import static org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.Unsigned.ushort;


//@Component
public class WriteSingleNodeToOpcUaServer extends RouteBuilder {
    private static final int MIN = 1;
    private static final int MAX = 100;

    @Override
    public void configure() throws Exception {

        // set up a route that generates an event every 2 seconds:
        from("timer://timerName?fixedRate=true&period=2000")
        .process(exchange -> {

            var random = MIN + (int)(Math.random() * ((MAX - MIN) + 1));

            Variant v = new Variant(ushort(random));

            // don't write status
            // DataValue dataValue = new DataValue(v, null, new DateTime());
            // don't write StatusCode or timestamp, most servers don't support it
            DataValue dataValue = new DataValue(v, null, null);

            exchange.getIn().setBody(dataValue);
        })

//        .to("mock:test")
        .to("milo-client:opc.tcp://172.16.32.159:4840?allowedSecurityPolicies=None&node=RAW(ns=4;i=4)")
        .log("Wrote ${body} to OPC UA server");
    }
}
