package probability.calculators;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import probability.calculators.exceptions.NGramDepthOutOfBoundsException;
import training.NGramModel;

public class AdditiveCalculator {
	
	private NGramModel trainedSet;
	
	private Double delta;
	
	public AdditiveCalculator(Double delta, NGramModel model)
	{
		setDelta(delta);
		setTrainedSet(model);
	}

	public NGramModel getTrainedSet() {
		return trainedSet;
	}

	public void setTrainedSet(NGramModel trainedSet) {
		this.trainedSet = trainedSet;
	}

	public Double getDelta() {
		return delta;
	}

	public void setDelta(Double delta) {
		this.delta = delta;
	}
	
	public Double probOfText(String textFile, int nGramType) throws IOException
	{
		
		if (nGramType > trainedSet.getNGramType())
		{
			throw new NGramDepthOutOfBoundsException();
		}
		Double acumulatedProb = 0.0;
		
		BufferedReader text = new BufferedReader( new FileReader(textFile));
		
		String line;
		
		while ((line = text.readLine()) != null)
		{
			line = "<start> " + line;
			acumulatedProb += probOfSentence(line, nGramType);
		}
		text.close();
		
		return acumulatedProb;
	}
	
	private Double probOfSentence (String line, int nGramType)
	{
		Double probability = 0.0;
		
		String [] tokens = line.trim().split("//s+");
		
		for (int i = 0; i < tokens.length - (nGramType-1) ; i++){
			List<String> ngram = new ArrayList<String> ();
			Long parentCount = 0L;
			for (int j = 0; j < nGramType; j++)
			{
				ngram.add(tokens[ i + j]);
				if (j == nGramType - 2)
				{
					parentCount = trainedSet.getNGramCount(ngram);
				}
			}
			
			Long nGramCount = trainedSet.getNGramCount(ngram);
			
			probability += Math.log10((delta + nGramCount)/(trainedSet.getVocabSize() + parentCount));
			
		}
		
		return probability;
	}

}
