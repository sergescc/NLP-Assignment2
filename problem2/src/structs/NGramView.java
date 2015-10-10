package structs;

import java.util.HashMap;
import java.util.List;


public class NGramView extends HashMap<Integer, NGramView>
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1289718971767941572L;
	
	private Integer count;
	
	public NGramView()
	{
		count = 1;
	}
	public void addNGram (List<Integer> values)
	{
		if (values.size() == 1) 
		{
			Integer thisLevel = values.get(0);
			values.remove(0);
			
			if (this.containsKey(thisLevel))
			{
				this.get(thisLevel).addCount();
				
			}
			else
			{
				this.put(thisLevel, new NGramView ());
				
			}
		}
		else if (values.size() < 1)
		{
			throw new IllegalArgumentException();
		}
		else
		{	Integer thisLevel = values.get(0);
			values.remove(0);
			this.get(thisLevel).addNGram(values);
		}
		
	}
	
	public void addCount()
	{
		count++;
	}
	
	public Integer getCount()
	{
		return count;
	}
	
}