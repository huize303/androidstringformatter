
public class Config {
	public String path1;
	public String path2;
	
	public boolean copyEachOther;

	public String getPath1() {
		return path1;
	}
	public Config setPath1(String path1) {
		this.path1 = path1;
		return this;
	}
	public String getPath2() {
		return path2;
	}
	public Config setPath2(String path2) {
		this.path2 = path2;
		return this;
	}
	public boolean isCopyEachOther() {
		return copyEachOther;
	}
	public Config setCopyEachOther(boolean copyEachOther) {
		this.copyEachOther = copyEachOther;
		return this;
	}
}
