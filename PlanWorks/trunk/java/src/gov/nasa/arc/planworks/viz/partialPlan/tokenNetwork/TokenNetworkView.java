// 
// * See the file "PlanWorks/disclaimers-and-notices.txt" for 
// * information on usage and redistribution of this file, 
// * and for a DISCLAIMER OF ALL WARRANTIES. 
// 

// $Id: TokenNetworkView.java,v 1.64 2004-08-10 21:17:11 taylor Exp $
//
// PlanWorks -- 
//
// Will Taylor -- started 19June03
//

package gov.nasa.arc.planworks.viz.partialPlan.tokenNetwork;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import javax.swing.BoxLayout;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

// PlanWorks/java/lib/JGo/JGo.jar
import com.nwoods.jgo.JGoDocument;
import com.nwoods.jgo.JGoPen;
import com.nwoods.jgo.JGoView;

// PlanWorks/java/lib/JGo/Classier.jar
import com.nwoods.jgo.examples.BasicNode;

import gov.nasa.arc.planworks.PlanWorks;
import gov.nasa.arc.planworks.db.PwEntity;
import gov.nasa.arc.planworks.db.PwPartialPlan;
import gov.nasa.arc.planworks.db.PwPlanningSequence;
import gov.nasa.arc.planworks.db.PwRuleInstance;
import gov.nasa.arc.planworks.db.PwSlot;
import gov.nasa.arc.planworks.db.PwToken;
import gov.nasa.arc.planworks.util.ColorMap;
import gov.nasa.arc.planworks.util.MouseEventOSX;
import gov.nasa.arc.planworks.util.SwingWorker;
import gov.nasa.arc.planworks.viz.ViewConstants;
import gov.nasa.arc.planworks.viz.ViewGenerics;
import gov.nasa.arc.planworks.viz.ViewListener;
import gov.nasa.arc.planworks.viz.VizView;
import gov.nasa.arc.planworks.viz.VizViewOverview;
import gov.nasa.arc.planworks.viz.nodes.BasicNodeLink;
import gov.nasa.arc.planworks.viz.nodes.ExtendedBasicNode;
import gov.nasa.arc.planworks.viz.nodes.IncrementalNode;
import gov.nasa.arc.planworks.viz.nodes.NodeGenerics;
import gov.nasa.arc.planworks.viz.nodes.RuleInstanceNode;
import gov.nasa.arc.planworks.viz.nodes.TokenNode;
import gov.nasa.arc.planworks.viz.partialPlan.PartialPlanView;
import gov.nasa.arc.planworks.viz.partialPlan.PartialPlanViewSet;
import gov.nasa.arc.planworks.viz.partialPlan.PartialPlanViewState;
import gov.nasa.arc.planworks.viz.util.AskNodeByKey;
import gov.nasa.arc.planworks.viz.util.AskQueryTwoEntityKeys;
import gov.nasa.arc.planworks.viz.util.MessageDialog;
import gov.nasa.arc.planworks.viz.viewMgr.ViewableObject;
import gov.nasa.arc.planworks.viz.viewMgr.ViewSet;

/**
 * <code>TokenNetworkView</code> - render a partial plan's tokens, their masters
 *                                 and slaves
 *
 * @author <a href="mailto:william.m.taylor@nasa.gov">Will Taylor</a>
 *                  NASA Ames Research Center - Code IC
 * @version 0.0
 */
public class TokenNetworkView extends PartialPlanView {

  /**
   * variable <code>timelineColorMap</code>
   *
   */
  protected Map timelineColorMap;

  /**
   * variable <code>entityTokNetNodeMap</code>
   *
   */
  protected Map entityTokNetNodeMap; 

  /**
   * variable <code>tokNetLinkMap</code>
   *
   */
  protected Map tokNetLinkMap;

  private long startTimeMSecs;
  private ViewSet viewSet;
  private TokenNetworkJGoView jGoView;
  private JGoDocument jGoDocument;
  private Map tokenNodeMap; // key = tokenId, element TokenNetworkTokenNode
  private Map ruleInstanceNodeMap; // key = ruleInstanceId, element TokenNetworkRuleInstanceNode
  private boolean isStepButtonView;
  private Integer focusNodeId;
  private boolean isLayoutNeeded;
  private ExtendedBasicNode focusNode;
  private List rootTokens;
  private boolean isDebugPrint;
  private PartialPlanViewState state;
  private List highlightPathNodesList;
  private Integer tokenKey1;
  private Integer tokenKey2;

  /**
   * <code>TokenNetworkView</code> - constructor - 
   *                             Use SwingWorker to
   *                             properly render the JGo widgets
   *
   * @param partialPlan - <code>ViewableObject</code> -
   * @param viewSet - <code>ViewSet</code> - 
   */
  public TokenNetworkView( final ViewableObject partialPlan,  final ViewSet viewSet) {
    super( (PwPartialPlan) partialPlan, (PartialPlanViewSet) viewSet);
    tokenNetworkViewInit( viewSet);
    isStepButtonView = false;
    state = null;
    // print content spec
    // viewSet.printSpec();

    // SwingUtilities.invokeLater( runInit);
    final SwingWorker worker = new SwingWorker() {
        public Object construct() {
          init();
          return null;
        }
    };
    worker.start();  
  } // end constructor

  /**
   * <code>TokenNetworkView</code> - constructor 
   *
   * @param partialPlan - <code>ViewableObject</code> - 
   * @param viewSet - <code>ViewSet</code> - 
   * @param state - <code>PartialPlanViewState</code> - 
   */
  public TokenNetworkView( final ViewableObject partialPlan, final ViewSet viewSet,
                           final PartialPlanViewState state) {
    super((PwPartialPlan) partialPlan, (PartialPlanViewSet) viewSet);
    tokenNetworkViewInit( viewSet);
    isStepButtonView = true;
    // setState( state);
    this.state = state;
    // SwingUtilities.invokeLater( runInit);
    final SwingWorker worker = new SwingWorker() {
        public Object construct() {
          init();
          return null;
        }
    };
    worker.start();  
  }

