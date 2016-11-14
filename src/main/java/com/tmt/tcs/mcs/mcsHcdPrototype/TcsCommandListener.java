package com.tmt.tcs.mcs.mcsHcdPrototype;

import com.tmt.tcs.mcs.protos.TcsToMcsCommandProtos.FollowCommand.Axes;
import com.tmt.tcs.mcs.protos.TcsToMcsCommandProtos.TransitionType;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.Properties;
import java.util.PropertyResourceBundle;
import java.util.Queue;
import java.util.ResourceBundle;
import java.util.logging.Logger;

/**
 * {@link CommandListener} implementation for CommandListener
 * @author E401581.
 *
 */
final class TcsCommandListener implements CommandListener {
  
  /**
   * Flag indicates that command listener is already processing a command.
   */
  private boolean isBusy = false;
  
  /**
   * Lifecycle command identifier.
   */
  private static final int LIFECYCLE_COMMAND = 1;
  
  /**
   * Follow command identifier.
   */
  private static final int FOLLOW_COMMAND = 2;
  
  /**
   * Represents current command under execution.
   */
  private int commandUnderExecution;
  
  /**
   * Queue contains mcs commands sent by TCS.
   */
  private Queue<Integer> mcsCommandQueue = null;
  
  /**
   * Key name errorstateheader from string.properties.
   */
  private static final String ERROR_STATE_HEADER = "errorstateheader";
  
  /**
   * Key name errorinfoheader from string.properties.
   */
  private static final String ERROR_INFO_HEADER = "errorinfoheader";
  
  /**
   * Key name timeheader from string.properties.
   */
  private static final String TIME_HEADER = "timeheader";
  
  /**
   * Logger instance.
   */
  private static final Logger logger = Logger.getLogger(TcsCommandListener.class.getName());
  
  /**
   * Resource bundle to load resources.
   */
  private static ResourceBundle strings = null;
  
  /**
   * Name of string resource file.
   */
  private static final String RESOURCE_FILE_NAME = "string.properties";
  
  /**
   * Singleton instance for the class.
   */
  private static TcsCommandListener instance = null;
  
  /**
   * Receive timeout in miliseconds.
   */
  private int receiveTimeout = 5000;

  /**
   * Config property name for receive timeout value in miliseconds.
   */
  private static final String RECEIVE_TIMEOUT_KEY = "receivetimeout";
  
  /**
   * {@link HcdRuntimeController} instance.
   */
  private HcdRuntimeController controller = null;
  
  /**
   * Time when command was sent in miliseconds.
   */
  private long commandSentTime;
  
  /**
   * Time when follow command was dequeued.
   */
  private long commandDequeuedTime;
  
  /**
   * Delay in miliseconds for follow command.
   */
  private static final long FOLLOW_COMMAND_DELAY = 18000;
  
  /**
   * {@link OperationResult} of sending/receiving TCS command to MCS on ZeroMQ.
   */
  private OperationResult result = null;
    
  /**
   * Initializes new instance of TcsCommandListener.
   */
  private TcsCommandListener() {
    mcsCommandQueue = new LinkedList<Integer>();
  }
  
  /**
   * Setting callback for state event reporting.
   * @param controller {@link HcdRuntimeController} object.
   */
  public void setCallback(HcdRuntimeController controller) {
    this.controller = controller;
  }
  
  /**
   * Gets singleton instance of class.
   * @return object of type TcsCommandListener.
   */
  public static TcsCommandListener getInstance() {
    if (instance == null) {
      instance = new TcsCommandListener();
    }
    
    return instance;
  }
  
  /**
   * Provides intialization.
   */
  public void init() {
    logger.info("init called.");
    try {
      FileInputStream fis = new FileInputStream(RESOURCE_FILE_NAME);
      try {
        strings = new PropertyResourceBundle(fis);
      } catch (IOException ex) {
        ex.printStackTrace();
        logger.severe(ex.getMessage());
      }
    } catch (FileNotFoundException e1) {
      e1.printStackTrace();
      logger.severe(e1.getMessage());
    }
    
    //Code to simulate reading commands from MCS assembly on private AKKA and queuing them in
    mcsCommandQueue.offer(LIFECYCLE_COMMAND);
    mcsCommandQueue.offer(FOLLOW_COMMAND);
    logger.info("Queued lifecycle and follow command messages.");
    
    // Read configuration
    Properties prop = new Properties();    
    
    try (InputStream input = new FileInputStream("config.properties")) {               
      // load a properties file
      try {
        prop.load(input);
        receiveTimeout = Integer.parseInt(prop.getProperty(RECEIVE_TIMEOUT_KEY));
        logger.info("Read timeout from config properties.");
      } catch (IOException ex) {
        ex.printStackTrace();
      } 
    } catch (IOException ex) {
      ex.printStackTrace();
    } 

    logger.info("init completed.");
  }

