import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.StringTokenizer;


public class NaiveBayes {
	public static void main(String[] args) throws IOException
	{
		  //Prompt the user to input the file name of training data and index of the target column
		  Scanner input = new Scanner(System.in);
		  String fileName, testFileName;
		  int target;
		  System.out.print("Please enter file name: ");
		  fileName = input.nextLine();

	      File file = new File(fileName);
	      File output = new File("Result.txt");
		  Scanner inputFile = new Scanner(file);
		  String line;
		  line = inputFile.nextLine();
		  
		  //store the names of attributes
		  ArrayList<String> attributes = new ArrayList<String>();
		  StringTokenizer token = new StringTokenizer(line, " ");
		  while(token.hasMoreTokens())
			  attributes.add(token.nextToken());
		  
		  //Prompt the user to input the index of the target column
		  for(int i=0; i<attributes.size(); i++)
			  System.out.println(i+"\t"+attributes.get(i));
		  System.out.print("Please enter the index of target column(0 to n-1): ");
		  target = input.nextInt();
		  //find the number of rows in the training table
		  int rowCount =0;
		  while(inputFile.hasNext())
		  {
			  inputFile.nextLine();
			  rowCount++;
		  }
		  //two-dimensional array that stores the training data file
		  String[][] table = new String[rowCount][attributes.size()];
		  rowCount =0;
		  inputFile = new Scanner(file);
		  inputFile.nextLine();
		  while(inputFile.hasNext())
		  {
			  line = inputFile.nextLine();
			  token = new StringTokenizer(line, " ");
			  for(int i=0; i<attributes.size(); i++)
				  table[rowCount][i] = token.nextToken().toLowerCase();
			  rowCount++;
		  }

		  int trainingTableRowCount = rowCount;
		  //Create an arrayList of tables (new table for each distinct value in the target column)
		  ArrayList<HashMap<String, Integer>> distinctList = getDistinctElementsCount(table,attributes,table.length);
		  ArrayList<String[][]> tables = split(distinctList,table,attributes, target);
		  
		  //HashMaps that hold the count and probability of each element in the table
		  HashMap<String, HashMap<String, HashMap<String, Double>>> allProbabilities = new HashMap<String, HashMap<String, HashMap<String, Double>>>();
		  HashMap<String, HashMap<String, HashMap<String, Double>>> allCount = new HashMap<String, HashMap<String, HashMap<String, Double>>>();
		  //loop through the list of tables to store the count and probability of each element
		  for(int i=0; i<tables.size(); i++)
		  {
	
			  ArrayList<HashMap<String, Double>> currentTableCount = getDistinctElementsCount(tables.get(i), attributes, tables.get(i).length);
			  HashMap<String, HashMap<String, Double>> tableCount = new HashMap<String, HashMap<String,Double>>();
			  
			  ArrayList<HashMap<String, Double>> currentTableProbilities = getDistinctElementsProbabilites(tables.get(i), attributes);
			  HashMap<String, HashMap<String, Double>> tableProbabilities = new HashMap<String, HashMap<String,Double>>();
			  
			  for(int s=0; s<currentTableProbilities.size(); s++)
			  {
				  tableProbabilities.put(attributes.get(s), currentTableProbilities.get(s));
				  tableCount.put(attributes.get(s), currentTableCount.get(s));	  
			  }
			  allProbabilities.put(tables.get(i)[0][target], tableProbabilities);
			  allCount.put(tables.get(i)[0][target], tableCount);
		  }
		  
		  //Prompt the user to input the name of the file that contains testing data
		  System.out.print("Please enter test file name: ");
		  testFileName = String.valueOf(input.next());
	      File testfile = new File(testFileName);
	      inputFile = new Scanner(testfile);
	      line = inputFile.nextLine();
	      token = new StringTokenizer(line, " ");
	      //find the number of rows in the file to use it to declare two-dimensional array that stores the testing data
		  rowCount =0;
		  while(inputFile.hasNext())
		  {
			  inputFile.nextLine();
			  rowCount++;
		  }
		  //Store the testing data in a two-dimensional array
		  String[][] testTable = new String[rowCount][(attributes.size()+1)];
		  rowCount=0;
		  inputFile = new Scanner(testfile);
		  line = inputFile.nextLine();
		  while(inputFile.hasNext())
		  {
			  line = inputFile.nextLine();
			  token = new StringTokenizer(line, " ");
			  for(int i=0; i<attributes.size(); i++)
				  testTable[rowCount][i] = token.nextToken().toLowerCase();
			  testTable[rowCount][attributes.size()] = findResult(allProbabilities, attributes, testTable, rowCount, trainingTableRowCount, allCount, target);
			  rowCount++;
		  }
		  //add a new column for classification in the output file
		  attributes.add("Classification");
		  FileWriter w = new FileWriter(output);
		  BufferedWriter writer = new BufferedWriter(w);
		  int inconsistency =0;
		  String result = "";
		  //find the result table and write it to the Result.txt file
		  for(int i=0; i<attributes.size(); i++)
			  result+= attributes.get(i) +"\t\t";
		  for(int i=0; i<testTable.length; i++)
		  {
			  result +=(System.getProperty("line.separator"));
			  for(int j=0; j<attributes.size(); j++)
			  {
				  result += (testTable[i][j] + "\t\t\t");
			  }
			  if(testTable[i][attributes.size()-1].equals(testTable[i][attributes.size()-2]))
				  inconsistency++;
			  
		  }
		  result += (System.getProperty("line.separator"))+(System.getProperty("line.separator"))+"Accuracy: " + inconsistency+"/"+testTable.length;
		  writer.write(result);
		  writer.close();
		  System.out.println("Results has been written to Result.txt");
		
	}
	//Splits the table into new smaller tables, where each new table has only one distinct value in the target column
	public static ArrayList split(ArrayList<HashMap<String,Integer>> distinctList, String[][] table, ArrayList<String> attributes, int target) throws IOException
	{
		HashMap<String, Integer> currentMap = distinctList.get(target);//HashMap that contains all distinct values of the target column and the count of each one of them
		ArrayList<String[][]> newTables = new ArrayList<String[][]>();
		int currentCount =0;
		Iterator<HashMap.Entry<String, Integer>> iterate = currentMap.entrySet().iterator();
		//create new table for each element in the hashMap
		while(iterate.hasNext())
		{
			int count =0;
			Map.Entry currentEntryValue = (Map.Entry)iterate.next();
			int rowsCount = (int) (currentEntryValue.getValue());
			String[][] splitTable = new String[rowsCount][distinctList.size()];
			for(int j= 0; j<table.length; j++)
			{
				if(table[j][target].equals(currentEntryValue.getKey()))
				{
					splitTable[count] = table[j];
					count++;
				}
			}
			//add the generated table to the list of tables
			newTables.add(splitTable);
		}
		return newTables;
	}
	//Take table, attributes and Return an arraylist of HashMaps where each hashMap correspond to a column
	//The key of the hashMap produced is distinct elements and the value of HashMap is the occurrences of this element
	public static ArrayList getDistinctElementsCount(String[][] table, ArrayList<String> attributes, int rowCount)
	{
		  ArrayList<HashMap<String,Integer>> distinctList = new ArrayList<HashMap<String,Integer>>();
		  HashMap<String, Integer> distinctEntries = new HashMap<String,Integer>();
		  //loop through each column in the table
		  for(int i=0; i<attributes.size(); i++)
		  {
			  distinctEntries = new HashMap<String,Integer>();
			  //loop through each row in the table
			  for(int j=0; j<rowCount; j++)
			  {
				  //add distinct items to the hashMap and update their count
				  if(distinctEntries.containsKey(table[j][i]))
					  distinctEntries.put(table[j][i], distinctEntries.get(table[j][i]) +1);
				  else
					  distinctEntries.put(table[j][i],1);
			  }
			  distinctList.add(distinctEntries);
		  }
		  return distinctList;
	}
	//Take table, attributes and Return an arraylist of HashMaps where each hashMap correspond to a column
	//The key of the hashMap produced is distinct elements and the value of HashMap is the probability of this element
	public static ArrayList getDistinctElementsProbabilites(String[][] table, ArrayList<String> attributes)
	{
		  ArrayList<HashMap<String,Double>> probabilitiesList = new ArrayList<HashMap<String,Double>>();
		  HashMap<String, Double> probabilitiestEntries = new HashMap<String,Double>();

		  HashMap<String, Integer> distinctEntries;
		  ArrayList<HashMap<String, Integer>> distinctList = getDistinctElementsCount(table,attributes,table.length);
		  //loop through each column in the table
		  for(int i=0; i<attributes.size(); i++)
		  {
			  probabilitiestEntries = new HashMap<String,Double>();
			  distinctEntries = distinctList.get(i);
			  //iterate through each distinct element of column i and calculate the probability
			  for(Map.Entry<String, Integer> entry : distinctEntries.entrySet())
			  {
				  probabilitiestEntries.put(entry.getKey(), (double)entry.getValue()/(double)table.length);
			  }
			  probabilitiesList.add(probabilitiestEntries);
		  }
		  return probabilitiesList;
	}
	//Take hashMaps of count and probability of each element in the table, list of attributes, index of target column, the table and the index of the row that we should find classification result for
	//and return a value for the target column which is the element with the highest probability
	public static String findResult(HashMap<String, HashMap<String, HashMap<String, Double>>> allProbabilities, ArrayList<String> attributes, String[][] table, int rowCount, int trainingTableRowCount, HashMap<String, HashMap<String, HashMap<String, Double>>> allCount, int target)
	{
		String result;
		ArrayList<String> candidateResult = new ArrayList<String>();//list of names of all candidate result 
		ArrayList<Double> probabilities = new ArrayList<Double>();//list of probabilities for each candidate value
		//iterate through the HashMap of probabilities
		for(Entry<String, HashMap<String, HashMap<String, Double>>> entry : allProbabilities.entrySet())
		{
			double currentProbability = 0;
			candidateResult.add(entry.getKey());
			currentProbability = Math.log(table.length/trainingTableRowCount)/Math.log(2);
			//find the probability of each column in the current row and update the probability of the current candidate
			for(int j=0; j<attributes.size()-1; j++)
			{
				if(entry.getValue().get(attributes.get(j)).get(table[rowCount][j])!=null)
				{
					currentProbability +=  (Math.log(entry.getValue().get(attributes.get(j)).get(table[rowCount][j]))/Math.log(2));
				}
				else
				{
					currentProbability +=  Math.log((1/allCount.get(entry.getKey()).get(attributes.get(j)).size())/(Math.log(2)));
				}
			}
			probabilities.add(currentProbability);//add the probability of current candidate to the probability list
		}
		//find the candidate with highest probability and return it as the classification result of the current row
		double resultProbability = probabilities.get(0);
		result = candidateResult.get(0);
		for(int i=1; i<candidateResult.size(); i++)
		{
			if(resultProbability < probabilities.get(i))
			{
				resultProbability = probabilities.get(i);
				result = candidateResult.get(i);
			}
		}
		return result;
	}
}
