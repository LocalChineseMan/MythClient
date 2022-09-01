package sun.java2d.cmm;

import java.awt.color.ProfileDataException;
import java.util.Vector;

public class ProfileDeferralMgr {
  public static boolean deferring = true;
  
  private static Vector<ProfileActivator> aVector;
  
  public static void registerDeferral(ProfileActivator paramProfileActivator) {
    if (!deferring)
      return; 
    if (aVector == null)
      aVector = new Vector<>(3, 3); 
    aVector.addElement(paramProfileActivator);
  }
  
  public static void unregisterDeferral(ProfileActivator paramProfileActivator) {
    if (!deferring)
      return; 
    if (aVector == null)
      return; 
    aVector.removeElement(paramProfileActivator);
  }
  
  public static void activateProfiles() {
    deferring = false;
    if (aVector == null)
      return; 
    int i = aVector.size();
    for (ProfileActivator profileActivator : aVector) {
      try {
        profileActivator.activate();
      } catch (ProfileDataException profileDataException) {}
    } 
    aVector.removeAllElements();
    aVector = null;
  }
}
