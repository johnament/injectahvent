package io.injectahvent.camel.processors;

import javax.enterprise.inject.spi.BeanManager;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ConfigurationBuilder {
    private List<Annotation> annotations;
    private boolean fireBody = false;
    private boolean fireExchange = false;
    private BeanManager beanManager;

    private ConfigurationBuilder() {
        this.annotations = new ArrayList<Annotation>();
    }

    public ConfigurationBuilder addQualifiers(Annotation... qualifiers) {
        Collections.addAll(this.annotations,qualifiers);
        return this;
    }

    public ConfigurationBuilder fireBody() {
        this.fireBody = true;
        return this;
    }

    public ConfigurationBuilder fireExchange() {
        this.fireExchange = true;
        return this;
    }

    public ConfigurationBuilder beanManager(BeanManager beanManager) {
        this.beanManager = beanManager;
        return this;
    }

    public Configuration build() {
        if(beanManager == null) {
            throw new IllegalStateException("Bean Manager cannot be null.");
        }
        if(!fireBody && !fireExchange) {
            throw new IllegalStateException("You must enable either firing a body or exchange.");
        }
        if(this.annotations.isEmpty()) {
            throw new IllegalStateException("You must add at least one qualifier.");
        }
        final Annotation[] annotations = this.annotations.toArray(new Annotation[this.annotations.size()]);
        return new ConfigurationImpl(fireBody,fireExchange,beanManager,annotations);
    }

    public static ConfigurationBuilder builder() {
        return new ConfigurationBuilder();
    }

    public final class ConfigurationImpl implements Configuration,Serializable {
        private boolean fireBody = true;
        private boolean fireExchange = true;
        private BeanManager beanManager;
        private Annotation[] annotations;

        public ConfigurationImpl(boolean fireBody, boolean fireExchange, BeanManager beanManager, Annotation[] annotations) {
            this.fireBody = fireBody;
            this.fireExchange = fireExchange;
            this.beanManager = beanManager;
            this.annotations = annotations;
        }

        @Override
        public boolean isFireBody() {
            return fireBody;
        }

        @Override
        public boolean isFireExchange() {
            return fireExchange;
        }

        @Override
        public BeanManager getBeanManager() {
            return beanManager;
        }

        @Override
        public Annotation[] getAnnotations() {
            return annotations;
        }
    }
}