  /**
   * <code>TokenNetworkView</code> - constructor 
   *
   * @param partialPlan - <code>ViewableObject</code> - 
   * @param viewSet - <code>ViewSet</code> - 
   * @param viewListener - <code>ViewListener</code> - 
   */
  public TokenNetworkView( final ViewableObject partialPlan,  final ViewSet viewSet,
                           final ViewListener viewListener) {
    super( (PwPartialPlan) partialPlan, (PartialPlanViewSet) viewSet);
    tokenNetworkViewInit( viewSet);
    isStepButtonView = false;
    state = null;
    // print content spec
    // viewSet.printSpec();
    if (viewListener != null) {
      addViewListener( viewListener);
    }

    // SwingUtilities.invokeLater( runInit);
    final SwingWorker worker = new SwingWorker() {
        public Object construct() {
          init();
          return null;
        }
    };
    worker.start();  
  } // end constructor

  /**
   * <code>TokenNetworkView</code> - constructor 
   *
   * @param partialPlan - <code>ViewableObject</code> - 
   * @param viewSet - <code>ViewSet</code> - 
   * @param isFindTokenPath - <code>boolean</code> - 
   */
  public TokenNetworkView( ViewableObject partialPlan, ViewSet viewSet,
			   boolean isFindTokenPath) {
    super( (PwPartialPlan)partialPlan, (PartialPlanViewSet) viewSet);
  } // end constructor

  private void tokenNetworkViewInit( final ViewSet viewSet) {
    this.viewSet = (PartialPlanViewSet) viewSet;
    focusNodeId = null;
    setLayout( new BoxLayout( this, BoxLayout.Y_AXIS));
    jGoView = new TokenNetworkJGoView();
    jGoView.setBackground( ViewConstants.VIEW_BACKGROUND_COLOR);
    add( jGoView, BorderLayout.NORTH);
    jGoView.validate();
    jGoView.setVisible( true);
    this.setVisible( true);
    ViewListener viewListener = null;
    viewFrame = viewSet.openView( this.getClass().getName(), viewListener);
    // for PWTestHelper.findComponentByName
    this.setName( viewFrame.getTitle());
    viewName = ViewConstants.TOKEN_NETWORK_VIEW;
    // isDebugPrint = true;
    isDebugPrint = false;
    highlightPathNodesList = null;
  }

  /**
   * <code>getState</code>
   *
   * @return - <code>PartialPlanViewState</code> - 
   */
  public PartialPlanViewState getState() {
    return new TokenNetworkViewState( this);
  }

  /**
   * <code>setState</code>
   *
   * @param s - <code>PartialPlanViewState</code> - 
   */
  public void setState( PartialPlanViewState s) {
    super.setState(s);
    if(s == null) {
      return;
    }
    zoomFactor = s.getCurrentZoomFactor();
    boolean isSetState = true;
    zoomView( jGoView, isSetState, this);
    int penWidth = getOpenJGoPenWidth( zoomFactor);

    TokenNetworkViewState state = (TokenNetworkViewState) s;

    ListIterator idIterator = state.getModTokens().listIterator();
    while (idIterator.hasNext()) {
      TokenNetworkViewState.ModNode modNode = (TokenNetworkViewState.ModNode) idIterator.next();
      TokenNetworkTokenNode node =
        addTokenTokNetNode( partialPlan.getToken( modNode.getId()));
      node.setInLayout( true);
      node.setAreNeighborsShown( modNode.getAreNeighborsShown());
      if (modNode.getAreNeighborsShown()) {
        node.setPen( new JGoPen( JGoPen.SOLID, penWidth, ColorMap.getColor( "black")));
      }
    }

    idIterator = state.getModRuleInstances().listIterator();
    while (idIterator.hasNext()) {
      TokenNetworkViewState.ModNode modNode = (TokenNetworkViewState.ModNode) idIterator.next();
      TokenNetworkRuleInstanceNode node =
        addRuleInstanceTokNetNode( partialPlan.getRuleInstance( modNode.getId()));
      node.setInLayout( true);
      node.setAreNeighborsShown( modNode.getAreNeighborsShown());
      if (modNode.getAreNeighborsShown()) {
        node.setPen( new JGoPen( JGoPen.SOLID, penWidth, ColorMap.getColor( "black")));
      }
    }

    Iterator nodeKeyItr = entityTokNetNodeMap.keySet().iterator();
    while (nodeKeyItr.hasNext()) {
      IncrementalNode tokNetNode = (IncrementalNode) entityTokNetNodeMap.get( nodeKeyItr.next());
      TokenNetworkGenerics.addParentToEntityTokNetLinks( tokNetNode, this, isDebugPrint);
    }
    // now set the linkCounts
    idIterator = state.getModTokens().listIterator();
    while (idIterator.hasNext()) {
      TokenNetworkViewState.ModNode modNode = (TokenNetworkViewState.ModNode) idIterator.next();
      TokenNetworkTokenNode node =
	(TokenNetworkTokenNode) entityTokNetNodeMap.get( modNode.getId());
      node.setLinkCount( modNode.getLinkCount());
    }
    idIterator = state.getModRuleInstances().listIterator();
    while (idIterator.hasNext()) {
      TokenNetworkViewState.ModNode modNode = (TokenNetworkViewState.ModNode) idIterator.next();
      TokenNetworkRuleInstanceNode node =
	(TokenNetworkRuleInstanceNode) entityTokNetNodeMap.get( modNode.getId());
       node.setLinkCount( modNode.getLinkCount());
    }     
    ListIterator linkIterator = state.getModLinks().listIterator();
    while (linkIterator.hasNext()) {
      TokenNetworkViewState.ModLink modLink =
	(TokenNetworkViewState.ModLink) linkIterator.next();
      BasicNodeLink link = (BasicNodeLink) tokNetLinkMap.get( modLink.getLinkName());
      if (link == null) {
	System.err.println( "setState: linkName " + modLink.getLinkName() + " not found");
      } else {
	link.setLinkCount( modLink.getLinkCount());
      }
    }

  } // end setState



//   private Runnable runInit = new Runnable() {
//       public final void run() {
//         init();
//       }
//     };

