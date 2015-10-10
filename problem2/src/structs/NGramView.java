package structs;

import java.util.HashMap;
import java.util.List;


public class NGramView extends HashMap<Integer, NGramView>
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1289718971767941572L;
	
	private Long count;
	
	public NGramView()
	{
		count = 1L;
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
	
	public Long getLocalCounts (List<Integer> nGram)
	{
		Long count = 0L;
		
		if (nGram.size() >= 1 && this.containsKey(nGram.get(0)))
		{
			Integer thisLevel = nGram.get(0);
			
			if (nGram.size() <= 1)
			{
				count = Long.valueOf(this.get(thisLevel).getCount());			
			}
			else
			{
				nGram.remove(0);
				count = this.get(thisLevel).getLocalCounts(nGram);
				
			}
		}
		
		return count;
	}
	
	
	public Long getTotalCounts(int depth)
	{
		Long totalCount = 0L;
		if ( depth <= 0)
		{
			for (Integer key : this.keySet())
			{
				totalCount += this.get(key).getCount();
			}
		}
		else
		{
			depth--;
			for ( Integer key: this.keySet())
			{
				totalCount += this.get(key).getTotalCounts(depth);
			}
		}
		
		
		return totalCount;
	}
	
	public Long getUniqueCounts (Integer depth)
	{
		Long uniqueCounts = 0L;
		if (depth <= 0)
		{
			uniqueCounts = Long.valueOf(this.size());
		}
		else
		{
			depth--;
			for (Integer key : this.keySet())
			{
				uniqueCounts += this.get(key).getUniqueCounts(depth--);
			}
		}
		
		return uniqueCounts;
	}
	
	
	public void addCount()
	{
		count++;
	}
	
	public Long getCount()
	{
		return count;
	}
	
}