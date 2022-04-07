/*
 * this class is the processDict
 * it arose from the need to keep all information about a given process in one place with 
 * convenient getters and setters
 */
public class ProcessDict {
	int key;
	Integer[] alloc, max, need;

	public ProcessDict(int key, Integer[] alloc, Integer[] max, Integer[] need) {
		this.key = key;
		this.alloc = alloc;
		this.max = max;
		this.need = need;
	}

	/*
	 * this method determines whether the process is executable with the given array
	 * of available resources
	 */
	public boolean isExecutable(Integer[] available) {
		for (int i = 0; i < available.length; i++) {
			if (getNeed()[i] > available[i]) {
				return false;
			}
		}
		return true;
	}

	public int getKey() {
		return key;
	}

	public void setKey(int key) {
		this.key = key;
	}

	public Integer[] getAlloc() {
		return alloc;
	}

	public void setAlloc(Integer[] alloc) {
		this.alloc = alloc;
	}

	public Integer[] getMax() {
		return max;
	}

	public void setMax(Integer[] max) {
		this.max = max;
	}

	public Integer[] getNeed() {
		return need;
	}

	public void setNeed(Integer[] need) {
		this.need = need;
	}

}
