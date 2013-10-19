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
import org.apache.camel.Exchange;

import javax.enterprise.event.Reception;
import javax.enterprise.event.TransactionPhase;
import javax.enterprise.inject.spi.ObserverMethod;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;

public class ProducerObserverMethod implements ObserverMethod<Object> {

    private final Annotation[] annotations;
    private final String aliasName;
    private final String destinationName;

    private final CamelContext camelContext;

    private final Set<Annotation> qualifiers;

    public ProducerObserverMethod(String aliasName, String destinationName, Annotation[] annotations, CamelContext camelContext) {
        this.annotations = annotations;
        this.aliasName = aliasName;
        this.destinationName = destinationName;
        this.camelContext = camelContext;

        this.qualifiers = new HashSet<Annotation>();

        RouteTo routeTo = new RouteToLiteral(aliasName);
        qualifiers.add(routeTo);
        if(annotations != null && annotations.length > 0) {
            for(Annotation a : annotations) {
                this.qualifiers.add(a);
            }
        }

    }

    @Override
    public Class<?> getBeanClass() {
        return getClass();
    }

    @Override
    public Type getObservedType() {
        return Object.class;
    }

    @Override
    public Set<Annotation> getObservedQualifiers() {
        return this.qualifiers;
    }

    @Override
    public Reception getReception() {
        return Reception.ALWAYS;
    }

    @Override
    public TransactionPhase getTransactionPhase() {
        return TransactionPhase.IN_PROGRESS;
    }

    @Override
    public void notify(Object o) {
        if(o instanceof Exchange) {
            Exchange e = (Exchange)o;
            this.camelContext.createProducerTemplate().send(this.destinationName,e);
        }
        else {
            this.camelContext.createProducerTemplate().sendBody(this.destinationName,o);
        }
    }
}
