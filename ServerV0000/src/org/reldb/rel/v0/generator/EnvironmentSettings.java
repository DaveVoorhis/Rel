package org.reldb.rel.v0.generator;

import java.util.TreeMap;

import org.reldb.rel.exceptions.ExceptionSemantic;

public class EnvironmentSettings {
	
	private EnvironmentSettings() {
		Attribute verboseExternalCompilation = new Attribute("VerboseExternalCompilation");
		new Value(verboseExternalCompilation, "On", "Verbose compilation of external operators and types.") {
			void setter(Generator generator) {
				generator.setVerboseExternalCompilation(true);
			}
		};
		new Value(verboseExternalCompilation, "Off", "Silent compilation of external operators and types. (Default)") {
			void setter(Generator generator) {
				generator.setVerboseExternalCompilation(false);
			}			
		};
	}
	
	private TreeMap<String, Attribute> attributes = new TreeMap<String, Attribute>();

	private class Attribute {
		
		private String name;
		private TreeMap<String, Value> values = new TreeMap<String, Value>();
		
		Attribute(String name) {
			this.name = name;
			attributes.put(name, this);
		}
		
		void add(Value value) {
			values.put(value.getValue(), value);
		}
		
		String getName() {
			return name;
		}
		
		Value findValue(String valueName) {
			return values.get(valueName);
		}
		
		String getUsage() {
			String out = "";
			for (Value value: values.values())
				out += "\n" + getName() + " " + value.getValue() + ": " + value.getDescription();
			return out;
		}
		
	}
	
	private static abstract class Value {
		
		private String value;
		private String description;
		
		Value(Attribute attribute, String value, String description) {
			this.value = value;
			this.description = description;
			attribute.add(this);
		}
		
		String getValue() {
			return value;
		}
		
		String getDescription() {
			return description;
		}
				
		abstract void setter(Generator generator);
	}
	
	private String getUsage() {
		String out = "";
		for (Attribute attribute: attributes.values())
			out += attribute.getUsage();
		return "\n" + out;
	}
	
	private Attribute findAttribute(String attributeName) {
		return attributes.get(attributeName);
	}
	
	private static EnvironmentSettings environmentSetter = null;
	
	public static void set(Generator generator, String attribute, String value) {
		if (environmentSetter == null)
			environmentSetter = new EnvironmentSettings();
		Attribute a = environmentSetter.findAttribute(attribute);
		if (a == null)
			throw new ExceptionSemantic("RS0016: Unknown attribute '" + attribute + "'.  Available attributes are: " + environmentSetter.getUsage());
		Value v = a.findValue(value);
		if (v == null)
			throw new ExceptionSemantic("RS0017: Unknown value '" + value + "'.  Available values are: " + a.getUsage());
		v.setter(generator);
	}

}
