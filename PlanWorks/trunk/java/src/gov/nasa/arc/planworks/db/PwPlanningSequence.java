// 
// * See the file "PlanWorks/disclaimers-and-notices.txt" for 
// * information on usage and redistribution of this file, 
// * and for a DISCLAIMER OF ALL WARRANTIES. 
// 

// $Id: PwPlanningSequence.java,v 1.6 2003-06-11 01:02:11 taylor Exp $
//
// PlanWorks -- 
//
// Will Taylor -- started 08May03
//

package gov.nasa.arc.planworks.db;

import java.util.List;

import gov.nasa.arc.planworks.util.ResourceNotFoundException;


/**
 * <code>PwPlanningSequence</code> - 
 *
 * @author <a href="mailto:william.m.taylor@nasa.gov">Will Taylor</a>
 *                         NASA Ames Research Center - Code IC
 * @version 0.0
 */
public interface PwPlanningSequence {


  /**
   * <code>getStepCount</code>
   *
   * @return - <code>int</code> - 
   */
  public abstract int getStepCount();

  /**
   * <code>getUrl</code>
   *
   * @return - <code>String</code> - 
   */
  public abstract String getUrl();

  /**
   * <code>getName</code>
   *
   * @return - <code>String</code> - 
   */
  public abstract String getName();

  /**
   * <code>getModel</code>
   *
   * @return - <code>PwModel</code> - 
   */
  public abstract PwModel getModel();

  /**
   * <code>listTransactions</code>
   *
   * @param step - <code>int</code> - 
   * @return - <code>List</code> - 
   * @exception IndexOutOfBoundsException if an error occurs
   */
  public abstract List listTransactions( int step) throws IndexOutOfBoundsException;

  /**
   * <code>getPartialPlan</code>
   *
   * @param step - <code>int</code> - 
   * @return - <code>PartialPlan</code> - 
   * @exception IndexOutOfBoundsException if an error occurs
   */
  public abstract PwPartialPlan getPartialPlan( int step) throws IndexOutOfBoundsException;


  /**
   * <code>getPartialPlan</code>
   *
   * @param planName - <code>String</code> - 
   * @return - <code>PwPartialPlan</code> - 
   */
  public abstract PwPartialPlan getPartialPlan( String planName)
    throws ResourceNotFoundException;

  /**
   * <code>addPartialPlan</code> -
   *          maintain PwPartialPlanImpl instance ordering of partialPlanNames
   *
   * @param url - <code>String</code> - 
   * @param partialPlanName - <code>String</code> - 
   * @return - <code>PartialPlan</code> - 
   * @exception ResourceNotFoundException if an error occurs
   */
  public abstract PwPartialPlan addPartialPlan( String url, String partialPlanName)
    throws ResourceNotFoundException;

} // end interface PwPlanningSequence
