package phonology;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class PhonemicInventory
{

	public static void main(String[] args)
	{
		// generateCatalog();
		Phoneme[] catalog = loadCatalog();
		
		List<List<Phoneme>> inventory = new ArrayList<List<Phoneme>>();
		
		String[] manners = new String[] {"vowel", "affricate", "approximant", "fricative",
											"nasal", "plosive"};
		
		for (int i = 0; i < 6; i++)
			inventory.add(new ArrayList<Phoneme>());
		
		// Populate inventory
		for (Phoneme p : catalog)
		{
			if (Math.random() < p.getFreq())
			{
				// Add the phoneme to this language
				for (int i = 0; i < 6; i++)
					if (p.getManner().equals(manners[i]))
						inventory.get(i).add(p);
			}
		}
		
		// Show contents of inventory
		for (int i = 0; i < 6; i++)
		{
			ArrayList<Phoneme> manner = (ArrayList<Phoneme>) inventory.get(i);
			
			String label = manners[i];
			if (label.length() < 8)
				label += "\t";
			label += "\t";
			System.out.print(label);
			
			for (Phoneme p : manner)
				System.out.print(p.getSymbol() + " ");
			
			System.out.println();
		}
	}
	
	public static void generateCatalog()
	{
		// Populate catalog
		Phoneme[] catalog = new Phoneme[] {
				// Affricates
				new Consonant("t͡ʃ",	0.459,	false,	false,	"palato-alveolar",	"sibilant",	"affricate"		),
				new Consonant("t͡s",	0.308,	false,	false,	"alveolar", 		"sibilant",	"affricate"		),
				new Consonant("d͡ʒ",	0.250,	true,	false,	"palato-alveolar", 	"sibilant",	"affricate"		),
				new Consonant("d͡z",	0.120,	true,	false,	"alveolar", 		"sibilant",	"affricate"		),
				
				// Approximants
				new Consonant("j",	0.836,	true,	false,	"palatal", 			"",			"approximant"	),
				new Consonant("l",	0.761,	true,	false,	"alveolar", 		"lateral",	"approximant"	),
				new Consonant("w",	0.734,	true,	false,	"labial-velar", 	"",			"approximant"	),
				new Consonant("r",	0.703,	true,	false,	"alveolar", 		"",			"trill"			),
				new Consonant("β̞",	0.043,	true,	false,	"bilabial", 		"",			"approximant"	),
				new Consonant("ʍ",	0.033,	false,	false,	"labial-velar",		"",			"approximant"	),
				
				// Fricatives
				new Consonant("s",	0.818,	false,	false,	"alveolar",			"sibilant",	"fricative"		),
				new Consonant("h",	0.647,	false,	false,	"glottal",			"",			"fricative"		),
				new Consonant("ʃ",	0.415,	false,	false,	"palato-alveolar",	"sibilant",	"fricative"		),
				new Consonant("f",	0.399,	false,	false,	"labiodental",		"",			"fricative"		),
				new Consonant("z",	0.271,	true,	false,	"alveolar",			"sibilant",	"fricative"		),
				new Consonant("x",	0.270,	false,	false,	"velar",			"",			"fricative"		),
				new Consonant("v",	0.211,	true,	false,	"labiodental",		"",			"fricative"		),
				new Consonant("ɣ",	0.145,	true,	false,	"velar",			"",			"fricative"		),
				new Consonant("ʒ",	0.135,	true,	false,	"palato-alveolar",	"sibilant",	"fricative"		),
				new Consonant("β",	0.120,	true,	false,	"bilabial",			"",			"fricative"		),
				new Consonant("ɸ",	0.120,	false,	false,	"bilabial",			"",			"fricative"		),
				new Consonant("ð",	0.049,	true,	false,	"dental",			"sibilant",	"fricative"		),
				new Consonant("θ",	0.040,	false,	false,	"dental",			"sibilant",	"fricative"		),
				
				// Nasals
				new Consonant("n",	0.956,	true,	false,	"alveolar",			"",			"nasal"			),
				new Consonant("m",	0.940,	true,	false,	"bilabial",			"",			"nasal"			),
				new Consonant("ŋ",	0.525,	true,	false,	"velar",			"",			"nasal"			),
				new Consonant("m̥",	0.038,	false,	false,	"bilabial",			"",			"nasal"			),
				new Consonant("n̥",	0.020,	false,	false,	"alveolar",			"",			"nasal"			),
				
				// Plosive
				new Consonant("k",	0.920,	false,	false,	"velar",			"",			"plosive"		),
				new Consonant("p",	0.856,	false,	false,	"bilabial",			"",			"plosive"		),
				new Consonant("t",	0.756,	false,	false,	"alveolar",			"",			"plosive"		),
				new Consonant("b",	0.665,	true,	false,	"bilabial",			"",			"plosive"		),
				new Consonant("d",	0.643,	true,	false,	"alveolar",			"",			"plosive"		),
				new Consonant("g",	0.563,	true,	false,	"velar",			"",			"plosive"		),
				new Consonant("ʔ",	0.479,	false,	false,	"glottal",			"",			"plosive"		),
				new Consonant("tʰ",	0.246,	false,	true,	"alveolar",			"",			"plosive"		),
				new Consonant("kʰ",	0.228,	false,	true,	"velar",			"",			"plosive"		),
				new Consonant("pʰ",	0.224,	false,	true,	"bilabial",			"",			"plosive"		),
				new Consonant("q",	0.140,	false,	false,	"uvular",			"",			"plosive"		),
				new Consonant("qʰ",	0.038,	false,	false,	"uvular",			"",			"plosive"		),
				
				new Vowel	 ("i",	0.978,	"high",			"front",	false),
				new Vowel    ("ä",	0.958,	"low",			"central",	false),
				new Vowel	 ("u",	0.933,	"high",			"back",		true),
				new Vowel	 ("ɛ",	0.871,	"lower mid",	"front",	false),
				new Vowel	 ("o̞",	0.856,	"mid",			"back",		false),
				new Vowel	 ("ə",	0.213,	"mid",			"central",	false),
				new Vowel	 ("y",	0.053,	"high",			"front",	true)
			};
		
		// Write catalog to file
		try {
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("phonemes.dat"));
			oos.writeObject(catalog);
			oos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static Phoneme[] loadCatalog()
	{
		try {
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream("phonemes.dat"));
			Phoneme[] catalog = (Phoneme[]) ois.readObject();
			ois.close();
			return catalog;
		} catch (FileNotFoundException e) {
			System.out.println("Couldn't find file \"phonemes.dat\"");
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		return null;
	}
}



class Phoneme implements Serializable
{
	protected String symbol, manner;
	protected double freq;
	
	public Phoneme(String symbol, String manner, double freq)
	{
		this.symbol = symbol;
		this.manner = manner;
		this.freq = freq;
	}

	public String getSymbol() {
		return symbol;
	}

	public String getManner() {
		return manner;
	}

	public double getFreq() {
		return freq;
	}
}

class Consonant extends Phoneme
{
	private boolean voiced, aspirated; 
	private String place, modifier;

	public Consonant(String symbol, double freq, boolean voiced, boolean aspirated, String place, String modifier, String manner)
	{
		super(symbol, manner, freq);
		this.voiced = voiced;
		this.aspirated = aspirated;
		this.place = place;
		this.modifier = modifier;
	}

	@Override
	public String toString()
	{
		String result = symbol + "\t";
		
		if (voiced)
			result += "voiced ";
		else
			result += "voiceless ";
		
		if (aspirated)
			result += "aspirated ";
		
		result += place + " ";
		
		if (!modifier.equals(""))
			result += modifier + " ";
		
		result += manner;
			
		return result;
	}
	
	
}

class Vowel extends Phoneme
{
	private String height, place;
	private boolean rounded;

	public Vowel(String symbol, double freq, String height, String place, boolean rounded) {
		super(symbol, "vowel", freq);
		this.height = height;
		this.place = place;
		this.rounded = rounded;
	}

	@Override
	public String toString()
	{
		String result = symbol + "\t" + height + " " + place + " ";
		if (rounded)
			result += "rounded ";
		else
			result += "unrounded ";
		
		result += manner;
			
		return result;
	}
}
