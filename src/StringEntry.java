
public class StringEntry  implements Comparable<StringEntry>{
	
	public String key;
	public String value;
	
	public StringEntry(String key,String value) {
		this.key = key;
		this.value = value;
	}
	
	
	@Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StringEntry that = (StringEntry) o;
        return key.equals(that.key);

    }

    @Override
    public int hashCode() {
        return key.hashCode();
    }
    
    @Override
    public String toString() {
    	return new StringBuilder().append("key = ").append(key).append(" value = ").append(value).toString();
    }


	@Override
	public int compareTo(StringEntry stringEntry) {
		
		return 0;
	}

}
