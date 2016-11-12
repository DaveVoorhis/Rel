package org.reldb.rel.v0.interpreter;

import java.util.*;

import org.reldb.rel.exceptions.*;
import org.reldb.rel.v0.generator.*;
import org.reldb.rel.v0.generator.Generator.Summarize.SummarizeItem;
import org.reldb.rel.v0.languages.tutoriald.BaseASTNode;
import org.reldb.rel.v0.languages.tutoriald.parser.*;
import org.reldb.rel.v0.storage.BuiltinTypeBuilder;
import org.reldb.rel.v0.storage.relvars.RelvarHeading;
import org.reldb.rel.v0.storage.relvars.RelvarMetadata;
import org.reldb.rel.v0.storage.relvars.RelvarRealMetadata;
import org.reldb.rel.v0.types.*;
import org.reldb.rel.v0.types.builtin.TypeBoolean;
import org.reldb.rel.v0.types.builtin.TypeCharacter;
import org.reldb.rel.v0.types.builtin.TypeInteger;
import org.reldb.rel.v0.types.builtin.TypeRational;
import org.reldb.rel.v0.types.userdefined.*;
import org.reldb.rel.v0.values.*;
import org.reldb.rel.v0.vm.Context;
import org.reldb.rel.v0.vm.NativeFunction;
import org.reldb.rel.v0.vm.Operator;
import org.reldb.rel.v0.vm.VirtualMachine;

public class TutorialDParser implements TutorialDVisitor {

	// Code generator
	private Generator generator;

	// Current node (for error reporting)
	private BaseASTNode currentNode;
	
	// False if operatorsAreStorable > 0
	private int operatorsAreStorable = 0;
	
	/** Ctor */
	public TutorialDParser(Generator generator) {
		this.generator = generator;
		generator.setParser(this);
	}

	public void beginOperatorsNonStorable() {
		operatorsAreStorable++;
	}
	
	public void endOperatorsNonStorable() {
		operatorsAreStorable--;
	}
	
	public BaseASTNode getCurrentNode() {
		return currentNode;
	}
	
	// Obtain the source code of a given node
	private String getSourceCodeOf(BaseASTNode node) {
		StringBuffer source = new StringBuffer();
		Token currentToken = node.first_token;
		 while (currentToken != null) {
			source.append(currentToken);
			if (currentToken == node.last_token)
				break;
			source.append(' ');
			currentToken = currentToken.next;
		}
		return source.toString().trim();
	}
	
	// Obtain the source code of a child of a given node
	private String getSourceCodeOfChild(SimpleNode node, int childIndex) {
		return getSourceCodeOf(getChild(node, childIndex));
	}
	
	// Compile a given child of the given node
	private Object compileChild(SimpleNode node, int childIndex, Object data) {
		return node.jjtGetChild(childIndex).jjtAccept(this, data);
	}
	
	// Compile all children of the given node
	private void compileChildren(SimpleNode node, Object data) {
		node.childrenAccept(this, data);
	}

	// Get the ith child as a BaseASTNode
	private static BaseASTNode getChild(SimpleNode node, int childIndex) {
		return (BaseASTNode)node.jjtGetChild(childIndex);
	}
	
	// Get the token value of the ith child of a given node.
	private static String getTokenOfChild(SimpleNode node, int childIndex) {
		return getChild(node, childIndex).tokenValue;
	}
	
	// Get the count of children of a given node.
	private static int getChildCount(SimpleNode node) {
		return node.jjtGetNumChildren();
	}
	
	// Get the count of children of a given child.
	private static int getChildCountOfChild(SimpleNode node, int childIndex) {
		return ((SimpleNode)getChild(node, childIndex)).jjtGetNumChildren();
	}
	
	public Object visit(SimpleNode node, Object data) {
		System.out.println(node + ": acceptor not implemented in subclass?");
		return data;
	}

	//
	// Type definition
	//
	
	public Object visit(ASTTypeDef node, Object data) {
		currentNode = node;
		// Child 0 - type name
		String typeName = getTokenOfChild(node, 0);
		// Compile remaining children
		for (int i=1; i<getChildCount(node); i++)
			compileChild(node, i, typeName);
		return null;
	}
	
	public Object visit(ASTTypeDefExternal node, Object data) {
		currentNode = node;
		// data - type name
		String typeName = (String)data;
		// Child 0 - external language name
		String language = getTokenOfChild(node, 0);
		// node.tokenValue - external source code
		String source = node.tokenValue;
		generator.createTypeExternal(typeName, language, source, new References());
		return null;
	}

	/*
	 * Possrep nodes
	 */
	
	public Object visit(ASTTypeDefInternal node, Object data) {
		currentNode = node;
		References references = new References();
		generator.setGlobalReferenceCollector(references);
		// data - type name
		String typeName = (String)data;
		// Create new type
		TypeAlpha typedef = new TypeAlpha(typeName);
		generator.createTypeInternalForwardReference(typedef);
		// Compile children
		for (int i=0; i<getChildCount(node); i++)
			compileChild(node, i, typedef);
		generator.setGlobalReferenceCollector(null);
		generator.createTypeInternal(typedef, getSourceCodeOf(node), references);
		return data;
	}

	public Object visit(ASTTypeDefInternalOptOrdinal node, Object data) {
		currentNode = node;
		((TypeAlpha)data).setOrdinal(true);
		return null;
	}
	
	public Object visit(ASTTypeDefInternalOptOrdered node, Object data) {
		currentNode = node;
		((TypeAlpha)data).setOrdered(true);
		return null;
	}
	
	public Object visit(ASTTypeDefInternalOptUnion node, Object data) {
		currentNode = node;
		((TypeAlpha)data).setUnion(true);
		return null;
	}
	
	public Object visit(ASTSingleInheritanceIsDef node, Object data) {
		currentNode = node;
		// Data = TypeUserdefined
		TypeAlpha udt = (TypeAlpha)data;
		// Child 0 - parent type identifier
		String supertypeName = getTokenOfChild(node, 0);
		Type supertype = generator.findType(supertypeName);
		if (!(supertype instanceof TypeAlpha))
			throw new ExceptionSemantic("RS0099: IS must specify a user-defined TYPE as a supertype.  " + supertype.getSignature() + " is not a user-defined TYPE.");
		udt.setSupertype((TypeAlpha)supertype);
		// Child 1 - n - PossrepOrSpecializationDetails 
		for (int i=1; i<getChildCount(node); i++)
			compileChild(node, i, udt);
		return null;
	}

	public Object visit(ASTMultipleInheritanceIsDef node, Object data) {
		currentNode = node;
		// Data = TypeUserdefined
		MultipleInheritance mi = new MultipleInheritance();
		// Child 0 - scalar_type_name_commalist
		compileChild(node, 0, mi);
		// Child 1 - derived_possrep_def_list
		compileChild(node, 1, mi);
		// Set this type definition to be multiple inheritance
		((TypeAlpha)data).setMultipleInheritance(mi);
		return null;
	}

	public Object visit(ASTScalarTypeName node, Object data) {
		currentNode = node;
		// Data = MultipleInheritance
		String identifier = getTokenOfChild(node, 0);
		// Add type name to MultipleInheritance
		((MultipleInheritance)data).addScalarTypeName(identifier);
		return null;
	}
	
	public Object visit (ASTPossrepInitialiser node, Object data) {
		// Data = TypeUserdefined
		compileChildren(node, data);
		if (getChildCount(node) > 0)
			((TypeAlpha)data).checkPossrepInitialisation();
		return null;
	}

	// Not per TTM
	public Object visit(ASTPossrepInitialiserAssignments node, Object data) {
		currentNode = node;
		// Data = TypeUserdefined
		// Child 0 = identifier() = possrep name
		String identifier = getTokenOfChild(node, 0);
		Generator.PossrepInitialisation possrepInit = generator.new PossrepInitialisation(((TypeAlpha)data), identifier);
		// Child 1 = assignment()
		compileChild(node, 1, data);
		possrepInit.endPossrepInitialisation();
		return null;
	}

	public Object visit(ASTPossrepDef node, Object data) {
		currentNode = node;
		// Data = TypeUserdefined
		TypeAlpha udt = (TypeAlpha)data;
		// Child 0 - PossrepDefIdentifier
		String possrepName = (String)compileChild(node, 0, data);
		Possrep possrep = new Possrep(udt, possrepName);
		// Child 1 - PossrepDefComponentCommalist
		compileChild(node, 1, possrep);
		// Child 2 - PossrepDefConstraintDef
		compileChild(node, 2, possrep);
		return data;
	}
	
	public Object visit(ASTPossrepDefIdentifier node, Object data) {
		currentNode = node;
		// Data = TypeUserdefined
		// Child 0 - optional identifier
		if (getChildCount(node) > 0)
			return getTokenOfChild(node, 0);
		else
			return ((TypeAlpha)data).getTypeName();
	}

	public Object visit(ASTPossrepDefConstraintDef node, Object data) {
		currentNode = node;
		// Data = Possrep
		if (getChildCount(node) > 0) {
			Possrep possrep = (Possrep)data;
			Generator.PossrepConstraint constraint = generator.new PossrepConstraint(possrep);
			// Compile <boolexpr>
			compileChildren(node, data);
			// End constraint
			constraint.endPossrepConstraint();
		}
		return data;
	}

	public Object visit(ASTPossrepDefComponentCommalist node, Object data) {
		currentNode = node;
		// Data = Possrep
		compileChildren(node, data);
		return data;
	}

	public Object visit(ASTPossrepDefComponent node, Object data) {
		currentNode = node;
		// Data = Possrep
		// Child 0 - Identifier
		String componentName = getTokenOfChild(node, 0);
		// Child 1 - Type
		Type componentType = (Type)compileChild(node, 1, data);
		// Add this component to the Possrep
		new PossrepComponent((Possrep)data, componentName, componentType);
		return data;
	}

	public Object visit(ASTPossrepConstraintDef node, Object data) {
		currentNode = node;
		// Data = Possrep
		compileChildren(node, data);
		return data;
	}

	public Object visit(ASTSpecialisationConstraintDef node, Object data) {
		currentNode = node;
		// Data = TypeUserdefined
		TypeAlpha udt = (TypeAlpha)data;
		Generator.SpecialisationConstraint constraint = generator.new SpecialisationConstraint(udt);
		// Compile <boolexpr>
		compileChildren(node, data);
		// End constraint
		constraint.endSpecialisationConstraint();
		return data;
	}
	
	public Object visit(ASTDerivedPossrepDef node, Object data) {
		currentNode = node;
		// Data = TypeUserdefined
		// Child 0 = possrep name
		String possrepName = (String)compileChild(node, 0, data);
		TypeAlpha udt = (TypeAlpha)data;
		Possrep possrep = new DerivedPossrep(udt, possrepName);
		for (int i=1; i<getChildCount(node); i++)
			compileChild(node, i, possrep);
		return data;
	}
	
	public Object visit(ASTDerivedPossrepDefOptIdentifier node, Object data) {
		currentNode = node;
		// Data = TypeUserdefined
		// Child 0 - optional identifier
		if (getChildCount(node) > 0)
			return getTokenOfChild(node, 0);
		else
			return ((TypeAlpha)data).getTypeName();
	}

	public Object visit(ASTDerivedPossrepComponentDef node, Object data) {
		currentNode = node;
		// Data = DerivedPossrep
		// Child 0 - new identifier
		String newIdentifier = getTokenOfChild(node, 0);
		// Child 1 - old identifier in the form THE_x
		String oldIdentifier = getTokenOfChild(node, 1);
		// Child 2 - supertype identifier
		String supertypeName = getTokenOfChild(node, 2);
		((DerivedPossrep)data).addDerivation(newIdentifier, oldIdentifier, supertypeName);
		return data;
	}

	/*
	 * End possrep nodes
	 */	
	
	//
	// End type definition
	//
	
	// Compile a Rel program.  Return the main operator definition as a RelOperatorDefinition. 
	public Object visit(ASTCode node, Object data) {
		currentNode = node;
		generator.beginCompilation();
		// Compile all children of this node, which compiles the whole program.
		compileChildren(node, data);
		return generator.endCompilation();		
	}
	
	// Evaluate a Rel expression.  Return the main operator definition as a RelOperatorDefinition.
	public Object visit(ASTEvaluate node, Object data) {
		currentNode = node;
		generator.beginCompilation();
		// Compile children of this node, which compiles the whole expression.
		Type type = null;
		for (int i=0; i<getChildCount(node); i++)
			type = (Type)compileChild(node, i, data);
		generator.setDeclaredReturnType(type);
		generator.compileReturnValue(type);
		return generator.endCompilation();		
	}
	
	// Statement list
	public Object visit(ASTStatementList node, Object data) {
		currentNode = node;
		compileChildren(node, data);
		return null;
	}

	// Statement
	public Object visit(ASTStatement node, Object data) {
		currentNode = node;
		compileChildren(node, data);
		return null;
	}

	// Obtain operator return type
	public Object visit(ASTGetOperatorReturnType node, Object data) {
		currentNode = node;
		generator.setCompilingOff();
		try {
			// Child 1 - return definition (Type)
			return compileChild(node, 1, data);
		} finally {
			generator.setCompilingOn();
		}
	}
	
	// Obtain heading
	public Object visit(ASTGetHeading node, Object data) {
		currentNode = node;
		generator.setCompilingOff();
		try {
			// Child 0 - return definition (Heading)
			return compileChild(node, 0, data);	
		} finally {
			generator.setCompilingOn();
		}
	}

	// Obtain operator signature
	public Object visit(ASTGetSignature node, Object data) {
		currentNode = node;
		generator.setCompilingOff();
		try {
			// Child 0 - return OperatorSignature
			OperatorSignature sig = (OperatorSignature)compileChild(node, 0, data);
			// Child 1 - get RETURN type
			compileChild(node, 1, data);
			sig.setReturnType(generator.getDeclaredReturnType());
			return sig;
		} finally {
			generator.setCompilingOn();
		}
	}

	public Object visit(ASTBackup node, Object data) {
		generator.backup();
		return null;
	}
	
	public Object visit(ASTWrite node, Object data) {
		// Child 0 - expression
		generator.compileWrite((Type)compileChild(node, 0, data));
		return null;
	}
	
	public Object visit(ASTWriteln node, Object data) {
		if (getChildCount(node) > 0) {
			// Child 0 - expression
			generator.compileWriteln((Type)compileChild(node, 0, data));
		} else
			generator.compileWritelnNoExpression();
		return null;
	}
	
	public Object visit(ASTOutput node, Object data) {
		// Child 0 - expression
		generator.compileOutput((Type)compileChild(node, 0, data));
		return null;
	}
	
	public Object visit(ASTAnnounce node, Object data) {
		// Child 0 - string_literal
		generator.announce(getTokenOfChild(node, 0));
		return null;
	}
		
	public Object visit(ASTExecute node, Object data) {
		// Child 0 - expression - should be TypeCharacter
		Type exprType = (Type)compileChild(node, 0, data);
		if (!(exprType instanceof TypeCharacter))
			throw new ExceptionSemantic("RS0100: Expected expression of type CHARACTER but got " + exprType);
		generator.compileExecute();
		return null;
	}
	
	public Object visit(ASTSet node, Object data) {
		// Child 0 - attribute
		// Child 1 - value
		generator.set(getTokenOfChild(node, 0), getTokenOfChild(node, 1));
		return null;
	}

	public Object visit(ASTTransactionBegin node, Object data) {
		currentNode = node;
		generator.compileTransactionBegin();
		return null;
	}

	public Object visit(ASTTransactionCommit node, Object data) {
		currentNode = node;
		generator.compileTransactionCommit();
		return null;
	}

	public Object visit(ASTTransactionRollback node, Object data) {
		currentNode = node;
		generator.compileTransactionRollback();
		return null;
	}
	
	// Update statement optional WHERE clause.
	public Object visit(ASTUpdateWhere node, Object data) {
		currentNode = node;
		Type type = (Type)compileChild(node, 0, data);
		if (!(type instanceof TypeBoolean))
			throw new ExceptionSemantic("RS0101: Expected BOOLEAN in WHERE of UPDATE, but got " + type);
		return null;
	}
	
	// Expression
	public Object visit(ASTExpression node, Object data) {
		currentNode = node;
		Type returnType = null;
		for (int i=0; i<getChildCount(node); i++)
			returnType = (Type)compileChild(node, i, data);
		return returnType;
	}
	
	// Update expression assignment statements
	public Object visit(ASTUpdateAssignment node, Object data) {
		currentNode = node;
		compileChildren(node, data);
		return null;
	}
	
	// Substitute
	public Object visit(ASTSubstitute node, Object data) {
		currentNode = node;
		// Child 0 - expression
		Type exprType = (Type)compileChild(node, 0, data);
		if (exprType instanceof TypeTuple) {
			Generator.TupleSubstitute tupleSubstitute = generator.new TupleSubstitute((TypeTuple)exprType);
			// Child 1 - update assignment statements
			compileChild(node, 1, data);
			return tupleSubstitute.endTupleSubstitute();
		} else if (exprType instanceof TypeRelation) {
			Generator.RelationSubstitute relationSubstitute = generator.new RelationSubstitute((TypeRelation)exprType);
			// Child 1 - update assignment statements
			compileChild(node, 1, data);
			return relationSubstitute.endRelationSubstitute();
		} else
			throw new ExceptionSemantic("RS0102: Expected TUPLE or RELATION, but got " + exprType);
	}

