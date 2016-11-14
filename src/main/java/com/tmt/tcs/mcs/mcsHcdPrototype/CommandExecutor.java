package com.tmt.tcs.mcs.mcsHcdPrototype;

import com.tmt.tcs.mcs.mcsCommands.McsCommand;
import com.tmt.tcs.mcs.mcsCommands.McsFollowCommand;
import com.tmt.tcs.mcs.mcsCommands.McsLifecycleCommand;
import com.tmt.tcs.mcs.protos.TcsToMcsCommandProtos.CommandResponse;
import com.tmt.tcs.mcs.protos.TcsToMcsCommandProtos.FollowCommand.Axes;
import com.tmt.tcs.mcs.protos.TcsToMcsCommandProtos.TransitionType;
import com.tmt.tcs.mcs.tcsToMcsEvents.PositionDemandEvent;

import org.zeromq.ZMQ;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

/**
 * Class contains implementation for sending commands and
 * events on ZeroMQ. This class contains implementation for the communication layer. It is
 * responsible for setting up ZeroMQ sockets for communication.
 */
final class CommandExecutor {
  /**
   * Reference for single instance of class.
   */
  private static CommandExecutor cmdExecutor = null;

  /**
   * Address string for push socket.
   */
  private String pushAddress = "tcp://localhost:";
  
  /**
   * Key for reading push url from config.
   */
  private static final String PUSH_URL_KEY = "pushurl";

  /**
   * Address string for pull socket.
   */
  private String pullAddress = "tcp://localhost:";
  
  /**
   * Key for reading pull url from config.
   */
  private static final String PULL_URL_KEY = "pullurl";

  /**
   * Address string for pub socket.
   */
  private String pubAddress = "tcp://localhost:";  
  
  /**
   * Key for reading pub url from config.
   */
  private static final String PUB_URL_KEY = "puburl";

  /**
   * ZeroMQ context.
   */
  private ZMQ.Context context = null;

  /**
   * ZeroMQ pull socket.
   */
  private ZMQ.Socket pullSocket = null;

  /**
   * ZeroMQ push socket.
   */
  private ZMQ.Socket pushSocket = null;

  /**
   * ZeroMQ publisher socket.
   */
  private ZMQ.Socket publisher = null;
  
  /**
   * error_state value in response in case of success.
   */
  private String okResponse = null;

  /**
   * error_state value in response in case of failure.
   */
  private String errorResponse = null;

  /**
   * Name of log file.
   */
  private String logFileName = null;

  /**
   * Receive timeout in miliseconds.
   */
  private int receiveTimeout = 5000;

  /**
   * Config property name for receive timeout value in miliseconds.
   */
  private static final String RECEIVE_TIMEOUT_KEY = "receivetimeout";  

  /**
   * Config property name for error response identifier.
   */
  private static final String ERROR_RESPONSE_VALUE_KEY = "errorresponsevalue";

  /**
   * Config property name for ok response identifier.
   */
  private static final String OK_RESPONSE_KEY = "okresponse";
  
  /**
   * Config property name for ZeroMQ publish port.
   */
  private static final String PUBLISH_PORT_KEY = "publishport";

  /**
   * Config property name for ZeroMQ pull port.
   */
  private static final String PULL_PORT_KEY = "pullport";

  /**
   * Config property name for ZeroMQ push port.
   */
  private static final String PUSH_PORT_KEY = "pushport";

  /**
   * Config property name for log file.
   */
  private static final String LOG_FILE_NAME_KEY = "logfile";

  /**
   * Error info string in case of command send time out.
   */
  private static final String SEND_TIMEOUT_ERROR_INFO = "Send timeout";

  /**
   * Synchronization object to ensure only 1 thread enters the
   * sendpositiondemands block.
   */
  private final Object positionDemandSync = new Object();

  /**
   * Logger instance.
   */
  private static final Logger logger = Logger.getLogger(CommandExecutor.class.getName());

  /**
   * Flag indicates if the object is cleaned up, if so caller should get a new
   * instance by calling getInstance. After getting new instance caller should
   * also follow init() and start() sequence.
   */
  private boolean isCleaned = false;  

