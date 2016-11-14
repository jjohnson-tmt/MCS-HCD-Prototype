package com.tmt.tcs.mcs.mcsHcdPrototype;

import com.google.protobuf.InvalidProtocolBufferException;
import com.tmt.tcs.mcs.mcsHcdPrototype.CommandExecutor;
import com.tmt.tcs.mcs.mcsHcdPrototype.OperationResult;
import com.tmt.tcs.mcs.mcsHcdPrototype.StatusCodes;
import com.tmt.tcs.mcs.protos.TcsMcsEventsProtos.TcsPositionDemandEvent;
import com.tmt.tcs.mcs.protos.TcsToMcsCommandProtos.CmdError;
import com.tmt.tcs.mcs.protos.TcsToMcsCommandProtos.CommandResponse;
import com.tmt.tcs.mcs.protos.TcsToMcsCommandProtos.FollowCommand;
import com.tmt.tcs.mcs.protos.TcsToMcsCommandProtos.FollowCommand.Axes;
import com.tmt.tcs.mcs.protos.TcsToMcsCommandProtos.LifeCycleCommand;
import com.tmt.tcs.mcs.protos.TcsToMcsCommandProtos.TransitionType;
import com.tmt.tcs.mcs.tcsToMcsEvents.PositionDemandEvent;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.internal.util.reflection.Whitebox;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.testng.PowerMockTestCase;
import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Socket;

import static org.junit.Assert.assertTrue;

/**
 * Unit test case for {@link CommandExecutor} class.
 * @author E401581.
 *
 */

public class CommandExecutorTest {
  
  /**
   * Simulate timeout on MCS for sending command response.
   */
  private static final int RESPONSE_SEND_TIMEOUT = 3000;

  /**
   * Header for lifecycle command.
   */
  private static final String LIFECYCLE_COMMAND_NAME = "tcs_mcs_Lifecycle";

  /**
   * Header for follow command.
   */
  private static final String FOLLOW_COMMAND_NAME = "tcs_mcs_Follow";

  /**
   * Address of pull socket on MCS which receives command.
   */
  private static final String MCS_PULL_SOCKET_ADDRESS = "tcp://localhost:5550";

  /**
   * Address of push socket on MCS which sends response.
   */
  private static final String MCS_PUSH_SOCKET_ADDRESS = "tcp://localhost:5551";
  
  /**
   * Position demand event name.
   */
  private static final String POSITION_DEMAND_EVENT_NAME = "tcs_mcs_PositionDemand";

  /**
   * Address string for sub socket.
   */
  private static final String SUB_ADDRESS = "tcp://localhost:5552";
  
  /**
   * Address string for push socket.
   */
  private static final String PUSH_ADDRESS = "tcp://localhost:5550";
  
  /**
   * Address string for pull socket.
   */
  private static final String PULL_ADDRESS = "tcp://localhost:5551";
  
  /**
   * Address string for pub socket.
   */
  private static final String PUB_ADDRESS = "tcp://localhost:5552";

  @BeforeClass
  public static void setUpBeforeClass() throws Exception {

  }

  @AfterClass
  public static void tearDownAfterClass() throws Exception {
  }

  /**
   * Setup for the class creating object and initialization and startup.
   * @throws java.lang.Exception Exception during setup.
   */
  @Before
  public void setUp() throws Exception {            
         
  }

  /**
   * Cleanup for the CommandExecutorTest class.
   */
  @After
  public void tearDown() throws Exception {
    
  }  
  
