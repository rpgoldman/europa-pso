#ifndef _H_Constraints
#define _H_Constraints

#include "ConstraintEngineDefs.hh"
#include "Constraint.hh"
#include "Variable.hh"
#include "IntervalDomain.hh"
#include "IntervalIntDomain.hh"
#include "BoolDomain.hh"

namespace EUROPA {

  /**
   * @brief Maintains a unary relation from a constant to a variable such that the variable
   * is a subset of the given constant.
   */
  class UnaryConstraint : public Constraint {
  public:
    /**
     * @brief Specialized constructor
     */
    UnaryConstraint(const AbstractDomain& dom, const ConstrainedVariableId& var); 

    /**
     * @brief Standard constructor
     */
    UnaryConstraint(const LabelStr& name,
		    const LabelStr& propagatorName,
		    const ConstraintEngineId& constraintEngine,
		    const std::vector<ConstrainedVariableId>& variables);

    ~UnaryConstraint();

  private:

    void handleExecute();

    void handleDiscard();

    bool canIgnore(const ConstrainedVariableId& variable,
		   int argIndex,
		   const DomainListener::ChangeType& changeType);

    void setSource(const ConstraintId& sourceConstraint);

    AbstractDomain* m_x;
    AbstractDomain* m_y;
  };

  class AddEqualConstraint : public Constraint {
  public:
    AddEqualConstraint(const LabelStr& name,
		       const LabelStr& propagatorName,
		       const ConstraintEngineId& constraintEngine,
		       const std::vector<ConstrainedVariableId>& variables);

    void handleExecute();

  private:

    AbstractDomain& m_x;
    AbstractDomain& m_y;
    AbstractDomain& m_z;

    static const int X = 0;
    static const int Y = 1;
    static const int Z = 2;
    static const int ARG_COUNT = 3;
  };

  class EqualConstraint : public Constraint {
  public:
    EqualConstraint(const LabelStr& name,
		    const LabelStr& propagatorName,
		    const ConstraintEngineId& constraintEngine,
		    const std::vector<ConstrainedVariableId>& variables);

    void handleExecute();

    /**
     * @brief Accessor required for EqualityConstraintPropagator.
     */
    static AbstractDomain& getCurrentDomain(const ConstrainedVariableId& var);

  private:
    bool equate(const ConstrainedVariableId& v1, const ConstrainedVariableId& v2, bool& isEmpty);
    const unsigned int m_argCount;
  };

  class SubsetOfConstraint : public Constraint {
  public:
    SubsetOfConstraint(const LabelStr& name,
		       const LabelStr& propagatorName,
		       const ConstraintEngineId& constraintEngine,
		       const std::vector<ConstrainedVariableId>& variables);

    ~SubsetOfConstraint();

    void handleExecute();

    bool canIgnore(const ConstrainedVariableId& variable,
		   int argIndex,
		   const DomainListener::ChangeType& changeType);

  private:
    AbstractDomain& m_currentDomain;
    AbstractDomain& m_superSetDomain;
  };

  class LockConstraint : public Constraint {
  public:
    LockConstraint(const LabelStr& name,
		   const LabelStr& propagatorName,
		   const ConstraintEngineId& constraintEngine,
		   const std::vector<ConstrainedVariableId>& variables);

    ~LockConstraint();

    void handleExecute();

    const AbstractDomain& getDomain() const;

  private:
    AbstractDomain& m_currentDomain;
    AbstractDomain& m_lockDomain;
  };

  class LessThanEqualConstraint : public Constraint {
  public:
    LessThanEqualConstraint(const LabelStr& name,
			    const LabelStr& propagatorName,
			    const ConstraintEngineId& constraintEngine,
			    const std::vector<ConstrainedVariableId>& variables);

    void handleExecute();

    bool canIgnore(const ConstrainedVariableId& variable,
		   int argIndex,
		   const DomainListener::ChangeType& changeType);

  private:
    bool testIsRedundant(const ConstrainedVariableId& var = ConstrainedVariableId::noId()) const;

    AbstractDomain& m_x;
    AbstractDomain& m_y;
    static const int X = 0;
    static const int Y = 1;
    static const int ARG_COUNT = 2;
  };

