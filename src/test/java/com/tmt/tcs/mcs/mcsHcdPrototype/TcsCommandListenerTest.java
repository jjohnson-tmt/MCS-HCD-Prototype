package com.tmt.tcs.mcs.mcsHcdPrototype;

import com.google.protobuf.InvalidProtocolBufferException;
import com.tmt.tcs.mcs.mcsHcdPrototype.CommandExecutor;
import com.tmt.tcs.mcs.mcsHcdPrototype.HcdRuntimeController;
import com.tmt.tcs.mcs.mcsHcdPrototype.OperationResult;
import com.tmt.tcs.mcs.mcsHcdPrototype.StatusCodes;
import com.tmt.tcs.mcs.mcsHcdPrototype.TcsCommandListener;
import com.tmt.tcs.mcs.protos.TcsToMcsCommandProtos.CmdError;
import com.tmt.tcs.mcs.protos.TcsToMcsCommandProtos.CommandResponse;
import com.tmt.tcs.mcs.protos.TcsToMcsCommandProtos.FollowCommand;
import com.tmt.tcs.mcs.protos.TcsToMcsCommandProtos.FollowCommand.Axes;
import com.tmt.tcs.mcs.protos.TcsToMcsCommandProtos.LifeCycleCommand;
import com.tmt.tcs.mcs.protos.TcsToMcsCommandProtos.TransitionType;

import static org.junit.Assert.assertTrue;

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
import org.zeromq.ZMQ.Context;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedList;
import java.util.PropertyResourceBundle;
import java.util.Queue;
import java.util.ResourceBundle;

