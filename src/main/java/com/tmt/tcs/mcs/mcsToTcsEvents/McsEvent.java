package com.tmt.tcs.mcs.mcsToTcsEvents;

import com.google.protobuf.GeneratedMessage;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author E401581.
 *     Represents MCS event class.
 */
public class McsEvent implements PublishContentProvider {
  
  /**
   * Proto message representing the event.
   */
  private GeneratedMessage message = null;
  
  /**
   * Name of config file.
   */
  private static final String CONFIG_FILE_NAME = "config.properties";
     
  /**
   * Event name.
   */
  private String eventName = null;

  /**
   * Initializes new instance of McsEvent class.
   * @param message proto class object representing the event.
   * @param eventKey Key to read event Name from config.
   */
  public McsEvent(GeneratedMessage message, String eventKey) {
    this.message = message;
    // Read configuration
    Properties prop = new Properties();
    try(InputStream input = new FileInputStream(CONFIG_FILE_NAME)) {
    
    // load a properties file
    try {
      prop.load(input);
      eventName = prop.getProperty(eventKey);
    } catch (IOException ex) {
      ex.printStackTrace();
    }
    }catch (IOException exp) {
      exp.printStackTrace();
    }
  }

  public void generateContents() {
  
  }
  
}