	// DIVIDEBY
	public Object visit(ASTAlgDivide node, Object data) {
		currentNode = node;
		// Child0 - r1
		Type r1Type = (Type)compileChild(node, 0, data);
		if (!(r1Type instanceof TypeRelation))
			throw new ExceptionSemantic("RS0103: DIVIDEBY expected RELATION as first operand, but got " + r1Type);
		// Child1 - r2
		Type r2Type = (Type)compileChild(node, 1, data);
		if (!(r2Type instanceof TypeRelation))
			throw new ExceptionSemantic("RS0104: DIVIDEBY expected RELATION as second operand, but got " + r2Type);
		// Child3 - r3
		Type r3Type = (Type)compileChild(node, 2, data);
		if (!(r3Type instanceof TypeRelation))
			throw new ExceptionSemantic("RS0105: DIVIDEBY expected RELATION as third operand, but got " + r3Type);
		// Child4 - AlgDividePerOptional - if not null, it's r4
		Type r4Type = (Type)compileChild(node, 3, data);
		if (r4Type == null)
			return generator.compileSmallDivide((TypeRelation)r1Type, (TypeRelation)r2Type, (TypeRelation)r3Type);
		else {
			if (!(r4Type instanceof TypeRelation))
				throw new ExceptionSemantic("RS0106: DIVIDEBY expected RELATION as fourth operand, but got " + r4Type);
			return generator.compileGreatDivide((TypeRelation)r1Type, (TypeRelation)r2Type, (TypeRelation)r3Type, (TypeRelation)r4Type);
		}
	}

	// DIVIDEBY 'per' optional term
	public Object visit(ASTAlgDividePerOptional node, Object data) {
		currentNode = node;
		// Child0 if present, is r4
		if (getChildCount(node) > 0)
			return compileChild(node, 0, data);
		return null;
	}
	
	// Tuple attribute from
	public Object visit(ASTAttributeFrom node, Object data) {
		currentNode = node;
		// Child 0 - attribute name
		String name = getTokenOfChild(node, 0);
		// Child 1 - tuple expression
		Type tupleExpression = (Type)compileChild(node, 1, data);
		if (!(tupleExpression instanceof TypeTuple))
			throw new ExceptionSemantic("RS0107: Expected TUPLE, but got " + tupleExpression);
		return generator.compileTupleGetAttribute((TypeTuple)tupleExpression, name);
	}

	// Tuple from
	public Object visit(ASTTupleFrom node, Object data) {
		currentNode = node;
		// Child 0 - relation expression
		Type relationExpression = (Type)compileChild(node, 0, data);
		if (!(relationExpression instanceof TypeRelation))
			throw new ExceptionSemantic("RS0108: Expected RELATION, but got " + relationExpression);
		return generator.compileRelationGetTuple((TypeRelation)relationExpression);	
	}

	private abstract class NadicDefinition {
		abstract TypeTuple compileTupleOperation(TypeTuple left, TypeTuple right);
		abstract TypeRelation compileRelationOperation(TypeRelation left, TypeRelation right);
		abstract TypeHeading compileNadicEmptyList();
		void checkNoExpressionsAllowed() {}
	}

	private abstract class NadicDefinitionWithoutHeading extends NadicDefinition {
		TypeHeading compileNadicEmptyList() {
			generator.compilePush(ValueRelation.getDee(generator));
			return TypeRelation.getEmptyRelationType();
		}		
	}
	
	private abstract class NadicDefinitionWithOptionalHeading extends NadicDefinition {
		TypeHeading compileNadicEmptyList() {
			generator.compilePush(ValueTuple.getEmptyTuple(generator));
			return TypeTuple.getEmptyTupleType();
		}
	}
	
	// n-adic relation/tuple operator definer without heading
	private Type defineNadicWithoutHeading(SimpleNode node, NadicDefinition nadic) {
		// Child 0 - HeadingExpCommalist of TUPLE or RELATION expressions
		Type[] types = (Type[])compileChild(node, 0, null);
		if (types.length == 0) {
			nadic.checkNoExpressionsAllowed();
			return nadic.compileNadicEmptyList();
		} else {
			Type returnType = types[types.length - 1];
			if (returnType instanceof TypeTuple) {
				for (int i=types.length - 1; i > 0; i--)
					returnType = nadic.compileTupleOperation((TypeTuple)types[i - 1], (TypeTuple)returnType);
			} else if (returnType instanceof TypeRelation) {
				for (int i=types.length - 1; i > 0; i--)
					returnType = nadic.compileRelationOperation((TypeRelation)types[i - 1], (TypeRelation)returnType);
			}
			return returnType;
		}	
	}
	
	// n-adic relation/tuple operator definer with optional heading
	private Type defineNadicWithOptionalHeading(SimpleNode node, NadicDefinition nadic) {
		if (getChildCount(node) == 2) {
			// Child 0 - NadicHeading
			Heading heading = (Heading)compileChild(node, 0, null);
			// Child 1 - HeadingExpCommalist of RELATION expressions
			Type[] types = (Type[])compileChild(node, 1, heading);
			if (types.length == 0) {
				nadic.checkNoExpressionsAllowed();
				Generator.RelationDefinition relationDefinition = generator.new RelationDefinition(heading);
				relationDefinition.endRelation();
				return new TypeRelation(heading);
			} else {
				Type returnType = types[types.length - 1];
				if (!(returnType instanceof TypeRelation))
					throw new ExceptionSemantic("RS0109: Expected expression(s) of type RELATION, but got " + returnType);
				for (int i=types.length - 1; i > 0; i--)
					returnType = nadic.compileRelationOperation((TypeRelation)types[i - 1], (TypeRelation)returnType);
				return returnType;
			}
		} else
			return defineNadicWithoutHeading(node, nadic);
	}

	// optional n-adic relation operator heading.  Return Heading if present.
	public Object visit(ASTNadicHeading node, Object data) {
		currentNode = node;
		return (Heading)compileChild(node, 0, data);
	}

	// Heading expression commalist.  Return Vector of expression TypeS.
	public Object visit(ASTHeadingExpCommalist node, Object data) {
		currentNode = node;
		// data is optional Heading.  If not null, all expressions must be of RELATION type
		// and match the heading.  If null, all expressions must be of TUPLE type or RELATION
		// type but not both.
		Heading heading = null;
		if (data != null)
			heading = (Heading)data;
		Type firstType = null;
		Type types[] = new Type[getChildCount(node)];
		for (int i = 0; i < types.length; i++) {
			Type type = (Type)compileChild(node, i, data);
			if (i == 0) {
				firstType = type;
				if (!(type instanceof TypeHeading))
					throw new ExceptionSemantic("RS0110: Expected expression of type TUPLE or RELATION, but got " + type);
			}
			else if (firstType instanceof TypeTuple && !(type instanceof TypeTuple))
				throw new ExceptionSemantic("RS0111: Expected TUPLE, but got " + type);
			else if (firstType instanceof TypeRelation && !(type instanceof TypeRelation))
				throw new ExceptionSemantic("RS0112: Expected expression of type " + firstType + " but got " + type);
			if (heading != null) {
				if (!(type instanceof TypeRelation) || !heading.canAccept(((TypeRelation)type).getHeading()))
					throw new ExceptionSemantic("RS0113: Expected expression of type RELATION " + heading + " but got " + type);						
			}
			types[i] = type;
		}
		return types;
	}
	
	// n-adic UNION
	public Object visit(ASTNadicUnion node, Object data) {
		currentNode = node;
		return defineNadicWithOptionalHeading(node, new NadicDefinitionWithOptionalHeading() {
			TypeTuple compileTupleOperation(TypeTuple left, TypeTuple right) {
				return generator.compileTupleJoin(left, right);
			}
			TypeRelation compileRelationOperation(TypeRelation left, TypeRelation right) {
				return generator.compileRelationUnion(left, right);
			}
		});
	}

	// n-adic XUNION
	public Object visit(ASTNadicXunion node, Object data) {
		currentNode = node;
		return defineNadicWithOptionalHeading(node, new NadicDefinitionWithOptionalHeading() {
			TypeTuple compileTupleOperation(TypeTuple left, TypeTuple right) {
				return generator.compileTupleCompose(left, right);
			}
			TypeRelation compileRelationOperation(TypeRelation left, TypeRelation right) {
				return generator.compileRelationXunion(left, right);
			}
		});
	}

	// n-adic D_UNION
	public Object visit(ASTNadicDUnion node, Object data) {
		currentNode = node;
		return defineNadicWithOptionalHeading(node, new NadicDefinitionWithOptionalHeading() {
			TypeTuple compileTupleOperation(TypeTuple left, TypeTuple right) {
				return generator.compileTupleDUnion(left, right);
			}
			TypeRelation compileRelationOperation(TypeRelation left, TypeRelation right) {
				return generator.compileRelationDUnion(left, right);
			}
		});
	}

	// n-adic INTERSECT
	public Object visit(ASTNadicIntersect node, Object data) {
		currentNode = node;
		return defineNadicWithOptionalHeading(node, new NadicDefinitionWithOptionalHeading() {
			TypeTuple compileTupleOperation(TypeTuple left, TypeTuple right) {
				return generator.compileTupleIntersect(left, right);
			}
			TypeRelation compileRelationOperation(TypeRelation left, TypeRelation right) {
				return generator.compileRelationIntersect(left, right);
			}
			void checkNoExpressionsAllowed() {
				throw new ExceptionSemantic("RS0114: Expression list for n-adic INTERSECT cannot be empty.");				
			}
		});
	}
	
	// n-adic JOIN
	public Object visit(ASTNadicJoin node, Object data) {
		currentNode = node;
		return defineNadicWithoutHeading(node, new NadicDefinitionWithoutHeading() {
			TypeTuple compileTupleOperation(TypeTuple left, TypeTuple right) {
				return generator.compileTupleJoin(left, right);
			}
			TypeRelation compileRelationOperation(TypeRelation left, TypeRelation right) {
				return generator.compileRelationJoin(left, right);
			}
		});
	}

	// n-adic TIMES
	public Object visit(ASTNadicTimes node, Object data) {
		currentNode = node;
		return defineNadicWithoutHeading(node, new NadicDefinitionWithoutHeading() {
			TypeTuple compileTupleOperation(TypeTuple left, TypeTuple right) {
				return generator.compileTupleJoin(left, right);
			}
			TypeRelation compileRelationOperation(TypeRelation left, TypeRelation right) {
				return generator.compileRelationTimes(left, right);
			}
		});
	}

	// n-adic COMPOSE
	public Object visit(ASTNadicCompose node, Object data) {
		currentNode = node;
		return defineNadicWithoutHeading(node, new NadicDefinitionWithoutHeading() {
			TypeTuple compileTupleOperation(TypeTuple left, TypeTuple right) {
				return generator.compileTupleCompose(left, right);
			}
			TypeRelation compileRelationOperation(TypeRelation left, TypeRelation right) {
				return generator.compileRelationCompose(left, right);
			}
		});
	}

	private abstract class BinaryDefinition {
		abstract String getName();
		abstract TypeTuple compileTupleOperation(TypeTuple left, TypeTuple right);
		abstract TypeRelation compileRelationOperation(TypeRelation left, TypeRelation right);
	}
	
	// Binary relation/tuple operator definer.
	private Type defineBinary(SimpleNode node, BinaryDefinition binary) {
		currentNode = node;
		// Child 0 - left hand operand
		Type leftType = (Type)compileChild(node, 0, null);
		// Child 1 - right hand operand
		Type rightType = (Type)compileChild(node, 1, null);		
		if (leftType instanceof TypeTuple && rightType instanceof TypeTuple) {
			return binary.compileTupleOperation((TypeTuple)leftType, (TypeTuple)rightType);
		} else if (leftType instanceof TypeRelation && rightType instanceof TypeRelation) {
			return binary.compileRelationOperation((TypeRelation)leftType, (TypeRelation)rightType);
		} else
			throw new ExceptionSemantic("RS0115: Cannot perform " + binary.getName() + " on " + leftType + " with " + rightType);		
	}
	
	// D_UNION
	public Object visit(ASTAlgDUnion node, Object data) {
		currentNode = node;
		return defineBinary(node, new BinaryDefinition() {
			String getName() {return "D_UNION";}
			TypeTuple compileTupleOperation(TypeTuple left, TypeTuple right) {
				return generator.compileTupleDUnion(left, right);				
			}
			TypeRelation compileRelationOperation(TypeRelation left, TypeRelation right) {
				return generator.compileRelationDUnion(left, right);
			}
		});
	}
	
	// Semijoin
	public Object visit(ASTAlgSemijoin node, Object data) {
		currentNode = node;
		return defineBinary(node, new BinaryDefinition() {
			String getName() {return "SEMIJOIN";}
			TypeTuple compileTupleOperation(TypeTuple left, TypeTuple right) {
				return generator.compileTupleSemijoin(left, right);
			}
			TypeRelation compileRelationOperation(TypeRelation left, TypeRelation right) {
				return generator.compileRelationSemijoin(left, right);
			}
		});
	}
	
	// Semiminus
	public Object visit(ASTAlgSemiminus node, Object data) {
		currentNode = node;
		return defineBinary(node, new BinaryDefinition() {
			String getName() {return "SEMIMINUS";}
			TypeTuple compileTupleOperation(TypeTuple left, TypeTuple right) {
				return generator.compileTupleSemiminus(left, right);
			}
			TypeRelation compileRelationOperation(TypeRelation left, TypeRelation right) {
				return generator.compileRelationSemiminus(left, right);
			}
		});
	}
	
	// Compose
	public Object visit(ASTAlgCompose node, Object data) {
		currentNode = node;
		return defineBinary(node, new BinaryDefinition() {
			String getName() {return "COMPOSE";}
			TypeTuple compileTupleOperation(TypeTuple left, TypeTuple right) {
				return generator.compileTupleCompose(left, right);
			}
			TypeRelation compileRelationOperation(TypeRelation left, TypeRelation right) {
				return generator.compileRelationCompose(left, right);
			}
		});
	}
	
	// Minus
	public Object visit(ASTAlgMinus node, Object data) {
		currentNode = node;
		return defineBinary(node, new BinaryDefinition() {
			String getName() {return "MINUS";}
			TypeTuple compileTupleOperation(TypeTuple left, TypeTuple right) {
				return generator.compileTupleMinus(left, right);
			}
			TypeRelation compileRelationOperation(TypeRelation left, TypeRelation right) {
				return generator.compileRelationMinus(left, right);
			}
		});
	}

	// I_MINUS
	public Object visit(ASTAlgIMinus node, Object data) {
		currentNode = node;
		return defineBinary(node, new BinaryDefinition() {
			String getName() {return "I_MINUS";}
			TypeTuple compileTupleOperation(TypeTuple left, TypeTuple right) {
				return generator.compileTupleIMinus(left, right);
			}
			TypeRelation compileRelationOperation(TypeRelation left, TypeRelation right) {
				return generator.compileRelationIMinus(left, right);
			}
		});
	}
	
	// Intersect
	public Object visit(ASTAlgIntersect node, Object data) {
		currentNode = node;
		return defineBinary(node, new BinaryDefinition() {
			String getName() {return "INTERSECT";}
			TypeTuple compileTupleOperation(TypeTuple left, TypeTuple right) {
				return generator.compileTupleIntersect(left, right);
			}
			TypeRelation compileRelationOperation(TypeRelation left, TypeRelation right) {
				return generator.compileRelationIntersect(left, right);
			}
		});
	}
	
	// Union
	public Object visit(ASTAlgUnion node, Object data) {
		currentNode = node;
		return defineBinary(node, new BinaryDefinition() {
			String getName() {return "UNION";}
			TypeTuple compileTupleOperation(TypeTuple left, TypeTuple right) {
				return generator.compileTupleJoin(left, right);
			}
			TypeRelation compileRelationOperation(TypeRelation left, TypeRelation right) {
				return generator.compileRelationUnion(left, right);
			}
		});
	}

	// Xunion
	public Object visit(ASTAlgXunion node, Object data) {
		currentNode = node;
		return defineBinary(node, new BinaryDefinition() {
			String getName() {return "XUNION";}
			TypeTuple compileTupleOperation(TypeTuple left, TypeTuple right) {
				return generator.compileTupleCompose(left, right);
			}
			TypeRelation compileRelationOperation(TypeRelation left, TypeRelation right) {
				return generator.compileRelationXunion(left, right);
			}
		});
	}
	
	// Join
	public Object visit(ASTAlgJoin node, Object data) {
		currentNode = node;
		return defineBinary(node, new BinaryDefinition() {
			String getName() {return "JOIN";}
			TypeTuple compileTupleOperation(TypeTuple left, TypeTuple right) {
				return generator.compileTupleJoin(left, right);
			}
			TypeRelation compileRelationOperation(TypeRelation left, TypeRelation right) {
				return generator.compileRelationJoin(left, right);
			}
		});
	}

	// Times
	public Object visit(ASTAlgTimes node, Object data) {
		currentNode = node;
		return defineBinary(node, new BinaryDefinition() {
			String getName() {return "TIMES";}
			TypeTuple compileTupleOperation(TypeTuple left, TypeTuple right) {
				return generator.compileTupleJoin(left, right);
			}
			TypeRelation compileRelationOperation(TypeRelation left, TypeRelation right) {
				return generator.compileRelationTimes(left, right);
			}
		});
	}
		
	private abstract class UnaryDefinition {
		abstract String getName();
		abstract TypeTuple compileTupleOperation(TypeTuple operand);
		abstract TypeRelation compileRelationOperation(TypeRelation operand);
	}
	
	// Unary relation/tuple operator definer.
	private Type defineUnary(SimpleNode node, UnaryDefinition unary) {
		currentNode = node;
		// Child 0 - source expression
		Type sourceType = (Type)compileChild(node, 0, null);
		if (sourceType instanceof TypeTuple) {			
			return unary.compileTupleOperation((TypeTuple)sourceType);
		} else if (sourceType instanceof TypeRelation) {
			return unary.compileRelationOperation((TypeRelation)sourceType);
		} else
			throw new ExceptionSemantic("RS0116: Cannot perform " + unary.getName() + " on " + sourceType);
	}

	// Unwrap
	public Object visit(final ASTAlgUnwrap node, Object data) {
		currentNode = node;
		return defineUnary(node, new UnaryDefinition() {
			String getName() {return "UNWRAP";}
			TypeTuple compileTupleOperation(TypeTuple operand) {
				// Child 1 - identifier
				String name = getTokenOfChild(node, 1);
				return generator.compileTupleUnwrap(operand, name);		
			}
			TypeRelation compileRelationOperation(TypeRelation operand) {
				// Child 1 - identifier
				String name = getTokenOfChild(node, 1);
				return generator.compileRelationUnwrap(operand, name);		
			}
		});
	}
	
