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

import java.lang.instrument.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.*;
import java.security.*;


public class JLivePatcher{

  private static void closeStream(Closeable obj){

    try{

      if(obj != null){
        obj.close();
      }

    }
    catch(IOException e){
      e.printStackTrace();
    }

  }

  public static void agentmain(String agentArgs, Instrumentation inst){
    premain(agentArgs, inst);
  }

  public static void premain(String agentArgs, Instrumentation inst){
    Properties prop = new Properties();
    FileInputStream propFile = null;
    ConcurrentHashMap<String, byte[]> classMap =
                                        new ConcurrentHashMap<String, byte[]>();

    try{
      propFile = new FileInputStream(agentArgs);
      prop.load(propFile);

      for(Map.Entry entry : prop.entrySet()){
        File classFile = new File((String)entry.getValue());
        byte[] classBytes = new byte[(int)classFile.length()];
        FileInputStream classFileStream = null;

        try{
          classFileStream = new FileInputStream(classFile);
          classFileStream.read(classBytes);
          String className = ((String)entry.getKey()).replace('.', '/');
          classMap.put(className, classBytes);
        }
        catch(IOException e){
          System.err.println("Exception occurred in " + (String)entry.getKey());
          e.printStackTrace();
        }
        finally{
          closeStream(classFileStream);
        }

      }

    }
    catch(IOException e){
      e.printStackTrace();
    }
    finally{
      closeStream(propFile);
    }

    JPatchTransformer transformer = new JPatchTransformer(classMap);
    inst.addTransformer(transformer, true);

    /* Retransform for loaded classes. */
    List<Class> targetClasses = new ArrayList<Class>();
    for(Class cls : inst.getAllLoadedClasses()){
      String className = cls.getName().replace('.', '/');

      if(classMap.containsKey(className)){
        targetClasses.add(cls);
      }

    }

    if(!targetClasses.isEmpty()){
      try{
        inst.retransformClasses(targetClasses.toArray(new Class[0]));
      }
      catch(UnmodifiableClassException e){
        e.printStackTrace();
      }
    }

  }

  static class JPatchTransformer implements ClassFileTransformer{

    private ConcurrentHashMap<String, byte[]> transformTable;

    public JPatchTransformer(ConcurrentHashMap<String, byte[]> table){
      transformTable = table;
    }

    @Override
    public byte[] transform(ClassLoader loader, String className,
                                      Class<?> classBegingRedefined,
                                      ProtectionDomain protectionDomain,
                                      byte[] classfileBuffer)
                                          throws IllegalClassFormatException{

      byte[] result = transformTable.get(className);

      if(result != null){
        System.out.println("JPatch Patched: " + className);
      }

      return result;
    }

  }

}

