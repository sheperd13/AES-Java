//AES Class for Project 1

public class AES {
	State state;
	//number of words in the key
	int Nk;
	//number of words in the block *always 4
	int Nb;
	//number of rounds
	int Nr;
	
	int[] w;
	byte[] key;
	
	//Constructors
	public AES(){
		state = new State();
		Nk = 4;
		Nb = 4;
		Nr = Nk + Nb + 2;
		w = new int[Nb * (Nr + 1)];
		key = new byte[16];
	}
	
	public AES(byte[] key){
		state = new State();
		this.Nk = key.length/4;
		this.Nb = 4;
		this.Nr = Nk + Nb + 2;
		w = new int[Nb * (Nr + 1)];
		this.key = new byte[key.length];
		this.key = key;
	}
	
	/*Cipher Functions*/
	//TODO: implement these
	public byte[] cipher(byte[] plainText){
		byte[] out = new byte[4*Nb];
		System.out.println("round[ " + 0 + "].input\t\t" + printByteArray(plainText));
		for(int i = 0; i < Nb; i++){
			state.data[0][i] = plainText[Nb * i];
			state.data[1][i] = plainText[Nb * i + 1];
			state.data[2][i] = plainText[Nb * i + 2];
			state.data[3][i] = plainText[Nb * i + 3];
		}
		
		System.out.println("round[ " + 0 + "].k_sch\t\t" + printKeySched(0));
		addRoundKey(w,0);
		for(int i = 1; i < Nr; i++){
			System.out.println("round[ " + i + "].start  \t" + printState());
			subBytes();
			System.out.println("round[ " + i + "].s_box  \t" + printState());
			shiftRows();
			System.out.println("round[ " + i + "].s_row  \t" + printState());
			mixColumns();
			System.out.println("round[ " + i + "].m_col  \t" + printState());
			System.out.println("round[ " + i + "].k_sch  \t" + printKeySched(i));
			addRoundKey(w,i*Nb);
		}
		
		System.out.println("round[ " + Nr + "].start\t" + printState());
		subBytes();
		System.out.println("round[ " + Nr + "].s_box\t" + printState());
		shiftRows();
		System.out.println("round[ " + Nr + "].s_row\t" + printState());
		addRoundKey(w,Nr * Nb);
		System.out.println("round[ " + Nr + "].k_sch\t" + printKeySched(Nr));
		
		for(int i = 0; i < Nb; i++){
			out[Nb * i]     = state.data[0][i];
			out[Nb * i + 1] = state.data[1][i];
			out[Nb * i + 2] = state.data[2][i];
			out[Nb * i + 3] = state.data[3][i];
		}
		System.out.println("round[ " + Nr + "].output\t" + printByteArray(out) + '\n');
		return out;
	}
	
	public State subBytes(){
		byte curByte = 0x00;
		int row_ind = 0, col_ind = 0;
		for(int i = 0; i < Nb; i++){
			for(int j = 0; j < Nb; j++){
				curByte = state.data[i][j];
				row_ind = (curByte & 0x0F);
				col_ind = ((curByte & 0xF0) >> 4);
				state.data[i][j] = (byte) Sbox[col_ind][row_ind];
			}
		}
		return state;
	}
	
	public State shiftRows(){
		int curRow;
		int byte0, byte1, byte2, byte3;
		int[] byte_array;
		for(int i = 0; i < Nb; i++){
			byte0 = state.data[i][0];
			byte1 = state.data[i][1];
			byte2 = state.data[i][2];
			byte3 = state.data[i][3];
	
			if(i == 1){
				state.data[i][0] = (byte)byte1;
				state.data[i][1] = (byte)byte2;
				state.data[i][2] = (byte)byte3;
				state.data[i][3] = (byte)byte0;
			}else if(i == 2){
				state.data[i][0] = (byte)byte2;
				state.data[i][1] = (byte)byte3;
				state.data[i][2] = (byte)byte0;
				state.data[i][3] = (byte)byte1;
			}else if(i == 3){
				state.data[i][0] = (byte)byte3;
				state.data[i][1] = (byte)byte0;
				state.data[i][2] = (byte)byte1;
				state.data[i][3] = (byte)byte2;
			}
		}
		return state;
	}
	