	// Wrap
	public Object visit(final ASTAlgWrap node, Object data) {
		currentNode = node;
		return defineUnary(node, new UnaryDefinition() {
			String getName() {return "WRAP";}
			TypeTuple compileTupleOperation(TypeTuple operand) {
				// Child 1 - wrapping list
				return (TypeTuple)compileChild(node, 1, operand);
			}
			TypeRelation compileRelationOperation(TypeRelation operand) {
				// Child 1 - wrapping list
				return (TypeRelation)compileChild(node, 1, operand);
			}
		});
	}

	// Wrapping item
	public Object visit(ASTWrappingItem node, Object data) {
		currentNode = node;
		// data is Type of expression that will be available on the stack
		Type sourceType = (Type)data;
		// Child 0 - AttributeNameList -- compilation returns AttributeSelection
		SelectAttributes selection = (SelectAttributes)compileChild(node, 0, data);
		// Child 1 - identifier -- name of new attribute
		String name = getTokenOfChild(node, 1);
		if (sourceType instanceof TypeTuple)
			return generator.compileTupleWrap((TypeTuple)sourceType, selection, name);
		else if (sourceType instanceof TypeRelation)
			return generator.compileRelationWrap((TypeRelation)sourceType, selection, name);
		else
			throw new ExceptionSemantic("RS0309: Unable to WRAP " + sourceType);
	}

	// Group
	public Object visit(ASTGroup node, Object data) {
		currentNode = node;
		// Child 0 - source
		Type sourceType = (Type)compileChild(node, 0, data);
		// Child 1 - AttributeNameList -- compilation returns AttributeSelection
		SelectAttributes selection = (SelectAttributes)compileChild(node, 1, data);
		// Child 2 - identifier -- name of new attribute
		String name = getTokenOfChild(node, 2);
		if (!(sourceType instanceof TypeRelation))
			throw new ExceptionSemantic("RS0310: Unable to GROUP " + sourceType);
		return generator.compileRelationGroup((TypeRelation)sourceType, selection, name);
	}
	
	// Ungroup
	public Object visit(ASTAlgUngroup node, Object data) {
		currentNode = node;
		// Child 0 - source
		Type sourceType = (Type)compileChild(node, 0, data);
		// Child 1 - attribute name
		String name = getTokenOfChild(node, 1);
		if (!(sourceType instanceof TypeRelation))
			throw new ExceptionSemantic("RS0311: Unable to UNGROUP " + sourceType);
		return generator.compileRelationUngroup((TypeRelation)sourceType, name);	
	}
	
	// LOAD ... FROM ...
	public Object visit(ASTRelationArrayLoad node, Object data) {
		currentNode = node;
		// Child 0 - identifier
		String identifier = getTokenOfChild(node, 0);
		// Child 1 - expression
		Type expressionType = (Type)compileChild(node, 1, data);
		generator.compileLoad(identifier, expressionType);
		return null;
	}
	
	// ORDER
	public Object visit(ASTAlgOrder node, Object data) {
		currentNode = node;
		// Child 0 - source
		Type sourceType = (Type)compileChild(node, 0, data);
		// Child 1 - SelectOrder via ASTOrderItemCommalist
		SelectOrder orderItems = (SelectOrder)compileChild(node, 1, data);
		if (sourceType instanceof TypeRelation || sourceType instanceof TypeArray)
			return generator.compileOrder((TypeHeading)sourceType, orderItems);
		else
			throw new ExceptionSemantic("RS0117: Unable to perform ORDER on " + sourceType + "; expected a RELATION or ARRAY.");		
	}
	
	// UNORDER
	public Object visit(ASTAlgUnorder node, Object data) {
		currentNode = node;
		// Child 0 - source
		Type sourceType = (Type)compileChild(node, 0, data);
		if (!(sourceType instanceof TypeArray))
			throw new ExceptionSemantic("RS0444: Unable to perform UNORDER on " + sourceType + "; expected an ARRAY.");		
		return generator.compileArrayUnorder((TypeArray)sourceType);
	}
	
	// ORDER item commalist
	public Object visit(ASTOrderItemCommalist node, Object data) {
		currentNode = node;
		SelectOrder orderItems = new SelectOrder();
		compileChildren(node, orderItems);
		return orderItems;
	}
	
	public Object visit(ASTOrderItemAsc node, Object data) {
		currentNode = node;
		// data is OrderItems
		SelectOrder orderItems = (SelectOrder)data;
		// Child 0 - attribute identifier
		String identifier = getTokenOfChild(node, 0);
		orderItems.add(identifier, SelectOrder.Order.ASC);
		return null;
	}

	public Object visit(ASTOrderItemDesc node, Object data) {
		currentNode = node;
		// data is OrderItems
		SelectOrder orderItems = (SelectOrder)data;
		// Child 0 - attribute identifier
		String identifier = getTokenOfChild(node, 0);
		orderItems.add(identifier, SelectOrder.Order.DESC);
		return null;
	}
	
	// Extend
	public Object visit(final ASTExtend node, Object data) {
		currentNode = node;
		return defineUnary(node, new UnaryDefinition() {
			String getName() {return "EXTEND";}
			TypeTuple compileTupleOperation(TypeTuple operand) {
				Generator.Extend extend = generator.new Extend(operand.getHeading());
				// Child 1 - ExtendList
				compileChild(node, 1, extend);
				return generator.endTupleExtend(extend);
			}
			TypeRelation compileRelationOperation(TypeRelation operand) {
				Generator.Extend extend = generator.new Extend(operand.getHeading());
				// Child 1 - ExtendList
				compileChild(node, 1, extend);
				return generator.endRelationExtend(extend);
			}
		});
	}
	
	// Extend list.
	public Object visit(ASTExtendList node, Object data) {
		currentNode = node;
		compileChildren(node, data);
		return null;
	}

	// Extend item
	public Object visit(ASTExtendItem node, Object data) {
		currentNode = node;
		// data is either Generator.TupleExtend or Generator.RelationExtend	
		// Child 0 - identifier
		String identifier = getTokenOfChild(node, 0);
		// Child 1 - expression
		Type expressionType = (Type)compileChild(node, 1, data);
		((Generator.Extend)data).addExtendItem(identifier, expressionType);
		return null;
	}
	
	// Project.  Return Type of projection.
	public Object visit(final ASTAlgProject node, Object data) {
		currentNode = node;
		return defineUnary(node, new UnaryDefinition() {
			String getName() {return "projection";}
			TypeTuple compileTupleOperation(TypeTuple operand) {
				// Child 1 - AttributeNameList
				SelectAttributes attributes = (SelectAttributes)compileChild(node, 1, null);
				return generator.compileTupleProject(operand, attributes);
			}
			TypeRelation compileRelationOperation(TypeRelation operand) {
				// Child 1 - AttributeNameList
				SelectAttributes attributes = (SelectAttributes)compileChild(node, 1, null);
				return generator.compileRelationProject(operand, attributes);
			}
		});
	}
		
	// WHERE
	public Object visit(final ASTAlgWhere node, Object data) {
		currentNode = node;
		return defineUnary(node, new UnaryDefinition() {
			String getName() {return "WHERE";}
			TypeTuple compileTupleOperation(TypeTuple operand) {
				throw new ExceptionSemantic("RS0118: WHERE expected relation, but got " + operand);
			}
			TypeRelation compileRelationOperation(TypeRelation operand) {
				Generator.Where where = generator.new Where(operand);
				// Child 1 - boolean expression
				Type expressionType = (Type)compileChild(node, 1, null);
				if (!(expressionType instanceof TypeBoolean))
					throw new ExceptionSemantic("RS0119: WHERE expression expected boolean, but got " + expressionType);				
				return where.endWhere();
			}
		});
	}
	
	// Rename.  Return Type of rename.
	public Object visit(final ASTAlgRename node, Object data) {
		currentNode = node;
		return defineUnary(node, new UnaryDefinition() {
			String getName() {return "RENAME";}
			TypeTuple compileTupleOperation(TypeTuple operand) {
				// Child 1 - renaming list
				Heading newHeading = new Heading(operand);
				compileChild(node, 1, newHeading);
				return new TypeTuple(newHeading);
			}
			TypeRelation compileRelationOperation(TypeRelation operand) {
				// Child 1 - renaming list
				Heading newHeading = new Heading(operand);
				compileChild(node, 1, newHeading);
				return new TypeRelation(newHeading);
			}
		});
	}
	
	// Renaming list
	public Object visit(ASTRenamingList node, Object data) {
		currentNode = node;
		Heading newHeading = (Heading)data;
		for (int i = 0; i < getChildCount(node); i++)
			compileChild(node, i, newHeading);
		return null;
	}

	// Simple rename element
	public Object visit(ASTRenamingSimple node, Object data) {
		currentNode = node;
		// data is Heading
		Heading newHeading = (Heading)data;
		// Child 0 - name from
		String nameFrom = getTokenOfChild(node, 0);
		// Child 1 - name to
		String nameTo = getTokenOfChild(node, 1);
		if (!newHeading.rename(nameFrom, nameTo))
			throw new ExceptionSemantic("RS0120: Rename from " + nameFrom + " to " + nameTo + " found no matching attributes.");
		return null;
	}
	
	// Prefix rename element
	public Object visit(ASTRenamingPrefix node, Object data) {
		currentNode = node;
		// data is Heading
		Heading newHeading = (Heading)data;
		// Child 0 - name from
		String nameFrom = (String)compileChild(node, 0, data);
		// Child 1 - name to
		String nameTo = (String)compileChild(node, 1, data);
		if (!newHeading.renamePrefix(nameFrom, nameTo))
			throw new ExceptionSemantic("RS0121: Rename from prefix " + nameFrom + " to " + nameTo + " found no matching attributes.");
		return null;
	}
	
	// Suffix rename element
	public Object visit(ASTRenamingSuffix node, Object data) {
		currentNode = node;
		// data is Heading
		Heading newHeading = (Heading)data;
		// Child 0 - name from
		String nameFrom = (String)compileChild(node, 0, data);
		// Child 1 - name to
		String nameTo = (String)compileChild(node, 1, data);
		if (!newHeading.renameSuffix(nameFrom, nameTo))
			throw new ExceptionSemantic("RS0122: Rename from suffix " + nameFrom + " to " + nameTo + " found no matching attributes.");
		return null;
	}
	
	// Attribute name list.  Return an AttributeSelection.
	public Object visit(ASTAttributeNameList node, Object data) {
		currentNode = node;
		SelectAttributes attributes = new SelectAttributes();
		compileChildren(node, attributes);
		return attributes;
	}
	
	// ALL BUT
	public Object visit(ASTAllBut node, Object data) {
		currentNode = node;
		if (getChildCount(node) == 0)
			return null;
		// data is AttributeSelection
		SelectAttributes attributes = (SelectAttributes)data;
		attributes.setAllBut(true);
		return null;
	}

	// ATTRIBUTES_OF(r)
	public Object visit(ASTAttributeNameCommalistListAttributesOf node, Object data) {
		currentNode = node;
		// data is AttributeSelection
		SelectAttributes attributes = (SelectAttributes)data;
		try {
			generator.setCompilingOff();
			Type type = (Type)compileChild(node, 0, data);
			if (!(type instanceof TypeHeading))
				throw new ExceptionSemantic("RS0448: Expected RELATION, TUPLE or ARRAY as ATTRIBUTES_OF operand, but got " + type);
			TypeHeading typeHeading = (TypeHeading)type;
			attributes.add(typeHeading.getHeading().getAttributes());
		} finally {
			generator.setCompilingOn();
		}
		return null;
	}

	// name1, name2, ..., nameN
	public Object visit(ASTAttributeNameCommalistList node, Object data) {
		currentNode = node;
		// data is AttributeSelection
		SelectAttributes attributes = (SelectAttributes)data;
		for (int i=0; i<getChildCount(node); i++)
			attributes.add(getTokenOfChild(node, i));
		return null;
	}
	
	// Var def
	public Object visit(ASTVarDef node, Object data) {
		currentNode = node;
		// Child 0 - identifier
		String varName = getTokenOfChild(node, 0);
		// Child 1 - pass var name to remainder of var definition
		compileChild(node, 1, varName);
		return null;
	}
	
	// Scalar or TUPLE variable definition
	public Object visit(ASTVarScalarOrTuple node, Object data) {
		currentNode = node;
		// data is varName
		String varName = (String)data;
		// Child - VarTypeOrInitValue
		Type varType = (Type)compileChild(node, 0, data);
		if (varType instanceof TypeRelation)
			throw new ExceptionSemantic("RS0123: Relation-valued variable definition requires REAL, BASE, PUBLIC, or PRIVATE and a KEY specification.");
		else
			generator.defineVariable(varName, varType);
		return null;
	}
	
	private RelvarHeading relvarDefinition(SimpleNode node) {
		currentNode = node;
		// Child 0 - VarTypeOrInitValue (or in the case of a PUBLIC relvar, just a TypeRef)
		Type varType = (Type)compileChild(node, 0, null);
		if (!(varType instanceof TypeRelation))
			throw new ExceptionSemantic("RS0124: Relation-valued variable definition expected initialization of RELATION type, but got " + varType);			
		Heading heading = ((TypeRelation)varType).getHeading();
		// Child 1 - KeyDefList
		return (RelvarHeading)compileChild(node, 1, new RelvarHeading(heading));
	}
	
	// Relvar definition
	public Object visit(ASTVarRelvarReal node, Object data) {
		currentNode = node;
		// data is varName
		String varName = (String)data;
		References references = new References();
		generator.setGlobalReferenceCollector(references);
		RelvarHeading definition = relvarDefinition(node);
		generator.setGlobalReferenceCollector(null);
		generator.defineRelvarReal(varName, definition, references);
		return null;
	}
	
	// Relvar definition
	public Object visit(ASTVarRelvarPublic node, Object data) {
		currentNode = node;
		// data is varName
		String varName = (String)data;
		RelvarHeading definition = relvarDefinition(node);
		generator.defineRelvarPublic(varName, definition);
		return null;
	}
	
	// Relvar definition
	public Object visit(ASTVarRelvarPrivate node, Object data) {
		currentNode = node;
		// data is varName
		String varName = (String)data;
		RelvarHeading definition = relvarDefinition(node);
		generator.defineRelvarPrivate(varName, definition);
		return null;
	}

	public Object visit(ASTVarRelvarVirtual node, Object data) {
		currentNode = node;
		// data is varName
		String varName = (String)data;
		// Child 0 - expression
		String sourceCode = getSourceCodeOfChild(node, 0);
		References references = new References();
		generator.setGlobalReferenceCollector(references);
		generator.setPersistentOnlyOn();
		Type varType = (Type)compileChild(node, 0, null);
		generator.setPersistentOnlyOff();
		generator.setGlobalReferenceCollector(null);
		if (!(varType instanceof TypeRelation))
			throw new ExceptionSemantic("RS0125: Virtual relation-valued variable definition expected expression of RELATION type, but got " + varType);			
		Heading heading = ((TypeRelation)varType).getHeading();
		// Child 1 - KeyDefList
		RelvarHeading keydef = (RelvarHeading)compileChild(node, 1, new RelvarHeading(heading));
		generator.defineRelvarVirtual(varName, sourceCode, keydef, references);
		return varType;
	}

	public Object visit(ASTVarRelvarExternal node, Object data) {
		currentNode = node;
		// data is varName
		String varName = (String)data;
		// Child 0 - identifier -- type of EXTERNAL relvar
		String externalRelvarType = getTokenOfChild(node, 0);
		// Child 1 - string literal -- string specifying EXTERNAL relvar
		String externalRelvarSpecification = ValueCharacter.stripDelimitedString(getTokenOfChild(node, 1));
		// Child 2 - identifier -- duplicates or no duplicates
		String duplicates = "AUTOKEY";
		if (getChildCount(node) > 2)
			duplicates = getTokenOfChild(node, 2);
		generator.defineRelvarExternal(varName, externalRelvarType, externalRelvarSpecification, duplicates);
		return null;
	}

	public Object visit(ASTKeyDefList node, Object data) {
		currentNode = node;
		// data is RelvarHeading
		RelvarHeading keydef = (RelvarHeading)data;
		for (int i=0; i<getChildCount(node); i++)
			keydef.addKey((SelectAttributes)compileChild(node, i, (RelvarHeading)data));
		return keydef;
	}

	public Object visit(ASTKeyDef node, Object data) {
		currentNode = node;
		// Child 0 - SelectAttributes
		return compileChild(node, 0, data); 
	}
	
	// Compile INIT (init value is on stack at run-time) and return variable Type
	public Object visit(ASTVarTypeAndOptionalInit node, Object data) {
		currentNode = node;
		// Child 0 - Type
		Type varType = (Type)compileChild(node, 0, data);
		// Child 1 (optional) - init expression
		if (getChildCount(node) > 1) {
			Type expressionType = (Type)compileChild(node, 1, data);
			if (!varType.canAccept(expressionType))
				throw new ExceptionSemantic("RS0126: Variable of type " + varType + " cannot be initialised with an expression of type " + expressionType);
			generator.compileReformat(varType, expressionType);
		} else
			generator.compilePush(varType.getDefaultValue(generator));
		return varType;
	}

	// Compile INIT (init value is on stack at run-time) and return variable Type
	public Object visit(ASTVarInit node, Object data) {
		currentNode = node;
		// Child 0 - init expression
		return compileChild(node, 0, data);
	}
	
	// Var DROP
	public Object visit(ASTDropRelvar node, Object data) {
		currentNode = node;
		// Child 0 - identifier
		String varName = getTokenOfChild(node, 0);
		generator.dropRelvar(varName);
		return null;
	}
	
	public Object visit(ASTDatabaseConstraint node, Object data) {
		currentNode = node;
		// Child 0 - identifier
		String constraintName = getTokenOfChild(node, 0);
		// Child 1 - boolean expression
		References references = new References();
		generator.setGlobalReferenceCollector(references);
		Operator constraintOperator = generator.beginConstraintDefinition();
		Type expressionType = (Type)compileChild(node, 1, data);
		if (!(expressionType instanceof TypeBoolean))
			throw new ExceptionSemantic("RS0127: Constraint definition expected expression of type BOOLEAN, but got " + expressionType);
		generator.compileReturnValue(TypeBoolean.getInstance());
		generator.endConstraintDefinition();
		generator.setGlobalReferenceCollector(null);
		generator.createConstraint(constraintName, getSourceCodeOfChild(node, 1), constraintOperator, references);
		return null;
	}
	
