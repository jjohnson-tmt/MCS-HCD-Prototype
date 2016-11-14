package com.tmt.tcs.mcs.mcsHcdPrototype;

import com.tmt.tcs.mcs.mcsHcdPrototype.CommandExecutor;
import com.tmt.tcs.mcs.mcsHcdPrototype.TcsEventListener;

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

/**
 * @author E401581.
 *     Unit test for {@link TcsEventListener} class.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(CommandExecutor.class)
public class TcsEventListenerTest extends PowerMockTestCase {
      
  /**
   * Position demand event name.
   */
  private static final String POSITION_DEMAND_EVENT_NAME = "tcs_mcs_PositionDemand";
  
  /**
   * Address string for sub socket.
   */
  private static final String SUB_ADDRESS = "tcp://localhost:5552";
  
  /**
   * number of events to complete 1 minute.
   */
  private static final int MAX_ITERATIONS = 6000;
  
  /**
   * Increment for the az and el demand.
   */
  private static final double DEMAND_INCREMENT = 5.00;
  
  /**
   * Maximum az, el demand value.
   */
  private static final double MAX_DEMAND = 60.0;

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
   * Integration Test method for 
   * {@link com.tmt.tcs.mcs.mcsHcdPrototype.TcsEventListener#listenforEvents()}
   * for testing increment of demand.
   */
  @Test
  public final void testListenforDemandincrement() {
    CommandExecutor mockExecutor = PowerMockito.mock(CommandExecutor.class);
    PowerMockito.mockStatic(CommandExecutor.class);
    Mockito.when(CommandExecutor.getInstance()).thenReturn(mockExecutor);
    Mockito.when(mockExecutor.sendPositionDemands(Mockito.anyDouble(), Mockito.anyDouble(), Mockito.anyDouble())).thenReturn(true);
    
    TcsEventListener listener = TcsEventListener.getInstance();
    
    double az = ((Double)Whitebox.getInternalState(listener, "az")).doubleValue();
    double el = ((Double)Whitebox.getInternalState(listener, "el")).doubleValue();        
    
    for (int count = 1; count <= MAX_ITERATIONS - 2; count++) {
      listener.listenforEvents();            
    }                
    
    listener.listenforEvents();
    //Check if demand is incremented by 1
    double newAz = ((Double)Whitebox.getInternalState(listener, "az")).doubleValue();
    double newEl = ((Double)Whitebox.getInternalState(listener, "el")).doubleValue();
    
    if (((newAz - az) == DEMAND_INCREMENT) && ((newEl - el) == DEMAND_INCREMENT)) {
      assertTrue(true);
    } else {
      assertTrue(false);
    }        
    
    //Cleanup
    listener.stop();
  }
  
  /**
   * Integration Test method for 
   * {@link com.tmt.tcs.mcs.mcsHcdPrototype.TcsEventListener#listenforEvents()}
   * for testing increment of demand for 2 cycles.
   */
  @Test
  public final void testListenforDemandincrementTwoCycles() {  
    CommandExecutor mockExecutor = PowerMockito.mock(CommandExecutor.class);
    PowerMockito.mockStatic(CommandExecutor.class);
    Mockito.when(CommandExecutor.getInstance()).thenReturn(mockExecutor);
    Mockito.when(mockExecutor.sendPositionDemands(Mockito.anyDouble(), Mockito.anyDouble(), Mockito.anyDouble())).thenReturn(true);
    
    TcsEventListener listener = TcsEventListener.getInstance();    
    
    double az =  ((Double)Whitebox.getInternalState(listener, "az")).doubleValue();
    double el = ((Double)Whitebox.getInternalState(listener, "el")).doubleValue();
    
    for (int count = 1; count <= MAX_ITERATIONS - 2; count++) {
      listener.listenforEvents();            
    }                
    
    listener.listenforEvents();
    //Check if demand is incremented
    double newAz = ((Double)Whitebox.getInternalState(listener, "az")).doubleValue();
    double newEl = ((Double)Whitebox.getInternalState(listener, "el")).doubleValue();
    
    if (((newAz - az) == DEMAND_INCREMENT) && ((newEl - el) == DEMAND_INCREMENT)) {
      for (int count = 1; count <= MAX_ITERATIONS - 2; count++) {
        listener.listenforEvents();            
      }                
      
      listener.listenforEvents();
      //Check if demand is incremented
      double newAz1 = ((Double)Whitebox.getInternalState(listener, "az")).doubleValue();
      double newEl1 = ((Double)Whitebox.getInternalState(listener, "el")).doubleValue();
      if (((newAz1 - newAz) == DEMAND_INCREMENT) && ((newEl1 - newEl) == DEMAND_INCREMENT)) {
        assertTrue(true);                
      } else {
        assertTrue(false);
      }
            
    } else {
      assertTrue(false);
    }
    
    //Cleanup
    listener.stop();
        
  }
  
  /**
   * Integration Test method for 
   * {@link com.tmt.tcs.mcs.mcsHcdPrototype.TcsEventListener#listenforEvents()}
   * to check demand remains within limit.
   */
  @Test
  public final void testListenforDemandLimit() {
    CommandExecutor mockExecutor = PowerMockito.mock(CommandExecutor.class);
    PowerMockito.mockStatic(CommandExecutor.class);
    Mockito.when(CommandExecutor.getInstance()).thenReturn(mockExecutor);
    Mockito.when(mockExecutor.sendPositionDemands(Mockito.anyDouble(), Mockito.anyDouble(), Mockito.anyDouble())).thenReturn(true);
    
    TcsEventListener listener = TcsEventListener.getInstance();    
    
    final double az =  ((Double)Whitebox.getInternalState(listener, "az")).doubleValue();
    final double el = ((Double)Whitebox.getInternalState(listener, "el")).doubleValue();
    
    do {
      listener.listenforEvents();
    } while (!((((Double)Whitebox.getInternalState(listener, "az")).doubleValue() == MAX_DEMAND) && (((Double)Whitebox.getInternalState(listener, "el")).doubleValue() == MAX_DEMAND)));
    
    for (int count = 1; count <= MAX_ITERATIONS - 2; count++) {
      listener.listenforEvents();            
    }
    
    listener.listenforEvents();
    
    if ((((Double)Whitebox.getInternalState(listener, "az")).doubleValue() == az) && (((Double)Whitebox.getInternalState(listener, "el")).doubleValue() == el)) {
      assertTrue(true);
    } else {
      assertTrue(false);
    }
    
    //Cleanup
    listener.stop();
        
  }
  
  /**
   * Test method for 
   * {@link com.tmt.tcs.mcs.mcsHcdPrototype.TcsEventListener#getAz()} 
   */
  @Test
  public final void testGetAz() {
    TcsEventListener instance = TcsEventListener.getInstance(); 
    double testAz = 10.0;
    Whitebox.setInternalState(instance, "az", testAz);    
    assertTrue(instance.getAz() == testAz);
  }
  
  /**
   * Test method for 
   * {@link com.tmt.tcs.mcs.mcsHcdPrototype.TcsEventListener#getEl()} 
   */
  @Test
  public final void testGetEl() {
    TcsEventListener instance = TcsEventListener.getInstance(); 
    double testEl = 10.0;
    Whitebox.setInternalState(instance, "el", testEl);    
    assertTrue(instance.getEl() == testEl);
  }
  
  /**
   * Test method for 
   * {@link com.tmt.tcs.mcs.mcsHcdPrototype.TcsEventListener#getInstance()} 
   */
  @Test
  public final void testGetInstance() {
    TcsEventListener instance1 = TcsEventListener.getInstance(); 
    TcsEventListener instance2 = TcsEventListener.getInstance();
    
    assertTrue(instance1.equals(instance2));
    
    instance1.stop();
  }
  
  /**
   * Test method for 
   * {@link com.tmt.tcs.mcs.mcsHcdPrototype.TcsEventListener#init()}.
   */
  @Test
  public final void testInit() {
    TcsEventListener listener = TcsEventListener.getInstance(); 
    listener.init();
    if (Whitebox.getInternalState(listener, "logFileName") == null) {
      assertTrue(false);
      return;
    }            
    assertTrue(true);
    
    //Cleanup
    listener.start();
    listener.stop();        
  }
  
  /**
   * Test method for 
   * {@link com.tmt.tcs.mcs.mcsHcdPrototype.TcsEventListener#stop()}.
   */
  @Test
  public final void testStop() {    
    
    TcsEventListener listener = TcsEventListener.getInstance();
    listener.stop();
    if (Whitebox.getInternalState(listener, "instance") == null) {
      assertTrue(true);      
    }
        
  }

}
