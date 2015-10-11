package main;

import java.io.IOException;

import probability.calculators.AdditiveCalculator;
import training.NGramModel;

public class NGramTool {

	
	public static void main (String [] args) 
	{
		Boolean training = false;
		String modelFile = null;
		Double delta = null;
		Integer nGram = null;
		String dataFile= null;
		Double oovField = 0.0;
		
		for ( int i = 0; i < args.length; i++ )
		{
			if (args[i].matches("--train"))
			{
				if (i+1 >= args.length || args[i +1].contains("--"))
				{
					printHelp("\"--train\" must be folowed by training fileand an ngramlength.", true);
					System.exit(0);
				}
				else
				{
					training = true;
					dataFile = args[++i];
				}
				
				if (i+1 >= args.length || args[i +1].contains("--"))
				{
					printHelp("\"--train\" must be folowed by training file and an ngram length.", true);
				}
				else
				{
					nGram = Integer.valueOf(args[++i]);
				}
				
				
			}
			else if (args [i].matches("--test"))
			{
				if (i+1 >= args.length || args[i +1].contains("--"))
				{
					printHelp("\"--test\" must be folowed by tresting file.", true);
					System.exit(0);
				}
				else
				{	
					training = false;
					dataFile = args[++i];
				}
			}
			else if (args[i].matches("--model"))
			{
				if (i+1 >= args.length || args[i +1].contains("--"))
				{
					printHelp("\"--model\" must be folowed by model file.", true);
					System.exit(0);
				}
				else
				{
					modelFile = args[++i];
				}
			}
			else if (args[i].matches("--delta"))
			{
				if (i+1 >= args.length || args[i +1].contains("--"))
				{
					printHelp("\"--model\" must be folowed by a delta value.", true);
					System.exit(0);
				}
				else
				{
					delta = Double.valueOf(args[++i]);
				}
			}
			else if (args[i].matches("--OOV"))
			{
				if (i+1 >= args.length || args[i + 1].contains("--"))
				{
					
				}
				else
				{
					oovField = Double.valueOf(args [++i]);
				}
			}
			else
			{
				printHelp("",false);
			}
			
			
		}
		
		if (training)
		{
			if (dataFile == null || nGram == null || modelFile == null)
			{
				printHelp("Training requires: Training File, ngram length and model file", true);
				System.exit(0);
			}
			NGramModel model = new NGramModel();
			
			try {
				model.modelTrain(dataFile, nGram, oovField);
			} catch (IOException e) {
				printHelp("Could Not Open or Read Required Files", true);
			}
			
			model.printModel();
			
			try {
				model.persistModel(modelFile);
			} catch (IOException e) {
				
				printHelp("Could Not Open or Read Required Files", true);
			}
		}
		else
		{
			if (dataFile == null || delta == null || modelFile == null)
			{
				printHelp( "Testing requires testing file, model file and delta value.", true);
				System.exit(0);
				
			}
			
			try {
				
				NGramModel model = new NGramModel(modelFile);
				
				AdditiveCalculator propability = new AdditiveCalculator(delta, model);
				
				Double prob = propability.probOfText(dataFile, model.getNGramType());
				
				System.out.println("Probability: " + prob);
				
			} catch ( Exception e)
			{
				printHelp("Counld not open file or other error occur", true);
			}
			
		}
	}
	
	public static void printHelp(String error, boolean isError)
	{
		if (isError) System.out.println("[\033[31m ERR \033[m] " + error);
		System.out.println("Usage:");
		System.out.println("\t --train <trainingFile> <ngram-length> : \tset to training mode must be followed by training file and n-gram length"
				+ "\t\t\t ngram length and model file must be specified");
		System.out.println("\t --test <testingFile> : \tset to testing mode must be followed by testing file"
				+ "\t\t\t model file must be specified and delta must be specified");
		System.out.println("\t --model <modelFile> : \tset model file to use"
				+ "\t\t\t must be used in conjucntion with test or train");
		System.out.println("\t");
	}
}
