package lisong_mechlab.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

import lisong_mechlab.util.Huffman1;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test suite for {@link Huffman1}
 * 
 * @author Emily Björk
 */
public class Huffman1Test{

   /**
    * A simple test case that tests an input sequence that only generates a single encoded byte out.
    */
   @Test
   public void testEncodeDecode_Simple() throws EncodingException, DecodingException{
      // Setup
      List<Integer> values = new ArrayList<>();
      values.add(1);
      values.add(2);
      values.add(3);
      Map<Integer, Integer> freqs = new TreeMap<>();
      freqs.put(1, 1);
      freqs.put(2, 3);
      freqs.put(3, 2);

      // Execute
      Huffman1<Integer> huffman1 = new Huffman1<Integer>(freqs, null);
      byte[] encoded = huffman1.encode(values);
      List<Integer> ans = huffman1.decode(encoded);

      // Verify
      assertArrayEquals(values.toArray(), ans.toArray());
   }

   /**
    * A simple test case that tests an input sequence that only generates a few encoded bytes out.
    */
   @Test
   public void testEncodeDecode_SimpleMultiByte() throws DecodingException, EncodingException{
      // Setup
      List<Integer> values = new ArrayList<>();
      values.add(1);
      values.add(1);
      values.add(2);
      values.add(1);
      values.add(3);
      values.add(1);
      Map<Integer, Integer> freqs = new TreeMap<>();
      freqs.put(1, 1);
      freqs.put(2, 3);
      freqs.put(3, 2);

      // Execute
      Huffman1<Integer> huffman1 = new Huffman1<Integer>(freqs, null);
      byte[] encoded = huffman1.encode(values);
      List<Integer> ans = huffman1.decode(encoded);

      // Verify
      assertArrayEquals(values.toArray(), ans.toArray());
   }

   /**
    * Test a non-trivial case that the encoder can encode a long sequence of symbols and then decode it's own output.
    * 
    * @throws DecodingException
    * @throws EncodingException
    */
   @Test
   public void testEncodeDecode() throws DecodingException, EncodingException{
      // Setup
      Map<Integer, Integer> freqs = new TreeMap<>();
      List<Integer> values = gaussianInput(1400, freqs);
      new ArrayList<>();

      // Execute
      Huffman1<Integer> huffman1 = new Huffman1<Integer>(freqs, null);
      byte[] encoded = huffman1.encode(values);
      List<Integer> ans = huffman1.decode(encoded);

      // Verify
      assertArrayEquals(values.toArray(), ans.toArray());
   }

   /**
    * Test that {@link Huffman1#encode(List)} produces a code that's within 1% of the Shannon limit.
    * 
    * @throws EncodingException
    */
   @Test
   public void testEncode_performance() throws EncodingException{
      // Setup
      Map<Integer, Integer> freqs = new TreeMap<>();
      List<Integer> values = gaussianInput(50000, freqs);
      new ArrayList<>();

      // Calculate Shannon limit using Shannon's source coding theorem
      final int numSamples = values.size();
      double sourceEntropy = 0;
      for(int i : freqs.keySet()){
         double p = (double)freqs.get(i) / (double)numSamples;
         sourceEntropy += -(Math.log(p) / Math.log(2)) * p;
      }
      final double shannonLimit = sourceEntropy * numSamples;

      // Execute
      Huffman1<Integer> huffman1 = new Huffman1<Integer>(freqs, null);
      byte[] encoded = huffman1.encode(values);

      // Verify
      assertTrue("Actual entropy: " + encoded.length * 8 + " bits, actual calculated entropy: " + shannonLimit + " bits",
                 encoded.length * 8 < shannonLimit * 1.01);
   }

   /**
    * Generates an input vector with a Gaussian distribution of integers. The standard deviation is proportional to the
    * number of samples
    * 
    * @param num
    *           The number of samples to generate
    * @param freqs
    *           A map to store the frequency data into.
    * @return A {@link List} of sample values.
    */
   private List<Integer> gaussianInput(int num, Map<Integer, Integer> freqs){
      Random random = new Random(0);
      List<Integer> values = new ArrayList<>();
      for(int i = 0; i < num; ++i){
         int v = (int)(random.nextGaussian() * num / 100.0);
         values.add(v);
         if( freqs.containsKey(v) ){
            freqs.put(v, freqs.get(v) + 1);
         }
         else{
            freqs.put(v, 1);
         }
      }
      return values;
   }
}