package com.tmt.tcs.mcs.mcsHcdPrototype;

import com.google.protobuf.InvalidProtocolBufferException;

import com.tmt.tcs.mcs.mcsToTcsEvents.CurrentPositionEvent;
import com.tmt.tcs.mcs.mcsToTcsEvents.McsEvent;
import com.tmt.tcs.mcs.protos.TcsMcsEventsProtos.McsCurrentPositionEvent;

import org.zeromq.ZMQ;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

/**
 * @author E401581.
 *     {@link McsEventListener} implementation for reading mcs event from ZeroMQ SUB socket.
 */
final class McsEventListeningProvider implements McsEventListener {

  /**
   * Address string for sub socket.
   */
  private String subAddress = "tcp://localhost:";

  /**
   * ZeroMQ context.
   */
  private ZMQ.Context context = null;
  
  /**
   * ZeroMQ subscriber socket.
   */
  private ZMQ.Socket subscriber = null;
  
  /**
   * CurrentPosition event name.
   */
  private String currentPositionEventName = null;
  
  /**
   * Name of log file.
   */
  private String logFileName = null;
  
  /**
   * Config property name for TCS events topic name.
   */
  private static final String CURRENT_POSITION_EVENT_NAME_KEY = "currentpositioneventname";
  
  /**
   * Config property name for ZeroMQ subscribe port.
   */
  private static final String SUBSCRIBE_PORT_KEY = "subscribeport";
  
  /**
   * Config property name for log file.
   */
  private static final String LOG_FILE_NAME_KEY = "logfile";
  
  /**
   * Key for reading sub url from config.
   */
  private static final String SUB_URL_KEY = "suburl";
  
  /**
   * Logger instance.
   */
  private static final Logger logger = Logger.getLogger(McsEventListener.class.getName());

  /**
   * Flag indicates if the object is cleaned up, if so caller should get a new
   * instance by calling getInstance. After getting new instance caller should
   * also follow init() and start() sequence.
   */
  private boolean isCleaned = false;
  
  /**
   * Singleton instance of the class.
   */
  private static McsEventListeningProvider instance = null;
  
  /**
   * Initializes new instaance of McsEventListeningProvider.
   */
  private McsEventListeningProvider() {

  }
  
  /**
   * Setting zero mq context.
   * @param context ZeroMQ context for creating push, pull and pub sockets.
   */
  void setContext(ZMQ.Context context) {
    this.context = context;
  }
  
  /**
   * Creates singleton instance.
   * @return singleton instance.
   */
  public static McsEventListeningProvider getInstance() {
    if (instance == null) {
      instance = new McsEventListeningProvider();
    }
    
    return instance;
  }
  
  public void init() {
    logger.info("Initializing McsEventListeningProvider");
    // Read configuration
    Properties prop = new Properties();    

    try (InputStream input = new FileInputStream("config.properties")) {      
      // load a properties file
      prop.load(input);

      // Read and assign variables            
      currentPositionEventName = prop.getProperty(CURRENT_POSITION_EVENT_NAME_KEY);
      logFileName = prop.getProperty(LOG_FILE_NAME_KEY);

      // Logger initialization
      try {

        logger.addHandler(new FileHandler(logFileName));
      } catch (SecurityException ex) {
        // TODO Auto-generated catch block
        ex.printStackTrace();
      } catch (IOException ex) {
        // TODO Auto-generated catch block
        ex.printStackTrace();
      }
      logger.info("config.properties loaded successfully");

      subAddress = prop.getProperty(SUB_URL_KEY);
      StringBuilder builder = new StringBuilder(subAddress);
      subAddress = builder.append(prop.getProperty(SUBSCRIBE_PORT_KEY))
          .toString();
      
      logger.info("Read and assigned properties successfully");
    } catch (IOException e1) {
      // TODO Auto-generated catch block
      e1.printStackTrace();
      logger.severe(e1.getMessage());
    } 

    // ZeroMQ initialization    
    subscriber = context.socket(ZMQ.SUB);    

    logger.info("Initialized ZeroMQ context and sockets");
  }

  /**
   * Create push, pull, pub, sub socket connections. Bind sockets and start
   * thread to listen for MCS events.
   */
  public void start() {

    logger.info("McsEventListeningProvider start called");
    
    // Connect and subscribe for topic
    if (subscriber != null) {
      subscriber.connect(subAddress);
      subscriber.subscribe(currentPositionEventName.getBytes());
    }    

    logger.info("ZeroMQ sub socket connected and subscribed to current position event.");
  }

  /**
   * Returns if the object is cleaned up, if so caller should get a new instance
   * by calling getInstance. Caller should also follow init() and start()
   * sequence.
   */
  boolean isCleanedUp() {
    logger.info("MCS HCD isCleanedUP called");
    return isCleaned;
  }

  /**
   * Provides cleanup for MCS HCD.
   */
  public void stop() {
    logger.info("MCS HCD stop called");      
    instance = null;

    if (subscriber != null) {
      subscriber.unsubscribe(currentPositionEventName.getBytes());
      subscriber.disconnect(subAddress);
      subscriber.close();
    }    
    
    logger.info("ZeroMQ cleanup completed");      
    isCleaned = true;
  }

  /* (non-Javadoc)
   * @see com.tmt.tcs.mcs.mcsHcdPrototype.McsEventListener#listenforMcsEvent()
   */
  public McsEvent listenforMcsEvent() {
    byte[] content = null;
    String eventName = subscriber.recvStr(ZMQ.DONTWAIT);
    if (eventName == null) {
      // timeout
      logger.info("No event received.");
      return null;
    }
    
    if (eventName != null) {  
      logger.info("Event received: " + eventName);
      
      // Read 2nd part of message
      if (subscriber.hasReceiveMore()) {
        content = subscriber.recv(ZMQ.DONTWAIT);
        
        if (content != null) {
          if (currentPositionEventName.compareTo(eventName) == 0) {
            McsCurrentPositionEvent mcsCurrentPositionEvent = null;
            try {
              mcsCurrentPositionEvent = McsCurrentPositionEvent.parseFrom(content);
              logger.info("event parsed successfully"); 
              if (mcsCurrentPositionEvent != null) {
                System.out.println("Current position event received.");
                System.out.println("Az position = " 
                    + ((Double)mcsCurrentPositionEvent.getAzPos()).toString());
                System.out.println("Az position demand = " 
                    + ((Double)mcsCurrentPositionEvent.getAzPosDemand()).toString());
                System.out.println("Az position error = " 
                    + ((Double)mcsCurrentPositionEvent.getAzPosError()).toString());
                System.out.println("El position = " 
                    + ((Double)mcsCurrentPositionEvent.getElPos()).toString());
                System.out.println("El position demand = " 
                    + ((Double)mcsCurrentPositionEvent.getElPosDemand()).toString());
                System.out.println("El position error = " 
                    + ((Double)mcsCurrentPositionEvent.getElPosError()).toString());
                System.out.println("Encoder latching time = " 
                    + ((Double)mcsCurrentPositionEvent.getEncodeLatchingTime()).toString());
              }
              
              return new CurrentPositionEvent(mcsCurrentPositionEvent);
            } catch (InvalidProtocolBufferException ex) {
              ex.printStackTrace();
            }            
          }                   
        }        
      }
    }
    
    return null;
  }

}
