package com.ibm.camel.demo.components;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.boot.json.JacksonJsonParser;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class DemoRoute extends RouteBuilder {


    @Override
    public void configure() throws Exception {

        restConfiguration().component("netty-http").host("0.0.0.0").port(8080).bindingMode(RestBindingMode.json);
//        .setJsonDataFormat("json-jackson");

        // GET /echoget/(echoValue}
        from("rest://get:3echoget/{echoValue}").process(new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                System.out.println(exchange.getIn().getHeader("echoValue"));
                // exchange.getMessage().setBody(exchange.getIn().getHeader("echoValue"));
            }
        });

        // POST /echo
        // {"echo":"anything"}
        from("rest://post:3echo").unmarshal().json(JsonLibrary.Jackson).process(new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                System.out.println(exchange.getIn().getBody());
                Map bodyMap = (Map) exchange.getIn().getBody();
                exchange.getMessage().setHeader("postEcho", bodyMap.get("echo"));
            }
        }).to("rest://get:3echoget/{postEcho}");
        /*
		from("file:C:/inboxPOST?noop=true").process(new CreateEmployeeProcessor()).marshal(jsonDataFormat)
		.setHeader(Exchange.HTTP_METHOD, simple("POST"))
		.setHeader(Exchange.CONTENT_TYPE, constant("application/json")).to("http://localhost:8080/employee")
		.process(new MyProcessor());
        */
    }
}