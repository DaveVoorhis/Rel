package org.reldb.rel.v0.languages.tutoriald;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.reldb.rel.v0.generator.Generator;
import org.reldb.rel.v0.types.Heading;
import org.reldb.rel.v0.types.builtin.TypeCharacter;
import org.reldb.rel.v0.values.TupleIterator;
import org.reldb.rel.v0.values.Value;
import org.reldb.rel.v0.values.ValueCharacter;
import org.reldb.rel.v0.values.ValueTuple;

public class Keywords {
	
	// Rel / Tutorial D keywords.
	private static final String[] fgKeywords = {   
	    "ADD",
	    "AGGREGATE",
	    "AGGREGATED",
	    "ALL",
	    "ALTER",
	    "AND",
	    "ANNOUNCE",
	    "ARRAY",
	    "AS",
	    "ASC",
	    "ATTRIBUTES_OF",
	    "AVG",
	    "AVGD",
	    "BASE",
	    "BACKUP",
	    "BEGIN",
	    "BUT",
	    "BY",
	    "CALL",
	    "CASE",
	    "COMMIT",
	    "COMPOSE",
	    "CONSTRAINT",
	    "COUNT",
	    "COUNTD",
	    "DELETE",
	    "DESC",
	    "DIVIDEBY",
	    "DISTINCT",
	    "DO",
	    "DROP",
	    "D_INSERT",
	    "D_UNION",
	    "ELSE",
	    "END",
	    "<EOT>",
	    "EXACTLYD",
	    "EXACTLY",
	    "EXECUTE",
	    "EXTEND",
	    "EXTERNAL",
	    "FALSE",
	    "FOREIGN",
	    "FOR",
	    "FROM",
	    "GROUP",
	    "I_DELETE",
	    "I_MINUS",
	    "IF",
	    "IMAGE_IN",
	    "IN",
	    "INIT",
	    "INSERT",
	    "INTERSECT",
	    "IS",
	    "JOIN",
	    "KEY",
	    "LEAVE",
	    "LOAD",
	    "~[",
	    "]~",
	    "MATCHING",
	    "MAX",
	    "MIN",
	    "MINUS",
	    "NOEMPTY",
	    "NOT",
	    "OPERATOR",
	    "ORDER",
	    "ORDERED",
	    "ORDINAL",
	    "OR",
	    "OUTPUT",
	    "PER",
	    "POSSREP",
	    "PREFIX",
	    "PRIVATE",
	    "PUBLIC",
	    "REAL",
	    "RELATION",
	    "REL",
	    "RENAME",
	    "RETURN",
	    "RETURNS",
	    "ROLLBACK",
	    "SAME_HEADING_AS",
	    "SAME_TYPE_AS",
	    "SET",
	    "SEMIJOIN" ,
	    "SEMIMINUS",
	    "SUFFIX",
	    "SUMD",
	    "SUMMARIZE",
	    "SUM",
	    "SYNONYMS",
	    "DEE",
	    "TABLE_DEE",
	    "DUM",
	    "TABLE_DUM",
	    "TCLOSE",
	    "THEN",
	    "TIMES",
	    "TO",
	    "TRANSACTION",
	    "TRUE",
	    "TUPLE",
	    "TUP",
	    "TYPE",
	    "TYPE_OF",
	    "UNGROUP",
	    "UNION",
	    "UNORDER",
	    "UNWRAP",
	    "UPDATES",
	    "UPDATE",
	    "VAR",
	    "VERSION",
	    "VIRTUAL",
	    "VIEW",
	    "WHEN",
	    "WHERE",
	    "WHILE",
	    "WITH",
	    "WRAP",
	    "WRITE",
	    "WRITELN",
	    "XOR",
	    "XUNION"
	};
	
	private static List<String>keywords = null;
	
	private static Iterator<String> getKeywordIterator() {
		if (keywords == null)
			keywords = Arrays.asList(fgKeywords);
		return keywords.iterator();
	}
	
	/** Obtain heading for keywords relvar.
	 * REL {Identifier CHAR, Documentation CHAR, isFileConnectionString BOOLEAN, FileExtensions REL {Extension CHAR}}
	 */
	public static Heading getHeading() {
		Heading heading = new Heading();
		heading.add("Keyword", TypeCharacter.getInstance());
		return heading;
	}
	
	/** Obtain keywords list as a TupleIterator for the above.
	 */
	public static TupleIterator getKeywords(Generator generator) {
		return new TupleIterator() {
			Iterator<String> iterator = getKeywordIterator();
			@Override
			public boolean hasNext() {
				return iterator.hasNext();
			}
			@Override
			public ValueTuple next() {
				Value rawTuple[];
				String keyword = iterator.next();
				rawTuple = new Value[] {
					ValueCharacter.select(generator, keyword)
				};
				return new ValueTuple(generator, rawTuple);
			}
			@Override
			public void close() {}
		};
	}

	public static long getCardinality() {
		return fgKeywords.length;
	}
}