  /**
   * <code>init</code> - wait for instance to become displayable, determine
   *                     appropriate font metrics, and render the JGo timeline,
   *                     and slot widgets
   *
   *    These functions are not done in the constructor to avoid:
   *    "Cannot measure text until a JGoView exists and is part of a visible window".
   *    called by componentShown method on the JFrame
   *    JGoView.setVisible( true) must be completed -- use SwingWorker in constructor
   */
  public final void init() {
    handleEvent( ViewListener.EVT_INIT_BEGUN_DRAWING);
    // wait for TimelineView instance to become displayable
    if (! ViewGenerics.displayableWait( TokenNetworkView.this)) {
      closeView( this);
      return;
    }
    this.computeFontMetrics( this);

    jGoDocument = jGoView.getDocument();
    jGoDocument.addDocumentListener( createDocListener());

    validTokenIds = viewSet.getValidIds();
    displayedTokenIds = new ArrayList();
    tokNetLinkMap = new HashMap();
    entityTokNetNodeMap = new HashMap();
    tokenNodeMap = new HashMap();
    ruleInstanceNodeMap = new HashMap();

    if (state == null) {
      rootTokens = getRootTokens();
      renderRootTokens();
    }
    setState( state);

    setNodesLinksVisible();

    TokenNetworkLayout layout = new TokenNetworkLayout( jGoDocument, startTimeMSecs);
    layout.performLayout();
    
//     Rectangle documentBounds = jGoView.getDocument().computeBounds();
//     jGoView.getDocument().setDocumentSize( (int) documentBounds.getWidth() +
//                                            (ViewConstants.TIMELINE_VIEW_X_INIT * 4),
//                                            (int) documentBounds.getHeight() +
//                                            (ViewConstants.TIMELINE_VIEW_Y_INIT * 2));
    if (! isStepButtonView) {
      expandViewFrame( viewFrame, (int) jGoView.getDocumentSize().getWidth(),
                       (int) jGoView.getDocumentSize().getHeight());
    }
    // print out info for created nodes
    // iterateOverJGoDocument(); // slower - many more nodes to go thru
    // iterateOverNodes();
    
    addStepButtons( jGoView);
    if (! isStepButtonView) {
      expandViewFrameForStepButtons( viewFrame, jGoView);
    }
    long stopTimeMSecs = System.currentTimeMillis();
    System.err.println( "   ... " + ViewConstants.TOKEN_NETWORK_VIEW + " elapsed time: " +
                        (stopTimeMSecs -
                         PlanWorks.getPlanWorks().getViewRenderingStartTime
                         ( ViewConstants.TOKEN_NETWORK_VIEW)) + " msecs.");
    startTimeMSecs = 0L;
    isLayoutNeeded = false;
    focusNode = null;
    handleEvent( ViewListener.EVT_INIT_ENDED_DRAWING);
  } // end init

  /**
   * <code>redraw</code> - called by Content Spec to apply user's content spec request.
   *
   */
  public final void redraw() {
    Thread thread = new RedrawViewThread();
    thread.setPriority( Thread.MIN_PRIORITY);
    thread.start();
  }

  class RedrawViewThread extends Thread {

    public RedrawViewThread() {
    }  // end constructor

    public final void run() {
      try {
        ViewGenerics.setRedrawCursor( viewFrame);
        redrawView();
      } finally {
        ViewGenerics.resetRedrawCursor( viewFrame);
      }
    } // end run

  } // end class RedrawViewThread


  private void redrawView() {
    System.err.println( "Redrawing Token Network View ...");
    if (startTimeMSecs == 0L) {
      startTimeMSecs = System.currentTimeMillis();
    }
    this.setVisible( false);

    progressMonitorThread( "Redrawing Token Network View ...", 0, 6, Thread.currentThread(),
			   this);
    if (! progressMonitorWait( this)) {
      System.err.println( "progressMonitorWait failed");
      closeView( this);
      return;
    }
    progressMonitor.setProgress( 3 * ViewConstants.MONITOR_MIN_MAX_SCALING);
    // content spec apply/reset do not change layout, only TokenNode/
    // variableNode/constraintNode opening/closing

    setNodesLinksVisible();

    if (isLayoutNeeded) {
      TokenNetworkLayout layout = new TokenNetworkLayout( jGoDocument, startTimeMSecs);
      layout.performLayout();

      if ((focusNode == null) && (highlightPathNodesList != null)) {
	NodeGenerics.highlightPathNodes( highlightPathNodesList, jGoView);
      } else if (focusNode != null) {
	// do not highlight node, if it has been removed
	NodeGenerics.focusViewOnNode( focusNode, ((IncrementalNode) focusNode).inLayout(),
				      jGoView);
      }
      isLayoutNeeded = false;
    }
    long stopTimeMSecs = System.currentTimeMillis();
    System.err.println( "   ... " + ViewConstants.TOKEN_NETWORK_VIEW + " elapsed time: " +
                        (stopTimeMSecs - startTimeMSecs) + " msecs.");
    startTimeMSecs = 0L;
    this.setVisible( true);
    isProgressMonitorCancel = true;
  } // end redrawView

  /**
   * <code>setStartTimeMSecs</code>
   *
   * @param msecs - <code>long</code> - 
   */
  protected final void setStartTimeMSecs( final long msecs) {
    startTimeMSecs = msecs;
  }

  /**
   * <code>setLayoutNeeded</code>
   *
   */
  public final void setLayoutNeeded() {
    isLayoutNeeded = true;
  }

