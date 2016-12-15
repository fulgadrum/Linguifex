package phonology;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;

import java.util.Iterator;

public class Phonology
{
	static Random rng;
	static Phoneme[] catalog;
	static HashMap<Integer, Integer> counts;
	static String[] manners;
	
	List<List<Phoneme>> inventory;
	List<Orthograph> masterList;
	List<Orthograph> orthography;
	private double[][] transitionProbability;
	private HashMap<String, Integer> mannerMap;
	private ArrayList<List<Orthograph>> categories;
	
	
	public static void main(String[] args)
	{
//		generateCatalog();
//		Phoneme[] catalog = loadCatalog();
		
		Phonology.makeCatalog();
		
		
		for (int i = 0; i < 1; i++)
		{
			Phonology phonology = new Phonology();
			phonology.MakeInventory();
//			phonology.printInventory();
			phonology.MakeOrthography();
			
				phonology.MakePhonotactics();
		}
		
		
		
		counts = new HashMap<Integer, Integer>(20);
	}
	
	public Phonology()
	{
		if (rng == null)
			rng = new Random();
		
		if (manners == null)
			manners = new String[] {"initial s", "unvoiced plosive", "voiced plosive", 
									"affricate", "unvoiced fricative", "voiced fricative",
									"nasal", "L", "R", "glide", "other", "vowel"};
		
		makeCatalog();
	}
	
	static void LangTest()
	{
		for (int i = 0; i < 21; i++)
		{
			counts.put(i, 0);
		}
		
		for (int i = 0; i < 10000; i++)
		{
			Phonology phonology = new Phonology();
			
			int result = phonology.MakeOrthography();
			counts.put(result, counts.get(result) + 1);
		}
		
		Iterator<Entry<Integer, Integer>> itr = counts.entrySet().iterator();
		while (itr.hasNext())
		{
			Entry<Integer, Integer> next = itr.next();
			System.out.println(next.getKey() + ":\t" + next.getValue());
		}
	}
	
