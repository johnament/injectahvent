package io.injectahvent.test;

import io.injectahvent.camel.processors.CDIExchangeProcessor;
import io.injectahvent.camel.processors.Configuration;
import io.injectahvent.camel.processors.ConfigurationBuilder;
import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
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
import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Inject;

@RunWith(Arquillian.class)
public class EventReceivedTest {

    @Deployment
    public static JavaArchive createInjectahventArchive() {
        return ShrinkWrap.create(JavaArchive.class).addClasses(CDIExchangeProcessor.class,Configuration.class,
                ConfigurationBuilder.class,EventReceiver.class)
                .addPackage(WeldContextControl.class.getPackage())
                .addPackage(CdiContainer.class.getPackage())
                .addAsManifestResource(new StringAsset("org.apache.deltaspike.cdise.weld.WeldContainerControl"),"services/org.apache.deltaspike.cdise.api.CdiContainer")
                .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
    }

    @Inject
    private EventReceiver eventReceiver;

    @Inject
    private BeanManager beanManager;

    @Test
    public void testReceiveMessage() throws Exception {
        CamelContext cc = new DefaultCamelContext();
        RouteBuilder rb = new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                AnnotationLiteral<Sendable> sendable = new AnnotationLiteral<Sendable>(){};
                Configuration config = ConfigurationBuilder.builder().addQualifiers(sendable).beanManager(beanManager).fireBody().fireExchange().build();
                super.from("direct://foo").routeId("directFoo").process(new CDIExchangeProcessor(config));
            }
        };
        cc.addRoutes(rb);
        cc.start();
        cc.createProducerTemplate().sendBody("direct://foo","ralph");
        Assert.assertEquals("Event should have been received twice", 2, eventReceiver.getReceivedMessages());
        cc.stop();
    }
}