  class NotEqualConstraint : public Constraint {
  public:
    NotEqualConstraint(const LabelStr& name,
		       const LabelStr& propagatorName,
		       const ConstraintEngineId& constraintEngine,
		       const std::vector<ConstrainedVariableId>& variables);

    void handleExecute();

    bool canIgnore(const ConstrainedVariableId& variable,
		   int argIndex,
		   const DomainListener::ChangeType& changeType);
    /**
     * @brief Helper method to do domain comparisons, and process removals if necessary
     */
    static bool checkAndRemove(const AbstractDomain& domx, AbstractDomain& domy);

  private:
    static const int X = 0;
    static const int Y = 1;
    static const int ARG_COUNT = 2;
  };

  /**
   * Enforces the relation x < y
   */

  class LessThanConstraint : public Constraint {
  public:
    LessThanConstraint(const LabelStr& name,
                       const LabelStr& propagatorName,
                       const ConstraintEngineId& constraintEngine,
                       const std::vector<ConstrainedVariableId>& variables);


    void handleExecute();

    bool canIgnore(const ConstrainedVariableId& variable,
		   int argIndex,
		   const DomainListener::ChangeType& changeType);

  private:
    static const int X = 0;
    static const int Y = 1;
    static const int ARG_COUNT = 2;
  };

  class MultEqualConstraint : public Constraint {
  public:
    MultEqualConstraint(const LabelStr& name,
			const LabelStr& propagatorName,
			const ConstraintEngineId& constraintEngine,
			const std::vector<ConstrainedVariableId>& variables);

    void handleExecute();

    /**
     * @brief Helper method to compute new bounds for both X and Y in X*Y == Z.
     * @return True if the target domain was modified.
     */
    static bool updateMinAndMax(IntervalDomain& targetDomain,
				double denomMin, double denomMax,
				double numMin, double numMax);
  private:
    static const int X = 0;
    static const int Y = 1;
    static const int Z = 2;
    static const int ARG_COUNT = 3;
  };

  class AddMultEqualConstraint : public Constraint {
  public:
    AddMultEqualConstraint(const LabelStr& name,
			   const LabelStr& propagatorName,
			   const ConstraintEngineId& constraintEngine,
			   const std::vector<ConstrainedVariableId>& variables);

    ~AddMultEqualConstraint();
  private:
    // All the work is done by the member constraints
    inline void handleExecute() { }
    void handleDiscard();

    static const int A = 0;
    static const int B = 1;
    static const int C = 2;
    static const int D = 3;
    static const int ARG_COUNT = 4;

    Variable<IntervalDomain> m_interimVariable;
    MultEqualConstraint m_multEqualConstraint;
    AddEqualConstraint m_addEqualConstraint;
  };

  /**
   * @class EqualSumConstraint
   * @brief A = B + C where B and C can each be sums.
   * Converted into an AddEqualConstraint and/or two EqSumConstraints with fewer variables.
   */
  class EqualSumConstraint : public Constraint {
  public:
    EqualSumConstraint(const LabelStr& name,
                       const LabelStr& propagatorName,
                       const ConstraintEngineId& constraintEngine,
                       const std::vector<ConstrainedVariableId>& variables);

    ~EqualSumConstraint();

  private:

    // All the work is done by the member constraints
    inline void handleExecute() { }

    void handleDiscard();

    const unsigned int ARG_COUNT;

    ConstraintId m_eqSumC1, m_eqSumC2, m_eqSumC3, m_eqSumC4, m_eqSumC5;
    Variable<IntervalDomain> m_sum1, m_sum2, m_sum3, m_sum4;
  };

  /**
   * @class EqualProductConstraint
   * @brief A = B * C where B and C can each be products.
   * Converted into an AddEqualConstraint and/or two EqProductConstraints with fewer variables.
   */
  class EqualProductConstraint : public Constraint {
  public:
    EqualProductConstraint(const LabelStr& name,
                           const LabelStr& propagatorName,
                           const ConstraintEngineId& constraintEngine,
                           const std::vector<ConstrainedVariableId>& variables);

    ~EqualProductConstraint();

  private:

