package org.reldb.rel.v0.interpreter;

import org.reldb.rel.v0.languages.tutoriald.parser.*;

public class TutorialDDebugger implements TutorialDVisitor {
	
	private int indent = 0;
	
	private String indentString() {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < indent; ++i) {
			sb.append(" ");
		}
		return sb.toString();
	}
	
	/** Debugging dump of a node. */
	private Object dump(SimpleNode node, Object data) {
		System.out.println(indentString() + node);
		++indent;
		data = node.childrenAccept(this, data);
		--indent;
		return data;		
	}
	
	public Object visit(SimpleNode node, Object data) {
		System.out.println(node + ": acceptor not implemented in subclass?");
		return data;
	}

	//
	// Type definition
	//
	
	public Object visit(ASTTypeDef node, Object data) {
		return dump(node, data);
	}
	
	public Object visit(ASTTypeDefExternal node, Object data) {
		return dump(node, data);
	}

	/*
	 * Possrep nodes
	 */
	
	public Object visit(ASTTypeDefInternal node, Object data) {
		return dump(node, data);
	}

	public Object visit(ASTTypeDefInternalOptOrdinal node, Object data) {
		return dump(node, data);
	}

	public Object visit(ASTTypeDefInternalOptOrdered node, Object data) {
		return dump(node, data);
	}

	public Object visit(ASTTypeDefInternalOptUnion node, Object data) {
		return dump(node, data);
	}

	public Object visit(ASTSingleInheritanceIsDef node, Object data) {
		return dump(node, data);
	}

	public Object visit(ASTMultipleInheritanceIsDef node, Object data) {
		return dump(node, data);
	}

	public Object visit(ASTScalarTypeName node, Object data) {
		return dump(node, data);
	}
	
	public Object visit (ASTPossrepInitialiser node, Object data) {
		return dump(node, data);
	}
	
	public Object visit (ASTPossrepInitialiserAssignments node, Object data) {
		return dump(node, data);
	}
	
	public Object visit(ASTPossrepDef node, Object data) {
		return dump(node, data);
	}
	
	public Object visit(ASTPossrepDefIdentifier node, Object data) {
		return dump(node, data);
	}

	public Object visit(ASTPossrepDefConstraintDef node, Object data) {
		return dump(node, data);
	}

	public Object visit(ASTPossrepDefComponentCommalist node, Object data) {
		return dump(node, data);
	}

	public Object visit(ASTPossrepDefComponent node, Object data) {
		return dump(node, data);
	}

	public Object visit(ASTPossrepConstraintDef node, Object data) {
		return dump(node, data);
	}

	public Object visit(ASTSpecialisationConstraintDef node, Object data) {
		return dump(node, data);
	}
	
	public Object visit(ASTDerivedPossrepDef node, Object data) {
		return dump(node, data);
	}
		
	public Object visit(ASTDerivedPossrepDefOptIdentifier node, Object data) {
		return dump(node, data);
	}
	
	public Object visit(ASTDerivedPossrepComponentDef node, Object data) {
		return dump(node, data);
	}

	/*
	 * End possrep nodes
	 */	
	
	//
	// End type definition
	//
	
	// Execute a Rel program
	public Object visit(ASTCode node, Object data) {
		return dump(node, data);
	}
	
	// Evaluate a Rel expression
	public Object visit(ASTEvaluate node, Object data) {
		return dump(node, data);
	}
	
	// Statement
	public Object visit(ASTStatement node, Object data) {
		return dump(node, data);
	}
	
	// Obtain operator return type
	public Object visit(ASTGetOperatorReturnType node, Object data) {
		return dump(node, data);
	}
	
	// Obtain heading
	public Object visit(ASTGetHeading node, Object data) {
		return dump(node, data);
	}
	
	// Obtain operator signature
	public Object visit(ASTGetSignature node, Object data) {
		return dump(node, data);
	}

	@Override
	public Object visit(ASTBackup node, Object data) {
		return dump(node, data);
	}
	
	public Object visit(ASTWrite node, Object data) {
		return dump(node, data);
	}
	
	public Object visit(ASTWriteln node, Object data) {
		return dump(node, data);
	}
	
	public Object visit(ASTOutput node, Object data) {
		return dump(node, data);
	}
	
	public Object visit(ASTAnnounce node, Object data) {
		return dump(node, data);
	}
	
	public Object visit(ASTExecute node, Object data) {
		return dump(node, data);
	}
	
	public Object visit(ASTSet node, Object data) {
		return dump(node, data);
	}
		
	public Object visit(ASTTransactionBegin node, Object data) {
		return dump(node, data);
	}

	public Object visit(ASTTransactionCommit node, Object data) {
		return dump(node, data);
	}

	public Object visit(ASTTransactionRollback node, Object data) {
		return dump(node, data);
	}
	
	// DIVIDEBY
	public Object visit(ASTAlgDivide node, Object data) {
		return dump(node, data);
	}

	// DIVIDEBY 'per' optional term
	public Object visit(ASTAlgDividePerOptional node, Object data) {
		return dump(node, data);
	}
		
	// SUMMARIZE
	public Object visit(ASTSummarize node, Object data) {
		return dump(node, data);
	}

	// SUMMARIZE optional 'PER' or 'BY'
	public Object visit(ASTSummarizePerOrBy node, Object data) {
		return dump(node, data);
	}

	// SUMMARIZE 'PER'
	public Object visit(ASTSummarizePer node, Object data) {
		return dump(node, data);
	}
	
	// SUMMARIZE 'BY'
	public Object visit(ASTSummarizeBy node, Object data) {
		return dump(node, data);
	}

	// SUMMARIZE items
	public Object visit(ASTSummarizeItems node, Object data) {
		return dump(node, data);
	}
	
	// SUMMARIZE item
	public Object visit(ASTSummarizeItem node, Object data) {
		return dump(node, data);
	}
	
	// SUMMARIZE aggregation
	public Object visit(ASTSummarizeCount node, Object data) {
		return dump(node, data);
	}

	// SUMMARIZE aggregation
	public Object visit(ASTSummarizeCountDistinct node, Object data) {
		return dump(node, data);
	}

	// SUMMARIZE aggregation
	public Object visit(ASTSummarizeSum node, Object data) {
		return dump(node, data);
	}
	
	// SUMMARIZE aggregation
	public Object visit(ASTSummarizeSumDistinct node, Object data) {
		return dump(node, data);
	}

	// SUMMARIZE aggregation
	public Object visit(ASTSummarizeAvg node, Object data) {
		return dump(node, data);
	}
	
	// SUMMARIZE aggregation
	public Object visit(ASTSummarizeAvgDistinct node, Object data) {
		return dump(node, data);
	}

	// SUMMARIZE aggregation
	public Object visit(ASTSummarizeMax node, Object data) {
		return dump(node, data);
	}
	
	// SUMMARIZE aggregation
	public Object visit(ASTSummarizeMin node, Object data) {
		return dump(node, data);
	}

	// SUMMARIZE aggregation
	public Object visit(ASTSummarizeAnd node, Object data) {
		return dump(node, data);
	}

	// SUMMARIZE aggregation
	public Object visit(ASTSummarizeOr node, Object data) {
		return dump(node, data);
	}
	
	// SUMMARIZE aggregation
	public Object visit(ASTSummarizeXor node, Object data) {
		return dump(node, data);
	}

	// SUMMARIZE aggregation
	public Object visit(ASTSummarizeExactly node, Object data) {
		return dump(node, data);
	}
	
	// SUMMARIZE aggregation
	public Object visit(ASTSummarizeExactlyDistinct node, Object data) {
		return dump(node, data);
	}

	// SUMMARIZE aggregation
	public Object visit(ASTSummarizeUnion node, Object data) {
		return dump(node, data);
	}

	// SUMMARIZE aggregation
	public Object visit(ASTSummarizeXunion node, Object data) {
		return dump(node, data);
	}

	// SUMMARIZE aggregation
	public Object visit(ASTSummarizeDUnion node, Object data) {
		return dump(node, data);
	}
	
	// SUMMARIZE aggregation
	public Object visit(ASTSummarizeIntersect node, Object data) {
		return dump(node, data);
	}
	
	// UPDATE statement
	public Object visit(ASTUpdateStatement node, Object data) {
		return dump(node, data);		
	}
	
	// Update statement optional WHERE clause
	public Object visit(ASTUpdateWhere node, Object data) {
		return dump(node, data);
	}
	
	// Update expression
	public Object visit(ASTSubstitute node, Object data) {
		return dump(node, data);
	}
	
	// Update expression assignment statements
	public Object visit(ASTUpdateAssignment node, Object data) {
		return dump(node, data);
	}
		
	// Expression
	public Object visit(ASTExpression node, Object data) {
		return dump(node, data);
	}
	
	// Tuple attribute from
	public Object visit(ASTAttributeFrom node, Object data) {
		return dump(node, data);
	}

	// Tuple from
	public Object visit(ASTTupleFrom node, Object data) {
		return dump(node, data);
	}

	// aggregate COUNT
	public Object visit(ASTAggCount node, Object data) {
		return dump(node, data);
	}
	
	// aggregate SUM
	public Object visit(ASTAggSum node, Object data) {
		return dump(node, data);
	}
	
	// aggregate AVG
	public Object visit(ASTAggAvg node, Object data) {
		return dump(node, data);
	}
	
	// aggregate MAX
	public Object visit(ASTAggMax node, Object data) {
		return dump(node, data);
	}
	
	// aggregate MIN
	public Object visit(ASTAggMin node, Object data) {
		return dump(node, data);
	}
	
	// aggregate AND
	public Object visit(ASTAggAnd node, Object data) {
		return dump(node, data);
	}
	
	// aggregate OR
	public Object visit(ASTAggOr node, Object data) {
		return dump(node, data);
	}
	
	// aggregate XOR
	public Object visit(ASTAggXor node, Object data) {
		return dump(node, data);
	}
	
	// aggregate UNION
	public Object visit(ASTAggUnion node, Object data) {
		return dump(node, data);
	}

	// aggregate XUNION
	public Object visit(ASTAggXunion node, Object data) {
		return dump(node, data);
	}
	
	// aggregate D_UNION
	public Object visit(ASTAggDUnion node, Object data) {
		return dump(node, data);
	}
	
	// aggregate INTERSECT
	public Object visit(ASTAggIntersect node, Object data) {
		return dump(node, data);
	}
	
	// EXACTLY
	public Object visit(ASTExactly node, Object data) {
		return dump(node, data);
	}
	
	// n-adic OR
	public Object visit(ASTNadicOr node, Object data) {
		return dump(node, data);
	}
	
	// n-adic AND
	public Object visit(ASTNadicAnd node, Object data) {
		return dump(node, data);
	}
	
	// n-adic XOR
	public Object visit(ASTNadicXor node, Object data) {
		return dump(node, data);
	}
	
	// n-adic SUM
	public Object visit(ASTNadicSum node, Object data) {
		return dump(node, data);
	}

	// n-adic AVG
	public Object visit(ASTNadicAvg node, Object data) {
		return dump(node, data);
	}

	// n-adic MAX
	public Object visit(ASTNadicMax node, Object data) {
		return dump(node, data);
	}

	// n-adic MIN
	public Object visit(ASTNadicMin node, Object data) {
		return dump(node, data);
	}
	
	// n-adic UNION
	public Object visit(ASTNadicUnion node, Object data) {
		return dump(node, data);
	}

	// n-adic XUNION
	public Object visit(ASTNadicXunion node, Object data) {
		return dump(node, data);
	}

	// n-adic D_UNION
	public Object visit(ASTNadicDUnion node, Object data) {
		return dump(node, data);
	}

	// n-adic INTERSECT
	public Object visit(ASTNadicIntersect node, Object data) {
		return dump(node, data);
	}
	
	// n-adic COUNT
	public Object visit(ASTNadicCount node, Object data) {
		return dump(node, data);
	}

	// n-adic operator heading
	public Object visit(ASTNadicHeading node, Object data) {
		return dump(node, data);
	}
	
	// n-adic JOIN
	public Object visit(ASTNadicJoin node, Object data) {
		return dump(node, data);
	}
	
	// n-adic TIMES
	public Object visit(ASTNadicTimes node, Object data) {
		return dump(node, data);
	}

	// n-adic COMPOSE
	public Object visit(ASTNadicCompose node, Object data) {
		return dump(node, data);
	}

	// heading expression commalist
	public Object visit(ASTHeadingExpCommalist node, Object data) {
		return dump(node, data);
	}
	
	// D_UNION
	public Object visit(ASTAlgDUnion node, Object data) {
		return dump(node, data);
	}
	
	// Semijoin
	public Object visit(ASTAlgSemijoin node, Object data) {
		return dump(node, data);
	}
	
	// Semiminus
	public Object visit(ASTAlgSemiminus node, Object data) {
		return dump(node, data);
	}
	
	// Minus
	public Object visit(ASTAlgMinus node, Object data) {
		return dump(node, data);
	}

	// I_MINUS
	public Object visit(ASTAlgIMinus node, Object data) {
		return dump(node, data);
	}
	
	// Intersect
	public Object visit(ASTAlgIntersect node, Object data) {
		return dump(node, data);
	}
	
	// Compose
	public Object visit(ASTAlgCompose node, Object data) {
		return dump(node, data);
	}
		
	// Union
	public Object visit(ASTAlgUnion node, Object data) {
		return dump(node, data);
	}

	// Xunion
	public Object visit(ASTAlgXunion node, Object data) {
		return dump(node, data);
	}
	
	// Join
	public Object visit(ASTAlgJoin node, Object data) {
		return dump(node, data);
	}

	// Times
	public Object visit(ASTAlgTimes node, Object data) {
		return dump(node, data);
	}
	
	// Wrap
	public Object visit(ASTAlgWrap node, Object data) {
		return dump(node, data);
	}
	
	// Wrapping item
	public Object visit(ASTWrappingItem node, Object data) {
		return dump(node, data);
	}
	
	// Unwrap
	public Object visit(ASTAlgUnwrap node, Object data) {
		return dump(node, data);
	}

	// Group
	public Object visit(ASTGroup node, Object data) {
		return dump(node, data);
	}
	
	// Ungroup
	public Object visit(ASTAlgUngroup node, Object data) {
		return dump(node, data);
	}
		
	// ORDER
	public Object visit(ASTAlgOrder node, Object data) {
		return dump(node, data);
	}
	
	// ORDER item commalist
	public Object visit(ASTOrderItemCommalist node, Object data) {
		return dump(node, data);
	}
	
	public Object visit(ASTOrderItemAsc node, Object data) {
		return dump(node, data);
	}

	public Object visit(ASTOrderItemDesc node, Object data) {
		return dump(node, data);
	}
	
	// Extend
	public Object visit(ASTExtend node, Object data) {
		return dump(node, data);
	}
		
	// Extend list
	public Object visit(ASTExtendList node, Object data) {
		return dump(node, data);
	}
	
	// Extend item
	public Object visit(ASTExtendItem node, Object data) {
		return dump(node, data);
	}
	
	// Project
	public Object visit(ASTAlgProject node, Object data) {
		return dump(node, data);
	}
	
	// WHERE
	public Object visit(ASTAlgWhere node, Object data) {
		return dump(node, data);
	}
	
	// Rename
	public Object visit(ASTAlgRename node, Object data) {
		return dump(node, data);
	}
	
	// Renaming list
	public Object visit(ASTRenamingList node, Object data) {
		return dump(node, data);
	}
		
	// Simple rename element
	public Object visit(ASTRenamingSimple node, Object data) {
		return dump(node, data);	
	}
	
	// Prefix rename element
	public Object visit(ASTRenamingPrefix node, Object data) {
		return dump(node, data);	
	}
	
	// Suffix rename element
	public Object visit(ASTRenamingSuffix node, Object data) {
		return dump(node, data);
	}
	
	// Attribute name list
	public Object visit(ASTAttributeNameList node, Object data) {
		return dump(node, data);
	}
	
	// ALL BUT
	public Object visit(ASTAllBut node, Object data) {
		return dump(node, data);
	}

	// Attribute name commalist
	public Object visit(ASTAttributeNameCommalist node, Object data) {
		return dump(node, data);
	}
	
	// Var def
	public Object visit(ASTVarDef node, Object data) {
		return dump(node, data);
	}

	public Object visit(ASTVarScalarOrTuple node, Object data) {
		return dump(node, data);
	}
	
	public Object visit(ASTVarRelvarReal node, Object data) {
		return dump(node, data);
	}	

	public Object visit(ASTVarRelvarPublic node, Object data) {
		return dump(node, data);
	}	

	public Object visit(ASTVarRelvarPrivate node, Object data) {
		return dump(node, data);
	}	
	
	public Object visit(ASTVarRelvarVirtual node, Object data) {
		return dump(node, data);
	}	

	public Object visit(ASTVarRelvarExternal node, Object data) {
		return dump(node, data);
	}
	
	public Object visit(ASTKeyDefList node, Object data) {
		return dump(node, data);
	}	

	public Object visit(ASTKeyDef node, Object data) {
		return dump(node, data);
	}	
	
	public Object visit(ASTVarTypeAndOptionalInit node, Object data) {
		return dump(node, data);
	}

	public Object visit(ASTVarInit node, Object data) {
		return dump(node, data);
	}

	public Object visit(ASTVarArray node, Object data) {
		return dump(node, data);
	}
	
	public Object visit(ASTDropRelvar node, Object data) {
		return dump(node, data);
	}

	public Object visit(ASTDatabaseConstraint node, Object data) {
		return dump(node, data);
	}

	public Object visit(ASTDropConstraint node, Object data) {
		return dump(node, data);
	}
	
	public Object visit(ASTDropOperator node, Object data) {
		return dump(node, data);
	}

	public Object visit(ASTOpSignature node, Object data) {
		return dump(node, data);
	}

	public Object visit(ASTDropType node, Object data) {
		return dump(node, data);
	}
	
	public Object visit(ASTTypeRefCommalist node, Object data) {
		return dump(node, data);
	}
	
	// LOAD ... FROM ...
	public Object visit(ASTRelationArrayLoad node, Object data) {
		return dump(node, data);
	}
	
	// Type
	public Object visit(ASTType node, Object data) {
		return dump(node, data);
	}

	// TYPE_OF pseudo-operator
	public Object visit(ASTTypeOf node, Object data) {
		return dump(node, data);
	}
	
	// SAME TYPE AS
	public Object visit(ASTTypeSameTypeAs node, Object data) {
		return dump(node, data);		
	}
	
	// SAME HEADING AS
	public Object visit(ASTSameHeadingAs node, Object data) {
		return dump(node, data);
	}

	// TypeRelation
	public Object visit(ASTTypeRelation node, Object data) {
		return dump(node, data);
	}
	
	// TypeTuple
	public Object visit(ASTTypeTuple node, Object data) {
		return dump(node, data);
	}
	
	// Heading
	public Object visit(ASTHeading node, Object data) {
		return dump(node, data);
	}

	// Attribute specification
	public Object visit(ASTAttributeSpec node, Object data) {
		return dump(node, data);
	}

	// External operator definition
	public Object visit(ASTExternalOpDef node, Object data) {
		return dump(node, data);
	}
	
	// Function definition
	public Object visit(ASTUserOpDef node, Object data) {
		return dump(node, data);
	}
	
	// Parameter definition list
	public Object visit(ASTUserOpParameters node, Object data) {
		return dump(node, data);
	}
	
	// Function definition parameter definition
	public Object visit(ASTParmDef node, Object data) {
		return dump(node, data);
	}

	// Function's return definition
	public Object visit(ASTUserOpReturns node, Object data) {
		return dump(node, data);
	}
	
	// Function's updates definition
	public Object visit(ASTUserOpUpdates node, Object data) {
		return dump(node, data);
	}
	
	// Function's synonym definition
	public Object visit(ASTUserOpSynonym node, Object data) {
		return dump(node, data);
	}
	
	// Function's version definition
	public Object visit(ASTUserOpVersion node, Object data) {
		return dump(node, data);
	}
	
	// Function body
	public Object visit(ASTUserOpBody node, Object data) {
		return dump(node, data);
	}
	
	// Function return expression
	public Object visit(ASTReturnExpression node, Object data) {
		return dump(node, data);
	}
	
	// Function argument list
	public Object visit(ASTArgList node, Object data) {
		return dump(node, data);
	}
	
	// Function call
	public Object visit(ASTCall node, Object data) {
		return dump(node, data);
	}
	
	// Function invocation in an expression
	public Object visit(ASTFnInvoke node, Object data) {
		return dump(node, data);
	}
	
	// Dereference an array, and push its value onto the stack
	public Object visit(ASTArrayDereference node, Object data) {
		return dump(node, data);
	}
	
	// Dereference a variable, and push its value onto the stack
	public Object visit(ASTDereference node, Object data) {
		return dump(node, data);
	}
	
	// IF 
	public Object visit(ASTIfStatement node, Object data) {
		return dump(node, data);
	}

	// ELSE
	public Object visit(ASTElseStatement node, Object data) {
		return dump(node, data);
	}
	
	// CASE statement
	public Object visit(ASTCaseStatement node, Object data) {
		return dump(node, data);
	}
		
	// CASE WHEN list
	public Object visit(ASTCaseWhenList node, Object data) {
		return dump(node, data);
	}
	
	// CASE WHEN
	public Object visit(ASTCaseWhen node, Object data) {
		return dump(node, data);
	}
	
	// CASE ELSE
	public Object visit(ASTCaseElse node, Object data) {
		return dump(node, data);
	}
	
	public Object visit(ASTCaseExpression node, Object data) {
		return dump(node, data);
	}
	
	public Object visit(ASTCaseWhenListExpression node, Object data) {
		return dump(node, data);
	}
	
	public Object visit(ASTCaseWhenExpression node, Object data) {
		return dump(node, data);
	}
	
	public Object visit(ASTIfExpression node, Object data) {
		return dump(node, data);
	}
	
	// DO loop
	public Object visit(ASTDoLoop node, Object data) {
		return dump(node, data);
	}
	
	// WHILE loop
	public Object visit(ASTWhileLoop node, Object data) {
		return dump(node, data);
	}
	
	// FOR loop
	public Object visit(ASTForLoop node, Object data) {
		return dump(node, data);
	}
	
	// Process an identifier
	// This doesn't do anything, but needs to be here because we need an ASTIdentifier node.
	public Object visit(ASTIdentifier node, Object data) {
		return dump(node, data);
	}
	
	// Execute a group of comma-separated assignment statements
	public Object visit(ASTAssignment node, Object data) {
		return dump(node, data);
	}
		
	// Execute an assignment statement, by popping a value off the stack and assigning it
	// to a variable.
	public Object visit(ASTAssign node, Object data) {
		return dump(node, data);
	}

	public Object visit(ASTInsert node, Object data) {
		return dump(node, data);
	}

	public Object visit(ASTDInsert node, Object data) {
		return dump(node, data);
	}
	
	public Object visit(ASTDelete node, Object data) {
		return dump(node, data);
	}
	
	public Object visit(ASTDeleteParameter node, Object data) {
		return dump(node, data);
	}

	public Object visit(ASTDeleteWhere node, Object data) {
		return dump(node, data);
	}

	public Object visit(ASTIDelete node, Object data) {
		return dump(node, data);
	}
	
	// Comma-separated boolean expression list
	public Object visit(ASTBooleanExpressionCommalist node, Object data) {
		return dump(node, data);
	}
	
	// Comma-separated expression list
	public Object visit(ASTExpressionCommalist node, Object data) {
		return dump(node, data);
	}
	
	// XOR
	public Object visit(ASTXor node, Object data) {
		return dump(node, data);
	}
	
	// OR
	public Object visit(ASTOr node, Object data) {
		return dump(node, data);
	}

	// AND
	public Object visit(ASTAnd node, Object data) {
		return dump(node, data);
	}

	// ==
	public Object visit(ASTCompEqual node, Object data) {
		return dump(node, data);
	}

	// !=
	public Object visit(ASTCompNequal node, Object data) {
		return dump(node, data);
	}

	// >=
	public Object visit(ASTCompGTE node, Object data) {
		return dump(node, data);
	}

	// <=
	public Object visit(ASTCompLTE node, Object data) {
		return dump(node, data);
	}

	// >
	public Object visit(ASTCompGT node, Object data) {
		return dump(node, data);
	}

	// <
	public Object visit(ASTCompLT node, Object data) {
		return dump(node, data);
	}

	// Concatenate (||)
	public Object visit(ASTConcatenate node, Object data) {
		return dump(node, data);
	}
	
	// IN
	public Object visit(ASTTupleIn node, Object data) {
		return dump(node, data);
	}
	
	// +
	public Object visit(ASTAdd node, Object data) {
		return dump(node, data);
	}

	// -
	public Object visit(ASTSubtract node, Object data) {
		return dump(node, data);
	}

	// *
	public Object visit(ASTTimes node, Object data) {
		return dump(node, data);
	}

	// /
	public Object visit(ASTDivide node, Object data) {
		return dump(node, data);
	}

	// NOT
	public Object visit(ASTUnaryNot node, Object data) {
		return dump(node, data);
	}

	// + (unary)
	public Object visit(ASTUnaryPlus node, Object data) {
		return dump(node, data);
	}

	// - (unary)
	public Object visit(ASTUnaryMinus node, Object data) {
		return dump(node, data);
	}

	// WITH
	public Object visit(ASTWith node, Object data) {
		return dump(node, data);
	}

	// WITH name introduction list
	public Object visit(ASTWithNameIntroCommalist node, Object data) {
		return dump(node, data);
	}
	
	// WITH name introduction
	public Object visit(ASTWithNameIntro node, Object data) {
		return dump(node, data);
	}
	
	// TCLOSE
	public Object visit(ASTTClose node, Object data) {
		return dump(node, data);
	}
	
	// RELATION
	public Object visit(ASTRelation node, Object data) {
		return dump(node, data);
	}
	
	// Optional RELATION heading specification.
	public Object visit(ASTRelationHeading node, Object data) {
		return dump(node, data);
	}

	// Tuple expression list
	public Object visit(ASTTupleExpressionCommalist node, Object data) {
		return dump(node, data);
	}

	// TABLE_DUM
	public Object visit(ASTRelationDum node, Object data) {
		return dump(node, data);
	}	
	
	// TABLE_DEE
	public Object visit(ASTRelationDee node, Object data) {
		return dump(node, data);
	}
	
	// TUPLE
	public Object visit(ASTTuple node, Object data) {
		return dump(node, data);
	}
	
	// tuple component
	public Object visit(ASTTupleComponent node, Object data) {
		return dump(node, data);
	}
	
	// String literal
	public Object visit(ASTStringLiteral node, Object data) {
		return dump(node, data);
	}
	
	// Push character literal to stack
	public Object visit(ASTCharacter node, Object data) {
		return dump(node, data);
	}
	
	// Push integer literal to stack
	public Object visit(ASTInteger node, Object data) {
		return dump(node, data);
	}

	// Push floating point literal to stack
	public Object visit(ASTRational node, Object data) {
		return dump(node, data);
	}

	// Push true literal to stack
	public Object visit(ASTTrue node, Object data) {
		return dump(node, data);
	}

	// Push false literal to stack
	public Object visit(ASTFalse node, Object data) {
		return dump(node, data);
	}

	@Override
	public Object visit(ASTLambda node, Object data) {
		return dump(node, data);
	}

	@Override
	public Object visit(ASTOpType node, Object data) {
		return dump(node, data);
	}

	@Override
	public Object visit(ASTFnInvokeAnonymous node, Object data) {
		return dump(node, data);
	}

}
