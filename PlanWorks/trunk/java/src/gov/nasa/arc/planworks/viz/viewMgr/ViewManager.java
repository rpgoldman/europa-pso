//
// * See the file "PlanWorks/disclaimers-and-notices.txt" for
// * information on usage and redistribution of this file,
// * and for a DISCLAIMER OF ALL WARRANTIES.
//

// $Id: ViewManager.java,v 1.15 2003-09-26 22:47:07 miatauro Exp $
//
package gov.nasa.arc.planworks.viz.viewMgr;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

//import gov.nasa.arc.planworks.db.PwPartialPlan;
import gov.nasa.arc.planworks.db.PwPartialPlan;
import gov.nasa.arc.planworks.mdi.MDIDesktopFrame;
import gov.nasa.arc.planworks.mdi.MDIInternalFrame;
import gov.nasa.arc.planworks.viz.viewMgr.ViewableObject;
import gov.nasa.arc.planworks.viz.partialPlan.PartialPlanViewSet;

/**
 * <code>ViewManager</code> -
 * @author <a href="mailto:miatauro@email.arc.nasa.gov">Michael Iatauro</a>
 * A class to manage the various views.  A user can have, at any time, one each of up to five
 * different views per partial plan in the current project.  The ViewManager keeps track of 
 * ViewSets by their associted PwPartialPlan.
 */

public class ViewManager implements ViewSetRemover {
    public static final String CNET_VIEW = //"constraintNetworkView";
	"gov.nasa.arc.planworks.viz.partialPlan.constraintNetwork.ConstraintNetworkView";
    public static final String TEMPEXT_VIEW = //"temporalExtentView";
	"gov.nasa.arc.planworks.viz.partialPlan.temporalExtent.TemporalExtentView";
    public static final String TEMPNET_VIEW = "";//"temporalNetworkView";
    public static final String TIMELINE_VIEW = //"timelineView";
	"gov.nasa.arc.planworks.viz.partialPlan.timeline.TimelineView";
    public static final String TNET_VIEW = //"tokenNetworkView";
	"gov.nasa.arc.planworks.viz.partialPlan.tokenNetwork.TokenNetworkView";
  private MDIDesktopFrame desktopFrame;
  private HashMap viewSets;

  /**
   * Creates the ViewManager object and prepares it for adding views.
   */
  public ViewManager(MDIDesktopFrame desktopFrame) {
    viewSets = new HashMap();
    this.desktopFrame = desktopFrame;
  }

  public MDIInternalFrame openView(ViewableObject viewable, String viewName) {
    if(!viewSets.containsKey(viewable)) {
	if(viewable instanceof PwPartialPlan) {
	    viewSets.put(viewable, new PartialPlanViewSet(desktopFrame, (PwPartialPlan) viewable, this));
	}
	else {
	    viewSets.put(viewable, new ViewSet(desktopFrame, viewable, this));
	}
    }
    return ((ViewSet)viewSets.get(viewable)).openView(viewName);
  }

//   /**
//    * Opens a TimelineView.  If one exists, it is setSelected(true).
//    * @param partialPlan The PwPartialPlan with which this view is associated.
//    * @param planName The name of the plan.  This is used as the title of the view windows so
//    *                 they are visually distinct across partial plans.
//    * @return MDIInternalFrame the frame containing the newly created or selected view.
//    */
//   public MDIInternalFrame openTimelineView(PwPartialPlan partialPlan, String planName,
//                                            long startTimeMSecs) {
//     if(!viewSets.containsKey(partialPlan)) {
//       viewSets.put(partialPlan, new ViewSet(desktopFrame, partialPlan, planName, this));
//     }
//     return ((ViewSet)viewSets.get(partialPlan)).openTimelineView( startTimeMSecs);
//   }

//   /**
//    * Opens a TokenNetworkView.  If one exists, it is setSelected(true).
//    * @param partialPlan The PwPartialPlan with which this view is associated.
//    * @param planName The name of the plan.  This is used as the title of the view windows so
//    *                 they are visually distinct across partial plans.
//    * @return MDIInternalFrame the frame containing the newly created or selected view.
//    */
//   public MDIInternalFrame openTokenNetworkView(PwPartialPlan partialPlan, String planName,
//                                                long startTimeMSecs) {
//     if(!viewSets.containsKey(partialPlan)) {
//       viewSets.put(partialPlan, new ViewSet(desktopFrame, partialPlan, planName, this));
//     }
//     return ((ViewSet)viewSets.get(partialPlan)).openTokenNetworkView( startTimeMSecs);
//   }

//   /**
//    * Opens a TemporalExtentView.  If one exists, it is setSelected(true).
//    * @param partialPlan The PwPartialPlan with which this view is associated.
//    * @param planName The name of the plan.  This is used as the title of the view windows so
//    *                 they are visually distinct across partial plans.
//    * @return MDIInternalFrame the frame containing the newly created or selected view.
//    */
//   public MDIInternalFrame openTemporalExtentView(PwPartialPlan partialPlan, String planName,
//                                                  long startTimeMSecs) {
//     if(!viewSets.containsKey(partialPlan)) {
//       viewSets.put(partialPlan, new ViewSet(desktopFrame, partialPlan, planName, this));
//     }
//     return ((ViewSet)viewSets.get(partialPlan)).openTemporalExtentView( startTimeMSecs);
//   }

//   /**
//    * Opens a ConstrintNetworkView.  If one exists, it is setSelected(true).
//    * @param partialPlan The PwPartialPlan with which this view is associated.
//    * @param planName The name of the plan.  This is used as the title of the view windows so
//    *                 they are visually distinct across partial plans.
//    * @return MDIInternalFrame the frame containing the newly created or selected view.
//    */
//   public MDIInternalFrame openConstraintNetworkView(PwPartialPlan partialPlan, String planName,
//                                                     long startTimeMSecs) {
//     if(!viewSets.containsKey(partialPlan)) {
//       viewSets.put(partialPlan, new ViewSet(desktopFrame, partialPlan, planName, this));
//     }
//     return ((ViewSet)viewSets.get(partialPlan)).openConstraintNetworkView( startTimeMSecs);
//   } 

  /*
  public MDIInternalFrame openTemporalNetworkView(PwPartialPlan partialPlan, String planName) {
  }
  */
  /**
   * Removes all views associated with a partial plan.
   * @param key The partial plan whose views are going away.
   */
  //public void removeViewSet(PwPartialPlan key) {
  public void removeViewSet(ViewableObject key) {
    viewSets.remove(key);
  }
  /**
   * Clears all of the view sets.
   */
  public void clearViewSets() {
    Object [] viewSetss = viewSets.values().toArray();
    for(int i = 0; i < viewSetss.length; i++) {
      ((ViewSet)viewSetss[i]).close();
    }
    viewSets.clear();
  }
  /**
   * Gets the ViewSet associated with a particular partial plan.
   * @param partialPlan the PwPartialPlan whose ViewSet is needed.
   * @return ViewSet the ViewSet associated with the partial plan.
   */
  //public ViewSet getViewSet(PwPartialPlan partialPlan) {
  public ViewSet getViewSet(ViewableObject viewable) {
    //if(viewSets.containsKey(partialPlan)) {
    //  return (ViewSet) viewSets.get(partialPlan);
    //}
    return (ViewSet) viewSets.get(viewable);
      //return null;
  }
}