    // All the work is done by the member constraints
    inline void handleExecute() { }

    void handleDiscard();

    const unsigned int ARG_COUNT;

    ConstraintId m_eqProductC1, m_eqProductC2, m_eqProductC3, m_eqProductC4, m_eqProductC5;
    Variable<IntervalDomain> m_product1, m_product2, m_product3, m_product4;
  };

  /**
   * @class LessOrEqThanSumConstraint
   * @brief A <= B + C + ...
   * Converted into two constraints: A <= temp and temp equal to the sum of the rest.
   */
  class LessOrEqThanSumConstraint : public Constraint {
  public:
    LessOrEqThanSumConstraint(const LabelStr& name,
                              const LabelStr& propagatorName,
                              const ConstraintEngineId& constraintEngine,
                              const std::vector<ConstrainedVariableId>& variables);

    ~LessOrEqThanSumConstraint();

  private:
    void handleExecute();
    void handleDiscard();
    Variable<IntervalDomain> m_interimVariable;
    LessThanEqualConstraint m_lessOrEqualConstraint;
    ConstraintId m_eqSumConstraint;
  };

  /**
   * @class LessThanSumConstraint
   * @brief A < B + C + ...
   * Converted into two constraints: A < temp and temp equal to the sum of the rest.
   */
  class LessThanSumConstraint : public Constraint {
  public:
    LessThanSumConstraint(const LabelStr& name,
                          const LabelStr& propagatorName,
                          const ConstraintEngineId& constraintEngine,
                          const std::vector<ConstrainedVariableId>& variables);

    ~LessThanSumConstraint() {
      discard(false);
    }

  private:

    // All the work is done by the member constraints
    inline void handleExecute() { }

    void handleDiscard(){
      Constraint::handleDiscard();
      m_interimVariable.discard();
      m_lessThanConstraint.discard();
      m_eqSumConstraint->discard();
    }

    Variable<IntervalDomain> m_interimVariable;
    LessThanConstraint m_lessThanConstraint;
    ConstraintId m_eqSumConstraint;
  };

  /**
   * @class GreaterOrEqThanSumConstraint
   * @brief A >= B + C + ...
   * Converted into two constraints: A >= temp and temp equal to the sum of the rest.
   */
  class GreaterOrEqThanSumConstraint : public Constraint {
  public:
    GreaterOrEqThanSumConstraint(const LabelStr& name,
                                 const LabelStr& propagatorName,
                                 const ConstraintEngineId& constraintEngine,
                                 const std::vector<ConstrainedVariableId>& variables);

    ~GreaterOrEqThanSumConstraint() {
      discard(false);
    }

  private:
    // All the work is done by the member constraints
    inline void handleExecute() { }

    void handleDiscard(){
      Constraint::handleDiscard();
      m_interimVariable.discard();
      m_lessOrEqualConstraint.discard();
      m_eqSumConstraint->discard();
    }

    Variable<IntervalDomain> m_interimVariable;
    LessThanEqualConstraint m_lessOrEqualConstraint;
    ConstraintId m_eqSumConstraint;
  };

  /**
   * @class GreaterThanSumConstraint
   * @brief A > B + C + ...
   * Converted into two constraints: A < temp and temp equal to the sum of the rest.
   */
  class GreaterThanSumConstraint : public Constraint {
  public:
    GreaterThanSumConstraint(const LabelStr& name,
                             const LabelStr& propagatorName,
                             const ConstraintEngineId& constraintEngine,
                             const std::vector<ConstrainedVariableId>& variables);

    ~GreaterThanSumConstraint() {
      discard(false);
    }

  private:
    // All the work is done by the member constraints
    inline void handleExecute() { }

    void handleDiscard(){
      Constraint::handleDiscard();
      m_interimVariable.discard();
      m_lessThanConstraint.discard();
      m_eqSumConstraint->discard();
    }

    Variable<IntervalDomain> m_interimVariable;
    LessThanConstraint m_lessThanConstraint;
    ConstraintId m_eqSumConstraint;
  };

