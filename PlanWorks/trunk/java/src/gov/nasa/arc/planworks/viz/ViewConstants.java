// 
// * See the file "PlanWorks/disclaimers-and-notices.txt" for 
// * information on usage and redistribution of this file, 
// * and for a DISCLAIMER OF ALL WARRANTIES. 
// 

// PlanWorks
//
// Will Taylor -- started 18may03
//

package gov.nasa.arc.planworks.viz;

import java.awt.Font;
import java.util.ArrayList;
import java.util.List;

// PlanWorks/java/lib/JGo/JGo.jar
import com.nwoods.jgo.JGoText;



public interface ViewConstants {

  /**
   * constant <code>orderedViewNames - String []</code>
   *
   */
  public static final String[] orderedViewNames = 
    new String [] { "constraintNetworkView", "temporalExtentView", "temporalNetworkView",
                    "timelineView", "tokenNetworkView" };

  // views.timeline.TimelineView

  /**
   * constant <code>TIMELINE_VIEW_X_INIT</code>
   *
   */
  public static final int TIMELINE_VIEW_X_INIT = 10; 

  /**
   * constant <code>TIMELINE_VIEW_Y_INIT</code>
   *
   */
  public static final int TIMELINE_VIEW_Y_INIT = 10;

  /**
   * constant <code>TIMELINE_VIEW_Y_DELTA</code>
   *
   */
  public static final int TIMELINE_VIEW_Y_DELTA = 50;

  /**
   * constant <code>TIMELINE_VIEW_FONT_SIZE</code>
   *
   */
  public static final int TIMELINE_VIEW_FONT_SIZE = 12;

  /**
   * constant <code>TIMELINE_VIEW_FONT_NAME</code> - fixed width font
   *
   */
  public static final String TIMELINE_VIEW_FONT_NAME = "Monospaced";

  /**
   * constant <code>TIMELINE_VIEW_FONT_STYLE</code>
   *
   */
  public static final int TIMELINE_VIEW_FONT_STYLE = Font.PLAIN;

  /**
   * constant <code>TIME_INTERVAL_STRINGS_OVERLAP_OFFSET</code>
   *
   */
  public static final int TIME_INTERVAL_STRINGS_OVERLAP_OFFSET = 4;

  /**
   * constant <code>TIMELINE_VIEW_EMPTY_NODE_LABEL</code>
   *
   */
  public static final String TIMELINE_VIEW_EMPTY_NODE_LABEL = "<empty>";

  /**
   * constant <code>TIMELINE_VIEW_EMPTY_NODE_LABEL_LEN</code>
   *
   */
  public static final int TIMELINE_VIEW_EMPTY_NODE_LABEL_LEN = 7;



  // nodes.TimelineNode
  // nodes.SlotNode

  /**
   * constant <code>TIMELINE_VIEW_INSET_SIZE</code>
   *
   */
  public static final int TIMELINE_VIEW_INSET_SIZE = 10;

  /**
   * constant <code>TIMELINE_VIEW_INSET_SIZE_HALF</code>
   *
   */
  public static final int TIMELINE_VIEW_INSET_SIZE_HALF = TIMELINE_VIEW_INSET_SIZE / 2;

  /**
   * constant <code>TIMELINE_VIEW_IS_FONT_BOLD</code>
   *
   */
  public static final boolean TIMELINE_VIEW_IS_FONT_BOLD = false;

  /**
   * constant <code>TIMELINE_VIEW_IS_FONT_UNDERLINED</code>
   *
   */
  public static final boolean TIMELINE_VIEW_IS_FONT_UNDERLINED = false;

  /**
   * constant <code>TIMELINE_VIEW_IS_FONT_ITALIC</code>
   *
   */
  public static final boolean TIMELINE_VIEW_IS_FONT_ITALIC = false;

  /**
   * constant <code>TIMELINE_VIEW_TEXT_ALIGNMENT</code>
   *
   */
  public static final int TIMELINE_VIEW_TEXT_ALIGNMENT = JGoText.ALIGN_LEFT;