  /**
   * <code>isLayoutNeeded</code>> - 
   *
   * @return - <code>boolean</code> - 
   */
  public final boolean isLayoutNeeded() {
    return isLayoutNeeded;
  }

  /**
   * <code>setFocusNode</code>
   *
   * @param node - <code>ExtendedBasicNode</code> - 
   */
  public final void setFocusNode( final ExtendedBasicNode node) {
    this.focusNode = node;
  }

  /**
   * <code>getJGoView</code> - 
   *
   * @return - <code>JGoView</code> - 
   */
  public final JGoView getJGoView()  {
    return jGoView;
  }

  /**
   * <code>getJGoDocument</code>
   *
   * @return - <code>JGoDocument</code> - 
   */
  public final JGoDocument getJGoDocument()  {
    return this.jGoDocument;
  }

  /**
   * <code>getTokenNodeKeyList</code>
   *
   * @return - <code>List</code> - 
   */
  public final List getTokenNodeKeyList() {
    return new ArrayList( tokenNodeMap.keySet());
  }

  /**
   * <code>getRuleInstanceNodeKeyList</code>
   *
   * @return - <code>List</code> - 
   */
  public final List getRuleInstanceNodeKeyList() {
    return new ArrayList( ruleInstanceNodeMap.keySet());
  }

  /**
   * <code>getTokenNode</code>
   *
   * @param id - <code>Integer</code> - 
   * @return - <code>TokenNetworkTokenNode</code> - 
   */
  public final TokenNetworkTokenNode getTokenNode( final Integer id) {
    return (TokenNetworkTokenNode) tokenNodeMap.get( id);
  }

  /**
   * <code>getRuleInstanceNode</code>
   *
   * @param id - <code>Integer</code> - 
   * @return - <code>RuleInstanceNode</code> - 
   */
  public final RuleInstanceNode getRuleInstanceNode( final Integer id) {
    return (RuleInstanceNode) ruleInstanceNodeMap.get( id);
  }

  /**
   * <code>getFocusNodeId</code>
   *
   * @return - <code>Integer</code> - 
   */
  public final Integer getFocusNodeId() {
    return focusNodeId;
  }

  private List getRootTokens() {
    List rootTokens = new ArrayList();
    Iterator tokenIterator = partialPlan.getTokenList().iterator();
    while (tokenIterator.hasNext()) {
      PwToken token = (PwToken) tokenIterator.next();
      if (isTokenInContentSpec( token)) {
        Integer masterTokenId = partialPlan.getMasterTokenId( token.getId());
        if (masterTokenId == null) {
          rootTokens.add( token);
        }
      }
    }
//     Iterator rootTokensItr = rootTokens.iterator();
//     while (rootTokensItr.hasNext()) {
//       System.err.println( "root token id " + ((PwToken) rootTokensItr.next()).getId());
//     }
    return rootTokens;
  } // end getRootTokens

  private void renderRootTokens() {
    Iterator tokenItr = rootTokens.iterator();
    while (tokenItr.hasNext()) {
      PwToken token = (PwToken) tokenItr.next();
      ExtendedBasicNode node = addEntityTokNetNode( token, isDebugPrint);
      IncrementalNode tokNetNode = (IncrementalNode) node;
      TokenNetworkGenerics.addEntityTokNetNodes( tokNetNode, this, isDebugPrint);
      TokenNetworkGenerics.addParentToEntityTokNetLinks( tokNetNode, this, isDebugPrint);
      TokenNetworkGenerics.addEntityToChildTokNetLinks( tokNetNode, this, isDebugPrint);

      int penWidth = this.getOpenJGoPenWidth( this.getZoomFactor());
      node.setPen( new JGoPen( JGoPen.SOLID, penWidth, ColorMap.getColor( "black")));
      node.setAreNeighborsShown( true);
    }
  } // end renderRootTokens

  /**
   * <code>addEntityTokNetNode</code>
   *
   * @param object - <code>PwEntity</code> - 
   * @param isDebugPrint - <code>boolean</code> - 
   * @return - <code>ExtendedBasicNode</code> - 
   */
  protected final ExtendedBasicNode addEntityTokNetNode( final PwEntity object,
                                                         final boolean isDebugPrint) {
    ExtendedBasicNode node = null;
    if (object instanceof PwToken) {
      node = addTokenTokNetNode( (PwToken) object);
    } else if (object instanceof PwRuleInstance) {
      node = addRuleInstanceTokNetNode( (PwRuleInstance) object);
    } else {
      System.err.println( "\nTokenNetworkView.addEntityTokNetNode " + object + " not handled");
      try {
        throw new Exception();
      } catch (Exception e) { e.printStackTrace(); }
    }
    IncrementalNode tokNetNode = (IncrementalNode) node;
    if (isDebugPrint) {
      System.err.println( "add " + tokNetNode.getTypeName() + "TokNetNode " +
                          tokNetNode.getId());
    }
    if (! tokNetNode.inLayout()) {
      tokNetNode.setInLayout( true);
    }
    return node;
  } // end addEntityTokNetNode

  /**
   * <code>addTokenTokNetNode</code>
   *
   * @param token - <code>PwToken</code> - 
   * @return - <code>TokenNetworkTokenNode</code> - 
   */
  protected final TokenNetworkTokenNode addTokenTokNetNode( final PwToken token) {
    boolean isDraggable = true;
    TokenNetworkTokenNode tokenTokNetNode =
      (TokenNetworkTokenNode) entityTokNetNodeMap.get( token.getId());
    if (tokenTokNetNode == null) {
      tokenTokNetNode =
        new TokenNetworkTokenNode( token, new Point( ViewConstants.TIMELINE_VIEW_X_INIT * 2,
                                                     ViewConstants.TIMELINE_VIEW_Y_INIT * 2),
                                   TokenNetworkGenerics.getTokenColor( token, this),
                                   isDraggable, this);
      entityTokNetNodeMap.put( token.getId(), tokenTokNetNode);
      tokenNodeMap.put( token.getId(), tokenTokNetNode);
      jGoDocument.addObjectAtTail( tokenTokNetNode);
    }
    return tokenTokNetNode;
  } // end addTokenNetworkTokenNode

