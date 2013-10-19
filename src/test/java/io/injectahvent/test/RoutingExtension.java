/*
 * Copyright 2013 John D. Ament
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.injectahvent.test;

import io.injectahvent.camel.processors.CDIExchangeProcessor;
import io.injectahvent.camel.processors.Configuration;
import io.injectahvent.camel.processors.ConfigurationBuilder;
import io.injectahvent.cdi.AbstractRoutingExtension;
import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;

import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.util.AnnotationLiteral;

public class RoutingExtension extends AbstractRoutingExtension{
    private CamelContext camelContext = null;
    @Override
    protected CamelContext getCamelContext(final BeanManager beanManager) throws Exception {
        if(camelContext == null) {
            camelContext = new DefaultCamelContext();
            RouteBuilder rb = new RouteBuilder() {
                @Override
                public void configure() throws Exception {
                    AnnotationLiteral<Sendable> sendable = new AnnotationLiteral<Sendable>(){};
                    Configuration config = ConfigurationBuilder.builder().addQualifiers(sendable).beanManager(beanManager).fireBody().fireExchange().build();
                    super.from("direct://foo").routeId("directFoo").process(new CDIExchangeProcessor(config));
                }
            };
            camelContext.addRoutes(rb);
            camelContext.start();
        }
        return camelContext;
    }

    @Override
    protected void createRoutes() {
        super.addToAliased("direct://foo","fooDirect");
    }
}
