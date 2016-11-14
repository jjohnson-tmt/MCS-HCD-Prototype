package com.tmt.tcs.mcs.mcsHcdPrototype;

import com.google.protobuf.DynamicMessage.Builder;

import com.tmt.tcs.mcs.mcsCommands.McsLifecycleCommand;

import com.tmt.tcs.mcs.protos.TcsToMcsCommandProtos.LifeCycleCommand;
import com.tmt.tcs.mcs.protos.TcsToMcsCommandProtos.TransitionType;

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
 *     Unit test for {@link LifeCycleCommand} class.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({LifeCycleCommand.class, Builder.class})
public class LifecycleCommandTest extends PowerMockTestCase {

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
   * Test method for {@link com.tmt.tcs.mcs.mcsCommands.McsLifecycleCommand#encode()}.
   */
  @Test
  public final void testEncode() {
    LifeCycleCommand mockLifecycle = PowerMockito.mock(LifeCycleCommand.class);
    com.tmt.tcs.mcs.protos.TcsToMcsCommandProtos.LifeCycleCommand.Builder mockBuilder = PowerMockito.mock(com.tmt.tcs.mcs.protos.TcsToMcsCommandProtos.LifeCycleCommand.Builder.class);
    final byte[] mockedBytes = new byte[] {10, 12};
    
    PowerMockito.mockStatic(LifeCycleCommand.class);
    Mockito.when(LifeCycleCommand.newBuilder()).thenReturn(mockBuilder);
    Mockito.when(mockBuilder.setTransitionType(TransitionType.STARTUP)).thenReturn(mockBuilder);
    Mockito.when(mockBuilder.build()).thenReturn(mockLifecycle);
    Mockito.when(mockLifecycle.toByteArray()).thenReturn(mockedBytes);
    
    McsLifecycleCommand instance = McsLifecycleCommand.getInstance();    
    TransitionType mockedTransition = TransitionType.STARTUP;
    Whitebox.setInternalState(instance, "transition", mockedTransition);    
    byte[] packet = instance.encode();
    if (packet == null) {
      assertTrue(false);
      return;
    } else if (packet.length != 2){
      assertTrue(false);
      return;
    } else {
      if ((packet[0] == 10) && (packet[1] == 12)) {
        assertTrue(true);
      }
    }        
  }

  /**
   * Test method for {@link com.tmt.tcs.mcs.mcsCommands.McsLifecycleCommand#getInstance()}.
   */
  @Test
  public final void testGetInstance() {
    McsLifecycleCommand instance1 = McsLifecycleCommand.getInstance(); 
    McsLifecycleCommand instance2 = McsLifecycleCommand.getInstance();
    
    assertTrue(instance1.equals(instance2));
  }  
  
  /**
   * Test method for {@link com.tmt.tcs.mcs.mcsCommands.McsLifecycleCommand#setTransition()}.
   */
  @Test
  public final void testSetTransition() {
    McsLifecycleCommand instance = McsLifecycleCommand.getInstance();    
    TransitionType mockedTransition = TransitionType.STARTUP;
    instance.setTransition(mockedTransition);
    assertTrue((
        (TransitionType)(Whitebox.getInternalState(instance, "transition")))
        == mockedTransition);
  }

}
