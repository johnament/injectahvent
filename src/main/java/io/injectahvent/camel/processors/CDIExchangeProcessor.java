package io.injectahvent.camel.processors;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.deltaspike.cdise.api.CdiContainer;
import org.apache.deltaspike.cdise.api.CdiContainerLoader;
import org.apache.deltaspike.cdise.api.ContextControl;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.spi.BeanManager;
import java.lang.annotation.Annotation;


public class CDIExchangeProcessor implements Processor {

    private boolean fireBody;
    private boolean fireExchange;
    private BeanManager beanManager;
    private Annotation[] annotations;

    private CdiContainer cdiContainer;
    private ContextControl contextControl;

    public CDIExchangeProcessor(final Configuration configuration) {
        this.fireBody = configuration.isFireBody();
        this.fireExchange = configuration.isFireExchange();
        this.beanManager = configuration.getBeanManager();
        this.annotations = configuration.getAnnotations();
        this.loadContainer();
    }

    private void loadContainer() {
        this.cdiContainer = CdiContainerLoader.getCdiContainer();
        this.contextControl = cdiContainer.getContextControl();
    }

    private void startRequestContext() {
        this.contextControl.startContext(RequestScoped.class);
    }

    private void stopRequestContext() {
        this.contextControl.stopContext(RequestScoped.class);
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        try{
            this.startRequestContext();
            if(fireBody) {
                Object body = exchange.getIn().getBody();
                beanManager.fireEvent(body, annotations);
            }
            if(fireExchange) {
                beanManager.fireEvent(exchange,annotations);
            }
        }
        catch (Exception e) {
            this.stopRequestContext();
            throw e;
        }
    }
}
