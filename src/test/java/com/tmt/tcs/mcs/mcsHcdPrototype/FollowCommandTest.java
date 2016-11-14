package com.tmt.tcs.mcs.mcsHcdPrototype;

import com.google.protobuf.DynamicMessage.Builder;

import com.tmt.tcs.mcs.mcsCommands.McsFollowCommand;

import com.tmt.tcs.mcs.protos.TcsToMcsCommandProtos.FollowCommand;
import com.tmt.tcs.mcs.protos.TcsToMcsCommandProtos.FollowCommand.Axes;

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
 *     Unit test for {@link FollowCommand} class.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({FollowCommand.class, Builder.class})
public class FollowCommandTest extends PowerMockTestCase {

  /**
   * @throws java.lang.Exception Initial setup needed by the test case.
   */
  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
  }

  /**
   * @throws java.lang.Exception Cleanup after the test case.
   */
  @AfterClass
  public static void tearDownAfterClass() throws Exception {
  }

  /**
   * @throws java.lang.Exception Initial setup needed by the test case.
   */
  @Before
  public void setUp() throws Exception {
  }

  /**
   * @throws java.lang.Exception Cleanup after the test case.
   */
  @After
  public void tearDown() throws Exception {
  }

  /**
   * Test method for {@link com.tmt.tcs.mcs.mcsCommands.McsFollowCommand#encode()}.
   */
  @Test
  public final void testEncode() {
    FollowCommand mockFollow = PowerMockito.mock(FollowCommand.class);
    com.tmt.tcs.mcs.protos.TcsToMcsCommandProtos.FollowCommand.Builder mockBuilder = PowerMockito.mock(com.tmt.tcs.mcs.protos.TcsToMcsCommandProtos.FollowCommand.Builder.class);
    final byte[] mockedBytes = new byte[] {10, 12};
    
    PowerMockito.mockStatic(FollowCommand.class);
    Mockito.when(FollowCommand.newBuilder()).thenReturn(mockBuilder);
    Mockito.when(mockBuilder.setAxes(Axes.BOTH)).thenReturn(mockBuilder);
    Mockito.when(mockBuilder.build()).thenReturn(mockFollow);
    Mockito.when(mockFollow.toByteArray()).thenReturn(mockedBytes);
    
    McsFollowCommand instance = McsFollowCommand.getInstance();    
    Axes mockedAxes = Axes.BOTH;
    Whitebox.setInternalState(instance, "axes", mockedAxes);    
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
   * Test method for {@link com.tmt.tcs.mcs.mcsCommands.McsFollowCommand#setAxes()}.
   */
  @Test
  public final void testSetAxes() {
    McsFollowCommand instance = McsFollowCommand.getInstance();    
    Axes mockedAxes = Axes.BOTH;
    instance.setAxes(mockedAxes);
    assertTrue(((Axes)(Whitebox.getInternalState(instance, "axes"))) == mockedAxes);
  }

  /**
   * Test method for {@link com.tmt.tcs.mcs.mcsCommands.McsFollowCommand#getInstance()}.
   */
  @Test
  public final void testGetInstance() {
    McsFollowCommand instance1 = McsFollowCommand.getInstance(); 
    McsFollowCommand instance2 = McsFollowCommand.getInstance();
    
    assertTrue(instance1.equals(instance2));
  }

}
