

import java.util.ArrayList;

public class StringArryEntry implements Comparable<StringEntry>{
	public String key;
	public ArrayList<String> value = new ArrayList<>();

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public ArrayList<String> getValue() {
		return value;
	}

	public void setValue(ArrayList<String> value) {
		this.value = value;
	}

	public void addValue(String value) {
		this.value.add(value);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder().append("key = ").append(key).append(" /n");
		sb.append("[");
		for(String val : value) {
			sb.append(val).append(",");
		}
		sb.append("]");
		return sb.toString();
	}
	
	@Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StringArryEntry that = (StringArryEntry) o;
        return key.equals(that.key);

    }

    @Override
    public int hashCode() {
        return key.hashCode();
    }

	@Override
	public int compareTo(StringEntry o) {
		return 0;
	}

}