  /**
   * <code>addRuleInstanceTokNetNode</code>
   *
   * @param ruleInstance - <code>PwRuleInstance</code> - 
   * @return - <code>TokenNetworkRuleInstanceNode</code> - 
   */
  protected final TokenNetworkRuleInstanceNode addRuleInstanceTokNetNode ( final PwRuleInstance ruleInstance) {
    boolean isDraggable = true;
    TokenNetworkRuleInstanceNode ruleInstanceTokNetNode =
      (TokenNetworkRuleInstanceNode) entityTokNetNodeMap.get( ruleInstance.getId());
    if (ruleInstanceTokNetNode == null) {
      ruleInstanceTokNetNode =
        new TokenNetworkRuleInstanceNode( ruleInstance,
                                 new Point( ViewConstants.TIMELINE_VIEW_X_INIT * 2,
                                            ViewConstants.TIMELINE_VIEW_Y_INIT * 2),
                                 ViewConstants.RULE_INSTANCE_BG_COLOR, isDraggable, this);
      entityTokNetNodeMap.put( ruleInstance.getId(), ruleInstanceTokNetNode);
      ruleInstanceNodeMap.put( ruleInstance.getId(), ruleInstanceTokNetNode);
      jGoDocument.addObjectAtTail( ruleInstanceTokNetNode);
    }
    return ruleInstanceTokNetNode;
  } // end addTokenNetworkRuleInstanceNode


  /**
   * <code>addTokenNetworkLinkNew</code>
   *
   * @param fromNode - <code>ExtendedBasicNode</code> - 
   * @param link - <code>BasicNodeLink</code> - 
   * @param linkType - <code>String</code> - 
   * @return - <code>boolean</code> - 
   */
  protected final boolean addTokenNetworkLinkNew( final ExtendedBasicNode fromNode,
                                                  final BasicNodeLink link,
                                                  final String linkType) {
    boolean areLinksChanged = false;
    if (link != null) {
      // links are always behind any nodes
      // jGoDocument.addObjectAtHead( link);
      // jGoDocument.addObjectAtTail( link);
      // jGoDocument.insertObjectBefore( jGoDocument.findObject( fromNode), link);
      jGoDocument.insertObjectAfter( jGoDocument.findObject( fromNode), link);

      link.setInLayout( true);
      link.incrLinkCount();
      if (isDebugPrint) {
        System.err.println( linkType + " incr link: " + link.toString() + " to " +
                            link.getLinkCount());
      }
      areLinksChanged = true;
    }
    return areLinksChanged;
  } // end addNavigatorLinkNew

  private void setNodesLinksVisible() {
    List objectNodeKeyList = new ArrayList( entityTokNetNodeMap.keySet());
    Iterator objectNodeKeyItr = objectNodeKeyList.iterator();
    while (objectNodeKeyItr.hasNext()) {
      ExtendedBasicNode objectTokNetNode =
        (ExtendedBasicNode) entityTokNetNodeMap.get( (Integer) objectNodeKeyItr.next());
      if (((IncrementalNode) objectTokNetNode).inLayout()) {
        objectTokNetNode.setVisible( true);
      } else {
        objectTokNetNode.setVisible( false);
      }
    }
    List tokNetLinkKeyList = new ArrayList( tokNetLinkMap.keySet());
    Iterator tokNetLinkKeyItr = tokNetLinkKeyList.iterator();
    while (tokNetLinkKeyItr.hasNext()) {
      BasicNodeLink tokNetLink =
        (BasicNodeLink) tokNetLinkMap.get( (String) tokNetLinkKeyItr.next());
      if (tokNetLink.inLayout()) {
        tokNetLink.setVisible( true);
        if (isDebugPrint && (tokNetLink.getMidLabel() != null)) {
          tokNetLink.getMidLabel().setVisible( true);
        }
      } else {
        tokNetLink.setVisible( false);
        if (isDebugPrint && (tokNetLink.getMidLabel() != null)) {
          tokNetLink.getMidLabel().setVisible( false);
        }
      }
    }
  } // end setNodesLinksVisible


  /**
   * <code>FindTokenPath</code> - used as arg to ProgressMonitorThread
   *
   */
  public class FindTokenPath extends TokenNetworkView {

    private List tokenRuleKeyList;

    FindTokenPath( ViewableObject partialPlan, ViewSet viewSet,
		      boolean isFindTokenPath) {
      super( (PwPartialPlan) partialPlan, (PartialPlanViewSet) viewSet, isFindTokenPath);
    }

    public List getTokenRuleKeyList() {
      return tokenRuleKeyList;
    }

    public void setTokenRuleKeyList( List lst) {
      // System.err.println( "setTokenRuleKeyList " + lst);import gov.nasa.arc.planworks.viz.util.
      tokenRuleKeyList = lst;
    }

  } // end class FindTokenPath


  /**
   * <code>TokenNetworkJGoView</code> - subclass JGoView to add doBackgroundClick
   *
   */
  class TokenNetworkJGoView extends JGoView {

    /**
     * <code>TokenNetworkJGoView</code> - constructor 
     *
     */
    public TokenNetworkJGoView() {
      super();
    }

    /**
     * <code>doBackgroundClick</code> - Mouse-Right pops up menu:
     *                                 1) snap to active token
     *
     * @param modifiers - <code>int</code> - 
     * @param docCoords - <code>Point</code> - 
     * @param viewCoords - <code>Point</code> - 
     */
    public final void doBackgroundClick( final int modifiers, final Point docCoords,
                                         final Point viewCoords) {
      if (MouseEventOSX.isMouseLeftClick( modifiers, PlanWorks.isMacOSX())) {
        // do nothing
      } else if (MouseEventOSX.isMouseRightClick( modifiers, PlanWorks.isMacOSX())) {
        mouseRightPopupMenu( viewCoords);
      }
    } // end doBackgroundClick

  } // end class TokenNetworkJGoView


