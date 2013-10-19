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
        ctxCtrlBean = (Bean<ContextControl>) beanManager.resolve(beans);
        ctxCtrlCreationalContext = getBeanManager().createCreationalContext(ctxCtrlBean);
        ctxCtrl = (ContextControl)
                    this.beanManager.getReference(ctxCtrlBean, ContextControl.class, ctxCtrlCreationalContext);
        return ctxCtrl;
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
