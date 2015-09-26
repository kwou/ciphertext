import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Assn2 {
	public static void main(String[] args) {
		try {
			TextAnalyze tx = new TextAnalyze("ciphertext2.txt");
			tx.frequencyCount();
			// tx.getCharacterMap("key2.txt");
			tx.repeatingSubStrings(2);
			tx.repeatingSubStrings(3);
			tx.repeatingSubStrings(4);
			tx.repeatingSubStrings(5);
			tx.repeatingSubStrings(6);
			tx.getKey("key2.txt");
			tx.decryptPlayFair();
			// tx.repeatingSubStrings(7);
			// tx.getDefaultCharacterMap();
			// tx.printCharacterMap();
			tx.translateFromCharacterMap();
			// tx.bruteForce();
			// tx.getCharacterMap("key.txt");
			// tx.translateFromCharacterMap();
		} catch (IOException e) {
			System.out.println("Caught IOException!");
		}
	}

	public static class TextAnalyze {
		String text;
		String filename;
		String mostFreqEng = "etaoinshrdlcumwfgypbvkjxqz";
		Map<Character, FreqCountNode> m;
		Map<Character, Character> characterMap;

		public TextAnalyze(String filename) throws IOException {
			m = new TreeMap<Character, FreqCountNode>();
			this.filename = filename;
			getContents(filename);
		}

		public void printCharacterMap() {
			for (Map.Entry<Character, Character> ent : characterMap.entrySet()) {
				System.out.println(ent.getKey() + " " + ent.getValue());
			}
		}

		public void translateFromCharacterMap() {
			if (characterMap == null) {
				System.out.println("Populate character map first!");
				return;
			}

			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < text.length(); ++i) {
				char c = text.charAt(i);

				if (characterMap.containsKey(c)) {
					sb.append(characterMap.get(c));
				} else {
					sb.append(' ');
				}
			}
			System.out.println(text);
			System.out.println(sb.toString());
		}

		public void getDefaultCharacterMap() {
			List<FreqCountNode> l = new ArrayList<FreqCountNode>(m.values());
			Collections.sort(l, new FreqCountNodeCompare());

			characterMap = new HashMap<Character, Character>();
			for (int i = 0; i < l.size(); ++i) {
				char key = l.get(i).getChar();
				char value = mostFreqEng.charAt(i);
				characterMap.put(key, value);
			}
		}

		public void getCharacterMap(String filename) throws IOException {
			characterMap = new HashMap<Character, Character>();
			FileReader fr = new FileReader(filename);

			BufferedReader br = new BufferedReader(fr);
			String line;

			int diff = 'A' - 'a';
			while ((line = br.readLine()) != null) {
				if (line.length() < 3)
					continue;
				char key = line.charAt(0);
				char value = line.charAt(2);
				if (value >= 'A' && value <= 'Z') {
					value = (char) ((int) line.charAt(2) - diff); // convert to
																	// lowercase
				}
				characterMap.put(key, value);
			}
			br.close();
			fr.close();
		}

		public void bruteForce() {

			for (int i = 0; i < 26; ++i) {
				StringBuffer sb = new StringBuffer();

				for (int j = 0; j < text.length(); ++j) {
					int c = (int) text.charAt(j);
					// System.out.println("c: " + c + " " + (int)c);
					char p = (char) (((c - i) % 26) + 'A');
					// System.out.println((((int)c - i) % 26));
					// System.out.println("p: " + p);
					sb.append(p);
				}
				System.out.println(sb.toString());
				System.out.println();
			}
		}

		public void frequencyCount() {
			m.clear();
			int max = 0;

			for (int i = 0; i < 26; ++i) {
				char c = (char) ((int) 'A' + i);
				FreqCountNode node = new FreqCountNode(c);
				m.put(c, node);
			}

			for (int i = 0; i < text.length(); ++i) {
				char c = text.charAt(i);
				int count = 0;

				FreqCountNode node = m.get(c);
				node.incrementCount();
				count = node.getCount();
				max = Math.max(max, count);
			}

			// print values
			for (Map.Entry<Character, FreqCountNode> ent : m.entrySet()) {
				System.out.format("%c : %-3s ", ent.getKey(), ent.getValue().getCount());
				double count = (double) ent.getValue().getCount();
				// System.out.println("Count: " + count + " " + ((int)(count
				// *30)));
				// System.out.println("Max: " + max);
				count /= max;
				printHash((int) (count * 50));
				System.out.println();
			}

			System.out.println();
			List<FreqCountNode> l = new ArrayList<FreqCountNode>(m.values());
			Collections.sort(l, new FreqCountNodeCompare());

			for (FreqCountNode node : l) {
				System.out.format("%c : %-3s ", node.getChar(), node.getCount());
				double count = (double) node.getCount();
				// System.out.println("Count: " + count + " " + ((int)(count
				// *30)));
				// System.out.println("Max: " + max);
				count /= max;
				printHash((int) (count * 50));
				System.out.println();
			}
		}

		private void printHash(int n) {
			while (n-- > 0) {
				System.out.print("#");
			}
		}

		private void getContents(String filename) throws IOException {
			FileReader fr = new FileReader(filename);
			BufferedReader br = new BufferedReader(fr);
			String line;
			StringBuffer sb = new StringBuffer();

			while ((line = br.readLine()) != null) {
				sb.append(line);
			}
			text = sb.toString();
		}

		public void repeatingSubStrings(int length) {
			Map<String, StringNode> sc = new HashMap<String, StringNode>();

			System.out.println();
			System.out.println("Length: " + length);
			for (int i = 0; i <= (text.length() - length); ++i) {
				String sb = text.substring(i, i + length);
				if (sc.containsKey(sb)) {
					StringNode sn = sc.get(sb);
					sn.incrementCount();
				} else {
					StringNode sn = new StringNode(sb);
					sn.incrementCount();
					sc.put(sb, sn);
				}
			}

			List<StringNode> l = new ArrayList<StringNode>(sc.values());
			Collections.sort(l, new StringNodeCompare());
			for (StringNode sn : l) {
				System.out.println(sn.getChar() + " " + sn.getCount());
			}
			System.out.println();
		}

		/* Playfair cipher utilities */
		Map<Character, CipherNode> mc;
		char[][] cipher;

		public void getKey(String filename) throws IOException {
			mc = new HashMap<Character, CipherNode>();
			cipher = new char[5][5];
			FileReader fr = new FileReader(filename);
			BufferedReader br = new BufferedReader(fr);
			String line;
			StringBuffer sb = new StringBuffer();

			int i = 0;
			while ((line = br.readLine()) != null) {
				for (int j = 0; j < line.length(); ++j) {
					char c = line.charAt(j);
					mc.put(c, new CipherNode(c, i, j));
					cipher[i][j] = c;
				}
				++i;
			}

			br.close();
			fr.close();

			for (int k = 0; k < cipher.length; ++k) {
				for (int l = 0; l < cipher[k].length; ++l) {
					System.out.print(cipher[k][l] + " ");
				}
				System.out.println();
			}
		}

		public void decryptPlayFair() {
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < text.length(); i += 2) {
				char c1 = text.charAt(i);
				char c2 = text.charAt(i + 1);

				CipherNode c1n = mc.get(c1);
				CipherNode c2n = mc.get(c2);

				if (c1n.x == c2n.x) { // same row
					sb.append(cipher[c1n.x][(c1n.y + 4) % 5]);
					sb.append(cipher[c2n.x][(c2n.y + 4) % 5]);
				} else if (c1n.y == c2n.y) { // same column
					sb.append(cipher[(c1n.x + 4) % 5][c1n.y]);
					sb.append(cipher[(c2n.x + 4) % 5][c2n.y]);
				} else {
					sb.append(cipher[c1n.x][c2n.y]);
					sb.append(cipher[c2n.x][c1n.y]);
				}
				sb.append(' ');
				System.out.print(c1 + "" + c2 + " ");
			}
			System.out.println();
			System.out.println(sb.toString());
		}
	}

	public static class FreqCountNode {
		private int count;
		private char c;

		public FreqCountNode(char c) {
			this.c = c;
			count = 0;
		}

		public char getChar() {
			return c;
		}

		public int getCount() {
			return count;
		}

		public void incrementCount() {
			++count;
		}
	}

	public static class StringNode {
		private int count;
		private String s;

		public StringNode(String s) {
			this.s = s;
			count = 0;
		}

		public String getChar() {
			return s;
		}

		public int getCount() {
			return count;
		}

		public void incrementCount() {
			++count;
		}
	}

	public static class CipherNode {
		char c;
		int x;
		int y;

		public CipherNode(char c, int x, int y) {
			this.c = c;
			this.x = x;
			this.y = y;
		}
	}

	public static class StringNodeCompare implements Comparator<StringNode> {
		public int compare(StringNode a, StringNode b) {
			return b.count - a.count;
		}
	}

	public static class FreqCountNodeCompare implements Comparator<FreqCountNode> {
		public int compare(FreqCountNode a, FreqCountNode b) {
			return b.count - a.count;
		}
	}
}