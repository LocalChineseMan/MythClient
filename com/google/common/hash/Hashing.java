package com.google.common.hash;

import com.google.common.annotations.Beta;
import com.google.common.base.Preconditions;
import com.google.common.base.Supplier;
import java.util.Iterator;

@Beta
public final class Hashing {
  public static HashFunction goodFastHash(int minimumBits) {
    int bits = checkPositiveAndMakeMultipleOf32(minimumBits);
    if (bits == 32)
      return Murmur3_32Holder.GOOD_FAST_HASH_FUNCTION_32; 
    if (bits <= 128)
      return Murmur3_128Holder.GOOD_FAST_HASH_FUNCTION_128; 
    int hashFunctionsNeeded = (bits + 127) / 128;
    HashFunction[] hashFunctions = new HashFunction[hashFunctionsNeeded];
    hashFunctions[0] = Murmur3_128Holder.GOOD_FAST_HASH_FUNCTION_128;
    int seed = GOOD_FAST_HASH_SEED;
    for (int i = 1; i < hashFunctionsNeeded; i++) {
      seed += 1500450271;
      hashFunctions[i] = murmur3_128(seed);
    } 
    return (HashFunction)new ConcatenatedHashFunction(hashFunctions);
  }
  
  private static final int GOOD_FAST_HASH_SEED = (int)System.currentTimeMillis();
  
  public static HashFunction murmur3_32(int seed) {
    return (HashFunction)new Murmur3_32HashFunction(seed);
  }
  
  public static HashFunction murmur3_32() {
    return Murmur3_32Holder.MURMUR3_32;
  }
  
  public static HashFunction murmur3_128(int seed) {
    return (HashFunction)new Murmur3_128HashFunction(seed);
  }
  
  public static HashFunction murmur3_128() {
    return Murmur3_128Holder.MURMUR3_128;
  }
  
  public static HashFunction sipHash24() {
    return SipHash24Holder.SIP_HASH_24;
  }
  
  public static HashFunction sipHash24(long k0, long k1) {
    return (HashFunction)new SipHashFunction(2, 4, k0, k1);
  }
  
  public static HashFunction md5() {
    return Md5Holder.MD5;
  }
  
  public static HashFunction sha1() {
    return Sha1Holder.SHA_1;
  }
  
  private static final class Hashing {}
  
  static final class Hashing {}
  
  enum Hashing {
  
  }
  
  private static class Hashing {}
  
  private static class Hashing {}
  
  private static class Hashing {}
  
  private static class Hashing {}
  
  private static class Sha1Holder {
    static final HashFunction SHA_1 = new MessageDigestHashFunction("SHA-1", "Hashing.sha1()");
  }
  
  public static HashFunction sha256() {
    return Sha256Holder.SHA_256;
  }
  
  public static HashFunction sha512() {
    return Sha512Holder.SHA_512;
  }
  
  public static HashFunction crc32() {
    return Crc32Holder.CRC_32;
  }
  
  public static HashFunction adler32() {
    return Adler32Holder.ADLER_32;
  }
  
  private static HashFunction checksumHashFunction(ChecksumType type, String toString) {
    return (HashFunction)new ChecksumHashFunction((Supplier)type, ChecksumType.access$200(type), toString);
  }
  
  public static int consistentHash(HashCode hashCode, int buckets) {
    return consistentHash(hashCode.padToLong(), buckets);
  }
  
  public static int consistentHash(long input, int buckets) {
    Preconditions.checkArgument((buckets > 0), "buckets must be positive: %s", new Object[] { Integer.valueOf(buckets) });
    LinearCongruentialGenerator generator = new LinearCongruentialGenerator(input);
    int candidate = 0;
    while (true) {
      int next = (int)((candidate + 1) / generator.nextDouble());
      if (next >= 0 && next < buckets) {
        candidate = next;
        continue;
      } 
      break;
    } 
    return candidate;
  }
  
  public static HashCode combineOrdered(Iterable<HashCode> hashCodes) {
    Iterator<HashCode> iterator = hashCodes.iterator();
    Preconditions.checkArgument(iterator.hasNext(), "Must be at least 1 hash code to combine.");
    int bits = ((HashCode)iterator.next()).bits();
    byte[] resultBytes = new byte[bits / 8];
    for (HashCode hashCode : hashCodes) {
      byte[] nextBytes = hashCode.asBytes();
      Preconditions.checkArgument((nextBytes.length == resultBytes.length), "All hashcodes must have the same bit length.");
      for (int i = 0; i < nextBytes.length; i++)
        resultBytes[i] = (byte)(resultBytes[i] * 37 ^ nextBytes[i]); 
    } 
    return HashCode.fromBytesNoCopy(resultBytes);
  }
  
  public static HashCode combineUnordered(Iterable<HashCode> hashCodes) {
    Iterator<HashCode> iterator = hashCodes.iterator();
    Preconditions.checkArgument(iterator.hasNext(), "Must be at least 1 hash code to combine.");
    byte[] resultBytes = new byte[((HashCode)iterator.next()).bits() / 8];
    for (HashCode hashCode : hashCodes) {
      byte[] nextBytes = hashCode.asBytes();
      Preconditions.checkArgument((nextBytes.length == resultBytes.length), "All hashcodes must have the same bit length.");
      for (int i = 0; i < nextBytes.length; i++)
        resultBytes[i] = (byte)(resultBytes[i] + nextBytes[i]); 
    } 
    return HashCode.fromBytesNoCopy(resultBytes);
  }
  
  static int checkPositiveAndMakeMultipleOf32(int bits) {
    Preconditions.checkArgument((bits > 0), "Number of bits must be positive");
    return bits + 31 & 0xFFFFFFE0;
  }
  
  private static class Hashing {}
  
  private static class Hashing {}
  
  private static class Hashing {}
  
  private static class Hashing {}
}
