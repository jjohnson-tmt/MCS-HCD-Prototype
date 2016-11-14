package com.tmt.tcs.mcs.mcsHcdPrototype;

import com.tmt.tcs.mcs.mcsHcdPrototype.McsEventListeningProvider;
import com.tmt.tcs.mcs.mcsToTcsEvents.CurrentPositionEvent;
import com.tmt.tcs.mcs.mcsToTcsEvents.McsEvent;
import com.tmt.tcs.mcs.protos.TcsMcsEventsProtos.McsCurrentPositionEvent;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import org.mockito.internal.util.reflection.Whitebox;

import org.zeromq.ZMQ;

import static org.junit.Assert.*;

/**
 * @author E401581.
 *     Unit test for {@link McsEventListeningProvider} class.
 */
public class McsEventListeningProviderTest {
      
  /**
   * Address string for pub socket.
   */
  private static final String PUB_ADDRESS = "tcp://localhost:5553";
  
  /**
   * Address string for sub socket.
   */
  private static final String SUB_ADDRESS = "tcp://localhost:5553";
  
  /**
   * Name of current position event.
   */
  private static final String CURRENT_POSITION_EVENT_NAME = "mcs.currentposition";
  
  /**
   * Event other than current position event.
   */
  private static final String UNKNOWN_EVENT = "unknownevent";
   
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
   * @throws java.lang.Exception Exception during setup.
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
   * {@link com.tmt.tcs.mcs.mcsHcdPrototype.McsEventListeningProvider#init()}.
   */
  @Test
  public final void testInit() {
    McsEventListeningProvider listener = McsEventListeningProvider.getInstance(); 
    ZMQ.Context context = ZMQ.context(1);
    //Set the context prerequisite for init
    Whitebox.setInternalState(listener, "context", context);    
    listener.init();
    if (Whitebox.getInternalState(listener, "logFileName") == null) {
      assertTrue(false);
      return;
    }
    if (Whitebox.getInternalState(listener, "subscriber") == null) {
      assertTrue(false);
      return;
    }    
    
    String subAddress = (String)Whitebox.getInternalState(listener, "subAddress");
    if (subAddress.compareTo(SUB_ADDRESS) != 0) {
      assertTrue(false);
      return;
    }
    String eventName = (String)Whitebox.getInternalState(listener, "currentPositionEventName");
    if (eventName.compareTo(CURRENT_POSITION_EVENT_NAME) != 0) {
      assertTrue(false);
      return;
    }
        
    assertTrue(true);
    
    //Cleanup
    listener.start();
    listener.stop();
    
    context.term();
  }
    
  /**
   * Test method for 
   * {@link com.tmt.tcs.mcs.mcsHcdPrototype.McsEventListeningProvider#stop()}.
   */
  @Test
  public final void testStop() {    
    
    McsEventListeningProvider listener = McsEventListeningProvider.getInstance();
    listener.stop();
    if ((Whitebox.getInternalState(listener, "instance") == null) 
        && (((Boolean)Whitebox.getInternalState(listener, "isCleaned")).booleanValue() == true)) {
      assertTrue(true);      
    }
        
  }
  
  /**
   * Test method for 
   * {@link com.tmt.tcs.mcs.mcsHcdPrototype.McsEventListeningProvider#isCleanedUp()}.
   */
  @Test
  public final void testCleanup() {        
    McsEventListeningProvider listener = McsEventListeningProvider.getInstance();
    Whitebox.setInternalState(listener, "isCleaned", true);
    if (!listener.isCleanedUp()) {
      assertTrue(false);
      return;
    }
    
    Whitebox.setInternalState(listener, "isCleaned", false);
    if (listener.isCleanedUp()) {
      assertTrue(false);
      return;
    }
    
    assertTrue(true);
  }

  /**
   * Integration Test method for 
   * {@link com.tmt.tcs.mcs.mcsHcdPrototype.McsEventListeningProvider#listenforMcsEvent()} 
   * when no events to read.
   */
  @Test
  public final void testListenforNoEventtoread() {
    McsEventListeningProvider listener = McsEventListeningProvider.getInstance();
    ZMQ.Context context = ZMQ.context(1);
    listener.setContext(context);
    listener.init();
    listener.start();
    
    if (listener.listenforMcsEvent() == null) {
      assertTrue(true);      
    }
    
    listener.stop();
    context.term();
  }
  
  /**
   * Integration Test method for 
   * {@link com.tmt.tcs.mcs.mcsHcdPrototype.McsEventListeningProvider#listenforMcsEvent()} 
   * when current position event to read.
   */
  @Test
  public final void testListenfoCurrentPositionEvent() {
    McsEventListeningProvider listener = McsEventListeningProvider.getInstance();
    ZMQ.Context context = ZMQ.context(1);
    ZMQ.Socket publisher = context.socket(ZMQ.PUB);    
    if (publisher != null) {
      publisher.bind(PUB_ADDRESS);
    }
    
    listener.setContext(context);    
    listener.init();
    listener.start();
    //Send current position event like stream.
    McsCurrentPositionEvent currentpositionEvent = McsCurrentPositionEvent
        .newBuilder()
        .setAzInPosition(true).setAzPos(0.0).setAzPosDemand(0.0).setAzPosDmdErrCount(0)
        .setAzPosError(0.0).setElInPosition(true).setElPos(0.0).setElPosDemand(0.0)
        .setElPosDmdErrCount(0).setElPosError(0.0).setEncodeLatchingTime(0.0)
        .setMcsInPosition(true).setTime(0.0).build();
    
    boolean status  = true;
    for (int count = 1; count <= 100; count++) {
      if (status) {
        status = publisher.sendMore(CURRENT_POSITION_EVENT_NAME);
        if (status) {
          status = publisher.send(currentpositionEvent.toByteArray());
        }
      }
    }
    
    if (!status) {
      listener.stop();      
      if (publisher != null) {
        publisher.unbind(PUB_ADDRESS);
        publisher.close();
      }      
      context.term();
      
      assertTrue(false);
      return;
    }
    
    
    try {
      //Wait for zeromq buffering and delay
      Thread.sleep(20000);
    } catch (InterruptedException ex) {
      // TODO Auto-generated catch block
      ex.printStackTrace();
    }
        
    //try at least 100 times
    McsEvent evt = null;
    int count = 0;
    do {
      evt = listener.listenforMcsEvent();
      count++;
    } while ((count < 100) && (evt == null));
                
    if (evt == null) {
      listener.stop();
          
      if (publisher != null) {
        publisher.unbind(PUB_ADDRESS);
        publisher.close();
      }
          
      context.term();
      assertTrue(false);
      return;
    } else if (CurrentPositionEvent.class.isInstance(evt)) {
      assertTrue(true);
    }              
              
    
    listener.stop();
    
    if (publisher != null) {
      publisher.unbind(PUB_ADDRESS);
      publisher.close();
    }
    
    context.term();
  }
  
  /**
   * Integration Test method for 
   * {@link com.tmt.tcs.mcs.mcsHcdPrototype.McsEventListeningProvider#listenforMcsEvent()} 
   * when there are events other than current position to read.
   */
  @Test
  public final void testListenfoOtherEvent() {
    McsEventListeningProvider listener = McsEventListeningProvider.getInstance();
    ZMQ.Context context = ZMQ.context(1);
    listener.setContext(context);
    listener.init();
    listener.start();    
    
    byte[] packet = new byte[2];
    packet[0] = 10;
    packet[1] = 25;
    
    ZMQ.Socket publisher = context.socket(ZMQ.PUB);    
    publisher.bind(PUB_ADDRESS);          
    
    publisher.sendMore(UNKNOWN_EVENT);
    publisher.send(packet);
    
    McsEvent evt = listener.listenforMcsEvent();
    if (evt == null) {
      assertTrue(true);
    }    
    
    listener.stop();
    if (publisher != null) {
      publisher.unbind(PUB_ADDRESS);
      publisher.close();
    }
    context.term();
  }
    
  /**
   * Test method for 
   * {@link com.tmt.tcs.mcs.mcsHcdPrototype.McsEventListeningProvider#getInstance()} 
   */
  @Test
  public final void testGetInstance() {
    McsEventListeningProvider instance1 = McsEventListeningProvider.getInstance(); 
    McsEventListeningProvider instance2 = McsEventListeningProvider.getInstance();
    
    assertTrue(instance1.equals(instance2));
    //Cleanup
    instance1.stop();
  }
  
  /**
   * Test method for 
   * {@link com.tmt.tcs.mcs.mcsHcdPrototype.McsEventListeningProvider#setContext()} 
   */
  @Test
  public final void testSetContext() {
    McsEventListeningProvider listener = McsEventListeningProvider.getInstance();
    ZMQ.Context context = ZMQ.context(1);
    
    listener.setContext(context);
    ZMQ.Context setContext = (ZMQ.Context)Whitebox.getInternalState(listener, "context");
    if ((setContext.equals(context)) && (setContext.hashCode() == context.hashCode())) {
      assertTrue(true);
    }
    
    context.term();
  }
  
}