	public Object visit(ASTDropConstraint node, Object data) {
		currentNode = node;
		// Child 0 - identifier
		String constraintName = getTokenOfChild(node, 0);
		generator.dropConstraint(constraintName);
		return null;
	}
		
	public Object visit(ASTDropType node, Object data) {
		currentNode = node;
		// Child 0 - identifier
		String typeName = getTokenOfChild(node, 0);
		generator.dropType(typeName);
		return null;
	}

	private class Alteration {
		private String varname;
		private RelvarHeading relvarHeading;
		public Alteration(String varname, RelvarHeading relvarHeading) {
			this.varname = varname;
			this.relvarHeading = relvarHeading;
		}
		public String getVarname() {
			return varname;
		}
		public RelvarHeading getRelvarHeading() {
			return relvarHeading;
		}
		public void setRelvarHeading(RelvarHeading relvarHeading) {
			this.relvarHeading = relvarHeading;
		}
	}
	
	@Override
	public Object visit(ASTAlterVar node, Object data) {
		currentNode = node;
		// Child 0 - identifier
		String varname = getTokenOfChild(node, 0);
		RelvarMetadata rawMetadata = generator.getDatabase().getRelvarMetadata(varname);
		if (rawMetadata == null)
			throw new ExceptionSemantic("RS0439: REAL VAR '" + varname + "' does not exist.");
		if (!(rawMetadata instanceof RelvarRealMetadata))
			throw new ExceptionSemantic("RS0440: '" + varname + "' is not a REAL VAR.");
		RelvarRealMetadata metadata = (RelvarRealMetadata)rawMetadata;		
		RelvarHeading relvarHeading = metadata.getHeadingDefinition(generator.getDatabase());
		Alteration alteration = new Alteration(varname, relvarHeading);
		// Child 1 - AlterVarActionOptional
		alteration = (Alteration)compileChild(node, 1, alteration);
		// If there is a Child 2, it's an AlterVarActionKey
		if (getChildCount(node) == 3)
			compileChild(node, 2, alteration);
		return null;
	}

	@Override
	public Object visit(ASTAlterVarActionKey node, Object data) {
		currentNode = node;
		// data is an Alteration
		Alteration alteration = (Alteration)data;
		String varname = alteration.getVarname();
		// child 0 is ASTKeyDefList; pass and return RelvarHeading
		RelvarHeading keydef = (RelvarHeading)compileChild(node, 0, new RelvarHeading(alteration.getRelvarHeading().getHeading()));
		generator.alterVarRealAlterKey(varname, keydef);
		return null;
	}

	@Override
	public Object visit(ASTAlterVarActionOptional node, Object data) {
		// Child 0 to n: all AlterVarAction*
		// 'data' is an Alteration
		for (int i=0; i<getChildCount(node); i++)
			data = compileChild(node, i, data);
		return data;
	}

	@Override
	public Object visit(ASTAlterVarActionRename node, Object data) {
		currentNode = node;
		// data is an Alteration
		Alteration alteration = (Alteration)data;
		String varname = alteration.getVarname();
		// child 0 is old attribute name
		String oldName = getTokenOfChild(node, 0);
		// child 1 is new attribute name
		String newName = getTokenOfChild(node, 1);
		alteration.setRelvarHeading(generator.alterVarRealRename(varname, alteration.getRelvarHeading(), oldName, newName));
		return alteration;
	}

	@Override
	public Object visit(ASTAlterVarActionChangeType node, Object data) {
		currentNode = node;
		// data is an Alteration
		Alteration alteration = (Alteration)data;
		String varname = alteration.getVarname();
		// child 0 is attribute name
		String attributeName = getTokenOfChild(node, 0);
		// child 1 is new type
		Type newType = (Type)compileChild(node, 1, data);
		alteration.setRelvarHeading(generator.alterVarRealChangeType(varname, alteration.getRelvarHeading(), attributeName, newType));
		return alteration;
	}

	@Override
	public Object visit(ASTAlterVarActionInsert node, Object data) {
		currentNode = node;
		// data is an Alteration
		Alteration alteration = (Alteration)data;
		String varname = alteration.getVarname();
		// child 0 is ASTAttributeSpec; heading will contain new attribute name and type
		Heading heading = new Heading();
		compileChild(node, 0, heading);
		alteration.setRelvarHeading(generator.alterVarRealInsertAttributes(varname, alteration.getRelvarHeading(), heading));
		return alteration;
	}

	@Override
	public Object visit(ASTAlterVarActionDrop node, Object data) {
		currentNode = node;
		// data is an Alteration
		Alteration alteration = (Alteration)data;
		String varname = alteration.getVarname();
		// child 0 is attribute name
		String attributeName = getTokenOfChild(node, 0);
		alteration.setRelvarHeading(generator.alterVarRealDropAttribute(varname, alteration.getRelvarHeading(), attributeName));
		return alteration;
	}

	// Type specification
	public Object visit(ASTType node, Object data) {
		currentNode = node;
		// Child 0 - type name
		String typeName = getTokenOfChild(node, 0);
		return generator.findType(typeName);
	}
	
	public Object visit(ASTTypeArray node, Object data) {
		currentNode = node;
		// Child 0 - Type
		Type maybeArrayType = (Type)compileChild(node, 0, data);
		if (!(maybeArrayType instanceof TypeTuple))
			throw new ExceptionSemantic("RS0128: ARRAY expected type TUPLE, but got " + maybeArrayType);
		return new TypeArray(((TypeTuple)maybeArrayType).getHeading());
	}
	
	// TYPE_OF pseudo-operator.  Return TypeInfo for given expression.
	public Object visit(ASTTypeOf node, Object data) {
		currentNode = node;
		// don't emit code, because the expression isn't evaluated
		generator.setCompilingOff();
		Type typeOfExpression = null;
		try {
			// Child 0 - expression
			typeOfExpression = (Type)compileChild(node, 0, data);
		} finally {
			generator.setCompilingOn();
		}
		Type typeInfo = generator.findType("TypeInfo");
		generator.compilePush(generator.getTypeOf(typeOfExpression));
		return typeInfo;
	}

	// IMAGE_IN pseudo-operator
	public Object visit(ASTImageIn node, Object data) {
		currentNode = node;
		// Child 0 - relational expression
		Type maybeRelationType = (Type)compileChild(node, 0, data);
		if (!(maybeRelationType instanceof TypeRelation))
			throw new ExceptionSemantic("RS0445: IMAGE_IN expected first operand of type RELATION, but got " + maybeRelationType);
		TypeRelation leftType = (TypeRelation)maybeRelationType;
		Generator.RelationDefinition relation = generator.new RelationDefinition(null);
		// Child 1 - optional tuple expression. If absent, it's TUPLE {*}
		if (getChildCount(node) == 2) {
			Type maybeTupleType = (Type)compileChild(node, 1, data);
			if (!(maybeTupleType instanceof TypeTuple))
				throw new ExceptionSemantic("RS0446: IMAGE_IN expected second operand of type TUPLE, but got " + maybeTupleType);
			relation.addTupleToRelation((TypeTuple)maybeTupleType);
		} else {
			Generator.TupleDefinition tuple = generator.new TupleDefinition();
			tuple.setWildcard();
			relation.addTupleToRelation(tuple.endTuple());
		}
		TypeRelation rightType = relation.endRelation();
		TypeRelation joinType = generator.compileRelationJoin(leftType, rightType);
		SelectAttributes attributes = new SelectAttributes();
		attributes.setAllBut(true);
		attributes.add(rightType.getHeading().getAttributes());
		return generator.compileRelationProject(joinType, attributes);
	}
	
	// SAME TYPE AS.  Return Type.
	public Object visit(ASTTypeSameTypeAs node, Object data) {
		currentNode = node;
		// don't emit code for what is essentially a type generator
		generator.setCompilingOff();
		try {
			// Child 0 - expression
			return (Type)compileChild(node, 0, data);
		} finally {
			generator.setCompilingOn();
		}
	}
	
	// SAME HEADING AS.  Return Heading.
	public Object visit(ASTSameHeadingAs node, Object data) {
		currentNode = node;
		// don't emit code for what is essentially a heading generator
		generator.setCompilingOff();
		try {
			// Child 0 - expression
			Type exprType = (Type)compileChild(node, 0, data);
			if (exprType instanceof TypeHeading)
				return ((TypeHeading)exprType).getHeading();
			else
				throw new ExceptionSemantic("RS0129: SAME_HEADING_AS is only applicable to tuple and relation expressions, not " + exprType);
		} finally {
			generator.setCompilingOn();
		}
	}

	// TypeOperator.  Return TypeOperator.
	public Object visit(ASTOpType node, Object data) {
		currentNode = node;
		// Child 0 - type_ref_commalist
		OperatorSignature signature = (OperatorSignature)compileChild(node, 0, null);
		// Child 1 - optional return type
		if (getChildCount(node) > 1) {
			Type returnType = (Type)compileChild(node, 1, null);
			signature.setReturnType(returnType);
		}
		return new TypeOperator(signature);
	}
	
	// TypeRelation.  Return TypeRelation.
	public Object visit(ASTTypeRelation node, Object data) {
		currentNode = node;
		// Child 0 - heading
		return new TypeRelation((Heading)compileChild(node, 0, data));
	}
	
	// TypeTuple.  Return TypeTuple.
	public Object visit(ASTTypeTuple node, Object data) {
		currentNode = node;
		// Child 0 - Heading
		return new TypeTuple((Heading)compileChild(node, 0, data));
	}
	
	// Heading.  Return Heading.
	public Object visit(ASTHeading node, Object data) {
		currentNode = node;
		Heading heading = new Heading();
		// Children are AttributeSpecS
		compileChildren(node, heading);
		return heading;
	}

	// Attribute specification
	public Object visit(ASTAttributeSpec node, Object data) {
		currentNode = node;
		// data is Heading
		Heading heading = (Heading)data;
		// Child 0 - Identifier
		String attributeName = getTokenOfChild(node, 0);
		// Child 1 - Type
		Type attributeType = (Type)compileChild(node, 1, data);
		heading.add(attributeName, attributeType);
		return null;
	}
	
	private OperatorDefinition lastPersistentOperatorDefinition;

	public void addOperator(OperatorDefinition operator) {
		// persist it, if appropriate
		if (operatorsAreStorable == 0)
			generator.persistOperator(operator);
		lastPersistentOperatorDefinition = operator;
	}
	
	// External operator definition
	public Object visit(ASTExternalOpDef node, Object data) {
		// Child 0 - identifier (fn name)
		compileChild(node, 0, data);
		String fnname = getTokenOfChild(node, 0);
		Generator.ExternalOperator operator = generator.new ExternalOperator(fnname);
		// Child 1 - parameter def commalist
		compileChild(node, 1, data);
		// Child 2 - optional return definition -- UserOpReturns
		compileChild(node, 2, data);
		// Child 3 - external language identifier
		String externalLanguage = getTokenOfChild(node, 3);
		// node.tokenValue - source code
		String sourcecode = node.tokenValue;
		// done
		operator.endExternalOperator(externalLanguage, sourcecode);
		return null;
	}
	
	// Operator definition
	public Object visit(ASTUserOpDef node, Object data) {
		currentNode = node;
		// Child 0 - identifier (fn name)
		compileChild(node, 0, data);
		String fnname = getTokenOfChild(node, 0);
		References references = new References();
		generator.setGlobalReferenceCollector(references);
		generator.setPersistentOnlyOn();
		generator.beginOperator(fnname);
		OperatorDefinition operator = generator.getCurrentOperatorDefinition();
		try {
			// Child 1 - parameter def commalist
			compileChild(node, 1, data);
			// Child 2 - optional return definition -- UserOpReturns
			compileChild(node, 2, data);
			// Child 3 - optional updates definition
			compileChild(node, 3, data);
			// Child 4 - optional synonym definition
			compileChild(node, 4, data);
			// Child 5 - optional version definition
			compileChild(node, 5, data);
			// Child 6 - body
			compileChild(node, 6, data);
			// done
			generator.endOperator();
		} finally {
			generator.setPersistentOnlyOff();
		}
		generator.setGlobalReferenceCollector(null);
		// capture source and persist it, if appropriate
		if (generator.isTopLevelOperator(operator)) {
			if (operatorsAreStorable == 0) {
				operator.setSourceCode(operator.getSignature().getOperatorDeclaration() + getSourceCodeOf(node) + " ;");
				operator.setReferences(references);
				generator.persistOperator(operator);
			}
			lastPersistentOperatorDefinition = operator;
		}
		return null;
	}
	
	// Get most recent persistent operator definition
	public OperatorDefinition getLastPersistentOperatorDefinition() {
		return lastPersistentOperatorDefinition;
	}
	
	// Parameter definition list
	public Object visit(ASTUserOpParameters node, Object data) {
		currentNode = node;
		generator.beginParameterDefinitions();
		compileChildren(node, data);
		generator.endParameterDefinitions();
		return null;
	}
	
	// Parameter definition
	public Object visit(ASTParmDef node, Object data) {
		currentNode = node;
		// Child 0 - identifier
		String parmName = getTokenOfChild(node, 0);
		// Child 1 - parameter type
		Type parmType = (Type)compileChild(node, 1, data);
		// Define parm
		generator.defineOperatorParameter(parmName, parmType);
		return null;
	}

	// Operator's return definition
	public Object visit(ASTUserOpReturns node, Object data) {
		currentNode = node;
		if (getChildCount(node) == 0)
			return null;
		Type returnType = (Type)compileChild(node, 0, data);
		generator.setDeclaredReturnType(returnType);
		return returnType;
	}
	
	// Operator's updates definition
	public Object visit(ASTUserOpUpdates node, Object data) {
		currentNode = node;
		// TODO - mutually exclusive with return definition
		if (getChildCount(node) == 0)
			return null;
		compileChildren(node, data);
		return null;
	}
	
	// Operator's synonym definition
	public Object visit(ASTUserOpSynonym node, Object data) {
		currentNode = node;
		// TODO - User Op Synonym
		if (getChildCount(node) == 0)
			return null;
		compileChildren(node, data);
		return null;
	}
	
	// Operator's version definition
	public Object visit(ASTUserOpVersion node, Object data) {
		currentNode = node;
		// TODO - User Op Version
		if (getChildCount(node) == 0)
			return null;
		compileChildren(node, data);
		return null;
	}
	
	// Operator body
	public Object visit(ASTUserOpBody node, Object data) {
		currentNode = node;
		compileChildren(node, data);
		return null;
	}
	
	// Operator return, with optional expression
	public Object visit(ASTReturnExpression node, Object data) {
		currentNode = node;
		// Child 0 - optional return expression
		if (getChildCount(node) > 0) {
			Type expressionType = (Type)compileChild(node, 0, data);
			if (generator.getDeclaredReturnType() == null)
				throw new ExceptionSemantic("RS0130: Operator " + generator.getCurrentDefinitionSignature() + " has not declared a return type but has defined a return expression.");
			if (!generator.getDeclaredReturnType().canAccept(expressionType))
				throw new ExceptionSemantic("RS0131: Operator " + generator.getCurrentDefinitionSignature() + " has declared return type " + generator.getDeclaredReturnType() + " but attempted to return " + expressionType);
			generator.compileReturnValue(generator.getDeclaredReturnType());
		} else
			generator.compileReturn();
		return null;
	}
	
	public Object visit(ASTDropOperator node, Object data) {
		// Child 0 - OpSignature
		OperatorSignature signature = (OperatorSignature)compileChild(node, 0, data);
		generator.dropOperator(signature);
		return null;
	}
	
	public Object visit(ASTOpSignature node, Object data) {
		// Child 0 - identifier
		String identifier = getTokenOfChild(node, 0);
		// Child 1 - type_ref_commalist
		return (OperatorSignature)compileChild(node, 1, identifier);
	}

	public Object visit(ASTTypeRefCommalist node, Object data) {
		// data is String - operator name
		OperatorSignature signature = new OperatorSignature((String)data);
		for (int i=0; i<getChildCount(node); i++)
			signature.addParameterType((Type)compileChild(node, i, data));
		return signature;
	}
		
	// Operator invocation argument list.  Returns an OperatorSignature.
	public Object visit(ASTArgList node, Object data) {
		currentNode = node;
		// data is operator name
		String operatorName = (String)data;
		OperatorSignature signature = new OperatorSignature(operatorName);
		for (int i=0; i<getChildCount(node); i++)
			signature.setParameterType(i, (Type)compileChild(node, i, data));			
		return signature;
	}
	
	// Operator call statement
	public Object visit(ASTCall node, Object data) {
		currentNode = node;
		// Child 0 - identifier (fn name)
		String opName = getTokenOfChild(node, 0);		
		// Child 1 - arglist
		OperatorSignature signature = (OperatorSignature)compileChild(node, 1, opName);
		generator.compileCall(signature);
		return null;
	}
	
	// Operator evaluation in an expression
	public Object visit(ASTFnInvoke node, Object data) {
		currentNode = node;
		// Child 0 - identifier (fn name)
		String opName = getTokenOfChild(node, 0);		
		// Child 1 - arglist
		OperatorSignature signature = (OperatorSignature)compileChild(node, 1, opName);
		return generator.compileEvaluate(signature);
	}

	// Compile an IF 
	public Object visit(ASTIfStatement node, Object data) {
		currentNode = node;
		// Child 0 - test expression
		Type expressionType = (Type)compileChild(node, 0, data);
		if (!(expressionType instanceof TypeBoolean))
			throw new ExceptionSemantic("RS0132: IF expression must be boolean.");
		Generator.IfStatement ifStatement = generator.new IfStatement();
		// Child 1 - if statement
		compileChild(node, 1, data);
		// Child 2 - ElseStatement -- pass it the ifStatement
		compileChild(node, 2, ifStatement);
		// end if
		ifStatement.endIf();
		return null;
	}

