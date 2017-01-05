package org.reldb.rel.v0.values;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Set;

import org.reldb.rel.exceptions.*;
import org.reldb.rel.v0.generator.Generator;
import org.reldb.rel.v0.storage.RelDatabase;
import org.reldb.rel.v0.storage.temporary.TempIndex;
import org.reldb.rel.v0.storage.temporary.TempIndexImplementation;
import org.reldb.rel.v0.types.*;
import org.reldb.rel.v0.types.builtin.TypeInteger;
import org.reldb.rel.v0.vm.Context;

public abstract class ValueRelation extends ValueAbstract implements Projectable, TupleIteratable {
	
	public ValueRelation(Generator generator) {
		super(generator);
	}

	private static final long serialVersionUID = 0;
	
	/** Obtain a serializable clone of this value. */
	public Value getSerializableClone() {
		// TODO - fix this to use TempStorageTuples in an appropriate manner, i.e., one that doesn't invoke getSerializableClone()
		ValueRelationLiteral newRelation = new ValueRelationLiteral(getGenerator());
		TupleIterator iterator = iterator();
		try {
			while (iterator.hasNext())
				newRelation.insert((ValueTuple)iterator.next().getSerializableClone());
		} finally {
			iterator.close();
		}
		return newRelation;
	}

	public static ValueRelation getDum(Generator generator) {
		return new ValueRelation(generator) {
			private static final long serialVersionUID = 0;

			public int hashCode() {
				return 0;
			}
			
			public TupleIterator newIterator() {
				return new TupleIterator() {
					public boolean hasNext() {
						return false;
					}

					public ValueTuple next() {
						throw new NoSuchElementException();
					}
					
					public void close() {}
				};
			}
		};
	}

	public static ValueRelation getDee(Generator generator) {
		return new ValueRelation(generator) {
			private static final long serialVersionUID = 0;

			public int hashCode() {
				return 1;
			}
			
			public TupleIterator newIterator() {
				return new TupleIterator() {
					public boolean available = true;

					public boolean hasNext() {
						return available;
					}

					public ValueTuple next() {
						if (available)
							try {
								return ValueTuple.getEmptyTuple(getGenerator());
							} finally {
								available = false;
							}
						throw new NoSuchElementException();
					}
					
					public void close() {}
				};
			}
		};	
	}

	public ValueTuple getTuple() {
		TupleIterator iterator = iterator();
		try {
			if (iterator.hasNext()) {
				ValueTuple tuple = iterator.next();
				if (!iterator.hasNext())
					return tuple;
			}
		} finally {
			iterator.close();
		}
		throw new ExceptionSemantic("RS0274: TUPLE FROM expects a relation with cardinality equal to one.");
	}

	public Value project(final AttributeMap map) {
		return new ValueRelation(getGenerator()) {
			private final static long serialVersionUID = 0;

			public int hashCode() {
				return 0;
			}

			public TupleIterator newIterator() {
				return new TupleIteratorUnique(new TupleIterator() {
					TupleIterator tuples = ValueRelation.this.iterator();

					public boolean hasNext() {
						return tuples.hasNext();
					}

					public ValueTuple next() {
						return (ValueTuple)tuples.next().project(map);
					}
					
					public void close() {
						tuples.close();
					}
				});
			}
		};
	}

	public ValueRelation union(final ValueRelation rightRelation) {
		return new ValueRelation(getGenerator()) {
			private final static long serialVersionUID = 0;

			public int hashCode() {
				return 0;
			}

			public TupleIterator newIterator() {
				return new TupleIteratorUnique(new TupleIterator() {
					TupleIterator left = ValueRelation.this.iterator();
					TupleIterator right = rightRelation.iterator();

					public boolean hasNext() {
						return (left.hasNext() || right.hasNext());
					}

					public ValueTuple next() {
						if (left.hasNext())
							return left.next();
						else if (right.hasNext())
							return right.next();
						throw new NoSuchElementException();
					}
					
					public void close() {
						left.close();
						right.close();
					}
				});
			}
		};
	}
	