	public void MakeInventory()
	{
		inventory = new ArrayList<List<Phoneme>>();
		for (int i = 0; i < manners.length; i++)
			inventory.add(new ArrayList<Phoneme>());
		
		masterList = new ArrayList<Orthograph>();
		
		
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
				inventory.get(p.getManner()).add(p);
				masterList.add(new Orthograph(p, 0));
				count++;
				
				// Add the phoneme to this language
//				for (int i = 0; i < manners.length; i++)
//					if (p.getManner().equals(manners[i]))
//					{
//						inventory.get(i).add(p);
//						
//					}
			}
		}
	}
	
	public int MakeOrthography()
	{
		// ORTHOGRAPHY
		orthography = new ArrayList<Orthograph>();
		double orthographicDeviancy = 1.0/3;	// The odds of using an alternate orthograph
		
		System.out.println("\nINITIAL ORTHOGRAPHY");
		for (Orthograph o : masterList)
		{
			Phoneme p = o.phoneme;
			for (int i = 0; i < p.getOrthog().length; )
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
		
		// Sort results
		Collections.sort(orthography);
		
		// Display results - phonemes
		for (Orthograph o : orthography)
		{
			String entry = "/" + o.phoneme.getSymbol() + "/";
			if (o.phoneme.getSymbol().length() == 1)
				entry = entry + " ";
			
			System.out.print(entry + " ");
		}
		System.out.println();
		
		// Display results - representations
		for (Orthograph o : orthography)
		{
			String entry = o.phoneme.getOrthog()[o.preferredOrthograph];
			if (entry.length() == 1)
				entry = entry + " ";
			entry = " " + entry + " ";
			
			System.out.print(entry + " ");
		}
		
		int iterations;
		
		// Purify orthography
		Orthograph prev = null;
		HashSet<Orthograph> duplicates = new HashSet<Orthograph>();
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
					
					System.out.print("\t" + curr.phoneme.getSymbol() + "/" + curr.phoneme.getOrthog()[curr.preferredOrthograph] + " --> ");
					curr.preferredOrthograph = (curr.preferredOrthograph + 1) % curr.phoneme.getOrthog().length;
					System.out.print(curr.phoneme.getOrthog()[curr.preferredOrthograph]);
				}
				prev = curr;
			}
			
			if (!changes)
			{
				i = 5;
				System.out.print("\tAll good!");
				iterations = i;
			}
			else
				Collections.sort(orthography);
			System.out.println();
		}
		iterations = 999;
		
		System.out.println("\nCORRECTED ORTHOGRAPHY");
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
		
		return iterations;
	}
	
	public void MakePhonotactics()
	{
		String onset = "C", nucleus = "V", coda = "";
	
		double onsetRequiredChance = 	0.3;
		double restrictOnsetPhonemesChance = 0.25;
		double restrictCodaPhonemesChance = 0.5;
		
		boolean onsetRequired = false;
		double restrictOnsetPhonemes = 1;
		double restrictCodaPhonemes = 1;
		
		if (rng.nextDouble() < onsetRequiredChance)
		{
			onsetRequired = true;
			onset = "C";
		}
		
		if (rng.nextDouble() < restrictOnsetPhonemesChance)
			restrictOnsetPhonemes = Math.max(Math.sqrt(rng.nextDouble()), 0.25);
		
		if (rng.nextDouble() < restrictCodaPhonemesChance)
			restrictCodaPhonemes = Math.sqrt(rng.nextDouble());
		
		System.out.println("\n" + restrictOnsetPhonemes);
		
		
		// Allowed phonemes in onset list
		ArrayList<Orthograph> allowedInOnset = new ArrayList<Orthograph>();
		
		double total = 0;
		while (total == 0)
		{
			for (int i = 0; i < masterList.size() - inventory.get(11).size(); i++)
			{
				if (rng.nextDouble() < restrictOnsetPhonemes)
				{
					allowedInOnset.add(masterList.get(i));
					masterList.get(i).onsetFreq = Math.max(0.5 + rng.nextGaussian() * 0.25, 0.001);
					total += masterList.get(i).onsetFreq; 
				}		
			}
		}
		
		for (int i = 0; i < allowedInOnset.size(); i++)
		{
			Orthograph o = allowedInOnset.get(i); 
			o.onsetFreq /= total;
			System.out.println(o.phoneme.getSymbol() + "\t" + o.onsetFreq);
		}
		
		String[] pManner = new String[] {"initial s", "unvoiced plosive", "voiced plosive", 
										 "affricate", "unvoiced fricative", "voiced fricative",
										 "nasal", "L", "R", "glide", "other"};
		
		categories = new ArrayList<List<Orthograph>>();
		for (int i = 0; i < pManner.length; i++)
			categories.add(new ArrayList<Orthograph>());
		
		for (int i = 0; i < allowedInOnset.size(); i++)
		{
			Orthograph o = allowedInOnset.get(i);
			Phoneme p = allowedInOnset.get(i).phoneme;
			
			if (p.getManner() != 11)
				categories.get(p.getManner()).add(o);
			
//			if (p.getSymbol().equals("s"))
//				categories.get(0).add(o);
//			
//			int dest = -1;
//			
//			if (p.getManner().equals("plosive"))
//				if (((Consonant) p).getVoiced())
//					dest = 2;
//				else
//					dest = 1;
//			
//			else if (p.getManner().equals("affricate"))
//				dest = 3;
//			
//			else if (p.getManner().equals("fricative"))
//				if (((Consonant) p).getVoiced())
//					dest = 4;
//				else
//					dest = 5;
//			
//			else if (p.getManner().equals("nasal"))
//				dest = 6;
//		
//			else if (p.getManner().equals("L"))
//				dest = 7;
//			
//			else if (p.getManner().equals("R"))
//				dest = 8;
//			
//			else if (p.getManner().equals("glide"))
//				dest = 9;
//			
//			else if (p.getManner().equals("other"))
//				dest = 10;
//			
//			if (!p.getManner().equals("vowel"))
//				categories.get(dest).add(o);
		}
		
		for (int i = 0; i < categories.size(); i++)
		{
			System.out.print(pManner[i] + "\t\t\t");
			for (int j = 0; j < categories.get(i).size(); j++)
				System.out.print(categories.get(i).get(j).phoneme.getSymbol() + " ");
				
			System.out.println();
		}
		
		
		
		
		transitionProbability = new double[][] {
				{0, 1, 0, 0, 0, 0, 1, 0, 0, 0},	// s
				{0, 0, 0, 0, 0, 0, 0, 1, 1, 1},	// unvoiced plosives
				{0, 0, 0, 0, 0, 0, 0, 1, 1, 1},	// voiced plosives
				{0, 0, 0, 0, 0, 0, 0, 0, 0, 1},	// affricates
				{0, 0, 0, 0, 0, 0, 0, 1, 1, 1},	// unvoiced fricatives
				{0, 0, 0, 0, 0, 0, 0, 0, 1, 1},	// voiced fricatives
				{0, 0, 0, 0, 0, 0, 0, 0, 0, 1},	// nasals
				{0, 0, 0, 0, 0, 0, 0, 0, 0, 1},	// L's
				{0, 0, 0, 0, 0, 0, 0, 0, 0, 1},	// R's
				{0, 0, 0, 0, 0, 0, 0, 0, 0, 0},	// glides
				{0, 0, 0, 0, 0, 0, 0, 0, 0, 0}	// others
		};
		
		// Mapping of manner strings to numbers
		mannerMap = new HashMap<String, Integer>();
		for (int i = 0; i < pManner.length; i++)
		{
			mannerMap.put(pManner[i], i);
		}
		
		
		for (int i = 0; i < allowedInOnset.size(); i++)
		{
			Phoneme p = allowedInOnset.get(i).phoneme;
			System.out.print (p.getSymbol() + " ");
		}
		System.out.println();
		
		
		
		
		double random = 1;
		while (random > 1f/3)
		{
			random = rng.nextDouble();
			if (random > 2f/3 && onset.length() < 4)
				onset = "C" + onset;
			else if (random > 1f/3 && coda.length() < 4)
				coda = coda + "C";
		
		}
		
		onset = "CCCC";
		
		System.out.println(onset + nucleus + coda);
		
		for (int i = 0; i < 25; i++)
		{
			ArrayList<Orthograph> onsetCluster = generateOnset(onset, allowedInOnset);
			
			for (Orthograph o : onsetCluster)
			{
				System.out.print(o);
			}
			System.out.print(" (");
			for (Orthograph o : onsetCluster)
			{
				System.out.print(o.phoneme.getSymbol());
			}
			System.out.println(")");
		}
		
	}
	
	public ArrayList<Orthograph> generateOnset(String onset, ArrayList<Orthograph> allowedInOnset)
	{
		double clusteros = 0.5;
		ArrayList<Orthograph> onsetCluster = new ArrayList<Orthograph>();
		onsetCluster.add(selectOnset(allowedInOnset));
		int row = onsetCluster.get(onsetCluster.size() - 1).phoneme.getManner();
		
		while (onsetCluster.size() < onset.length() && row < transitionProbability[0].length &&
				rng.nextDouble() < Math.pow(clusteros, onsetCluster.size()))
		
//		while (onsetCluster.size() < onset.length() && row != transitionProbability[0].length - 1 &&
//			rng.nextDouble() < Math.pow(clusteros, onsetCluster.size()))
		{
			double total = 0;
			for (int i = 0; i < transitionProbability[row].length; i++)
				if (categories.get(i).size() > 0)
				{
//					System.out.print(" " + i + ":" + categories.get(i).size());
					
					total += transitionProbability[row][i];
				}
			
			if (total == 0)
				return onsetCluster;
			
			double random = rng.nextDouble() * total;
//			System.out.printf(" !-%.1f", random);
			for (int i = row; i < transitionProbability[0].length; i++)
			{
				if (categories.get(i).size() > 0)
					random -= transitionProbability[row][i];
//				System.out.printf(" %d-%.1f", i, random);
				if (random < 0)
				{
					row = i;
					i = transitionProbability[0].length;
				}
			}
			
//			System.out.print(" row " + row + " has " + categories.get(row).size() + "\t");
			
			ArrayList<Orthograph> potentialNexties = new ArrayList<Orthograph>();
			for (Orthograph o : categories.get(row))
			{
				if (o.onsetFreq > 0)
					potentialNexties.add(o);
			}
			
			Orthograph next = selectOnset(potentialNexties);
			onsetCluster.add(next);
			
			
		}
		
		return onsetCluster;
		
		
	}
	
	public void printInventory()
	{
		System.out.println("INVENTORY");
		System.out.println(masterList.size() + " phones assigned");
		
		for (int i = 0; i < manners.length; i++)
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
	
	public Orthograph selectOnset(ArrayList<Orthograph> choices)
	{
		double total = 0;
		for (Orthograph o : choices)
			total += o.onsetFreq;
		
		double random = total * rng.nextDouble();
		
		for (Orthograph o : choices)
		{
			random -= o.onsetFreq;
			if (random < 0)
				return o;
		}
		
				
		System.out.println(total + " / " + random);
		return null;
	}
	
	static class Orthograph implements Comparable<Orthograph>
	{
		Phoneme phoneme;
		int preferredOrthograph;
		double onsetFreq, nucleusFreq, codaFreq;
		boolean allowedInOnset, allowedInCoda;
		int id;
		
		static int count = 0;
		
		
		public Orthograph (Phoneme phoneme, int grapheme)
		{
			this.phoneme = phoneme;
			this.preferredOrthograph = grapheme;
			
			id = count;
			count++;
		}

		@Override
		public int compareTo(Orthograph other)
		{
			return phoneme.getOrthog()[preferredOrthograph].compareTo(other.phoneme.getOrthog()[other.preferredOrthograph]);
		}
		
		public String toString()
		{
			return phoneme.getOrthog()[preferredOrthograph];
		}
	}
	
	public static void makeCatalog()
	{
		if (catalog != null)
			return;
			
		catalog = new Phoneme[] {
				
				// 0 = s
				// 1 = unvoiced plosive
				// 2 = voiced plosive
				// 3 = affricate
				// 4 = unvoiced fricative
				// 5 = voiced fricative
				// 6 = nasal
				// 7 = L
				// 8 = R
				// 9 = glide
				// 10 = other
				
				// Affricates
				new Consonant("tʃ",	0.459,	false,	false,	"palato-alveolar",	"sibilant",	3,	new String[] {"ch", "c"}),
				new Consonant("ts",	0.308,	false,	false,	"alveolar", 		"sibilant",	3,	new String[] {"z", "ts"}),
				new Consonant("dʒ",	0.250,	true,	false,	"palato-alveolar", 	"sibilant",	3,	new String[] {"j"}),
				new Consonant("dz",	0.120,	true,	false,	"alveolar", 		"sibilant",	3,	new String[] {"dz"}),
				
				// Approximants
				new Consonant("j",	0.836,	true,	false,	"palatal", 			"",			9,		new String[] {"y", "j", "i"}),
				new Consonant("l",	0.761,	true,	false,	"alveolar", 		"lateral",	7,		new String[] {"l"}),
				new Consonant("w",	0.734,	true,	false,	"labial-velar", 	"",			9,		new String[] {"w", "u"}),
				new Consonant("r",	0.703,	true,	false,	"alveolar", 		"",			8,		new String[] {"r"}),
//				new Consonant("β̞",	0.043,	true,	false,	"bilabial", 		"",			"approximant",	new String[] {"v", "w"}),
				new Consonant("ʍ",	0.033,	false,	false,	"labial-velar",		"",			10,		new String[] {"w", "wh"}),
				
				// Fricatives
				new Consonant("s",	0.818,	false,	false,	"alveolar",			"sibilant",	4,	new String[] {"s"}),
				new Consonant("h",	0.647,	false,	false,	"glottal",			"",			4,	new String[] {"h"}),
				new Consonant("ʃ",	0.415,	false,	false,	"palato-alveolar",	"sibilant",	4,	new String[] {"s", "sh", "x"}),
				new Consonant("f",	0.399,	false,	false,	"labiodental",		"",			4,	new String[] {"f"}),
				new Consonant("z",	0.271,	true,	false,	"alveolar",			"sibilant",	5,	new String[] {"z"}),
				new Consonant("x",	0.270,	false,	false,	"velar",			"",			4,	new String[] {"h", "kh", "ch"}),
				new Consonant("v",	0.211,	true,	false,	"labiodental",		"",			5,	new String[] {"v"}),
				new Consonant("ɣ",	0.145,	true,	false,	"velar",			"",			5,	new String[] {"g", "gh"}),
				new Consonant("ʒ",	0.135,	true,	false,	"palato-alveolar",	"sibilant",	5,	new String[] {"zh", "j"}),
				new Consonant("β",	0.120,	true,	false,	"bilabial",			"",			5,	new String[] {"v", "bh"}),
				new Consonant("ɸ",	0.120,	false,	false,	"bilabial",			"",			4,	new String[] {"f", "ph"}),
				new Consonant("ð",	0.049,	true,	false,	"dental",			"sibilant",	5,	new String[] {"th", "dh"}),
				new Consonant("θ",	0.040,	false,	false,	"dental",			"sibilant",	4,	new String[] {"th"}),
				
				// Nasals
				new Consonant("n",	0.956,	true,	false,	"alveolar",			"",			6,	new String[] {"n"}),
				new Consonant("m",	0.940,	true,	false,	"bilabial",			"",			6,	new String[] {"m"}),
				new Consonant("ŋ",	0.525,	true,	false,	"velar",			"",			6,	new String[] {"ng"}),
				new Consonant("m.",	0.038,	false,	false,	"bilabial",			"",			10,	new String[] {"mh"}),
				new Consonant("n.",	0.020,	false,	false,	"alveolar",			"",			10,	new String[] {"nh"}),
				
				// Plosive
				new Consonant("k",	0.920,	false,	false,	"velar",			"",			1,	new String[] {"k", "c", "q"}),
				new Consonant("p",	0.856,	false,	false,	"bilabial",			"",			1,	new String[] {"p"}),
				new Consonant("t",	0.756,	false,	false,	"alveolar",			"",			1,	new String[] {"t"}),
				new Consonant("b",	0.665,	true,	false,	"bilabial",			"",			2,	new String[] {"b"}),
				new Consonant("d",	0.643,	true,	false,	"alveolar",			"",			2,	new String[] {"d"}),
				new Consonant("g",	0.563,	true,	false,	"velar",			"",			2,	new String[] {"g"}),
//				new Consonant("ʔ",	0.479,	false,	false,	"glottal",			"",			1,	new String[] {"'"}),
				new Consonant("tʰ",	0.246,	false,	true,	"alveolar",			"",			1,	new String[] {"t", "th"}),
				new Consonant("kʰ",	0.228,	false,	true,	"velar",			"",			1,	new String[] {"k", "kh"}),
				new Consonant("pʰ",	0.224,	false,	true,	"bilabial",			"",			1,	new String[] {"p", "ph"}),
				new Consonant("q",	0.140,	false,	false,	"uvular",			"",			1,	new String[] {"q"}),
				new Consonant("qʰ",	0.038,	false,	false,	"uvular",			"",			1,	new String[] {"q", "qh"}),
				
				new Vowel	 ("i",	0.978,	"high",			"front",	false,	new String[] {"i"}),
				new Vowel    ("ä",	0.958,	"low",			"central",	false,	new String[] {"a"}),
				new Vowel	 ("u",	0.933,	"high",			"back",		true,	new String[] {"u"}),
				new Vowel	 ("ɛ",	0.871,	"lower mid",	"front",	false,	new String[] {"e"}),
				new Vowel	 ("o",	0.856,	"mid",			"back",		false,	new String[] {"o"}),
				new Vowel	 ("ə",	0.213,	"mid",			"central",	false,	new String[] {"'", "u", "e"}),
				new Vowel	 ("y",	0.053,	"high",			"front",	true,	new String[] {"u", "y"})
			};
	}
	
	public static void generateCatalog()
	{
		// Populate catalog
		makeCatalog();
		
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
	protected String symbol;
	protected int manner;
	protected String[] orthog;
	protected double freq;
	
	public Phoneme(String symbol, int manner, double freq, String[] orthog)
	{
		this.symbol = symbol;
		this.manner = manner;
		this.freq = freq;
		this.orthog = orthog;
	}

	public String getSymbol() {
		return symbol;
	}

	public int getManner() {
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

	public Consonant(String symbol, double freq, boolean voiced, boolean aspirated, String place,
			String modifier, int manner, String[] orthog)
	{
		super(symbol, manner, freq, orthog);
		this.voiced = voiced;
		this.aspirated = aspirated;
		this.place = place;
		this.modifier = modifier;
	}

	public boolean getVoiced()
	{
		return voiced;
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

	public Vowel(String symbol, double freq, String height, String place, boolean rounded,
			String[] orthog) {
		super(symbol, 11, freq, orthog);
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
