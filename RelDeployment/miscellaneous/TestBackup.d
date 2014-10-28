/*** Rel Database Backup ***/

// Created in Rel Version 1.0.9 Beta
// Using DatabaseToScript version 0.3.3

BEGIN TRANSACTION;

ANNOUNCE 'var SP';
VAR SP REAL RELATION {DR INTEGER, SPs INTEGER} KEY {DR};
SP := RELATION {
	TUPLE {DR 0, SPs 0},
	TUPLE {DR 1, SPs 0},
	TUPLE {DR 2, SPs 0},
	TUPLE {DR 3, SPs 0},
	TUPLE {DR 4, SPs 0},
	TUPLE {DR 5, SPs 0},
	TUPLE {DR 6, SPs 0},
	TUPLE {DR 7, SPs 1},
	TUPLE {DR 8, SPs 2}
}
;

ANNOUNCE 'var Problemist';
VAR Problemist REAL RELATION {FirstName CHARACTER, LastName CHARACTER, MPs INTEGER, SPs INTEGER} KEY {FirstName, LastName};
Problemist := RELATION {
	TUPLE {FirstName "", LastName "Llhuii5614", MPs 12, SPs 1},
	TUPLE {FirstName "", LastName "Mike", MPs 2, SPs 0},
	TUPLE {FirstName "", LastName "Satyanarayana", MPs 3, SPs 0},
	TUPLE {FirstName "A", LastName "Colpitts", MPs 27, SPs 2},
	TUPLE {FirstName "A", LastName "Franczak", MPs 537, SPs 28},
	TUPLE {FirstName "A", LastName "Rajani", MPs 2, SPs 0},
	TUPLE {FirstName "A", LastName "Rajpold", MPs 15, SPs 0},
	TUPLE {FirstName "A", LastName "Roberts", MPs 10, SPs 0},
	TUPLE {FirstName "A", LastName "Sanitt", MPs 2, SPs 0},
	TUPLE {FirstName "A C", LastName "Enthoven", MPs 298, SPs 12},
	TUPLE {FirstName "A D", LastName "Horn", MPs 26, SPs 0},
	TUPLE {FirstName "A F", LastName "Olech", MPs 39, SPs 2},
	TUPLE {FirstName "A L", LastName "Edwards", MPs 22, SPs 0},
	TUPLE {FirstName "A L", LastName "Morris", MPs 144, SPs 4},
	TUPLE {FirstName "A M", LastName "Calmonson", MPs 32, SPs 4},
	TUPLE {FirstName "A M", LastName "Seaver", MPs 3, SPs 0},
	TUPLE {FirstName "A.", LastName "Desmedt", MPs 6, SPs 2},
	TUPLE {FirstName "A.B.", LastName "Harris", MPs 230, SPs 20},
	TUPLE {FirstName "A.C.", LastName "Currie", MPs 251, SPs 6},
	TUPLE {FirstName "A.C.", LastName "Eastwood", MPs 7, SPs 0},
	TUPLE {FirstName "A.F.", LastName "Jaffrelot", MPs 895, SPs 35},
	TUPLE {FirstName "A.M.", LastName "Poole", MPs 119, SPs 4},
	TUPLE {FirstName "A.N.", LastName "Forbes", MPs 13, SPs 0},
	TUPLE {FirstName "Abby", LastName "Chiu", MPs 5, SPs 0},
	TUPLE {FirstName "Adrian", LastName "Smith", MPs 14, SPs 0},
	TUPLE {FirstName "Ahmet Rifat", LastName "Sahin", MPs 8, SPs 2},
	TUPLE {FirstName "Amar Naih", LastName "Poddar", MPs 7, SPs 1},
	TUPLE {FirstName "Anastas", LastName "Anastasov", MPs 84, SPs 4},
	TUPLE {FirstName "Andrew", LastName "Mountain", MPs 0, SPs 0},
	TUPLE {FirstName "Andrew", LastName "Prothero", MPs 508, SPs 39},
	TUPLE {FirstName "Andrew", LastName "Rollo", MPs 6, SPs 0},
	TUPLE {FirstName "Andrew", LastName "Scott", MPs 20, SPs 2},
	TUPLE {FirstName "Anil", LastName "Upadhyay", MPs 7, SPs 1},
	TUPLE {FirstName "Anna", LastName "Hogers", MPs 2, SPs 0},
	TUPLE {FirstName "Anne", LastName "Tovee", MPs 2, SPs 0},
	TUPLE {FirstName "Ayhan", LastName "Kök", MPs 0, SPs 0},
	TUPLE {FirstName "B", LastName "Jankovic", MPs 24, SPs 1},
	TUPLE {FirstName "B", LastName "O\'Donnell", MPs 18, SPs 1},
	TUPLE {FirstName "B", LastName "Rigal", MPs 2, SPs 0},
	TUPLE {FirstName "B G", LastName "Finestein", MPs 5, SPs 0},
	TUPLE {FirstName "B.", LastName "Williams", MPs 3, SPs 0},
	TUPLE {FirstName "Bala", LastName "Maniam", MPs 278, SPs 9},
	TUPLE {FirstName "Barbara", LastName "Janes", MPs 837, SPs 27},
	TUPLE {FirstName "Bill", LastName "Barclay", MPs 66, SPs 2},
	TUPLE {FirstName "Bircan", LastName "Ozturk", MPs 45, SPs 3},
	TUPLE {FirstName "Birol", LastName "Guvenc", MPs 11, SPs 0},
	TUPLE {FirstName "Bjorn", LastName "Furu", MPs 4, SPs 0},
	TUPLE {FirstName "Bob", LastName "Bignall", MPs 25, SPs 1},
	TUPLE {FirstName "Bob", LastName "Brown", MPs 7, SPs 0},
	TUPLE {FirstName "Bob", LastName "Stockbridge", MPs 11, SPs 0},
	TUPLE {FirstName "Bob", LastName "Watson", MPs 4, SPs 0},
	TUPLE {FirstName "Bomi", LastName "Kavarana", MPs 51, SPs 2},
	TUPLE {FirstName "Brian", LastName "Humphreys", MPs 0, SPs 0},
	TUPLE {FirstName "Bruce", LastName "Perry", MPs 4, SPs 0},
	TUPLE {FirstName "Bryan", LastName "Delfs", MPs 3, SPs 0},
	TUPLE {FirstName "Bu", LastName "Feiming", MPs 32, SPs 1},
	TUPLE {FirstName "C", LastName "Crocker", MPs 45, SPs 0},
	TUPLE {FirstName "C", LastName "Rich", MPs 3, SPs 0},
	TUPLE {FirstName "C C", LastName "Jones", MPs 15, SPs 0},
	TUPLE {FirstName "C C", LastName "Thame", MPs 8, SPs 0},
	TUPLE {FirstName "C E", LastName "Phillips", MPs 15, SPs 0},
	TUPLE {FirstName "C.", LastName "Adam", MPs 13, SPs 0},
	TUPLE {FirstName "C.", LastName "Edam", MPs 30, SPs 0},
	TUPLE {FirstName "C.", LastName "Fischer", MPs 3, SPs 0},
	TUPLE {FirstName "C.D.", LastName "Frehr", MPs 2, SPs 0},
	TUPLE {FirstName "Carolus", LastName "Hooijman", MPs 3, SPs 0},
	TUPLE {FirstName "Ch.P.", LastName "Maris", MPs 25, SPs 0},
	TUPLE {FirstName "Chaokai", LastName "Cheng", MPs 0, SPs 0},
	TUPLE {FirstName "Charles", LastName "Taylor", MPs 12, SPs 0},
	TUPLE {FirstName "Cheung", LastName "Simon", MPs 5, SPs 0},
	TUPLE {FirstName "Christina", LastName "Syrakopoulou", MPs 4, SPs 0},
	TUPLE {FirstName "Christopher", LastName "Hext", MPs 2, SPs 0},
	TUPLE {FirstName "Christopher", LastName "Whelan", MPs 2, SPs 0},
	TUPLE {FirstName "Clare", LastName "Symes", MPs 2, SPs 0},
	TUPLE {FirstName "Clint", LastName "Fyke", MPs 17, SPs 0},
	TUPLE {FirstName "Clive", LastName "Hutchinson", MPs 2, SPs 0},
	TUPLE {FirstName "Con", LastName "Vention", MPs 0, SPs 0},
	TUPLE {FirstName "D", LastName "Biswas", MPs 2, SPs 0},
	TUPLE {FirstName "D", LastName "Castell", MPs 2, SPs 0},
	TUPLE {FirstName "D", LastName "Jolly", MPs 11, SPs 0},
	TUPLE {FirstName "D", LastName "Kleinman", MPs 3, SPs 0},
	TUPLE {FirstName "D", LastName "Nicholson", MPs 1, SPs 0},
	TUPLE {FirstName "D", LastName "Norris", MPs 2, SPs 0},
	TUPLE {FirstName "D", LastName "Whiteside", MPs 2, SPs 0},
	TUPLE {FirstName "D", LastName "Yu", MPs 15, SPs 0},
	TUPLE {FirstName "D A", LastName "Eddy", MPs 196, SPs 19},
	TUPLE {FirstName "D A", LastName "Edmunds", MPs 6, SPs 0},
	TUPLE {FirstName "D A", LastName "Percival", MPs 109, SPs 0},
	TUPLE {FirstName "D B", LastName "Thorburn", MPs 28, SPs 1},
	TUPLE {FirstName "D H", LastName "Apperly", MPs 30, SPs 0},
	TUPLE {FirstName "D H C", LastName "Hampshire", MPs 0, SPs 0},
	TUPLE {FirstName "D I", LastName "Tomlinson", MPs 27, SPs 0},
	TUPLE {FirstName "D N", LastName "Tyler", MPs 9, SPs 0},
	TUPLE {FirstName "D T C", LastName "Hudson", MPs 6, SPs 0},
	TUPLE {FirstName "D.", LastName "Doub", MPs 125, SPs 5},
	TUPLE {FirstName "D.", LastName "Humphries", MPs 2, SPs 0},
	TUPLE {FirstName "Damien", LastName "Lescot", MPs 47, SPs 6},
	TUPLE {FirstName "Daniel de Lind van", LastName "Wijngaarden", MPs 126, SPs 3},
	TUPLE {FirstName "Danny", LastName "Roth", MPs 8, SPs 0},
	TUPLE {FirstName "Dave", LastName "Williams", MPs 2, SPs 0},
	TUPLE {FirstName "David", LastName "Ellis", MPs 13, SPs 0},
	TUPLE {FirstName "David", LastName "Sheerin", MPs 2, SPs 0},
	TUPLE {FirstName "David", LastName "Wiseman", MPs 27, SPs 1},
	TUPLE {FirstName "Derek", LastName "Lohmann", MPs 8, SPs 0},
	TUPLE {FirstName "Derek", LastName "Lu", MPs 5, SPs 0},
	TUPLE {FirstName "Dick", LastName "Yuen", MPs 92, SPs 0},
	TUPLE {FirstName "Don", LastName "Smedley", MPs 1253, SPs 90},
	TUPLE {FirstName "Dorothy", LastName "Spittal", MPs 19, SPs 0},
	TUPLE {FirstName "Dr B P", LastName "Srivatsan", MPs 3, SPs 0},
	TUPLE {FirstName "Dr D.J.", LastName "Levy", MPs 498, SPs 16},
	TUPLE {FirstName "Dr E H", LastName "Mansfield", MPs 48, SPs 4},
	TUPLE {FirstName "Dr J.W.", LastName "Liebeschuetz", MPs 76, SPs 2},
	TUPLE {FirstName "Dr K F", LastName "Loughlin", MPs 24, SPs 0},
	TUPLE {FirstName "Dr M.E.", LastName "Weber", MPs 292, SPs 22},
	TUPLE {FirstName "Dr P", LastName "Hawkes", MPs 2, SPs 0},
	TUPLE {FirstName "Dr S F A", LastName "Rafique", MPs 24, SPs 0},
	TUPLE {FirstName "Dr. I.S.", LastName "Hamilton", MPs 199, SPs 6},
	TUPLE {FirstName "Dr. V.S.R.", LastName "Murti", MPs 2, SPs 0},
	TUPLE {FirstName "E", LastName "Evans-Jones", MPs 8, SPs 0},
	TUPLE {FirstName "E", LastName "Garre", MPs 17, SPs 0},
	TUPLE {FirstName "E", LastName "Moorehead", MPs 2, SPs 0},
	TUPLE {FirstName "E", LastName "O\'Neill", MPs 19, SPs 1},
	TUPLE {FirstName "E", LastName "Theiler", MPs 140, SPs 3},
	TUPLE {FirstName "E C", LastName "Cole", MPs 54, SPs 0},
	TUPLE {FirstName "E D", LastName "Gordon", MPs 4, SPs 0},
	TUPLE {FirstName "E J de M", LastName "Rudolf", MPs 91, SPs 1},
	TUPLE {FirstName "E J van", LastName "Walen", MPs 3, SPs 0},
	TUPLE {FirstName "E.", LastName "Pichler", MPs 38, SPs 1},
	TUPLE {FirstName "Edward", LastName "Pyke", MPs 178, SPs 2},
	TUPLE {FirstName "Ellen", LastName "Cherniavsky", MPs 183, SPs 5},
	TUPLE {FirstName "Elsie", LastName "Taplay", MPs 2, SPs 0},
	TUPLE {FirstName "Er. R.V.S.", LastName "Chauhan", MPs 136, SPs 12},
	TUPLE {FirstName "Eric", LastName "Zhang", MPs 5, SPs 0},
	TUPLE {FirstName "Erke", LastName "Suicmez", MPs 11, SPs 0},
	TUPLE {FirstName "Ernest", LastName "Samuel", MPs 24, SPs 0},
	TUPLE {FirstName "Eugenius", LastName "Paprotny", MPs 448, SPs 24},
	TUPLE {FirstName "F", LastName "Demirkol", MPs 12, SPs 0},
	TUPLE {FirstName "F", LastName "Naudet", MPs 16, SPs 0},
	TUPLE {FirstName "F.B.", LastName "Caine", MPs 5, SPs 0},
	TUPLE {FirstName "F.Y.", LastName "Sing", MPs 6, SPs 0},
	TUPLE {FirstName "Frank", LastName "Bayden", MPs 18, SPs 0},
	TUPLE {FirstName "Fred", LastName "Kreek", MPs 3, SPs 0},
	TUPLE {FirstName "G", LastName "Angus", MPs 242, SPs 6},
	TUPLE {FirstName "G", LastName "Chadha", MPs 1, SPs 0},
	TUPLE {FirstName "G", LastName "Jensen", MPs 73, SPs 0},
	TUPLE {FirstName "G", LastName "Osborne", MPs 41, SPs 0},
	TUPLE {FirstName "G", LastName "Sutcliffe", MPs 8, SPs 0},
	TUPLE {FirstName "G E", LastName "Roscoe", MPs 2, SPs 0},
	TUPLE {FirstName "G M", LastName "Anderson", MPs 12, SPs 0},
	TUPLE {FirstName "G S", LastName "Green", MPs 4, SPs 0},
	TUPLE {FirstName "G S", LastName "Parbury", MPs 12, SPs 0},
	TUPLE {FirstName "G.A.", LastName "Stevens", MPs 2, SPs 0},
	TUPLE {FirstName "Gabins", LastName "Pathcom", MPs 3, SPs 0},
	TUPLE {FirstName "Gary", LastName "Stevens", MPs 5, SPs 0},
	TUPLE {FirstName "Gautam", LastName "Nandi", MPs 2, SPs 0},
	TUPLE {FirstName "Geoff", LastName "Dennett", MPs 81, SPs 5},
	TUPLE {FirstName "George", LastName "Wagner", MPs 10, SPs 1},
	TUPLE {FirstName "H", LastName "Emen", MPs 4, SPs 0},
	TUPLE {FirstName "H", LastName "Francis", MPs 20, SPs 0},
	TUPLE {FirstName "H", LastName "Mahoney", MPs 13, SPs 0},
	TUPLE {FirstName "H", LastName "Sarisaban", MPs 34, SPs 0},
	TUPLE {FirstName "H C", LastName "Easton", MPs 53, SPs 4},
	TUPLE {FirstName "H van der", LastName "Heijde", MPs 3, SPs 0},
	TUPLE {FirstName "H.", LastName "Vermeulen", MPs 111, SPs 4},
	TUPLE {FirstName "Helge", LastName "Leonhardsen", MPs 285, SPs 11},
	TUPLE {FirstName "Hess", LastName "Cheng", MPs 4, SPs 0},
	TUPLE {FirstName "Hugh", LastName "Darwen", MPs 0, SPs 0},
	TUPLE {FirstName "I", LastName "Sime", MPs 2, SPs 0},
	TUPLE {FirstName "I", LastName "Tanioka", MPs 3, SPs 0},
	TUPLE {FirstName "I A", LastName "Howie", MPs 6, SPs 0},
	TUPLE {FirstName "I H", LastName "Pagan", MPs 91, SPs 1},
	TUPLE {FirstName "I M", LastName "Draper", MPs 2, SPs 0},
	TUPLE {FirstName "Ian", LastName "Budden", MPs 748, SPs 37},
	TUPLE {FirstName "Ian", LastName "McGarrett", MPs 0, SPs 0},
	TUPLE {FirstName "Igor", LastName "Vorozheykin", MPs 6, SPs 0},
	TUPLE {FirstName "J", LastName "Bennet", MPs 232, SPs 22},
	TUPLE {FirstName "J", LastName "Hughes", MPs 2, SPs 0},
	TUPLE {FirstName "J", LastName "Probst", MPs 6, SPs 0},
	TUPLE {FirstName "J C", LastName "Ramsey", MPs 23, SPs 0},
	TUPLE {FirstName "J D R", LastName "Collings", MPs 5, SPs 0},
	TUPLE {FirstName "J G", LastName "Harrison", MPs 198, SPs 10},
	TUPLE {FirstName "J G", LastName "Morgan", MPs 69, SPs 0},
	TUPLE {FirstName "J H", LastName "Doyle", MPs 72, SPs 0},
	TUPLE {FirstName "J H", LastName "Gardiner", MPs 13, SPs 0},
	TUPLE {FirstName "J K", LastName "Randall", MPs 15, SPs 0},
	TUPLE {FirstName "J K", LastName "Schnitzer", MPs 12, SPs 0},
	TUPLE {FirstName "J M", LastName "Koprowski", MPs 6, SPs 0},
	TUPLE {FirstName "J O", LastName "Wylie", MPs 3, SPs 0},
	TUPLE {FirstName "J P", LastName "Jensen", MPs 4, SPs 0},
	TUPLE {FirstName "J P", LastName "Koorevaar", MPs 12, SPs 0},
	TUPLE {FirstName "J P", LastName "Mullen", MPs 3, SPs 0},
	TUPLE {FirstName "J R", LastName "Atkins", MPs 49, SPs 0},
	TUPLE {FirstName "J W F", LastName "Day", MPs 338, SPs 17},
	TUPLE {FirstName "J-L", LastName "Marro", MPs 7, SPs 0},
	TUPLE {FirstName "J.", LastName "Bygott", MPs 24, SPs 2},
	TUPLE {FirstName "J.", LastName "Guoba", MPs 60, SPs 1},
	TUPLE {FirstName "J.", LastName "Kelly", MPs 211, SPs 11},
	TUPLE {FirstName "J.A.", LastName "Dixon", MPs 3, SPs 0},
	TUPLE {FirstName "J.C.", LastName "Aggarwal", MPs 704, SPs 32},
	TUPLE {FirstName "J.J.", LastName "Zeckhauser", MPs 161, SPs 7},
	TUPLE {FirstName "J.N.", LastName "Salib", MPs 4, SPs 0},
	TUPLE {FirstName "J.R.", LastName "Manning", MPs 531, SPs 12},
	TUPLE {FirstName "Jac", LastName "Fuchs", MPs 10, SPs 0},
	TUPLE {FirstName "Jean-Marc", LastName "Bihl", MPs 208, SPs 24},
	TUPLE {FirstName "Jean-Marie", LastName "Maréchal", MPs 1002, SPs 77},
	TUPLE {FirstName "Jeff", LastName "Richmond", MPs 140, SPs 16},
	TUPLE {FirstName "Jesper", LastName "Dall", MPs 11, SPs 0},
	TUPLE {FirstName "Jim", LastName "Munday", MPs 12, SPs 1},
	TUPLE {FirstName "Joanna", LastName "Hands", MPs 2, SPs 0},
	TUPLE {FirstName "John", LastName "Bowness", MPs 2, SPs 0},
	TUPLE {FirstName "John", LastName "Goodwin", MPs 4, SPs 0},
	TUPLE {FirstName "John", LastName "Moser", MPs 3, SPs 0},
	TUPLE {FirstName "John", LastName "Murray", MPs 73, SPs 1},
	TUPLE {FirstName "John", LastName "Routley", MPs 41, SPs 0},
	TUPLE {FirstName "John", LastName "Slater", MPs 57, SPs 2},
	TUPLE {FirstName "John N.", LastName "Shadrick", MPs 4, SPs 0},
	TUPLE {FirstName "Jonathan", LastName "Mestel", MPs 7, SPs 1},
	TUPLE {FirstName "Julian", LastName "Lang", MPs 127, SPs 7},
	TUPLE {FirstName "Julian", LastName "Pottage", MPs 109, SPs 13},
	TUPLE {FirstName "K", LastName "Cardozo", MPs 6, SPs 0},
	TUPLE {FirstName "K", LastName "Cheung", MPs 2, SPs 0},
	TUPLE {FirstName "K", LastName "Goodge", MPs 2, SPs 0},
	TUPLE {FirstName "K A", LastName "Moore", MPs 17, SPs 0},
	TUPLE {FirstName "K E", LastName "Ballans", MPs 258, SPs 11},
	TUPLE {FirstName "K V", LastName "Bhatt", MPs 2, SPs 0},
	TUPLE {FirstName "K.Y.", LastName "Chen", MPs 4, SPs 0},
	TUPLE {FirstName "Karl Morten", LastName "Lunna", MPs 6, SPs 0},
	TUPLE {FirstName "Kit", LastName "Orde-Powlett", MPs 4, SPs 0},
	TUPLE {FirstName "Krishna", LastName "Vahalia", MPs 3, SPs 0},
	TUPLE {FirstName "Kukuh", LastName "Indrayana", MPs 0, SPs 0},
	TUPLE {FirstName "L A", LastName "Best", MPs 41, SPs 3},
	TUPLE {FirstName "L.", LastName "Moday", MPs 773, SPs 41},
	TUPLE {FirstName "L.J.", LastName "Curtis", MPs 85, SPs 3},
	TUPLE {FirstName "Lars-Hakan", LastName "Wilhelmsson", MPs 3, SPs 0},
	TUPLE {FirstName "Laszlo", LastName "Majoros", MPs 6, SPs 0},
	TUPLE {FirstName "Laurence", LastName "Payton", MPs 24, SPs 0},
	TUPLE {FirstName "Leigh", LastName "Matheson", MPs 61, SPs 1},
	TUPLE {FirstName "Leslie", LastName "Cass", MPs 768, SPs 129},
	TUPLE {FirstName "Lu Wu", LastName "Ping", MPs 7, SPs 1},
	TUPLE {FirstName "Luigi", LastName "Caroli", MPs 7, SPs 2},
	TUPLE {FirstName "M", LastName "Hornung", MPs 16, SPs 1},
	TUPLE {FirstName "M", LastName "Hosoya", MPs 71, SPs 3},
	TUPLE {FirstName "M", LastName "McGinley", MPs 81, SPs 0},
	TUPLE {FirstName "M", LastName "Tolaney", MPs 5, SPs 0},
	TUPLE {FirstName "M A", LastName "Bari", MPs 83, SPs 4},
	TUPLE {FirstName "M B", LastName "Glauert", MPs 479, SPs 25},
	TUPLE {FirstName "M I", LastName "Fosterjohn", MPs 4, SPs 0},
	TUPLE {FirstName "M J", LastName "Kuriger", MPs 2, SPs 0},
	TUPLE {FirstName "M J", LastName "Lloyd", MPs 2, SPs 0},
	TUPLE {FirstName "M J", LastName "Wynne", MPs 2, SPs 0},
	TUPLE {FirstName "M J S", LastName "Dewar", MPs 241, SPs 2},
	TUPLE {FirstName "M T", LastName "Vaughn", MPs 40, SPs 3},
	TUPLE {FirstName "M.", LastName "Leared", MPs 3, SPs 0},
	TUPLE {FirstName "M.", LastName "Smith", MPs 0, SPs 0},
	TUPLE {FirstName "M.", LastName "Vickers", MPs 556, SPs 23},
	TUPLE {FirstName "M.A.", LastName "Regan", MPs 4, SPs 0},
	TUPLE {FirstName "M.V.", LastName "Llewellyn", MPs 712, SPs 41},
	TUPLE {FirstName "Matt", LastName "Treveyn", MPs 2, SPs 0},
	TUPLE {FirstName "Mauro", LastName "Fiorentini", MPs 3, SPs 0},
	TUPLE {FirstName "Michael", LastName "Kaye", MPs 13, SPs 0},
	TUPLE {FirstName "Michael", LastName "Palitsch", MPs 8, SPs 2},
	TUPLE {FirstName "Michael", LastName "Webley", MPs 2, SPs 0},
	TUPLE {FirstName "Michael", LastName "Whittaker", MPs 7, SPs 0},
	TUPLE {FirstName "Mick", LastName "Spencer", MPs 2, SPs 0},
	TUPLE {FirstName "Mike", LastName "Betts", MPs 10, SPs 0},
	TUPLE {FirstName "Mike", LastName "Gallagher", MPs 206, SPs 12},
	TUPLE {FirstName "Mike", LastName "Hou", MPs 14, SPs 0},
	TUPLE {FirstName "Mike", LastName "Liver", MPs 0, SPs 0},
	TUPLE {FirstName "Mirna", LastName "Goacher", MPs 2, SPs 0},
	TUPLE {FirstName "Miss E A", LastName "Page", MPs 512, SPs 23},
	TUPLE {FirstName "Mohammad", LastName "Haikal", MPs 0, SPs 0},
	TUPLE {FirstName "Mrs B", LastName "Pascual", MPs 340, SPs 20},
	TUPLE {FirstName "Mrs CA", LastName "Pettit", MPs 4, SPs 0},
	TUPLE {FirstName "Mrs D C", LastName "Lintott", MPs 36, SPs 0},
	TUPLE {FirstName "Mrs D M", LastName "Calder", MPs 244, SPs 13},
	TUPLE {FirstName "Mrs I H", LastName "Banks", MPs 2, SPs 0},
	TUPLE {FirstName "Mrs M", LastName "Davies", MPs 12, SPs 0},
	TUPLE {FirstName "Mrs M", LastName "Lucy", MPs 26, SPs 0},
	TUPLE {FirstName "Mrs O", LastName "Watson", MPs 3, SPs 0},
	TUPLE {FirstName "Mrs. R.C.", LastName "Benson", MPs 45, SPs 0},
	TUPLE {FirstName "Mrs. T.", LastName "Czernuszka", MPs 4, SPs 0},
	TUPLE {FirstName "Ms T", LastName "Finestein", MPs 4, SPs 0},
	TUPLE {FirstName "N", LastName "Freake", MPs 30, SPs 1},
	TUPLE {FirstName "N", LastName "Rydell", MPs 3, SPs 0},
	TUPLE {FirstName "N H", LastName "Gayner", MPs 22, SPs 0},
	TUPLE {FirstName "N.", LastName "Guthrie", MPs 78, SPs 2},
	TUPLE {FirstName "N.", LastName "Sandqvist", MPs 5, SPs 0},
	TUPLE {FirstName "N.", LastName "Tawil", MPs 374, SPs 11},
	TUPLE {FirstName "N.H.", LastName "Morgenstern", MPs 17, SPs 0},
	TUPLE {FirstName "Nathan", LastName "Piper", MPs 11, SPs 0},
	TUPLE {FirstName "Ned", LastName "Paul", MPs 2, SPs 0},
	TUPLE {FirstName "Neil", LastName "Macdonald", MPs 57, SPs 2},
	TUPLE {FirstName "O G", LastName "Sims", MPs 6, SPs 0},
	TUPLE {FirstName "Ossi von", LastName "Goertz", MPs 209, SPs 16},
	TUPLE {FirstName "P", LastName "Barden", MPs 69, SPs 12},
	TUPLE {FirstName "P", LastName "Below", MPs 17, SPs 0},
	TUPLE {FirstName "P", LastName "Bengtsson", MPs 28, SPs 0},
	TUPLE {FirstName "P", LastName "Cockayne", MPs 8, SPs 0},
	TUPLE {FirstName "P", LastName "Fondevik", MPs 10, SPs 0},
	TUPLE {FirstName "P", LastName "Mallett", MPs 3, SPs 0},
	TUPLE {FirstName "P", LastName "Mitchell", MPs 2, SPs 0},
	TUPLE {FirstName "P", LastName "Newman", MPs 1, SPs 0},
	TUPLE {FirstName "P", LastName "Relph", MPs 42, SPs 1},
	TUPLE {FirstName "P", LastName "Tempest", MPs 3, SPs 0},
	TUPLE {FirstName "P", LastName "Viitasalo", MPs 15, SPs 1},
	TUPLE {FirstName "P A", LastName "Brereton", MPs 2, SPs 0},
	TUPLE {FirstName "P J", LastName "Bailey", MPs 84, SPs 7},
	TUPLE {FirstName "P M", LastName "Shannahan", MPs 2, SPs 0},
	TUPLE {FirstName "P.", LastName "Byrne", MPs 2, SPs 0},
	TUPLE {FirstName "P.", LastName "Davies", MPs 4, SPs 0},
	TUPLE {FirstName "P.", LastName "Fearnhead", MPs 24, SPs 2},
	TUPLE {FirstName "P.", LastName "Finaut", MPs 6, SPs 0},
	TUPLE {FirstName "P.A.", LastName "Lamford", MPs 68, SPs 3},
	TUPLE {FirstName "Paolo", LastName "Treossi", MPs 160, SPs 16},
	TUPLE {FirstName "Pavel", LastName "Striz", MPs 0, SPs 0},
	TUPLE {FirstName "Pearl", LastName "Duncan", MPs 2, SPs 0},
	TUPLE {FirstName "Pengcheng", LastName "Wu", MPs 14, SPs 0},
	TUPLE {FirstName "Peter", LastName "Calviou", MPs 2, SPs 0},
	TUPLE {FirstName "Peter", LastName "Dodson", MPs 4, SPs 0},
	TUPLE {FirstName "Peter", LastName "Wallrodt", MPs 7, SPs 1},
	TUPLE {FirstName "Prahalad", LastName "Rajkumar", MPs 0, SPs 0},
	TUPLE {FirstName "Pravin", LastName "Kashelkar", MPs 2, SPs 0},
	TUPLE {FirstName "R", LastName "Bley", MPs 2, SPs 0},
	TUPLE {FirstName "R", LastName "Dober", MPs 9, SPs 0},
	TUPLE {FirstName "R", LastName "Lemaire", MPs 39, SPs 13},
	TUPLE {FirstName "R", LastName "Proctor", MPs 16, SPs 0},
	TUPLE {FirstName "R", LastName "Schinasi", MPs 2, SPs 0},
	TUPLE {FirstName "R A", LastName "Cliffe", MPs 6, SPs 0},
	TUPLE {FirstName "R C", LastName "France", MPs 6, SPs 0},
	TUPLE {FirstName "R D", LastName "Alexander", MPs 32, SPs 1},
	TUPLE {FirstName "R F W", LastName "Coppen", MPs 9, SPs 0},
	TUPLE {FirstName "R G", LastName "Yaziçigil", MPs 5, SPs 0},
	TUPLE {FirstName "R H", LastName "Berk", MPs 11, SPs 0},
	TUPLE {FirstName "R H", LastName "Merson", MPs 36, SPs 2},
	TUPLE {FirstName "R J", LastName "Granville", MPs 360, SPs 27},
	TUPLE {FirstName "R J", LastName "Jarvis", MPs 2, SPs 0},
	TUPLE {FirstName "R J", LastName "Rose", MPs 18, SPs 0},
	TUPLE {FirstName "R L", LastName "Jepson", MPs 23, SPs 0},
	TUPLE {FirstName "R L", LastName "Pickering", MPs 9, SPs 0},
	TUPLE {FirstName "R M", LastName "Chamberlain", MPs 11, SPs 0},
	TUPLE {FirstName "R M", LastName "Turner", MPs 44, SPs 0},
	TUPLE {FirstName "R W", LastName "Skeates", MPs 9, SPs 0},
	TUPLE {FirstName "R.", LastName "Plumley", MPs 34, SPs 0},
	TUPLE {FirstName "R.", LastName "Topakbashian", MPs 54, SPs 1},
	TUPLE {FirstName "R. van", LastName "Poelgeest", MPs 6, SPs 0},
	TUPLE {FirstName "R.B.", LastName "Goyal", MPs 6, SPs 0},
	TUPLE {FirstName "R.F.", LastName "MacKinnon", MPs 5, SPs 0},
	TUPLE {FirstName "R.G.", LastName "Roscoe", MPs 2, SPs 0},
	TUPLE {FirstName "R.H.", LastName "Harper", MPs 23, SPs 0},
	TUPLE {FirstName "R.J.", LastName "Rutherford", MPs 3, SPs 0},
	TUPLE {FirstName "R.M.", LastName "Barker", MPs 51, SPs 2},
	TUPLE {FirstName "Radu", LastName "Mihai", MPs 0, SPs 0},
	TUPLE {FirstName "Rajeswar", LastName "Tewari", MPs 172, SPs 9},
	TUPLE {FirstName "Richard", LastName "Kent", MPs 2, SPs 0},
	TUPLE {FirstName "Robin", LastName "Adey", MPs 894, SPs 47},
	TUPLE {FirstName "Robin", LastName "Squire", MPs 9, SPs 0},
	TUPLE {FirstName "Ron", LastName "McEwan", MPs 9, SPs 0},
	TUPLE {FirstName "S", LastName "Cox", MPs 4, SPs 0},
	TUPLE {FirstName "S", LastName "Gellatly", MPs 34, SPs 2},
	TUPLE {FirstName "S", LastName "Göct?", MPs 15, SPs 0},
	TUPLE {FirstName "S", LastName "Kükyavuz", MPs 11, SPs 2},
	TUPLE {FirstName "S", LastName "McGibbon", MPs 5, SPs 0},
	TUPLE {FirstName "S", LastName "Whittleton", MPs 83, SPs 2},
	TUPLE {FirstName "S", LastName "Yalçin", MPs 86, SPs 1},
	TUPLE {FirstName "S B", LastName "Lilly", MPs 3, SPs 0},
	TUPLE {FirstName "S J", LastName "McVea", MPs 75, SPs 7},
	TUPLE {FirstName "Sandy", LastName "Dow", MPs 20, SPs 0},
	TUPLE {FirstName "Sebastian", LastName "Nowacki", MPs 38, SPs 3},
	TUPLE {FirstName "Senol", LastName "Baban", MPs 7, SPs 1},
	TUPLE {FirstName "Serhat", LastName "Erden", MPs 3, SPs 0},
	TUPLE {FirstName "Simon", LastName "Cheung", MPs 19, SPs 0},
	TUPLE {FirstName "Simon", LastName "Whitehouse", MPs 2, SPs 0},
	TUPLE {FirstName "Sinan", LastName "Tatlicioglu", MPs 7, SPs 1},
	TUPLE {FirstName "Souren Coomer", LastName "Dutt", MPs 27, SPs 0},
	TUPLE {FirstName "Stephen", LastName "Dunn", MPs 10, SPs 0},
	TUPLE {FirstName "Stephen", LastName "Rose", MPs 15, SPs 1},
	TUPLE {FirstName "Steve", LastName "Bloom", MPs 268, SPs 39},
	TUPLE {FirstName "Steve", LastName "Porter", MPs 2, SPs 0},
	TUPLE {FirstName "Stewart", LastName "Pye", MPs 109, SPs 5},
	TUPLE {FirstName "Sudhir Kumar", LastName "Ganguly", MPs 2, SPs 0},
	TUPLE {FirstName "Sylvain", LastName "Schwartz", MPs 69, SPs 0},
	TUPLE {FirstName "T", LastName "Karatzas", MPs 411, SPs 36},
	TUPLE {FirstName "T", LastName "Mortensen", MPs 1, SPs 0},
	TUPLE {FirstName "T", LastName "Smith", MPs 2, SPs 0},
	TUPLE {FirstName "T C S", LastName "Northover", MPs 12, SPs 0},
	TUPLE {FirstName "T G", LastName "Townsend", MPs 2, SPs 0},
	TUPLE {FirstName "T T", LastName "Paterson", MPs 2, SPs 0},
	TUPLE {FirstName "Temes", LastName "Chaim", MPs 7, SPs 1},
	TUPLE {FirstName "Terry", LastName "Coulson", MPs 2, SPs 0},
	TUPLE {FirstName "Tim", LastName "Chanter", MPs 4, SPs 0},
	TUPLE {FirstName "Tommy", LastName "Cho", MPs 5, SPs 0},
	TUPLE {FirstName "Tonci", LastName "Tomic", MPs 7, SPs 2},
	TUPLE {FirstName "Tony", LastName "Hobson", MPs 4, SPs 0},
	TUPLE {FirstName "Ulrich", LastName "Auhagen", MPs 111, SPs 7},
	TUPLE {FirstName "Uscumlic", LastName "Milan", MPs 5, SPs 0},
	TUPLE {FirstName "V", LastName "Morris", MPs 31, SPs 0},
	TUPLE {FirstName "V A", LastName "Silverstone", MPs 7, SPs 0},
	TUPLE {FirstName "V G", LastName "Kamath", MPs 65, SPs 0},
	TUPLE {FirstName "Vasco M.", LastName "Leite", MPs 5, SPs 0},
	TUPLE {FirstName "Victor", LastName "Floriean", MPs 37, SPs 1},
	TUPLE {FirstName "Vincent", LastName "Labbé", MPs 100, SPs 17},
	TUPLE {FirstName "W", LastName "Rabbit", MPs 3, SPs 0},
	TUPLE {FirstName "W A", LastName "Bushell", MPs 138, SPs 4},
	TUPLE {FirstName "W C", LastName "Pettet", MPs 27, SPs 0},
	TUPLE {FirstName "W.", LastName "Frith", MPs 18, SPs 0},
	TUPLE {FirstName "W.D.", LastName "Clarke", MPs 352, SPs 21},
	TUPLE {FirstName "W.G.", LastName "Turner", MPs 4, SPs 0},
	TUPLE {FirstName "Wg Cdr WJ", LastName "Massey", MPs 53, SPs 1},
	TUPLE {FirstName "Wim van der", LastName "Zijden", MPs 1406, SPs 116},
	TUPLE {FirstName "Wu", LastName "Yaodong", MPs 156, SPs 8},
	TUPLE {FirstName "Y", LastName "Aköz", MPs 4, SPs 0},
	TUPLE {FirstName "Y", LastName "Salomon", MPs 4, SPs 0},
	TUPLE {FirstName "Y.M.", LastName "Gay", MPs 3, SPs 0},
	TUPLE {FirstName "Yair", LastName "Arad", MPs 9, SPs 1},
	TUPLE {FirstName "Yong Hao", LastName "Ng", MPs 4, SPs 1},
	TUPLE {FirstName "Yunfeng", LastName "Zhu", MPs 9, SPs 0},
	TUPLE {FirstName "Z.", LastName "Shilon", MPs 196, SPs 6}
}
;

