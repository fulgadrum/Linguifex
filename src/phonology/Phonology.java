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
	static Phone[] catalog;
	static HashMap<Integer, Integer> counts;
	static String[] manners;
	
	List<List<Phone>> inventory;
	List<Segment> masterList;
	private double[][] onsetTransitionProbability;
	private double[][] vowelTransitionProbability;
	private double[][] codaTransitionProbability;
	private ArrayList<List<Segment>> sortedOnsetPhonemes;
	private Segment initialOnset;
	private String onset, coda;
	private Segment initialNucleus;
	private ArrayList<List<Segment>> sortedCodaPhonemes;
	private Segment initialCoda;
	private double nucleusClusterRate;
	
	public static void main(String[] args)
	{		
		Phonology.makeCatalog();
		
		
			Phonology phonology = new Phonology();
			phonology.MakeInventory();
//			phonology.printInventory();
			phonology.MakeOrthography();
			
			phonology.MakeSyllableStructure();
			
			phonology.MakePhonotactics();
			phonology.MakePhonotactics2();
		
			if (phonology.coda.length() > 0)
				phonology.MakePhonotactics3();
			
			// Generate some random names yeah!!!!!!!
			for (int i = 0; i < 25; i++)
			{
				System.out.println(phonology.generateName());
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
		
		if (onsetTransitionProbability == null)
			onsetTransitionProbability = new double[][] {
				{0, 1, 0, 0, 0, 0, 1, 1, 1, 1},	// s
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
		
		if (vowelTransitionProbability == null)
			vowelTransitionProbability = new double[][] {
				{0, 1, 1, 1},
				{0, 0, 1, 1},
				{0, 0, 0, 1},
			};
		
			if (codaTransitionProbability == null)
				codaTransitionProbability = new double[][] {
					{0, 1, 0, 0, 0, 0, 1, 1, 1, 1},	// s
					{0, 0, 0, 0, 0, 0, 1, 1, 1, 1},	// unvoiced plosives
					{0, 0, 0, 0, 0, 0, 1, 1, 1, 1},	// voiced plosives
					{0, 0, 0, 0, 0, 0, 0, 0, 0, 1},	// affricates
					{0, 0, 0, 0, 0, 0, 0, 1, 1, 1},	// unvoiced fricatives
					{0, 0, 0, 0, 0, 0, 0, 0, 1, 1},	// voiced fricatives
					{0, 0, 0, 0, 0, 0, 0, 0, 0, 1},	// nasals
					{0, 0, 0, 0, 0, 0, 0, 0, 0, 1},	// L's
					{0, 0, 0, 0, 0, 0, 0, 0, 0, 1},	// R's
					{0, 0, 0, 0, 0, 0, 0, 0, 0, 0},	// glides
					{0, 0, 0, 0, 0, 0, 0, 0, 0, 0}	// others
				};
			
		if (initialOnset == null)
			initialOnset = new Segment(null, 0);
			
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
		inventory = new ArrayList<List<Phone>>();
		for (int i = 0; i < manners.length; i++)
			inventory.add(new ArrayList<Phone>());
		
		masterList = new ArrayList<Segment>();
		
		
		double expectedPhones = 20.507;
		expectedPhones = ((rng.nextGaussian() * 5) + 20.507);
		double arf = (expectedPhones - 20.507) / (46 - 20.507);
		
		for (int i = 0; i < 6; i++)
			inventory.add(new ArrayList<Phone>());
		
		int count = 0;
		
		// Populate inventory
		for (Phone p : catalog)
		{
			// Determine probability of current phoneme
			double prob = 1 - (1 - p.getFreq()) * (1 - arf);
			
			if (Math.random() < prob)
			{
				inventory.get(p.getManner()).add(p);
				masterList.add(new Segment(p, 0));
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
		ArrayList<Segment> orthography = new ArrayList<Segment>();
		double orthographicDeviancy = 1.0/3;	// The odds of using an alternate orthograph
		
		System.out.println("\nINITIAL ORTHOGRAPHY");
		for (Segment o : masterList)
		{
			Phone p = o.phone;
			for (int i = 0; i < p.getOrthog().length; )
				// If we're on the last grapheme on the list, pick it
				if (i == p.getOrthog().length - 1)
				{
					orthography.add(new Segment(p, i));
					i = p.getOrthog().length;
				}
				
				// Otherwise, consider the next option
				else if (rng.nextDouble() < orthographicDeviancy)
					i++;
				else
				{
					orthography.add(new Segment(p, i));
					i = p.getOrthog().length;
				}
		}
		
		// Sort results
		Collections.sort(orthography);
		
		// Display results - phonemes
		for (Segment o : orthography)
		{
			String entry = "/" + o.phone.getSymbol() + "/";
			if (o.phone.getSymbol().length() == 1)
				entry = entry + " ";
			
			System.out.print(entry + " ");
		}
		System.out.println();
		
		// Display results - representations
		for (Segment o : orthography)
		{
			String entry = o.phone.getOrthog()[o.orthography];
			if (entry.length() == 1)
				entry = entry + " ";
			entry = " " + entry + " ";
			
			System.out.print(entry + " ");
		}
		
		int iterations;
		
		// MAYBE purify orthography (this algorithm needs improvement)
		Segment prev = null;
		HashSet<Segment> duplicates = new HashSet<Segment>();
		System.out.println();
		boolean changes = false;
		
		for (int i = 0; i < 5; i++)
		{
			System.out.print((i+1) + ":");
			changes = false;
			for (Segment curr : orthography)
			{
				if (prev != null && curr.compareTo(prev) == 0)
				{
					changes = true;
					System.out.print("\t" + prev.phone.getSymbol() + "/" + prev.phone.getOrthog()[prev.orthography] + " --> ");
					prev.orthography = (prev.orthography + 1) % prev.phone.getOrthog().length;
					System.out.print(prev.phone.getOrthog()[prev.orthography]);
					
					System.out.print("\t" + curr.phone.getSymbol() + "/" + curr.phone.getOrthog()[curr.orthography] + " --> ");
					curr.orthography = (curr.orthography + 1) % curr.phone.getOrthog().length;
					System.out.print(curr.phone.getOrthog()[curr.orthography]);
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
		
		// Copy orthography to masterList
		for (Segment s : masterList)
		{
			boolean success = false;
			for (int i = 0; i < orthography.size(); i++)
				if (orthography.get(i).phone == s.phone)
				{
					success = true;
					s.orthography = orthography.get(i).orthography;
					i = orthography.size();
				}
			
			if (!success)
			{
				System.out.println("we fucked up boss");
				System.exit(1);
			}
		}
			
		
		// Display corrected orthography
		System.out.println("\nCORRECTED ORTHOGRAPHY");
		for (Segment o : orthography)
		{
			String entry = "/" + o.phone.getSymbol() + "/";
			if (o.phone.getSymbol().length() == 1)
				entry = entry + " ";
			
			System.out.print(entry + " ");
		}
		System.out.println();
		
		for (Segment o : orthography)
		{
			String entry = o.phone.getOrthog()[o.orthography];
			if (entry.length() == 1)
				entry = entry + " ";
			entry = " " + entry + " ";
			
			System.out.print(entry + " ");
		}
		System.out.println();

		return iterations;
	}
	
	public void MakeSyllableStructure()
	{
		onset = "C";
		coda = "";
		
		// Determine syllable structure
		double random = 1;
		while (random > 1f/3)
		{
			random = rng.nextDouble();
			if (random > 2f/3 && onset.length() < 4)
				onset = "C" + onset;
			else if (random > 1f/3 && coda.length() < 4)
				coda = coda + "C";
		
		}
	}
	
	public void MakePhonotactics()
	{
		boolean onsetRequired = false;
		if (rng.nextDouble() < 0.25)
			onsetRequired = true;
		
		boolean forbidInitialEng = true;
		if (rng.nextDouble() < 0.25)
			forbidInitialEng = false;
		
		// Probability of phoneme being an acceptable onset
		double onsetInclusionRate = Math.min(1, 1 + rng.nextGaussian() * 0.25);
		
		// Allowed phonemes in onset list
		ArrayList<Segment> allowedInOnset = new ArrayList<Segment>();
		
		double total = 0;
		while (total == 0)
		{
			for (int i = 0; i < masterList.size() - inventory.get(11).size(); i++)
			{
				if (rng.nextDouble() < onsetInclusionRate &&
						!(masterList.get(i).phone.getSymbol().equals("ŋ") && forbidInitialEng))
				{
					allowedInOnset.add(masterList.get(i));
					masterList.get(i).onsetFreq = Math.max(0.5 + rng.nextGaussian() * 0.25, 0.01);
					total += masterList.get(i).onsetFreq; 
				}		
			}
		}
		
		System.out.println("Onset characters");
		
		// Make a new list of lists to hold orthographs by category
		sortedOnsetPhonemes = new ArrayList<List<Segment>>();
		for (int i = 0; i < manners.length; i++)
			sortedOnsetPhonemes.add(new ArrayList<Segment>());
		
		// Populate list-list of onset orthographs
		for (int i = 0; i < allowedInOnset.size(); i++)
		{
			Segment o = allowedInOnset.get(i);
			Phone p = allowedInOnset.get(i).phone;
			
			if (p.getManner() < 11)
				sortedOnsetPhonemes.get(p.getManner()).add(o);
		}
		
		// Set up initial ghost orthograph
		initialOnset = new Segment(null, 0);
		
		initialOnset.onsetNext = new HashMap<Segment, Double>();
		total = 0;
		for (Segment o : allowedInOnset)
		{
			initialOnset.onsetNext.put(o, o.onsetFreq);
			total += o.onsetFreq;
		}
		if (!onsetRequired)
			initialOnset.onsetNext.put(initialOnset, Math.max(total * rng.nextGaussian() / 2 + total, 0));

		
		System.out.println(onset + "V" + coda);
		
		// Generate onset clustering rules - IFF this language allows initial consonant clusters
		if (onset.length() > 1)
		{
			// Populate onsetNext hierarchy
			double clusterPermissiveness = Math.min(rng.nextDouble() + 0.15, 1);
			System.out.println("Cluster permissiveness: " + clusterPermissiveness);
			for (Segment o : allowedInOnset)
			{
				int row = o.phone.getManner();
				double totalFreq = 0;
				for (int i = row; i < onsetTransitionProbability[row].length; i++)
				{
//					System.out.println(manners[row] + " to " + manners[i] + ":\t\t" + transitionProbability[row][i]);
					if (onsetTransitionProbability[row][i] > 0)
						for (Segment next : sortedOnsetPhonemes.get(i))
							if (rng.nextDouble() < clusterPermissiveness)
							{
								o.onsetNext.put(next, next.onsetFreq);
								totalFreq += next.onsetFreq;
							}
				}
				
				double endChance = Math.max(totalFreq * rng.nextGaussian() / 2 + totalFreq, 0);
				o.onsetNext.put(initialOnset, endChance);
			}
			
			System.out.println();
			
			// Print onset phoneme transitions
				System.out.println("Allowable Clusters");
			
			for (Segment o : allowedInOnset)
			{
				System.out.print(o.phone.getSymbol() + "\t");
				Iterator itr = o.onsetNext.entrySet().iterator();
				while (itr.hasNext())
				{
					Entry<Segment, Double> kv = (Entry) itr.next();
					if (kv.getKey().phone != null)
						System.out.printf("-->%s (%.2f)\t", kv.getKey().phone.getSymbol(), kv.getValue());
					else
						System.out.printf("-->$ (%.2f)\t", kv.getValue());
				}
				System.out.println();
			}
		}
	}
	
	public void MakePhonotactics2()
	{
		System.out.println();
		
		nucleusClusterRate = 0.5 + 0.25 * rng.nextGaussian();
		System.out.println("nucleus cluster rate: " + nucleusClusterRate);
		
		List<List<Segment>> sortedVowels = new ArrayList<List<Segment>>();
		for (int i = 0; i < 4; i++)
			sortedVowels.add(new ArrayList<Segment>());
		
		List<Segment> allVowels = new ArrayList<Segment>();
		
		// Sorted lists of vowels
		for (Segment o : masterList)
			if (o.phone.getManner() == 11)
			{
				Vowel v = (Vowel) o.phone;
				o.nucleusFreq = Math.max(0.5 + rng.nextGaussian() * 0.15, 0.01);
				allVowels.add(o);
				sortedVowels.get(v.getHeight()).add(o);
			}

		// Make long vowel character
		Segment macron = new Segment(new Vowel("~", 0, "", "", false, new String[]{"~"}), 0);
		if (rng.nextDouble() < 0.2)
		{
			macron.nucleusFreq = Math.max(0.5 + rng.nextGaussian() * 0.15, 0.01);
			sortedVowels.get(3).add(macron);
		}
		
		Phone p = new Phone("$", 0, 0, new String[] {"$"});
		initialNucleus = new Segment(p, 0);
		
		initialNucleus.nucleusNext = new HashMap<Segment, Double>();
		for (Segment o : allVowels)
			initialNucleus.nucleusNext.put(o, o.nucleusFreq);
		
		// Populate nucleusNext hierarchy
		double diphthongos = Math.max(0.25*rng.nextGaussian() + 0.5, 0);
		System.out.println("Diphthong permissiveness: " + diphthongos);
		for (Segment o : allVowels)
		{
			int row = ((Vowel) o.phone).getHeight();
			double totalFreq = 0;
			for (int i = row; i < vowelTransitionProbability[row].length; i++)
			{
//							System.out.println(manners[row] + " to " + manners[i] + ":\t\t" + transitionProbability[row][i]);
				if (vowelTransitionProbability[row][i] > 0)
					for (Segment next : sortedVowels.get(i))
						if (rng.nextDouble() < diphthongos)
						{
							o.nucleusNext.put(next, next.nucleusFreq);
							totalFreq += next.nucleusFreq;
						}
			}
			
			double endChance = Math.max(totalFreq * rng.nextGaussian() / 2 + totalFreq, 0);
			o.nucleusNext.put(initialNucleus, endChance);
		}
		
		System.out.println();
		
		// Print onset phoneme transitions
		System.out.println("Allowable Clusters");
		
		for (Segment s : allVowels)
		{
			System.out.print(s.phone.getSymbol() + "\t");
			Iterator itr = s.nucleusNext.entrySet().iterator();
			while (itr.hasNext())
			{
				Entry<Segment, Double> kv = (Entry) itr.next();
				if (kv.getKey().phone != null)
					System.out.printf("-->%s (%.2f)\t", kv.getKey().phone.getSymbol(), kv.getValue());
				else
					System.out.printf("-->$ (%.2f)\t", kv.getValue());
			}
			System.out.println();
		}
	}
	
	public void MakePhonotactics3()
	{
		if (coda.length() == 0)
			return;
		
		// Probability of phoneme being an acceptable coda
		double codaInclusionRate = Math.min(1, 1 + rng.nextGaussian() * 0.25);
		
		// Allowed phonemes in coda list
		ArrayList<Segment> allowedInCoda = new ArrayList<Segment>();
		
		double total = 0;
		while (total == 0)
		{
			for (int i = 0; i < masterList.size() - inventory.get(11).size(); i++)
			{
				if (rng.nextDouble() < codaInclusionRate &&			// don't include special phones
						masterList.get(i).phone.getManner() != 10	// in coda
						&& masterList.get(i).phone.getManner() != 9)// or glides, for now
				{
					allowedInCoda.add(masterList.get(i));
					masterList.get(i).codaFreq = Math.max(0.5 + rng.nextGaussian() * 0.25, 0.01);
					total += masterList.get(i).codaFreq; 
				}		
			}
		}
		
		System.out.println("Coda characters");
		
		
		// Make a new list of lists to hold orthographs by category
		sortedCodaPhonemes = new ArrayList<List<Segment>>();
		for (int i = 0; i < manners.length; i++)
			sortedCodaPhonemes.add(new ArrayList<Segment>());
		
		// Populate list-list of onset orthographs
		for (int i = 0; i < allowedInCoda.size(); i++)
		{
			Segment o = allowedInCoda.get(i);
			Phone p = allowedInCoda.get(i).phone;
			
			if (p.getManner() < 11)
				sortedCodaPhonemes.get(p.getManner()).add(o);
		}
		
		// Set up initial ghost orthograph
		initialCoda = new Segment(null, 0);
		total = 0;
		initialCoda.codaNext = new HashMap<Segment, Double>();
		for (Segment o : allowedInCoda)
		{
			initialCoda.codaNext.put(o, o.codaFreq);
			total += o.codaFreq;
		}
		initialCoda.codaNext.put(initialCoda, Math.max(total * rng.nextGaussian() / 2 + total, 0));
		
		// Coda clustering rules - IFF this language allows initial consonant clusters
		if (coda.length() > 1)
		{
			// Populate codaNext hierarchy
			double clusterPermissiveness = Math.min(rng.nextDouble() + 0.15, 1);
			System.out.println("Cluster permissiveness: " + clusterPermissiveness);
			for (Segment s : allowedInCoda)
			{
				int row = s.phone.getManner();
				double totalFreq = 0;
				for (int i = 0; i <= row; i++)
				{
					if (codaTransitionProbability[i][row] > 0)
						for (Segment next : sortedCodaPhonemes.get(i))
							if (rng.nextDouble() < clusterPermissiveness)
							{
								s.codaNext.put(next, next.codaFreq);
								totalFreq += next.codaFreq;
							}
				}
				
				double endChance = Math.max(totalFreq * rng.nextGaussian() / 2 + totalFreq, 0);
				s.codaNext.put(initialCoda, endChance);
			}
			
			System.out.println();
			
			// Print coda phoneme transitions
			System.out.println("Allowable Clusters");
			
			for (Segment o : allowedInCoda)
			{
				System.out.print(o.phone.getSymbol() + "\t");
				Iterator itr = o.codaNext.entrySet().iterator();
				while (itr.hasNext())
				{
					Entry<Segment, Double> kv = (Entry) itr.next();
					if (kv.getKey().phone != null)
						System.out.printf("-->%s (%.2f)\t", kv.getKey().phone.getSymbol(), kv.getValue());
					else
						System.out.printf("-->$ (%.2f)\t", kv.getValue());
				}
				System.out.println();
			}
		}
	}
	
	public ArrayList<Segment> generateOnset(boolean nonEmpty)
	{
		double clusteros = 0.3;
		ArrayList<Segment> onsetCluster = new ArrayList<Segment>();

		Segment curr = initialOnset;
		
		while (onsetCluster.size() < onset.length() && 
				rng.nextDouble() < Math.pow(clusteros, onsetCluster.size()))
		{
			if (onsetCluster.size() == 0)
				curr = selectSegment(curr.onsetNext, 1, nonEmpty);
			else
				curr = selectSegment(curr.onsetNext, 1, false);
			
			if (curr == null || curr == initialOnset)
				return onsetCluster;
			else
				onsetCluster.add(curr);
		}
		
		
		return onsetCluster;
	}
	
	public ArrayList<Segment> generateNucleus()
	{
		ArrayList<Segment> nucleusCluster = new ArrayList<Segment>();
		Segment curr = initialNucleus;
		
		while (rng.nextDouble() < Math.pow(nucleusClusterRate, nucleusCluster.size()) &&
				nucleusCluster.size() < 2)
		{
			curr = selectSegment(curr.nucleusNext, 2, false);
			
			if (curr == null || curr == initialNucleus)
				return nucleusCluster;
			else
				nucleusCluster.add(curr);
		}
		
		
		return nucleusCluster;
	}
	
	public ArrayList<Segment> generateCoda()
	{
		double clusteros = 0.3;
		ArrayList<Segment> codaCluster = new ArrayList<Segment>();

		Segment curr = initialCoda;
		
		while (codaCluster.size() < coda.length() && 
				rng.nextDouble() < Math.pow(clusteros, codaCluster.size()))
		{
			curr = selectSegment(curr.codaNext, 3, false);
			
			if (curr == null || curr == initialCoda)
				return codaCluster;
			else
				codaCluster.add(curr);
		}
		
		
		return codaCluster;
	}
	
	public String generateName()
	{
		Syllable syl1 = new Syllable(this);
		Syllable syl2 = new Syllable(this);
		
		Segment s1 = syl1.content().get(syl1.content().size() - 1);
		Segment s2 = syl2.content().get(0);
		
		String result = "";
		
		if (syl1.coda.size() == 0 && syl2.onset.size() == 0 &&
				((Vowel) s2.phone).getHeight() >= ((Vowel) s1.phone).getHeight())
		{
//			result += "!";
			syl2.onset = generateOnset(true);
		}
		
		return result + syl1.buildString() + "" + syl2.buildString();
	}
	
	public Segment selectSegment(HashMap<Segment, Double> choices, int element, boolean nonEmpty)
	{
		double total = 0;
		
		Iterator<Entry<Segment, Double>> itr = choices.entrySet().iterator();
		Entry<Segment, Double> next;
		
		while (itr.hasNext())
		{
			next = (Entry<Segment, Double>) itr.next();
			
			if (next.getKey().phone != null || !nonEmpty)
				total += (Double) next.getValue();
		}
				
		itr = choices.entrySet().iterator();
		
		double random = total * rng.nextDouble();
		while (itr.hasNext())
		{
			next = (Entry) itr.next();
			Segment o = (Segment) next.getKey();
			
			if (o.phone != null || !nonEmpty)
			{
				if (element == 1)
					random -= o.onsetFreq;
				else if (element == 2)
					random -= o.nucleusFreq;
				else if (element == 3)
					random -= o.codaFreq;
				if (random <= 0)
					return o;
			}
		}
		
		return null;
	}

	public void printInventory()
	{
		System.out.println("INVENTORY");
		System.out.println(masterList.size() + " phones assigned");
		
		for (int i = 0; i < manners.length; i++)
		{
			ArrayList<Phone> manner = (ArrayList<Phone>) inventory.get(i);
			
			String label = manners[i];
			if (label.length() < 8)
				label += "\t";
			label += "\t";
			System.out.print(label);
			
			for (Phone p : manner)
				System.out.print(p.getSymbol() + " ");
			
			System.out.println();
		}
	}
	
	public static void makeCatalog()
	{
		if (catalog != null)
			return;
			
		catalog = new Phone[] {
				
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
				new Consonant("j",	0.836,	true,	false,	"palatal", 			"",			9,		new String[] {"ï", "y", "j"}),
				new Consonant("l",	0.761,	true,	false,	"alveolar", 		"lateral",	7,		new String[] {"l"}),
				new Consonant("w",	0.734,	true,	false,	"labial-velar", 	"",			9,		new String[] {"ü", "w"}),
				new Consonant("r",	0.703,	true,	false,	"alveolar", 		"",			8,		new String[] {"r"}),
				new Consonant("ʍ",	0.033,	false,	false,	"labial-velar",		"",			10,		new String[] {"w", "wh"}),
				
				// Fricatives
				new Consonant("s",	0.818,	false,	false,	"alveolar",			"sibilant",	0,	new String[] {"s"}),
				new Consonant("h",	0.647,	false,	false,	"glottal",			"",			4,	new String[] {"h"}),
				new Consonant("ʃ",	0.415,	false,	false,	"palato-alveolar",	"sibilant",	4,	new String[] {"sh", "x", "s"}),
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
				new Vowel    ("a",	0.958,	"low",			"front",	false,	new String[] {"a"}),
				new Vowel	 ("u",	0.933,	"high",			"back",		true,	new String[] {"u"}),
				new Vowel	 ("e",	0.871,	"mid",			"front",	false,	new String[] {"e"}),
				new Vowel	 ("o",	0.856,	"mid",			"back",		false,	new String[] {"o"}),
//				new Vowel	 ("ə",	0.213,	"mid",			"central",	false,	new String[] {"'", "u", "e"}),
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
	
	public static Phone[] loadCatalog()
	{
		try {
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream("phonemes.dat"));
			Phone[] catalog = (Phone[]) ois.readObject();
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

class Syllable
{
	ArrayList<Segment> onset, nucleus, coda;
	String toString;
	String phoneticSpelling;
	
	public Syllable(Phonology phon)
	{
		onset 	= phon.generateOnset(false);
		nucleus	= phon.generateNucleus();
		coda 	= phon.generateCoda();
		
		StringBuilder sb = new StringBuilder();
		
		// Build phonetic string
//		sb = new StringBuilder();
//		for (Segment s : syl)
//			sb.append(s.phone.getSymbol());
//		phoneticSpelling = sb.toString();
	}

	public String buildString()
	{
		ArrayList<Segment> syl = content();
		
		// Build string version
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < syl.size(); i++)
		{
			String graph = syl.get(i).phone.getOrthog()[syl.get(i).orthography];
			
			if (graph.equals("ï"))
			{
				if (i == 0)
					sb.append("y");
				else if (i > 0 && i + 1 < syl.size() && syl.get(i+1).phone.getManner() == 11 &&
					syl.get(i+1).phone.getSymbol() != "i")
				{
					sb.append("i");
				}
				else
					sb.append("y");
			}
			
			else if (graph.equals("ü"))
			{
				if (i > 0 && i + 1 < syl.size() && syl.get(i+1).phone.getManner() == 11 &&
						syl.get(i+1).phone.getSymbol() != "u")
				{
					sb.append("u");
				}
				else
					sb.append("w");
			}
			
			else if (graph.equals("~"))
				sb.append(syl.get(i-1).phone.getOrthog()[syl.get(i-1).orthography]);
			
			else
				sb.append(graph);
		}
		
		return sb.toString();
	}
	
	public ArrayList<Segment> content()
	{
		ArrayList<Segment> syl = new ArrayList<Segment>();
		
		for (int i = 0; i < onset.size(); i++)
			syl.add(onset.get(i));
		
		for (int i = 0; i < nucleus.size(); i++)
			syl.add(nucleus.get(i));
		
		for (int i = 0; i < coda.size(); i++)
			syl.add(coda.get(i));
		
		return syl;
	}
	
	public String toString()
	{
		return toString;
	}
	
	public String getPhoneticSpelling()
	{
		return phoneticSpelling;
	}
	
}

class Segment implements Comparable<Segment>
{
	Phone phone;
	int orthography;
	double onsetFreq, nucleusFreq, codaFreq;
	boolean allowedInOnset, allowedInCoda;
	int id;
	
	static int count = 0;
	
	HashMap<Segment, Double> onsetNext;
	HashMap<Segment, Double> nucleusNext;
	HashMap<Segment, Double> codaNext;
	
	
	public Segment (Phone phone, int orthography)
	{
		this.phone = phone;
		this.orthography = orthography;
		
		onsetNext = new HashMap<Segment, Double>();
		nucleusNext = new HashMap<Segment, Double>();
		codaNext = new HashMap<Segment, Double>();
		
		id = count;
		count++;
	}

	@Override
	public int compareTo(Segment other)
	{
		return phone.getOrthog()[orthography].compareTo(other.phone.getOrthog()[other.orthography]);
	}
	
	public String toString()
	{
		return phone.getOrthog()[orthography];
	}
}

class Phone implements Serializable
{
	protected String symbol;
	protected int manner;
	protected String[] orthog;
	protected double freq;
	
	public Phone(String symbol, int manner, double freq, String[] orthog)
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

class Consonant extends Phone
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

class Vowel extends Phone
{
	private String height, place;
	private int heightNumeric;
	private boolean rounded;

	public Vowel(String symbol, double freq, String height, String place, boolean rounded,
			String[] orthog) {
		super(symbol, 11, freq, orthog);
		this.height = height;
		this.place = place;
		this.rounded = rounded;
		
		if (height.equals("high"))
			heightNumeric = 2;
		else if (height.equals("mid"))
			heightNumeric = 1;
		else if (height.equals("low"))
			heightNumeric = 0;
		else
			heightNumeric = 3;
	}

	public int getHeight()
	{
		return heightNumeric;
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
