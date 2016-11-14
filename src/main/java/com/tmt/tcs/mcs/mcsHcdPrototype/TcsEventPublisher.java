package com.tmt.tcs.mcs.mcsHcdPrototype;

import com.tmt.tcs.mcs.mcsToTcsEvents.PublishContentProvider;

/**
 * @author E401581
 *     Provides implementation for publishing mcs events to mcs assembly.
 */
final class TcsEventPublisher implements EventPublisher {

  /**
   * Singleton instance of TcsEventPublisher class.
   */
  private static TcsEventPublisher instance = null;
  
  /**
   * Initializes new instance of TcsEventPublisher.
   */
  private TcsEventPublisher() {

  }
  
  /**
   * Creates singleton instance.
   * @return singleton instance.
   */
  public static TcsEventPublisher getInstance() {
    if (instance == null) {
      instance = new TcsEventPublisher();
    }
    return instance;
  }

  /* (non-Javadoc)
   * @see com.tmt.tcs.mcs.mcsHcdPrototype.EventPublisher#publishEvent
   * (com.tmt.tcs.mcs.mcsToTcsEvents.PublishContentProvider)
   */
  public void publishEvent(PublishContentProvider provider) {

  }

  public void init() {
    
  }

  public void start() {
    
  }

  public void stop() {
    //Ensure that a new instance will be created.
    instance = null;
  }
}