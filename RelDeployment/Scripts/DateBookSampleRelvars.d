// Sample relvars from "An Introduction to Database Systems", C. J. Date, 8th ed., 2004

TYPE S# POSSREP {SNUM CHAR};
TYPE NAME POSSREP {NAME CHAR};
TYPE P# POSSREP {PNUM CHAR};
TYPE COLOR POSSREP {COLOUR CHAR};
TYPE WEIGHT POSSREP {WEIGHT RATIONAL};
TYPE QTY POSSREP {QTY INTEGER};

VAR S REAL RELATION {S# S#, SNAME NAME, STATUS INTEGER, CITY CHAR} KEY {S#};

S := RELATION {
	TUPLE {S# S#("S1"), SNAME NAME("Smith"), STATUS 20, CITY "London"},
	TUPLE {S# S#("S2"), SNAME NAME("Jones"), STATUS 10, CITY "Paris"},
	TUPLE {S# S#("S3"), SNAME NAME("Blake"), STATUS 30, CITY "Paris"},
	TUPLE {S# S#("S4"), SNAME NAME("Clark"), STATUS 20, CITY "London"},
	TUPLE {S# S#("S5"), SNAME NAME("Adams"), STATUS 30, CITY "Athens"}
};

VAR P REAL RELATION {P# P#, PNAME NAME, COLOR COLOR, WEIGHT WEIGHT, CITY CHAR} KEY {P#};

P := RELATION {
	TUPLE {P# P#("P1"), PNAME NAME("Nut"), COLOR COLOR("Red"), WEIGHT WEIGHT(12.0), CITY "London"},
	TUPLE {P# P#("P2"), PNAME NAME("Bolt"), COLOR COLOR("Green"), WEIGHT WEIGHT(17.0), CITY "Paris"},
	TUPLE {P# P#("P3"), PNAME NAME("Screw"), COLOR COLOR("Blue"), WEIGHT WEIGHT(17.0), CITY "Oslo"},
	TUPLE {P# P#("P4"), PNAME NAME("Screw"), COLOR COLOR("Red"), WEIGHT WEIGHT(14.0), CITY "London"},
	TUPLE {P# P#("P5"), PNAME NAME("Cam"), COLOR COLOR("Blue"), WEIGHT WEIGHT(12.0), CITY "Paris"},
	TUPLE {P# P#("P6"), PNAME NAME("Cog"), COLOR COLOR("Red"), WEIGHT WEIGHT(19.0), CITY "London"}
};

VAR SP REAL RELATION {S# S#, P# P#, QTY QTY} KEY {S#, P#};

SP := RELATION {
	TUPLE {S# S#("S1"), P# P#("P1"), QTY QTY(300)},
	TUPLE {S# S#("S1"), P# P#("P2"), QTY QTY(200)},
	TUPLE {S# S#("S1"), P# P#("P3"), QTY QTY(400)},
	TUPLE {S# S#("S1"), P# P#("P4"), QTY QTY(200)},
	TUPLE {S# S#("S1"), P# P#("P5"), QTY QTY(100)},
	TUPLE {S# S#("S1"), P# P#("P6"), QTY QTY(100)},
	TUPLE {S# S#("S2"), P# P#("P1"), QTY QTY(300)},
	TUPLE {S# S#("S2"), P# P#("P2"), QTY QTY(400)},
	TUPLE {S# S#("S3"), P# P#("P2"), QTY QTY(200)},
	TUPLE {S# S#("S4"), P# P#("P2"), QTY QTY(200)},
	TUPLE {S# S#("S4"), P# P#("P4"), QTY QTY(300)},
	TUPLE {S# S#("S4"), P# P#("P5"), QTY QTY(400)}
};

CONSTRAINT SP_FKEY1 SP {S#} <= S {S#};
CONSTRAINT SP_FKEY2 SP {P#} <= P {P#};

TYPE J# POSSREP {JNUM CHAR};

VAR J REAL RELATION {J# J#, JNAME NAME, CITY CHAR} KEY {J#};

J := RELATION {
	TUPLE {J# J#("J1"), JNAME NAME("Sorter"), CITY "Paris"},
	TUPLE {J# J#("J2"), JNAME NAME("Display"), CITY "Rome"},
	TUPLE {J# J#("J3"), JNAME NAME("OCR"), CITY "Athens"},
	TUPLE {J# J#("J4"), JNAME NAME("Console"), CITY "Athens"},
	TUPLE {J# J#("J5"), JNAME NAME("RAID"), CITY "London"},
	TUPLE {J# J#("J6"), JNAME NAME("EDS"), CITY "Oslo"},
	TUPLE {J# J#("J7"), JNAME NAME("Tape"), CITY "London"}
};

VAR SPJ REAL RELATION {S# S#, P# P#, J# J#, QTY QTY} KEY {S#, P#, J#};

CONSTRAINT SPJ_FKEY1 SPJ {S#} <= S {S#};
CONSTRAINT SPJ_FKEY2 SPJ {P#} <= P {P#};
CONSTRAINT SPJ_FKEY3 SPJ {J#} <= J {J#};

SPJ := RELATION {
	TUPLE {S# S#("S1"), P# P#("P1"), J# J#("J1"), QTY QTY(200)},
	TUPLE {S# S#("S1"), P# P#("P1"), J# J#("J4"), QTY QTY(700)},
	TUPLE {S# S#("S2"), P# P#("P3"), J# J#("J1"), QTY QTY(400)},
	TUPLE {S# S#("S2"), P# P#("P3"), J# J#("J2"), QTY QTY(200)},
	TUPLE {S# S#("S2"), P# P#("P3"), J# J#("J3"), QTY QTY(200)},
	TUPLE {S# S#("S2"), P# P#("P3"), J# J#("J4"), QTY QTY(500)},
	TUPLE {S# S#("S2"), P# P#("P3"), J# J#("J5"), QTY QTY(600)},
	TUPLE {S# S#("S2"), P# P#("P3"), J# J#("J6"), QTY QTY(400)},
	TUPLE {S# S#("S2"), P# P#("P3"), J# J#("J7"), QTY QTY(800)},
	TUPLE {S# S#("S2"), P# P#("P5"), J# J#("J2"), QTY QTY(100)},
	TUPLE {S# S#("S3"), P# P#("P3"), J# J#("J1"), QTY QTY(200)},
	TUPLE {S# S#("S3"), P# P#("P4"), J# J#("J2"), QTY QTY(500)},
	TUPLE {S# S#("S4"), P# P#("P6"), J# J#("J3"), QTY QTY(300)},
	TUPLE {S# S#("S4"), P# P#("P6"), J# J#("J7"), QTY QTY(300)},
	TUPLE {S# S#("S5"), P# P#("P2"), J# J#("J2"), QTY QTY(200)},
	TUPLE {S# S#("S5"), P# P#("P2"), J# J#("J4"), QTY QTY(100)},
	TUPLE {S# S#("S5"), P# P#("P5"), J# J#("J5"), QTY QTY(500)},
	TUPLE {S# S#("S5"), P# P#("P5"), J# J#("J7"), QTY QTY(100)},
	TUPLE {S# S#("S5"), P# P#("P6"), J# J#("J2"), QTY QTY(200)},
	TUPLE {S# S#("S5"), P# P#("P1"), J# J#("J4"), QTY QTY(100)},
	TUPLE {S# S#("S5"), P# P#("P3"), J# J#("J4"), QTY QTY(200)},
	TUPLE {S# S#("S5"), P# P#("P4"), J# J#("J4"), QTY QTY(800)},
	TUPLE {S# S#("S5"), P# P#("P5"), J# J#("J4"), QTY QTY(400)},
	TUPLE {S# S#("S5"), P# P#("P6"), J# J#("J4"), QTY QTY(500)}
};
