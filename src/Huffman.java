import java.util.PriorityQueue;
import java.util.concurrent.ThreadLocalRandom;
import java.util.Comparator;
import java.util.HashMap;




class HuffmanNode {

	char symbol;
	int freq;

	HuffmanNode left;
	HuffmanNode right;
	
}









class HuffmanComparator implements Comparator<HuffmanNode> {
	
	public int compare(HuffmanNode x, HuffmanNode y) {

		return x.freq - y.freq;
		
	}
}








public class Huffman {
	
	static HashMap<Character, Float> sourceDistr = new HashMap<Character, Float>();
	
	// For Improved Huffman only:
	static float wghtdAvgCWLenSum = 0;
	static float wghtdAvgCWLenNum = 0;


	public static void letterToCodewordFunc(HashMap<Character, String> letterToCodeword, HuffmanNode root, String s) {


		if (root.left == null && root.right == null && root.symbol != Character.MIN_VALUE) {

			// symbol is the character in the node
//			System.out.println(root.symbol + "\t" + s);
			letterToCodeword.put(root.symbol, s);

			return;
		}

		// if we go to left then add "0" to the code.
		// if we go to the right add"1" to the code.

		letterToCodewordFunc(letterToCodeword, root.left, s + "0");
		letterToCodewordFunc(letterToCodeword, root.right, s + "1");
	}
	
	
	
	
	public static PriorityQueue<HuffmanNode> generatePQ(HashMap<Character, Integer> letterFreq) {
		
		
		int lettersNum = letterFreq.size();
		PriorityQueue<HuffmanNode> pq = new PriorityQueue<HuffmanNode>(lettersNum, new HuffmanComparator());
		
//		System.out.println("LETTER FREQUENCIES:");
		letterFreq.forEach((letter, freq) -> {
			
//			System.out.println(letter +": "+ freq);
			
			HuffmanNode node = new HuffmanNode();
	
			node.symbol = letter;
			node.freq = freq;
	
			node.left = null;
			node.right = null;
	
			pq.add(node);
			
		});
//		System.out.println();
		
		
		return pq;
	}
	
	public static HuffmanNode generateTree(PriorityQueue<HuffmanNode> pq) {
		
		// create a tree for the Huffman
		HuffmanNode root = null;
	
		while (pq.size() > 1) {
	
			// poll() returns element and deletes it from the min heap, peak() just returns the element (without deleting)
			HuffmanNode x = pq.poll();
			HuffmanNode y = pq.poll();
			HuffmanNode z = new HuffmanNode();
	

			z.freq = x.freq + y.freq;
			// We mark this charater with special MIN_VAL char because
			// this one is like the "NULL" (empty) value, so that
			// it serves as indicator of not having
			//  the actual char
			z.symbol = Character.MIN_VALUE;
	
			z.left = x;
	
			z.right = y;
	
			root = z;
	
			pq.add(z);
		}
		
		return root;
		
	}
	
	
	
	
	public static void doNormalHuffman(String originalText) {
		
//		System.out.println("\nOriginal String to be encoded:\t" +originalText+ "\n");
		System.out.println("\nOriginal String to be encoded:\t" +originalText);

		HashMap<Character, Integer> letterFreq = new HashMap<Character, Integer>();
		
		letterFreq.put('a', 5);
		letterFreq.put('b', 10);
		letterFreq.put('c', 15);
		letterFreq.put('d', 18);
		letterFreq.put('e', 22);
		letterFreq.put('f', 30);
		
		PriorityQueue<HuffmanNode> pq = generatePQ(letterFreq);
		HuffmanNode root = generateTree(pq);
		
		HashMap<Character, String> letterToCodeword = new HashMap<Character, String>();
//		System.out.println("Our codewords:");
		letterToCodewordFunc(letterToCodeword, root, "");
		System.out.println("Our initial codewords: " +letterToCodeword+ "\n");
//		System.out.println();
		

		
		
		
		
		// Now, let's encode the string
		String encoded = NormalHuffmanEncoder.encode(letterToCodeword, originalText);
		System.out.println("Encoded message: " +encoded);
		
		// Now, let's DECODE the string
		String decoded = NormalHuffmanDecoder.decode(root, encoded);
		System.out.println("Decoded message: " +decoded+ "\n");
		
		
		
		
		
		
		// Check if original text and DEcoded are equal
		if (originalText.equals(decoded))
			System.out.println("Original message and decoded message are EQUAL\n");
		else
			System.out.println("Original message and decoded message are NOT equal\n");
		
		
		
		
		// Compression ratio
		// Firstly, UNcompressed data
		int lettersNum = letterFreq.size(); // how many letters do we have in alphabet
		int noCompCodewordLen = log2Ceiled(lettersNum);
		System.out.println("If NO coding used: " +noCompCodewordLen+ "-bit codewords.");
		int noCompMessageLen = noCompCodewordLen * originalText.length();
		System.out.println("If NO coding used: " +noCompMessageLen+ " bits needed for the message.");
		
		// Secondly, COMPRESSED data
		int compMessageLen = encoded.length();
		System.out.println("If coding is USED: " +compMessageLen+ " bits needed for the (encoded) message.");
		
		// And thirdly, compression ratio
		float comprRat = (float) noCompMessageLen / (float) compMessageLen;
		System.out.println("\nData compression ratio: " +comprRat);
		
		
		
		// Entropy
//		System.out.println("\nOur source distribution: " +sourceDistr);
		
		float entropy = 0;
		for (Float prob : sourceDistr.values())
		    entropy += prob * log2(prob);
		entropy = -entropy;			// don't forget to put minus!
		
		System.out.println("\nEntropy: " +entropy);
		
		// Weighted average codeword length
		float wghtdAvgCWLen = 0;
		for (HashMap.Entry<Character, String> entry : letterToCodeword.entrySet()) {
			float bits = entry.getValue().length();		// not int, but float, because then multiplication with float
			float prob = sourceDistr.get(entry.getKey());
			wghtdAvgCWLen += bits * prob;
		}
		
		System.out.println("Weighted average codeword length: " +wghtdAvgCWLen);
		
		
	}
	
	
	
	
	