	public ValueRelation xunion(final ValueRelation rightRelation) {
		return union(rightRelation).minus(intersect(rightRelation));
	}

	public ValueRelation dunion(final ValueRelation rightRelation) {
		return new ValueRelation(getGenerator()) {
			private final static long serialVersionUID = 0;

			public int hashCode() {
				return 0;
			}

			public TupleIterator newIterator() {
				return new TupleIteratorDisjoint(new TupleIterator() {
					TupleIterator left = ValueRelation.this.iterator();
					TupleIterator right = rightRelation.iterator();

					public boolean hasNext() {
						return (left.hasNext() || right.hasNext());
					}

					public ValueTuple next() {
						if (left.hasNext())
							return left.next();
						else if (right.hasNext())
							return right.next();
						throw new NoSuchElementException();
					}
					
					public void close() {
						left.close();
						right.close();
					}
				});
			}
		};
	}

	public ValueRelation intersect(final ValueRelation rightRelation) {
		return new ValueRelation(getGenerator()) {
			private final static long serialVersionUID = 0;

			public int hashCode() {
				return 0;
			}

			public TupleIterator newIterator() {
				return new TupleIterator() {
					TupleIterator left = ValueRelation.this.iterator();
					ValueTuple current = null;

					public boolean hasNext() {
						if (current != null)
							return true;
						ValueTuple leftTuple;
						do {
							if (!left.hasNext())
								return false;
							leftTuple = left.next();
						} while (!rightRelation.contains(leftTuple));
						current = leftTuple;
						return true;
					}

					public ValueTuple next() {
						if (hasNext())
							try {
								return current;
							} finally {
								current = null;
							}
						throw new NoSuchElementException();
					}
					
					public void close() {
						left.close();
					}
				};
			}
		};
	}

	public ValueRelation minus(final ValueRelation rightRelation) {
		return new ValueRelation(getGenerator()) {
			private final static long serialVersionUID = 0;

			public int hashCode() {
				return 0;
			}

			public TupleIterator newIterator() {
				return new TupleIterator() {
					TupleIterator left = ValueRelation.this.iterator();
					ValueTuple current = null;

					public boolean hasNext() {
						if (current != null)
							return true;
						ValueTuple leftTuple;
						do {
							if (!left.hasNext())
								return false;
							leftTuple = left.next();
						} while (rightRelation.contains(leftTuple));
						current = leftTuple;
						return true;
					}

					public ValueTuple next() {
						if (hasNext())
							try {
								return current;
							} finally {
								current = null;
							}
						throw new NoSuchElementException();
					}
					
					public void close() {
						left.close();
					}
				};
			}
		};
	}

	public Value iminus(ValueRelation rightRelation) {
		if (!rightRelation.isSubsetOf(this))
			throw new ExceptionSemantic("RS0275: In I_MINUS, the right operand must be a subset of the left operand.");
		return minus(rightRelation);
	}
	
	public ValueRelation product(final ValueRelation rightRelation) {
		return new ValueRelation(getGenerator()) {
			private final static long serialVersionUID = 0;

			public int hashCode() {
				return 0;
			}

			public TupleIterator newIterator() {
				return new TupleIterator() {
					TupleIterator left = ValueRelation.this.iterator();
					TupleIterator right = null;
					ValueTuple current = null;
					ValueTuple leftTuple = null;

					public boolean hasNext() {
						if (current != null)
							return true;
						if (leftTuple != null) {
							if (!right.hasNext())
								leftTuple = null;
							else {
								current = leftTuple.joinDisjoint(right.next());
								return true;
							}
						}
						if (leftTuple == null) {
							if (!left.hasNext())
								return false;
							leftTuple = left.next();
							if (right != null)
								right.close();
							right = rightRelation.iterator();
							if (!right.hasNext())
								return false;
							current = leftTuple.joinDisjoint(right.next());
						}
						return true;
					}

					public ValueTuple next() {
						if (hasNext())
							try {
								return current;
							} finally {
								current = null;
							}
						throw new NoSuchElementException();
					}
					
					public void close() {
						left.close();
						if (right != null)
							right.close();
					}
				};
			}
		};
	}
	