	// Compile an ELSE 
	public Object visit(ASTElseStatement node, Object data) {
		currentNode = node;
		if (getChildCount(node) == 0)
			return null;
		// data is Generator.IfStatement
		Generator.IfStatement ifStatement = (Generator.IfStatement)data;
		ifStatement.beginElse();
		compileChild(node, 0, data);
		return null;
	}
	
	public Object visit(ASTIfExpression node, Object data) {
		currentNode = node;
		// Child 0 - test expression
		Type expressionType = (Type)compileChild(node, 0, data);
		if (!(expressionType instanceof TypeBoolean))
			throw new ExceptionSemantic("RS0133: IF expression must be boolean.");
		Generator.IfStatement ifStatement = generator.new IfStatement();
		// Child 1 - if's conditionally-executed expression
		Type ifType = (Type)compileChild(node, 1, data);
		// Child 2 - if's ElseExpression
		ifStatement.beginElse();
		Type elseType = (Type)compileChild(node, 2, data);
		// end if
		ifStatement.endIf();
		if (!ifType.canAccept(elseType))
			throw new ExceptionSemantic("RS0144: IF ... ELSE expected the ELSE expression to be " + ifType + " but got " + elseType);
		return ifType;
	}

	class CaseStatement {
		Stack<Generator.IfStatement> ifStatements = new Stack<Generator.IfStatement>();

		void beginCaseWhen() {
			if (ifStatements.size() > 0)
				ifStatements.peek().beginElse();
		}
		
		void endCaseWhen() {
			ifStatements.push(generator.new IfStatement());
		}
		
		void caseElse() {
			ifStatements.peek().beginElse();
		}
		
		void endCase() {
			while (ifStatements.size() > 0)
				ifStatements.pop().endIf();
		}
	}

	// CASE statement
	public Object visit(ASTCaseStatement node, Object data) {
		currentNode = node;
		CaseStatement caseStatement = new CaseStatement();
		// Child 0 - CaseWhenList
		compileChild(node, 0, caseStatement);
		// Child 1 - CaseElse
		compileChild(node, 1, caseStatement);
		caseStatement.endCase();
		return null;
	}
		
	// CASE WHEN list
	public Object visit(ASTCaseWhenList node, Object data) {
		currentNode = node;
		// data is Generator.CaseStatement
		compileChildren(node, data);
		return null;
	}

	// CASE WHEN
	public Object visit(ASTCaseWhen node, Object data) {
		currentNode = node;
		// data is Generator.CaseStatement
		CaseStatement caseStatement = (CaseStatement)data;
		caseStatement.beginCaseWhen();
		// Child 0 - boolean expression
		Type expressionType = (Type)compileChild(node, 0, data);
		if (!(expressionType instanceof TypeBoolean))
			throw new ExceptionSemantic("RS0145: WHEN expression must be boolean.");
		caseStatement.endCaseWhen();
		// Child 1 - statement
		compileChild(node, 1, data);
		return null;
	}
	
	// CASE ELSE
	public Object visit(ASTCaseElse node, Object data) {
		currentNode = node;
		// data is Generator.CaseStatement
		if (getChildCount(node) > 0) {
			((CaseStatement)data).caseElse();
			compileChild(node, 0, data);
		}
		return null;
	}

	class CaseExpression extends CaseStatement {
		private Type exprType = null;
		public void setExpressionType(Type type) {
			if (exprType == null)
				exprType = type;
			else if (type == null)
				throw new ExceptionFatal("RS0312: CASE encountered null type returned from expression compilation");
			else
				if (!exprType.canAccept(type))
					throw new ExceptionSemantic("RS0145: CASE expected " + exprType + " but got " + type);
		}
		public Type getExpressionType() {
			return exprType;
		}
	}
	
	public Object visit(ASTCaseExpression node, Object data) {
		currentNode = node;
		CaseExpression caseExpression = new CaseExpression();
		// Child 0 - CaseWhenList
		compileChild(node, 0, caseExpression);
		// Child 1 - CaseElse
		caseExpression.caseElse();
		caseExpression.setExpressionType((Type)compileChild(node, 1, data));
		caseExpression.endCase();
		return caseExpression.getExpressionType();
	}
	
	public Object visit(ASTCaseWhenListExpression node, Object data) {
		currentNode = node;
		// data is Generator.CaseExpression
		compileChildren(node, data);
		return null;
	}
	
	public Object visit(ASTCaseWhenExpression node, Object data) {
		currentNode = node;
		// data is Generator.CaseExpression
		CaseExpression caseExpression = (CaseExpression)data;
		caseExpression.beginCaseWhen();
		// Child 0 - boolean expression
		Type expressionType = (Type)compileChild(node, 0, data);
		if (!(expressionType instanceof TypeBoolean))
			throw new ExceptionSemantic("RS0146: WHEN expression must be boolean.");
		caseExpression.endCaseWhen();
		// Child 1 - expression
		caseExpression.setExpressionType((Type)compileChild(node, 1, data));
		return null;
	}

	// Compile a DO loop
	public Object visit(ASTDoLoop node, Object data) {
		currentNode = node;
		// Child 0 - identifier
		String identifier = getTokenOfChild(node, 0);
		// Child 1 - initial expression
		Type initExprType = (Type)compileChild(node, 1, data);
		Slot loopIndex = generator.findReference(identifier);
		if (!(loopIndex.getType() instanceof TypeInteger))
			throw new ExceptionSemantic("RS0147: DO loop only supports INTEGER iteration.  Iterator of type " + loopIndex.getType() + " not supported.");
		if (!loopIndex.getType().canAccept(initExprType))
			throw new ExceptionSemantic("RS0148: Cannot assign " + initExprType + " to " + identifier + " which is a " + loopIndex.getType());
		generator.compileSet(loopIndex);
		// begin loop
		Generator.DoLoop forLoop = generator.new DoLoop();
		// Child 2 - terminal expression ... compile as test
		generator.compileGet(loopIndex);
		Type termExprType = (Type)compileChild(node, 2, data);
		if (!(termExprType.canAccept(TypeInteger.getInstance())))
			throw new ExceptionSemantic("RS0149: Expression after TO is " + termExprType + ", expected " + loopIndex.getType());
		generator.compileLTE();
		forLoop.testDo();
		// compile loop body
		compileChild(node, 3, data);
		// compile loop increment
		generator.compileGet(loopIndex);
		generator.compilePush(1);
		generator.compilePlus();
		generator.compileSet(loopIndex);
		forLoop.endDo();
		return null;
	}

	// Compile a WHILE loop
	public Object visit(ASTWhileLoop node, Object data) {
		currentNode = node;
		Generator.DoLoop whileLoop = generator.new DoLoop();
		// Child 0 - boolean test expression
		Type testExpressionType = (Type)compileChild(node, 0, data);
		if (!(testExpressionType instanceof TypeBoolean))
			throw new ExceptionSemantic("RS0150: WHILE expression expected boolean, but got " + testExpressionType);
		whileLoop.testDo();
		// Child 1 - loop body
		compileChild(node, 1, data);
		whileLoop.endDo();
		return null;
	}
	
	// Compile a FOR loop
	public Object visit(ASTForLoop node, Object data) {
		// Child 0 - expression
		Type expressionType = (Type)compileChild(node, 0, data);
		if (!(expressionType instanceof TypeArray)) {
			if (expressionType instanceof TypeRelation)
				throw new ExceptionSemantic("RS0151: FOR expected ARRAY, but got " + expressionType + ".  Use ORDER() to convert a relation to an ARRAY.");
			else
				throw new ExceptionSemantic("RS0152: FOR expected ARRAY, but got " + expressionType);
		}
		Generator.ForLoop forLoop = generator.new ForLoop();
		forLoop.beginForLoop((TypeArray)expressionType);
		// Child 1 - statement
		compileChild(node, 1, data);
		forLoop.endForLoop();
		return null;
	}
	
	// Process an identifier
	// This doesn't do anything, but needs to be here because we need an ASTIdentifier node in order to get an identifier's token.
	public Object visit(ASTIdentifier node, Object data) {
		currentNode = node;
		return null;
	}
	
	// Execute a group of comma-separated assignment statements
	//
	// The evaluation of all expressions as a unit, followed by assigning expression evaluation
	// results as a unit, ensures assignment conforms to TTM multiple-assignment requirements.
	public Object visit(ASTAssignment node, Object data) {
		currentNode = node;
		generator.beginAssignment();
		// Compile expression evaluations.
		for (int i = getChildCount(node) - 1; i >= 0; i--)
			compileChild(node, i, new Integer(1));
		// Compile slot assignments.
		for (int i = 0; i < getChildCount(node); i++)
			compileChild(node, i, new Integer(2));
		generator.endAssignment();
		return null;
	}

	// Assignment statement.
	//
	// If data is Integer(1), compile the expression evaluation.
	//
	// If data is Integer(2), compile the assignment.
	//
	public Object visit(ASTAssign node, Object data) {
		currentNode = node;
		switch (((Integer)data).intValue()) {
		case 1:
			compileChild(node, 1, data);
			break;
		case 2:
			generator.setCompilingOff();
			Type expressionType = (Type)compileChild(node, 1, data);
			generator.setCompilingOn();			
			String refName = getTokenOfChild(node, 0);
			Slot reference = generator.findReference(refName);
			if (!reference.getType().canAccept(expressionType))
				throw new ExceptionSemantic("RS0153: Cannot assign " + expressionType + " to '" + refName + "' which is a " + reference.getType());
			if (reference.isParameter())
				throw new ExceptionSemantic("RS0403: Parameter is not updateable.");			
			generator.compileReformat(reference.getType(), expressionType);
			generator.compileSet(reference);
		}
		return null;
	}
	
	// Insert into Relvar
	// 
	// If data is Integer(1), compile the expression evaluation.
	//
	// If data is Integer(2), compile the assignment.
	//
	public Object visit(ASTInsert node, Object data) {
		currentNode = node;
		switch (((Integer)data).intValue()) {
		case 1:
			break;
		case 2:
			Type expressionType = (Type)compileChild(node, 1, data);
			String refName = getTokenOfChild(node, 0);
			Slot reference = generator.findReference(refName);
			if (!(expressionType instanceof TypeRelation))
				throw new ExceptionSemantic("RS0154: Expected expression of type relation, but got " + expressionType);
			if (!reference.getType().canAccept(expressionType))
				throw new ExceptionSemantic("RS0155: Cannot insert " + expressionType + " into '" + refName + "' because it is a " + reference.getType());
			Type resultType = generator.compileReformat(reference.getType(), expressionType);
			generator.compileRelvarInsert(reference, refName, (TypeRelation)resultType);
			break;
		}
		return null;
	}

	// Insert into Relvar.  Throw exception if tuples already exist.
	// 
	// If data is Integer(1), compile the expression evaluation.
	//
	// If data is Integer(2), compile the assignment.
	//
	public Object visit(ASTDInsert node, Object data) {
		currentNode = node;
		switch (((Integer)data).intValue()) {
		case 1:
			break;
		case 2:
			Type expressionType = (Type)compileChild(node, 1, data);
			String refName = getTokenOfChild(node, 0);
			Slot reference = generator.findReference(refName);
			if (!(expressionType instanceof TypeRelation))
				throw new ExceptionSemantic("RS0156: Expected expression of type relation, but got " + expressionType);
			if (!reference.getType().canAccept(expressionType))
				throw new ExceptionSemantic("RS0157: Cannot insert " + expressionType + " into '" + refName + "' because it is a " + reference.getType());
			Type resultType = generator.compileReformat(reference.getType(), expressionType);
			generator.compileRelvarInsertNoDuplicates(reference, refName, (TypeRelation)resultType);
			break;
		}
		return null;
	}
		
	// UPDATE statement
	// 
	// If data is Integer(1), exit
	//
	// If data is Integer(2), compile the assignment.
	//
	// TODO - ensure UPDATE in multiple-assignment conforms to TTM. 
	public Object visit(ASTUpdateStatement node, Object data) {
		currentNode = node;
		if (data != null && data.equals(new Integer(1)))
			return null;
		// Child 0 - variable name
		String identifier = getTokenOfChild(node, 0);
		Slot slot = generator.findReference(identifier);
		if (slot.isParameter())
			throw new ExceptionSemantic("RS0404: Parameter is not updateable.");
		Type slotType = slot.getType();
		if (slotType instanceof TypeTuple) {
			if (getChildCountOfChild(node, 1) > 0)
				throw new ExceptionSemantic("RS0158: WHERE clause is not appropriate for UPDATE of a tuple.");
			generator.compileGet(slot);
			Generator.TupleSubstitute tupleUpdate = generator.new TupleSubstitute((TypeTuple)slotType);
			// Child 2 - update expression assignment statements
			compileChild(node, 2, data);
			tupleUpdate.endTupleSubstitute();
			generator.compileSet(slot);
			return null;
		} else if (slotType instanceof TypeRelation) {
			Generator.UpdateWhere relvarUpdate = generator.new UpdateWhere(identifier, (TypeRelation)slotType);
			if (getChildCountOfChild(node, 1) > 0) {
				relvarUpdate.beginRelvarUpdateWhere();
				// Child 1 - optional WHERE clause
				compileChild(node, 1, data);
				relvarUpdate.endRelvarUpdateWhere();
			}
			relvarUpdate.beginRelvarUpdateAssignment();
			// Child 2 - update expression assignment statements
			compileChild(node, 2, data);			
			relvarUpdate.endUpdateWhere();
			return null;
		} else
			throw new ExceptionSemantic("RS0159: Expected TUPLE or RELATION, but got " + slotType);
	}
	
	// Delete from Relvar
	// 
	// If data is Integer(1), exit
	//
	// If data is Integer(2), compile the assignment.
	//
	// TODO - ensure DELETE in multiple-assignment conforms to TTM. 
	public Object visit(ASTDelete node, Object data) {
		currentNode = node;
		if (data != null && data.equals(new Integer(1)))
			return null;
		// Child 0 - relvar name
		String identifier = getTokenOfChild(node, 0);
		Slot slot = generator.findReference(identifier);
		if (slot.isParameter())
			throw new ExceptionSemantic("RS0405: Parameter is not updateable.");
		Type slotType = slot.getType();
		if (slotType instanceof TypeRelation) {
			// is a parameter present?
			if (getChildCountOfChild(node, 1) > 0) {
				Generator.DeleteHandler deleteHandler = generator.new DeleteHandler(identifier, (TypeRelation)slotType);
				// Child 1 - parameter
				Type expressionType = (Type)compileChild(node, 1, deleteHandler);
				deleteHandler.endDeleteHandler(expressionType);
			} else {
				generator.compileRelvarPurge(slot, identifier);
			}
			return null;
		} else
			throw new ExceptionSemantic("RS0160: Expected relvar or relation-valued attribute in DELETE, but got " + slotType);
	}

	// Delete from Relvar - optional WHERE expression or expression specifying relation
	// data is Generator.DeleteHandler
	public Object visit(ASTDeleteParameter node, Object data) {
		currentNode = node;
		Type type = null;
		if (getChildCount(node) == 2) {
			// WHERE
			compileChild(node, 0, data);
			// BOOLEAN expression
			type = (Type)compileChild(node, 1, data);
			if (!(type instanceof TypeBoolean))
				throw new ExceptionSemantic("RS0161: Expected BOOLEAN expression in WHERE, but got " + type);
		} else if (getChildCount(node) == 1) {
			// RELATION expression
			type = (Type)compileChild(node, 0, data);
			if (!(type instanceof TypeRelation))
				throw new ExceptionSemantic("RS0162: Expected RELATION to specify tuples to DELETE, but got " + type);
		}
		return type;
	}

	// Delete from Relvar - WHERE keyword 
	public Object visit(ASTDeleteWhere node, Object data) {
		currentNode = node;
		((Generator.DeleteHandler)data).doWhere();
		return null;
	}

	// I_DELETE
	// 
	// If data is Integer(1), exit
	//
	// If data is Integer(2), compile the assignment.
	//
	// TODO - ensure DELETE in multiple-assignment conforms to TTM. 
	public Object visit(ASTIDelete node, Object data) {
		currentNode = node;
		if (data != null && data.equals(new Integer(1)))
			return null;
		// Child 0 - relvar name
		String identifier = getTokenOfChild(node, 0);
		Slot slot = generator.findReference(identifier);
		Type slotType = slot.getType();
		if (slotType instanceof TypeRelation) {
			// Child 1 - expression
			Type type = (Type)compileChild(node, 1, null);
			if (!(type instanceof TypeRelation))
				throw new ExceptionSemantic("RS0163: Expected RELATION to specify tuples to I_DELETE, but got " + type);
			if (!slotType.canAccept(type))
				throw new ExceptionSemantic("RS0164: Expected expression of type " + slotType + " but got " + type);
			generator.compileReformat(slotType, type);
			generator.compileRelvarIDelete(slot, identifier);
		} else
			throw new ExceptionSemantic("RS0165: Expected relvar in I_DELETE, but got " + slotType);
		return null;
	}

	// Dereference an array at a specified subscript, and push its value onto the stack
	public Object visit(ASTArrayDereference node, Object data) {
		currentNode = node;
		// Child0 - expression
		Type arrayType = (Type)compileChild(node, 0, data);
		if (!(arrayType instanceof TypeArray))
			throw new ExceptionSemantic("RS0166: ARRAY dereference expected ARRAY, but got " + arrayType);
		Type arrayHoldsType = ((TypeArray)arrayType).getElementType();
		// Child1 - subscript expression
		Type subscriptExprType = (Type)compileChild(node, 1, data);
		if (!(subscriptExprType instanceof TypeInteger))
			throw new ExceptionSemantic("RS0167: ARRAY dereference expected subscript of type INTEGER, but got " + subscriptExprType);
		generator.compileArrayGet();
		return arrayHoldsType;
	}