	public static void doAdaptiveHuffman(String originalText) {
		
//		System.out.println("\nOriginal String to be encoded:\t" +originalText+ "\n");
		System.out.println("\nOriginal String to be encoded:\t" +originalText);
		HashMap<Character, Integer> letterFreq = new HashMap<Character, Integer>();
		
		letterFreq.put('a', 5);
		letterFreq.put('b', 10);
		letterFreq.put('c', 15);
		letterFreq.put('d', 18);
		letterFreq.put('e', 22);
		letterFreq.put('f', 30);
		
		PriorityQueue<HuffmanNode> pq = generatePQ(letterFreq);
		HuffmanNode root = generateTree(pq);
		
		HashMap<Character, String> letterToCodeword = new HashMap<Character, String>();
//		System.out.println("Our codewords:");
		letterToCodewordFunc(letterToCodeword, root, "");
		System.out.println("Our initial codewords: " +letterToCodeword+ "\n");
//		System.out.println();
		

		
		
		
		
		// Now, let's encode the string
//		System.out.println("----------------------------------------------------------------------\nEncoding process");

		String encoded = ImprovedHuffmanEncoder.encode(letterToCodeword, originalText);
		System.out.println("Encoded message: " +encoded);
		
		// Now, let's DECODE the string
//		System.out.println("----------------------------------------------------------------------\nDecoding process");
		String decoded = ImprovedHuffmanDecoder.decode(root, encoded, originalText.length());
//		System.out.println("Decoded message: " +decoded+ "\n----------------------------------------------------------------------");
		System.out.println("Decoded message: " +decoded+ "\n");
		
		
		
		
		
		
		// Check if original text and DEcoded are equal
		if (originalText.equals(decoded))
			System.out.println("Original message and decoded message are EQUAL\n");
		else
			System.out.println("Original message and decoded message are NOT equal\n");
		
		
		
		
		// Compression ratio
		// Firstly, UNcompressed data
		int lettersNum = letterFreq.size(); // how many letters do we have in alphabet
		int noCompCodewordLen = log2Ceiled(lettersNum);
		System.out.println("If NO coding used: " +noCompCodewordLen+ "-bit codewords.");
		int noCompMessageLen = noCompCodewordLen * originalText.length();
		System.out.println("If NO coding used: " +noCompMessageLen+ " bits needed for the message.");
		
		// Secondly, COMPRESSED data
		int compMessageLen = encoded.length();
		System.out.println("If coding is USED: " +compMessageLen+ " bits needed for the (encoded) message.");
		
		// And thirdly, compression ratio
		float comprRat = (float) noCompMessageLen / (float) compMessageLen;
		System.out.println("\nData compression ratio: " +comprRat);
		
		
		
		// Entropy
//		System.out.println("\nOur source distribution: " +sourceDistr);
		
		float entropy = 0;
		for (Float prob : sourceDistr.values())
		    entropy += prob * log2(prob);
		entropy = -entropy;			// don't forget to put minus!
		
		System.out.println("\nEntropy: " +entropy);
		
		// Weighted average codeword length
		float meanWghtdAvgCWLen = wghtdAvgCWLenSum / wghtdAvgCWLenNum;
		System.out.println("(Mean) Weighted average codeword length: " +meanWghtdAvgCWLen);
		
		// Don't forget to reset the values after the coding is finished!
		wghtdAvgCWLenSum = 0;
		wghtdAvgCWLenNum = 0;
		
		
	}





