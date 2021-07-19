package org.apache.pinot.thirdeye.spi.detection;

public interface EventTriggerFactory {

  String name();

  <T extends AbstractSpec>
  EventTrigger<T> build(EventTriggerFactoryContext context);
}