  /**
   * @class CondAllSame
   * @brief If A, then B == C && B == D && C == D && ... ; if not A, then !(B == C && B == D && C == D && ...).
   */
  class CondAllSameConstraint : public Constraint {
  public:
    CondAllSameConstraint(const LabelStr& name,
                          const LabelStr& propagatorName,
                          const ConstraintEngineId& constraintEngine,
                          const std::vector<ConstrainedVariableId>& variables);

    ~CondAllSameConstraint() { }

    void handleExecute();

  private:
    const unsigned int ARG_COUNT;
  };

  /**
   * @class CondAllDiff
   * @brief If A, then B != C && B != D && C != D && ... ; if not A, then !(B != C && B != D && C != D && ...).
   */
  class CondAllDiffConstraint : public Constraint {
  public:
    CondAllDiffConstraint(const LabelStr& name,
                          const LabelStr& propagatorName,
                          const ConstraintEngineId& constraintEngine,
                          const std::vector<ConstrainedVariableId>& variables);

    ~CondAllDiffConstraint() { }

    void handleExecute();

  private:
    const unsigned int ARG_COUNT;
  };

  /**
   * @class AllDiff
   * @brief A != B && A != C && B != C && A != D && B != D && ...
   */
  class AllDiffConstraint : public Constraint {
  public:
    AllDiffConstraint(const LabelStr& name,
                      const LabelStr& propagatorName,
                      const ConstraintEngineId& constraintEngine,
                      const std::vector<ConstrainedVariableId>& variables);

    ~AllDiffConstraint() {
			discard(false);
    }

  private:
    void handleExecute() { }

    void handleDiscard(){
      Constraint::handleDiscard();
      m_condVar.discard();
      m_condAllDiffConstraint->discard();
    }

    Variable<BoolDomain> m_condVar;
    ConstraintId m_condAllDiffConstraint;
  };

  /**
   * @class MemberImplyConstraint
   * @brief If A is subset of B, then require that C is subset of D.
   */
  class MemberImplyConstraint : public Constraint {
  public:
    MemberImplyConstraint(const LabelStr& name,
                          const LabelStr& propagatorName,
                          const ConstraintEngineId& constraintEngine,
                          const std::vector<ConstrainedVariableId>& variables);

    ~MemberImplyConstraint() { }

    void handleExecute();

  private:
    const unsigned int ARG_COUNT;
  };

  /**
   * @class CountZerosConstraint
   * @brief First variable is the count of the rest that can be zero.
   * @note Supports boolean domains with the usual C/C++ convention of false
   * being zero and true being non-zero.
   */
  class CountZerosConstraint : public Constraint {
  public:
    CountZerosConstraint(const LabelStr& name,
                         const LabelStr& propagatorName,
                         const ConstraintEngineId& constraintEngine,
                         const std::vector<ConstrainedVariableId>& variables);

    ~CountZerosConstraint() { }

    void handleExecute();
  };

  /**
   * @class CountNonZerosConstraint
   * @brief First variable is the count of the rest that can be non-zero.
   * @note Supports boolean domains with the usual C/C++ convention of false
   * being zero and true being non-zero.
   */
  class CountNonZerosConstraint : public Constraint {
  public:
    CountNonZerosConstraint(const LabelStr& name,
                            const LabelStr& propagatorName,
                            const ConstraintEngineId& constraintEngine,
                            const std::vector<ConstrainedVariableId>& variables);

    ~CountNonZerosConstraint() {
      discard(false);
    }

    // All the work is done by the member constraints.
    inline void handleExecute() { }

    void handleDiscard(){
      Constraint::handleDiscard();
      m_zeros.discard();
      m_otherVars.discard();
      m_superset.discard();
      m_addEqualConstraint.discard();
      m_countZerosConstraint->discard();
      m_subsetConstraint->discard();
    }

  private:
    Variable<IntervalDomain> m_zeros, m_otherVars,  m_superset;
    AddEqualConstraint m_addEqualConstraint;
    ConstraintId m_subsetConstraint;
    ConstraintId m_countZerosConstraint;
  };

