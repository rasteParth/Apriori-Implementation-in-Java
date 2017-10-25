import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;

public class SilentGuardian {

	public static void main(String[] args) throws IOException {
		Scanner scanner = new Scanner(System.in);
		String line;
		System.out.println("Enter support threshold (It will be taken in percentage)");
		line = scanner.nextLine();
		double sup = Double.parseDouble(line);
		System.out.println("Enter confidence threshold (It will be taken in percentage)");
		line = scanner.nextLine();
		double conf = Double.parseDouble(line);
		System.out.println("Enter the database to be operated on: 1, 2, 3, 4, 5");
		line = scanner.nextLine();
		String path = "";
		switch (line) {
		case "1":
			path = "C:\\Database1.txt";
			break;
		case "2":
			path = "C:\\Database2.txt";
			break;
		case "3":
			path = "C:\\Database3.txt";
			break;
		case "4":
			path = "C:\\Database4.txt";
			break;
		case "5":
			path = "C:\\Database5.txt";
			break;
		default:
			System.out.println("Invalid database number!");
		}
		File file = new File(path);
		Scanner in = new Scanner(file);
		List<String> transactionList = new ArrayList<String>();
		Set<String> itemSet = new TreeSet<String>();
		Map<String, Integer> finalMap = new HashMap<String, Integer>();
		int a = 0;
		while (in.hasNextLine()) {
			line = in.nextLine();
			line = line.trim();
			transactionList.add(line);
			a++;
			String[] itemArray = line.split("\\s");
			for (int i = 0; i < itemArray.length; i++) {
				itemSet.add(itemArray[i]);
				itemSet.remove("");
			}
		}
		GetApriori(transactionList, itemSet, sup, conf);
	}

	public static void GetApriori(List<String> transactionList, Set<String> itemSet, double sup, double conf) {

		Map<String, Integer> supports = new HashMap<String, Integer>();
		for (String item : itemSet) {
			for (int j = 0; j < transactionList.size(); j++) {
				if (transactionList.get(j).contains(item)) {
					if (supports.containsKey(item)) {
						supports.put(item, (supports.get(item) + 1));
					} else {
						supports.put(item, 1);
					}
				}
			}
		}
		double supportThreshold = (sup / 100) * transactionList.size();
		double confidenceThreshold = (conf / 100) * transactionList.size();

		for (String item : itemSet) {
			if (supports.get(item) < supportThreshold) {
				supports.remove(item);
			}
		}
		itemSet.addAll(supports.keySet());
		List<List> tranList = new ArrayList<List>();
		for (int l = 0; l < transactionList.size(); l++) {
			String[] tranArray = transactionList.get(l).split(" ");
			tranList.add(Arrays.asList(tranArray));
		}
		Map<String, Integer> returnedSupports = new HashMap<String, Integer>();
		returnedSupports = FurtherIterations(transactionList, itemSet, supportThreshold, confidenceThreshold, tranList);
		supports.putAll(returnedSupports);

		System.out.println("Transactions are:");
		for (int i = 0; i < transactionList.size(); i++) {
			System.out.println("T" + (i + 1) + " -----> " + transactionList.get(i));
		}
		System.out.println();
		System.out.println("Frequent itemsets are:");
		for (String s : supports.keySet()) {
			System.out.println(s + " ------> " + supports.get(s) * 100 / 20);
		}
		GetAssociation(supports, tranList, conf);
	}

	public static Map<String, Integer> FurtherIterations(List<String> transactionList, Set<String> itemSet,
			double supportThreshold, double confidenceThreshold, List<List> tranList) {
		Map<String, Integer> supports2 = new HashMap<String, Integer>();
		List<List> keyList = new ArrayList<List>();
		List<String> tempList1 = new ArrayList<String>();
		List<String> tempList2 = new ArrayList<String>();
		Set<String> tempSet = new TreeSet<String>();
		int i = 0;
		for (String item : itemSet) {
			String[] temp1 = item.split(" ");
			// tempList1.clear();
			for (int j = 0; j < temp1.length; j++) {
				tempList1.add(temp1[j]);
			}
			for (String items : itemSet) {
				String[] temp2 = items.split(" ");
				for (int j = 0; j < temp2.length; j++) {
					tempList2.add(temp2[j]);
				}
				if (tempList1.containsAll(tempList2) == false) {
					String[] temporary = items.split(" ");
					String tempString = "";
					for (int k = 0; k < temporary.length; k++) {
						if (tempList1.contains(temporary[k]) == false) {
							tempString = tempString + temporary[k] + " ";
						}
					}
					tempString = tempString.trim();
					tempSet.add(item + " " + tempString);
					tempList2.clear();
				} else {
					tempList2.clear();
				}
			}
		}
		itemSet = tempSet;
		for (String item : itemSet) {
			String[] temp = item.split(" ");
			keyList.add(Arrays.asList(temp)); // convert keylist elements from
												// array to list
			i++;
		}
		int loopNumber = keyList.size();
		List<List> tempKeyList = new ArrayList<List>();
		tempKeyList = keyList;
		List<List> transactList = new ArrayList<List>();

		itemSet.clear();
		for (int l = 0; l < tranList.size(); l++) {

			String checkKey = "";

			for (int k = 0; k < keyList.size(); k++) {

				if (tranList.get(l).containsAll(keyList.get(k))) {
					checkKey = "";
					for (int n = 0; n < keyList.get(k).size(); n++) {
						checkKey = checkKey + keyList.get(k).get(n) + " ";
					}

				}
				checkKey = checkKey.trim();
				if (supports2.containsKey(checkKey)) {
					supports2.put(checkKey, supports2.get(checkKey) + 1);
				} else {
					supports2.put(checkKey, 1);
					itemSet.add(checkKey);
				}
				supports2.remove("");
				itemSet.remove("");
				checkKey = "";
			}

		}
		for (String item : itemSet) {
			if (supports2.get(item) < supportThreshold) {
				supports2.remove(item);
			}
		}
		itemSet = supports2.keySet();
		if (itemSet.size() > 1) {
			Map<String, Integer> returnedSupports = new HashMap<String, Integer>();
			returnedSupports = FurtherIterations(transactionList, itemSet, supportThreshold, confidenceThreshold,
					tranList);
			supports2.putAll(returnedSupports);
		}

		return supports2;
	}

