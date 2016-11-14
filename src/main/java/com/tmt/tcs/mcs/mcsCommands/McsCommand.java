package com.tmt.tcs.mcs.mcsCommands;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.google.protobuf.InvalidProtocolBufferException;
import com.tmt.tcs.mcs.protos.TcsToMcsCommandProtos.CommandResponse;

/**
 * Represents base class for all MCS commands.
 * @author E401581.
 *
 */
public abstract class McsCommand {
  
  /**
   * Command name.
   */
  private String commandName = null;
  
  /**
   * Name of config file.
   */
  private static final String CONFIG_FILE_NAME = "config.properties";
  
  /**
   * Generates byte packet as per the protocol.
   * @return byte array containing packet to be sent to MCS on ZeroMQ.
   */
  public abstract byte[] encode();
  
  /**
   * Properties loaded from config.properties.
   */
  private static Properties prop;
  
  static{
    prop = new Properties();
    //InputStream input = null;
    try ( InputStream input = new FileInputStream(CONFIG_FILE_NAME)){      
      // load a properties file
      prop.load(input);
    } catch (IOException ex) {
      ex.printStackTrace();
    }    
  }
  
  /**
   * Initializes new instance of McsCommand class.
   */
  public McsCommand(String commandKey) {
    // Read configuration
    commandName = prop.getProperty(commandKey);
  }
  
  /**
   * Parses the response packet.
   * @param responsePaket responsePacket received on ZeroMQ pull socket for decoding.
   */    
  public CommandResponse decode(byte[] responsePaket) {
    //Parse response
    if (responsePaket == null) {
      return null;
    }
    
    CommandResponse commandResponse = null;
    try {                      
      commandResponse = CommandResponse.parseFrom(responsePaket);        
    } catch (InvalidProtocolBufferException ex) {      
      ex.printStackTrace();      
    }
    
    return commandResponse;
  }
           
  /**
   * Returns name for the command.
   */  
  public String getCommandName() {
    return commandName;
  }
}
