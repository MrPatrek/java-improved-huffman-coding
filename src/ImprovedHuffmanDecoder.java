import java.util.HashMap;
import java.util.PriorityQueue;

public class ImprovedHuffmanDecoder {
	
	// take all these from the encoder so that their "rules" are the same
	
	static final int howManyLastLettersToRead = ImprovedHuffmanEncoder.howManyLastLettersToRead;
	static final int afterWhatLetterBeginAdaptive = ImprovedHuffmanEncoder.afterWhatLetterBeginAdaptive;
	static final int adaptSecUpdFreq = ImprovedHuffmanEncoder.adaptSecUpdFreq;
	
	
	public static String decode(HuffmanNode root, String encodedStr, int messLen) {
		
		int adaptLen = messLen - afterWhatLetterBeginAdaptive;
//		System.out.println("adaptLen: " +adaptLen);
		double adaptSectorsDb = (double) adaptLen / adaptSecUpdFreq;
		int adaptSectors = (int) Math.ceil(adaptSectorsDb);
//		System.out.println("adaptSectors: " +adaptSectors);
//		System.out.println();
		

		
		String decoded = "";
		HuffmanNode currentNode = root;
		char[] encoded = encodedStr.toCharArray();
		

		int lettersDecoded = 0;
		int lastDecodedBitPos = -437547;		// any value, it will be changed anyway, and needs to be initialized
		for (int i = 0; i < encoded.length && lettersDecoded < afterWhatLetterBeginAdaptive; i++) {	// for loop should work for only first zeroSecLen letters (before going to the adaptive part)
			
			if (encoded[i] == '0')
				currentNode = currentNode.left;
			else if (encoded[i] == '1')
				currentNode = currentNode.right;
			
			if (currentNode.left != null && currentNode.right != null && root.symbol == Character.MIN_VALUE) // If this is NOT the leaf
				continue;
			else { // If this is the leaf (when we reached the node with letter)
				decoded = decoded + currentNode.symbol;
				currentNode = root;// Reset the currentNode to initial root
				lettersDecoded++;
				lastDecodedBitPos = i;
			}

		}
		
//		System.out.println("lettersDecoded: " +lettersDecoded);				// returns 150
//		System.out.println("decoded.length(): " +decoded.length());			// returns 150
		
			
	
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
			
			
			
			String last150DecodedLetters;
			int lettersEncodedSoFar = messLen - lenLeft;
			
			if (lettersEncodedSoFar >= howManyLastLettersToRead)
				last150DecodedLetters = decoded.substring(newStartIndex - howManyLastLettersToRead, newStartIndex);
			else			// lettersEncodedSoFar < howManyLastLettersToRead
				last150DecodedLetters = decoded.substring(newStartIndex - lettersEncodedSoFar, newStartIndex);
			
			
			
//			System.out.println("last150DecodedLetters: " +last150DecodedLetters);
//			System.out.println("last150DecodedLetters.length(): " +last150DecodedLetters.length());
//			System.out.println("Substring from indices " +(decoded.length() - howManyLastLettersToRead)+ " to " +(decoded.length() - 1));
			
			
			root = updateTree(last150DecodedLetters);
			currentNode = root;			// ÑÓÊÀ, ×ÅÃÎ ÌÍÅ ÍÅ ÕÂÀÒÀËÎ ! ! ! ! ! ß ÍÅ ðåñåòèë currentNode ! ! ! ß åáàë ýòó õóéíþ â ðîò!
			
			
			// Ïðîâåðÿé îòñþäà. Äî ýòîãî âñ¸ ÷èñòî.
			
			
			// Important! int startFrom = last decoded bit position, increased by 1. So that we do not start new decoding with already decoded bit before
			int startFrom = lastDecodedBitPos + 1;
			
			int lettersWillBeDecoded = lettersDecoded + currentSecLen;
			for (int i = startFrom ; i < encoded.length && lettersDecoded < lettersWillBeDecoded ; i++) {
				
				if (encoded[i] == '0')
					currentNode = currentNode.left;
				else if (encoded[i] == '1')
					currentNode = currentNode.right;
				
				if (currentNode.left != null && currentNode.right != null && root.symbol == Character.MIN_VALUE) // If this is NOT the leaf
					continue;
				else { // If this is the leaf (when we reached the node with letter)
					decoded = decoded + currentNode.symbol;
					currentNode = root;// Reset the currentNode to initial root
					lettersDecoded++;
					lastDecodedBitPos = i;
//					System.out.println("lettersDecoded: " +lettersDecoded);
//					System.out.println("lastDecodedBitPos: " +lastDecodedBitPos);
				}
				
			}
			
			
			lenLeft = lenLeft - adaptSecUpdFreq;
			
		}
	

		return decoded;
		
	}
	
	
	
	
	
	
	
	public static HuffmanNode updateTree(String last150Letters) {
		
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
		
		PriorityQueue<HuffmanNode> pq = Huffman.generatePQ(letterFreq);
		HuffmanNode root = Huffman.generateTree(pq);
		
		// HERE, WE DON'T EVEN NEED TO CREATE A letterToCodeword HashMap
		// We just return the root (the tree)
		
		//BUT! For now I'll do that, just to print the HashMap (for testing purposes)
		HashMap<Character, String> letterToCodeword = new HashMap<Character, String>();
		Huffman.letterToCodewordFunc(letterToCodeword, root, "");
//		System.out.println("Updated HashMap (decoder): " +letterToCodeword);
		
		return root;
	}
	
	
	

}
