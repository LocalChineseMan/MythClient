package org.lwjgl.opengl;

import org.lwjgl.LWJGLException;

interface DrawableLWJGL extends Drawable {
  void setPixelFormat(PixelFormatLWJGL paramPixelFormatLWJGL) throws LWJGLException;
  
  void setPixelFormat(PixelFormatLWJGL paramPixelFormatLWJGL, ContextAttribs paramContextAttribs) throws LWJGLException;
  
  PixelFormatLWJGL getPixelFormat();
  
  Context getContext();
  
  Context createSharedContext() throws LWJGLException;
  
  void checkGLError();
  
  void setSwapInterval(int paramInt);
  
  void swapBuffers() throws LWJGLException;
  
  void initContext(float paramFloat1, float paramFloat2, float paramFloat3);
}