  private void mouseRightPopupMenu( final Point viewCoords) {
    String partialPlanName = partialPlan.getPartialPlanName();
    PwPlanningSequence planSequence = PlanWorks.getPlanWorks().getPlanSequence( partialPlan);
    JPopupMenu mouseRightPopup = new JPopupMenu();

    JMenuItem nodeByKeyItem = new JMenuItem( "Find by Key");
    createNodeByKeyItem( nodeByKeyItem);
    mouseRightPopup.add( nodeByKeyItem);

    JMenuItem findTokenPathItem = new JMenuItem( "Find Token Path");
    createFindTokenPathItem( findTokenPathItem);
    mouseRightPopup.add( findTokenPathItem);

    if (highlightPathNodesList != null) {
      JMenuItem highlightPathItem = new JMenuItem( "Highlight Current Path");
      createHighlightPathItem( highlightPathItem, highlightPathNodesList);
      mouseRightPopup.add( highlightPathItem);
    }

    createOpenViewItems( partialPlan, partialPlanName, planSequence, mouseRightPopup,
                         viewListenerList, ViewConstants.TOKEN_NETWORK_VIEW);

    JMenuItem overviewWindowItem = new JMenuItem( "Overview Window");
    createOverviewWindowItem( overviewWindowItem, this, viewCoords);
    mouseRightPopup.add( overviewWindowItem);

    JMenuItem raiseContentSpecItem = new JMenuItem( "Raise Content Filter");
    createRaiseContentSpecItem( raiseContentSpecItem);
    mouseRightPopup.add( raiseContentSpecItem);
    
    if (((PartialPlanViewSet) this.getViewSet()).getActiveToken() != null) {
      JMenuItem activeTokenItem = new JMenuItem( "Snap to Active Token");
      createActiveTokenItem( activeTokenItem);
      mouseRightPopup.add( activeTokenItem);
    }

    this.createZoomItem( jGoView, zoomFactor, mouseRightPopup, this);

    if ((viewSet.doesViewFrameExist( ViewConstants.NAVIGATOR_VIEW)) ||
        (viewSet.doesViewFrameExist( ViewConstants.RULE_INSTANCE_VIEW))) {
      mouseRightPopup.addSeparator();
    }
    if (viewSet.doesViewFrameExist( ViewConstants.NAVIGATOR_VIEW)) {
      JMenuItem closeNavWindowsItem = new JMenuItem( "Close Navigator Views");
      createCloseNavigatorWindowsItem( closeNavWindowsItem);
      mouseRightPopup.add( closeNavWindowsItem);
    }
    if (viewSet.doesViewFrameExist( ViewConstants.RULE_INSTANCE_VIEW)) {
      JMenuItem closeRuleWindowsItem = new JMenuItem( "Close Rule Instance Views");
      createCloseRuleWindowsItem( closeRuleWindowsItem);
      mouseRightPopup.add( closeRuleWindowsItem);
    }

    createAllViewItems( partialPlan, partialPlanName, planSequence, viewListenerList,
                        mouseRightPopup);

    ViewGenerics.showPopupMenu( mouseRightPopup, this, viewCoords);
  } // end mouseRightPopupMenu

  private void createActiveTokenItem( final JMenuItem activeTokenItem) {
    activeTokenItem.addActionListener( new ActionListener() {
        public final void actionPerformed( final ActionEvent evt) {
          PwToken activeToken =
            ((PartialPlanViewSet) TokenNetworkView.this.getViewSet()).getActiveToken();
          if (activeToken != null) {
            boolean isByKey = false;
            findAndSelectToken( activeToken, isByKey);
          }
        }
      });
  } // end createActiveTokenItem

  private void createNodeByKeyItem( final JMenuItem nodeByKeyItem) {
    nodeByKeyItem.addActionListener( new ActionListener() {
        public final void actionPerformed( final ActionEvent evt) {
          AskNodeByKey nodeByKeyDialog =
            new AskNodeByKey( "Find by Key", "key (int)", TokenNetworkView.this);
          Integer nodeKey = nodeByKeyDialog.getNodeKey();
          if (nodeKey != null) {
            boolean isByKey = true;
            // System.err.println( "createNodeByKeyItem: nodeKey " + nodeKey.toString());
            PwToken tokenToFind = partialPlan.getToken( nodeKey);
	    PwRuleInstance ruleInstanceToFind =  null;
 	    boolean isFound = false;
            if (tokenToFind != null) {
	      // look at already created tokens
              isFound = findAndSelectToken( tokenToFind, isByKey);
            } else {
	       // look at already created rule instances
              ruleInstanceToFind =  partialPlan.getRuleInstance( nodeKey);
              if (ruleInstanceToFind != null) {
                isFound = findAndSelectRuleInstance( ruleInstanceToFind);
              }
            }
	    if ((! isFound) && ((tokenToFind != null) || (ruleInstanceToFind != null))) {
	      PwEntity entityToFind = tokenToFind;
	      boolean entityIsToken =  true;
	      if (entityToFind == null) {
		entityToFind = ruleInstanceToFind;
		entityIsToken = false;
	      }
	      Iterator rootTokenItr = rootTokens.iterator();
	      while (rootTokenItr.hasNext()) {
		PwToken rootToken = (PwToken) rootTokenItr.next();
		tokenKey1 = rootToken.getId();
		tokenKey2 = entityToFind.getId();
		FindTokenPath findTokenPath =  getFindTokenPath();
		if (findTokenPath.getTokenRuleKeyList().size() != 0) {
		  List nodeList = renderTokenPathNodes( findTokenPath);
		  setLayoutNeeded();
		  setFocusNode( (ExtendedBasicNode) nodeList.get( nodeList.size() - 1));
		  redraw();
		  return;
		}
	      }
	      if (entityIsToken) {
		String message = "Token " + tokenToFind.getPredicateName() +
		  " (key=" + tokenToFind.getId().toString() + ") not found.";
		JOptionPane.showMessageDialog( PlanWorks.getPlanWorks(), message,
					       "Token Not Found in TokenNetworkView",
					       JOptionPane.ERROR_MESSAGE);
		System.err.println( message);
	      } else {
		String message = "RuleInstance 'rule " + ruleInstanceToFind.getRuleId() +
		  "' (key=" + ruleInstanceToFind.getId().toString() + ") not found.";
		JOptionPane.showMessageDialog( PlanWorks.getPlanWorks(), message,
					       "RuleInstance Not Found in TokenNetworkView",
					       JOptionPane.ERROR_MESSAGE);
		System.err.println( message);
	      }
	    }
          }
        }
      });
  } // end createNodeByKeyItem

