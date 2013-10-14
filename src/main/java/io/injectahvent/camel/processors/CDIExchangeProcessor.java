package io.injectahvent.camel.processors;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.deltaspike.cdise.api.CdiContainer;
import org.apache.deltaspike.cdise.api.CdiContainerLoader;
import org.apache.deltaspike.cdise.api.ContextControl;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import java.lang.annotation.Annotation;
import java.util.Set;


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
        this.contextControl = this.getContextControl();
    }

    private void startRequestContext() {
        this.contextControl.startContext(RequestScoped.class);
    }

    private void stopRequestContext() {
        this.contextControl.stopContext(RequestScoped.class);
    }

    private BeanManager getBeanManager() {
        return this.beanManager;
    }

    public ContextControl getContextControl()
    {
        Bean<ContextControl> ctxCtrlBean = null;
        CreationalContext<ContextControl> ctxCtrlCreationalContext = null;
        ContextControl ctxCtrl = null;
        Set<Bean<?>> beans = this.beanManager.getBeans(ContextControl.class);
        System.out.println("Found beans: "+beans);
        ctxCtrlBean = (Bean<ContextControl>) beanManager.resolve(beans);
        ctxCtrlCreationalContext = getBeanManager().createCreationalContext(ctxCtrlBean);
        ctxCtrl = (ContextControl)
                    this.beanManager.getReference(ctxCtrlBean, ContextControl.class, ctxCtrlCreationalContext);
        return ctxCtrl;
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        System.out.println("Processing exchange.");
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