	public State mixColumns(){
		int byte0 = 0;
		int byte1 = 0;
		int byte2 = 0;
		int byte3 = 0;
		int const2 = 2, const3 = 3;
		for(int i = 0; i < Nb; i++){
			byte0 = ffAdd(ffAdd(ffAdd(ffMultiply((byte)const2,state.data[0][i]),ffMultiply((byte)const3,state.data[1][i])),state.data[2][i]),state.data[3][i]);
			byte1 = ffAdd(ffAdd(ffAdd(state.data[0][i],ffMultiply((byte)const2,state.data[1][i])),ffMultiply((byte)const3,state.data[2][i])),state.data[3][i]);
			byte2 = ffAdd(ffAdd(ffAdd(state.data[0][i],state.data[1][i]),ffMultiply((byte)const2,state.data[2][i])),ffMultiply((byte)const3,state.data[3][i]));
			byte3 = ffAdd(ffAdd(ffAdd(ffMultiply((byte)const3,state.data[0][i]),state.data[1][i]),state.data[2][i]),ffMultiply((byte)const2,state.data[3][i]));
			state.data[0][i] = (byte)byte0;
			state.data[1][i] = (byte)byte1;
			state.data[2][i] = (byte)byte2;
			state.data[3][i] = (byte)byte3;
		}
		return state;
	}
	
	public State addRoundKey(int[] words, int start){
		for(int i = 0; i < Nb; i++){
			state.data[0][i] ^= ((words[start + i] & 0xFF000000) >>> 24);
			state.data[1][i] ^= ((words[start + i] & 0x00FF0000) >> 16);
			state.data[2][i] ^= ((words[start + i] & 0x0000FF00) >> 8);
			state.data[3][i] ^=  (words[start + i] & 0x000000FF);
		}
		return state;
	}
	
	/*Inverse Cipher Functions*/
	//TODO: implement these
	public byte[] invCipher(byte[] cipherText){
		byte[] out = new byte[4*Nb];
		System.out.println("round[ " + 0 + "].iinput\t" + printByteArray(cipherText));
		for(int i = 0; i < Nb; i++){
			state.data[0][i] = cipherText[Nb * i];
			state.data[1][i] = cipherText[Nb * i + 1];
			state.data[2][i] = cipherText[Nb * i + 2];
			state.data[3][i] = cipherText[Nb * i + 3];
		}
		
		addRoundKey(w,(Nr * Nb));
		System.out.println("round[ " + 0 + "].ik_sch\t" + printKeySched(Nr));
		
		int round = 1;
		for(int i = Nr - 1; i > 0; i--){
			System.out.println("round[ " + round + "].istart\t" + printState());
			invShiftRows();
			System.out.println("round[ " + round + "].is_row\t" + printState());
			invSubBytes();
			System.out.println("round[ " + round + "].is_box\t" + printState());
			addRoundKey(w,i*Nb);
			System.out.println("round[ " + round + "].ik_sch\t" + printKeySched(i));
			System.out.println("round[ " + round + "].ik_add\t" + printState());
			invMixColumns();
			round++;
		}
		
		System.out.println("round[ " + Nr + "].istart\t" + printState());
		invShiftRows();
		System.out.println("round[ " + Nr + "].is_row\t" + printState());
		invSubBytes();
		System.out.println("round[ " + Nr + "].is_box\t" + printState());
		addRoundKey(w,0);
		System.out.println("round[ " + Nr + "].ik_sch\t" + printKeySched(0));
		
		for(int i = 0; i < Nb; i++){
			out[Nb * i]     = state.data[0][i];
			out[Nb * i + 1] = state.data[1][i];
			out[Nb * i + 2] = state.data[2][i];
			out[Nb * i + 3] = state.data[3][i];
		}
		System.out.println("round[ " + Nr + "].ioutput\t" + printByteArray(out) + '\n');
		return out;
	}
	
	public State invSubBytes(){
		byte curByte = 0x00;
		int row_ind = 0, col_ind = 0;
		for(int i = 0; i < Nb; i++){
			for(int j = 0; j < Nb; j++){
				curByte = state.data[i][j];
				row_ind = (curByte & 0x0F);
				col_ind = ((curByte & 0xF0) >> 4);
				state.data[i][j] = (byte) InvSbox[col_ind][row_ind];
			}
		}
		return state;
	}
	
