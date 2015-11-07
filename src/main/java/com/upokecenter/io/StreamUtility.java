/*
Written in 2013 by Peter Occil.
Any copyright is dedicated to the Public Domain.
http://creativecommons.org/publicdomain/zero/1.0/

If you like this, you should donate to Peter O.
at: http://upokecenter.dreamhosters.com/articles/donate-now-2/
*/
package com.upokecenter.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import com.upokecenter.util.DataUtilities;

public final class StreamUtility {
  /**
   * Copies the data from one input stream to an
   * output stream.
   *
   * @param stream A readable data stream
   * @param output A writable data stream to write the data.
   * @throws IOException An I/O error occurred.
   */
  public static void copyStream(InputStream stream, OutputStream output)
      throws IOException {
    byte[] buffer=new byte[8192];
    while(true){
      int count=stream.read(buffer,0,buffer.length);
      if(count<0) {
        break;
      }
      output.write(buffer,0,count);
    }
  }

  public static void inputStreamToFile(InputStream stream, File file)
      throws IOException {
    FileOutputStream output=null;
    try {
      output=new FileOutputStream(file);
      copyStream(stream,output);
    } finally {
      if(output!=null) {
        output.close();
      }
    }
  }

  public static void skipToEnd(InputStream stream){
    if(stream==null)return;
    while(true){
      byte[] x=new byte[1024];
      try {
        int c=stream.read(x,0,x.length);
        if(c<0) {
          break;
        }
      } catch(IOException e){
        break; // maybe this stream is already closed
      }
    }
  }

  public static String fileToString(File file)
      throws IOException {
    FileInputStream input=null;
    try {
      input=new FileInputStream(file);
      return DataUtilities.ReadUtf8ToString(input);
    } finally {
      if(input!=null) {
        input.close();
      }
    }
  }

  /**
   *
   * Writes a string in UTF-8 to the specified file.
   * If the file exists, it will be overwritten
   *
   * @param s a string to write. Illegal code unit
   * sequences are replaced with
   * with U+FFFD REPLACEMENT CHARACTER when writing to the stream.
   * @param file a filename
   * @throws IOException if the file can't be created
   * or another I/O error occurs.
   */
  public static void stringToFile(String s, File file) throws IOException{
    OutputStream os=null;
    try {
      os=new FileOutputStream(file);
      DataUtilities.WriteUtf8(s, os, true);
    } finally {
      if(os!=null) {
        os.close();
      }
    }
  }
  private StreamUtility(){}

}