ANNOUNCE 'operator IS_DIGITS';
OPERATOR IS_DIGITS(s CHARACTER) RETURNS BOOLEAN Java FOREIGN      
	String sbuf = s.stringValue();
	for (int i=0; i<sbuf.length(); i++)
		if (!Character.isDigit(sbuf.charAt(i)))
			return ValueBoolean.select(context.getGenerator(), false);
	return ValueBoolean.select(context.getGenerator(), true);
END OPERATOR;

ANNOUNCE 'operator LENGTH';
OPERATOR LENGTH(s CHARACTER) RETURNS INTEGER Java FOREIGN      
	return ValueInteger.select(context.getGenerator(), s.stringValue().length());
END OPERATOR;

ANNOUNCE 'operator SUBSTRING';
OPERATOR SUBSTRING(s CHARACTER, index INTEGER) RETURNS CHARACTER Java FOREIGN      
// Substring, 0 based
	return ValueCharacter.select(context.getGenerator(), s.stringValue().substring((int)index.longValue()));
END OPERATOR;

ANNOUNCE 'operator SUBSTRING';
OPERATOR SUBSTRING(s CHARACTER, beginindex INTEGER, endindex INTEGER) RETURNS CHARACTER Java FOREIGN      
// Substring, 0 based
	return ValueCharacter.select(context.getGenerator(), s.stringValue().substring((int)beginindex.longValue(), (int)endindex.longValue()));