/**
 * @author E401581.
 *     Unit test for {@link TcsCommandListener} class.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({HcdRuntimeController.class, CommandExecutor.class})
public class TcsCommandListenerTest extends PowerMockTestCase {
    
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
   * Receive timeout for command response.
   */
  private static final long TIMEOUT = 18000;
  
  /**
   * Lifecycle command identifier.
   */
  private static final int LIFECYCLE_COMMAND = 1;
  
  /**
   * Follow command identifier.
   */
  private static final int FOLLOW_COMMAND = 2;

  
  /**
   * @throws java.lang.Exception Exception during setup.
   */
  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
  }

  /**
   * @throws java.lang.Exception Exception during cleanup.
   */
  @AfterClass
  public static void tearDownAfterClass() throws Exception {
  }

  /**
   * @throws java.lang.Exception Exxception during setup.
   */
  @Before
  public void setUp() throws Exception {            
    
  }

  /**
   * @throws java.lang.Exception Exception during cleanup.
   */
  @After
  public void tearDown() throws Exception {    
    
  }
  
  /**
   * Test method for 
   * {@link com.tmt.tcs.mcs.mcsHcdPrototype.TcsCommandListener#init()}.
   */
  @SuppressWarnings("unchecked")
  @Test
  public final void testInit() {
    final int queueCount = 2;
    TcsCommandListener listener = TcsCommandListener.getInstance();
    listener.init();
    Queue<Integer> commandQueue 
    = (Queue<Integer>)Whitebox
    .getInternalState(listener, "mcsCommandQueue");
    if (commandQueue.size() != queueCount) {
      assertTrue(false);
      return;
    }
    
    assertTrue(true);
    //Cleanup
    listener.stop();
  }

  /**
   * Integration Test method for 
   * {@link com.tmt.tcs.mcs.mcsHcdPrototype.TcsCommandListener#listenforHcdCommand()}.
   */
  @SuppressWarnings("unchecked")
  @Test
  public final void testListenforHcdCommand() {
    //Setup
    CommandExecutor mockExecutor = PowerMockito.mock(CommandExecutor.class);
    PowerMockito.mockStatic(CommandExecutor.class);
    Mockito.when(CommandExecutor.getInstance()).thenReturn(mockExecutor);
    
    Queue<Integer> mcsCommandQueue = new LinkedList<Integer>();
    mcsCommandQueue.offer(LIFECYCLE_COMMAND);
    mcsCommandQueue.offer(FOLLOW_COMMAND);
    ResourceBundle strings = null;
    final String RESOURCE_FILE_NAME = "string.properties";
    try {
      FileInputStream fis = new FileInputStream(RESOURCE_FILE_NAME);
      try {
        strings = new PropertyResourceBundle(fis);
      } catch (IOException ex) {
        ex.printStackTrace();
        assertTrue(false);
      }
    } catch (FileNotFoundException e1) {
      e1.printStackTrace();
      assertTrue(false);
    }
    
    TcsCommandListener listener = TcsCommandListener.getInstance();
    HcdRuntimeController controller = PowerMockito.mock(HcdRuntimeController.class);
    //Mockito.doNothing((controller.setState(Mockito.anyBoolean()));
    Whitebox.setInternalState(listener, "controller", controller);    
    Whitebox.setInternalState(listener, "mcsCommandQueue", mcsCommandQueue);
    Whitebox.setInternalState(listener, "strings", strings);
    
    //Send lifecycle command
    Mockito.when
    (mockExecutor.sendLifecycleCommand
        (TransitionType.STARTUP)).thenReturn
        (new OperationResult("Ok", 0.0, StatusCodes.Success));
    listener.listenforHcdCommand();
    OperationResult result = (OperationResult)Whitebox.getInternalState(listener, "result");
    if (result == null) {
      assertTrue(false);
      return;
    }
    
    if (result.getStatus() != StatusCodes.Success) {
      assertTrue(false);
      return;
    }
            
    int commandSent 
    = ((Integer)Whitebox
        .getInternalState(listener, "commandUnderExecution"))
        .intValue();
    if (commandSent != LIFECYCLE_COMMAND) {
      assertTrue(false);
      return;
    }
    
    boolean isBusyValue = ((Boolean)Whitebox.getInternalState(listener, "isBusy")).booleanValue();
    if (!isBusyValue) {
      assertTrue(false);
      return;
    }    
    
    Mockito.when
    (mockExecutor.readLifecycleCommandResponse()
        ).thenReturn(null);
    //try in between
    listener.listenforHcdCommand();
    result = (OperationResult)Whitebox.getInternalState(listener, "result");
    if (result != null) {
      assertTrue(false);
      return;
    }
    
    Whitebox.setInternalState(listener, "result", null);
    Mockito.when
    (mockExecutor.readLifecycleCommandResponse()
        ).thenReturn(new OperationResult("Ok", 0.0,
            StatusCodes.Success));
    listener.listenforHcdCommand();
    result = (OperationResult)Whitebox.getInternalState(listener, "result");
    if (result == null) {
      assertTrue(false);
      return;
    }
    
    if (result.getStatus() != StatusCodes.Success) {
      assertTrue(false);
      return;
    }
    
    isBusyValue = ((Boolean)Whitebox.getInternalState(listener, "isBusy")).booleanValue();
    if (isBusyValue) {
      assertTrue(false);
      return;
    }
    
    if (((Queue<Integer>)Whitebox.getInternalState(listener, "mcsCommandQueue")).size() 
        != 1) {
      assertTrue(false);
      return;
    }
    
    //Wait for the Simulator to change state to datumed
    final long FOLLOW_COMMAND_DELAY = 18000;
    
    Whitebox.setInternalState(listener, "result", null);
    try {
      Thread.sleep(FOLLOW_COMMAND_DELAY);
    } catch (InterruptedException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
      assertTrue(false);
      return;
    }        
    
    //Send follow command
    Mockito.when
    (mockExecutor.sendFollowCommand(Axes.BOTH)
        ).thenReturn
        (new OperationResult("Ok", 0.0, StatusCodes.Success));
    
    listener.listenforHcdCommand();
    result = (OperationResult)Whitebox.getInternalState(listener, "result");
    if (result == null) {
      assertTrue(false);
      return;
    }
    
    if (result.getStatus() != StatusCodes.Success) {
      assertTrue(false);
      return;
    }
    
    commandSent = 
        ((Integer)Whitebox
            .getInternalState(listener, "commandUnderExecution"))
            .intValue();
    if (commandSent != FOLLOW_COMMAND) {
      assertTrue(false);
      return;
    }
    
    isBusyValue = ((Boolean)Whitebox.getInternalState(listener, "isBusy")).booleanValue();
    if (!isBusyValue) {
      assertTrue(false);
      return;
    }
    
    Whitebox.setInternalState(listener, "result", null);
    //Receive response for follow command
    Mockito.when
    (mockExecutor.readFollowCommandResponse()
        ).thenReturn(new OperationResult("Ok", 0.0,
            StatusCodes.Success));
    listener.listenforHcdCommand();
    
    result = (OperationResult)Whitebox.getInternalState(listener, "result");
    if (result == null) {
      assertTrue(false);
      return;
    }
    
    if (result.getStatus() != StatusCodes.Success) {
      assertTrue(false);
      return;
    }
    
    isBusyValue 
    = ((Boolean)Whitebox
        .getInternalState(listener, "isBusy"))
        .booleanValue();
    if (isBusyValue) {
      assertTrue(false);
      return;
    }
    
    if (((Queue<Integer>)Whitebox
        .getInternalState(listener, "mcsCommandQueue"))
        .size() != 0) {
      assertTrue(false);
      return;
    }        
              
    assertTrue(true);        
    
    //cleanup
    listener.stop();

  }
  
  /**
   * Integration Test method for 
   * {@link com.tmt.tcs.mcs.mcsHcdPrototype.TcsCommandListener#listenforHcdCommand()}.
   * Retry after timeout
   */
  @Test
  public final void testListenforHcdCommandTimeoutRetry() {
    //Setup        
    CommandExecutor executor = PowerMockito.mock(CommandExecutor.class);
    PowerMockito.mockStatic(CommandExecutor.class);
    Mockito.when(CommandExecutor.getInstance()).thenReturn(executor);
    
    Queue<Integer> mcsCommandQueue = new LinkedList<Integer>();
    mcsCommandQueue.offer(LIFECYCLE_COMMAND);
    mcsCommandQueue.offer(FOLLOW_COMMAND);
    ResourceBundle strings = null;
    final String RESOURCE_FILE_NAME = "string.properties";
    try {
      FileInputStream fis = new FileInputStream(RESOURCE_FILE_NAME);
      try {
        strings = new PropertyResourceBundle(fis);
      } catch (IOException ex) {
        ex.printStackTrace();
        assertTrue(false);
      }
    } catch (FileNotFoundException e1) {
      e1.printStackTrace();
      assertTrue(false);
    }
        
    TcsCommandListener listener = TcsCommandListener.getInstance();
    
    HcdRuntimeController controller = PowerMockito.mock(HcdRuntimeController.class);
    //Mockito.doNothing((controller.setState(Mockito.anyBoolean()));
    Whitebox.setInternalState(listener, "controller", controller);    
    Whitebox.setInternalState(listener, "mcsCommandQueue", mcsCommandQueue);
    Whitebox.setInternalState(listener, "strings", strings);
    
    //Send lifecycle command
    Mockito.when
    (executor.sendLifecycleCommand
        (TransitionType.STARTUP)).thenReturn
        (new OperationResult("Ok", 0.0, StatusCodes.Success));
    
    listener.listenforHcdCommand();
    
    //Wait for timeout
    try {
      Thread.sleep(TIMEOUT);
    } catch (InterruptedException ex) {
      // TODO Auto-generated catch block
      ex.printStackTrace();
    }
    
    Mockito.when
    (executor.readLifecycleCommandResponse()
        ).thenReturn(null);
    
    //try to read the response.
    listener.listenforHcdCommand();
    OperationResult result = (OperationResult)Whitebox.getInternalState(listener, "result");
    if (result != null) {
      assertTrue(false);
      return;
    }
    
    boolean isBusyValue 
    = ((Boolean)Whitebox
        .getInternalState(listener, "isBusy"))
        .booleanValue();
    //Cleanup
    listener.stop();
    if (isBusyValue) {
      assertTrue(false);
      return;
    } else {
      assertTrue(true);
    }
        
  }
  
  /**
   * Test method for 
   * {@link com.tmt.tcs.mcs.mcsHcdPrototype.TcsCommandListener#getInstance()} 
   */
  @Test
  public final void testGetInstance() {
    TcsCommandListener instance1 = TcsCommandListener.getInstance();     
    TcsCommandListener instance2 = TcsCommandListener.getInstance();
    
    assertTrue(instance1.equals(instance2));
    
    instance1.stop();
  }
  
  /**
   * Test method for 
   * {@link com.tmt.tcs.mcs.mcsHcdPrototype.TcsCommandListener#setCallback()} 
   */
  @Test
  public final void testSetCallback() {
    TcsCommandListener instance = TcsCommandListener.getInstance();
    final Integer hashcode = 1;
    HcdRuntimeController mockController = PowerMockito.mock(HcdRuntimeController.class);
    Mockito.when(mockController.equals(mockController)).thenReturn(true);
    Mockito.when(mockController.hashCode()).thenReturn(hashcode);
    //HcdRuntimeController mockController = new HcdRuntimeController();
    instance.setCallback(mockController);
    HcdRuntimeController setController = (HcdRuntimeController)Whitebox
        .getInternalState(instance, "controller");    
    assertTrue((setController.equals(mockController)) 
        && (setController.hashCode() == mockController.hashCode()));
    //Cleanup
    instance.stop();
  }
  
  /**
   * Test method for 
   * {@link com.tmt.tcs.mcs.mcsHcdPrototype.TcsCommandListener#stop()} 
   */
  @Test
  public final void testStop() {
    TcsCommandListener instance = TcsCommandListener.getInstance();
    instance.stop();
    if (Whitebox.getInternalState(instance, "instance") == null) {
      assertTrue(true);
    }
  }
}