	public static int log2Ceiled(int N) {
 
        int result = (int) Math.ceil( (Math.log(N) / Math.log(2)) );
        return result;
        
    }
	
	
	
	public static float log2(float N) {
		 
        float result = (float) ( (Math.log(N) / Math.log(2)) );
        return result;
        
    }
	
	
	
	
	
	public static String randomStr() {
		
		String randStr = "";
		
		for (int i = 1; i <= 1000; i++) {
			
			int randInt = ThreadLocalRandom.current().nextInt(1, 100 + 1);
			
			if (randInt >= 1 && randInt <= 5)
				randStr = randStr + 'a';
			
			else if (randInt >= 6 && randInt <= 15)
				randStr = randStr + 'b';
			
			else if (randInt >= 16 && randInt <= 30)
				randStr = randStr + 'c';
			
			else if (randInt >= 31 && randInt <= 48)
				randStr = randStr + 'd';
			
			else if (randInt >= 49 && randInt <= 70)
				randStr = randStr + 'e';
			
			else if (randInt >= 71 && randInt <= 100)
				randStr = randStr + 'f';
			
		}
		
		sourceDistr.clear();					// clear it before the next usage
		sourceDistr.put('a', 5f		/ 100);
		sourceDistr.put('b', 10f	/ 100);
		sourceDistr.put('c', 15f	/ 100);
		sourceDistr.put('d', 18f	/ 100);
		sourceDistr.put('e', 22f	/ 100);
		sourceDistr.put('f', 30f	/ 100);
		// And don't forget to immediately use this string in some coding!
		// I mean, sourceDistr is static, so...
		
		return randStr;
		
	}
	
	
	
	
	
	public static String randomStrSwap() {
		
		String randStr = "";
		
		for (int i = 1; i <= 10; i++) {
			
			for (int j = 1; j <= 100; j++) {
				
				int randInt = ThreadLocalRandom.current().nextInt(1, 100 + 1);
				
				if (randInt >= 1 && randInt <= 5) {
					if (i % 2 != 0) // if NOT even
						randStr = randStr + 'a';
					else // if EVEN
						randStr = randStr + 'c';
				}
				
				else if (randInt >= 6 && randInt <= 15)
					randStr = randStr + 'b';
				
				else if (randInt >= 16 && randInt <= 30) {
					if (i % 2 != 0) // if NOT even
						randStr = randStr + 'c';
					else // if EVEN
						randStr = randStr + 'a';
				}
				
				else if (randInt >= 31 && randInt <= 48) {
					if (i % 2 != 0) // if NOT even
						randStr = randStr + 'd';
					else // if EVEN
						randStr = randStr + 'f';
				}
				
				else if (randInt >= 49 && randInt <= 70)
					randStr = randStr + 'e';
				
				else if (randInt >= 71 && randInt <= 100) {
					if (i % 2 != 0) // if NOT even
						randStr = randStr + 'f';
					else // if EVEN
						randStr = randStr + 'd';
				}
				
			}
			
		}
		
		
		sourceDistr.clear();
		sourceDistr.put('a', 10f / 100);
		sourceDistr.put('b', 10f / 100);
		sourceDistr.put('c', 10f / 100);
		sourceDistr.put('d', 24f / 100);
		sourceDistr.put('e', 22f / 100);
		sourceDistr.put('f', 24f / 100);
		
		return randStr;
		
	}
	
	
	