	// Invoke a ValueOperator
	public Object visit(ASTFnInvokeAnonymous node, Object data) {
		currentNode = node;
		// Child 1 - arglist
		OperatorSignature signature = (OperatorSignature)compileChild(node, 1, null);
		// Child 0 - expression evaluating to ValueOperator
		Type expressionType = (Type)compileChild(node, 0, data);
		if (!(expressionType instanceof TypeOperator))
			throw new ExceptionSemantic("RS0168: Expected OPERATOR, but got " + expressionType);
		TypeOperator operatorType = (TypeOperator)expressionType;
		if (!operatorType.getOperatorSignature().canBeInvokedBy(signature))
			throw new ExceptionSemantic("RS0169: Expected invocation of OPERATOR " + operatorType.getOperatorSignature().toRelLookupString() + " but got invocation of OPERATOR " + signature);
		generator.compileEvaluateAnonymous();
		return operatorType.getOperatorSignature().getReturnType(); 
	}

	// Compile a dereference of a variable or parameter.  Return variable or parm Type.
	public Object visit(ASTDereference node, Object data) {
		currentNode = node;
		return generator.compileGet(node.tokenValue);
	}

	// aggregate COUNT invocation
	private Type aggInvokeCount(Type exprType) {
		OperatorSignature sig = new OperatorSignature("COUNT");
		if (exprType instanceof TypeRelation || exprType instanceof TypeArray)
			sig.addParameterType(exprType);
		else
			throw new ExceptionSemantic("RS0170: Aggregate COUNT expected RELATION or ARRAY, but got " + exprType);
		return generator.compileEvaluate(sig);		
	}
	
	// aggregate COUNT
	public Object visit(ASTAggCount node, Object data) {
		currentNode = node;
		// Child 0 - Expression
		Type exprType = (Type)compileChild(node, 0, null);
		return aggInvokeCount(exprType);
	}
	
	// Aggregator result type
	private static class AggregateResult {
		private Type attributeType;
		private Type returnType;
		AggregateResult(Type attributeType, Type returnType) {
			this.attributeType = attributeType;
			this.returnType = returnType;
		}
		Type getAttributeType() {
			return attributeType;
		}
		Type getReturnType() {
			return returnType;
		}
	}

	private static long introducedAttributeNameSerial = 0;
	
	// Aggregate operator handler for SUM, AVG, MAX, MIN, etc.
	private abstract class Aggregator {
		private String opName;
		private Type returnType;
		private TypeArray extendedRelationExprType;
		protected String introducedAttributeName;
		protected Type attributeExprType;
		
		Aggregator(String operatorName) {
			opName = operatorName;
		}
		
		protected TypeArray extendAndProjectToAggregatable(Type source, String aggregandName) {
			final String aggregandAttributeName = "AGGREGAND";
			final String aggregationSerialAttributeName = "AGGREGATION_SERIAL";
			
			// sourceRenaming := source RENAME {AGGREGAND AS %random1, AGGREGATION_SERIAL AS %random2}
			Heading sourceRenaming = (source instanceof TypeArray) ? ((TypeArray)source).getHeading() : ((TypeRelation)source).getHeading();
			sourceRenaming.rename(aggregandAttributeName, sourceRenaming.getRandomFreeAttributeName());
			sourceRenaming.rename(aggregationSerialAttributeName, sourceRenaming.getRandomFreeAttributeName());

			// extendedType := EXTEND sourceRenaming {AGGREGATION_SERIAL := serial_number()}
			Generator.Extend extend = generator.new Extend(sourceRenaming);
			extend.addExtendSerialiser(aggregationSerialAttributeName);
			TypeArray extendedType = generator.endArrayExtend(extend);
			
			// renamed := extendedType RENAME {aggregandName AS AGGREGAND}
			Heading renamed = extendedType.getHeading();
			renamed.rename(aggregandName, aggregandAttributeName);
			
			// extendedRelationExprType := renamed {AGGREGAND, AGGREGATION_SERIAL}
			SelectAttributes attributes = new SelectAttributes();
			attributes.add(aggregandAttributeName);
			attributes.add(aggregationSerialAttributeName);
			extendedRelationExprType = generator.compileArrayProject(new TypeArray(renamed), attributes);
			
			return extendedRelationExprType;
		}
		
		// SUMMARIZE aggregator build !!!
		Type buildAggregator(SimpleNode node, Generator.Summarize.SummarizeItem item, boolean distinct, int attributeExpressionNodeNumber) {
			// Child attributeExpressionNodeNumber - expression
			item.beginSummarizeItemExpression();
			attributeExprType = (Type)compileChild(node, attributeExpressionNodeNumber, item);
			TypeRelation extendedSourceType = item.endSummarizeItemExpression(attributeExprType, distinct);
			// convert to ARRAY ... consider using this to define specific SelectOrder as part of SUMMARIZE syntax
			return generator.compileOrder(extendedSourceType, new SelectOrder());
		}
		
		// SUMMARIZE aggregator invocation
		AggregateResult buildInvocation(Type aggExpType, Generator.Summarize.SummarizeItem item, OperatorDefinition aggregator, SimpleNode node, int initialValueNodeNumber) {
			return createAggregator(aggExpType, item.getExtendAttributeName(), aggregator, node, initialValueNodeNumber);
		}
		
		// SUMMARIZE aggregator !!!
		AggregateResult createAggregator(SimpleNode node, Generator.Summarize.SummarizeItem item, boolean distinct) {
			Type aggExpType = buildAggregator(node, item, distinct, 0);
			return createAggregator(aggExpType, item.getExtendAttributeName());			
		}
		
		// extend TupleIteratable source with attribute expression as %AGGn attribute, and result to ARRAY if it isn't already an ARRAY
		TypeArray buildAggregator(SimpleNode node, int tupleIteratableExpressionNodeNumber, int attributeExpressionNodeNumber) {
			// Child tupleIteratableExpressionNodeNumber - TupleIteratable expression
			Type attributeExpressionType = (Type)compileChild(node, tupleIteratableExpressionNodeNumber, null);
			if (attributeExpressionType instanceof TypeArray) {
				// Child attributeExpressionNodeNumber - Attribute expression
				Generator.Extend extend = generator.new Extend(((TypeArray)attributeExpressionType).getHeading());
				introducedAttributeName = "%AGG" + introducedAttributeNameSerial++;
				attributeExprType = (Type)compileChild(node, attributeExpressionNodeNumber, null);
				checkAttributeType(attributeExprType);
				extend.addExtendItem(introducedAttributeName, attributeExprType);
				extendedRelationExprType = generator.endArrayExtend(extend);
				return extendedRelationExprType;				
			} else if (attributeExpressionType instanceof TypeRelation) {
				// Child attributeExpressionNodeNumber - Attribute expression
				Generator.Extend extend = generator.new Extend(((TypeRelation)attributeExpressionType).getHeading());
				introducedAttributeName = "%AGG" + introducedAttributeNameSerial++;
				attributeExprType = (Type)compileChild(node, attributeExpressionNodeNumber, null);
				checkAttributeType(attributeExprType);
				extend.addExtendItem(introducedAttributeName, attributeExprType);
				TypeRelation extendedSourceType = generator.endRelationExtend(extend);
				extendedRelationExprType = generator.compileOrder(extendedSourceType, new SelectOrder());
				return extendedRelationExprType;				
			} else
				throw new ExceptionSemantic("RS0171: Aggregate " + (opName.startsWith("%") ? "" : opName + " ") + "expected RELATION or ARRAY, but got " + attributeExpressionType);
		}
		
		// implement operator invocation
		AggregateResult buildInvocation(Type aggExpType, OperatorDefinition aggregator, SimpleNode node, int initialValueNodeNumber) {
			return createAggregator(aggExpType, introducedAttributeName, aggregator, node, initialValueNodeNumber);
		}
		
		// aggregate operator invocation
		AggregateResult createAggregator(SimpleNode node) {
			buildAggregator(node, 0, 1);
			return createAggregator(extendedRelationExprType, introducedAttributeName);			
		}
		
		private AggregateResult createAggregator(Type exprType, String attributeName, OperatorDefinition aggregator, SimpleNode initialValueNode, int initialValueNodeNumber) {
			extendAndProjectToAggregatable(exprType, attributeName);
			if (initialValueNodeNumber >= 0) {
				Type initialValueType = (Type)compileChild(initialValueNode, initialValueNodeNumber, null);
				if (!getAttributeExpressionType().canAccept(initialValueType))
					throw new ExceptionSemantic("RS0442: Expected type of initial value to be " + getAttributeExpressionType() + " but got " + initialValueType);
			}			
			returnType = generator.compileEvaluate(aggregator);
			return new AggregateResult(attributeExprType, getReturnType(attributeExprType));
		}
		
		// create aggregator !!!
		private AggregateResult createAggregator(Type exprType, String attributeName) {
			TypeArray aggregatableType = extendAndProjectToAggregatable(exprType, attributeName);
			String operatorName = getOpNameForType(attributeExprType);
			OperatorSignature sig = new OperatorSignature("AGGREGATE_" + operatorName);
			sig.addParameterType(aggregatableType);
			returnType = generator.compileEvaluate(sig);
			return new AggregateResult(attributeExprType, getReturnType(attributeExprType));
		}
		
		void checkAttributeType(Type t) {}
		
		String getOpNameForType(Type attributeType) {
			return opName;
		}
		
		Type getReturnType(Type attributeType) {
			return returnType;
		}
		
		Type getAttributeExpressionType() {
			return attributeExprType;
		}
		
		String getName() {
			return opName;
		}
	}
	
	class AggregatorSum extends Aggregator {
		AggregatorSum() {
			super("SUM");
		}
		String getOpNameForType(Type attributeType) {
			if (attributeType instanceof TypeInteger)
				return "SUM_INTEGER";
			else if (attributeType instanceof TypeRational)
				return "SUM_RATIONAL";
			else
				return super.getOpNameForType(attributeType);
		}
	}
	
	class AggregatorAvg extends Aggregator {
		AggregatorAvg() {
			super("AVG");
		}
		String getOpNameForType(Type attributeType) {
			if (attributeType instanceof TypeInteger)
				return "AVG_INTEGER";
			else if (attributeType instanceof TypeRational)
				return "AVG_RATIONAL";
			else
				return super.getOpNameForType(attributeType);
		}
	}

	class AggregatorMax extends Aggregator {
		AggregatorMax() {
			super("MAX");
		}
		Type getReturnType(Type attributeType) {
			return attributeType;
		}
	}

	class AggregatorMin extends Aggregator {
		AggregatorMin() {
			super("MIN");
		}
		Type getReturnType(Type attributeType) {
			return attributeType;
		}
	}

	class AggregatorAnd extends Aggregator {
		AggregatorAnd() {
			super("AND");
		}
	}
	
	class AggregatorOr extends Aggregator {
		AggregatorOr() {
			super("OR");
		}
	}
	
	class AggregatorXor extends Aggregator {
		AggregatorXor() {
			super("XOR");
		}
	}
	
	class AggregatorUnion extends Aggregator {
		AggregatorUnion() {
			super("UNION");
		}
		Type getReturnType(Type attributeType) {
			return attributeType;
		}
		void checkAttributeType(Type t) {
			if (!(t instanceof TypeRelation))
				throw new ExceptionSemantic("RS0173: Aggregate UNION expected attribute of type RELATION; got " + t);
		}
	}
	
	class AggregatorXunion extends Aggregator {
		AggregatorXunion() {
			super("XUNION");
		}
		Type getReturnType(Type attributeType) {
			return attributeType;
		}
		void checkAttributeType(Type t) {
			if (!(t instanceof TypeRelation))
				throw new ExceptionSemantic("RS0174: Aggregate XUNION expected attribute of type RELATION; got " + t);
		}
	}
	
	class AggregatorDUnion extends Aggregator {
		AggregatorDUnion() {
			super("D_UNION");
		}
		Type getReturnType(Type attributeType) {
			return attributeType;
		}
		void checkAttributeType(Type t) {
			if (!(t instanceof TypeRelation))
				throw new ExceptionSemantic("RS0175: Aggregate D_UNION expected attribute of type RELATION; got " + t);
		}
	}
	
	class AggregatorIntersect extends Aggregator {
		AggregatorIntersect() {
			super("INTERSECT");
		}
		Type getReturnType(Type attributeType) {
			return attributeType;
		}
		void checkAttributeType(Type t) {
			if (!(t instanceof TypeRelation))
				throw new ExceptionSemantic("RS0176: Aggregate INTERSECT expected attribute of type RELATION; got " + t);
		}
	}
	
	private static long introducedAggregateOperatorNameSerial = 0; 
	
	class AggregatorAggregate extends Aggregator {
		
		AggregatorAggregate() {
			super("%AGGREGATE" + introducedAggregateOperatorNameSerial++);
		}
		
		Type getReturnType(Type attributeType) {
			return attributeType;
		}
		
		private OperatorDefinition buildGenericAggregator(SimpleNode node, Type aggExpType, int initialValueNodeNumber) {
			final OperatorDefinition attributeFold = generator.beginAnonymousOperator();
			attributeFold.defineParameter("VALUE1", getAttributeExpressionType());
			attributeFold.defineParameter("VALUE2", getAttributeExpressionType());
			attributeFold.setDeclaredReturnType(getAttributeExpressionType());
			// last child - aggregator's op_body()
			compileChild(node, getChildCount(node) - 1, null);
			generator.endOperator();
			
			VirtualMachine vm = new VirtualMachine(generator, generator.getDatabase(), generator.getPrintStream());
			Context context = new Context(generator, vm);
			Operator attributeFoldOperator = attributeFold.getOperator();
			
			NativeFunction aggregatorFunction;
			if (initialValueNodeNumber < 0)
				aggregatorFunction = new NativeFunction() {
					@Override
					public Value evaluate(Value[] arguments) {
						TupleIteratable tupleIteratable = (TupleIteratable)arguments[0];
						TupleFoldFirstIsIdentity folder = new TupleFoldFirstIsIdentity("AGGREGATE requires at least one tuple.", tupleIteratable.iterator(), 0) {
							@Override
							public Value fold(Value left, Value right) {
								context.push(left);
								context.push(right);
								context.call(attributeFoldOperator);
								return context.pop();
							}
						};
						folder.run();
						return folder.getResult();
					}
				};
			else
				aggregatorFunction = new NativeFunction() {
					@Override
					public Value evaluate(Value[] arguments) {
						TupleIteratable tupleIteratable = (TupleIteratable)arguments[0];
						Value initialValue = arguments[1];
						TupleFold folder = new TupleFold(tupleIteratable.iterator(), 0) {
							@Override
							public Value fold(Value left, Value right) {
								context.push(left);
								context.push(right);
								context.call(attributeFoldOperator);
								return context.pop();
							}
							@Override
							public Value getIdentity() {
								return initialValue;
							}	
						};
						folder.run();
						return folder.getResult();
					}
				};			
			return new OperatorDefinitionNativeFunction(
					getName(),
					(initialValueNodeNumber < 0) ? new Type[] {aggExpType} :
												   new Type[] {getAttributeExpressionType(), aggExpType}, 
					getAttributeExpressionType(), aggregatorFunction);
		}
		
		// node assumed to have two or three children.
		// if two:
		//    child 0 - expression
		//    child 1 - AGGREGATE body
		// if three:
		//    child 0 - expression
		//    child 1 - initial accumulator
		//    child 2 - AGGREGATE body
		public AggregateResult makeAggregator(SimpleNode node, SummarizeItem summarizeItem, boolean distinct) {			
			Type aggExpType = buildAggregator(node, summarizeItem, distinct, 0);
			int initialValueNodeNumber = getChildCount(node) == 3 ? 1 : -1;
			OperatorDefinition aggregator = buildGenericAggregator(node, aggExpType, initialValueNodeNumber);
			return buildInvocation(aggExpType, summarizeItem, aggregator, node, initialValueNodeNumber);
		}
		
		// node assumed to have three or four children.
		// if three:
		//    child 0 - relation
		//    child 1 - expression
		//    child 2 - AGGREGATE body
		// if four:
		//    child 0 - relation
		//    child 1 - expression
		//    child 2 - initial accumulator
		//    child 3 - AGGREGATE body		
		public AggregateResult makeAggregator(SimpleNode node) {
			Type aggExpType = buildAggregator(node, 0, 1);
			int initialValueNodeNumber = getChildCount(node) == 4 ? 2 : -1;
			OperatorDefinition aggregator = buildGenericAggregator(node, aggExpType, initialValueNodeNumber);
			return buildInvocation(aggExpType, aggregator, node, initialValueNodeNumber);
		}
	}
	
	class AggregatorUserdefined extends Aggregator {
		AggregatorUserdefined(String opName) {
			super(opName);
		}
		Type getReturnType(Type attributeType) {
			return attributeType;
		}
		public AggregateResult makeAggregator(SimpleNode node, SummarizeItem summarizeItem, boolean distinct) {
			// Child 2 - expression
			Type aggExpType = buildAggregator(node, summarizeItem, distinct, 2);
			// Child 3 - [optional] initial value
			int initialValueNodeNumber = getChildCount(node) == 4 ? 3 : -1;
			introducedAttributeName = summarizeItem.getExtendAttributeName();
			return createOperatorInvocation(node, aggExpType, initialValueNodeNumber);
		}
		public AggregateResult makeAggregator(SimpleNode node) {
			// Child 1 - relation expression
			// Child 2 - attribute expression
			Type aggExpType = buildAggregator(node, 1, 2);
			// Child 3 - initial value (optional)
			int initialValueNodeNumber = getChildCount(node) == 4 ? 3 : -1;
			return createOperatorInvocation(node, aggExpType, initialValueNodeNumber);
		}
		private AggregateResult createOperatorInvocation(SimpleNode node, Type aggExpType, int initialValueNodeNumber) {
			Type aggregatableType = extendAndProjectToAggregatable(aggExpType, introducedAttributeName);
			Type initialValueType = null;
			if (initialValueNodeNumber >= 0) {
				initialValueType = (Type)compileChild(node, initialValueNodeNumber, null);
				if (!getAttributeExpressionType().canAccept(initialValueType))
					throw new ExceptionSemantic("RS0443: Expected type of initial value to be " + getAttributeExpressionType() + " but got " + initialValueType);
			}
			String operatorName = getOpNameForType(attributeExprType);
			OperatorSignature sig = new OperatorSignature("AGGREGATE_" + operatorName);
			sig.addParameterType(aggregatableType);
			if (initialValueType != null)
				sig.addParameterType(initialValueType);
			Type returnType = generator.compileEvaluate(sig);
			return new AggregateResult(attributeExprType, returnType);
		}
	}
	
