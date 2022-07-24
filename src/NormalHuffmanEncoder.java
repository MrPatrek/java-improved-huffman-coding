import java.util.HashMap;

public class NormalHuffmanEncoder {

	public static String encode(HashMap<Character, String> letterToCodeword, String originalText) {
		
		String encoded = "";
		char[] text = originalText.toCharArray();

		for (int i = 0; i < text.length; i++) {
			
			String currentCodeword = letterToCodeword.get(text[i]);
			encoded = encoded + currentCodeword;
			
		}
		
		return encoded;

	}

}
