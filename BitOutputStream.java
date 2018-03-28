// The BitOutputStream and BitInputStream classes provide the ability to
// write and read individual bits to a file in a compact form.  One major
// limitation of this approach is that the resulting file will always have
// a number of bits that is a multiple of 8.  In effect, whatever bits are
// output to the file are padded at the end with 0's to make the total
// number of bits a multiple of 8.

import java.io.*;

public class BitOutputStream {
	private PrintStream output;
	private int digits; // a buffer used to build up next set of digits
	private int numDigits; // how many digits are currently in the buffer
	private boolean debug; // set to true to write ASCII 0s and 1s rather than
							// bits

	private static final int BYTE_SIZE = 8; // digits per byte

	// Creates a BitOutputStream sending output to the given stream. If debug is
	// set to true, bits are printed as ASCII 0s and 1s.
	public BitOutputStream(PrintStream output, boolean debug) {
		this.output = output;
		this.debug = debug;
	}

	// Writes given bit to output
	public void writeBit(int bit) {
		if (debug) {
			output.print(bit);
		} else {
			if (bit < 0 || bit > 1)
				throw new IllegalArgumentException("Illegal bit: " + bit);
			digits += bit << numDigits;
			numDigits++;
			if (numDigits == BYTE_SIZE)
				flush();
		}
	}

	// Flushes the buffer. If numDigits < BYTE_SIZE, this will
	// effectively pad the output with extra 0's, so this should
	// be called only when numDigits == BYTE_SIZE or when we are
	// closing the output.
	private void flush() {
		output.write(digits);
		digits = 0;
		numDigits = 0;
	}

	// post: output is closed
	public void close() {
		if (numDigits > 0)
			flush();
		output.close();
	}

	// included to ensure that the stream is closed
	protected void finalize() {
		close();
	}
}
