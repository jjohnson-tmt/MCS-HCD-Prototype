package com.tmt.tcs.mcs.mcsHcdPrototype;

/**
 * @author E401581.
 *     Interface to be implemented for listening to tcs events.
 */
interface EventListener {
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
   * Reads MCS event from private AKKA message sent by Assembly.
   * Sends event to MCS.
   */
  void listenforEvents();

}
