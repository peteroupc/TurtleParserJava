/*
Written in 2013 by Peter Occil.
Any copyright to this work is released to the Public Domain.
In case this is not possible, this work is also
licensed under Creative Commons Zero (CC0):
https://creativecommons.org/publicdomain/zero/1.0/

*/
package com.upokecenter.io;

import java.io.IOException;

public final class IntArrayCharacterInput implements ICharacterInput {

  int pos;
  int[] ilist;

  public IntArrayCharacterInput(int[] ilist){
    this.ilist=ilist;
  }

  @Override
  public int read() throws IOException {
    int[] arr=this.ilist;
    if(pos<this.ilist.length)
      return arr[pos++];
    return -1;
  }

  @Override
  public int read(int[] buf, int offset, int unitCount) throws IOException {
    if(offset<0 || unitCount<0 || offset+unitCount>buf.length)
      throw new IndexOutOfBoundsException();
    if(unitCount==0)return 0;
    int[] arr=this.ilist;
    int size=this.ilist.length;
    int count=0;
    while(pos<size && unitCount>0){
      buf[offset]=arr[pos];
      offset++;
      count++;
      unitCount--;
      pos++;
    }
    return count==0 ? -1 : count;
  }

}
