package notthatuwu.xyz.mythrecode.api.utils;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class RandomUtil {
  private static Random rng = new Random();
  
  private static ThreadLocalRandom threadLocalRandom = ThreadLocalRandom.current();
  
  public static double nextDouble(double max, double min) {
    return Math.random() * (max - min) + min;
  }
  
  public static double getRandomGaussian(double average) {
    return threadLocalRandom.nextGaussian() * average;
  }
  
  public static double randomInRange(double min, double max) {
    return rng.nextInt((int)(max - min + 1.0D)) + max;
  }
}
