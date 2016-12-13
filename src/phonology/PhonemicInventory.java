package phonology;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class PhonemicInventory
{

	public static void main(String[] args)
	{
//		generateCatalog();
//		Phoneme[] catalog = loadCatalog();
		Phoneme[] catalog = makeCatalog();
		
		List<List<Phoneme>> inventory = new ArrayList<List<Phoneme>>();
		// HashMap<Phoneme, Integer> orthography = new HashMap<Phoneme, Integer>();
		List<Orthograph> orthography = new ArrayList<Orthograph>();
		ArrayList<Phoneme> masterList = new ArrayList<Phoneme>();
		
		String[] manners = new String[] {"vowel", "affricate", "approximant", "fricative",
											"nasal", "plosive"};
		
		double orthographicDeviancy = 1.0/3;
		
		Random rng = new Random();
		
		double expectedPhones = 20.507;
		expectedPhones = ((rng.nextGaussian() * 5) + 20.507);
		double arf = (expectedPhones - 20.507) / (46 - 20.507);
		
		for (int i = 0; i < 6; i++)
			inventory.add(new ArrayList<Phoneme>());
		
		int count = 0;
		
		// Populate inventory
		for (Phoneme p : catalog)
		{
			// Determine probability of current phoneme
			double prob = 1 - (1 - p.getFreq()) * (1 - arf);
			
			if (Math.random() < prob)
			{
				// Add the phoneme to this language
				for (int i = 0; i < 6; i++)
					if (p.getManner().equals(manners[i]))
					{
						inventory.get(i).add(p);
						masterList.add(p);
						count++;
					}
			}
		}
		
		System.out.println("INVENTORY");
		// Show results
		System.out.println(count + " sounds\t(Expected " + expectedPhones + ", arf = " + arf + ")");
		
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
		
		
		// ORTHOGRAPHY
		System.out.println("\nORTHOGRAPHY");
		for (Phoneme p : masterList)
		{
			for (int i = 0; i < p.getOrthog().length; )
			{
				// If we're on the last grapheme on the list, pick it
				if (i == p.getOrthog().length - 1)
				{
					orthography.add(new Orthograph(p, i));
					i = p.getOrthog().length;
				}
				
				// Otherwise, consider the next option
				else if (rng.nextDouble() < orthographicDeviancy)
					i++;
				else
				{
					orthography.add(new Orthograph(p, i));
					i = p.getOrthog().length;
				}
			}
		}
		
		Collections.sort(orthography);
		
		// Display results
		for (Orthograph o : orthography)
		{
			String entry = "/" + o.phoneme.getSymbol() + "/";
			if (o.phoneme.getSymbol().length() == 1)
				entry = entry + " ";
			
			System.out.print(entry + " ");
		}
		System.out.println();
		
		for (Orthograph o : orthography)
		{
			String entry = o.phoneme.getOrthog()[o.preferredOrthograph];
			if (entry.length() == 1)
				entry = entry + " ";
			entry = " " + entry + " ";
			
			System.out.print(entry + " ");
		}
		
		// Purify orthography
		Orthograph prev = null;
		ArrayList<Orthograph> duplicates = new ArrayList<Orthograph>();
		System.out.println();
		boolean changes = false;
		for (int i = 0; i < 5; i++)
		{
			System.out.print((i+1) + ":");
			changes = false;
			for (Orthograph curr : orthography)
			{
				if (prev != null && curr.compareTo(prev) == 0)
				{
					changes = true;
					System.out.print("\t" + prev.phoneme.getSymbol() + "/" + prev.phoneme.getOrthog()[prev.preferredOrthograph] + " --> ");
					prev.preferredOrthograph = (prev.preferredOrthograph + 1) % prev.phoneme.getOrthog().length;
					System.out.print(prev.phoneme.getOrthog()[prev.preferredOrthograph]);
					
//					System.out.print("\t" + curr.phoneme.getSymbol() + "/" + curr.phoneme.getOrthog()[curr.preferredOrthograph] + " --> ");
//					curr.preferredOrthograph = (curr.preferredOrthograph + 1) % curr.phoneme.getOrthog().length;
//					System.out.print(curr.phoneme.getOrthog()[curr.preferredOrthograph]);
				}
				prev = curr;
			}
			
			if (!changes)
			{
				i = 5;
				System.out.print("\tAll good!");
			}
			System.out.println();
		}
	}
	
	static class Orthograph implements Comparable<Orthograph>
	{
		Phoneme phoneme;
		int preferredOrthograph;
		
		public Orthograph (Phoneme phoneme, int grapheme)
		{
			this.phoneme = phoneme;
			this.preferredOrthograph = grapheme;
		}

		@Override
		public int compareTo(Orthograph other)
		{
			return phoneme.getOrthog()[preferredOrthograph].compareTo(other.phoneme.getOrthog()[other.preferredOrthograph]);
		}
	}
	
	public static Phoneme[] makeCatalog()
	{
		Phoneme[] catalog = new Phoneme[] {
				
				// Affricates
				new Consonant("tʃ",	0.459,	false,	false,	"palato-alveolar",	"sibilant",	"affricate",	new String[] {"ch", "c"}),
				new Consonant("ts",	0.308,	false,	false,	"alveolar", 		"sibilant",	"affricate",	new String[] {"z", "ts"}),
				new Consonant("dʒ",	0.250,	true,	false,	"palato-alveolar", 	"sibilant",	"affricate",	new String[] {"j"}),
				new Consonant("dz",	0.120,	true,	false,	"alveolar", 		"sibilant",	"affricate",	new String[] {"dz"}),
				
				// Approximants
				new Consonant("j",	0.836,	true,	false,	"palatal", 			"",			"approximant",	new String[] {"y", "j", "i"}),
				new Consonant("l",	0.761,	true,	false,	"alveolar", 		"lateral",	"approximant",	new String[] {"l"}),
				new Consonant("w",	0.734,	true,	false,	"labial-velar", 	"",			"approximant",	new String[] {"w", "u"}),
				new Consonant("r",	0.703,	true,	false,	"alveolar", 		"",			"trill",		new String[] {"r"}),
//				new Consonant("β̞",	0.043,	true,	false,	"bilabial", 		"",			"approximant",	new String[] {"v", "w"}),
				new Consonant("ʍ",	0.033,	false,	false,	"labial-velar",		"",			"approximant",	new String[] {"wh"}),
				
				// Fricatives
				new Consonant("s",	0.818,	false,	false,	"alveolar",			"sibilant",	"fricative",	new String[] {"s"}),
				new Consonant("h",	0.647,	false,	false,	"glottal",			"",			"fricative",	new String[] {"h"}),
				new Consonant("ʃ",	0.415,	false,	false,	"palato-alveolar",	"sibilant",	"fricative",	new String[] {"sh", "x"}),
				new Consonant("f",	0.399,	false,	false,	"labiodental",		"",			"fricative",	new String[] {"f"}),
				new Consonant("z",	0.271,	true,	false,	"alveolar",			"sibilant",	"fricative",	new String[] {"z"}),
				new Consonant("x",	0.270,	false,	false,	"velar",			"",			"fricative",	new String[] {"h", "kh", "ch"}),
				new Consonant("v",	0.211,	true,	false,	"labiodental",		"",			"fricative",	new String[] {"v"}),
				new Consonant("ɣ",	0.145,	true,	false,	"velar",			"",			"fricative",	new String[] {"g", "gh"}),
				new Consonant("ʒ",	0.135,	true,	false,	"palato-alveolar",	"sibilant",	"fricative",	new String[] {"zh", "j"}),
				new Consonant("β",	0.120,	true,	false,	"bilabial",			"",			"fricative",	new String[] {"v", "bh"}),
				new Consonant("ɸ",	0.120,	false,	false,	"bilabial",			"",			"fricative",	new String[] {"f", "ph"}),
				new Consonant("ð",	0.049,	true,	false,	"dental",			"sibilant",	"fricative",	new String[] {"th", "dh"}),
				new Consonant("θ",	0.040,	false,	false,	"dental",			"sibilant",	"fricative",	new String[] {"th"}),
				
				// Nasals
				new Consonant("n",	0.956,	true,	false,	"alveolar",			"",			"nasal",	new String[] {"n"}),
				new Consonant("m",	0.940,	true,	false,	"bilabial",			"",			"nasal",	new String[] {"m"}),
				new Consonant("ŋ",	0.525,	true,	false,	"velar",			"",			"nasal",	new String[] {"ng"}),
				new Consonant("m_",	0.038,	false,	false,	"bilabial",			"",			"nasal",	new String[] {"mh"}),
				new Consonant("n_",	0.020,	false,	false,	"alveolar",			"",			"nasal",	new String[] {"nh"}),
				
				// Plosive
				new Consonant("k",	0.920,	false,	false,	"velar",			"",			"plosive",	new String[] {"k", "c", "q"}),
				new Consonant("p",	0.856,	false,	false,	"bilabial",			"",			"plosive",	new String[] {"p"}),
				new Consonant("t",	0.756,	false,	false,	"alveolar",			"",			"plosive",	new String[] {"t"}),
				new Consonant("b",	0.665,	true,	false,	"bilabial",			"",			"plosive",	new String[] {"b"}),
				new Consonant("d",	0.643,	true,	false,	"alveolar",			"",			"plosive",	new String[] {"d"}),
				new Consonant("g",	0.563,	true,	false,	"velar",			"",			"plosive",	new String[] {"g"}),
//				new Consonant("ʔ",	0.479,	false,	false,	"glottal",			"",			"plosive",	new String[] {"'"}),
				new Consonant("tʰ",	0.246,	false,	true,	"alveolar",			"",			"plosive",	new String[] {"t", "th"}),
				new Consonant("kʰ",	0.228,	false,	true,	"velar",			"",			"plosive",	new String[] {"k", "kh"}),
				new Consonant("pʰ",	0.224,	false,	true,	"bilabial",			"",			"plosive",	new String[] {"p", "ph"}),
				new Consonant("q",	0.140,	false,	false,	"uvular",			"",			"plosive",	new String[] {"q"}),
				new Consonant("qʰ",	0.038,	false,	false,	"uvular",			"",			"plosive",	new String[] {"q", "qh"}),
				
				new Vowel	 ("i",	0.978,	"high",			"front",	false,	new String[] {"i"}),
				new Vowel    ("ä",	0.958,	"low",			"central",	false,	new String[] {"a"}),
				new Vowel	 ("u",	0.933,	"high",			"back",		true,	new String[] {"u"}),
				new Vowel	 ("ɛ",	0.871,	"lower mid",	"front",	false,	new String[] {"e"}),
				new Vowel	 ("o",	0.856,	"mid",			"back",		false,	new String[] {"o"}),
				new Vowel	 ("ə",	0.213,	"mid",			"central",	false,	new String[] {"'", "u", "e"}),
				new Vowel	 ("y",	0.053,	"high",			"front",	true,	new String[] {"u", "y"})
			};
		
		return catalog;
	}
	
	public static void generateCatalog()
	{
		// Populate catalog
		Phoneme[] catalog = makeCatalog();
		
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
	protected String[] orthog;
	protected double freq;
	
	public Phoneme(String symbol, String manner, double freq, String[] orthog)
	{
		this.symbol = symbol;
		this.manner = manner;
		this.freq = freq;
		this.orthog = orthog;
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
	
	public String[] getOrthog() {
		return orthog;
	}
}

class Consonant extends Phoneme
{
	private boolean voiced, aspirated; 
	private String place, modifier;

	public Consonant(String symbol, double freq, boolean voiced, boolean aspirated, String place, String modifier, String manner, String[] orthog)
	{
		super(symbol, manner, freq, orthog);
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

	public Vowel(String symbol, double freq, String height, String place, boolean rounded, String[] orthog) {
		super(symbol, "vowel", freq, orthog);
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