	public State invShiftRows(){
		int curRow;
		int byte0, byte1, byte2, byte3;
		int[] byte_array;
		for(int i = 0; i < Nb; i++){
			byte0 = state.data[i][0];
			byte1 = state.data[i][1];
			byte2 = state.data[i][2];
			byte3 = state.data[i][3];
	
			if(i == 1){
				state.data[i][0] = (byte)byte3;
				state.data[i][1] = (byte)byte0;
				state.data[i][2] = (byte)byte1;
				state.data[i][3] = (byte)byte2;
			}else if(i == 2){
				state.data[i][0] = (byte)byte2;
				state.data[i][1] = (byte)byte3;
				state.data[i][2] = (byte)byte0;
				state.data[i][3] = (byte)byte1;
			}else if(i == 3){
				state.data[i][0] = (byte)byte1;
				state.data[i][1] = (byte)byte2;
				state.data[i][2] = (byte)byte3;
				state.data[i][3] = (byte)byte0;
			}
		}
		return state;
	}
	
	public State invMixColumns(){
		int byte0 = 0;
		int byte1 = 0;
		int byte2 = 0;
		int byte3 = 0;
		int constE = 0x0e, const9 = 0x09, constD = 0x0d, constB = 0x0b;
		for(int i = 0; i < Nb; i++){
			byte0 = ffAdd(ffAdd(ffAdd(ffMultiply((byte)constE,state.data[0][i]),ffMultiply((byte)constB,state.data[1][i])),ffMultiply((byte)constD,state.data[2][i])),ffMultiply((byte)const9,state.data[3][i]));
			byte1 = ffAdd(ffAdd(ffAdd(ffMultiply((byte)const9,state.data[0][i]),ffMultiply((byte)constE,state.data[1][i])),ffMultiply((byte)constB,state.data[2][i])),ffMultiply((byte)constD,state.data[3][i]));
			byte2 = ffAdd(ffAdd(ffAdd(ffMultiply((byte)constD,state.data[0][i]),ffMultiply((byte)const9,state.data[1][i])),ffMultiply((byte)constE,state.data[2][i])),ffMultiply((byte)constB,state.data[3][i]));
			byte3 = ffAdd(ffAdd(ffAdd(ffMultiply((byte)constB,state.data[0][i]),ffMultiply((byte)constD,state.data[1][i])),ffMultiply((byte)const9,state.data[2][i])),ffMultiply((byte)constE,state.data[3][i]));
			state.data[0][i] = (byte)byte0;
			state.data[1][i] = (byte)byte1;
			state.data[2][i] = (byte)byte2;
			state.data[3][i] = (byte)byte3;
		}
		return state;
	}
	
	/*Key Expansion Functions*/
	//TODO: implement these
	public void keyExpansion(){
		int temp = 0;
		
		for(int i = 0; i < Nk; i++){
			w[i] = ((key[4*i] 	  << 24) &	0xFF000000) | 
				   ((key[(4*i)+1] << 16) & 	0x00FF0000) |
				   ((key[(4*i)+2] << 8)  & 	0x0000FF00) | 
				   ((key[(4*i)+3])       &	0x000000FF);
		}

		for(int i = Nk; i < (Nb * (Nr+1)); i++){
			temp = w[i - 1];
			if(i % Nk == 0){
				temp = subWord(rotWord(temp)) ^ Rcon[i/Nk];
			}else if(Nk > 6 && i % Nk == 4){
				temp = subWord(temp);
			}
			w[i] = w[i - Nk] ^ temp;
		}
	}
	
	public int subWord(int word){
		int byte0 = (word & 0x000000FF);
		int byte1 = (word & 0x0000FF00) >> 8;
		int byte2 = (word & 0x00FF0000) >> 16;
		int byte3 = (word & 0xFF000000) >>> 24;
		
		int[] byte_array = {byte0, byte1, byte2, byte3};
		
		int row_ind = 0, col_ind = 0;
		for(int i = 0; i < Nb; i++){
			row_ind = (byte_array[i] & 0x0F);
			col_ind = (byte_array[i] & 0xF0) >> 4;
			byte_array[i] = Sbox[col_ind][row_ind];
		}
		
		word = byte_array[0] | (byte_array[1] << 8) | 
			(byte_array[2] << 16) | (byte_array[3] << 24);
			
		return word;
	}
	