  /**
   * constant <code>TIMELINE_VIEW_IS_TEXT_MULTILINE</code>
   *
   */
  public static final boolean TIMELINE_VIEW_IS_TEXT_MULTILINE = false;

  /**
   * constant <code>TIMELINE_VIEW_IS_TEXT_EDITABLE</code>
   *
   */
  public static final boolean TIMELINE_VIEW_IS_TEXT_EDITABLE = false;

  /**
   * constant <code>EVEN_OBJECT_TIMELINE_BG_COLOR</code>
   *
   */
  public static final String EVEN_OBJECT_TIMELINE_BG_COLOR = "cornflowerBlue";

  /**
   * constant <code>EVEN_OBJECT_SLOT_BG_COLOR</code>
   *
   */
  public static final String EVEN_OBJECT_SLOT_BG_COLOR = "skyBlue";

  /**
   * constant <code>ODD_OBJECT_TIMELINE_BG_COLOR</code>
   *
   */
  public static final String ODD_OBJECT_TIMELINE_BG_COLOR = "aquamarine3";

  /**
   * constant <code>ODD_OBJECT_SLOT_BG_COLOR</code>
   *
   */
  public static final String ODD_OBJECT_SLOT_BG_COLOR = "paleGreen";

  /**
   * constant <code>FREE_TOKEN_BG_COLOR</code>
   *
   */
  public static final String FREE_TOKEN_BG_COLOR = "lightGray";

  /**
   * constant <code>EMPTY_SLOT_WIDTH</code>
   *
   */
  public static final int EMPTY_SLOT_WIDTH = 10;

  // Temporal Extent View

  /**
   * constant <code>TEMPORAL_NODE_X_DELTA</code>
   *
   */
  public static final int TEMPORAL_NODE_X_DELTA = 5;

  /**
   * constant <code>TEMPORAL_NODE_Y_DELTA</code>
   *
   */
  public static final int TEMPORAL_NODE_Y_DELTA = 10;

  /**
   * constant <code>TEMPORAL_NODE_CELL_HEIGHT</code>
   *
   */
  public static final int TEMPORAL_NODE_CELL_HEIGHT = 46;

  /**
   * constant <code>TEMPORAL_NODE_Y_LABEL_OFFSET</code>
   *
   */
  public static final int TEMPORAL_NODE_Y_LABEL_OFFSET = 10;

  /**
   * constant <code>TEMPORAL_NODE_Y_START_OFFSET</code>
   *
   */
  public static final int TEMPORAL_NODE_Y_START_OFFSET = 22;

  /**
   * constant <code>TEMPORAL_NODE_Y_END_OFFSET</code>
   *
   */
  public static final int TEMPORAL_NODE_Y_END_OFFSET = 34;

  /**
   * constant <code>TEMPORAL_MIN_END_X_LOC</code>
   *
   */
  public static final int TEMPORAL_MIN_END_X_LOC = 500;

  /**
   * constant <code>TEMPORAL_MIN_MAX_SLOTS</code>
   *
   */
  public static final int TEMPORAL_MIN_MAX_SLOTS = 15;

  /**
   * constant <code>TEMPORAL_LARGE_LABEL_RANGE</code>
   *
   */
  public static final int TEMPORAL_LARGE_LABEL_RANGE = 1000000;


  // cannot figure out how to get this from MDIInternalFrame
  /**
   * constant <code>MDI_FRAME_DECORATION_HEIGHT</code>
   *
   */
  public static final int MDI_FRAME_DECORATION_HEIGHT = 70;

  // cannot figure out how to get this from MDIInternalFrame
  /**
   * constant <code>MDI_FRAME_DECORATION_WIDTH</code>
   *
   */
  public static final int MDI_FRAME_DECORATION_WIDTH = 35;

  /**
   * constant <code>FRAME_DECORATION_HEIGHT</code>
   *
   */
  public static final int FRAME_DECORATION_HEIGHT = 75;

  /**
   * constant <code>FRAME_DECORATION_WIDTH</code>
   *
   */
  public static final int FRAME_DECORATION_WIDTH = 8;


} // end ViewConstants