	public ValueRelation join(final RelDatabase database, final JoinMap map, final ValueRelation rightRelation) {
		return new ValueRelation(getGenerator()) {
			private final static long serialVersionUID = 0;

			public int hashCode() {
				return 0;
			}
		
			public TupleIterator newIterator() {
				return new TupleIterator() {
					boolean indexed = false;
					TempIndex rightTuples = new TempIndexImplementation(database);
					TupleIterator leftIterator = ValueRelation.this.iterator();
					TupleIterator rightIterator;
					ValueTuple current = null;
					ValueTuple leftTuple = null;
					ValueTuple rightTuple = null;
					
					public boolean hasNextPair() {
						while (true) {
							if (rightIterator == null) {
								if (!leftIterator.hasNext())
									return false;
								leftTuple = leftIterator.next();
								if (indexed)
									rightIterator = rightTuples.keySearch(map.getLeftTupleCommon(getGenerator(), leftTuple));
								else
									rightIterator = rightRelation.iterator();
							}
							if (rightIterator.hasNext()) {
								rightTuple = rightIterator.next();
								if (indexed)
									return true;
								else {
									ValueTuple rightClone = (ValueTuple)rightTuple.getSerializableClone();
									rightTuples.put((ValueTuple)map.getRightTupleCommon(getGenerator(), rightClone), rightClone);
									if (map.isJoinable(leftTuple, rightTuple))
										return true;						
								}
							} else {
								indexed = true;
								rightIterator.close();
								rightIterator = null;
							}
						}
					}
					
					public boolean hasNext() {
						if (current != null)
							return true;
						if (!hasNextPair())
							return false;
						current = leftTuple.join(map, rightTuple);
						return true;
					}

					public ValueTuple next() {
						if (hasNext())
							try {
								return current;
							} finally {
								current = null;
							}
						throw new NoSuchElementException();
					}

					public void close() {
						if (rightIterator != null)
							rightIterator.close();
						leftIterator.close();
						rightTuples.close();
					}
					
				};
			}
		};
	}

	/**
	 * Apply a native tuple operator to each tuple in a given relation, in order to
	 * generate a new relation.
	 * 
	 * @param map - a TupleMap
	 * @return - a ValueRelation
	 */
	public TupleIteratable map(final TupleMap map) {
		return new ValueRelation(getGenerator()) {
			private final static long serialVersionUID = 0;

			public int hashCode() {
				return 0;
			}

			public TupleIterator newIterator() {
				return new TupleIterator() {
					TupleIterator iterator = ValueRelation.this.iterator();

					public boolean hasNext() {
						return iterator.hasNext();
					}

					public ValueTuple next() {
						return map.map(iterator.next());
					}
					
					public void close() {
						iterator.close();
					}
				};
			}
		};
	}