  /**
   * Integration test for sending of position demand event.
   */
  @Test
  public final void testsendPositionDemands() {
    //Setup
    ZMQ.Context context = ZMQ.context(1);
    Socket subscriber = context.socket(ZMQ.SUB);
    // Connect and subscribe for topic
    if (subscriber != null) {
      subscriber.connect(SUB_ADDRESS);
      subscriber.subscribe(POSITION_DEMAND_EVENT_NAME.getBytes());
    }
    
    CommandExecutor executor = CommandExecutor.getInstance();    
    executor.setContext(context);
    executor.init();
    executor.start();    
    
    byte[] content = null;
    String eventName = null;
            
    boolean status = executor.sendPositionDemands(0.0, 0.0, 0.0);
    
    if (!status) {
      assertTrue(false);
      return;
    }        
    
    //Check if position demand event is received.
    eventName = subscriber.recvStr(ZMQ.DONTWAIT);    
    if (eventName != null) {              
      // Read 2nd part of message
      if (subscriber.hasReceiveMore()) {
        content = subscriber.recv(ZMQ.DONTWAIT);
        
        if (content != null) {
          if (POSITION_DEMAND_EVENT_NAME.compareTo(eventName) == 0) {
            TcsPositionDemandEvent tcsPositionDemandEvent = null;
            
            try {
              tcsPositionDemandEvent = TcsPositionDemandEvent.parseFrom(content);               
              if (tcsPositionDemandEvent != null) {
                assertTrue(true);
              } else {
                assertTrue(false);
              }
            } catch (InvalidProtocolBufferException ex) {
              // TODO Auto-generated catch block
              ex.printStackTrace();
              assertTrue(false);
            }            
          }                   
        }        
      }
    }
    
    //cleanup
    executor.stop();
    if (subscriber != null) {
      subscriber.unsubscribe(POSITION_DEMAND_EVENT_NAME.getBytes());
      subscriber.disconnect(SUB_ADDRESS);
      subscriber.close();
    }    
    
    context.term();
        
  }
  
  /**
   * Integration test for sending lifecycle command.
   */
  @Test
  public final void testsendLifecycleCommand() {        
    //Setup
    ZMQ.Context context = ZMQ.context(1);
    Socket pullSocket = context.socket(ZMQ.PULL);
    pullSocket.connect(MCS_PULL_SOCKET_ADDRESS);
    
    CommandExecutor executor = CommandExecutor.getInstance();
    executor.setContext(context);
    executor.init();
    executor.start();
    
    OperationResult result = executor.sendLifecycleCommand(TransitionType.STARTUP);
    if (result.getStatus() != StatusCodes.Success) {
      assertTrue(false);
    }
        
    String responseName = pullSocket.recvStr();
    if (responseName.equalsIgnoreCase(LIFECYCLE_COMMAND_NAME)) {
      // parse command
      if (pullSocket.hasReceiveMore()) {
        byte[] responsepacket = pullSocket.recv();
        
        try {
          LifeCycleCommand objlifeCommand = LifeCycleCommand.parseFrom(responsepacket);
          assertTrue(objlifeCommand != null);
        } catch (InvalidProtocolBufferException ex) {
          // TODO Auto-generated catch block
          ex.printStackTrace();
          assertTrue(false);
        }
      }
    }
    
    //Cleanup
    executor.stop();
    
    if (pullSocket != null) {
      pullSocket.disconnect(MCS_PULL_SOCKET_ADDRESS);
      pullSocket.close();
    }
    
    context.term();
  }
    
  /**
   * Integration test for reading lifecycle command response.
   */
  @Test
  public final void testreadLifecycleCommandResponse() {  
    //Setup
    ZMQ.Context context = ZMQ.context(1);
    CommandExecutor executor = CommandExecutor.getInstance();
    executor.setContext(context);
    executor.init();
    executor.start();
    //Send lifecycle command response
    CommandResponse res = CommandResponse.newBuilder()
        .setErrorState("OK").setCmdError(CmdError.BUSY)
        .setErrorInfo("No Error").setTime(1.0).build();
    
    Socket pushSocket = context.socket(ZMQ.PUSH);
    pushSocket.setSendTimeOut(RESPONSE_SEND_TIMEOUT);
    pushSocket.bind(MCS_PUSH_SOCKET_ADDRESS);
    boolean status = pushSocket.sendMore(LIFECYCLE_COMMAND_NAME);
    if (status) {
      status = pushSocket.send(res.toByteArray(), ZMQ.NOBLOCK);              
      OperationResult result = executor.readLifecycleCommandResponse();            
      if (result == null) {
        //Cleanup
        executor.stop();
        
        if (pushSocket != null) {
          pushSocket.unbind(MCS_PUSH_SOCKET_ADDRESS);
          pushSocket.close();
        }
        
        context.term();
        
        assertTrue(false);
        return;
      } else if (result.getStatus() != StatusCodes.Success) {
        assertTrue(false);
      } else {
        assertTrue(true);
      }
    }
    
    //Cleanup
    executor.stop();
    
    if (pushSocket != null) {
      pushSocket.unbind(MCS_PUSH_SOCKET_ADDRESS);
      pushSocket.close();
    }
    
    context.term();
  }
    
