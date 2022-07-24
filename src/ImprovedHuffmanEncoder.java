import java.util.HashMap;
import java.util.PriorityQueue;

public class ImprovedHuffmanEncoder {
	
		
	static final int howManyLastLettersToRead = 150;			// 150 by assignment, but we will change it sometimes for experimentation
	static final int afterWhatLetterBeginAdaptive = 150;		// before this position of the corresponding letter, we use initial tree
	static final int adaptSecUpdFreq = 100;						// after how many new letters read we will rebuild the tree (the less it is, the more often "updates to the tree" will occur)


	public static String encode(HashMap<Character, String> letterToCodeword, String originalText) {
		
		int messLen = originalText.length();
		int adaptLen = messLen - afterWhatLetterBeginAdaptive;
//		System.out.println("adaptLen: " +adaptLen);
		double adaptSectorsDb = (double) adaptLen / adaptSecUpdFreq;
		int adaptSectors = (int) Math.ceil(adaptSectorsDb);
//		System.out.println("adaptSectors: " +adaptSectors);
//		System.out.println();
		
		String encoded = "";
		char[] text = originalText.toCharArray();
		
		for (int i = 0; i < afterWhatLetterBeginAdaptive; i++) {
//			System.out.println("ITERATION " +i);
			String currentCodeword = letterToCodeword.get(text[i]);
//			System.out.println("Letter from HashMap: " +currentCodeword);
			encoded = encoded + currentCodeword;
		}
		
		// Weighted average codeword length
		float wghtdAvgCWLen = 0;
		for (HashMap.Entry<Character, String> entry : letterToCodeword.entrySet()) {
			float bits = entry.getValue().length();		// not int, but float, because then multiplication with float
			float prob = Huffman.sourceDistr.get(entry.getKey());
			wghtdAvgCWLen += bits * prob;
		}
		Huffman.wghtdAvgCWLenSum += wghtdAvgCWLen;
		Huffman.wghtdAvgCWLenNum++;
		
		
		// Now, we begin with "adaptive" part
		int lenLeft = adaptLen;
		
		while (lenLeft > 0) {
			
			int newStartIndex = messLen - lenLeft;
			int newEndIndex;
			int currentSecLen;
			
			if (lenLeft >= adaptSecUpdFreq) {
				currentSecLen = adaptSecUpdFreq;
				newEndIndex = newStartIndex + adaptSecUpdFreq - 1;
			}
			else {
				currentSecLen = lenLeft;
				newEndIndex = messLen - 1;
			}
			
//			System.out.println("\nnewStartIndex: " +newStartIndex);
//			System.out.println("newEndIndex: " +newEndIndex);
//			System.out.println("lenLeft: " +lenLeft);
			
			
//			String last150Letters = encoded.substring(encoded.length() - 150, encoded.length());	here was the mistake
//			String last150Letters = originalText.substring(newStartIndex - 150, newStartIndex);
			
			
			
			String last150Letters;
			int lettersEncodedSoFar = messLen - lenLeft;
			
			if (lettersEncodedSoFar >= howManyLastLettersToRead)
				last150Letters = originalText.substring(newStartIndex - howManyLastLettersToRead, newStartIndex);
			else			// lettersEncodedSoFar < howManyLastLettersToRead
				last150Letters = originalText.substring(newStartIndex - lettersEncodedSoFar, newStartIndex);
			
			
			
//			System.out.println("last150Letters: " +last150Letters);
//			System.out.println("From " +(newStartIndex - zeroSecLen)+ " to " +newStartIndex);

			letterToCodeword = updateCodewords(last150Letters);
//			System.out.println("UPDATED HASHMAP: " +letterToCodeword);
			

			// After updating the letterToCodeword, continue encoding string with NEW codewords
			for (int i = newStartIndex ; i <=  newEndIndex; i++) {
//				System.out.println("ITERATION " +i);
				String currentCodeword = letterToCodeword.get(text[i]);
//				System.out.println("Letter from HashMap: " +currentCodeword);
				encoded = encoded + currentCodeword;
			}
			
			// Weighted average codeword length
			wghtdAvgCWLen = 0;
			for (HashMap.Entry<Character, String> entry : letterToCodeword.entrySet()) {
				float bits = entry.getValue().length();		// not int, but float, because then multiplication with float
				float prob = Huffman.sourceDistr.get(entry.getKey());
				wghtdAvgCWLen += bits * prob;
			}
			Huffman.wghtdAvgCWLenSum += wghtdAvgCWLen;
			Huffman.wghtdAvgCWLenNum++;
			
			
			lenLeft = lenLeft - adaptSecUpdFreq;
		}
		
		
		
		return encoded;
	}
	
	
	
	
	
	public static HashMap<Character, String> updateCodewords(String last150Letters) {
		
		HashMap<Character, Integer> letterFreq = new HashMap<Character, Integer>();
		
		for (int i = 0; i < last150Letters.length(); i++) {
		    char c = last150Letters.charAt(i);
		    Integer val = letterFreq.get(c);
		    if (val != null)
		    	letterFreq.put(c, new Integer(val + 1));
		    else
		    	letterFreq.put(c, 1);
		}
		
		// Make sure that all the elements from the alphabet are present:
		char[] alphabet = {'a', 'b', 'c', 'd', 'e', 'f'};
		for (int i = 0; i < alphabet.length; i++) {
			Integer val = letterFreq.get(alphabet[i]);
		    if (val != null)	// if we already encountered such a letter:
		    	continue;
		    else				// if it did not appear in last n-letters at all, then just add 0 freq to it:
		    	letterFreq.put(alphabet[i], 0);
		}
		
//		System.out.println("letterFreq (encoder): " +letterFreq);
		
		PriorityQueue<HuffmanNode> pq = Huffman.generatePQ(letterFreq);
		HuffmanNode root = Huffman.generateTree(pq);
		
		HashMap<Character, String> letterToCodeword = new HashMap<Character, String>();
		Huffman.letterToCodewordFunc(letterToCodeword, root, "");
//		System.out.println("Updated HashMap (encoder): " +letterToCodeword);
		
		return letterToCodeword;
	}
	
	
	
	
	
}
