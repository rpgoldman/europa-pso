// 
// * See the file "PlanWorks/disclaimers-and-notices.txt" for 
// * information on usage and redistribution of this file, 
// * and for a DISCLAIMER OF ALL WARRANTIES. 
// 

// $Id: VizView.java,v 1.5 2003-07-09 23:14:38 taylor Exp $
//
// PlanWorks -- 
//
// Will Taylor -- started 18May03
//

package gov.nasa.arc.planworks.viz.views;

import java.util.Iterator;
import java.util.List;
import javax.swing.JPanel;

import gov.nasa.arc.planworks.db.PwPartialPlan;
import gov.nasa.arc.planworks.db.PwSlot;
import gov.nasa.arc.planworks.db.PwTimeline;
import gov.nasa.arc.planworks.db.PwToken;


/**
 * <code>VizView</code> - 
 *
 * @author <a href="mailto:william.m.taylor@nasa.gov">Will Taylor</a>
 *           NASA Ames Research Center - Code IC
 * @version 0.0
 */
public class VizView extends JPanel {

  protected PwPartialPlan partialPlan;

  /**
   * <code>VizView</code> - constructor 
   *
   * @param partialPlan - <code>PwPartialPlan</code> - 
   */
  public VizView( PwPartialPlan partialPlan) {
    super();
    this.partialPlan = partialPlan;
  }

  /**
   * <code>redraw</code> - each subclass of VizView will implement redraw()
   *
   */
  public void redraw() {
  }


  /**
   * <code>isTimelineInContentSpec</code> - does timeline have a least one token
   *                  in content spec
   *
   * @param timeline - <code>PwTimeline</code> - 
   * @param validTokenIds - <code>List</code> - 
   * @return - <code>boolean</code> - 
   */
  public boolean isTimelineInContentSpec( PwTimeline timeline, List validTokenIds) {
    boolean inSpec = false;
    List slotList = timeline.getSlotList();
    Iterator slotIterator = slotList.iterator();
    while (slotIterator.hasNext()) {
      PwSlot slot = (PwSlot) slotIterator.next();
      List tokenList = slot.getTokenList();
      if (tokenList.size() > 0) {
        Iterator tokenIterator = tokenList.iterator();
        while (tokenIterator.hasNext()) {
          PwToken token = (PwToken) tokenIterator.next();
          if (validTokenIds.indexOf( token.getKey()) >= 0) {
            return true;
          }
        }
        continue;
      } else {
        // empty slot
        continue;
      }
    }
    return inSpec;
  } // end isTimelineInContentSpec

  /**
   * <code>isSlotInContentSpec</code> - is one of slot's tokens in content spec
   *
   * @param slot - <code>PwSlot</code> - 
   * @param validTokenIds - <code>List</code> - 
   * @return - <code>boolean</code> - 
   */
  public boolean isSlotInContentSpec( PwSlot slot, List validTokenIds) {
    List tokenList = slot.getTokenList();
    if (tokenList.size() > 0) {
      Iterator tokenIterator = tokenList.iterator();
      while (tokenIterator.hasNext()) {
        PwToken token = (PwToken) tokenIterator.next();
        if (validTokenIds.indexOf( token.getKey()) >= 0) {
          return true;
        }
      }
      return false;
    } else {
      // empty slot -- do not display
      return false;
    }
  } // end isSlotInContentSpec

  /**
   * <code>isTokenInContentSpec</code> - is token in content spec
   *
   * @param token - <code>PwToken</code> - 
   * @param validTokenIds - <code>List</code> - 
   * @return - <code>boolean</code> - 
   */
  public boolean isTokenInContentSpec( PwToken token, List validTokenIds) {
    return (validTokenIds.indexOf( token.getKey()) >= 0);
  } // end isTokenInContentSpec



} // end class VizView

