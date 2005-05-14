#include "ValueSource.hh"
#include "ConstrainedVariable.hh"
#include "AbstractDomain.hh"
#include "Debug.hh"

/**
 * @author Michael Iatauro
 * @file ValueSource.cc
 * @brief Implements ValueSource, IntervalValueSource, EnumValueSource
 * @date March, 2005
 */
namespace EUROPA {
  namespace SOLVERS {

    ValueSource* ValueSource::getSource(const ConstrainedVariableId& var) {
      if(var->lastDomain().isEnumerated())
	return new EnumValueSource(var->lastDomain());
      else
	return new IntervalValueSource(var->lastDomain());
    }

    ValueSource::ValueSource(unsigned int count) : m_count(count) {
      debugMsg("ValueSource:ValueSource", "Allocating for " << m_count << " choices.");
    }

    ValueSource::~ValueSource(){}

    unsigned int ValueSource::getCount() const { return m_count;}

    EnumValueSource::EnumValueSource(const AbstractDomain& dom)
      : ValueSource(dom.getSize()) {
      std::list<double> values;
      dom.getValues(values);
    
      for(std::list<double>::const_iterator it = values.begin(); it != values.end(); ++it)
        m_values.push_back(*it);
    }

    double EnumValueSource::getValue(unsigned int index) const { return m_values[index];}

    IntervalValueSource::IntervalValueSource(const AbstractDomain& dom)
      : ValueSource(calculateSize(dom)),
	m_lb(dom.getLowerBound()), m_ub(dom.getUpperBound()), m_step(dom.minDelta()){
    }

    double IntervalValueSource::getValue(unsigned int index) const {return m_lb + (m_step * index);}

    unsigned int IntervalValueSource::calculateSize(const AbstractDomain& dom){
      return (unsigned int) ((dom.getUpperBound() - dom.getLowerBound())/dom.minDelta()) + 1;
    }
  }
}