	/**
	 * Order by attributes in orderMap. For each group of equal orderMap attributes, 
	 * return one tuple of orderMap attributes with all groupAttributes in an appended relation-valued attribute.
	 *
	 * @param orderAttributes
	 * @param groupAttributes
	 * @return - a ValueRelation
	 */
	public Value group(AttributeMap orderAttributes, AttributeMap groupAttributes) {
		// TODO - MEM - fix so that high-cardinality relations don't run out of RAM
		final Map<ValueTuple, ValueRelationLiteral> group = new HashMap<ValueTuple, ValueRelationLiteral>();
    	(new TupleIteration(iterator()) {
    		public void process(ValueTuple tuple) {
    			ValueTuple orderTuple = (ValueTuple)tuple.project(orderAttributes);
    			ValueTuple groupAttributeTuple = (ValueTuple)tuple.project(groupAttributes);
    			ValueRelationLiteral groupAttributeValue = group.get(orderTuple);
    			if (groupAttributeValue == null) {
    				groupAttributeValue = new ValueRelationLiteral(getGenerator());
        			group.put(orderTuple, groupAttributeValue);
    			}
    			groupAttributeValue.insert(groupAttributeTuple);
    		}
    	}).run();
		return new ValueRelation(getGenerator()) {
			private final static long serialVersionUID = 0;
			
			public int hashCode() {
				return 0;
			}

			public TupleIterator newIterator() {
				return new TupleIterator() {
					Set<Entry<ValueTuple, ValueRelationLiteral>> groupedData = group.entrySet();
					Iterator<Entry<ValueTuple, ValueRelationLiteral>> iterator = groupedData.iterator();

					public boolean hasNext() {
						return iterator.hasNext();
					}

					public ValueTuple next() {
						Entry<ValueTuple, ValueRelationLiteral> entry = iterator.next();
						ValueTuple sortedTuple = entry.getKey();
						ValueRelationLiteral rva = entry.getValue();
						Value[] rvaTupleArray = {rva};
						ValueTuple rvaTuple = new ValueTuple(getGenerator(), rvaTupleArray);
						return sortedTuple.joinDisjoint(rvaTuple);
					}
					
					public void close() {
					}
				};
			}
		};    	
	}

	/**
	 * Ungroup a relation.
	 * 
	 * Note that the result includes ALL the original tuple attributes,
	 * including the specified RVA.
	 * 
	 * @param sourceMap -
	 *            mapping between original tuples and new tuples
	 * @param rvaMap -
	 *            mapping between original tuples' RVA and new tuples
	 * @param rvaIndex -
	 *            index of RVA in original tuple
	 * @param source -
	 *            ValueRelation containing at least one RVA, to which rvaIndex
	 *            points
	 * @return - ValueRelation
	 */
	public ValueRelation ungroup(final int resultDegree, final AttributeMap sourceMap, final AttributeMap rvaMap, final int rvaIndex) {
		return new ValueRelation(getGenerator()) {
			private final static long serialVersionUID = 0;

			public int hashCode() {
				return 0;
			}

			public TupleIterator newIterator() {
				return new TupleIterator() {
					ValueTuple current = null;
					TupleIterator outerIterator = ValueRelation.this.iterator();
					TupleIterator innerIterator = null;
					ValueTuple outerTuple;
					boolean done = false;

					public boolean hasNext() {
						if (done)
							return false;
						if (current != null)
							return true;
						while (innerIterator == null || !innerIterator.hasNext()) {
							if (!outerIterator.hasNext()) {
								done = true;
								return false;
							}
							outerTuple = outerIterator.next();
							if (innerIterator != null)
								innerIterator.close();
							innerIterator = ((ValueRelation)outerTuple.getValues()[rvaIndex]).iterator();
							if (innerIterator.hasNext())
								break;
							else
								continue;
						}
						Value[] buffer = new Value[resultDegree];
						current = new ValueTuple(getGenerator(), buffer);
						current.assign(rvaMap, innerIterator.next());
						current.assign(sourceMap, outerTuple);
						return true;
					}

					public ValueTuple next() {
						if (hasNext())
							try {
								return current;
							} finally {
								current = null;
							}
						throw new NoSuchElementException();
					}
					
					public void close() {
						outerIterator.close();
						if (innerIterator != null)
							innerIterator.close();
					}
				};
			}
		};
	}

	/**
	 * Apply a native boolean operator to each tuple in a given relation, in order to
	 * generate a new relation.
	 * 
	 * Tuples only appear in result if the operator returns true.
	 */
	public ValueRelation select(final TupleFilter filter) {
		return new ValueRelation(getGenerator()) {
			private final static long serialVersionUID = 0;

			public int hashCode() {
				return 0;
			}

			public TupleIterator newIterator() {
				return new TupleIterator() {
					TupleIterator iterator = ValueRelation.this.iterator();
					ValueTuple current = null;

					public boolean hasNext() {
						if (current != null)
							return true;
						boolean testResult;
						ValueTuple next;
						do {
							if (!iterator.hasNext())
								return false;
							next = iterator.next();
							testResult = filter.filter(next);
						} while (!testResult);
						current = next;
						return true;
					}

					public ValueTuple next() {
						if (hasNext())
							try {
								return current;
							} finally {
								current = null;
							}
						throw new NoSuchElementException();
					}
					
					public void close() {
						iterator.close();
					}
				};
			}
		};
	}