  /**
   * @class CardinalityConstraint
   * @brief First variable must be greater than or equal the count of the
   * other variables that are true.
   * @note Supports numeric domains for the other variables with the
   * usual C/C++ convention of false being zero and true being
   * any non-zero value.
   */
  class CardinalityConstraint : public Constraint {
  public:
    CardinalityConstraint(const LabelStr& name,
                          const LabelStr& propagatorName,
                          const ConstraintEngineId& constraintEngine,
                          const std::vector<ConstrainedVariableId>& variables);

    ~CardinalityConstraint() {
      discard(false);
    }

  private:
    // All the work is done by the member constraints.
    inline void handleExecute() { }

    void handleDiscard(){
      Constraint::handleDiscard();
      m_nonZeros.discard();
      m_lessThanEqualConstraint.discard();
      m_countNonZerosConstraint->discard();
    }

    Variable<IntervalIntDomain> m_nonZeros;
    LessThanEqualConstraint m_lessThanEqualConstraint;
    ConstraintId m_countNonZerosConstraint;
  };

  /**
   * @class OrConstraint
   * @brief At least one of the variables must be true.
   * @note Supports numeric domains for all the variables with the
   * usual C/C++ convention of false being zero and true being
   * any non-zero value.
   */
  class OrConstraint : public Constraint {
  public:
    OrConstraint(const LabelStr& name,
                 const LabelStr& propagatorName,
                 const ConstraintEngineId& constraintEngine,
                 const std::vector<ConstrainedVariableId>& variables);

    ~OrConstraint() {
      discard(false);
    }

  private:
    // All the work is done by the member constraints.
    inline void handleExecute() { }

    void handleDiscard(){
      Constraint::handleDiscard();
      m_nonZeros.discard();
      m_superset.discard();
      m_subsetConstraint->discard();
      m_countNonZerosConstraint->discard();
    }

    Variable<IntervalIntDomain> m_nonZeros;
    Variable<IntervalIntDomain> m_superset;
    ConstraintId m_subsetConstraint;
    ConstraintId m_countNonZerosConstraint;
  };

  /**
   * @class EqualMinimumConstraint
   * @brief First variable is the minimum value of the others.
   */
  class EqualMinimumConstraint : public Constraint {
  public:
    EqualMinimumConstraint(const LabelStr& name,
                           const LabelStr& propagatorName,
                           const ConstraintEngineId& constraintEngine,
                           const std::vector<ConstrainedVariableId>& variables);

    ~EqualMinimumConstraint() { }

    void handleExecute();
  };

  /**
   * @class EqualMaximumConstraint
   * @brief First variable is the maximum value of the others.
   */
  class EqualMaximumConstraint : public Constraint {
  public:
    EqualMaximumConstraint(const LabelStr& name,
                           const LabelStr& propagatorName,
                           const ConstraintEngineId& constraintEngine,
                           const std::vector<ConstrainedVariableId>& variables);

    ~EqualMaximumConstraint() { }

    void handleExecute();
  };

  /**
   * @class CondEqualSumConstraint
   * @brief If A is true, then B = C + D ...; if A is false, B != C + D ...
   * Converted into two constraints: CondAllSame(A, B, sum) and EqualSum(sum, C, D, ...).
   */
  class CondEqualSumConstraint : public Constraint {
  public:
    CondEqualSumConstraint(const LabelStr& name,
                           const LabelStr& propagatorName,
                           const ConstraintEngineId& constraintEngine,
                           const std::vector<ConstrainedVariableId>& variables);

    ~CondEqualSumConstraint() {
      discard(false);
    }

  private:
    // All the work is done by the member constraints.
    inline void handleExecute() { }

    void handleDiscard(){
      Constraint::handleDiscard();
      m_sumVar.discard();
      m_condAllSameConstraint.discard();
      m_eqSumConstraint->discard();
    }

    Variable<IntervalDomain> m_sumVar;
    CondAllSameConstraint m_condAllSameConstraint;
    ConstraintId m_eqSumConstraint;
  };