  /**
   * Constructor for the class.
   */
  private CommandExecutor() {

  }  
  
  /**
   * Setting zeromq context.
   * @param context ZeroMQ context required for scoket creation.
   */
  void setContext(ZMQ.Context context) {
    this.context = context;
  }

  /**
   * Provides initialization for the MCS HCD.
   */
  void init() {
    logger.info("Initializing HCD");
    // Read configuration
    Properties prop = new Properties();

    try (InputStream input = new FileInputStream("config.properties")) {      
      // load a properties file
      prop.load(input);

      // Read and assign variables
      receiveTimeout = Integer.parseInt(prop.getProperty(RECEIVE_TIMEOUT_KEY));     
            
      errorResponse = prop.getProperty(ERROR_RESPONSE_VALUE_KEY);
      okResponse = prop.getProperty(OK_RESPONSE_KEY);      
            
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

      pubAddress = prop.getProperty(PUB_URL_KEY);
      StringBuilder builder = new StringBuilder(pubAddress);
      pubAddress = builder.append(prop.getProperty(PUBLISH_PORT_KEY)).toString();

      pullAddress = prop.getProperty(PULL_URL_KEY);
      builder = new StringBuilder(pullAddress);
      pullAddress = builder.append(prop.getProperty(PULL_PORT_KEY)).toString();

      pushAddress = prop.getProperty(PUSH_URL_KEY);
      builder = new StringBuilder(pushAddress);
      pushAddress = builder.append(prop.getProperty(PUSH_PORT_KEY)).toString();

      logger.info("Read and assigned properties successfully");
    } catch (IOException e1) {
      e1.printStackTrace();
      logger.severe(e1.getMessage());
    }   

    pullSocket = context.socket(ZMQ.PULL);
    pullSocket.setReceiveTimeOut(receiveTimeout);

    pushSocket = context.socket(ZMQ.PUSH);
    pushSocket.setSendTimeOut(receiveTimeout);

    publisher = context.socket(ZMQ.PUB);    
    logger.info("Initialized ZeroMQ context and sockets");
  }