END OPERATOR;

ANNOUNCE 'operator COMPARE_TO';
OPERATOR COMPARE_TO(s CHARACTER, anotherString CHARACTER) RETURNS INTEGER Java FOREIGN       
//          Compares two strings lexicographically.
	return ValueInteger.select(context.getGenerator(), s.stringValue().compareTo(anotherString.stringValue()));
END OPERATOR;

ANNOUNCE 'operator COMPARE_TO_IGNORE_CASE';
OPERATOR COMPARE_TO_IGNORE_CASE(s CHARACTER, str CHARACTER) RETURNS INTEGER Java FOREIGN      
//          Compares two strings lexicographically, ignoring case differences.
	return ValueInteger.select(context.getGenerator(), s.stringValue().compareToIgnoreCase(str.stringValue()));
END OPERATOR;

ANNOUNCE 'operator ENDS_WITH';
OPERATOR ENDS_WITH(s CHARACTER, suffx CHARACTER) RETURNS BOOLEAN Java FOREIGN      
	return ValueBoolean.select(context.getGenerator(), s.stringValue().endsWith(suffx.stringValue()));
END OPERATOR;

ANNOUNCE 'operator EQUALS_IGNORE_CASE';
OPERATOR EQUALS_IGNORE_CASE(s CHARACTER, anotherString CHARACTER) RETURNS BOOLEAN Java FOREIGN      
//          Compares this String to another String, ignoring case considerations.
	return ValueBoolean.select(context.getGenerator(), s.stringValue().equalsIgnoreCase(anotherString.stringValue()));
