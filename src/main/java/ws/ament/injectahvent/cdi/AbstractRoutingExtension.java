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
package ws.ament.injectahvent.cdi;

import org.apache.camel.CamelContext;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.*;
import java.lang.annotation.Annotation;

public abstract class AbstractRoutingExtension implements Extension{

    protected abstract CamelContext getCamelContext(final BeanManager beanManager) throws Exception;

    protected abstract void createRoutes();

    private AfterBeanDiscovery afterBeanDiscovery;

    private CamelContext __LocalCamelContext;

    void addRoutes(@Observes AfterBeanDiscovery afterBeanDiscovery, final BeanManager beanManager) throws Exception {
        this.__LocalCamelContext = getCamelContext(beanManager);
        this.afterBeanDiscovery = afterBeanDiscovery;
        createRoutes();
    }

    protected final void addTo(final String destinationName, Annotation... qualifiers) {
        this.addToAliased(destinationName,destinationName,qualifiers);
    }

    protected final void addToAliased(final String destinationName, final String aliasName, Annotation... qualifiers) {
        ProducerObserverMethod observerMethod = new ProducerObserverMethod(aliasName,destinationName,qualifiers,__LocalCamelContext);
        this.afterBeanDiscovery.addObserverMethod(observerMethod);
    }
}
