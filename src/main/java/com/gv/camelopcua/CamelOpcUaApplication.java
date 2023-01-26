package com.gv.camelopcua;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.AggregationStrategy;
import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.Arrays;

import static org.apache.camel.component.milo.MiloConstants.HEADER_AWAIT;
import static org.apache.camel.component.milo.MiloConstants.HEADER_NODE_IDS;


/**
 * https://github.com/seletz/example-spring-boot-camel-opcua
 */
@SpringBootApplication
public class CamelOpcUaApplication {

	public static void main(String[] args) {
		SpringApplication.run(CamelOpcUaApplication.class, args);
	}

}

@Data
@Configuration
@ConfigurationProperties(prefix = "nexiles")
class Config {

	/**
	 * OPC endpoint
	 */
	String endpoint;

}

@Slf4j
@Component
class OPCUARouteBuilder extends RouteBuilder {

	private final Config config;

	OPCUARouteBuilder(Config config) {
		this.config = config;
		log.debug("Config: {}", config);
	}

	/**
	 * <b>Called on initialization to build the routes using the fluent builder syntax.</b>
	 * <p/>
	 * This is a central method for RouteBuilder implementations to implement
	 * the routes using the Java fluent builder syntax.
	 *
	 */
//	@Override
//	public void configure() {
//		log.debug("Configuring route for endpoint: {}", config.getEndpoint());
//
//		from(config.getEndpoint())
//				.routeId("Test Route")
//				.process(exchange -> {
//					String routeId = exchange.getFromRouteId();
//					DataValue data = exchange.getIn().getBody(DataValue.class);
//					log.info("Route '{}': Status: {}, Value: {}",
//							routeId,
//							data.getStatusCode().toString(), data.getValue().getValue());
//				})
//		.to("file:D:\\tmp\\out");
//
//	}


	@Override
	public void configure() {
		String ENDPOINT_URI = "milo-client:opc.tcp://VAL031.gv.local:53530/OPCUA/SimulationServer?allowedSecurityPolicies=None&samplingInterval=500";

		from("direct:start")
				.log("Processing ${body}")
				.setHeader(HEADER_NODE_IDS, constant(Arrays.asList("ns=3;i=1002","ns=3;i=1005")))
//				.setHeader(HEADER_AWAIT, constant(true)) // await: parameter "defaultAwaitWrites"
				.enrich(ENDPOINT_URI, new AggregationStrategy() {
					@Override
					public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
						return newExchange;
					}
				})
				.process(exchange -> {
					String routeId = exchange.getFromRouteId();
					DataValue data = exchange.getIn().getBody(DataValue.class);
					log.info("Route '{}': Status: {}, Value: {}",
							routeId,
							data.getStatusCode().toString(), data.getValue().getValue());
				})
				.to("file:D:\\tmp\\out");
//				.log("Processing ${id}");
	}
}
