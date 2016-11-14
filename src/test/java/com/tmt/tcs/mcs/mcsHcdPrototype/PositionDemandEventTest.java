package com.tmt.tcs.mcs.mcsHcdPrototype;

import com.google.protobuf.DynamicMessage.Builder;

import com.tmt.tcs.mcs.protos.TcsMcsEventsProtos.TcsPositionDemandEvent;
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

import static org.junit.Assert.assertTrue;

/**
 * @author E401581.
 *     Unit test for {@link PositionDemandEvent} class.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({TcsPositionDemandEvent.class, Builder.class})
public class PositionDemandEventTest extends PowerMockTestCase {

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
   * @throws java.lang.Exception Exceptioon during cleanup.
   */
  @After
  public void tearDown() throws Exception {
  }

  /**
   * Test method for {@link com.tmt.tcs.mcs.tcsToMcsEvents.PositionDemandEvent#encode()}.
   */
  @Test
  public final void testEncode() {
    TcsPositionDemandEvent mockEvent = PowerMockito.mock(TcsPositionDemandEvent.class);
    com.tmt.tcs.mcs.protos.TcsMcsEventsProtos.TcsPositionDemandEvent.Builder mockBuilder = PowerMockito.mock(com.tmt.tcs.mcs.protos.TcsMcsEventsProtos.TcsPositionDemandEvent.Builder.class);
    final byte[] mockedBytes = new byte[] {10, 12};
    
    final double az = 30.0;
    final double el = 45.0;
    final double time = 90.0;
    
    PowerMockito.mockStatic(TcsPositionDemandEvent.class);
    Mockito.when(TcsPositionDemandEvent.newBuilder()).thenReturn(mockBuilder);
    Mockito.when(mockBuilder.setAzimuth(az)).thenReturn(mockBuilder);
    Mockito.when(mockBuilder.setElevation(el)).thenReturn(mockBuilder);
    Mockito.when(mockBuilder.setTime(time)).thenReturn(mockBuilder);
    Mockito.when(mockBuilder.build()).thenReturn(mockEvent);
    Mockito.when(mockEvent.toByteArray()).thenReturn(mockedBytes);
    
    PositionDemandEvent instance = PositionDemandEvent.getInstance();    
    Whitebox.setInternalState(instance, "az", az);    
    Whitebox.setInternalState(instance, "el", el);    
    Whitebox.setInternalState(instance, "time", time);    
    
    byte[] packet = instance.encode(); 
    if (packet == null) {
      assertTrue(false);
      return;
    } else if (packet.length != 2) {
      assertTrue(false);
      return;
    } else if ((packet[0] == 10) && (packet[1] == 12)) {
       assertTrue(true);
    }        
  }

  /**
   * Test method for {@link com.tmt.tcs.mcs.tcsToMcsEvents.PositionDemandEvent#getInstance()}.
   */
  @Test
  public final void testGetInstance() {
    PositionDemandEvent instance1 = PositionDemandEvent.getInstance(); 
    PositionDemandEvent instance2 = PositionDemandEvent.getInstance();
    
    assertTrue(instance1.equals(instance2));
  }
  
  /**
   * Test method for {@link com.tmt.tcs.mcs.tcsToMcsEvents.PositionDemandEvent#setAzimuth()}.
   */
  @Test
  public final void testSetAzimuth() {
    PositionDemandEvent instance = PositionDemandEvent.getInstance();
    final double az = 100.0;
    instance.setAzimuth(az);    
    assertTrue(((Double)Whitebox.getInternalState(instance, "az")).doubleValue() == az);
  }
  
  /**
   * Test method for {@link com.tmt.tcs.mcs.tcsToMcsEvents.PositionDemandEvent#setElevation()}.
   */
  @Test
  public final void testSetElevation() {
    PositionDemandEvent instance = PositionDemandEvent.getInstance();
    final double el = 100.0;
    instance.setElevation(el);    
    assertTrue(((Double)Whitebox.getInternalState(instance, "el")).doubleValue() == el);
  }
  
  /**
   * Test method for {@link com.tmt.tcs.mcs.tcsToMcsEvents.PositionDemandEvent#setTime()}.
   */
  @Test
  public final void testSetTime() {
    PositionDemandEvent instance = PositionDemandEvent.getInstance();
    final double time = 100.0;
    instance.setTime(time);
    assertTrue(((Double)Whitebox.getInternalState(instance, "time")).doubleValue() == time);
  }

}
