package com.example.spring.camel.playground.route;

import com.example.spring.camel.playground.route.DeadLetterChannelRouterTest.Config;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@CamelSpringBootTest
@SpringBootTest(classes = Config.class)
@Slf4j
class DeadLetterChannelRouterTest {

  @Produce("direct:start")
  private ProducerTemplate start;
  @EndpointInject("mock:successOutput")
  private MockEndpoint mockSuccessOutput;
  @EndpointInject("mock:exceptionOutput")
  private MockEndpoint mockExceptionOutput;

  @TestConfiguration
  static class Config {

    @Bean
    RouteBuilder testRouter() {
      return new RouteBuilder() {
        @Override
        public void configure() throws Exception {
          errorHandler(
              deadLetterChannel("direct:error")
                  .onExceptionOccurred(new ExceptionProcessor())
                  .useOriginalMessage()
                  .maximumRedeliveries(2)
                  // default is 1s
                  .redeliveryDelay(TimeUnit.SECONDS.toMillis(2))
          );

          from("direct:start")
//              .errorHandler(
//                  deadLetterChannel("log:dead")
//                      .maximumRedeliveries(3)
//                      .redeliveryDelay(TimeUnit.SECONDS.toMillis(2))
//                      .onExceptionOccurred(new ExceptionProcessor())
//              )
              .to("direct:exception")
              .to("mock:successOutput")
          ;

          from("direct:exception")
              .bean(FaultyService.class, "get");

          from("direct:error")
              .log("Sending exception to ExceptionHandler")
              .bean(ExceptionHandler.class)
              .to("mock:exceptionOutput")
          ;

        }
      };
    }

    @Component
    static class FaultyService {

      public String get() {
        log.info("computeComplicatedData :: error");
        throw new ArithmeticException("can't handle maths");
      }
    }

    @Component
    static class ExceptionHandler implements Processor {

      @Override
      public void process(Exchange exchange) throws Exception {
        log.info("log :: {}", exchange);
      }
    }

    static class ExceptionProcessor implements Processor {

      @Override
      public void process(Exchange exchange) throws Exception {
        Exception cause = exchange.getProperty(Exchange.EXCEPTION_CAUGHT, Exception.class);
        String msg = "Something went wrong due to " + cause.getMessage();
        // do some custom logging here
        log.error(msg);
      }
    }

  }

  @Test
  void test() throws Exception {
    mockSuccessOutput.expectedMessageCount(0);
    mockExceptionOutput.expectedMessageCount(1);

    start.sendBody(null);

    mockSuccessOutput.assertIsSatisfied();
    mockExceptionOutput.assertIsSatisfied();
  }

}
