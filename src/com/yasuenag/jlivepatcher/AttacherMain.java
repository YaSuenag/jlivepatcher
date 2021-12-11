/*
 * Copyright (C) 2021 Yasumasa Suenaga
 *
 * This file is part of JLivePatcher
 *
 * JLivePatcher is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * JLivePatcher is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with ThreadDumper.  If not, see <http://www.gnu.org/licenses/>.
*/
package com.yasuenag.jlivepatcher;

import java.io.*;
import java.net.*;
import java.lang.reflect.*;
import com.sun.tools.attach.*;


public class AttacherMain{

  public static final String TARGET_JAR_NAME = "jlivepatcher.jar";

  private static void setupClassLoader()
                   throws FileNotFoundException, NoSuchMethodException,
                              MalformedURLException, IllegalAccessException,
                                                     InvocationTargetException{
    File toolsJar = new File(System.getProperty("java.home") + File.separator +
                             ".." + File.separator + "lib" + File.separator +
                             "tools.jar");
    if(!toolsJar.exists()){
      throw new FileNotFoundException("tools.jar does not exist.");
    }

    Method addURLMethod = URLClassLoader.class.getDeclaredMethod(
                                                         "addURL", URL.class);
    addURLMethod.setAccessible(true);
    addURLMethod.invoke(AttacherMain.class.getClassLoader(),
                                                     toolsJar.toURI().toURL());
  }


  public static void main(String[] args) throws Exception{

    if(args.length != 2){
      System.err.println("Usage:");
      System.err.println("  java -jar jlivepatcher.jar <PID> <properties>");
    }

    String pid = args[0];
    String propFile = args[1];
    File jarFile = null;

    for(String cp : System.getProperty("java.class.path")
                                             .split(File.pathSeparator)){

      if(cp.endsWith(TARGET_JAR_NAME)){
        jarFile = new File(cp);
        break;
      }

    }

    if(jarFile == null){
      throw new FileNotFoundException(
                            TARGET_JAR_NAME + " does not exist in classpath.");
    }

    setupClassLoader();

    VirtualMachine targetVM = null;
    try{

      if((targetVM = VirtualMachine.attach(pid)) == null){
        throw new IllegalStateException("Could not attach to " + pid);
      }

      targetVM.loadAgent(jarFile.getCanonicalPath(), propFile);
    }
    finally{

      if(targetVM != null){
        targetVM.detach();
      }

    }

  }

}