	public static Value sequence(Generator generator, ValueInteger start, ValueInteger end, ValueInteger step) {
		return new ValueRelation(generator) {
			private static final long serialVersionUID = 1L;

			@Override
			public TupleIterator newIterator() {
				return new TupleIterator() {
					long currentValue = start.longValue();
					ValueTuple current = getCurrentValue();

					private ValueTuple getCurrentValue() {
						return new ValueTuple(generator, new Value[] {ValueInteger.select(generator, currentValue)});
					}
					
					public boolean hasNext() {
						if (current != null)
							return true;
						currentValue += step.longValue();
						if ((step.longValue() > 0) ? currentValue > end.longValue() : currentValue < end.longValue())
							return false;
						current = getCurrentValue();
						return true;
					}

					public ValueTuple next() {
						if (hasNext())
							try {
								return current;
							} finally {
								current = null;
							}
						throw new NoSuchElementException();
					}
					
					public void close() {
					}
				};
			}

			@Override
			public int hashCode() {
				return 0;
			}
			
		};
	}
	
	public static Value sequence(Generator generator, ValueInteger start, ValueInteger end) {
		return sequence(generator, start, end, ValueInteger.select(generator, 1));
	}
	
	/** Return a new, possibly-sorted ValueArray */
	public ValueArray sort(OrderMap map) {
		if (map.getMap().length == 0)
			return new ValueArray(getGenerator(), this);
		else {
			// TODO - MEM - fix so that high-cardinality relations don't run out of RAM
			final ArrayList<ValueTuple> array = new ArrayList<ValueTuple>();
	    	(new TupleIteration(iterator()) {
	    		public void process(ValueTuple tuple) {
	    			array.add(tuple);		
	    		}
	    	}).run();
			Collections.sort(array, new Sorter(map));
			return new ValueArray(getGenerator(), array);
		}
	}
	
	public abstract TupleIterator newIterator();
	
	public abstract int hashCode();
	
	public final TupleIterator iterator() {
		return newIterator();
	}
	
	// TODO - MEM - replace this with something that won't out-of-memory on high-cardinality relations
	private HashSet<ValueTuple> cache = null;
		
	private void buildCache() {
		cache = new HashSet<ValueTuple>();
		(new TupleIteration(iterator()) {
			public void process(ValueTuple tuple) {
				cache.add(tuple);
			}
		}).run();
	}

	public boolean contains(ValueTuple findMe) {
		if (cache == null)
			buildCache();
		return cache.contains(findMe);
	}

	public String getTypeName() {
		return "RELATION";
	}

	/** Output this Value to a PrintStream. */
	public void toStream(Context context, Type type, PrintStream p, int depth) {
		Heading heading = ((TypeRelation)type).getHeading();
		TypeTuple tupleType = new TypeTuple(heading);
		p.print("RELATION" + " " + heading + " {");
		long count = 0;
		TupleIterator iterator = iterator();
		try {
			while (iterator.hasNext()) {
				ValueTuple tuple = iterator.next();
				if (count++ > 0)
					p.print(',');
				p.print("\n\t");
				tuple.toStream(context, tupleType, p, depth + 1);
			}
		} finally {
			iterator.close();
		}
		p.print("\n}");
	}

	// For debugging purposes
	public String toString() {
		String s = "";
		s += "RELATION {";
		long count = 0;
		TupleIterator iterator = iterator();
		try {
			while (iterator.hasNext()) {
				ValueTuple tuple = iterator.next();
				if (count++ > 0)
					s += ", ";
				s += tuple.toString();
			}
		} finally {
			iterator.close();
		}
		s += "}";
		return s;
	}
	