  /**
   * Integration test for reading lifecycle command response when response is not yet received.
   */
  @Test
  public final void testreadLifecycleCommandResponseNothingtoread() {
    //Setup
    ZMQ.Context context = ZMQ.context(1);
    CommandExecutor executor = CommandExecutor.getInstance();
    executor.setContext(context);
    executor.init();
    executor.start();
    
    OperationResult result = executor.readLifecycleCommandResponse();
    assertTrue(result == null);
    
    executor.stop();
    context.term();
  }
    
  /**
   * Integration test for sending follow command.
   */
  @Test
  public final void testsendFollowCommand() {
    //Setup
    ZMQ.Context context = ZMQ.context(1);
    Socket pullSocket = context.socket(ZMQ.PULL);
    pullSocket.connect(MCS_PULL_SOCKET_ADDRESS);
    
    CommandExecutor executor = CommandExecutor.getInstance();
    executor.setContext(context);
    executor.init();
    executor.start();
    
    OperationResult result = executor.sendFollowCommand(Axes.BOTH);
    if (result.getStatus() != StatusCodes.Success) {
      assertTrue(false);
    }          
    
    String responseName = pullSocket.recvStr();
    if (responseName.equalsIgnoreCase(FOLLOW_COMMAND_NAME)) {
      // parse command
      if (pullSocket.hasReceiveMore()) {
        byte[] responsepacket = pullSocket.recv();
          
        try {
          FollowCommand objfollowCommand = FollowCommand.parseFrom(responsepacket);
          assertTrue(objfollowCommand != null);
        } catch (InvalidProtocolBufferException ex) {
          // TODO Auto-generated catch block
          ex.printStackTrace();
          assertTrue(false);
        }
      }
    }
    
    executor.stop();
    if (pullSocket != null) {
      pullSocket.disconnect(MCS_PULL_SOCKET_ADDRESS);
      pullSocket.close();
    }
    
    context.term();
  }
      
  /**
   * Integration test for reading follow command response.
   */
  @Test
  public final void testreadFollowCommandResponse() {
    //Setup
    ZMQ.Context context = ZMQ.context(1);
    CommandExecutor executor = CommandExecutor.getInstance();
    executor.setContext(context);
    executor.init();
    executor.start();
        
    //Send follow command response
    CommandResponse res = CommandResponse.newBuilder()
        .setErrorState("OK").setCmdError(CmdError.BUSY)
        .setErrorInfo("No Error").setTime(1.0).build();
    Socket pushSocket = context.socket(ZMQ.PUSH);
    pushSocket.setSendTimeOut(RESPONSE_SEND_TIMEOUT);
    pushSocket.bind(MCS_PUSH_SOCKET_ADDRESS);
    
    boolean status = pushSocket.sendMore(FOLLOW_COMMAND_NAME);
    if (status) {
      status = pushSocket.send(res.toByteArray(), ZMQ.NOBLOCK);
          
      OperationResult result = executor.readFollowCommandResponse();
      if (result == null) {
        assertTrue(false);
        
      } else if (result.getStatus() != StatusCodes.Success) {
        assertTrue(false);
      } else {
        assertTrue(true);
      }
    }
    //Cleanup
    executor.stop();
    if (pushSocket != null) {
      pushSocket.unbind(MCS_PUSH_SOCKET_ADDRESS);
      pushSocket.close();
    }
    
    context.term();
  }
      
