package com.tmt.tcs.mcs.mcsHcdPrototype;

import com.tmt.tcs.mcs.mcsToTcsEvents.PublishContentProvider;

/**
 * @author E401581.
 *     Publishes event to MCS  assembly.
 */
interface EventPublisher {
  /**
   * Provides intialization.
   */
  void init();
  
  /**
   * Provides startup.
   */
  void start();
  
  /**
   * Provides cleanup.
   */
  void stop();
  
  /**
   * publishes the specified event to MCS Assembly
   * @param provider event to be published as PublishContentProvider.
   */
  void publishEvent(PublishContentProvider provider);

}