  private FindTokenPath getFindTokenPath() {
    boolean isFindTokenPath = true;
    FindTokenPath findTokenPath =  new FindTokenPath( partialPlan, viewSet, isFindTokenPath);
    findTokenPath.setTokenRuleKeyList( null);
    findTokenPathDoit( findTokenPath);
    while (findTokenPath.getTokenRuleKeyList() == null) {
      try {
	Thread.currentThread().sleep( ViewConstants.WAIT_INTERVAL * 2);
      } catch (InterruptedException ie) {}
      // System.err.println("createFindTokenPathItemWorker wait for findTokenPath");
    }
    return findTokenPath;
  }

  private void findTokenPathDoit( final FindTokenPath findTokenPath) {
    final SwingWorker worker = new SwingWorker() {
	public Object construct() {
	  System.err.println( "findTokenPathDoit");
	  progressMonitorThread( "Finding Token Path ...", 0, 6, Thread.currentThread(),
				 findTokenPath);
	  if (! progressMonitorWait( TokenNetworkView.this)) {
	    System.err.println( "progressMonitorWait failed");
	    findTokenPath.setTokenRuleKeyList( new ArrayList());
	    return null;
	  }
	  progressMonitor.setProgress( 3 * ViewConstants.MONITOR_MIN_MAX_SCALING);

	  findTokenPath.setTokenRuleKeyList( partialPlan.getTokenNetworkPath( tokenKey1,
	  								      tokenKey2));
	  isProgressMonitorCancel = true;
	  return null;
	}
      };
    worker.start();  
  } // end findTokenPath

  private void createFindTokenPathItem( JMenuItem findTokenPathItem) {
    findTokenPathItem.addActionListener( new ActionListener() {
	public void actionPerformed( ActionEvent evt) {
	  final SwingWorker worker = new SwingWorker() {
	      public Object construct() {
		createFindTokenPathItemWorker();
		return null;
	      }
	    };
	  worker.start();
	}
      });
  } // createFindTokenPathItem

  private void createFindTokenPathItemWorker() {
    AskQueryTwoEntityKeys twoKeysDialog =
      new AskQueryTwoEntityKeys( "Enter Ids for Find Token Path", "token",
				 "start token key (int)", "token",
				 "end token key (int)", partialPlan);
    tokenKey1 = twoKeysDialog.getEntityKey1();
    tokenKey2 = twoKeysDialog.getEntityKey2();
    if ((tokenKey1 == null) || (tokenKey2 == null)) {
      return;
    }
    FindTokenPath findTokenPath =  getFindTokenPath();
    if (findTokenPath.getTokenRuleKeyList().size() == 0) {
      JOptionPane.showMessageDialog
	( PlanWorks.getPlanWorks(), "no path found for " + tokenKey1 + " => " +
	  tokenKey2, "Find Token Path Error", JOptionPane.ERROR_MESSAGE);
      return;
    }
    List nodeList = renderTokenPathNodes( findTokenPath);
    setLayoutNeeded();
    setFocusNode( null);
    highlightPathNodesList = nodeList;
    redraw();
    outputTokenPathNodes( nodeList);
  } // end createFindTokenPathItemWorker

  private List renderTokenPathNodes( FindTokenPath findTokenPath) {
    List nodeList =  new ArrayList();
      Iterator tokenRuleItr = findTokenPath.getTokenRuleKeyList().iterator();
      PwToken token = null; PwRuleInstance ruleInstance = null;
      while (tokenRuleItr.hasNext()) {
	Integer tokenRuleKey = (Integer) tokenRuleItr.next();
	// System.err.println( "key " + tokenRuleKey);
	if ((token = partialPlan.getToken( tokenRuleKey)) != null) {
	  TokenNetworkTokenNode tokenNode = addTokenTokNetNode( token);
	  nodeList.add( tokenNode);
	  if (! tokenNode.areNeighborsShown()) {
	    tokenNode.addTokenObjects( tokenNode);
	    tokenNode.setAreNeighborsShown( true);
	  }
	} else if ((ruleInstance = partialPlan.getRuleInstance( tokenRuleKey)) != null) {
	  TokenNetworkRuleInstanceNode ruleInstanceNode =
	    addRuleInstanceTokNetNode( ruleInstance);
	  nodeList.add( ruleInstanceNode);
	  if (! ruleInstanceNode.areNeighborsShown()) {
	    ruleInstanceNode.addRuleInstanceObjects( ruleInstanceNode);
	    ruleInstanceNode.setAreNeighborsShown( true);
	  }
	}
      }
      return nodeList;
  } // end renderTokenPathNodes

  private void createHighlightPathItem( final JMenuItem highlightPathItem,
					final List nodeList) {
    highlightPathItem.addActionListener( new ActionListener() {
	public void actionPerformed(ActionEvent evt) {
	  NodeGenerics.highlightPathNodes( nodeList, jGoView);
	  outputTokenPathNodes( nodeList);
	}
      });
  } // end createHighlightPathItem