  /**
   * @class RotateScopeRightConstraint
   * @brief Rotate the scope right rotateCount places and call the otherName
   * constraint.
   * @note "Rotating right" comes from last variable moving to the start,
   * "pushing" all of the other variables to the right.
   * @note Negative and zero values for rotateCount are supported.
   */
  class RotateScopeRightConstraint : public Constraint {
  public:
    RotateScopeRightConstraint(const LabelStr& name,
                               const LabelStr& propagatorName,
                               const ConstraintEngineId& constraintEngine,
                               const std::vector<ConstrainedVariableId>& variables)
      : Constraint(name, propagatorName, constraintEngine, variables) {
      // Called via REGISTER_NARY() macro's factory rather than via the
      //   REGISTER_ROTATED_NARY() macro's factory: not enough information
      //   to create the constraint.
      assertTrue(false);
    }

    RotateScopeRightConstraint(const LabelStr& name,
                               const LabelStr& propagatorName,
                               const ConstraintEngineId& constraintEngine,
                               const std::vector<ConstrainedVariableId>& variables,
                               const LabelStr& otherName,
                               const int& rotateCount);

    ~RotateScopeRightConstraint() {
      discard(false);
    }

  private:
    void handleExecute() { }

    void handleDiscard(){
      Constraint::handleDiscard();
      m_otherConstraint->discard();
    }

    ConstraintId m_otherConstraint;
  };

  /**
   * @class SwapTwoVarsConstraint
   * @brief Swap two variables in the scope and call the otherName
   * constraint.
   */
  class SwapTwoVarsConstraint : public Constraint {
  public:
    SwapTwoVarsConstraint(const LabelStr& name,
                          const LabelStr& propagatorName,
                          const ConstraintEngineId& constraintEngine,
                          const std::vector<ConstrainedVariableId>& variables)
      : Constraint(name, propagatorName, constraintEngine, variables) {
      // Called via REGISTER_NARY() macro's factory rather than via the
      //   REGISTER_SWAP_TWO_VARS_NARY() macro's factory: not enough information
      //   to create the constraint.
      assertTrue(false);
    }

    SwapTwoVarsConstraint(const LabelStr& name,
                          const LabelStr& propagatorName,
                          const ConstraintEngineId& constraintEngine,
                          const std::vector<ConstrainedVariableId>& variables,
                          const LabelStr& otherName,
                          int firstIndex, int secondIndex);

    ~SwapTwoVarsConstraint() {
      discard(false);
    }

  private:
    void handleExecute() { }

    void handleDiscard(){
      Constraint::handleDiscard();
      m_otherConstraint->discard();
    }

    ConstraintId m_otherConstraint;
  };

  // Enforce X+Y=0. X >=0. Y <=0.
  class NegateConstraint : public Constraint {
  public:
    NegateConstraint(const LabelStr& name,
		    const LabelStr& propagatorName,
		    const ConstraintEngineId& constraintEngine,
		    const std::vector<ConstrainedVariableId>& variables);

    void handleExecute();
  private:
    static const int X=0;
    static const int Y=1;
  };

  class TestEQ : public Constraint {
  public:
    TestEQ(const LabelStr& name,
	   const LabelStr& propagatorName,
	   const ConstraintEngineId& constraintEngine,
	   const std::vector<ConstrainedVariableId>& variables);

    void handleExecute();

  private:
    AbstractDomain& m_test;
    AbstractDomain& m_arg1;
    AbstractDomain& m_arg2;
    static const unsigned int ARG_COUNT = 3;
  };

  class TestLessThan : public Constraint {
  public:
    TestLessThan(const LabelStr& name,
		 const LabelStr& propagatorName,
		 const ConstraintEngineId& constraintEngine,
		 const std::vector<ConstrainedVariableId>& variables);

    void handleExecute();

  private:
    AbstractDomain& m_test;
    AbstractDomain& m_arg1;
    AbstractDomain& m_arg2;
    static const unsigned int ARG_COUNT = 3;
  };

  class TestLEQ : public Constraint {
  public:
    TestLEQ(const LabelStr& name,
	    const LabelStr& propagatorName,
	    const ConstraintEngineId& constraintEngine,
	    const std::vector<ConstrainedVariableId>& variables);

    void handleExecute();

  private:
    AbstractDomain& m_test;
    AbstractDomain& m_arg1;
    AbstractDomain& m_arg2;
    static const unsigned int ARG_COUNT = 3;
  };


