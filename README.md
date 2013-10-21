injectahvent
============

A lightweight CDI Integration for Apache Camel, forwarding Exchanges and Bodies as CDI events.

[![Build Status](https://travis-ci.org/johnament/injectahvent.png)](https://travis-ci.org/johnament/injectahvent)

## A word to the wise

The library is right now in early Alpha.  As such, API is ugly and needs to be cleaned up.  Please feel free to leave [an issue](https://github.com/johnament/injectahvent/issues) with feedback.

## Using the Library

There are two components that you can setup.  The first is a `CDIExchangeProcessor` which allows you to register an event emitter that when messages are handed to a queue, a CDI event will be fired based on your configured qualifiers.  Both the `body` and `Exchange` objects will get fired, but you can turn one or the other off as needed.

The usage of events requires setting up a `CDIExchangeProcessor` that uses a provided `Configuration` to determine what qualifiers should be observed for forwarding events to Camel.

To register it, simply add it to your `CamelContext`

            RouteBuilder rb = new RouteBuilder() {
                @Override
                public void configure() throws Exception {
                    AnnotationLiteral<Sendable> sendable = new AnnotationLiteral<Sendable>(){};
                    Configuration config = ConfigurationBuilder.builder().addQualifiers(sendable).beanManager(beanManager).fireBody().fireExchange().build();
                    super.from("direct://foo").routeId("directFoo").process(new CDIExchangeProcessor(config));
                }
            };

This will register a new event emitter that fires messages for data that comes via the queue `direct://foo` to fire an event equivalent to `@Inject @Sendable Event<Object> objectEvent;`

The second is support for a drop in replacement of `ProducerTemplate` that is focused on CDI events.  This allows you to fire a CDI event and translate it into a Camel route.

This requires some additional steps, namely you need to create a CDI Extension that extends `AbstractRoutingExtension` which will allow you to register the necessary Routes via CDI.

Internally this adds new CDI `ObserverMethod`s that will listen fot the configured types and send a message to a Camel route.

    @Override
    protected CamelContext getCamelContext(final BeanManager beanManager) throws Exception {
        // add code that creates or retrieves your CamelContext.
        return camelContext;
    }

    @Override
    protected void createRoutes() {
        super.addToAliased("direct://foo", "fooDirect");
    }
    
This area still needs some work to allow delayed start up of routes, adding in callbacks for when to start the CamelContext and when to stop it.

## Gotchas

One criticism I have for this approach is that CDI events are essentially topics, not queues.  Many receivers will get the event, if there are multiple registered.
