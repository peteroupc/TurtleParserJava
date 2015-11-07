/*
Written in 2013 by Peter Occil.
Any copyright is dedicated to the Public Domain.
http://creativecommons.org/publicdomain/zero/1.0/

If you like this, you should donate to Peter O.
at: http://upokecenter.dreamhosters.com/articles/donate-now-2/
*/
package com.upokecenter.io;

import java.io.IOException;

public interface IMarkableCharacterInput extends ICharacterInput {

  /**
   * Gets the zero-based character position in the stream
   * from the last-set mark.
   */
  public int getMarkPosition();

  /**
   * Moves the stream position back the given number
   * of characters.
   * @throws IOException No mark was set, or the position
   * is too close to the currently set mark.
   */
  public void moveBack(int count) throws IOException;

  /**
   * Sets a mark on the stream's current position.
   * @return Always 0.
   */
  public int setHardMark();

  /**
   * Sets the stream's position from the last set mark.
   * @param pos Zero-based character offset from
   * the last set mark.
   * @throws IOException No mark was set, or the position
   * is less than 1, or the position reaches the end
   * of the stream
   */
  public void setMarkPosition(int pos) throws IOException;

  /**
   * If no mark is set, sets a mark on the stream.
   * Characters read before the mark are no longer
   * available, while characters read after will
   * be available if moveBack is called.
   * Otherwise, behaves like getMarkPosition.
   */
  public int setSoftMark();

}
