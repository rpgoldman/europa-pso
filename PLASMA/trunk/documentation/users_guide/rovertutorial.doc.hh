/** * @page rovertutorial Rover Tutorial *  * We introduced a simple EUROPA2 application based upon a planetary rover in the Quick Start Guide. * We will revisit that is example in this tutorial and cover it in * much greater detail so that you will be able to begin writing your own EUROPA2 applications. * We begin by introducing the goal of the application before stepping * through the stages of creating a EUROPA2 application beginning with an analysis of the application * to identify timelines and token types, then moving through the * NDDL encoding, and finally stepping through the planner's operation on the domain.  * * * @section rovertutorialapplicatonbackground Overview of the Simple Planetary Rover Application * *  NASA's Mars Exploratory Rover mission is operating two rovers on the surface of Mars. The rovers are *  moving to rocks of interest identified by scientists on earth *  and then collecting a variety of scientific data by placing instruments and transmitting the  *  data back to eart. We are going to write the planning application for controlling a simplified  * version of these rovers.  * * The figure below shows NASA Ames' K9 rover that is used to experiment with advanced control concepts  * for future Mars rover missions. K9 is operating in a simulated * Martian landscape (called Marscape) where it is to navigate to target rocks and collect data.  * The second picture shows K9 placing its sensor on a rock.  * * @image html k9-rover-overview-photo.jpg K9 Rover at NASA Ames' Marscape while controlled by a EUROPA2 planner * * @image html k9-sensor-on-rock-photo.jpg K9 Rover Placing a sensor at NASA Ames' Marscape  *  * @section rovertutorialdomainanalysis Application Domain Analysis * * Developing EUROPA2 applications is a design task that will require judgment and multiple iterations.  * We will follow the steps in this tutorial that we have  * found useful in practice. The overall approach is to gradually build up a domain description  * adding detail methodically. This approach controls complexity by allowing * the domain writer to focus only on well defined issues at a given instant.   * * The first stage is to draw a concept map of the entities in the application domain and their relationships. The figure below shows our diagram for the rover domain. We have * identified locations and the paths between them as the key environment entities. The rover itself has been divided in subcomponents. The navigator concept manages the  * location of the rover. The instrument concept will manage the instruments for sampling rocks and the commands concept looks after instructions from the scientists that  * the rover will serve. Finally the battery concept is included to provide a place for managing the power used by the other components of the rover. The Lander corresponds to the vehicle used to deliver the  * rover to the planet surface. The Lander also provides a communication service that the rover can use * to transmit information to earth.  *  * @image html rover-application-concept-diagram.jpg Rover Application Concept Diagram *  * The next decision is to identify the entities that will become timelines and to specify the predicates that will be placed on them. The rover in our domain is the  * actor we will be planning for and will contain all the timelines. Analyzing the components of the rover produces the following breakdown of timelines and predicates. The easiest way to identify the predicates * is to think through the lifecycle of each timeline. Think about what states it can be in.  * * @image html rover-tutorial-initial-timelines.jpg Initial Timelines and Predicates * * Working from the top of the figure downwards  * <ul> * <li>The navigator time line controls the rovers navigation between places. We have determined only  * two predicates for this timeline.  * The rover is <i>at</i> a location or it <i>going</i> between locations.  * <li> The instrument can be stowed away and can be in the state of being stowed or being unstowed. The instrument may also be placed on a target where it can then * take a sample * <li> The commands capture instructions from the scientist. The rover can be instructed to take a sample or to upload its data to the lander or directly to earth.  *</ul> * As noted earlier, identifying the timelines and predicates is an iterative process. It would not be unusual to find new timelines while identifying predicates or to  * discover that two timelines could be collapsed into one. Please iterate and be comfortable with experimentation. * * The next stage is to flesh out the properties of the predicates and the constraints between them. To lets begin by adding these elements to the figure above (we've  * omitted the properties of the instrument's predicates for clarity). We focus initial on just the transitions between predicates within a timeline not the constraints * between predicates on different timelines. The black circle indicates a startup state for the timeline * while the black circle with an outer circle indicates a terminal state.  * * @image html rover-tutorial-navigation-timeline.jpg Timelines and Predicates with Transitions between Predicates on each Timeline.  * * The meaning of the state transitions added to this figure are intuitive. The next stage is to consider the constraints between predicates on different time lines.  * So far we have only used the notion state transitions to connect predicates. These map to the temporal relations of <i>meets</i> and <i>met by</i> and are sufficient * for a timelines where only one predicate instance can occur at any given moment. When we start to connect predicates between timelines we need to use the full range * of temporal relations as we begin to deal with concurrent states.  * * @image html rover-tutorial-timeline-interaction.jpg Iterations Between Timelines. *  * We have only one interaction between timelines in this application. The <i>take sample</i> predicate requires that the navigation timeline be <i>at</i> the location  * throughout its lifetime. We are identifying the common sense constraint that we have to be at and remain at a location in order to sample it.  * * We now have the main concepts in the application identified and categorized and we are ready to start encoding it in NDDL.  * * @section rovertutorialnddl NDDL Encoding - Model *  * Our encoding of the Rover domain is available in the <i>Examples/SimpleRover</i> directory. We will step through each line of that file and explain how it was derived * form our analysis above and the NDDL constructs that were used. * * The first two lines are the default include statements that we meet in the <i>HelloRover</i> tutorial.  * Please disregard them for now. The first substantive area  * is the encoding of the <i>location</i> class.  * We will allow the rover * to be at locations which may or may not be rocks. The <i>location</i> class has three attributes. The <i>name</i> is a symbolic name for the location. The <i>x</i> and <i>y</i> * attributes are coordinates. We have decided to position all our locations on a Euclidean Plane. The second part of the class specifies the constructor. The constructor defines * how the attributes of a location are initialized when a new instance is created. We are simply providing a signature that allows us to create locations with the name * and coordinates specified. The constructor's job is simply to copy those initial values into the correct member variables.  * * <i> *  class Location { <br> * string name; <br> * int x; <br> * int y; <br> *  * Location(string _name, int _x, int _y){ <br> *  name = _name; <br> *  x = _x; <br> *  y = _y; <br> * }  <br> *} <br> * </i> * * The <i>Path</i> class follows a similar pattern to the <i>Location</i> class. Paths have a symbolic name and a * pair of locations that they connect. The <i>cost</i> parameter will be used to compute the amount of battery * power that will be consumed while traversing a path. The constructor's role is again just to take initial  * values for each of a <i>path's</i> attributes. * * <i> * class Path { <br> * string name; <br> * Location from; <br> * Location to; <br> * float cost; <br> * Path(string _name, Location _from, Location _to, float _cost){ <br> *  name = _name; <br> *  from = _from; <br> *   to = _to; <br> *   cost = _cost; <br> * } <br> *} <br> * </i> * * We next encode the <i>Battery</i> that will be used to power the rover. EUROPA2 provides a <i>Resource</i> class for modeling items like batteries and * fuel cells that have a numeric capacity that is produced and consumed during a plan. The <i>Battery</i> class specializes the general <i>Resource</i> class. * It takes four arguments. An initial capacity <i>ic</i>, a minimum charge level <i>ll_min</i> and a maximum charge level <i>ll_max</i>. The <i>Battery</i> * constructor delegates the creation of an instance to its parent or super class, the general <i>Resource</i> class.  * *<i> * class Battery extends Resource { <br> *  Battery(float ic, float ll_min, float ll_max){ <br> *   super(ic, ll_min, ll_max, 0.0, 0.0, MINUS_INFINITY, MINUS_INFINITY);  <br> *  } <br> *} <br> *</i> * * We now move on to encode the component of the rover. Lets begin with the rover's navigator. This class contains the two predicates we identified earlier. * The <i>At</i> predicate models the concept of the rover being at a particular location. The <i>Going</i> predicate models the concept of moving between * locations. The <i>neq</i> construct is a constraint that ensures the rover does not attempt to traverse a path that start and finishes in the same location. *  * <i> * class Navigator <br> * {<br> *   predicate At{<br> *     Location location; <br> * } <br> * <br> * (predicate Going{<br> *  Location from; <br> *  Location to; <br> *  neq(from, to); <br> *  } <br> * } <br> * </i> *  * The next element encodes the detail of the <i>At</i> predicate. The first constraint is a <i>met_by</i> that specifies instances of this predicate will be * proceeded by an instance of the <i>Going</i> predicate. Will name that predecessor as <i>from</i>. We then post the constraint that the <i>from</i> predicate's * <i>to</i> attribute is equal to the <i>location</i> attribute of this predicate. We are just saying that a preceding <i>Going</i> predicate must end at * the location of this <i>At</i>. The third constraint specifies that an <i>At</i> predicate will be followed by a <i>Going</i> predicate. That <i>from</i> * location of that succeeding <i>Going</i> predicate must be set this location.  * * <i> * Navigator::At{ <br> * met_by(object.Going from); <br> * eq(from.to, location); <br> * meets(object.Going to); <br> * eq(to.from, location); <br> * }</i> * * We next specify the details of the <i>Going</i> predicate. It first mirrors the <i>At</i> predicates constraints in ensuring that a <i>Going</i> * is always preceded and followed by a <i>At</i> predicate and that the <i>to</i> and <i>from</i> attributes are synchronized correctly. The <i>Path</i> * element models the constraint that <i>Going</i> predicate must pass along a path that connects to locations being traversed. Finally, the battery * components specifies that the change in battery level caused by a <i>Going</i> predicate is equal to the cost associated with the path selected.  * * <i> * Navigator::Going{ <br> * met_by(object.At _from); <br> * eq(_from.location, from); <br> * meets(object.At _to); <br> * eq(_to.location, to);  <br> * <br> * Path p;<br> * eq(p.from, from); <br> * eq(p.to, to); <br> * <br> * starts(Battery.change tx); <br> * eq(tx.quantity, p.cost); <br> *} <br> * </i> *  * The next section specifies the Commands object for handling instructions from the scientist user to take samples (<i>TakeSample</i>) or transmit information * back to the lander (<i>PhoneLander</i>) or earth (<i>PhoneEarth</i>).  * * <i> * class Commands{ <br> * predicate TakeSample{ <br> *  Location rock; <br> *  eq(duration, [20 25]); <br> * } <br> *    * predicate PhoneHome{} <br> *   * predicate PhoneLander{} <br>  * } <br> * </i> * * The next section details the constraint on each of the <i>Commands</i> class' predicates. Lets take <i>TakeSample</i> first. Much of the definition * will be familiar to you from the previous predicates. The <i>contains</i> constraint ensures that an <i>Instrument</i> class' <i>TakeSample</i> action * must occur at sometime during this predicate. The next line names the rock that that sample will occur at as <i>rock</i>. It also constrains the * <i>Instrument</i> instance used to share the same parent (i.e. rover) as this class. This ensures that commands to a specific rover are only executed * on that rover. The condition allows the predicate to either meet a <i>PhoneHome</i> or <i>PhoneLander</i> predicate to transmit the results of the  * sampling back to earth. Either way can be used by the planner unless we set specify the value of the <i>OR</i> variable.  *  * <i> * Commands::TakeSample{ <br> * contains(Instrument.TakeSample a); <br> * eq(a.rock, rock);  <br> * Rover rovers; <br> * commonAncestor(a.object, this.object, rovers); <br> * <br> * bool OR; <br> * <br> * if(OR == false){ <br> *  meets(object.PhoneHome t0); <br> * }<br> * if(OR == true){ <br> *  meets(object.PhoneLander t1); <br> * } <br> * } <br> * </i> * * The <i>PhoneHome</i> and <i>PhoneLander</i> predicates are specified next. Both * use battery power. <i>PhoneHome</i> is much more expensive as the signal needs to be transmitted back to earth requiring much more power.   * * <i> *Commands::PhoneHome{ <br> * starts(Battery.change tx); <br> * eq(tx.quantity, -600); <br> *} <br> * <br> *Commands::PhoneLander{ <br> *starts(Battery.change tx); <br> *eq(tx.quantity, -20); <br> *} <br> *</i> * * The next section details the management of the rover's instruments for taking samples and for keeping the instrument safely stowed while moving.   * The first predicate is <i>TakeSample</i> and it is constrained to take at most ten time units. * * <i> * class Instrument{ <br> * predicate TakeSample{ <br> * Location rock; <br> * leq(10, duration); <br> * } <br> * </i> * * <i>Place</i> models the process of putting the instrument on the rock once the rover has driven up to it. This is constrained to take between three and  * twelve time units.  * *<i> * predicate Place{ <br> *  Location rock; <br> *  eq([3 12], duration); <br> * } <br> *</i> * * We then model the actions for stowing the instrument safely and for the actions of stowing it and unstowing it. Both actions take between two and six time units. * * <i> *predicate Stow{ <br> * eq([2 6], duration); <br> *} <br> * * predicate Unstow{ <br> * eq([2 6], duration); <br> *} <br> * * predicate Stowed{} <br> * } <br> *</i> * * With the predicates defined we now move on to specify the detailed constraints on each beginning with <i>TakeSample</i>. We first constrain the predicate * to occur while the rover's navigator it at the location of the rock. The <i>comonAncestor</i> constraint is used to ensure that we refer to the same * rover for the location and sample taking. The action must be proceeded by a <i>Place</i> predicate and succeeded by a <i>Stow</i> predicate. The predicate * consumes one hundred and twenty units of battery power.  * * <i> * Instrument::TakeSample{ <br> * contained_by(Navigator.At at); <br> * eq(at.location, rock); <br> * Rover rovers; <br> * commonAncestor(at.object, this.object, rovers); <br> * <br> * met_by(Place b); <br> * eq(b.rock, rock); <br> * <br> * meets(Stow c); <br> * <br> * starts(Battery.change tx); <br> * eq(tx.quantity, -120); <br> * } <br> * </i> * * The <i>Place</i> predicate is similar to the <i>TakeSample</i> predicate.  * * <i> * Instrument::Place{<br> * contained_by(Navigator.At at); <br> * eq(at.location, rock); <br> * Rover rovers;<br> * commonAncestor(at.object, this.object, rovers); <br> * <br> * meets(TakeSample a);<br> * eq(a.rock, rock);<br> * met_by(Unstow b); <br> * <br> * starts(Battery.change tx); <br> * eq(tx.quantity, -20); <br> * }  <br>* </i>** <i>Unstow</i> ensures that it only occurs while the navigator is stationary and it is proceeded by a <i>Stowed</i> predicate as it is not possible to * unstow the instrument from any other state.** <i>* Instrument::Unstow{ <br>*  contained_by(Navigator.At at);  <br>* Rover rovers;  <br>* commonAncestor(at.object, this.object, rovers);  <br>*  <br>* meets(Place a);  <br>*  met_by(Stowed b);  <br>*  <br>* starts(Battery.change tx);  <br>* eq(tx.quantity, -20);  <br>* }  <br>* </i>* * The <i>Stow</i> predicate ensure that is followed by a <i>Stowed</i> predicate and succeeds a <i>TakeSample</i> predicate.  ** <i>* Instrument::Stow{  <br>* contained_by(Navigator.At at);  <br>*  Rover rovers;  <br>* commonAncestor(at.object, this.object, rovers);  <br>*  <br>*  meets(Stowed a);  <br>*  met_by(TakeSample b);  <br>*  <br>* starts(Battery.change tx);  <br>* eq(tx.quantity, -20);  <br>* }  <br>*</i> ** <i>Stowed</i> just ensures that it sits between <i>Stow</i> and <i>Unstow</i> predicates. ** <i>* Instrument::Stowed{*  met_by(Stow a);* meets(Unstow b);* }* </i>** The <i>Rover</i> class pulls together all the components we have defined so far. It has an attribute for the navigator, instrument, and * battery classes. The constructer takes an instance of the Battery class and creates instances of the other classes to setup the rover. ** <i>* class Rover {  <br>* Commands commands;  <br>*  Navigator navigator;  <br>*  Instrument instrument;  <br>*  Battery mainBattery;  <br>*   <br>*  Rover(Battery r){  <br>*  commands = new Commands();  <br>*  navigator = new Navigator();  <br>*  instrument = new Instrument();  <br>*   mainBattery = r; <br>*  } <br>*} <br>*</i>** We have now completed the NDDL modeling we need to describe our application domain and it is now possible to describe problems that we want our planner to * solve.** @section rovertutorialnddlinitalState NDDL Encoding - Initial State** The initial state file contains an example planning problem with a specific set of locations and paths. The first line of the file just includes the domain* model file we detailed in the previous section. ** <i>* #include "./SimpleRover-model.nddl"* </i>** The <i>PlannerConfig</i> sets the planning horizon to between 0 and 100 units and gives the planner 600 search steps to search for a solution. * <i>* PlannerConfig world = new PlannerConfig(0, 100, 600);* </i>** The environment is configured to have five locations. Four rocks and a single lander. * * <i>* Location lander = new Location("LANDER", 0, 0); <br>* Location rock1 = new Location("ROCK1", 9, 9); <br>* Location rock2 = new Location("ROCK2", 1, 6); <br>* Location rock3 = new Location("ROCK3", 4, 8); <br>* Location rock4 = new Location("ROCK4", 3, 9); <br>* </i>** We define three paths that lead from the lander to the location named <i>rock4</i>. The paths* vary considerably in the amount of battery energy needed to traverse them. ** <i>* Path p1 = new Path("Very Long Way", lander, rock4, -2000.0); <br>* Path p2 = new Path("Moderately Long Way", lander, rock4, -1500.0); <br>* Path p3 = new Path("Short Cut", lander, rock4, -400.0); <br>* </i>** We define a single battery with an initial and maximum capacity of 1000 units. The battery may be drained as low as 0 units.** <i>* Battery battery = new Battery(1000.0, 0.0, 1000.0); <br>* </i>** We define a single rover, <i>spirit</i> and pass it the battery we just defined. ** <i>* Rover spirit = new Rover(battery); <br>* </i>** We have now defined all the objects in our domain and we can close both the database and begin specifying the state of the world.** <i>* Resource.close(); <br>* close(); <br>* </i>** We define the initial state by creating an <i>At</i> token called <i>initialPosition</i>. It is constrained to start at the same time the* planning horizon starts and the location attribute is set to being the <i>lander</i>. The result is to place the rover at the lander at time zero.* * <i>* goal(Navigator.At initialPosition); <br>* eq(initialPosition.start, world.m_horizonStart);<br>* eq(initialPosition.location, lander); <br>* </i>** We define the goal as taking a sample of <i>rock4</i> starting at time 50. * * <i>* goal(Commands.TakeSample sample); <br>* sample.start.specify(50); <br>* sample.rock.specify(rock4); <br>*</i>* * The initial state is completed by setting the initial state of the instrument to stowed. **<i>rejectable(Instrument.Stowed stowed); <br>eq(stowed.start, world.m_horizonStart); <br></i>** With the objects in our world setup and the goal the planner is to bring about specified it is time to start planning. The next section traces* the operation of EUROPA2's planner on this problem.* * @section rovertutorialplanning Planning * * To be completed once Solver is stable enough to document.  * * @section rovertuotrialsummary Summary * * You are now familiar with the process of analyzing an application domain and encoding it in NDDL and tracing the operation of the planner on it.  * This completes the introductory tutorial for EUROPA2. We hope you enjoy developing your own applications. Please use the reference guides * for more detailed questions and do not hesitate to contact the EUROPA2 team if you get stuck. The contact address is at the bottom of each * page of this documentation. * * Happy planning! */