END OPERATOR;

ANNOUNCE 'operator INDEX_OF';
OPERATOR INDEX_OF(s CHARACTER, str CHARACTER) RETURNS INTEGER Java FOREIGN      
//          Returns the index within this string of the first occurrence of the specified substring.
	return ValueInteger.select(context.getGenerator(), s.stringValue().indexOf(str.stringValue()));
END OPERATOR;

ANNOUNCE 'operator INDEX_OF';
OPERATOR INDEX_OF(s CHARACTER, str CHARACTER, fromIndex INTEGER) RETURNS INTEGER Java FOREIGN      
//          Returns the index within this string of the first occurrence of the 
//          specified substring, starting at the specified index.
	return ValueInteger.select(context.getGenerator(), s.stringValue().indexOf(str.stringValue(), (int)fromIndex.longValue()));
END OPERATOR;

ANNOUNCE 'operator LAST_INDEX_OF';
OPERATOR LAST_INDEX_OF(s CHARACTER, str CHARACTER) RETURNS INTEGER Java FOREIGN      
//          Returns the index within this string of the rightmost occurrence of the specified substring.
	return ValueInteger.select(context.getGenerator(), s.stringValue().lastIndexOf(str.stringValue()));
END OPERATOR;

ANNOUNCE 'operator LAST_INDEX_OF';
OPERATOR LAST_INDEX_OF(s CHARACTER, str CHARACTER, fromIndex INTEGER) RETURNS INTEGER Java FOREIGN      
//          Returns the index within this string of the last occurrence of 
//          the specified substring, searching backward starting at the specified index.
	return ValueInteger.select(context.getGenerator(), s.stringValue().lastIndexOf(str.stringValue(), (int)fromIndex.longValue()));
END OPERATOR;

ANNOUNCE 'operator MATCHES';
OPERATOR MATCHES(s CHARACTER, regex CHARACTER) RETURNS BOOLEAN Java FOREIGN      
//          Tells whether or not this string matches the given regular expression.
	return ValueBoolean.select(context.getGenerator(), s.stringValue().matches(regex.stringValue()));
END OPERATOR;

ANNOUNCE 'operator REGION_MATCHES';
OPERATOR REGION_MATCHES(s CHARACTER, ignoreCase BOOLEAN, toffset INTEGER, other CHARACTER, ooffset INTEGER, len INTEGER) RETURNS BOOLEAN Java FOREIGN      
//          Tests if two string regions are equal.
	return ValueBoolean.select(context.getGenerator(), s.stringValue().regionMatches(ignoreCase.booleanValue(),
						(int)toffset.longValue(),
						other.stringValue(),
						(int)ooffset.longValue(),
						(int)len.longValue()));
END OPERATOR;

ANNOUNCE 'operator REPLACE_ALL';
OPERATOR REPLACE_ALL(s CHARACTER, regex CHARACTER, replacement CHARACTER) RETURNS CHARACTER Java FOREIGN      
//          Replaces each substring of this string that matches the given regular expression with the given replacement.
	return ValueCharacter.select(context.getGenerator(), s.stringValue().replaceAll(regex.stringValue(), replacement.stringValue()));
END OPERATOR;

ANNOUNCE 'operator REPLACE_FIRST';
OPERATOR REPLACE_FIRST(s CHARACTER, regex CHARACTER, replacement CHARACTER) RETURNS CHARACTER Java FOREIGN      
//          Replaces the first substring of this string that matches the given regular expression with the given replacement.
	return ValueCharacter.select(context.getGenerator(), s.stringValue().replaceFirst(regex.stringValue(), replacement.stringValue()));
END OPERATOR;

ANNOUNCE 'operator STARTS_WITH';
OPERATOR STARTS_WITH(s CHARACTER, prefx CHARACTER) RETURNS BOOLEAN Java FOREIGN      
//          Tests if this string starts with the specified prefix.
	return ValueBoolean.select(context.getGenerator(), s.stringValue().startsWith(prefx.stringValue()));
END OPERATOR;

ANNOUNCE 'operator STARTS_WITH';
OPERATOR STARTS_WITH(s CHARACTER, prefx CHARACTER, toffset INTEGER) RETURNS BOOLEAN Java FOREIGN      
//          Tests if this string starts with the specified prefix beginning at a specified index.
	return ValueBoolean.select(context.getGenerator(), s.stringValue().startsWith(prefx.stringValue(), (int)toffset.longValue()));
END OPERATOR;

ANNOUNCE 'operator TO_LOWER_CASE';
OPERATOR TO_LOWER_CASE(s CHARACTER) RETURNS CHARACTER Java FOREIGN      
	return ValueCharacter.select(context.getGenerator(), s.stringValue().toLowerCase());
