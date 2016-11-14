package com.tmt.tcs.mcs.mcsCommands;

import com.google.protobuf.GeneratedMessage;

import com.tmt.tcs.mcs.protos.TcsToMcsCommandProtos.LifeCycleCommand;
import com.tmt.tcs.mcs.protos.TcsToMcsCommandProtos.TransitionType;

import java.util.Objects;

/**
 * {@link McsCommand} implementation for lifecycle command.
 * @author E401581
 */
public final class McsLifecycleCommand extends McsCommand {
       
  /**
   * Config key name to read lifecycle command identifier.
   */
  private static final String LIFECYCLE_COMMAND_NAME_KEY = "lifecyclecommandname";
      
  /**
   * {@link TransitionType} for the lifecycle command.
   */
  private TransitionType transition;
  
  /**
   * Singleton instance of McsLifecycleCommand class.
   */
  private static McsLifecycleCommand instance;
       
  /**
   * Initializes new instance of McsLifecycleCommand class.
   */
  private McsLifecycleCommand() {          
    super(LIFECYCLE_COMMAND_NAME_KEY);    
  } 
  
  /**
   * Gets singleton instance of McsLifecycleCommand. 
   * @return object of type McsLifecycleCommand.
   */
  public static McsLifecycleCommand getInstance() {
    if (instance == null) {
      instance = new McsLifecycleCommand();
    }
    
    return instance;
  }
  
  /**
   * Sets the transition needed
   * @param transition {@link TransitionType}.
   */
  public void setTransition(TransitionType transition) {
    this.transition = transition;
  }
  
  /**
   * encodes the command to byte packet as per the protocol.
   * @return byte array containing packet to be sent to MCS on ZeroMQ.
   */
  @Override
  public byte[] encode() {
    byte[] packet = null;
    // Create command object
    GeneratedMessage command = LifeCycleCommand.newBuilder()
        .setTransitionType(transition).build();
    
    if (/*Objects.nonNull(command)*/ command != null) {
      packet = command.toByteArray();
    }
    
    return packet;
  }
 
}