	public int rotWord(int word){
		int rot_byte = (word & 0xFF000000) >>> 24;
		word = (word << 8) | rot_byte;
		return word;
	}
	
	/*FFMath Functions
	MAKE THESE PRIVATE WHEN DONE TESTING*/
	public byte ffAdd(byte a, byte b){
		return (byte)(a ^ b);
	}
	
	public byte xtime(byte a){
		if((a & (byte) 0x80) == 0){
			return (byte)(a << 1);
		} 
		else return (byte)((a << 1) ^ 0x1B);
	}
	
	public byte ffMultiply(byte a, byte b){
		byte result = 0;
		byte shift_bits = 1;
		byte xtime_value = a;
		do {
			if ((b & shift_bits) != 0){
				result = (byte)(result ^ xtime_value);
			}
			xtime_value = xtime(xtime_value);
			shift_bits = (byte)(shift_bits << 1);
		} while (shift_bits != 0
				&& ((int) shift_bits & 0x000000ff) <= ((int) b & 0x0000000ff));
				
		//System.out.println("Multiply output value: \n" + String.format("0x%02x",result) + '\n');
		return result;
	}
	
	//Sbox Stuff
	public String printSBox(){
		StringBuilder sb = new StringBuilder();
		sb.append("SBox Values: \n");
		for(int i = 0; i < SboxRows; i++){
			for(int j = 0; j < SboxCols; j++){
				sb.append("0x");
				sb.append(String.format("%02x",Sbox[i][j]));
				sb.append('\t');
			}
			sb.append('\n');
		}
		return sb.toString();
	}
	
	public String printInvSBox(){
		StringBuilder sb = new StringBuilder();
		sb.append("InvSBox Values: \n");
		for(int i = 0; i < SboxRows; i++){
			for(int j = 0; j < SboxCols; j++){
				sb.append("0x");
				sb.append(String.format("%02x",InvSbox[i][j]));
				sb.append('\t');
			}
			sb.append('\n');
		}
		return sb.toString();
	}
	
	public String printWords(){
		StringBuilder sb = new StringBuilder();
		sb.append("Key Word Values: \n");
		for(int i = 0; i < w.length; i++){
			sb.append("w" + i + ".\t0x");
			sb.append(String.format("%08x",w[i]));
			sb.append('\n');
		}
		return sb.toString();
	}
	
	public String printKey(){
		StringBuilder sb = new StringBuilder();
		sb.append("Key Values: \n");
		for(int i = 0; i < key.length; i++){
			sb.append("0x");
			sb.append(String.format("%02x",key[i]));
			sb.append('\n');
		}
		return sb.toString();
	}
	
