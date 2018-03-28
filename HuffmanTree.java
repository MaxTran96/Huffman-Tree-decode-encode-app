//Max Tran, CSE 143, June 1st, 2016
//TA: Rebecca Yuen, Section: BL
import java.io.*;
import java.util.*;

//HuffmanTree creates a binary tree using the frequency of the character
//in a given text file and write code using the tree. Along with other programs, 
//it is used to decode a compressed file by creating another tree with the given code
public class HuffmanTree {
   private HuffmanNode overallRoot;
   
   //build a HuffmanTree based on a given array of character frequency, "frequencyCounter"
   //less frequent characters are near the bottom of the tree and more frequent characters
   //are near the root. 
   public HuffmanTree(int[] counts){
      Queue<HuffmanNode> frequencyCounter = new PriorityQueue<HuffmanNode>();
      for (int i = 0; i < counts.length; i++) {
         if (counts[i] > 0) {
            HuffmanNode freqRate = new HuffmanNode(counts[i], i);
            frequencyCounter.add(freqRate);
         }
      }
      HuffmanNode eof = new HuffmanNode(1, counts.length);
      frequencyCounter.add(eof);
      while (frequencyCounter.size() != 1) {
         HuffmanNode current = frequencyCounter.remove();         
         HuffmanNode next = frequencyCounter.remove();
         // nodes representing a sum of counts with a character number of -1
         HuffmanNode sum = new HuffmanNode(current.frequency + next.frequency, -1, current, next);
         frequencyCounter.add(sum);
      }
      overallRoot = frequencyCounter.remove();
   }
   
   //write the tree to the given output stream in traversal order, representing 
   //line of position with 0 for left and 1 for right
   // and prints the character with ASCII value on the next line
   public void write(PrintStream output){
      if (overallRoot != null) {
         write(output, overallRoot, "");
      }
   }
   
   //this helper method helps search through tree starting with a "root"
   //using recursive method.
   //print the line of "code" to the given output in traversal order along with its
   //ASCII value.
   private void write(PrintStream output, HuffmanNode root, String code) {
      if (root.left == null && root.right == null) {
         output.println(root.letter);
         output.println(code);
      } else {
         write(output, root.left, code + "0");
         write(output, root.right, code + "1"); 
      }
   }
   
   //construct a new HuffmanTree from the given "input" that 
   //contains a tree in standard format 
   public HuffmanTree(Scanner input){
      while (input.hasNextLine()) {
         int letterCode = Integer.parseInt(input.nextLine());
         String code = input.nextLine();
         overallRoot = HuffmanTreeBuilder(overallRoot, letterCode, code);
      }
   }
   
   //this helper method helps reconstruct a tree starting with a "root" 
   //from the contents in the file passed in which contains a tree stored in standard format.
   private HuffmanNode HuffmanTreeBuilder(HuffmanNode root, int letterCode, String code){
       if (root == null) {
          root = new HuffmanNode(0, -1);
       }
       if (code.length() == 1) {
          if (code.charAt(0) == '0') {
             root.left = new HuffmanNode(0, letterCode);
          } else {
             root.right = new HuffmanNode(0, letterCode);
          }
       } else {
          char codeValue = code.charAt(0);
         code = code.substring(1);
          if (codeValue == '0') {
             root.left = HuffmanTreeBuilder(root.left, letterCode, code);
          } else {
             root.right = HuffmanTreeBuilder(root.right, letterCode, code);      
          }
       }
       return root;
   }
   
   //Read individual bits from the input passed in. Stop reading the output when the
   //method encounters a value of "eof". Input stream should contain legal
   //encoding of characters.
   public void decode(BitInputStream input, PrintStream output, int eofValue){
		int charCode = input.readBit();
		while (charCode != -1) {
			charCode = HuffmanTreeTraverse(input, charCode, overallRoot, output, eofValue);
		}  
   }

   //this methods traverse down the HuffmanTree as it read each individual bit passed in
   //and continue reading until it encounters a value of "eof" and print it to "output"
	private int HuffmanTreeTraverse(BitInputStream input, int code, HuffmanNode root, PrintStream output, int eofValue) {
		if (root.left == null && root.right == null && root.letter != eofValue) {
			output.write(root.letter);
			return code;
		} else if (code == 0 && root.letter != eofValue) {
			return HuffmanTreeTraverse(input, input.readBit(), root.left, output, eofValue);
		} else if (root.letter != eofValue) {
			return HuffmanTreeTraverse(input, input.readBit(), root.right, output, eofValue);
		}
		return -1;
	}   
   
   // this class manages each individual node in the HuffmanTree
   //and stores each node's frequency in a given text file and its 
   //associated ASCII value     
   private class HuffmanNode implements Comparable<HuffmanNode> {
      public int frequency; // count stored at this node
      public int letter; // character stored at this node
      public HuffmanNode left; // ref to left subtree
      public HuffmanNode right; // ref to right subtree
   
     // constructs a node with a character's ASCII value and its frequency
      public HuffmanNode(int frequency, int letter) {
         this(frequency, letter, null, null);
      }
    
      // constructs a node with a character's ASCII value and its frequency 
      // and links of "left" and "right" subtree.
      public HuffmanNode(int frequency, int letter, HuffmanNode left, HuffmanNode right) {
         this.frequency = frequency;
         this.letter = letter;
         this.left = left;
         this.right = right;
      }  
       
      // compare two nodes by their frequency value and return the difference
      public int compareTo(HuffmanNode other) {
         return this.frequency - other.frequency;
      }
   }
}
