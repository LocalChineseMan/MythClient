package sun.java2d;

public interface StateTrackable {
  State getState();
  
  StateTracker getStateTracker();
  
  public enum State {
    IMMUTABLE, STABLE, DYNAMIC, UNTRACKABLE;
  }
}