	public static void GetAssociation(Map<String, Integer> supports, List<List> tranList, double conf) {
		Set<String> newKeySet = new TreeSet<String>();
		newKeySet.addAll(supports.keySet());
		for (String s : newKeySet) {
			String[] sArray = s.split(" ");
			if (sArray.length <= 1) {
				supports.remove(s);
			}
		}
		newKeySet.clear();
		newKeySet.addAll(supports.keySet());
		List<String> AList = new ArrayList<String>();
		List<String> BList = new ArrayList<String>();
		List<String> CList = new ArrayList<String>();
		int index = 0;
		int value = 0;

		for (String s : newKeySet) {
			String aAss = "";
			String bAss = "";
			int size = 1, a = 0;
			String[] sArray = s.split(" ");

			while (size <= sArray.length / 2) {
				String finalElementA = "";
				String finalElementB = "";
				int m = a + size - 1;
				while (a < sArray.length) {
					m = a + size - 1;

					for (int i = 0; i < sArray.length / 2; i++) {
						if (sArray.length % 2 == 1) {
							for (int j = 0; j < sArray.length; j++) {
								if (j < a || j > m) {
									finalElementB = finalElementB + " " + sArray[j];
								} else {
									finalElementA = finalElementA + " " + sArray[j];
								}
							}
							AList.add(finalElementA);
							AList.add(finalElementB);
							BList.add(finalElementB);
							BList.add(finalElementA);
							CList.add(s);
							CList.add(s);
							finalElementA = "";
							finalElementB = "";
						} else {
							for (int j = 0; j < sArray.length; j++) {
								if (j < a || j > m) {
									finalElementB = finalElementB + " " + sArray[j];
								} else {
									finalElementA = finalElementA + " " + sArray[j];
								}
							}
							AList.add(finalElementA);
							BList.add(finalElementB);
							CList.add(s);
							finalElementA = "";
							finalElementB = "";
						}
					}
					a++;
				}
				size++;
			}

		}
		List<Integer> A = new ArrayList<Integer>();
		List<Integer> B = new ArrayList<Integer>();
		List<Integer> C = new ArrayList<Integer>();
		A = GetFinalSupports(AList, tranList);
		B = GetFinalSupports(AList, tranList);
		C = GetFinalSupports(CList, tranList);
		GetConfidence(AList, BList, A, C, conf);
	}

	public static List<Integer> GetFinalSupports(List<String> AList, List<List> transactionList) {
		List<Integer> A = new ArrayList<Integer>();
		List<List> tempList = new ArrayList<List>();
		int i = 0, count = 0;
		while (i < AList.size()) {
			String s = AList.get(i).trim();
			String[] tempArray = s.split(" ");
			tempList.add(Arrays.asList(tempArray));
			i++;
		}
		for (int k = 0; k < tempList.size(); k++) {
			for (int j = 0; j < transactionList.size(); j++) {
				if (transactionList.get(j).containsAll(tempList.get(k))) {
					count++;
				}
			}
			A.add(count);
			count = 0;
		}
		return A;
	}

	public static void GetConfidence(List<String> AList, List<String> BList, List<Integer> A, List<Integer> C,
			double confidenceThreshold) {
		double confidence = 0;
		System.out.println();
		System.out.println("Associations are:");
		for (int i = 0; i < AList.size(); i++) {
			confidence = (C.get(i) * 100 / A.get(i));
			if (confidence >= confidenceThreshold) {
				System.out.println(AList.get(i) + " -----> " + BList.get(i) + " {Support:" + C.get(i) * 100 / 20
						+ " Confidence:" + confidence + "}");
			}
		}

	}
}
