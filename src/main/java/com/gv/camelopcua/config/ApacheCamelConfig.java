package com.gv.camelopcua.config;


import org.apache.camel.Configuration;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.spring.SpringCamelContext;
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;
import org.springframework.context.annotation.Bean;

@Configuration
public class ApacheCamelConfig {

//    @Bean("t1")
//    public RouteBuilder log(SpringCamelContext camelContext) {
//        return new RouteBuilder() {
//            @Override
//            public void configure() throws Exception {
//                from("milo-client:opc.tcp://VAL031.gv.local:53530/OPCUA/SimulationServer?allowedSecurityPolicies=None&samplingInterval=500&node=RAW(ns=3;i=1002)")
//                        .routeId("Test Route")
//                        .process(exchange -> {
//                            String routeId = exchange.getFromRouteId();
//                            DataValue data = exchange.getIn().getBody(DataValue.class);
//                            log.info("Route '{}': Status: {}, Value: {}",
//                                    routeId,
//                                    data.getStatusCode().toString(), data.getValue().getValue());
//                        }).to("direct:start");
//
//
//            }
//        };
//    }


    @Bean
    public RouteBuilder device0005(SpringCamelContext context) throws Exception {
        var routeBuilder= new RouteBuilder() {
            @Override
            public void configure() throws Exception {
//                from("milo-client:opc.tcp://172.16.16.41:4840?allowedSecurityPolicies=None&samplingInterval=500&node=RAW(ns=4;i=2)")
                from("milo-client:opc.tcp://VAL031.gv.local:53530/OPCUA/SimulationServer?allowedSecurityPolicies=None&samplingInterval=500&node=RAW(ns=3;i=1005)")
                        .routeId("Test Route")
                        .process(exchange -> {
                            String routeId = exchange.getFromRouteId();
                            DataValue data = exchange.getIn().getBody(DataValue.class);
                            log.info("Route '{}': Status: {}, Value: {}",
                                    routeId,
                                    data.getStatusCode().toString(), data.getValue().getValue());
                        }).to("mock:test");
            }
        };
        routeBuilder.configureRoutes(context);
        context.start();
        return routeBuilder;
    }

}
