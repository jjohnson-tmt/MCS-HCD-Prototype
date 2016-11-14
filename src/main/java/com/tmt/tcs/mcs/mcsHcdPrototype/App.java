package com.tmt.tcs.mcs.mcsHcdPrototype;

import java.util.Scanner;

/**
 * This is a simulation of MCS HCD. Entry point for the application. 
 * Controls the sequence of execution.
 */
public class App {
    
  /**
   * Entry point for the application.
   * @throws InterruptedException from the execution thread used by all HCD components to exit.
   */
  public static void main(String[] args) throws InterruptedException {
    HcdRuntimeController controllerObject = new HcdRuntimeController();
    Thread executionThread = new Thread(controllerObject);
    executionThread.start();
    
    Scanner inputScan = new Scanner(System.in);
    inputScan.nextInt();
    inputScan.close();    
    controllerObject.stop();
    executionThread.join();
  }

}