	public static String randomStrSwap10000() {
		
		String randStr = "";
		
		for (int i = 1; i <= 10; i++) {
			
			for (int j = 1; j <= 1000; j++) {
				
				int randInt = ThreadLocalRandom.current().nextInt(1, 100 + 1);
				
				if (randInt >= 1 && randInt <= 5) {
					if (i % 2 != 0) // if NOT even
						randStr = randStr + 'a';
					else // if EVEN
						randStr = randStr + 'c';
				}
				
				else if (randInt >= 6 && randInt <= 15)
					randStr = randStr + 'b';
				
				else if (randInt >= 16 && randInt <= 30) {
					if (i % 2 != 0) // if NOT even
						randStr = randStr + 'c';
					else // if EVEN
						randStr = randStr + 'a';
				}
				
				else if (randInt >= 31 && randInt <= 48) {
					if (i % 2 != 0) // if NOT even
						randStr = randStr + 'd';
					else // if EVEN
						randStr = randStr + 'f';
				}
				
				else if (randInt >= 49 && randInt <= 70)
					randStr = randStr + 'e';
				
				else if (randInt >= 71 && randInt <= 100) {
					if (i % 2 != 0) // if NOT even
						randStr = randStr + 'f';
					else // if EVEN
						randStr = randStr + 'd';
				}
				
			}
			
		}
		
		sourceDistr.clear();
		sourceDistr.put('a', 10f / 100);
		sourceDistr.put('b', 10f / 100);
		sourceDistr.put('c', 10f / 100);
		sourceDistr.put('d', 24f / 100);
		sourceDistr.put('e', 22f / 100);
		sourceDistr.put('f', 24f / 100);
		
		
		return randStr;
		
	}
	
	
	
	
	
	
	public static String randomStrSwap10000Extreme() {
		
		String randStr = "";
		
		for (int i = 1; i <= 10; i++) {
			
			for (int j = 1; j <= 1000; j++) {
				
				int randInt = ThreadLocalRandom.current().nextInt(1, 100 + 1);
				
				if (randInt >= 1 && randInt <= 5) {
					if (i % 2 != 0) // if NOT even
						randStr = randStr + 'a';
					else // if EVEN
						randStr = randStr + 'f';
				}
				
				else if (randInt >= 6 && randInt <= 15)
					if (i % 2 != 0) // if NOT even
						randStr = randStr + 'b';
					else // if EVEN
						randStr = randStr + 'e';
				
				else if (randInt >= 16 && randInt <= 30) {
					if (i % 2 != 0) // if NOT even
						randStr = randStr + 'c';
					else // if EVEN
						randStr = randStr + 'd';
				}
				
				else if (randInt >= 31 && randInt <= 48) {
					if (i % 2 != 0) // if NOT even
						randStr = randStr + 'd';
					else // if EVEN
						randStr = randStr + 'c';
				}
				
				else if (randInt >= 49 && randInt <= 70)
					if (i % 2 != 0) // if NOT even
						randStr = randStr + 'e';
					else // if EVEN
						randStr = randStr + 'b';
				
				else if (randInt >= 71 && randInt <= 100) {
					if (i % 2 != 0) // if NOT even
						randStr = randStr + 'f';
					else // if EVEN
						randStr = randStr + 'a';
				}
				
			}
			
		}
		
		
		sourceDistr.clear();
		sourceDistr.put('a', 17.5f	/ 100);
		sourceDistr.put('b', 16f	/ 100);
		sourceDistr.put('c', 16.5f	/ 100);
		sourceDistr.put('d', 16.5f	/ 100);
		sourceDistr.put('e', 16f	/ 100);
		sourceDistr.put('f', 17.5f	/ 100);
		
		return randStr;
		
	}
	
	
	
	

	// main function
	public static void main(String[] args) {
		
		
		
		
		// This is just a random string with static probabilities
//		String randText = randomStr();
//		System.out.println("NORMAL Huffman on random string with STATIC probabilities");
//		doNormalHuffman(randText);
//		System.out.println("\n----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------\n");
//		System.out.println("IMPROVED Huffman on random string with STATIC probabilities:");
//		doAdaptiveHuffman(randText);
		
		
		
		
		
		
		// This is a random string GENERATED WITH SWAPPING PROBABILITIES FOR LETTERS
//		String randTextSwap = randomStrSwap();
//		System.out.println("NORMAL Huffman on random string with SWAPPING probabilities:");
//		doNormalHuffman(randTextSwap);
//		System.out.println("\n----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------\n");
//		System.out.println("IMPROVED Huffman on random string with SWAPPING probabilities:");		
//		doAdaptiveHuffman(randTextSwap);

		
		
		
		
		
		
		// Now, try modified strings (not 1 000, but 10 000 letters, and swaps are not after 100, but after 1 000 letters)
		// BEFORE running this, do not forget to change corresponding constants in ImprovedHuffmanEncoder class !   (or not? because 150 for both params is the best actually in practice hahah)
//		String randTextSwap10000 = randomStrSwap10000();
//		System.out.println("Normal Huffman:");
//		doNormalHuffman(randTextSwap10000);
//		System.out.println("\n----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------\n");
//		System.out.println("Improved Huffman:");		
//		doAdaptiveHuffman(randTextSwap10000);
		
		
		
		
		
		
		
		// Same as before, but now the lowest freq are swapped with highest freq. So it is even more volatile probabilities
		String randTextSwap10000Extreme = randomStrSwap10000Extreme();
		System.out.println("Normal Huffman:");
		doNormalHuffman(randTextSwap10000Extreme);
		System.out.println("\n----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------\n");
		System.out.println("Improved Huffman:");		
		doAdaptiveHuffman(randTextSwap10000Extreme);
		
		
		
		
		
		
		

		
	}
}
