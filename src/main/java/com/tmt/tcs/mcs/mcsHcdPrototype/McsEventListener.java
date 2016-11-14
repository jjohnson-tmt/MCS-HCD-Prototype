package com.tmt.tcs.mcs.mcsHcdPrototype;

import com.tmt.tcs.mcs.mcsToTcsEvents.McsEvent;

/**
 * @author E401581.
 *     Interface to be implemented for listening to MCS events.
 */
interface McsEventListener {
  /**
   * Reads MCS event from ZeroMQ.
   */
  McsEvent listenforMcsEvent();
  
  /**
   * Provides initialization.
   */
  void init();
  
  /**
   * Provides start.
   */
  void start();
  
  /**
   * Provides cleanup.
   */
  void stop();

}