  private void outputTokenPathNodes( List nodeList) {
    System.err.print( "Found Token Path ");
    StringBuffer nodeBuffer = new StringBuffer( "(");
    nodeBuffer.append( TokenNetworkView.this.getName()).append( ") => ");
    Iterator nodeItr = nodeList.iterator();
    while (nodeItr.hasNext()) {
      ExtendedBasicNode node = (ExtendedBasicNode) nodeItr.next();
      Integer nodeId = null;
      if (node instanceof TokenNetworkTokenNode) {
	nodeId = ((TokenNetworkTokenNode) node).getToken().getId();
      } else if (node instanceof TokenNetworkRuleInstanceNode) {
	nodeId = ((TokenNetworkRuleInstanceNode) node).getRuleInstance().getId();
      }
      nodeBuffer.append( nodeId).append( " ");
    }
    System.err.println( nodeBuffer.toString());
    MessageDialog msgDialog = // non-modal
      new MessageDialog( PlanWorks.getPlanWorks(), "Found Token Path",
			 nodeBuffer.toString());
  } // end outputTokenPathNodes

  /**
   * <code>findAndSelectToken</code>
   *
   * @param tokenToFind - <code>PwToken</code> - 
   * @param isByKey - <code>boolean</code> - 
   * @return - <code>boolean</code> - 
   */
  public final boolean findAndSelectToken( final PwToken tokenToFind, final boolean isByKey) {
    boolean isTokenFound = false;
    boolean isHighlightNode = true;
    List tokenNodeList = new ArrayList( tokenNodeMap.values());
    Iterator tokenNodeListItr = tokenNodeList.iterator();
    while (tokenNodeListItr.hasNext()) {
      TokenNetworkTokenNode tokenNode = (TokenNetworkTokenNode) tokenNodeListItr.next();
      if ((tokenNode.getToken() != null) &&
          (tokenNode.getToken().getId().equals( tokenToFind.getId()))) {
        System.err.println( "TokenNetworkView found token: " +
                            tokenToFind.getPredicateName() +
                            " (key=" + tokenToFind.getId().toString() + ")");
        focusNodeId = tokenNode.getToken().getId();
        NodeGenerics.focusViewOnNode( tokenNode, isHighlightNode, jGoView);
        isTokenFound = true;
        break;
      }
    }
    if (isTokenFound && (! isByKey)) {
      NodeGenerics.selectSecondaryNodes
        ( NodeGenerics.mapTokensToTokenNodes
          ( ((PartialPlanViewSet) TokenNetworkView.this.getViewSet()).getSecondaryTokens(),
           tokenNodeList),
          jGoView);
    }
//     if (! isTokenFound) {
//       // Content Spec filtering may cause this to happen
//       String message = "Token " + tokenToFind.getPredicateName() +
//         " (key=" + tokenToFind.getId().toString() + ") not found.";
//       JOptionPane.showMessageDialog( PlanWorks.getPlanWorks(), message,
//                                      "Token Not Found in TokenNetworkView",
//                                      JOptionPane.ERROR_MESSAGE);
//       System.err.println( message);
//     }
    return isTokenFound;
  } // end findAndSelectToken

  /**
   * <code>findAndSelectRuleInstance</code>
   *
   * @param ruleInstanceToFind - <code>PwRuleInstance</code> - 
   * @return - <code>boolean</code> - 
   */
  public final boolean findAndSelectRuleInstance( final PwRuleInstance ruleInstanceToFind) {
    boolean isRuleInstanceFound = false;
    boolean isHighlightNode = true;
    List ruleInstanceNodeList = new ArrayList( ruleInstanceNodeMap.values());
    Iterator ruleInstanceNodeListItr = ruleInstanceNodeList.iterator();
    while (ruleInstanceNodeListItr.hasNext()) {
      TokenNetworkRuleInstanceNode ruleInstanceNode =
        (TokenNetworkRuleInstanceNode) ruleInstanceNodeListItr.next();
      if ((ruleInstanceNode.getRuleInstance() != null) &&
          (ruleInstanceNode.getRuleInstance().getId().equals( ruleInstanceToFind.getId()))) {
        System.err.println( "TokenNetworkView found ruleInstance: rule " +
                            ruleInstanceToFind.getRuleId() +
                            " (key=" + ruleInstanceToFind.getId().toString() + ")");
        focusNodeId = ruleInstanceNode.getRuleInstance().getId();
        NodeGenerics.focusViewOnNode( ruleInstanceNode, isHighlightNode, jGoView);
        isRuleInstanceFound = true;
        break;
      }
    }
//     if (! isRuleInstanceFound) {
//       // Content Spec filtering may cause this to happen
//       String message = "RuleInstance 'rule " + ruleInstanceToFind.getRuleId() +
//         "' (key=" + ruleInstanceToFind.getId().toString() + ") not found.";
//       JOptionPane.showMessageDialog( PlanWorks.getPlanWorks(), message,
//                                      "RuleInstance Not Found in TokenNetworkView",
//                                      JOptionPane.ERROR_MESSAGE);
//       System.err.println( message);
//     }
    return isRuleInstanceFound;
  } // end findAndSelectRuleInstance

  private void createOverviewWindowItem( final JMenuItem overviewWindowItem,
                                         final TokenNetworkView tokenNetworkView,
                                         final Point viewCoords) {
    overviewWindowItem.addActionListener( new ActionListener() { 
        public final void actionPerformed( final ActionEvent evt) {
          VizViewOverview currentOverview =
            ViewGenerics.openOverviewFrame( ViewConstants.TOKEN_NETWORK_VIEW, partialPlan,
                                            tokenNetworkView, viewSet, jGoView, viewCoords);
          if (currentOverview != null) {
            overview = currentOverview;
          }
        }
      });
  } // end createOverviewWindowItem



} // end class TokenNetworkView











