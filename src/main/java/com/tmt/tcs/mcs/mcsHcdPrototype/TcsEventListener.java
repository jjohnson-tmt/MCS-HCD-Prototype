package com.tmt.tcs.mcs.mcsHcdPrototype;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

/**
 * @author E401581.
 *     {@link EventListener} implementation for TcsEventListener.
 *     As for the prototype generates continuous position demands.
 */
final class TcsEventListener implements EventListener {

  /**
   * Singleton instance for the class.
   */
  private static TcsEventListener instance = null;
  
  /**
   * azimuth position demand.
   */
  private double az = 30.0;

  /**
   * elevation position demand.
   */
  private double el = 30.0;

  /**
   * Increment for the az and el demand.
   */
  private static final double DEMAND_INCREMENT = 5.00;

  /**
   * Minimum az, el demand value.
   */
  private static final double MIN_DEMAND = 30.0;

  /**
   * Maximum az, el demand value.
   */
  private static final double MAX_DEMAND = 60.0;

  /**
   * number of events to complete 1 minute.
   */
  private static final int MAX_ITERATIONS = 6000;

  /**
   * Name of log file.
   */
  private static String logFileName = null;

  /**
   * Config property name for log file.
   */
  private static final String LOG_FILE_NAME_KEY = "logfile";

  /**
   * Logger instance.
   */
  private static final Logger logger = Logger
      .getLogger(TcsEventListener.class.getName());

  /**
   * Counter for changing demand.
   */
  private int startIteration = 1;
  
  /**
   * Gets the current Az value.
   * @return Az.
   */
  public double getAz() {
    return az;
  }
  
  /**
   * Gets the current El value.
   * @return El.
   */
  public double getEl() {
    return el;
  }
  
  /**
   * Initializes new instance of TcsEventListener.
   */
  private TcsEventListener() {

  }
  
  /**
   * Gets singleton instance of class.
   * @return object of type TcsEventListener.
   */
  public static TcsEventListener getInstance() {
    if (instance == null) {
      instance = new TcsEventListener();
    }
    
    return instance;
  }

  /* (non-Javadoc)
   * @see com.tmt.tcs.mcs.mcsHcdPrototype.EventListener#listenforHcdCommand()
   */
  public void listenforEvents() {
    //Logic to return new position demand each time the method is called.
    boolean status = CommandExecutor.getInstance().sendPositionDemands(az, el, 0.0);

    if (status) {
      Double azimuth = new Double(az);
      Double elevation = new Double(el);
      startIteration++;
      System.out.println("position demand sent.");
      System.out.println("demand sent " + azimuth.toString() + ", "
          + elevation.toString());

      logger.info("position demand sent az = " + azimuth.toString()
          + " el = " + elevation.toString());

      // Generate next az
      if (startIteration == MAX_ITERATIONS) {
        if (az == MAX_DEMAND) {
          az = MIN_DEMAND;
          logger.info("az demand reset to " + ((Double)az).toString());
        } else {
          az += DEMAND_INCREMENT;
          logger.info("az demand incremented to " + ((Double)az).toString());
        }
      }

      // Generate next el
      if (startIteration == MAX_ITERATIONS) {
        if (el == MAX_DEMAND) {
          el = MIN_DEMAND;
          logger.info("el demand reset to " + ((Double)el).toString());
        } else {
          el += DEMAND_INCREMENT;
          logger.info("el demand incremented to " + ((Double)el).toString());
        }

        startIteration = 1;
      }
    }

  }

  public void init() {
    logger.info("init called.");
    Properties prop = new Properties();    

    try (InputStream input = new FileInputStream("config.properties")) {      
      // load a properties file
      try {
        prop.load(input);
        logFileName = prop.getProperty(LOG_FILE_NAME_KEY);
      } catch (IOException e2) {
        e2.printStackTrace();
      } 

    } catch (IOException e2) {
      e2.printStackTrace();
    } 
    // Logger initialization
    try {

      logger.addHandler(new FileHandler(logFileName));
    } catch (SecurityException ex) {
      ex.printStackTrace();
    } catch (IOException ex) {
      ex.printStackTrace();
    }
    
    logger.info("init completed.");    
  }

  public void start() {
    
  }

  public void stop() {
    //Ensures that a new instance will be created.
    instance = null;
  }

}