	public long getCardinality() {
		if (cache == null)
			buildCache();
		return cache.size();
	}
	
	private final boolean isTestedSubsetOf(ValueRelation rightSide) {
		TupleIterator iterator = iterator();
		try {
			while (iterator.hasNext()) {
				ValueTuple tuple = iterator.next();
				if (!rightSide.contains(tuple))
					return false;
			}
		} finally {
			iterator.close();
		}
		return true;
	}

	/** Test for <=, i.e., left is subset of right */
	public boolean isSubsetOf(ValueRelation rightSide) {
		if (getCardinality() > rightSide.getCardinality()) {
			return false;
		} else {
			return isTestedSubsetOf(rightSide);
		}
	}

	/** Test for >=, i.e., left is superset of right */
	public boolean isSupersetOf(ValueRelation v) {
		return v.isSubsetOf(this);
	}

	/** Test for >, i.e. left is proper superset of right */
	public boolean isProperSupersetOf(ValueRelation v) {
		return v.isProperSubsetOf(this);
	}

	/** Test for <, i.e. left is proper subset of right */
	public boolean isProperSubsetOf(ValueRelation v) {
		return isSubsetOf(v) && neq(v);
	}

	public int compareTo(Value v) {
		ValueRelation rightSide = ((ValueRelation)v);
		long cardinalityLeft = getCardinality();
		long cardinalityRight = rightSide.getCardinality();
		if (cardinalityLeft == cardinalityRight)
			return isTestedSubsetOf(rightSide) ? 0 : 1;
		else
			return 1;
	}
	
	/** Test this relation and another for equality. */
	public boolean eq(ValueRelation rightSide) {
		if (getCardinality() == rightSide.getCardinality())
			return isTestedSubsetOf(rightSide);
		else
			return false;
	}

	/** Test this relation and another for non-equality. */
	public boolean neq(ValueRelation v) {
		return !eq(v);
	}

	private static ValueRelation tclose(Generator generator, JoinMap joinMap, AttributeMap projectMap, ValueRelation xy) {
		ValueRelation ttt = xy.union((ValueRelation)xy.join(generator.getDatabase(), joinMap, xy).project(projectMap));
		return (ttt.eq(xy)) ? ttt : tclose(generator, joinMap, projectMap, ttt);
	}
	
	public Value tclose() {
		Type attributeType = TypeInteger.getInstance(); // type is irrelevant here; any will do
		Heading left = new Heading();
		left.add("X", attributeType);
		left.add("LINK", attributeType);	
		Heading right = new Heading();
		right.add("LINK", attributeType);
		right.add("Y", attributeType);
		Heading joinTarget = new Heading();
		joinTarget.add("X", attributeType);
		joinTarget.add("LINK", attributeType);
		joinTarget.add("Y", attributeType);
		Heading projectTarget = new Heading();
		projectTarget.add("X", attributeType);
		projectTarget.add("Y", attributeType);
		JoinMap joinMap = new JoinMap(joinTarget, left, right);
		AttributeMap projectMap = new AttributeMap(projectTarget, joinTarget);
		return tclose(getGenerator(), joinMap, projectMap, this);
	}

	public ValueBoolean is_empty() {
		TupleIterator iterator = iterator();
		try {
			return (ValueBoolean)ValueBoolean.select(getGenerator(), !iterator.hasNext());
		} finally {
			iterator.close();
		}
	}
	
	/** Aggregate operator */
	public ValueBoolean exactly(long nCount, int attributeIndex) {
		long trueCount = 0;
		TupleIterator iterator = iterator();
		try {
			while (iterator.hasNext()) {
				ValueTuple t = iterator.next();
				trueCount += ((((ValueBoolean)t.getValues()[attributeIndex]).booleanValue()) ? 1 : 0);
			}
		} finally {
			iterator.close();
		}
		return (ValueBoolean)ValueBoolean.select(getGenerator(), trueCount == nCount);
	}
	
}