  /**
   * Create push, pull, pub, sub socket connections. Bind sockets and start
   * thread to listen for MCS events.
   */
  void start() {

    logger.info("MCS HCD start called");
    if (pushSocket != null) {
      pushSocket.bind(pushAddress);
    }

    if (pullSocket != null) {
      pullSocket.connect(pullAddress);
    }

    if (publisher != null) {
      publisher.bind(pubAddress);      
    }

    logger.info("Socket binding and connection completed");        
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
  void stop() {
    logger.info("MCS HCD stop called");    

    // ZeroMQ cleanup
    if (pullSocket != null) {             
      //Set linger to 0 for dropping pending connects and sends otherwise context.term() will hang.
      pullSocket.setLinger(0);
      pullSocket.disconnect(pullAddress);
      pullSocket.close();
    }

    if (pushSocket != null) {
      //Set linger to 0 for dropping pending connects and sends otherwise context.term() will hang.
      pushSocket.setLinger(0);
      pushSocket.unbind(pushAddress);
      pushSocket.close();
    }

    if (publisher != null) {
      //Set linger to 0 for dropping pending connects and sends otherwise context.term() will hang.
      publisher.setLinger(0);
      publisher.unbind(pubAddress);
      publisher.close();
    }        

    logger.info("ZeroMQ cleanup completed");
    cmdExecutor = null;
    isCleaned = true;
  }
  
  /**
   * Sends command packet on ZeroMq and returns status.
   * @param command object of type {@link McsCommand} representing the command to be sent to MCS.   
   * @return object of type OperationResult.
   *     It contains status of operation and description in case of error.
   */
  private OperationResult sendCommand(McsCommand command) {
    logger.info("MCS HCD sendCommand called");
    if (command == null) {
      return null;
    }

    // Clean up anything pending on the pull socket
    byte[] garbage = pullSocket.recv(ZMQ.DONTWAIT);
    if (garbage != null) {
      garbage = null;
      while (pullSocket.hasReceiveMore()) {
        garbage = pullSocket.recv(ZMQ.DONTWAIT);
        garbage = null;
      }
    }

    // send command
    boolean status = false;
    logger.info("Sending command idedntifier on ZeroMQ");
    status = pushSocket.sendMore(command.getCommandName());    

    if (status) {
      logger.info("Command identifier sent successfully");
      logger.info("Sending command data");
      status = pushSocket.send(command.encode(), ZMQ.NOBLOCK);
      if (status) {
        return new OperationResult(okResponse, 0.0, StatusCodes.Success);        
      } else {
        logger.severe("MCS command send time out");        
        
        return new OperationResult(SEND_TIMEOUT_ERROR_INFO, 0.0, StatusCodes.Timeout);
      }
      
    } else {
      return new OperationResult(SEND_TIMEOUT_ERROR_INFO, 0.0, StatusCodes.Timeout);
    }
  }
  
  /**
   * Reads response packet from MCS on PULL socket.
   * Validates that the response is of the expected type.
   * @param messageName expected response type.
   * @return response packet as byte[] if the response is of the expected type or null.
   */
  private byte[] readCommandResponse(String messageName) {
    logger.info("readCommandResponse called for command: " + messageName);
    byte[] responsepacket = null;
    String responseName = pullSocket.recvStr(ZMQ.DONTWAIT);
    if ((responseName != null) && (responseName.compareTo(messageName) != 0)) {
      // Response does not belong to the command sent
      logger.info("Response received but is of type: " + responseName);
      return null;
    } else if (responseName == null) {
      // Not yet received
      logger.info("Response not yet received");
      return null;
    }
    
    logger.info("Response received for the expected type");
    // Read 2nd part of response
    if (pullSocket.hasReceiveMore()) {
      responsepacket = pullSocket.recv(ZMQ.DONTWAIT);
    }
    
    return responsepacket;
  }
  
  /**
   * Sends lifecycle command to MCS on ZeroMQ
   * @param transition parameter of type {@link TransitionType} enum.
   * @return status of operation with description in case of error {@link OperationResult}.
   */
  synchronized OperationResult sendLifecycleCommand(TransitionType transition) {
    logger.info("sendLifecycleCommand called");
    // Create command object
    McsLifecycleCommand command = McsLifecycleCommand.getInstance();   
    command.setTransition(transition);
    logger.info("calling sendCommand");
    OperationResult result = sendCommand(command);
    return result;
  }
  
  /**
   * Reads MCS response for lifecycle command.
   * @return null in case the response is not yet received.
   *     If the response is received, 
   *     parse the response and return status, 
   *     error info as {@link OperationResult}.
   */
  synchronized OperationResult readLifecycleCommandResponse() {
    logger.info("readLIfecycleCommandResponse called");
    // Get command object
    McsLifecycleCommand command = McsLifecycleCommand.getInstance();
    
    byte[] responsePacket = readCommandResponse(command.getCommandName());
    
    if (responsePacket != null) {
      logger.info("Response received");
           
      //Parse response
      CommandResponse commandResponse = null;                      
      logger.info("Response received");
      commandResponse = command.decode(responsePacket);        
            
      if (commandResponse != null) {
        logger.info("Response parsed successfully");
        if (commandResponse.getErrorState().compareToIgnoreCase(okResponse) == 0) {
          // Success
          logger.info("Response error state is ok");
          return new OperationResult(okResponse, commandResponse.getTime(),
              StatusCodes.Success);
        } else if (commandResponse.getErrorState().compareToIgnoreCase(
            errorResponse) == 0) {
          // Failure
          StatusCodes code = StatusCodes.IllegalState;
          logger.info("Response error state " + code.name());
          switch (commandResponse.getCmdError()) {          
            case BUSY:
              code = StatusCodes.Busy;
              break;
            case FAILED:
              code = StatusCodes.Failure;
              break;
            case ILLEGAL_STATE:
              code = StatusCodes.IllegalState;
              break;
            case OUT_OF_RANGE:
              code = StatusCodes.OutofRange;
              break;
            case OUT_OF_SPEC:
              code = StatusCodes.OutofSpec;
              break;
            default:
              break;
          }

          return new OperationResult(commandResponse.getErrorInfo(),
              commandResponse.getTime(), code);
        } else {
          return null;
        }
      }
      return null;

    } else {
      logger.info("Response not yet received");
      return null;
    }
  }
  
  /**
   * Sends follow command for the specified axis to MCS and returns status.
   * @param axes Parameter of type {@link Axes} specifying the axis
   *      that needs to be put in the following state.
   * @return object of type {@link OperationResult}, 
   *     containing status and description text in case of error.
   */
  synchronized OperationResult sendFollowCommand(Axes axes) {
    logger.info("sendFollowCommand called");
    // Create command object
    McsFollowCommand command = McsFollowCommand.getInstance(); 
    command.setAxes(axes);
        
    logger.info("calling sendCommand");
    OperationResult result = sendCommand(command);
    return result;
  }
  
  /**
   * Reads MCS response for follow command.
   * @return null in case the response is not yet received.
   *     If the response is received, parse the response and return status, 
   *     error info in form of {@link OperationResult}.
   */
  synchronized OperationResult readFollowCommandResponse() {
    logger.info("readFollowCommandResponse called");
    // Create command object
    McsFollowCommand command = McsFollowCommand.getInstance();
    byte[] responsePacket = readCommandResponse(command.getCommandName());
    
    if (responsePacket != null) {
      logger.info("Response received");
      
      //Parse response
      CommandResponse commandResponse = null;                      
      logger.info("Response received");
      commandResponse = command.decode(responsePacket);        
            
      if (commandResponse != null) {
        logger.info("Response parsed successfully");
        if (commandResponse.getErrorState().compareToIgnoreCase(okResponse) == 0) {
          // Success
          logger.info("Response error state is ok");
          return new OperationResult(okResponse, commandResponse.getTime(),
              StatusCodes.Success);
        } else if (commandResponse.getErrorState().compareToIgnoreCase(
            errorResponse) == 0) {
          // Failure
          StatusCodes code = StatusCodes.IllegalState;
          logger.info("Response error state " + code.name());
          switch (commandResponse.getCmdError()) {          
            case BUSY:
              code = StatusCodes.Busy;
              break;
            case FAILED:
              code = StatusCodes.Failure;
              break;
            case ILLEGAL_STATE:
              code = StatusCodes.IllegalState;
              break;
            case OUT_OF_RANGE:
              code = StatusCodes.OutofRange;
              break;
            case OUT_OF_SPEC:
              code = StatusCodes.OutofSpec;
              break;
            default:
              break;
          }

          return new OperationResult(commandResponse.getErrorInfo(),
              commandResponse.getTime(), code);
        } else {
          return null;
        }
      }
      return null;

    } else {
      logger.info("Response not yet received");
      return null;
    }
  }

  /**
   * Sends position demands to MCS on the pub socket.
   * 
   * @param az
   *          azimuth demand in double.
   * @param el
   *          elevation demand in double.
   * @param time
   *          timestamp for the demand in double.
   * @return status of operation, true for success and false for failure.
   */
  boolean sendPositionDemands(double az, double el, double time) {
    logger.info("sendPositionDemands called");
    boolean status = false;
    synchronized (positionDemandSync) {
      // Create command object
      PositionDemandEvent event = PositionDemandEvent.getInstance();
      event.setAzimuth(az);
      event.setElevation(el);
      event.setTime(time);
      // Send on ZeroMQ
      // logger.info("sending event topic");
      // publisher.sendMore(tcsEventsTopic);
      logger.info("sending event identifier");

      status = publisher.sendMore(event.getEventName());
      if (status) {
        logger.info("sending event data");
        status = publisher.send(event.encode());  
      }
      
    }

    return status;
  }

  /**
   * Gets instance of CommandExecutor.
   * 
   * @return instance of CommandExecutor class.
   */
  static CommandExecutor getInstance() {
    if (cmdExecutor == null) {
      cmdExecutor = new CommandExecutor();
    }

    return cmdExecutor;
  }
}