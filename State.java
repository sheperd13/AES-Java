//AES State class

public class State {
	public byte[][] data;
	
	public State(){
		data = new byte[4][4];
	}
	
	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append("State Data: \n");
		for(int i = 0; i < 4; i++){
			for(int j = 0; j < 4; j++){
				sb.append(String.format("0x%02x", data[i][j]));
				sb.append('\t');
			}
			sb.append('\n');
		}
		return sb.toString();
	}
}