  /**
   * Unit test for reading follow command response when response is not yet received.
   */
  @Test
  public final void testreadFollowCommandResponseNothingtoread() {
    //Setup
    ZMQ.Context context = ZMQ.context(1);
    CommandExecutor executor = CommandExecutor.getInstance();
    executor.setContext(context);
    executor.init();
    executor.start();
    
    OperationResult result = executor.readFollowCommandResponse();
    assertTrue(result == null);
    //Cleanup
    executor.stop();
    context.term();
  }
  
  /**
   * Unit test for getInstance() method.
   */
  @Test
  public final void testGetInstance() {
    CommandExecutor instance1 = CommandExecutor.getInstance(); 
    CommandExecutor instance2 = CommandExecutor.getInstance();
    
    assertTrue(instance1.equals(instance2));    
    instance1.stop();
  }
  
  /**
   * Unit test for setContext() method.
   */
  @Test
  public final void testSetContext() {
    CommandExecutor executor = CommandExecutor.getInstance();
    ZMQ.Context context = ZMQ.context(1);
    
    executor.setContext(context);
    ZMQ.Context setContext = (ZMQ.Context)Whitebox.getInternalState(executor, "context");
    if ((setContext.equals(context)) && (setContext.hashCode() == context.hashCode())) {
      assertTrue(true);
    }
    
    context.term();
    executor.stop();
  }
  
  /**
   * Unit test for init() method.
   */
  @Test
  public final void testInit() {
    CommandExecutor executor = CommandExecutor.getInstance(); 
    ZMQ.Context context = ZMQ.context(1);
    //Set the context prerequisite for init
    Whitebox.setInternalState(executor, "context", context);    
    executor.init();
    if (Whitebox.getInternalState(executor, "errorResponse") == null) {
      assertTrue(false);
      return;
    }
    if (Whitebox.getInternalState(executor, "okResponse") == null) {
      assertTrue(false);
      return;
    }
    if (Whitebox.getInternalState(executor, "logFileName") == null) {
      assertTrue(false);
      return;
    }
    
    String pubAddress = (String)Whitebox.getInternalState(executor, "pubAddress");
    if (pubAddress.compareTo(PUB_ADDRESS) != 0) {
      assertTrue(false);
      return;
    }
    String pullAddress = (String)Whitebox.getInternalState(executor, "pullAddress");
    if (pullAddress.compareTo(PULL_ADDRESS) != 0) {
      assertTrue(false);
      return;
    }
    String pushAddress = (String)Whitebox.getInternalState(executor, "pushAddress");
    if (pushAddress.compareTo(PUSH_ADDRESS) != 0) {
      assertTrue(false);
      return;
    }
    
    if (Whitebox.getInternalState(executor, "pullSocket") == null) {
      assertTrue(false);
      return;
    }
    
    if (Whitebox.getInternalState(executor, "pushSocket") == null) {
      assertTrue(false);
      return;
    }
    
    if (Whitebox.getInternalState(executor, "publisher") == null) {
      assertTrue(false);
      return;
    }
    
    assertTrue(true);
    
    //Cleanup
    executor.start();
    executor.stop();
    context.term();
  }
  
  /**
   * Unit test for isCleanedUp() method.
   */
  @Test
  public final void testIsCleanedUp() {
    CommandExecutor executor = CommandExecutor.getInstance();
    Whitebox.setInternalState(executor, "isCleaned", true);
    if (!executor.isCleanedUp()) {
      assertTrue(false);
      return;
    }
    
    Whitebox.setInternalState(executor, "isCleaned", false);
    if (executor.isCleanedUp()) {
      assertTrue(false);
      return;
    }
    
    assertTrue(true);    
  }    
  
  /**
   * Unit test for stop() method.
   */
  @Test
  public final void testStop() {
    CommandExecutor executor = CommandExecutor.getInstance();     
        
    executor.stop();
    if ((Whitebox.getInternalState(executor, "cmdExecutor") == null) 
        && (((Boolean)Whitebox.getInternalState(executor, "isCleaned")).booleanValue() == true)) {
      assertTrue(true);      
    }
    
  }
}
