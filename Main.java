//Main for AES project

public class Main {
	public static void main(String[] args){
		
		//TEST 1 FOR 128 BIT KEY
		byte[] plainText = {
			(byte)0x00, (byte)0x11, (byte)0x22, (byte)0x33,
			(byte)0x44, (byte)0x55, (byte)0x66, (byte)0x77,
			(byte)0x88, (byte)0x99, (byte)0xaa, (byte)0xbb,
			(byte)0xcc, (byte)0xdd, (byte)0xee, (byte)0xff
		};
		
		byte[] key_128 = {
			(byte)0x00, (byte)0x01, (byte)0x02, (byte)0x03, 
			(byte)0x04, (byte)0x05, (byte)0x06, (byte)0x07, 
			(byte)0x08, (byte)0x09, (byte)0x0a, (byte)0x0b, 
			(byte)0x0c, (byte)0x0d, (byte)0x0e, (byte)0x0f
			};
			
		AES test1 = new AES(key_128);
		
		//TEST 2 FOR 192 BIT KEY
		byte[] key_192 = {
			(byte)0x00, (byte)0x01, (byte)0x02, (byte)0x03, 
			(byte)0x04, (byte)0x05, (byte)0x06, (byte)0x07, 
			(byte)0x08, (byte)0x09, (byte)0x0a, (byte)0x0b, 
			(byte)0x0c, (byte)0x0d, (byte)0x0e, (byte)0x0f,
			(byte)0x10, (byte)0x11, (byte)0x12, (byte)0x13,
			(byte)0x14, (byte)0x15, (byte)0x16, (byte)0x17,
			};
		
		AES test2 = new AES(key_192);
		
		//TEST 3 FOR 256 BIT KEY
		byte[] key_256 = {
			(byte)0x00, (byte)0x01, (byte)0x02, (byte)0x03, 
			(byte)0x04, (byte)0x05, (byte)0x06, (byte)0x07, 
			(byte)0x08, (byte)0x09, (byte)0x0a, (byte)0x0b, 
			(byte)0x0c, (byte)0x0d, (byte)0x0e, (byte)0x0f,
			(byte)0x10, (byte)0x11, (byte)0x12, (byte)0x13,
			(byte)0x14, (byte)0x15, (byte)0x16, (byte)0x17,
			(byte)0x18, (byte)0x19, (byte)0x1a, (byte)0x1b,
			(byte)0x1c, (byte)0x1d, (byte)0x1e, (byte)0x1f
			};
			
		AES test3 = new AES(key_256);
		
		//test 1
		System.out.println("128 BIT KEY TEST");
		test1.keyExpansion();
		System.out.println("Cipher");
		byte[] invCipherText1 = test1.cipher(plainText);
		System.out.println("Inverse Cipher");
		byte[] out1 = test1.invCipher(invCipherText1);
		System.out.println();
		
		//test 2
		System.out.println("192 BIT KEY TEST");
		test2.keyExpansion();
		System.out.println("Cipher");
		byte[] invCipherText2 = test2.cipher(plainText);
		System.out.println("Inverse Cipher");
		byte[] out2 = test2.invCipher(invCipherText2);
		System.out.println();
		
		//test 3
		System.out.println("256 BIT KEY TEST");
		test3.keyExpansion();
		System.out.println("Cipher");
		byte[] invCipherText3 = test3.cipher(plainText);
		System.out.println("Inverse Cipher");
		byte[] out3 = test3.invCipher(invCipherText3);
		System.out.println();
	}
	
	public static void resetState(AES thing, byte[][] testState){
		for(int i = 0; i < 4; i++){
			for(int j = 0; j < 4; j++){
				thing.state.data[i][j] = testState[i][j];
			}
		}
	}
}