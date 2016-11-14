package com.tmt.tcs.mcs.mcsCommands;

import com.google.protobuf.GeneratedMessage;

import com.tmt.tcs.mcs.protos.TcsToMcsCommandProtos.FollowCommand;
import com.tmt.tcs.mcs.protos.TcsToMcsCommandProtos.FollowCommand.Axes;

import java.util.Objects;

/**
 * {@link McsCommand} implementation for follow command.
 * @author E401581.  
 *
 */
public final class McsFollowCommand extends McsCommand {
  
  /**
   * Config key name to read follow command identifier.
   */
  private static final String FOLLOW_COMMAND_KEY_NAME = "followcommandname";
  
  /**
   * Singleton instance of Follow class.
   */
  private static McsFollowCommand instance;
  
  /**
   * {@link Axes} that needs to be put into following state.
   */
  private Axes axes;

  /**
   * Initializes new instance of McsFollowCommand class.
   */
  private McsFollowCommand() {
    super(FOLLOW_COMMAND_KEY_NAME);    
  }
  
  /**
   * Gets singleton instance of McsFollowCommand. 
   * @return object of type McsFollowCommand.
   */
  public static McsFollowCommand getInstance() {
    if (instance == null) {
      instance = new McsFollowCommand();
    }
    
    return instance;
  }
  
  /**
   * Sets the axis for which the follow command needs to be executed.
   * @param axes {@link Axes} to be put into following state.
   */
  public void setAxes(Axes axes) {
    this.axes = axes;
  }
  
  /**
   * Generates the byte packet for follow command as per the protocol.
   * @return byte array containing packet to be sent to MCS on ZeroMQ.
   */
  @Override
  public byte[] encode() {
    // Create command object
    byte[] result = null;
    GeneratedMessage command = FollowCommand.newBuilder()
        .setAxes(axes).build();
    
    if (/*Objects.nonNull(command)*/ command != null) {
      result = command.toByteArray();
    }
    
    return result;
  }
}