END OPERATOR;

ANNOUNCE 'operator TO_UPPER_CASE';
OPERATOR TO_UPPER_CASE(s CHARACTER) RETURNS CHARACTER Java FOREIGN      
//          Converts all of the characters in this String to upper case using the rules of the default locale.
	return ValueCharacter.select(context.getGenerator(), s.stringValue().toUpperCase());
END OPERATOR;

ANNOUNCE 'operator TRIM';
OPERATOR TRIM(s CHARACTER) RETURNS CHARACTER Java FOREIGN      
//          Trim leading and trailing blanks.
	return ValueCharacter.select(context.getGenerator(), s.stringValue().trim());
END OPERATOR;

ANNOUNCE 'var ComposedBy';
VAR ComposedBy REAL RELATION {Problem# CHARACTER, FirstName CHARACTER, LastName CHARACTER} KEY {Problem#, FirstName, LastName};
ComposedBy := RELATION {
	TUPLE {Problem# "071 ", FirstName "Ian", LastName "Budden"},
	TUPLE {Problem# "072 ", FirstName "Steve", LastName "Bloom"},
	TUPLE {Problem# "073 ", FirstName "Sebastian", LastName "Nowacki"},
	TUPLE {Problem# "074 ", FirstName "Steve", LastName "Bloom"},
	TUPLE {Problem# "075 ", FirstName "Paolo", LastName "Treossi"},
	TUPLE {Problem# "076 ", FirstName "Steve", LastName "Bloom"},
	TUPLE {Problem# "077 ", FirstName "Hugh", LastName "Darwen"},
	TUPLE {Problem# "078 ", FirstName "Paolo", LastName "Treossi"},
	TUPLE {Problem# "079 ", FirstName "Steve", LastName "Bloom"},
	TUPLE {Problem# "080 ", FirstName "Ian", LastName "Budden"},
	TUPLE {Problem# "081 ", FirstName "Paolo", LastName "Treossi"},
	TUPLE {Problem# "082 ", FirstName "Steve", LastName "Bloom"},
	TUPLE {Problem# "082a", FirstName "Sebastian", LastName "Nowacki"},
	TUPLE {Problem# "082b", FirstName "Sebastian", LastName "Nowacki"},
	TUPLE {Problem# "083 ", FirstName "Ian", LastName "Budden"},
	TUPLE {Problem# "084 ", FirstName "Paolo", LastName "Treossi"},
	TUPLE {Problem# "085 ", FirstName "Hugh", LastName "Darwen"},
	TUPLE {Problem# "086 ", FirstName "Paolo", LastName "Treossi"},
	TUPLE {Problem# "087 ", FirstName "Steve", LastName "Bloom"},
	TUPLE {Problem# "088 ", FirstName "Paolo", LastName "Treossi"},
	TUPLE {Problem# "089 ", FirstName "Jean-Marc", LastName "Bihl"},
	TUPLE {Problem# "090 ", FirstName "Steve", LastName "Bloom"},
	TUPLE {Problem# "091 ", FirstName "Paolo", LastName "Treossi"},
	TUPLE {Problem# "092 ", FirstName "Vincent", LastName "Labbé"},
	TUPLE {Problem# "093 ", FirstName "Paolo", LastName "Treossi"},
	TUPLE {Problem# "094a", FirstName "Con", LastName "Vention"},
	TUPLE {Problem# "094b", FirstName "F.Y.", LastName "Sing"}
}
;

ANNOUNCE 'var Problem';
VAR Problem REAL RELATION {Problem# CHARACTER, DR INTEGER, Month CHARACTER, Year CHARACTER, NumberOfComposers INTEGER} KEY {Problem#};
Problem := RELATION {
	TUPLE {Problem# "071 ", DR 2, Month "Jan", Year "2011", NumberOfComposers 1},
	TUPLE {Problem# "072 ", DR 4, Month "Feb", Year "2011", NumberOfComposers 1},
	TUPLE {Problem# "073 ", DR 6, Month "Mar", Year "2011", NumberOfComposers 1},
	TUPLE {Problem# "074 ", DR 3, Month "Apr", Year "2011", NumberOfComposers 1},
	TUPLE {Problem# "075 ", DR 7, Month "May", Year "2011", NumberOfComposers 1},
	TUPLE {Problem# "076 ", DR 4, Month "Jun", Year "2011", NumberOfComposers 1},
	TUPLE {Problem# "077 ", DR 5, Month "Jul", Year "2011", NumberOfComposers 1},
	TUPLE {Problem# "078 ", DR 6, Month "Aug", Year "2011", NumberOfComposers 1},
	TUPLE {Problem# "079 ", DR 4, Month "Sep", Year "2011", NumberOfComposers 1},
	TUPLE {Problem# "080 ", DR 4, Month "Oct", Year "2011", NumberOfComposers 1},
	TUPLE {Problem# "081 ", DR 7, Month "Nov", Year "2011", NumberOfComposers 1},
	TUPLE {Problem# "082 ", DR 8, Month "Dec", Year "2011", NumberOfComposers 1},
	TUPLE {Problem# "082a", DR 2, Month "Dec", Year "2011", NumberOfComposers 1},
	TUPLE {Problem# "082b", DR 1, Month "Dec", Year "2011", NumberOfComposers 1},
	TUPLE {Problem# "083 ", DR 2, Month "Jan", Year "2012", NumberOfComposers 1},
	TUPLE {Problem# "084 ", DR 8, Month "Feb", Year "2012", NumberOfComposers 1},
	TUPLE {Problem# "085 ", DR 5, Month "March", Year "2012", NumberOfComposers 1},
	TUPLE {Problem# "086 ", DR 6, Month "April", Year "2012", NumberOfComposers 1},
	TUPLE {Problem# "087 ", DR 6, Month "May", Year "2012", NumberOfComposers 1},
	TUPLE {Problem# "088 ", DR 6, Month "June", Year "2012", NumberOfComposers 1},
	TUPLE {Problem# "089 ", DR 8, Month "July", Year "2012", NumberOfComposers 1},
	TUPLE {Problem# "090 ", DR 6, Month "August", Year "2012", NumberOfComposers 1},
	TUPLE {Problem# "091 ", DR 5, Month "September", Year "2012", NumberOfComposers 1},
	TUPLE {Problem# "092 ", DR 5, Month "October", Year "2012", NumberOfComposers 1},
	TUPLE {Problem# "093 ", DR 3, Month "November", Year "2012", NumberOfComposers 1},
	TUPLE {Problem# "094a", DR 2, Month "December", Year "2012", NumberOfComposers 1},
	TUPLE {Problem# "094b", DR 3, Month "December", Year "2012", NumberOfComposers 1}
}
;

ANNOUNCE 'var SolvedBy';
VAR SolvedBy REAL RELATION {Problem# CHARACTER, FirstName CHARACTER, LastName CHARACTER} KEY {Problem#, FirstName, LastName};
SolvedBy := RELATION {
	TUPLE {Problem# "071 ", FirstName "", LastName "Satyanarayana"},
	TUPLE {Problem# "071 ", FirstName "Abby", LastName "Chiu"},
	TUPLE {Problem# "071 ", FirstName "Bob", LastName "Bignall"},
	TUPLE {Problem# "071 ", FirstName "Daniel de Lind van", LastName "Wijngaarden"},
	TUPLE {Problem# "071 ", FirstName "Dick", LastName "Yuen"},
	TUPLE {Problem# "071 ", FirstName "Mohammad", LastName "Haikal"},
	TUPLE {Problem# "071 ", FirstName "Rajeswar", LastName "Tewari"},
	TUPLE {Problem# "071 ", FirstName "Sebastian", LastName "Nowacki"},
	TUPLE {Problem# "071 ", FirstName "Steve", LastName "Bloom"},
	TUPLE {Problem# "071 ", FirstName "Sylvain", LastName "Schwartz"},
	TUPLE {Problem# "071 ", FirstName "Wim van der", LastName "Zijden"},
	TUPLE {Problem# "072 ", FirstName "", LastName "Satyanarayana"},
	TUPLE {Problem# "072 ", FirstName "Dick", LastName "Yuen"},
	TUPLE {Problem# "072 ", FirstName "Ian", LastName "Budden"},
	TUPLE {Problem# "072 ", FirstName "Leigh", LastName "Matheson"},
	TUPLE {Problem# "072 ", FirstName "Rajeswar", LastName "Tewari"},
	TUPLE {Problem# "072 ", FirstName "Sebastian", LastName "Nowacki"},
	TUPLE {Problem# "072 ", FirstName "Wim van der", LastName "Zijden"},
	TUPLE {Problem# "073 ", FirstName "", LastName "Satyanarayana"},
	TUPLE {Problem# "073 ", FirstName "Jean-Marc", LastName "Bihl"},
	TUPLE {Problem# "073 ", FirstName "Rajeswar", LastName "Tewari"},
	TUPLE {Problem# "073 ", FirstName "Steve", LastName "Bloom"},
	TUPLE {Problem# "073 ", FirstName "Wim van der", LastName "Zijden"},
	TUPLE {Problem# "074 ", FirstName "", LastName "Satyanarayana"},
	TUPLE {Problem# "074 ", FirstName "Jean-Marc", LastName "Bihl"},
	TUPLE {Problem# "074 ", FirstName "Leigh", LastName "Matheson"},
	TUPLE {Problem# "074 ", FirstName "Rajeswar", LastName "Tewari"},
	TUPLE {Problem# "074 ", FirstName "Sebastian", LastName "Nowacki"},
	TUPLE {Problem# "074 ", FirstName "Wim van der", LastName "Zijden"},
	TUPLE {Problem# "075 ", FirstName "", LastName "Satyanarayana"},
	TUPLE {Problem# "075 ", FirstName "Sebastian", LastName "Nowacki"},
	TUPLE {Problem# "075 ", FirstName "Steve", LastName "Bloom"},
	TUPLE {Problem# "076 ", FirstName "", LastName "Satyanarayana"},
	TUPLE {Problem# "076 ", FirstName "Ian", LastName "Budden"},
	TUPLE {Problem# "076 ", FirstName "Sebastian", LastName "Nowacki"},
	TUPLE {Problem# "077 ", FirstName "", LastName "Satyanarayana"},
	TUPLE {Problem# "077 ", FirstName "Ian", LastName "Budden"},
	TUPLE {Problem# "077 ", FirstName "Jean-Marc", LastName "Bihl"},
	TUPLE {Problem# "077 ", FirstName "Sebastian", LastName "Nowacki"},
	TUPLE {Problem# "077 ", FirstName "Steve", LastName "Bloom"},
	TUPLE {Problem# "077 ", FirstName "Wim van der", LastName "Zijden"},
	TUPLE {Problem# "078 ", FirstName "Ian", LastName "Budden"},
	TUPLE {Problem# "078 ", FirstName "Jean-Marc", LastName "Bihl"},
	TUPLE {Problem# "078 ", FirstName "Leigh", LastName "Matheson"},
	TUPLE {Problem# "078 ", FirstName "Sebastian", LastName "Nowacki"},
	TUPLE {Problem# "078 ", FirstName "Wim van der", LastName "Zijden"},
	TUPLE {Problem# "079 ", FirstName "Ian", LastName "Budden"},
	TUPLE {Problem# "079 ", FirstName "Jean-Marc", LastName "Bihl"},
	TUPLE {Problem# "079 ", FirstName "Leigh", LastName "Matheson"},
	TUPLE {Problem# "079 ", FirstName "Sebastian", LastName "Nowacki"},
	TUPLE {Problem# "080 ", FirstName "Chaokai", LastName "Cheng"},
	TUPLE {Problem# "080 ", FirstName "Daniel de Lind van", LastName "Wijngaarden"},
	TUPLE {Problem# "080 ", FirstName "Dick", LastName "Yuen"},
	TUPLE {Problem# "080 ", FirstName "Jean-Marc", LastName "Bihl"},
	TUPLE {Problem# "080 ", FirstName "Steve", LastName "Bloom"},
	TUPLE {Problem# "080 ", FirstName "Wim van der", LastName "Zijden"},
	TUPLE {Problem# "081 ", FirstName "Ian", LastName "Budden"},
	TUPLE {Problem# "081 ", FirstName "Jean-Marc", LastName "Bihl"},
	TUPLE {Problem# "081 ", FirstName "Steve", LastName "Bloom"},
	TUPLE {Problem# "081 ", FirstName "Wim van der", LastName "Zijden"},
	TUPLE {Problem# "082 ", FirstName "Ian", LastName "Budden"},
	TUPLE {Problem# "082a", FirstName "Daniel de Lind van", LastName "Wijngaarden"},
	TUPLE {Problem# "082a", FirstName "Dick", LastName "Yuen"},
	TUPLE {Problem# "082a", FirstName "Ian", LastName "Budden"},
	TUPLE {Problem# "082a", FirstName "Leigh", LastName "Matheson"},
	TUPLE {Problem# "082a", FirstName "Pavel", LastName "Striz"},
	TUPLE {Problem# "082a", FirstName "Wim van der", LastName "Zijden"},
	TUPLE {Problem# "082b", FirstName "Daniel de Lind van", LastName "Wijngaarden"},
	TUPLE {Problem# "082b", FirstName "Dick", LastName "Yuen"},
	TUPLE {Problem# "082b", FirstName "Ian", LastName "Budden"},
	TUPLE {Problem# "082b", FirstName "Ian", LastName "McGarrett"},
	TUPLE {Problem# "082b", FirstName "Leigh", LastName "Matheson"},
	TUPLE {Problem# "082b", FirstName "Pavel", LastName "Striz"},
	TUPLE {Problem# "082b", FirstName "Steve", LastName "Bloom"},
	TUPLE {Problem# "082b", FirstName "Wim van der", LastName "Zijden"},
	TUPLE {Problem# "083 ", FirstName "Ayhan", LastName "Kök"},
	TUPLE {Problem# "083 ", FirstName "Daniel de Lind van", LastName "Wijngaarden"},
	TUPLE {Problem# "083 ", FirstName "Jean-Marc", LastName "Bihl"},
	TUPLE {Problem# "083 ", FirstName "Leigh", LastName "Matheson"},
	TUPLE {Problem# "083 ", FirstName "Pavel", LastName "Striz"},
	TUPLE {Problem# "083 ", FirstName "Prahalad", LastName "Rajkumar"},
	TUPLE {Problem# "083 ", FirstName "Sebastian", LastName "Nowacki"},
	TUPLE {Problem# "083 ", FirstName "Steve", LastName "Bloom"},
	TUPLE {Problem# "083 ", FirstName "Wim van der", LastName "Zijden"},
	TUPLE {Problem# "085 ", FirstName "Jean-Marc", LastName "Bihl"},
	TUPLE {Problem# "085 ", FirstName "Sebastian", LastName "Nowacki"},
	TUPLE {Problem# "085 ", FirstName "Steve", LastName "Bloom"},
	TUPLE {Problem# "085 ", FirstName "Wim van der", LastName "Zijden"},
	TUPLE {Problem# "086 ", FirstName "Ian", LastName "Budden"},
	TUPLE {Problem# "086 ", FirstName "Jean-Marc", LastName "Bihl"},
	TUPLE {Problem# "086 ", FirstName "Leigh", LastName "Matheson"},
	TUPLE {Problem# "086 ", FirstName "Sebastian", LastName "Nowacki"},
	TUPLE {Problem# "086 ", FirstName "Steve", LastName "Bloom"},
	TUPLE {Problem# "086 ", FirstName "Vincent", LastName "Labbé"},
	TUPLE {Problem# "086 ", FirstName "Wim van der", LastName "Zijden"},
	TUPLE {Problem# "087 ", FirstName "Jean-Marc", LastName "Bihl"},
	TUPLE {Problem# "088 ", FirstName "Steve", LastName "Bloom"},
	TUPLE {Problem# "089 ", FirstName "Steve", LastName "Bloom"},
	TUPLE {Problem# "090 ", FirstName "Ian", LastName "Budden"},
	TUPLE {Problem# "090 ", FirstName "Jean-Marc", LastName "Bihl"},
	TUPLE {Problem# "090 ", FirstName "Leigh", LastName "Matheson"},
	TUPLE {Problem# "090 ", FirstName "Wim van der", LastName "Zijden"},
	TUPLE {Problem# "091 ", FirstName "Abby", LastName "Chiu"},
	TUPLE {Problem# "091 ", FirstName "Ian", LastName "Budden"},
	TUPLE {Problem# "091 ", FirstName "Jean-Marc", LastName "Bihl"},
	TUPLE {Problem# "091 ", FirstName "Wim van der", LastName "Zijden"},
	TUPLE {Problem# "092 ", FirstName "Abby", LastName "Chiu"},
	TUPLE {Problem# "092 ", FirstName "Dick", LastName "Yuen"},
	TUPLE {Problem# "092 ", FirstName "Leigh", LastName "Matheson"},
	TUPLE {Problem# "092 ", FirstName "Sebastian", LastName "Nowacki"},
	TUPLE {Problem# "092 ", FirstName "Wim van der", LastName "Zijden"},
	TUPLE {Problem# "093 ", FirstName "Ian", LastName "Budden"},
	TUPLE {Problem# "093 ", FirstName "Leigh", LastName "Matheson"},
	TUPLE {Problem# "093 ", FirstName "Sebastian", LastName "Nowacki"},
	TUPLE {Problem# "093 ", FirstName "Wim van der", LastName "Zijden"},
	TUPLE {Problem# "094a", FirstName "Daniel de Lind van", LastName "Wijngaarden"},
	TUPLE {Problem# "094a", FirstName "Ian", LastName "Budden"},
	TUPLE {Problem# "094a", FirstName "Jean-Marc", LastName "Bihl"},
	TUPLE {Problem# "094a", FirstName "Kukuh", LastName "Indrayana"},
	TUPLE {Problem# "094a", FirstName "Leigh", LastName "Matheson"},
	TUPLE {Problem# "094a", FirstName "Radu", LastName "Mihai"},
	TUPLE {Problem# "094a", FirstName "Sebastian", LastName "Nowacki"},
	TUPLE {Problem# "094a", FirstName "Steve", LastName "Bloom"},
	TUPLE {Problem# "094a", FirstName "Wim van der", LastName "Zijden"},
	TUPLE {Problem# "094b", FirstName "Daniel de Lind van", LastName "Wijngaarden"},
	TUPLE {Problem# "094b", FirstName "Ian", LastName "Budden"},
	TUPLE {Problem# "094b", FirstName "Jean-Marc", LastName "Bihl"},
	TUPLE {Problem# "094b", FirstName "Kukuh", LastName "Indrayana"},
	TUPLE {Problem# "094b", FirstName "Leigh", LastName "Matheson"},
	TUPLE {Problem# "094b", FirstName "Radu", LastName "Mihai"},
	TUPLE {Problem# "094b", FirstName "Sebastian", LastName "Nowacki"},
	TUPLE {Problem# "094b", FirstName "Steve", LastName "Bloom"},
	TUPLE {Problem# "094b", FirstName "Wim van der", LastName "Zijden"}
}
;

ANNOUNCE 'var BaseRelvars';
VAR BaseRelvars VIRTUAL ( sys.Catalog where Owner = 'User' and not ( isVirtual ) ) { Name , Definition };

ANNOUNCE 'var Views';
VAR Views VIRTUAL ( sys.Catalog where Owner = 'User' and isVirtual ) { Name , Definition };

ANNOUNCE 'var Constraints';
VAR Constraints VIRTUAL sys.Constraints { Name , Definition };

ANNOUNCE 'var Grade';
VAR Grade REAL RELATION {ReqMPs INTEGER, ReqSPs INTEGER, Grade CHARACTER} KEY {Grade};
Grade := RELATION {
	TUPLE {ReqMPs 100, ReqSPs 0, Grade "Expert Problemist"},
	TUPLE {ReqMPs 750, ReqSPs 25, Grade "Grand Master Problemist"},
	TUPLE {ReqMPs 400, ReqSPs 10, Grade "Life Master Problemist"},
	TUPLE {ReqMPs 200, ReqSPs 5, Grade "Master Problemist"},
	TUPLE {ReqMPs 0, ReqSPs 0, Grade "Novice"},
	TUPLE {ReqMPs 50, ReqSPs 0, Grade "Problemist"}
}
;

ANNOUNCE 'var PointsForSolving';
VAR PointsForSolving VIRTUAL summarize ( join { SolvedBy , Problem , SP } ) by { FirstName , LastName } : {MoreMPs := SUM ( DR ) , MoreSPs := SUM ( SPs ) };

ANNOUNCE 'var PointsToBeAdded';
VAR PointsToBeAdded VIRTUAL extend ( join { ( extend SolvedBy : {SorC := 'S'} union extend ComposedBy : {SorC := 'C'} ) , Problem , SP } ) : {ActSPs := ( CASE when SorC = 'C' then ( DR + 3 ) / 4 else SPs end case ) };

ANNOUNCE 'var AllTimeStep1';
VAR AllTimeStep1 VIRTUAL ( extend ( join { Problemist , summarize PointsToBeAdded by { FirstName , LastName } : {MoreMPs := SUM ( DR ) , MoreSPs := SUM ( ActSPs ) } } ) : {TotalMPs := MPs + MoreMPs , TotalSPs := SPs + MoreSPs } ) { FirstName , LastName , TotalMPs , TotalSPs } rename {TotalMPs AS MPs, TotalSPs AS SPs } union ( Problemist not matching SolvedBy );

ANNOUNCE 'var AllTimeGraded';
VAR AllTimeGraded VIRTUAL ( extend AllTimeStep1 : { Grade := Grade from tuple from ( join { Grade , summarize ( Grade where ReqMPs <= MPs and ReqSPs <= SPs ) : {ReqMPs := MAX ( ReqMPs ) } } ) } );

ANNOUNCE 'var AllTimeForWeb';
VAR AllTimeForWeb VIRTUAL extend AllTimeGraded : {Name := FirstName || ' ' || LastName } { ALL BUT FirstName , LastName };

ANNOUNCE 'var League';
VAR League VIRTUAL summarize ( ( PointsToBeAdded where Year = MAX ( Problem , Year ) ) { ALL BUT SorC , Month , Year , NumberOfComposers , SPs } ) by { FirstName , LastName } : {MPs := SUM ( DR ) , SPs := SUM ( ActSPs ) };

ANNOUNCE 'var PointsExcludingLatest';
VAR PointsExcludingLatest VIRTUAL WITH (Latest := MAX ( Problem , Problem# ) ) : extend ( join { ( extend SolvedBy where Problem# < Latest : {SorC := 'S' } union extend ComposedBy where Problem# < Latest : { SorC := 'C' } ) , Problem , SP } ) : {ActSPs := ( CASE when SorC = 'C' then ( DR + 3 ) / 4 else SPs end case ) };

ANNOUNCE 'var GradeBeforeLatest';
VAR GradeBeforeLatest VIRTUAL ( extend ( join { Problemist , summarize PointsExcludingLatest by { FirstName , LastName } : {MoreMPs := SUM ( DR ) , MoreSPs := SUM ( ActSPs ) } } ) : { TotalMPs := MPs + MoreMPs , TotalSPs := SPs + MoreSPs , PrevGrade := Grade from tuple from ( join { Grade , summarize ( Grade where ReqMPs <= TotalMPs and ReqSPs <= TotalSPs ) : {ReqMPs := MAX ( ReqMPs ) } } ) } ) { FirstName , LastName , TotalMPs , TotalSPs , PrevGrade } rename { TotalMPs AS MPs , TotalSPs as SPs };

ANNOUNCE 'var NewTotals';
VAR NewTotals VIRTUAL WITH (Latest := MAX ( SolvedBy , Problem# ) ) : ( extend ( join { Problemist , summarize ( PointsToBeAdded matching ( ( ( SolvedBy where Problem# = Latest ) union ( ComposedBy where Problem# = Latest ) ) { FirstName , LastName } ) ) by { FirstName , LastName } : {MoreMPs := SUM ( DR )  , MoreSPs := SUM ( ActSPs )} } ) : {TotalMPs := MPs + MoreMPs , TotalSPs := SPs + MoreSPs } ) { FirstName , LastName , TotalMPs , TotalSPs } rename { TotalMPs AS MPs , TotalSPs as SPs };

ANNOUNCE 'var NewGrades';
VAR NewGrades VIRTUAL ( extend NewTotals : { Grade := Grade from tuple from ( join { Grade , summarize ( Grade where ReqMPs <= MPs and ReqSPs <= SPs ) : {ReqMPs := MAX ( ReqMPs ) } } ) } );

ANNOUNCE 'var Promotions';
VAR Promotions VIRTUAL ( NewGrades join ( GradeBeforeLatest { ALL BUT MPs , SPs } ) ) where PrevGrade <> Grade;

ANNOUNCE 'var DTYforCP';
VAR DTYforCP REAL RELATION {DTY# INTEGER, Problem# CHARACTER} KEY {DTY#} KEY {Problem#};
DTYforCP := RELATION {
	TUPLE {DTY# 65, Problem# "071 "},
	TUPLE {DTY# 66, Problem# "082a"},
	TUPLE {DTY# 67, Problem# "072 "},
	TUPLE {DTY# 68, Problem# "074 "},
	TUPLE {DTY# 69, Problem# "076 "},
	TUPLE {DTY# 70, Problem# "079 "},
	TUPLE {DTY# 71, Problem# "073 "},
	TUPLE {DTY# 72, Problem# "082 "},
	TUPLE {DTY# 74, Problem# "086 "},
	TUPLE {DTY# 75, Problem# "077 "},
	TUPLE {DTY# 76, Problem# "080 "},
	TUPLE {DTY# 77, Problem# "075 "},
	TUPLE {DTY# 79, Problem# "083 "},
	TUPLE {DTY# 81, Problem# "081 "},
	TUPLE {DTY# 82, Problem# "088 "},
	TUPLE {DTY# 83, Problem# "084 "},
	TUPLE {DTY# 84, Problem# "087 "},
	TUPLE {DTY# 85, Problem# "090 "},
	TUPLE {DTY# 86, Problem# "085 "},
	TUPLE {DTY# 87, Problem# "091 "},
	TUPLE {DTY# 97, Problem# "089 "},
	TUPLE {DTY# 99, Problem# "092 "},
	TUPLE {DTY# 133, Problem# "094a"}
}
;

ANNOUNCE 'var DTYcomposer';
VAR DTYcomposer REAL RELATION {DTY# INTEGER, FirstName CHARACTER, LastName CHARACTER} KEY {DTY#, FirstName, LastName};
DTYcomposer := RELATION {
	TUPLE {DTY# 78, FirstName "Paolo", LastName "Treossi"},
	TUPLE {DTY# 88, FirstName "Paolo", LastName "Treossi"},
	TUPLE {DTY# 89, FirstName "Paolo", LastName "Treossi"},
	TUPLE {DTY# 90, FirstName "Paolo", LastName "Treossi"},
	TUPLE {DTY# 91, FirstName "Paolo", LastName "Treossi"},
	TUPLE {DTY# 92, FirstName "Paolo", LastName "Treossi"},
	TUPLE {DTY# 93, FirstName "Paolo", LastName "Treossi"},
	TUPLE {DTY# 94, FirstName "Paolo", LastName "Treossi"},
	TUPLE {DTY# 95, FirstName "Paolo", LastName "Treossi"},
	TUPLE {DTY# 96, FirstName "Paolo", LastName "Treossi"},
	TUPLE {DTY# 98, FirstName "Steve", LastName "Bloom"},
	TUPLE {DTY# 100, FirstName "Paolo", LastName "Treossi"},
	TUPLE {DTY# 101, FirstName "Paolo", LastName "Treossi"},
	TUPLE {DTY# 102, FirstName "Jean-Marc", LastName "Bihl"},
	TUPLE {DTY# 103, FirstName "Paolo", LastName "Treossi"},
	TUPLE {DTY# 104, FirstName "Paolo", LastName "Treossi"},
	TUPLE {DTY# 105, FirstName "Vincent", LastName "Labbé"},
	TUPLE {DTY# 106, FirstName "Paolo", LastName "Treossi"},
	TUPLE {DTY# 107, FirstName "Hugh", LastName "Darwen"},
	TUPLE {DTY# 107, FirstName "Paolo", LastName "Treossi"},
	TUPLE {DTY# 108, FirstName "Paolo", LastName "Treossi"},
	TUPLE {DTY# 109, FirstName "Ian", LastName "Budden"},
	TUPLE {DTY# 110, FirstName "Vincent", LastName "Labbé"},
	TUPLE {DTY# 111, FirstName "Hugh", LastName "Darwen"},
	TUPLE {DTY# 111, FirstName "Steve", LastName "Bloom"},
	TUPLE {DTY# 112, FirstName "Steve", LastName "Bloom"},
	TUPLE {DTY# 113, FirstName "Wim van der", LastName "Zijden"},
	TUPLE {DTY# 114, FirstName "Hugh", LastName "Darwen"},
	TUPLE {DTY# 115, FirstName "Ian", LastName "Budden"},
	TUPLE {DTY# 116, FirstName "F.Y.", LastName "Sing"},
	TUPLE {DTY# 117, FirstName "Steve", LastName "Bloom"},
	TUPLE {DTY# 118, FirstName "Steve", LastName "Bloom"},
	TUPLE {DTY# 119, FirstName "Jean-Marc", LastName "Bihl"},
	TUPLE {DTY# 120, FirstName "Steve", LastName "Bloom"},
	TUPLE {DTY# 121, FirstName "Steve", LastName "Bloom"},
	TUPLE {DTY# 122, FirstName "Steve", LastName "Bloom"},
	TUPLE {DTY# 123, FirstName "Sebastian", LastName "Nowacki"},
	TUPLE {DTY# 124, FirstName "Paolo", LastName "Treossi"},
	TUPLE {DTY# 125, FirstName "Paolo", LastName "Treossi"},
	TUPLE {DTY# 126, FirstName "Paolo", LastName "Treossi"},
	TUPLE {DTY# 127, FirstName "Paolo", LastName "Treossi"},
	TUPLE {DTY# 128, FirstName "Paolo", LastName "Treossi"},
	TUPLE {DTY# 129, FirstName "Paolo", LastName "Treossi"},
	TUPLE {DTY# 130, FirstName "Steve", LastName "Bloom"},
	TUPLE {DTY# 131, FirstName "Paolo", LastName "Treossi"},
	TUPLE {DTY# 132, FirstName "Paolo", LastName "Treossi"},
	TUPLE {DTY# 134, FirstName "Sebastian", LastName "Nowacki"},
	TUPLE {DTY# 135, FirstName "Jean-Marc", LastName "Bihl"},
	TUPLE {DTY# 136, FirstName "Leigh", LastName "Matheson"},
	TUPLE {DTY# 137, FirstName "Leslie", LastName "Cass"},
	TUPLE {DTY# 138, FirstName "Leslie", LastName "Cass"},
	TUPLE {DTY# 139, FirstName "Paolo", LastName "Treossi"},
	TUPLE {DTY# 140, FirstName "Paolo", LastName "Treossi"}
}
;

ANNOUNCE 'var dtyprobx';
VAR dtyprobx REAL RELATION {DTY# INTEGER, NumberOfComposers INTEGER, date CHARACTER} KEY {DTY#};
dtyprobx := RELATION {
	TUPLE {DTY# 65, NumberOfComposers 1, date "2010-07"},
	TUPLE {DTY# 66, NumberOfComposers 1, date "2010-05"},
	TUPLE {DTY# 67, NumberOfComposers 1, date "2010-05"},
	TUPLE {DTY# 68, NumberOfComposers 1, date "2010-05"},
	TUPLE {DTY# 69, NumberOfComposers 1, date "2010-05"},
	TUPLE {DTY# 70, NumberOfComposers 1, date "2010-05"},
	TUPLE {DTY# 71, NumberOfComposers 1, date "2010-06"},
	TUPLE {DTY# 72, NumberOfComposers 1, date "2010-07"},
	TUPLE {DTY# 74, NumberOfComposers 1, date "2010-03"},
	TUPLE {DTY# 75, NumberOfComposers 1, date "2010-08"},
	TUPLE {DTY# 76, NumberOfComposers 1, date "2010-07"},
	TUPLE {DTY# 77, NumberOfComposers 1, date "2010-08"},
	TUPLE {DTY# 78, NumberOfComposers 1, date "2010-09"},
	TUPLE {DTY# 79, NumberOfComposers 1, date "2010-10"},
	TUPLE {DTY# 81, NumberOfComposers 1, date "2010-10"},
	TUPLE {DTY# 82, NumberOfComposers 1, date "2010-06"},
	TUPLE {DTY# 83, NumberOfComposers 1, date "2010-12"},
	TUPLE {DTY# 84, NumberOfComposers 1, date "2011-01"},
	TUPLE {DTY# 85, NumberOfComposers 1, date "2011-01"},
	TUPLE {DTY# 86, NumberOfComposers 1, date "2011-01"},
	TUPLE {DTY# 87, NumberOfComposers 1, date "2011-01"},
	TUPLE {DTY# 88, NumberOfComposers 1, date "2011-01"},
	TUPLE {DTY# 89, NumberOfComposers 1, date "2011-02"},
	TUPLE {DTY# 90, NumberOfComposers 1, date "2011-02"},
	TUPLE {DTY# 91, NumberOfComposers 1, date "2011-02"},
	TUPLE {DTY# 92, NumberOfComposers 1, date "2011-02"},
	TUPLE {DTY# 93, NumberOfComposers 1, date "2011-02"},
	TUPLE {DTY# 94, NumberOfComposers 1, date "2011-02"},
	TUPLE {DTY# 95, NumberOfComposers 1, date "2011-02"},
	TUPLE {DTY# 96, NumberOfComposers 1, date "2011-03"},
	TUPLE {DTY# 97, NumberOfComposers 1, date "2011-10"},
	TUPLE {DTY# 98, NumberOfComposers 1, date "2011-04"},
	TUPLE {DTY# 99, NumberOfComposers 1, date "2011-09"},
	TUPLE {DTY# 100, NumberOfComposers 1, date "2011-02"},
	TUPLE {DTY# 101, NumberOfComposers 1, date "2011-02"},
	TUPLE {DTY# 102, NumberOfComposers 1, date "2011-10"},
	TUPLE {DTY# 103, NumberOfComposers 1, date "2011-02"},
	TUPLE {DTY# 104, NumberOfComposers 1, date "2011-02"},
	TUPLE {DTY# 105, NumberOfComposers 1, date "2011-11"},
	TUPLE {DTY# 106, NumberOfComposers 1, date "2011-02"},
	TUPLE {DTY# 107, NumberOfComposers 2, date "2011-02"},
	TUPLE {DTY# 108, NumberOfComposers 1, date "2011-02"},
	TUPLE {DTY# 109, NumberOfComposers 1, date "2012-01"},
	TUPLE {DTY# 110, NumberOfComposers 1, date "2012-01"},
	TUPLE {DTY# 111, NumberOfComposers 2, date "2012-01"},
	TUPLE {DTY# 112, NumberOfComposers 1, date "2012-02"},
	TUPLE {DTY# 113, NumberOfComposers 1, date "2012-02"},
	TUPLE {DTY# 114, NumberOfComposers 1, date "2012-04"},
	TUPLE {DTY# 115, NumberOfComposers 1, date "2012-05"},
	TUPLE {DTY# 116, NumberOfComposers 1, date "2012-05"},
	TUPLE {DTY# 117, NumberOfComposers 1, date "2012-08"},
	TUPLE {DTY# 118, NumberOfComposers 1, date "2012-09"},
	TUPLE {DTY# 119, NumberOfComposers 1, date "2012-07"},
	TUPLE {DTY# 120, NumberOfComposers 1, date "2012-10"},
	TUPLE {DTY# 121, NumberOfComposers 1, date "2012-09"},
	TUPLE {DTY# 122, NumberOfComposers 1, date "2012-07"},
	TUPLE {DTY# 123, NumberOfComposers 1, date "2011-03"},
	TUPLE {DTY# 124, NumberOfComposers 1, date "2011-03"},
	TUPLE {DTY# 125, NumberOfComposers 1, date "2011-03"},
	TUPLE {DTY# 126, NumberOfComposers 1, date "2011-03"},
	TUPLE {DTY# 127, NumberOfComposers 1, date "2011-03"},
	TUPLE {DTY# 128, NumberOfComposers 1, date "2011-03"},
	TUPLE {DTY# 129, NumberOfComposers 1, date "2011-03"},
	TUPLE {DTY# 130, NumberOfComposers 1, date "2012-02"},
	TUPLE {DTY# 131, NumberOfComposers 1, date "2011-03"},
	TUPLE {DTY# 132, NumberOfComposers 1, date "2011-03"},
	TUPLE {DTY# 133, NumberOfComposers 1, date "2012-10"},
	TUPLE {DTY# 134, NumberOfComposers 1, date "2011-01"},
	TUPLE {DTY# 135, NumberOfComposers 1, date "2012-03"}
}
;

ANNOUNCE 'var DTYprob';
VAR DTYprob REAL RELATION {DTY# INTEGER, NumberOfComposers INTEGER, date CHARACTER, Status CHARACTER} KEY {DTY#};
DTYprob := RELATION {
	TUPLE {DTY# 65, NumberOfComposers 0, date "2010-07", Status "G"},
	TUPLE {DTY# 66, NumberOfComposers 0, date "2010-05", Status "G"},
	TUPLE {DTY# 67, NumberOfComposers 0, date "2010-05", Status "G"},
	TUPLE {DTY# 68, NumberOfComposers 0, date "2010-05", Status "G"},
	TUPLE {DTY# 69, NumberOfComposers 0, date "2010-05", Status "G"},
	TUPLE {DTY# 70, NumberOfComposers 0, date "2010-05", Status "G"},
	TUPLE {DTY# 71, NumberOfComposers 0, date "2010-06", Status "G"},
	TUPLE {DTY# 72, NumberOfComposers 0, date "2010-07", Status "G"},
	TUPLE {DTY# 74, NumberOfComposers 0, date "2010-03", Status "G"},
	TUPLE {DTY# 75, NumberOfComposers 0, date "2010-08", Status "G"},
	TUPLE {DTY# 76, NumberOfComposers 0, date "2010-07", Status "G"},
	TUPLE {DTY# 77, NumberOfComposers 0, date "2010-08", Status "G"},
	TUPLE {DTY# 78, NumberOfComposers 1, date "2010-09", Status "G"},
	TUPLE {DTY# 79, NumberOfComposers 0, date "2010-10", Status "G"},
	TUPLE {DTY# 81, NumberOfComposers 0, date "2010-10", Status "G"},
	TUPLE {DTY# 82, NumberOfComposers 0, date "2010-06", Status "G"},
	TUPLE {DTY# 83, NumberOfComposers 0, date "2010-12", Status "G"},
	TUPLE {DTY# 84, NumberOfComposers 0, date "2011-01", Status "G"},
	TUPLE {DTY# 85, NumberOfComposers 0, date "2011-01", Status "G"},
	TUPLE {DTY# 86, NumberOfComposers 0, date "2011-01", Status "G"},
	TUPLE {DTY# 87, NumberOfComposers 0, date "2011-01", Status "G"},
	TUPLE {DTY# 88, NumberOfComposers 1, date "2011-01", Status "G"},
	TUPLE {DTY# 89, NumberOfComposers 1, date "2011-02", Status "G"},
	TUPLE {DTY# 90, NumberOfComposers 1, date "2011-02", Status "G"},
	TUPLE {DTY# 91, NumberOfComposers 1, date "2011-02", Status "G"},
	TUPLE {DTY# 92, NumberOfComposers 1, date "2011-02", Status "G"},
	TUPLE {DTY# 93, NumberOfComposers 1, date "2011-02", Status "G"},
	TUPLE {DTY# 94, NumberOfComposers 1, date "2011-02", Status "G"},
	TUPLE {DTY# 95, NumberOfComposers 1, date "2011-02", Status "G"},
	TUPLE {DTY# 96, NumberOfComposers 1, date "2011-03", Status "G"},
	TUPLE {DTY# 97, NumberOfComposers 0, date "2011-10", Status "G"},
	TUPLE {DTY# 98, NumberOfComposers 1, date "2011-04", Status "G"},
	TUPLE {DTY# 99, NumberOfComposers 0, date "2011-09", Status "G"},
	TUPLE {DTY# 100, NumberOfComposers 1, date "2011-02", Status "G"},
	TUPLE {DTY# 101, NumberOfComposers 1, date "2011-02", Status "G"},
	TUPLE {DTY# 102, NumberOfComposers 1, date "2011-10", Status "G"},
	TUPLE {DTY# 103, NumberOfComposers 1, date "2011-02", Status "G"},
	TUPLE {DTY# 104, NumberOfComposers 1, date "2011-02", Status "G"},
	TUPLE {DTY# 105, NumberOfComposers 1, date "2011-11", Status "G"},
	TUPLE {DTY# 106, NumberOfComposers 1, date "2011-02", Status "G"},
	TUPLE {DTY# 107, NumberOfComposers 2, date "2011-02", Status "G"},
	TUPLE {DTY# 108, NumberOfComposers 1, date "2011-02", Status "G"},
	TUPLE {DTY# 109, NumberOfComposers 1, date "2012-01", Status "G"},
	TUPLE {DTY# 110, NumberOfComposers 1, date "2012-01", Status "G"},
	TUPLE {DTY# 111, NumberOfComposers 2, date "2012-01", Status "G"},
	TUPLE {DTY# 112, NumberOfComposers 1, date "2012-02", Status "G"},
	TUPLE {DTY# 113, NumberOfComposers 1, date "2012-02", Status "G"},
	TUPLE {DTY# 114, NumberOfComposers 1, date "2012-04", Status "G"},
	TUPLE {DTY# 115, NumberOfComposers 1, date "2012-05", Status "G"},
	TUPLE {DTY# 116, NumberOfComposers 1, date "2012-05", Status "G"},
	TUPLE {DTY# 117, NumberOfComposers 1, date "2012-08", Status "G"},
	TUPLE {DTY# 118, NumberOfComposers 1, date "2012-09", Status "G"},
	TUPLE {DTY# 119, NumberOfComposers 1, date "2012-07", Status "G"},
	TUPLE {DTY# 120, NumberOfComposers 1, date "2012-10", Status "G"},
	TUPLE {DTY# 121, NumberOfComposers 1, date "2012-09", Status "G"},
	TUPLE {DTY# 122, NumberOfComposers 1, date "2012-07", Status "G"},
	TUPLE {DTY# 123, NumberOfComposers 1, date "2011-03", Status "G"},
	TUPLE {DTY# 124, NumberOfComposers 1, date "2011-03", Status "G"},
	TUPLE {DTY# 125, NumberOfComposers 1, date "2011-03", Status "G"},
	TUPLE {DTY# 126, NumberOfComposers 1, date "2011-03", Status "G"},
	TUPLE {DTY# 127, NumberOfComposers 1, date "2011-03", Status "G"},
	TUPLE {DTY# 128, NumberOfComposers 1, date "2011-03", Status "G"},
	TUPLE {DTY# 129, NumberOfComposers 1, date "2011-03", Status "G"},
	TUPLE {DTY# 130, NumberOfComposers 1, date "2012-02", Status "?"},
	TUPLE {DTY# 131, NumberOfComposers 1, date "2011-03", Status "G"},
	TUPLE {DTY# 132, NumberOfComposers 1, date "2011-03", Status "E"},
	TUPLE {DTY# 133, NumberOfComposers 0, date "2012-10", Status "G"},
	TUPLE {DTY# 134, NumberOfComposers 1, date "2011-01", Status "?"},
	TUPLE {DTY# 135, NumberOfComposers 1, date "2012-03", Status "G"},
	TUPLE {DTY# 136, NumberOfComposers 1, date "2012-12", Status "E"},
	TUPLE {DTY# 137, NumberOfComposers 1, date "2012-03", Status "I"},
	TUPLE {DTY# 138, NumberOfComposers 1, date "2012-06", Status "E"},
	TUPLE {DTY# 139, NumberOfComposers 1, date "2012-12", Status "G"},
	TUPLE {DTY# 140, NumberOfComposers 1, date "2012-12", Status "E"}
}
;

ANNOUNCE 'operator SetStatus';
OPERATOR SetStatus(D# INTEGER, S CHARACTER); update DTYprob where DTY# = D# : { Status := S } ; end operator ;

ANNOUNCE 'operator PublishDTY';
OPERATOR PublishDTY(D# INTEGER, Diff INTEGER, P# CHARACTER, M CHARACTER, Y CHARACTER); insert Problem rel { tup { Problem# P# , DR Diff , Month M , Year Y , NumberOfComposers NumberOfComposers from tuple from ( DTYprob where DTY# = D# ) } } , insert ComposedBy ( extend DTYcomposer where DTY# = D# : {Problem# := P#} ) { all but DTY# } , delete DTYcomposer where DTY# = D# , update DTYprob where DTY# = D# : { NumberOfComposers := 0 } , insert DTYforCP rel { tup { DTY# D# , Problem# P# } } ; end operator ;

ANNOUNCE 'constraint RightNumberOfComposers';
CONSTRAINT RightNumberOfComposers IS_EMPTY ( Problem not matching summarize ComposedBy by { Problem# } : { NumberOfComposers := COUNT ( ) } );

ANNOUNCE 'constraint SolverNotComposer';
CONSTRAINT SolverNotComposer IS_EMPTY ( SolvedBy matching ComposedBy );

ANNOUNCE 'constraint SolverIsProblemist';
CONSTRAINT SolverIsProblemist IS_EMPTY ( SolvedBy not matching Problemist );

ANNOUNCE 'constraint ComposerIsProblemist';
CONSTRAINT ComposerIsProblemist IS_EMPTY ( ComposedBy not matching Problemist );

ANNOUNCE 'constraint DTYcomposerIsProblemist';
CONSTRAINT DTYcomposerIsProblemist IS_EMPTY ( DTYcomposer not matching Problemist );

ANNOUNCE 'constraint ComposedProbExists';
CONSTRAINT ComposedProbExists IS_EMPTY ( ComposedBy NOT MATCHING Problem );

ANNOUNCE 'constraint ProbExistsForDTY';
CONSTRAINT ProbExistsForDTY IS_EMPTY ( DTYforCP not matching Problem );

ANNOUNCE 'constraint DTYexists';
CONSTRAINT DTYexists IS_EMPTY ( DTYforCP not matching DTYprob );

ANNOUNCE 'constraint ComposedDTYprobExists';
CONSTRAINT ComposedDTYprobExists IS_EMPTY ( DTYcomposer NOT MATCHING DTYprob );

ANNOUNCE 'constraint RightNumberOfDTYcomposers';
CONSTRAINT RightNumberOfDTYcomposers IS_EMPTY ( DTYprob not matching summarize DTYcomposer per ( DTYprob { DTY# } ) : {NumberOfCompsers := COUNT ( ) } );

COMMIT;

/*** End of Rel Database Backup ***/
ANNOUNCE 'End of Script.';

