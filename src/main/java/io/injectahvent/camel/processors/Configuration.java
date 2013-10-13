package io.injectahvent.camel.processors;

import javax.enterprise.inject.spi.BeanManager;
import java.lang.annotation.Annotation;

public interface Configuration {
    public boolean isFireBody();

    public boolean isFireExchange();

    public BeanManager getBeanManager();

    public Annotation[] getAnnotations();
}
