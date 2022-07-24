
public class NormalHuffmanDecoder {
	
	public static String decode(HuffmanNode root, String encodedStr) {
		
		String decoded = "";
		HuffmanNode currentNode = root;
		char[] encoded = encodedStr.toCharArray();

		for (int i = 0; i < encoded.length; i++) {			
			
			if (encoded[i] == '0')
				currentNode = currentNode.left;
			
			else if (encoded[i] == '1')
				currentNode = currentNode.right;
			
			// If this is NOT the leaf:
			if (currentNode.left != null && currentNode.right != null && root.symbol == Character.MIN_VALUE)
				continue;
			
			// If this is the leaf:
			else {
				
				// And now, when we reached the node with letter:
				decoded = decoded + currentNode.symbol;
				
				// Reset the currentNode to initial root
				currentNode = root;
				
			}
			
		}
		
		return decoded;
		
	}

}