	// aggregate SUM
	public Object visit(ASTAggSum node, Object data) {
		currentNode = node;
		return new AggregatorSum().createAggregator(node).getReturnType();
	}
	
	// aggregate AVG
	public Object visit(ASTAggAvg node, Object data) {
		currentNode = node;
		return new AggregatorAvg().createAggregator(node).getReturnType();
	}
	
	// aggregate MAX
	public Object visit(ASTAggMax node, Object data) {
		currentNode = node;
		return new AggregatorMax().createAggregator(node).getReturnType();
	}
	
	// aggregate MIN
	public Object visit(ASTAggMin node, Object data) {
		currentNode = node;
		return new AggregatorMin().createAggregator(node).getReturnType();
	}
	
	// aggregate AND
	public Object visit(ASTAggAnd node, Object data) {
		currentNode = node;
		return new AggregatorAnd().createAggregator(node).getReturnType();
	}
	
	// aggregate OR
	public Object visit(ASTAggOr node, Object data) {
		currentNode = node;
		return new AggregatorOr().createAggregator(node).getReturnType();
	}
	
	// aggregate XOR
	public Object visit(ASTAggXor node, Object data) {
		currentNode = node;
		return new AggregatorXor().createAggregator(node).getReturnType();
	}
	
	// aggregate UNION
	public Object visit(ASTAggUnion node, Object data) {
		currentNode = node;
		return new AggregatorUnion().createAggregator(node).getAttributeType();
	}

	// aggregate XUNION
	public Object visit(ASTAggXunion node, Object data) {
		currentNode = node;
		return new AggregatorXunion().createAggregator(node).getAttributeType();
	}
	
	// aggregate D_UNION
	public Object visit(ASTAggDUnion node, Object data) {
		currentNode = node;
		return new AggregatorDUnion().createAggregator(node).getAttributeType();
	}
	
	// aggregate INTERSECT
	public Object visit(ASTAggIntersect node, Object data) {
		currentNode = node;
		return new AggregatorIntersect().createAggregator(node).getAttributeType();
	}

	// aggregate AGGREGATE (generic aggregation)
	public Object visit(ASTAggAggregate node, Object data) {
		currentNode = node;
		return new AggregatorAggregate().makeAggregator(node).getReturnType();
	}
	
	// aggregate AGGREGATE (invoke user-defined aggregate operator)
	public Object visit(ASTAggAggregateUserdefined node, Object data) {
		currentNode = node;
		// Child 0 - operator name
		String aggOpName = getTokenOfChild(node, 0);
		return new AggregatorUserdefined(aggOpName).makeAggregator(node).getReturnType();
	}
	
	// EXACTLY.  Return TypeBoolean.
	public Object visit(ASTExactly node, Object data) {
		currentNode = node;
		int booleanExpressionCount = 0;
		if (getChildCount(node) >= 2) {
			Type firstExprType = (Type)compileChild(node, 1, data);
			if (firstExprType instanceof TypeRelation) {
				if (getChildCount(node) != 3)
					throw new ExceptionSemantic("RS0177: Aggregate EXACTLY expected three arguments, but got " + getChildCount(node));
				String attributeName = getTokenOfChild((SimpleNode)getChild(node, 2), 0);
				Heading source = ((TypeRelation)firstExprType).getHeading();
				// Get index of attribute in relation's tuples
				int attributeIndex = source.getIndexOf(attributeName);
				if (attributeIndex < 0)
					throw new ExceptionSemantic("RS0178: Attribute " + attributeName + " not found in " + firstExprType);
				Type attributeType = source.getAttributes().get(attributeIndex).getType();
				if (!(attributeType instanceof TypeBoolean))
					throw new ExceptionSemantic("RS0179: Aggregate EXACTLY expected attribute of type BOOLEAN, but got " + attributeType);
				generator.compilePush(ValueInteger.select(generator, attributeIndex));
				// Child 0 - ValueInteger n
				Type exactlyNType = (Type)compileChild(node, 0, data);
				if (!(exactlyNType instanceof TypeInteger))
					throw new ExceptionSemantic("RS0180: First parameter to EXACTLY expected INTEGER, but got " + exactlyNType);
				OperatorSignature sig = new OperatorSignature("EXACTLY");
				sig.addParameterType(firstExprType);
				sig.addParameterType(TypeInteger.getInstance());
				sig.addParameterType(TypeInteger.getInstance());
				return generator.compileEvaluate(sig);			
			} else if (firstExprType instanceof TypeBoolean) {
				booleanExpressionCount++;
				for (int i=2; i<getChildCount(node); i++) {
					Type exprType = (Type)compileChild(node, i, data);
					if (!(exprType instanceof TypeBoolean))
						throw new ExceptionSemantic("RS0181: n-adic EXACTLY expected BOOLEAN for argument " + (i + 1) + ", but got " + exprType);
					booleanExpressionCount++;
				}
			} else
				throw new ExceptionSemantic("RS0182: n-adic EXACTLY expected BOOLEAN for the second argument, but got " + firstExprType);
		}
		// Child 0 - ValueInteger n
		Type exactlyNType = (Type)compileChild(node, 0, data);
		if (!(exactlyNType instanceof TypeInteger))
			throw new ExceptionSemantic("RS0183: First parameter to EXACTLY expected INTEGER, but got " + exactlyNType);
		generator.compileExactly(booleanExpressionCount);		
		return TypeBoolean.getInstance();
	}

	// SUMMARIZE
	public Object visit(ASTSummarize node, Object data) {
		currentNode = node;
		// Child 0 - source expression
		Type sourceType = (Type)compileChild(node, 0, data);
		if (!(sourceType instanceof TypeRelation))
			throw new ExceptionSemantic("RS0184: Expected RELATION for first operand of SUMMARIZE, but got " + sourceType);
		// Child 1 - SummarizePerOrBy
		TypeRelation perType = (TypeRelation)compileChild(node, 1, sourceType);
		Generator.Summarize summarize = generator.new Summarize((TypeRelation)sourceType, perType);
		// Child 2 - SummarizeItems
		compileChild(node, 2, summarize);
		return summarize.endSummarize();
	}

	// SUMMARIZE optional 'PER' or 'BY'
	public Object visit(ASTSummarizePerOrBy node, Object data) {
		currentNode = node;
		// data is Type of source
		if (getChildCount(node) == 0) {
			// PER TABLE_DEE
			generator.compilePush(ValueRelation.getDee(generator));
			return TypeRelation.getEmptyRelationType();
		} else
			// Compile PER or BY
			return compileChild(node, 0, data);
	}

	// SUMMARIZE 'PER'
	public Object visit(ASTSummarizePer node, Object data) {
		currentNode = node;
		// data is Type of source
		// Child 0 - 'per' expression
		Type perType = (Type)compileChild(node, 0, data);
		if (!(perType instanceof TypeRelation))
			throw new ExceptionSemantic("RS0185: Expected RELATION for second operand of SUMMARIZE, but got " + perType);
		return perType;
	}
	
	// SUMMARIZE 'BY'
	public Object visit(ASTSummarizeBy node, Object data) {
		currentNode = node;
		// data is Type of source
		// Child 0 - AttributeNameList
		SelectAttributes nameList = (SelectAttributes)compileChild(node, 0, data);
		// DUP expression, and project it by the given AttributeNameList
		generator.compileDuplicate();
		return generator.compileRelationProject((TypeRelation)data, nameList);
	}

	// SUMMARIZE items
	public Object visit(ASTSummarizeItems node, Object data) {
		currentNode = node;
		// data is Generator.Summarize
		compileChildren(node, data);
		return null;
	}

	// SUMMARIZE item
	public Object visit(ASTSummarizeItem node, Object data) {
		currentNode = node;
		// data is Generator.Summarize
		Generator.Summarize.SummarizeItem item = ((Generator.Summarize)data).new SummarizeItem();
		// Child 0 - identifier
		String itemIntroducedName = getTokenOfChild(node, 0);
		// Child 1 - summarize aggregation
		Type aggReturnType = (Type)compileChild(node, 1, item);
		item.endSummarizeItem(aggReturnType, itemIntroducedName);
		return null;
	}
	
	// SUMMARIZE - COUNT
	public Object visit(ASTSummarizeCount node, Object data) {
		currentNode = node;
		// data is Generator.SummarizeItem
		Generator.Summarize.SummarizeItem item = (Generator.Summarize.SummarizeItem)data;
		OperatorSignature signature = new OperatorSignature("COUNT");
		signature.addParameterType(item.getTypeOfY());
		return generator.compileEvaluate(signature);
	}

	// SUMMARIZE - COUNTD
	public Object visit(ASTSummarizeCountDistinct node, Object data) {
		currentNode = node;
		// data is Generator.SummarizeItem
		Generator.Summarize.SummarizeItem item = (Generator.Summarize.SummarizeItem)data;
		// Child 0 - expression
		item.beginSummarizeItemExpression();
		Type exprType = (Type)compileChild(node, 0, data);
		item.endSummarizeItemExpression(exprType, true);
		OperatorSignature signature = new OperatorSignature("COUNT");
		signature.addParameterType(item.getTypeOfY());
		return generator.compileEvaluate(signature);
	}
	
	// SUMMARIZE - SUM
	public Object visit(ASTSummarizeSum node, Object data) {
		currentNode = node;
		return new AggregatorSum().createAggregator(node, (Generator.Summarize.SummarizeItem)data, false).getReturnType();
	}
	
	// SUMMARIZE - SUMD
	public Object visit(ASTSummarizeSumDistinct node, Object data) {
		currentNode = node;
		return new AggregatorSum().createAggregator(node, (Generator.Summarize.SummarizeItem)data, true).getReturnType();
	}

	// SUMMARIZE - AVG
	public Object visit(ASTSummarizeAvg node, Object data) {
		currentNode = node;
		return new AggregatorAvg().createAggregator(node, (Generator.Summarize.SummarizeItem)data, false).getReturnType();
	}
	
	// SUMMARIZE - AVGD
	public Object visit(ASTSummarizeAvgDistinct node, Object data) {
		currentNode = node;
		return new AggregatorAvg().createAggregator(node, (Generator.Summarize.SummarizeItem)data, true).getReturnType();
	}

	// SUMMARIZE - MAX
	public Object visit(ASTSummarizeMax node, Object data) {
		currentNode = node;
		return new AggregatorMax().createAggregator(node, (Generator.Summarize.SummarizeItem)data, false).getReturnType();
	}
	
	// SUMMARIZE - MIN
	public Object visit(ASTSummarizeMin node, Object data) {
		currentNode = node;
		return new AggregatorMin().createAggregator(node, (Generator.Summarize.SummarizeItem)data, false).getReturnType();
	}

	// SUMMARIZE - AND
	public Object visit(ASTSummarizeAnd node, Object data) {
		currentNode = node;
		return new AggregatorAnd().createAggregator(node, (Generator.Summarize.SummarizeItem)data, false).getReturnType();
	}

	// SUMMARIZE - OR
	public Object visit(ASTSummarizeOr node, Object data) {
		currentNode = node;
		return new AggregatorOr().createAggregator(node, (Generator.Summarize.SummarizeItem)data, false).getReturnType();
	}
	
	// SUMMARIZE - XOR
	public Object visit(ASTSummarizeXor node, Object data) {
		currentNode = node;
		return new AggregatorXor().createAggregator(node, (Generator.Summarize.SummarizeItem)data, false).getReturnType();
	}

	// SUMMARIZE - UNION
	public Object visit(ASTSummarizeUnion node, Object data) {
		currentNode = node;
		return new AggregatorUnion().createAggregator(node, (Generator.Summarize.SummarizeItem)data, false).getAttributeType();
	}

	// SUMMARIZE - XUNION
	public Object visit(ASTSummarizeXunion node, Object data) {
		currentNode = node;
		return new AggregatorXunion().createAggregator(node, (Generator.Summarize.SummarizeItem)data, false).getAttributeType();
	}

	// SUMMARIZE - D_UNION
	public Object visit(ASTSummarizeDUnion node, Object data) {
		currentNode = node;
		return new AggregatorDUnion().createAggregator(node, (Generator.Summarize.SummarizeItem)data, false).getAttributeType();
	}
	
	// SUMMARIZE - INTERSECT
	public Object visit(ASTSummarizeIntersect node, Object data) {
		currentNode = node;
		return new AggregatorIntersect().createAggregator(node, (Generator.Summarize.SummarizeItem)data, false).getAttributeType();
	}
	
	// SUMMARIZE - AGGREGATE
	public Object visit(ASTSummarizeAggregate node, Object data) {
		currentNode = node;
		return new AggregatorAggregate().makeAggregator(node, (Generator.Summarize.SummarizeItem)data, false).getReturnType();
	}
	
	// SUMMARIZE - AGGREGATED
	public Object visit(ASTSummarizeAggregateDistinct node, Object data) {
		currentNode = node;
		return new AggregatorAggregate().makeAggregator(node, (Generator.Summarize.SummarizeItem)data, true).getReturnType();
	}

	// SUMMARIZE - user-defined aggregation operator
	public Object visit(ASTSummarizeUserdefined node, Object data) {
		currentNode = node;
		// Child 0 - identifier
		String aggOpName = getTokenOfChild(node, 0);
		// Child 1 - SummarizeUserdefinedDistinct
		compileChild(node, 1, data);
		// Child 2 - expression
		// Child 3 - [optional] initial value
		Generator.Summarize.SummarizeItem summarizeItem = (Generator.Summarize.SummarizeItem)data;		
		return new AggregatorUserdefined(aggOpName).makeAggregator(node, summarizeItem, summarizeItem.isDistinct()).getReturnType();
	}
	
	// SUMMARIZE aggregation - user-defined aggregation operator invocation optional DISTINCT keyword
	public Object visit(ASTSummarizeUserdefinedDistinct node, Object data) {
		compileChildren(node, data);	// SummarizeUserDefinedDistinctTrue, if it's there
		return null;
	}
	
	// SUMMARIZE aggregation - user-defined aggregation operator invocation specified DISTINCT keyword
	public Object visit(ASTSummarizeUserdefinedDistinctTrue node, Object data) {
		Generator.Summarize.SummarizeItem summarizeItem = (Generator.Summarize.SummarizeItem)data;
		summarizeItem.setDistinct(true);
		return null;
	}

	private class SummarizeExactlyAggregator extends Aggregator {
		SummarizeExactlyAggregator() {
			super("EXACTLY");
		}
		AggregateResult createAggregator(SimpleNode node, Object data, boolean distinct) {
			currentNode = node;
			// data is Generator.SummarizeItem
			Generator.Summarize.SummarizeItem item = (Generator.Summarize.SummarizeItem)data;
			// Child 1 - expression
			item.beginSummarizeItemExpression();
			Type exprType = (Type)compileChild(node, 1, data);
			if (!(exprType instanceof TypeBoolean))
				throw new ExceptionSemantic("RS0186: Aggregate EXACTLY expected BOOLEAN, but got " + exprType);
			TypeRelation aggExpType = item.endSummarizeItemExpression(exprType, distinct);
			// Child 0 - n
			Type exactlyNType = (Type)compileChild(node, 0, data);
			if (!(exactlyNType instanceof TypeInteger))
				throw new ExceptionSemantic("RS0187: First parameter to EXACTLY expected INTEGER, but got " + exactlyNType);
			Heading source = ((TypeRelation)aggExpType).getHeading();
			// Get index of attribute in relation's tuples
			int attributeIndex = source.getIndexOf(item.getExtendAttributeName());
			if (attributeIndex < 0)
				throw new ExceptionFatal("RS0313: Attribute " + item.getExtendAttributeName() + " not found in " + aggExpType);
			generator.compilePush(ValueInteger.select(generator, attributeIndex));
			OperatorSignature sig = new OperatorSignature("EXACTLY");
			sig.addParameterType(aggExpType);
			sig.addParameterType(TypeInteger.getInstance());
			sig.addParameterType(TypeInteger.getInstance());
			return new AggregateResult(null, generator.compileEvaluate(sig));			
		}
		void checkAttributeType(Type t) {}
	}

	// SUMMARIZE - EXACTLY
	public Object visit(ASTSummarizeExactly node, Object data) {
		currentNode = node;
		return (new SummarizeExactlyAggregator()).createAggregator(node, data, false).getReturnType();
	}
	
	// SUMMARIZE - EXACTLYD
	public Object visit(ASTSummarizeExactlyDistinct node, Object data) {
		currentNode = node;
		return (new SummarizeExactlyAggregator()).createAggregator(node, data, true).getReturnType();
	}

	// construct n-adic boolean operation handler
	private class NadicBooleanOperation {
		private String opName;
		private String opOperatorName;
		NadicBooleanOperation(SimpleNode node, String opName, String opOperatorName) {
			currentNode = node;
			this.opName = opName;
			this.opOperatorName = opOperatorName;
			// Child 0 - BooleanExpressionCommalist
			int opCount = ((Integer)compileChild(node, 0, null)).intValue();
			if (opCount == 0)
				compileEmptyListResult();
			else if (opCount > 1)
				for (int i=0; i<opCount - 1; i++)
					compileOperation();
		}
		void compileOperation() {
			OperatorSignature signature = new OperatorSignature(getOpOperatorName());
			signature.addParameterType(TypeBoolean.getInstance());
			signature.addParameterType(TypeBoolean.getInstance());
			signature.setReturnType(TypeBoolean.getInstance());
			generator.compileEvaluate(signature);
		}
		String getOpOperatorName() {return opOperatorName;}
		void compileEmptyListResult() {
			throw new ExceptionSemantic("RS0188: n-adic " + opName + " was specified with no operands.  You must specify at least one operand.");			
		}
	}
	