  /**
   * Listens for commands from MCS Assembly, initiates execution for commands and returns response.
   */
  public void listenforHcdCommand() {
    logger.info("listenforHcdCommand called.");
    //Read command from queue
    Integer command = mcsCommandQueue.peek();
    if (command != null) {
      logger.info("dequeued command: " + command.toString());      
    }
    
    if ((command == null) && (!isBusy)) {
      return;
    } else if (isBusy) {
      logger.info("Lisetner already has command sent: " 
          + ((Integer)commandUnderExecution).toString());
      if (command != null) {
        //TBD
        //Send busy response to MCS Assembly
      }      
      
      //Read response for the command currently under execution
      switch (commandUnderExecution) {
        case  LIFECYCLE_COMMAND:
          result = CommandExecutor.getInstance().readLifecycleCommandResponse();
          break;
        case FOLLOW_COMMAND:
          result = CommandExecutor.getInstance().readFollowCommandResponse();
          break; 
        default:
          break;
            
      }
      
      if (result == null) {
        logger.info("Lisetner did not receive response for command: " 
            + ((Integer)commandUnderExecution).toString());
        //Consider timeout
        if ((System.currentTimeMillis() - commandSentTime) > receiveTimeout) {
          //isBusy = false in case of timeout
          //This will allow retry
          logger.info("Response timeout for command: " 
              + ((Integer)commandUnderExecution).toString());
          isBusy = false;
          System.out.println("Response timeout.");
          return;
        }
                        
      } else {
        //Retry in case command status is fail
        logger.info("Listener received response for command: " 
            + ((Integer)commandUnderExecution).toString());
        
        // Print command response
        StatusCodes status = result.getStatus();
        System.out.println(strings.getString(ERROR_STATE_HEADER)
            + status.name());

        System.out.println(strings.getString(ERROR_INFO_HEADER)
            + result.getErrorInfo());
        System.out.println(strings.getString(TIME_HEADER)
            + Double.toString(result.getTimeWhenProcessed()));

        if (status != StatusCodes.Success) {
          System.out.println(result.getErrorInfo());
          System.out.println(Double.toString(result.getTimeWhenProcessed()));
          logger.info("Response acknowledgment has fail status for command: " 
              + ((Integer)commandUnderExecution).toString());
          //Dont remove from queue only reset flag to enable retry
          isBusy = false;
          return;
        } else {
          // If success
          // Print
          logger.info("command succeeded");
          
          mcsCommandQueue.poll();
          isBusy = false;
          //For prototype only.
          if (commandUnderExecution == FOLLOW_COMMAND) {
            controller.setState(true);
          } else {
            //delay follow command send
            commandDequeuedTime = System.currentTimeMillis();
          }
          return;
        }                
      }
      
      return;
    } 
    
    if ((command != null) && (!isBusy)) {
      commandUnderExecution = command;
      //identify the type of command
      //Call respective method on Cmd Executor for sending command
            
      switch (command) {
        case  LIFECYCLE_COMMAND:
          System.out.println("Sending lifecycle command with startup");
          result = CommandExecutor.getInstance()
          .sendLifecycleCommand(TransitionType.STARTUP);          
          break;
        case FOLLOW_COMMAND:
          
          if ((System.currentTimeMillis() - commandDequeuedTime) > FOLLOW_COMMAND_DELAY) {
            //logic only for prototype implementation 
            //since the simulator does not respond to Follow command within
            //the first 10 seconds 
            //after responding successfully to the lifecycle command.
            System.out.println("Sending follow command for both axes");
            result = CommandExecutor.getInstance().sendFollowCommand(Axes.BOTH);
          } else {
            return;
          }
          
          break;
        default:
          break;
      }
      
      if ((result != null) && (result.getStatus() == StatusCodes.Success)) {
        logger.info("Command sent to MCS, command: " + ((Integer)commandUnderExecution).toString());
        isBusy = true;
        commandSentTime = System.currentTimeMillis();
        System.out.println("command sent successfully.");
      } else {
        logger.info("Command sending to MCS failed, command: " 
            + ((Integer)commandUnderExecution).toString());
        System.out.println("command sending failed.");
        //no listener connected drop the command.
        //mcsCommandQueue.poll();
        
      }
    } 
    
    logger.info("listenforHcdCommand exit.");
  }
  
  public void start() {
    
  }

  public void stop() {
    //Ensures that a new instance would be created.
    instance = null;
  }
}
