package com.tmt.tcs.mcs.mcsHcdPrototype;

import com.google.protobuf.InvalidProtocolBufferException;

import com.tmt.tcs.mcs.mcsCommands.McsCommand;
import com.tmt.tcs.mcs.mcsCommands.McsFollowCommand;
import com.tmt.tcs.mcs.mcsCommands.McsLifecycleCommand;

import com.tmt.tcs.mcs.protos.TcsToMcsCommandProtos.CommandResponse;

import static org.junit.Assert.*;

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
 * Unit test for testing {@link McsCommand} class.
 * @author E401581
 *
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(CommandResponse.class)
public class McsCommandTest extends PowerMockTestCase {

  /**
   * @throws java.lang.Exception
   */
  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
  }

  /**
   * @throws java.lang.Exception
   */
  @AfterClass
  public static void tearDownAfterClass() throws Exception {
  }

  /**
   * @throws java.lang.Exception
   */
  @Before
  public void setUp() throws Exception {
  }

  /**
   * @throws java.lang.Exception
   */
  @After
  public void tearDown() throws Exception {
  }

  /**
   * Test method for {@link com.tmt.tcs.mcs.mcsCommands.McsCommand#Mcscommand(java.lang.String)}.
   */
  @Test
  public final void testMcscommand() {
    McsLifecycleCommand instance = McsLifecycleCommand.getInstance();
    if (Whitebox.getInternalState(instance, "commandName") == null) {
      assertTrue(false);
      return;
    }
    
    McsFollowCommand instanceNew = McsFollowCommand.getInstance();
    if (Whitebox.getInternalState(instanceNew, "commandName") == null) {
      assertTrue(false);
      return;
    }
    
    assertTrue(true);    
  }

  /**
   * Test method for {@link com.tmt.tcs.mcs.mcsCommands.McsCommand#decode(byte[])}.
   * @throws InvalidProtocolBufferException To supress error for mocking CommandResponse.parseFrom.
   */
  @Test
  public final void testDecode() throws InvalidProtocolBufferException {
    McsLifecycleCommand instance = McsLifecycleCommand.getInstance();
    final byte[] packet =  new byte[] {10, 12};
    CommandResponse response = null;
    CommandResponse mockedResponse = PowerMockito.mock(CommandResponse.class);    
    
    PowerMockito.mockStatic(CommandResponse.class);
    Mockito.when(CommandResponse.parseFrom(packet)).thenReturn(mockedResponse);
    
    response = instance.decode(null);
    if (response != null) {
      assertTrue(false);
      return;
    }
        
    response = instance.decode(packet);
    if (response != null) {
      if ((response.equals(mockedResponse)) 
          && (response.hashCode() 
              == mockedResponse.hashCode())) {
        assertTrue(true);        
      } else {
        assertTrue(false);
      }
      
    } else {
      assertTrue(false);
    }
  }
  
  /**
   * Test method for {@link com.tmt.tcs.mcs.mcsCommands.McsCommand#getCommandName(byte[])}.
   */
  @Test
  public final void testGetCommandName() {
    final String commandName = "tcs_mcs_Lifecycle";
    McsLifecycleCommand instance = McsLifecycleCommand.getInstance();
    Whitebox.setInternalState(instance, "commandName", commandName);
    assertTrue(instance.getCommandName().compareTo(commandName) == 0);
  }

}
