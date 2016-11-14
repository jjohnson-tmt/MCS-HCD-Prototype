package com.tmt.tcs.mcs.tcsToMcsEvents;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author E401581.
 *     Class represents tcs event to be sent to MCS.
 */
public abstract class TcsEvent {
  
  /**
   * Name of config file.
   */
  private static final String CONFIG_FILE_NAME = "config.properties";
  
  /**
   * Command name.
   */
  private String eventName = null;

  /**
   * Initializes new instance of TcsEvent.
   */
  public TcsEvent(String eventKey) {
    // Read configuration
    Properties prop = new Properties();
    try(InputStream input=new FileInputStream(CONFIG_FILE_NAME)) {
      // load a properties file
      try {
        prop.load(input);
        eventName = prop.getProperty(eventKey);
      } catch (IOException ex) {
        // TODO Auto-generated catch block
        ex.printStackTrace();
      }
    }catch (IOException exp) {
      exp.printStackTrace();
    }
  }

  /**
   * Creates byte[] to be sent to MCS for the specified event.
   * @return byte[] containing the event packet.
   */
  public abstract byte[] encode();
  
  /**
   * Returns event identifier to be sent before event packet to MCS.
   * @return String containing event identifier.
   */
  public String getEventName() {
    return eventName;
  }
}