#ifndef _H_DbClientTransactionPlayer
#define _H_DbClientTransactionPlayer

#include "PlanDatabaseDefs.hh"
#include <iostream>
#include <map>
#include <list>

class TiXmlElement;

/**
 * @file DbClientTransactionPlayer
 * @brief Main interface for playing transactions. Necessary for copy. replay, and possibly recovery.
 */

namespace Prototype {

  class DbClientTransactionPlayer {
  public:
    DbClientTransactionPlayer(const DbClientId & client);
    ~DbClientTransactionPlayer();

    /**
     * @brief Play all transactions from an input stream
     * @param is a stream of xml-based transactions.
     */
    void play(std::istream& is);

    /**
     * @brief Play all transactions from a given TransactionLog
     * @param txLog the source log which has all transactions in memory
     */
    void play(const DbClientTransactionLogId& txLog);

  protected:
    void processTransaction(const TiXmlElement & element);
    void playDefineClass(const TiXmlElement & element);
    void playDefineEnumeration(const TiXmlElement & element);
    void playVariableCreated(const TiXmlElement & element);
    void playObjectCreated(const TiXmlElement & element);
    void playTokenCreated(const TiXmlElement & element);
    void playConstrained(const TiXmlElement & element);
    void playFreed(const TiXmlElement & element);
    void playActivated(const TiXmlElement & element);
    void playMerged(const TiXmlElement & element);
    void playRejected(const TiXmlElement & element);
    void playCancelled(const TiXmlElement & element);
    void playVariableSpecified(const TiXmlElement & element);
    void playVariableReset(const TiXmlElement & element);
    void playInvokeConstraint(const TiXmlElement & element);

  private:
    DbClientId m_client;
    int m_objectCount;
    std::map<std::string, TokenId> m_tokens;
    std::map<std::string, ConstrainedVariableId> m_variables;
    std::list<std::string> m_enumerations;
    std::list<std::string> m_classes;

  //! string input functions

    /** 
     * @brief read a value string as a float type
     */
    double parseFloat(const char * floatString);

    /** 
     * @brief read a value string as an int type
     */
    int parseInt(const char * intString);

    /** 
     * @brief read a value string as a bool type
     */
    bool parseBool(const char * boolString);

    /** 
     * @brief read a value string as a variable identifier
     */
    ConstrainedVariableId parseVariable(const char * varString);
  //! XML input functions

    /** 
     * @brief create an abstract domain as represented by an xml element
     */
    const AbstractDomain * xmlAsAbstractDomain(const TiXmlElement & element, const char * name = NULL);

    /** 
     * @brief create an interval domain as represented by an xml element
     */
    IntervalDomain * xmlAsIntervalDomain(const TiXmlElement & element);

    /** 
     * @brief create an enumerated domain as represented by an xml element
     */
    EnumeratedDomain * xmlAsEnumeratedDomain(const TiXmlElement & element);

    /** 
     * @brief return a value as represented by an xml element
     */
    double xmlAsValue(const TiXmlElement & value, const char * name = NULL);

    /** 
     * @brief return a variable as represented by an xml element
     */
    ConstrainedVariableId xmlAsVariable(const TiXmlElement & variable);

    /** 
     * @brief return a token as represented by an xml element
     */
    TokenId xmlAsToken(const TiXmlElement & token);
  };

}

#endif // _H_DbClientTransactionPlayer
