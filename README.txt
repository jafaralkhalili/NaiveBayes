This program is an implementation of NAÏVE BAYES CLASSIFIER in Java
   
-Overview the program code (NAÏVE BAYES CLASSIFIER):

	The program reads a training data file and use the information in the file to calculate the probability of each element in the table,
	then the program reads a testing data file and use the information obtained from the training data file to create a NAÏVE BAYES CLASSIFIER and predict
	the value of the target column. The program write the result table to an external file called Result.txt
 
 
-program structure:
  
	main()—> 
		Read training data and store it using proper data structure.
		Generate the count and probability of each element in the training data using getDistinctElementsCount and getDistinctElementsProbabilities methods.
		Read testing data and store it using proper data structure and call findResult to find classification result for each row in the table.
		Write the result table in Result.txt file.

	getDistinctElementsCount()->
		for each column of the table, store the values and count of distinct elements into HashMap(Key is elmenet, Valus is count of element),
		return an arrayList that contains the HashMaps that were found.
		
	getDistinctElementsProbabilities()->
		for each column of the table, store the values and probability of distinct elements into HashMap(Key is elmenet, Valus is count of element),
		return an arrayList that contains the HashMaps that were found.

	Split()—> 
		Generates an arrayList that contains new tables by creating a new table for each distinct value in the target column.
		
	findResult()->
		find the classification result for a specific row in a table by calculating the probability of each distinct element in the target table,
		and return the element with the highest probability.

	
  
Run the program:
   Compile: javac NaiveBayes.java.     
   Run: java NaiveBayes
                      
