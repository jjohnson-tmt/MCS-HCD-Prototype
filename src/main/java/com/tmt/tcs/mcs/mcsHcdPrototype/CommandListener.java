package com.tmt.tcs.mcs.mcsHcdPrototype;

/**
 * Listens for HCD commands from MCS assembly
 * @author E401581.
 *
 */
interface CommandListener {
  
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
   * Reads MCS command from private AKKA message sent by Assembly.
   * Sends command to MCS and sends the response back to Assembly.
   */
  void listenforHcdCommand();
  
  
}