	public String printState(){
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < 4; i++){
			for(int j = 0; j < 4; j++){
				sb.append(String.format("%02x", state.data[j][i]));
			}
		}
		return sb.toString();
	}
	
	public String printByteArray(byte[] in){
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < in.length; i++){
			sb.append(String.format("%02x",in[i]));
		}
		return sb.toString();
	}
	
	public String printKeySched(int i){
		StringBuilder sb = new StringBuilder();
		sb.append(String.format("%08x",w[Nb * i]));
		sb.append(String.format("%08x",w[Nb * i + 1]));
		sb.append(String.format("%08x",w[Nb * i + 2]));
		sb.append(String.format("%08x",w[Nb * i + 3]));
		return sb.toString();
	}
	
	private int SboxRows = 16;
	private int SboxCols = 16;
	private int[][] Sbox = {
	{ 0x63, 0x7c, 0x77, 0x7b, 0xf2, 0x6b, 0x6f, 0xc5, 0x30, 0x01, 0x67, 0x2b, 0xfe, 0xd7, 0xab, 0x76 } ,
	{ 0xca, 0x82, 0xc9, 0x7d, 0xfa, 0x59, 0x47, 0xf0, 0xad, 0xd4, 0xa2, 0xaf, 0x9c, 0xa4, 0x72, 0xc0 } ,
	{ 0xb7, 0xfd, 0x93, 0x26, 0x36, 0x3f, 0xf7, 0xcc, 0x34, 0xa5, 0xe5, 0xf1, 0x71, 0xd8, 0x31, 0x15 } ,
	{ 0x04, 0xc7, 0x23, 0xc3, 0x18, 0x96, 0x05, 0x9a, 0x07, 0x12, 0x80, 0xe2, 0xeb, 0x27, 0xb2, 0x75 } ,
	{ 0x09, 0x83, 0x2c, 0x1a, 0x1b, 0x6e, 0x5a, 0xa0, 0x52, 0x3b, 0xd6, 0xb3, 0x29, 0xe3, 0x2f, 0x84 } ,
	{ 0x53, 0xd1, 0x00, 0xed, 0x20, 0xfc, 0xb1, 0x5b, 0x6a, 0xcb, 0xbe, 0x39, 0x4a, 0x4c, 0x58, 0xcf } ,
	{ 0xd0, 0xef, 0xaa, 0xfb, 0x43, 0x4d, 0x33, 0x85, 0x45, 0xf9, 0x02, 0x7f, 0x50, 0x3c, 0x9f, 0xa8 } ,
	{ 0x51, 0xa3, 0x40, 0x8f, 0x92, 0x9d, 0x38, 0xf5, 0xbc, 0xb6, 0xda, 0x21, 0x10, 0xff, 0xf3, 0xd2 } ,
	{ 0xcd, 0x0c, 0x13, 0xec, 0x5f, 0x97, 0x44, 0x17, 0xc4, 0xa7, 0x7e, 0x3d, 0x64, 0x5d, 0x19, 0x73 } ,
	{ 0x60, 0x81, 0x4f, 0xdc, 0x22, 0x2a, 0x90, 0x88, 0x46, 0xee, 0xb8, 0x14, 0xde, 0x5e, 0x0b, 0xdb } ,
	{ 0xe0, 0x32, 0x3a, 0x0a, 0x49, 0x06, 0x24, 0x5c, 0xc2, 0xd3, 0xac, 0x62, 0x91, 0x95, 0xe4, 0x79 } ,
	{ 0xe7, 0xc8, 0x37, 0x6d, 0x8d, 0xd5, 0x4e, 0xa9, 0x6c, 0x56, 0xf4, 0xea, 0x65, 0x7a, 0xae, 0x08 } ,
	{ 0xba, 0x78, 0x25, 0x2e, 0x1c, 0xa6, 0xb4, 0xc6, 0xe8, 0xdd, 0x74, 0x1f, 0x4b, 0xbd, 0x8b, 0x8a } ,
	{ 0x70, 0x3e, 0xb5, 0x66, 0x48, 0x03, 0xf6, 0x0e, 0x61, 0x35, 0x57, 0xb9, 0x86, 0xc1, 0x1d, 0x9e } ,
	{ 0xe1, 0xf8, 0x98, 0x11, 0x69, 0xd9, 0x8e, 0x94, 0x9b, 0x1e, 0x87, 0xe9, 0xce, 0x55, 0x28, 0xdf } ,
	{ 0x8c, 0xa1, 0x89, 0x0d, 0xbf, 0xe6, 0x42, 0x68, 0x41, 0x99, 0x2d, 0x0f, 0xb0, 0x54, 0xbb, 0x16 }
	};
	
	private int[][] InvSbox = {
	{ 0x52, 0x09, 0x6a, 0xd5, 0x30, 0x36, 0xa5, 0x38, 0xbf, 0x40, 0xa3, 0x9e, 0x81, 0xf3, 0xd7, 0xfb } ,
	{ 0x7c, 0xe3, 0x39, 0x82, 0x9b, 0x2f, 0xff, 0x87, 0x34, 0x8e, 0x43, 0x44, 0xc4, 0xde, 0xe9, 0xcb } ,
	{ 0x54, 0x7b, 0x94, 0x32, 0xa6, 0xc2, 0x23, 0x3d, 0xee, 0x4c, 0x95, 0x0b, 0x42, 0xfa, 0xc3, 0x4e } ,
	{ 0x08, 0x2e, 0xa1, 0x66, 0x28, 0xd9, 0x24, 0xb2, 0x76, 0x5b, 0xa2, 0x49, 0x6d, 0x8b, 0xd1, 0x25 } ,
	{ 0x72, 0xf8, 0xf6, 0x64, 0x86, 0x68, 0x98, 0x16, 0xd4, 0xa4, 0x5c, 0xcc, 0x5d, 0x65, 0xb6, 0x92 } ,
	{ 0x6c, 0x70, 0x48, 0x50, 0xfd, 0xed, 0xb9, 0xda, 0x5e, 0x15, 0x46, 0x57, 0xa7, 0x8d, 0x9d, 0x84 } ,
	{ 0x90, 0xd8, 0xab, 0x00, 0x8c, 0xbc, 0xd3, 0x0a, 0xf7, 0xe4, 0x58, 0x05, 0xb8, 0xb3, 0x45, 0x06 } ,
	{ 0xd0, 0x2c, 0x1e, 0x8f, 0xca, 0x3f, 0x0f, 0x02, 0xc1, 0xaf, 0xbd, 0x03, 0x01, 0x13, 0x8a, 0x6b } ,
	{ 0x3a, 0x91, 0x11, 0x41, 0x4f, 0x67, 0xdc, 0xea, 0x97, 0xf2, 0xcf, 0xce, 0xf0, 0xb4, 0xe6, 0x73 } ,
	{ 0x96, 0xac, 0x74, 0x22, 0xe7, 0xad, 0x35, 0x85, 0xe2, 0xf9, 0x37, 0xe8, 0x1c, 0x75, 0xdf, 0x6e } ,
	{ 0x47, 0xf1, 0x1a, 0x71, 0x1d, 0x29, 0xc5, 0x89, 0x6f, 0xb7, 0x62, 0x0e, 0xaa, 0x18, 0xbe, 0x1b } ,
	{ 0xfc, 0x56, 0x3e, 0x4b, 0xc6, 0xd2, 0x79, 0x20, 0x9a, 0xdb, 0xc0, 0xfe, 0x78, 0xcd, 0x5a, 0xf4 } ,
	{ 0x1f, 0xdd, 0xa8, 0x33, 0x88, 0x07, 0xc7, 0x31, 0xb1, 0x12, 0x10, 0x59, 0x27, 0x80, 0xec, 0x5f } ,
	{ 0x60, 0x51, 0x7f, 0xa9, 0x19, 0xb5, 0x4a, 0x0d, 0x2d, 0xe5, 0x7a, 0x9f, 0x93, 0xc9, 0x9c, 0xef } ,
	{ 0xa0, 0xe0, 0x3b, 0x4d, 0xae, 0x2a, 0xf5, 0xb0, 0xc8, 0xeb, 0xbb, 0x3c, 0x83, 0x53, 0x99, 0x61 } ,
	{ 0x17, 0x2b, 0x04, 0x7e, 0xba, 0x77, 0xd6, 0x26, 0xe1, 0x69, 0x14, 0x63, 0x55, 0x21, 0x0c, 0x7d }
	};
	
	private int Rcon[] = { 0x00000000, // Rcon[] is 1-based, so the first entry is just a place holder 
		0x01000000, 0x02000000, 0x04000000, 0x08000000, 
		0x10000000, 0x20000000, 0x40000000, 0x80000000, 
		0x1B000000, 0x36000000, 0x6C000000, 0xD8000000, 
		0xAB000000, 0x4D000000, 0x9A000000, 0x2F000000, 
		0x5E000000, 0xBC000000, 0x63000000, 0xC6000000, 
		0x97000000, 0x35000000, 0x6A000000, 0xD4000000, 
		0xB3000000, 0x7D000000, 0xFA000000, 0xEF000000, 
		0xC5000000, 0x91000000, 0x39000000, 0x72000000, 
		0xE4000000, 0xD3000000, 0xBD000000, 0x61000000, 
		0xC2000000, 0x9F000000, 0x25000000, 0x4A000000, 
		0x94000000, 0x33000000, 0x66000000, 0xCC000000, 
		0x83000000, 0x1D000000, 0x3A000000, 0x74000000, 
		0xE8000000, 0xCB000000, 0x8D000000};
	
}