	// n-adic OR.  Return TypeBoolean.
	public Object visit(ASTNadicOr node, Object data) {
		currentNode = node;
		// TODO - optimise by exiting on encountering first OR that returns true.
		new NadicBooleanOperation(node, "OR", BuiltinTypeBuilder.OR) {
			void compileEmptyListResult() {
				generator.compilePush(false);
			}
		};
		return TypeBoolean.getInstance();
	}
	
	// n-adic AND.  Return TypeBoolean.
	public Object visit(ASTNadicAnd node, Object data) {
		currentNode = node;
		// TODO - optimise by exiting on encountering first AND that returns false.
		new NadicBooleanOperation(node, "AND", BuiltinTypeBuilder.AND) {
			void compileEmptyListResult() {
				generator.compilePush(true);
			}
		};
		return TypeBoolean.getInstance();
	}
	
	// n-adic XOR.  Return TypeBoolean.
	public Object visit(ASTNadicXor node, Object data) {
		currentNode = node;
		new NadicBooleanOperation(node, "XOR", BuiltinTypeBuilder.XOR) {
			void compileEmptyListResult() {
				generator.compilePush(false);
			}
		};
		return TypeBoolean.getInstance();
	}

	private class NadicOperation {
		private ExpressionCommalist commalist; 
		private String opName;
		private String opOperatorName;
		NadicOperation(SimpleNode node, String opName, String opOperatorName) {
			currentNode = node;
			this.opName = opName;
			this.opOperatorName = opOperatorName;
			// Child 0 - ExpressionCommalist
			commalist = (ExpressionCommalist)compileChild(node, 0, null);
			if (commalist.count == 0)
				commalist.exprType = compileEmptyListResult();
			else if (commalist.count > 1)
				for (int i=0; i<commalist.count - 1; i++)
					compileOperation();
		}
		void compileOperation() {
			OperatorSignature signature = new OperatorSignature(getOperatorName());
			signature.addParameterType(getFirstOperandType());
			signature.addParameterType(getFirstOperandType());
			signature.setReturnType(getFirstOperandType());
			generator.compileEvaluate(signature);
		}
		String getOperatorName() {return opOperatorName;}
		Type compileEmptyListResult() {
			throw new ExceptionSemantic("RS0189: n-adic " + opName + " was specified with no operands.  You must specify at least one operand.");			
		}
		Type getFirstOperandType() {
			return commalist.exprType;			
		}
	}
	
	// n-adic COUNT
	public Object visit(ASTNadicCount node, Object data) {
		currentNode = node;
		// Child 0 - ExpressionCommalist
		ExpressionCommalist commalist = (ExpressionCommalist)compileChild(node, 0, null);
		for (int i=0; i<commalist.count; i++)
			generator.compilePop();
		generator.compilePush(ValueInteger.select(generator, commalist.count));
		return TypeInteger.getInstance();
	}
	
	// n-adic SUM.  Return type of first operand.
	public Object visit(ASTNadicSum node, Object data) {
		currentNode = node;
		return (new NadicOperation(node, "SUM", BuiltinTypeBuilder.PLUS) {
			Type compileEmptyListResult() {
				generator.compilePush(0);
				return TypeInteger.getInstance();
			}
		}).getFirstOperandType();
	}

	// n-adic MAX.  Return type of first operand.
	public Object visit(ASTNadicMax node, Object data) {
		currentNode = node;
		return (new NadicOperation(node, "MAX", BuiltinTypeBuilder.MAX)).getFirstOperandType();
	}

	// n-adic MIN.  Return type of first operand.
	public Object visit(ASTNadicMin node, Object data) {
		currentNode = node;
		return (new NadicOperation(node, "MIN", BuiltinTypeBuilder.MIN)).getFirstOperandType();
	}

	// n-adic AVG.  Return rational.
	public Object visit(ASTNadicAvg node, Object data) {
		currentNode = node;
		// Child 0 - ExpressionCommalist
		ExpressionCommalist commalist = (ExpressionCommalist)compileChild(node, 0, null);
		generator.compileNadicAverage(commalist.count);
		return TypeRational.getInstance();
	}
	
	private static class ExpressionCommalist {
		int count;
		Type exprType;
	}
	
	// Comma-separated expression list.  Return ExpressionCommalist.
	public Object visit(ASTExpressionCommalist node, Object data) {
		currentNode = node;
		ExpressionCommalist commalist = new ExpressionCommalist();
		commalist.count = getChildCount(node);
		commalist.exprType = null;
		for (int i=0; i<getChildCount(node); i++) {
			Type t = (Type)compileChild(node, i, data);
			if (commalist.exprType == null)
				commalist.exprType = t;
			else if (commalist.exprType != t)
				throw new ExceptionSemantic("RS0190: Argument " + i + " of an n-adic expression list is " + t + " but " + commalist.exprType + " was expected.");
		}
		return commalist;
	}

	// Comma-separated boolean expression list.  Compile the list and
	// return the number of compiled nodes.  Check to ensure all operands are TypeBoolean.
	public Object visit(ASTBooleanExpressionCommalist node, Object data) {
		currentNode = node;
		int count = getChildCount(node);
		for (int i=0; i<getChildCount(node); i++) {
			Type t = (Type)compileChild(node, i, data);
			if (!(t instanceof TypeBoolean))
				throw new ExceptionSemantic("RS0191: Argument " + i + " of an n-adic boolean expression list is " + t + " but BOOLEAN was expected.");
		}
		return new Integer(count);
	}

	// IN
	public Object visit(ASTTupleIn node, Object data) {
		currentNode = node;
		Type leftType = (Type)compileChild(node, 0, data);
		if (!(leftType instanceof TypeTuple))
			throw new ExceptionSemantic("RS0192: Expected TUPLE on left side of IN, but got " + leftType);
		Type rightType = (Type)compileChild(node, 1, data);
		if (!(rightType instanceof TypeRelation))
			throw new ExceptionSemantic("RS0193: Expected RELATION on right side of IN, but got " + rightType);
		if (!(new TypeRelation(((TypeTuple)leftType).getHeading()).canAccept(rightType)))
			throw new ExceptionSemantic("RS0194: " + leftType + " does not have the same heading as " + rightType);
		return generator.compileTupleIn((TypeTuple)leftType, (TypeRelation)rightType);
	}
	
	// Concatenate (||)
	public Object visit(ASTConcatenate node, Object data) {
		currentNode = node;
		compileChild(node, 0, data);
		compileChild(node, 1, data);
		generator.compileConcatenate();
		return TypeCharacter.getInstance();
	}

	// ==
	public Object visit(ASTCompEqual node, Object data) {
		currentNode = node;
		Type leftType = (Type)compileChild(node, 0, data);
		Type rightType = (Type)compileChild(node, 1, data);
		return generator.compileEQ(leftType, rightType);		
	}

	// !=
	public Object visit(ASTCompNequal node, Object data) {
		currentNode = node;
		Type leftType = (Type)compileChild(node, 0, data);
		Type rightType = (Type)compileChild(node, 1, data);
		return generator.compileNEQ(leftType, rightType);
	}

	// >=
	public Object visit(ASTCompGTE node, Object data) {
		currentNode = node;
		Type leftType = (Type)compileChild(node, 0, data);
		Type rightType = (Type)compileChild(node, 1, data);
		return generator.compileGTE(leftType, rightType);
	}

	// <=
	public Object visit(ASTCompLTE node, Object data) {
		currentNode = node;
		Type leftType = (Type)compileChild(node, 0, data);
		Type rightType = (Type)compileChild(node, 1, data);
		return generator.compileLTE(leftType, rightType);
	}

	// >
	public Object visit(ASTCompGT node, Object data) {
		currentNode = node;
		Type leftType = (Type)compileChild(node, 0, data);
		Type rightType = (Type)compileChild(node, 1, data);
		return generator.compileGT(leftType, rightType);
	}

	// <
	public Object visit(ASTCompLT node, Object data) {
		currentNode = node;
		Type leftType = (Type)compileChild(node, 0, data);
		Type rightType = (Type)compileChild(node, 1, data);
		return generator.compileLT(leftType, rightType);
	}
	
	// XOR
	public Object visit(ASTXor node, Object data) {
		currentNode = node;
		Type leftType = (Type)compileChild(node, 0, data);
		Type rightType = (Type)compileChild(node, 1, data);
		return generator.compileOperatorInvocation(BuiltinTypeBuilder.XOR, leftType, rightType, TypeBoolean.getInstance());
	}
	
	// OR
	public Object visit(ASTOr node, Object data) {
		currentNode = node;
		Type leftType = (Type)compileChild(node, 0, data);
		Type rightType = (Type)compileChild(node, 1, data);
		return generator.compileOperatorInvocation(BuiltinTypeBuilder.OR, leftType, rightType, TypeBoolean.getInstance());
	}

	// AND
	public Object visit(ASTAnd node, Object data) {
		currentNode = node;
		Type leftType = (Type)compileChild(node, 0, data);
		Type rightType = (Type)compileChild(node, 1, data);
		return generator.compileOperatorInvocation(BuiltinTypeBuilder.AND, leftType, rightType, TypeBoolean.getInstance());
	}

	// NOT  Return Type of expression.
	public Object visit(ASTUnaryNot node, Object data) {
		currentNode = node;
		Type opType = (Type)compileChild(node, 0, data);
		return generator.compileOperatorInvocation(BuiltinTypeBuilder.NOT, opType, TypeBoolean.getInstance());
	}
		
	// +
	public Object visit(ASTAdd node, Object data) {
		currentNode = node;
		Type leftType = (Type)compileChild(node, 0, data);
		Type rightType = (Type)compileChild(node, 1, data);
		return generator.compileOperatorInvocation(BuiltinTypeBuilder.PLUS, leftType, rightType, rightType);
	}

	// -
	public Object visit(ASTSubtract node, Object data) {
		currentNode = node;
		Type leftType = (Type)compileChild(node, 0, data);
		Type rightType = (Type)compileChild(node, 1, data);
		return generator.compileOperatorInvocation(BuiltinTypeBuilder.MINUS, leftType, rightType, rightType);
	}

	// *
	public Object visit(ASTTimes node, Object data) {
		currentNode = node;
		Type leftType = (Type)compileChild(node, 0, data);
		Type rightType = (Type)compileChild(node, 1, data);
		return generator.compileOperatorInvocation(BuiltinTypeBuilder.TIMES, leftType, rightType, rightType);
	}

	// /
	public Object visit(ASTDivide node, Object data) {
		currentNode = node;
		Type leftType = (Type)compileChild(node, 0, data);
		Type rightType = (Type)compileChild(node, 1, data);
		return generator.compileOperatorInvocation(BuiltinTypeBuilder.DIVIDE, leftType, rightType, rightType);
	}
	
	// % (modulo)
	public Object visit(ASTMod node, Object data) {
		currentNode = node;
		Type leftType = (Type)compileChild(node, 0, data);
		Type rightType = (Type)compileChild(node, 1, data);
		return generator.compileOperatorInvocation(BuiltinTypeBuilder.MODULO, leftType, rightType, rightType);
	}
	
	// + (unary)  Return Type of expression.
	public Object visit(ASTUnaryPlus node, Object data) {
		currentNode = node;
		Type opType = (Type)compileChild(node, 0, data);
		return generator.compileOperatorInvocation(BuiltinTypeBuilder.UNARY_PLUS, opType, opType);
	}

	// - (unary)  Return Type of expression.
	public Object visit(ASTUnaryMinus node, Object data) {
		currentNode = node;
		Type opType = (Type)compileChild(node, 0, data);
		return generator.compileOperatorInvocation(BuiltinTypeBuilder.UNARY_MINUS, opType, opType);
	}
	
	// WITH
	public Object visit(ASTWith node, Object data) {
		currentNode = node;
		Generator.With with = generator.new With();
		// Child 0 - WITH name introduction list
		compileChild(node, 0, with);
		// Child 1 - WITH expression
		Type expressionType = (Type)compileChild(node, 1, data);
		return with.endWith(expressionType);
	}

	// WITH name introduction list
	public Object visit(ASTWithNameIntroCommalist node, Object data) {
		currentNode = node;
		// data is With
		compileChildren(node, data);
		return null;
	}

	// WITH name introduction
	public Object visit(ASTWithNameIntro node, Object data) {
		currentNode = node;
		// data is With
		// Child 0 - identifier
		String introducedName = getTokenOfChild(node, 0);
		// Child 1 - expression
		Type expressionType = (Type)compileChild(node, 1, data);
		((Generator.With)data).addWithItem(expressionType, introducedName);
		return null;
	}
	
	// TCLOSE
	public Object visit(ASTTClose node, Object data) {
		// Child 0 - expression
		return generator.compileTClose((Type)compileChild(node, 0, data));
	}
	
	private OperatorDefinition lastAnonymousOperatorDefinition;
	
	// Compile and push anonymous operator
	public Object visit(ASTLambda node, Object data) {
		currentNode = node;
		//
		OperatorDefinition operator = generator.beginAnonymousOperator();
		// Child 0 - parameter def commalist
		compileChild(node, 0, data);
		// Child 1 - RETURNS type_ref
		Type returnType = (Type)compileChild(node, 1, data);
		generator.setDeclaredReturnType(returnType);
		// Child 2 - body
		compileChild(node, 2, data);
		// done
		generator.endOperator();
		// push operator as value
		operator.setSourceCode(getSourceCodeOf(node));
		generator.compilePush(new ValueOperator(generator, operator.getOperator(), operator.getSourceCode()));
		lastAnonymousOperatorDefinition = operator;
		return new TypeOperator(operator.getSignature());
	}

	// Return most recent anonymous operator definition
	public OperatorDefinition getLastAnonymousOperatorDefinition() {
		return lastAnonymousOperatorDefinition;
	}

	// Compile literal RELATION.  Return TypeRelation.
	public Object visit(ASTRelation node, Object data) {
		currentNode = node;
		// Child 0 - optional heading specification
		Heading heading = (Heading)compileChild(node, 0, data);
		// Compile literal relation
		Generator.RelationDefinition relation = generator.new RelationDefinition(heading);
		// Child 1 - HeadingExpCommalist
		compileChild(node, 1, relation);
		return relation.endRelation();
	}
	
	// Optional RELATION heading specification.  Return Heading if specified, null otherwise.
	public Object visit(ASTRelationHeading node, Object data) {
		currentNode = node;
		if (getChildCount(node) > 0)
			return compileChild(node, 0, data);
		return null;		
	}

	// Tuple expression list
	public Object visit(ASTTupleExpressionCommalist node, Object data) {
		currentNode = node;
		// data is Generator.RelationDefinition
		Generator.RelationDefinition relation = (Generator.RelationDefinition)data;
		// Every child should be a ValueTuple
		for (int i=0; i<getChildCount(node); i++) {
			Type exprType = (Type)compileChild(node, i, data);
			if (!(exprType instanceof TypeTuple))
				throw new ExceptionSemantic("RS0195: Expected TUPLE in literal relation; got " + exprType);
			relation.addTupleToRelation((TypeTuple)exprType);
		}
		return null;
	}
	
	// TABLE_DUM
	public Object visit(ASTRelationDum node, Object data) {
		currentNode = node;
		generator.compilePush(ValueRelation.getDum(generator));
		return TypeRelation.getEmptyRelationType();
	}	
	
	// TABLE_DEE
	public Object visit(ASTRelationDee node, Object data) {
		currentNode = node;
		generator.compilePush(ValueRelation.getDee(generator));
		return TypeRelation.getEmptyRelationType();
	}
	
	// Compile literal TUPLE.  Return TypeTuple.
	public Object visit(ASTTuple node, Object data) {
		currentNode = node;
		// Compile literal tuple
		Generator.TupleDefinition tuple = generator.new TupleDefinition();
		// Children - TupleComponent
		compileChildren(node, tuple);
		return tuple.endTuple();
	}
	
	// tuple component
	public Object visit(ASTTupleComponent node, Object data) {
		currentNode = node;
		// data is Generator.TupleDefinition
		Generator.TupleDefinition tuple = (Generator.TupleDefinition)data;
		// Child 0 - identifier
		String identifier = getTokenOfChild(node, 0);
		// Child 1 - expression
		Type expressionType = (Type)compileChild(node, 1, data);
		tuple.setTupleAttribute(identifier, expressionType);
		return null;
	}
	
	// tuple wildcard, i.e., TUPLE {*}
	public Object visit(ASTTupleComponentWildcard node, Object data) {
		currentNode = node;
		// data is Generator.TupleDefinition
		Generator.TupleDefinition tuple = (Generator.TupleDefinition)data;
		tuple.setWildcard();
		return null;
	}
	
	// Capture string literal.  Return String.
	public Object visit(ASTStringLiteral node, Object data) {
		currentNode = node;
		return ValueCharacter.stripDelimitedString(node.tokenValue);
	}

	// Compile literal delimited string.  Return TypeCharacter.
	public Object visit(ASTCharacter node, Object data) {
		currentNode = node;
		generator.compilePushDelimitedString(node.tokenValue);
		return TypeCharacter.getInstance();
	}
	
	// Compile literal integer.  Return TypeInteger.
	public Object visit(ASTInteger node, Object data) {
		currentNode = node;
		try {
			generator.compilePush(Long.parseLong(node.tokenValue));
		} catch (java.lang.NumberFormatException nfe) {
			throw new ExceptionSemantic("RS0196: Invalid INTEGER '" + node.tokenValue + "'");
		}
		return TypeInteger.getInstance();
	}

	// Compile literal rational.  Return TypeRational.
	public Object visit(ASTRational node, Object data) {
		currentNode = node;
		try {
			generator.compilePush(Double.parseDouble(node.tokenValue));
		} catch (java.lang.NumberFormatException nfe) {
			throw new ExceptionSemantic("RS0197: Invalid RATIONAL '" + node.tokenValue + "'");			
		}
		return TypeRational.getInstance();
	}

	// Compile literal boolean true.  Return TypeBoolean.
	public Object visit(ASTTrue node, Object data) {
		currentNode = node;
		generator.compilePush(true);
		return TypeBoolean.getInstance();
	}

	// Compile literal boolean false.  Return TypeBoolean.
	public Object visit(ASTFalse node, Object data) {
		currentNode = node;
		generator.compilePush(false);
		return TypeBoolean.getInstance();
	}

}
