package sun.java2d;

public final class StateTrackableDelegate implements StateTrackable {
  public static final StateTrackableDelegate UNTRACKABLE_DELEGATE = new StateTrackableDelegate(StateTrackable.State.UNTRACKABLE);
  
  public static final StateTrackableDelegate IMMUTABLE_DELEGATE = new StateTrackableDelegate(StateTrackable.State.IMMUTABLE);
  
  private StateTrackable.State theState;
  
  StateTracker theTracker;
  
  private int numDynamicAgents;
  
  public static StateTrackableDelegate createInstance(StateTrackable.State paramState) {
    switch (paramState) {
      case UNTRACKABLE:
        return UNTRACKABLE_DELEGATE;
      case STABLE:
        return new StateTrackableDelegate(StateTrackable.State.STABLE);
      case DYNAMIC:
        return new StateTrackableDelegate(StateTrackable.State.DYNAMIC);
      case IMMUTABLE:
        return IMMUTABLE_DELEGATE;
    } 
    throw new InternalError("unknown state");
  }
  
  private StateTrackableDelegate(StateTrackable.State paramState) {
    this.theState = paramState;
  }
  
  public StateTrackable.State getState() {
    return this.theState;
  }
  
  public synchronized StateTracker getStateTracker() {
    Object object = this.theTracker;
    if (object == null) {
      switch (this.theState) {
        case IMMUTABLE:
          object = StateTracker.ALWAYS_CURRENT;
          break;
        case STABLE:
          object = new Object(this);
          break;
        case UNTRACKABLE:
        case DYNAMIC:
          object = StateTracker.NEVER_CURRENT;
          break;
      } 
      this.theTracker = (StateTracker)object;
    } 
    return (StateTracker)object;
  }
  
  public synchronized void setImmutable() {
    if (this.theState == StateTrackable.State.UNTRACKABLE || this.theState == StateTrackable.State.DYNAMIC)
      throw new IllegalStateException("UNTRACKABLE or DYNAMIC objects cannot become IMMUTABLE"); 
    this.theState = StateTrackable.State.IMMUTABLE;
    this.theTracker = null;
  }
  
  public synchronized void setUntrackable() {
    if (this.theState == StateTrackable.State.IMMUTABLE)
      throw new IllegalStateException("IMMUTABLE objects cannot become UNTRACKABLE"); 
    this.theState = StateTrackable.State.UNTRACKABLE;
    this.theTracker = null;
  }
  
  public synchronized void addDynamicAgent() {
    if (this.theState == StateTrackable.State.IMMUTABLE)
      throw new IllegalStateException("Cannot change state from IMMUTABLE"); 
    this.numDynamicAgents++;
    if (this.theState == StateTrackable.State.STABLE) {
      this.theState = StateTrackable.State.DYNAMIC;
      this.theTracker = null;
    } 
  }
  
  protected synchronized void removeDynamicAgent() {
    if (--this.numDynamicAgents == 0 && this.theState == StateTrackable.State.DYNAMIC) {
      this.theState = StateTrackable.State.STABLE;
      this.theTracker = null;
    } 
  }
  
  public final void markDirty() {
    this.theTracker = null;
  }
}
