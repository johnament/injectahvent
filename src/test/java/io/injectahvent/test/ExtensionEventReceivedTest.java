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
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.injectahvent.test;

import io.injectahvent.camel.processors.CDIExchangeProcessor;
import io.injectahvent.camel.processors.Configuration;
import io.injectahvent.camel.processors.ConfigurationBuilder;
import io.injectahvent.cdi.AbstractRoutingExtension;
import io.injectahvent.cdi.ProducerObserverMethod;
import io.injectahvent.cdi.RouteTo;
import io.injectahvent.cdi.RouteToLiteral;
import org.apache.deltaspike.cdise.api.CdiContainer;
import org.apache.deltaspike.cdise.weld.WeldContextControl;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;

@RunWith(Arquillian.class)
public class ExtensionEventReceivedTest {
    @Deployment
    public static JavaArchive createInjectahventArchive() {
        return ShrinkWrap.create(JavaArchive.class).addClasses(CDIExchangeProcessor.class, Configuration.class,
                ConfigurationBuilder.class, AppScopedEventReceiver.class, RequestScopedEventReceiver.class)
                .addClasses(AbstractRoutingExtension.class, ProducerObserverMethod.class, RouteTo.class, RouteToLiteral.class, RoutingExtension.class)
                .addPackage(WeldContextControl.class.getPackage())
                .addPackage(CdiContainer.class.getPackage())
                .addAsManifestResource(new StringAsset("org.apache.deltaspike.cdise.weld.WeldContainerControl"), "services/org.apache.deltaspike.cdise.api.CdiContainer")
                .addAsManifestResource(new StringAsset("io.injectahvent.test.RoutingExtension"),"services/javax.enterprise.inject.spi.Extension")
                .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
    }

    @Inject
    private AppScopedEventReceiver eventReceiver;

    @Inject
    private BeanManager beanManager;

    @Test
    public void testSendMessages() {
        beanManager.fireEvent("ralph",new RouteToLiteral("fooDirect"));
        Assert.assertEquals("Event should have been received twice", 2, eventReceiver.getReceivedMessages());
        Assert.assertEquals("RequestScoped should have been received once",1,eventReceiver.getReceivedReqScoped());
    }
}
