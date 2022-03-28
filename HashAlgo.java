/*\
 * Hashing algorithm inspired by reading sha256 source code:
 *
 * https://github.com/B-Con/crypto-algorithms/blob/master/sha256.c
 *
 * Everything written is for informational purposes only.
 *
 * @author: ZERDICORP
*/



public class HashAlgo
{
	/* initial hash */
	private int a = 0x2104E132;
	private int b = 0x177B888A;
	private int c = 0x702A033C;
	private int d = 0x61AC5D94;
	private int e = 0x456FF52;
	private int f = 0xE1115AC;
	private int g = 0x31274FB4;
	private int h = 0x27CD73AA;



	/* auxiliary constants */
	private static final int[] aux = new int[]
	{
		0x45356892, 0x3c5e8a5f, 0x3077fa6a, 0x1ccc2fb9,
		0x7d10f6f9, 0x67dad7ef, 0x58df339f, 0x2d75ac0e,
		0x64990010, 0xedd6320, 0x6aac8aa0, 0x1b5768f0,
		0xf496ee9, 0x266ef1cf, 0x1ca70143, 0x7e82e311,
		0x1b020acf, 0x7e78dd0, 0x7b85c589, 0x37116168,
		0x637f0baf, 0x5b2dd45a, 0x4ef5cbe6, 0x2c61e85c,
		0x3637f5c2, 0x29b9cad5, 0x5a222e00, 0x30cb32d2,
		0x225bea5, 0x4c443ebc, 0xa5053e9, 0x513ab57e,
		0xe1138f0, 0x592baa80, 0x740e41d, 0x6e59a8a0,
		0x132ac887, 0x11b7a4d4, 0x7e267b02, 0x36f11a99,
		0x30cc5f43, 0x883d5a6, 0x69232cc1, 0x79660924,
		0x966c0ec, 0x5b7b760a, 0x25dcc15, 0x44468528,
		0x77bb0f3f, 0x5fee54ef, 0x2dffad7, 0x7400dc09,
		0x2ea19ecc, 0x7686651, 0x428e02ae, 0x15fdc9d1,
		0x3cbf1260, 0x1014d93b, 0x47a9755c, 0x7776b3a9,
		0x58a8c011, 0x4395b5a8, 0x516f9411, 0x4880ea6b
	};



	/* bitwise right-rotating */
	private static int rotr(int i, int n) { return ((i >> n) | (i << (32 - n))); }



	/* main hash function */
	private void update(byte[] cluster)
	{
		int w1, w2, w3, i, j;

		int[] sheet = new int[64];



		/*\
		 * Copy cluster[] bits into sheet[],
		 * concatenating every 4 bytes
		 * into a 32 bit word.
		 */

		j = 0;
		for (i = 0; i < 16; ++i, j += 4)
			sheet[i] = ((0xFF & cluster[j + 0]) << 24) |
								 ((0xFF & cluster[j + 1]) << 16) |
								 ((0xFF & cluster[j + 2]) << 8) |
								 (0xFF & cluster[j + 3]);



		/*\
		 * Filling a sheet with some random
		 * transformations of it's contents.
		 */

		for (i = 16; i < sheet.length; ++i)
		{
			w1 = rotr(sheet[i - 16], 4) ^
					 rotr(sheet[i - 10], 18) ^
					 rotr(sheet[i - 4], 24) ^
					 ((sheet[i - 16] + sheet[i - 10] + sheet[i - 4]) >> 16);

			w2 = rotr(sheet[i - 10], 8) ^
					 rotr(sheet[i - 4], 17) ^
					 rotr(sheet[i - 16], 3) ^
					 ((sheet[i - 16] - sheet[i - 10] + sheet[i - 4]) >> 12);

			w3 = rotr(sheet[i - 4], 12) ^
					 rotr(sheet[i - 16], 16) ^
					 rotr(sheet[i - 10], 11) ^
					 ((sheet[i - 16] + sheet[i - 10] - sheet[i - 4]) >> 8);

			sheet[i] = sheet[i - 16] + w1 +
								 sheet[i - 10] + w2 +
								 sheet[i - 4] + w3;
		}



		/*\
		 * Shuffle hash based on sheet[],
		 * aux[] and it's own content.
		 */

		for (i = 0; i < sheet.length; ++i)
		{
			w1 = (rotr(a, 3) ^ rotr(g, 17)) ^
					 (rotr(c, 7) & rotr(e, 5)) +
					 sheet[i] + aux[i] + h;

			w2 = (rotr(h, 23) | rotr(a, 6)) ^
					 (rotr(f, 8) ^ rotr(b, 11));

			h = g;
			g = f;
			f = e + w1;
			e = d;
			d = c;
			c = b + w2;
			b = a;
			a = w1 + w2;
		}
	}



	/* distribution of input data by clusters */
	public byte[] get(byte[] data)
	{
		int i, j, bitlen;

		byte[] cluster = new byte[64];



		/*\
		 * Write every 56 bytes to the cluster
		 * and update the hash.
		 */

		j = 0;
		for (i = 0; i < data.length; ++i, ++j)
		{
			cluster[j] = data[i];

			if (j == 56)
			{
				while (j < 64)
					cluster[j++] = 0;
				
				update(cluster);

				j = 0;
			}
		}



		/*\
		 * Write the length of the input data
		 * in bits to the end of the cluster as
		 * a 64 bit number (long) and update
		 * the hash.
		 */

		if (j < 55)
			while (j < 56)
				cluster[j++] = 0;

		bitlen = data.length * 8;

		cluster[63] = (byte) (bitlen);
		cluster[62] = (byte) (bitlen >> 8);
		cluster[61] = (byte) (bitlen >> 16);
		cluster[60] = (byte) (bitlen >> 24);
		cluster[59] = (byte) (bitlen >> 32);
		cluster[58] = (byte) (bitlen >> 40);
		cluster[57] = (byte) (bitlen >> 48);
		cluster[56] = (byte) (bitlen >> 56);

		update(cluster);



		/*\
		 * Converting each 32-bit hash word into
		 * 4 bytes; as a result we return an array
		 * of 32 bytes.
		 */

		return new byte[]
		{
			(byte) (a >>> 24), (byte) (a >>> 16), (byte) (a >>> 8), (byte) a,
			(byte) (b >>> 24), (byte) (b >>> 16), (byte) (b >>> 8), (byte) b,
			(byte) (c >>> 24), (byte) (c >>> 16), (byte) (c >>> 8), (byte) c,
			(byte) (d >>> 24), (byte) (d >>> 16), (byte) (d >>> 8), (byte) d,
			(byte) (e >>> 24), (byte) (e >>> 16), (byte) (e >>> 8), (byte) e,
			(byte) (f >>> 24), (byte) (f >>> 16), (byte) (f >>> 8), (byte) f,
			(byte) (g >>> 24), (byte) (g >>> 16), (byte) (g >>> 8), (byte) g,
			(byte) (h >>> 24), (byte) (h >>> 16), (byte) (h >>> 8), (byte) h
		};
	}
}
