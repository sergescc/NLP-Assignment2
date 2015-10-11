package training;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import structs.Counts;
import structs.NGramView;
import training.exceptions.OOVFieldException;

public class NGramModel implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3911100506869204682L;

	private Integer vocabSize;

	private Map<String, Integer> vocabulary = new HashMap <String, Integer> ();
	private NGramView model = new NGramView ();
	private List<Counts> countsTable = new ArrayList<Counts>();
	private int NGramType;
	
	public NGramModel()
	{
		vocabSize = 0;
	}
	
	public NGramModel(String modelFile) throws IOException, ClassNotFoundException
	{
		loadModel(modelFile);
	}
	
	public void loadModel(String modelFile) throws IOException, ClassNotFoundException 
	{
		FileInputStream modelFileStream = new FileInputStream(modelFile);
		
		ObjectInputStream modelStream = new ObjectInputStream(modelFileStream);
		
		NGramModel readModel= (NGramModel)modelStream.readObject();
		
		vocabulary = readModel.getVocabulary();
		
		model = readModel.getModel();
		
		vocabSize = readModel.getVocabSize();
		
		modelStream.close();
		
	}
	
	public void persistModel(String modelFile) throws IOException
	{
		FileOutputStream modelFileStream = new FileOutputStream (modelFile);
		
		ObjectOutputStream modelOutStream = new ObjectOutputStream (modelFileStream);
		
		modelOutStream.writeObject(this);
		
		modelOutStream.close();
	}
	
	public NGramModel modelTrain (String trainingText, Integer depth, Double OOVField) throws IOException
	{
		setNGramType(depth);
		
		BufferedReader lines = new BufferedReader(new FileReader(trainingText));
		
		int numLines = 0;
		
		while (lines.readLine() != null ) numLines ++;
		lines.close();
		
		
		if (OOVField < 0 || OOVField > 1)
		{
			throw new OOVFieldException();
		}
		
		int partitionSize = (int)Math.floor(numLines * OOVField);
		
		List <String> OOVFieldPartition = new ArrayList<String>();
		
		BufferedReader text = new BufferedReader (new FileReader(trainingText));
		
		String newLine;
		int lineCounter = 0;
		
		while ((newLine = text.readLine()) != null)
		{
			
			newLine = "<start> " + newLine;
			
			if (lineCounter < partitionSize)
			{
				OOVFieldPartition.add(newLine);
			}
			else
			{
				processLine(newLine, depth, false);
			}
			
			lineCounter ++;
		}
		
		text.close();
		
		for ( String line : OOVFieldPartition)
		{
			processLine(line, depth, true);
		}
		
		populateCountsTable(depth);
		
		return this;	
	}
	
	private void populateCountsTable(Integer depth)
	{
		for (int i = 0; i < depth; i++)
		{
			Counts singleLayer = new Counts ();
			
			singleLayer.setTotalCounts(model.getTotalCounts(i));
			
			singleLayer.setUniqueCounts(model.getUniqueCounts(i));
			
			countsTable.add(singleLayer);
			
		}
	}
	
	private void processLine(String line, Integer depth, boolean OOVField)
	{
		String[] tokens = line.trim().split("\\s+");
		
		if ( tokens.length != 0)
		{
						
			for (int n = 0 ; n < depth; n++) 
			{
				for (int j = 0; j < tokens.length - n; j++)
				{
					List<Integer> ngram = new ArrayList<Integer> ();
					for ( int i = 0; i <= n; i++)
					{
						if (vocabulary.containsKey(tokens[j+i]))
						{
							ngram.add(vocabulary.get(tokens[j + i]));
						}
						else if ( !OOVField)
						{
							vocabulary.put(tokens[j + i], vocabSize++);
							ngram.add(vocabulary.get(tokens[j + i]));
						}
						else if (!vocabulary.containsKey(tokens[j+i]) && OOVField)
						{
							if (!vocabulary.containsKey("<oov>"))
							{
								vocabulary.put("<oov>", vocabSize++);
								
							}
							tokens[ j+ i] = "<oov>";
							
							ngram.add(vocabulary.get(tokens[j + i]));
						}
						else
						{
							ngram.add(vocabulary.get(tokens[ j+ i]));
						}
					}
					model.addNGram(ngram);
				}
			}
			
		}
		
	}
	
	public Long getNGramCount(List<String> ngram)
	{
		List <Integer> indexedNGram = new ArrayList<Integer>();
		
		for (String token : ngram)
		{
			if (vocabulary.containsKey(token))
			{
				indexedNGram.add(vocabulary.get(token));
			}
			else
			{
				if (vocabulary.containsKey("<oov>"))
				{
					indexedNGram.add(vocabulary.get("<oov>"));
				}
				else
				{
					return 0L;
				}
				
			}
		}
		return model.getLocalCounts(indexedNGram);
	}
	
	public void printModel()
	{
		
		if (!model.isEmpty())
		{
			System.out.println("\n\n Vocabulary Size: " + vocabSize);
			int counter = 1;
			for (Counts count : countsTable)
			{
				System.out.println(counter + "-gram: ");
				System.out.println("\tTotal Count: " + count.getTotalCounts());
				System.out.println("\tUnique Count:" + count.getUniqueCounts());
				
				counter ++;
			}
			
		}
		
		
	}
	

	public Map<String, Integer> getVocabulary() {
		return vocabulary;
	}

	public void setVocabulary(Map<String, Integer> vocabulary) {
		this.vocabulary = vocabulary;
	}

	public NGramView getModel() {
		return model;
	}

	public void setModel(NGramView model) {
		this.model = model;
	}
	
	public void setVocabSize (Integer vocabSize){
		this.vocabSize = vocabSize;
	}
	
	public Integer getVocabSize () {
		return vocabSize;
	}

	public int getNGramType() {
		return NGramType;
	}

	public void setNGramType(int nGramType) {
		NGramType = nGramType;
	}
	

}