  /**
   * @brief WithinBounds(x, y, z) maintains the relations:
   * @li x.lb >= y.lb
   * @li x.ub <= z.ub
   * @li y <= z
   */
  class WithinBounds : public Constraint {
  public:
    WithinBounds(const LabelStr& name,
		 const LabelStr& propagatorName,
		 const ConstraintEngineId& constraintEngine,
		 const std::vector<ConstrainedVariableId>& variables);

    void handleExecute();

  private:
    IntervalDomain& m_x;
    IntervalDomain& m_y;
    IntervalDomain& m_z;
    LessThanEqualConstraint m_leq;
    static const unsigned int ARG_COUNT = 3;
  };

  /**
   * @brief AbsoluteValue(x, y) maintains the relation:
   * @li x.lb >= 0
   * @li x.ub = max(abs(y.lb), abs(y.ub))
   * @li y.lb >= -x.lb
   * @li y.ub <= x.ub
   */
  class AbsoluteValue : public Constraint {
  public:
    AbsoluteValue(const LabelStr& name,
		  const LabelStr& propagatorName,
		  const ConstraintEngineId& constraintEngine,
		  const std::vector<ConstrainedVariableId>& variables);
    void handleExecute();
  private:
    IntervalDomain& m_x;
    IntervalDomain& m_y;
    static const unsigned int ARG_COUNT = 2;
  };


  /**
   * @brief Calculate the euclidean distance in 2-d space between between 2 points 
   */
  class CalcDistanceConstraint : public Constraint {
  public:
    CalcDistanceConstraint(const LabelStr& name,
			   const LabelStr& propagatorName,
			   const ConstraintEngineId& constraintEngine,
			   const std::vector<ConstrainedVariableId>& variables);

    void handleExecute();

    /**
     * Calculates the actual distance
     */
    static double compute(double x1, double y1, double x2, double y2);

    /**
     * Calculates the hypotenuse w. pythagaras
     */
    static double compute(double a, double b);

  private:

    static const unsigned int ARG_COUNT = 5;
    static const unsigned int DISTANCE = 0;
    static const unsigned int X1 = 1;
    static const unsigned int Y1 = 2;
    static const unsigned int X2 = 3;
    static const unsigned int Y2 = 4;

    AbstractDomain& m_distance;
    AbstractDomain& m_x1;
    AbstractDomain& m_y1;
    AbstractDomain& m_x2;
    AbstractDomain& m_y2;
  };

  /**
   * @brief Computes the sign of a given variable. Varable is in degrees. The constraint is a function
   * rather than a relation. The range of the source variable must be in [0 90].
   */
  class SineFunction : public Constraint {
  public:
    SineFunction(const LabelStr& name,
		 const LabelStr& propagatorName,
		 const ConstraintEngineId& constraintEngine,
		 const std::vector<ConstrainedVariableId>& variables);

    void handleExecute();

  private:
    static const unsigned int ARG_COUNT = 2;
    AbstractDomain& m_target;
    AbstractDomain& m_source;
  };

  /**
   * @brief SquareOfDifference(x, y, a) maintains the relation:
   * @li a = (x - y)^2
   * if x and y are singleton.
   */
  class SquareOfDifferenceConstraint : public Constraint {
  public:
    SquareOfDifferenceConstraint(const LabelStr& name,
		       const LabelStr& propagatorName,
		       const ConstraintEngineId& constraintEngine,
		       const std::vector<ConstrainedVariableId>& variables);

    void handleExecute();

  private:
    static const int V1 = 0;
    static const int V2 = 1;
    static const int RES = 2;
    static const int ARG_COUNT = 3;
  };

  /**
   * @brief DistanceFromSquaresConstraint(x, y, a) maintains the relation
   * @li a = sqrt(x + y)
   * if x and y are singleton
   */
  class DistanceFromSquaresConstraint : public Constraint {
  public:
    DistanceFromSquaresConstraint(const LabelStr& name,
		       const LabelStr& propagatorName,
		       const ConstraintEngineId& constraintEngine,
		       const std::vector<ConstrainedVariableId>& variables);

    void handleExecute();

  private:
    static const int V1 = 0;
    static const int V2 = 1;
    static const int RES = 2;
    static const int ARG_COUNT = 3;
  };
}
#endif
