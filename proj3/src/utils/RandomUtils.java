package utils;

import java.util.Random;
import tileengine.TETile; // Add this import statement

public class RandomUtils {

    public static double uniform(Random random) {
        return random.nextDouble();
    }

    public static int uniform(Random random, int n) {
        if (n <= 0) {
            throw new IllegalArgumentException("argument must be positive: " + n);
        }
        return random.nextInt(n);
    }

    public static long uniform(Random random, long n) {
        if (n <= 0L) {
            throw new IllegalArgumentException("argument must be positive: " + n);
        }

        long r = random.nextLong();
        long m = n - 1;

        if ((n & m) == 0L) {
            return r & m;
        }

        long u = r >>> 1;
        while (u + m - (r = u % n) < 0L) {
            u = random.nextLong() >>> 1;
        }
        return r;
    }

    public static int uniform(Random random, int a, int b) {
        if ((b <= a) || ((long) b - a >= Integer.MAX_VALUE)) {
            throw new IllegalArgumentException("invalid range: [" + a + ", " + b + ")");
        }
        return a + uniform(random, b - a);
    }

    public static double uniform(Random random, double a, double b) {
        if (!(a < b)) {
            throw new IllegalArgumentException("invalid range: [" + a + ", " + b + ")");
        }
        return a + uniform(random) * (b - a);
    }

    public static boolean bernoulli(Random random, double p) {
        if (!(p >= 0.0 && p <= 1.0)) {
            throw new IllegalArgumentException("probability p must be between 0.0 and 1.0: " + p);
        }
        return uniform(random) < p;
    }

    public static boolean bernoulli(Random random) {
        return bernoulli(random, 0.5);
    }

    public static double gaussian(Random random) {
        double r, x, y;
        do {
            x = uniform(random, -1.0, 1.0);
            y = uniform(random, -1.0, 1.0);
            r = x * x + y * y;
        } while (r >= 1 || r == 0);
        return x * Math.sqrt(-2 * Math.log(r) / r);
    }

    public static double gaussian(Random random, double mu, double sigma) {
        return mu + sigma * gaussian(random);
    }

    public static int geometric(Random random, double p) {
        if (!(p >= 0.0 && p <= 1.0)) {
            throw new IllegalArgumentException("probability p must be between 0.0 and 1.0: " + p);
        }
        return (int) Math.ceil(Math.log(uniform(random)) / Math.log(1.0 - p));
    }

    public static int poisson(Random random, double lambda) {
        if (!(lambda > 0.0)) {
            throw new IllegalArgumentException("lambda must be positive: " + lambda);
        }
        if (Double.isInfinite(lambda)) {
            throw new IllegalArgumentException("lambda must not be infinite: " + lambda);
        }
        int k = 0;
        double p = 1.0;
        double expLambda = Math.exp(-lambda);
        do {
            k++;
            p *= uniform(random);
        } while (p >= expLambda);
        return k - 1;
    }

    public static double pareto(Random random) {
        return pareto(random, 1.0);
    }

    public static double pareto(Random random, double alpha) {
        if (!(alpha > 0.0)) {
            throw new IllegalArgumentException("alpha must be positive: " + alpha);
        }
        return Math.pow(1 - uniform(random), -1.0 / alpha) - 1.0;
    }

    public static double cauchy(Random random) {
        return Math.tan(Math.PI * (uniform(random) - 0.5));
    }

    public static int discrete(Random random, double[] probabilities) {
        if (probabilities == null) {
            throw new IllegalArgumentException("argument array is null");
        }
        double eps = 1E-14;
        double sum = 0.0;
        for (int i = 0; i < probabilities.length; i++) {
            if (!(probabilities[i] >= 0.0)) {
                throw new IllegalArgumentException("array entry " + i + " must be nonnegative: "
                                                   + probabilities[i]);
            }
            sum += probabilities[i];
        }
        if (sum > 1.0 + eps || sum < 1.0 - eps) {
            throw new IllegalArgumentException("sum of array entries does not approximately "
                                               + "equal 1.0: " + sum);
        }

        while (true) {
            double r = uniform(random);
            sum = 0.0;
            for (int i = 0; i < probabilities.length; i++) {
                sum = sum + probabilities[i];
                if (sum > r) {
                    return i;
                }
            }
        }
    }

    public static int discrete(Random random, int[] frequencies) {
        if (frequencies == null) {
            throw new IllegalArgumentException("argument array is null");
        }
        long sum = 0;
        for (int i = 0; i < frequencies.length; i++) {
            if (frequencies[i] < 0) {
                throw new IllegalArgumentException("array entry " + i + " must be nonnegative: "
                                                   + frequencies[i]);
            }
            sum += frequencies[i];
        }
        if (sum == 0) {
            throw new IllegalArgumentException("at least one array entry must be positive");
        }
        if (sum >= Integer.MAX_VALUE) {
            throw new IllegalArgumentException("sum of frequencies overflows an int");
        }

        double r = uniform(random, (int) sum);
        sum = 0;
        for (int i = 0; i < frequencies.length; i++) {
            sum += frequencies[i];
            if (sum > r) {
                return i;
            }
        }

        assert false;
        return -1;
    }

    public static double exp(Random random, double lambda) {
        if (!(lambda > 0.0)) {
            throw new IllegalArgumentException("lambda must be positive: " + lambda);
        }
        return -Math.log(1 - uniform(random)) / lambda;
    }

    public static void shuffle(Random random, Object[] a) {
        validateNotNull(a);
        int n = a.length;
        for (int i = 0; i < n; i++) {
            int r = i + uniform(random, n - i);
            Object temp = a[i];
            a[i] = a[r];
            a[r] = temp;
        }
    }

    public static void shuffle(Random random, double[] a) {
        validateNotNull(a);
        int n = a.length;
        for (int i = 0; i < n; i++) {
            int r = i + uniform(random, n - i);
            double temp = a[i];
            a[i] = a[r];
            a[r] = temp;
        }
    }

    public static void shuffle(Random random, int[] a) {
        validateNotNull(a);
        int n = a.length;
        for (int i = 0; i < n; i++) {
            int r = i + uniform(random, n - i);
            int temp = a[i];
            a[i] = a[r];
            a[r] = temp;
        }
    }

    public static void shuffle(Random random, char[] a) {
        validateNotNull(a);
        int n = a.length;
        for (int i = 0; i < n; i++) {
            int r = i + uniform(random, n - i);
            char temp = a[i];
            a[i] = a[r];
            a[r] = temp;
        }
    }

    public static void shuffle(Random random, Object[] a, int lo, int hi) {
        validateNotNull(a);
        validateSubarrayIndices(lo, hi, a.length);

        for (int i = lo; i < hi; i++) {
            int r = i + uniform(random, hi - i);
            Object temp = a[i];
            a[i] = a[r];
            a[r] = temp;
        }
    }

    public static void shuffle(Random random, double[] a, int lo, int hi) {
        validateNotNull(a);
        validateSubarrayIndices(lo, hi, a.length);

        for (int i = lo; i < hi; i++) {
            int r = i + uniform(random, hi - i);
            double temp = a[i];
            a[i] = a[r];
            a[r] = temp;
        }
    }

    public static void shuffle(Random random, int[] a, int lo, int hi) {
        validateNotNull(a);
        validateSubarrayIndices(lo, hi, a.length);

        for (int i = lo; i < hi; i++) {
            int r = i + uniform(random, hi - i);
            int temp = a[i];
            a[i] = a[r];
            a[r] = temp;
        }
    }

    public static int[] permutation(Random random, int n) {
        if (n < 0) {
            throw new IllegalArgumentException("argument is negative");
        }
        int[] perm = new int[n];
        for (int i = 0; i < n; i++) {
            perm[i] = i;
        }
        shuffle(random, perm);
        return perm;
    }

    public static int[] permutation(Random random, int n, int k) {
        if (n < 0) {
            throw new IllegalArgumentException("argument is negative");
        }
        if (k < 0 || k > n) {
            throw new IllegalArgumentException("k must be between 0 and n");
        }
        int[] perm = new int[k];
        for (int i = 0; i < k; i++) {
            int r = uniform(random, i + 1);
            perm[i] = perm[r];
            perm[r] = i;
        }
        for (int i = k; i < n; i++) {
            int r = uniform(random, i + 1);
            if (r < k) {
                perm[r] = i;
            }
        }
        return perm;
    }

    private static void validateNotNull(Object x) {
        if (x == null) {
            throw new IllegalArgumentException("argument is null");
        }
    }

    private static void validateSubarrayIndices(int lo, int hi, int length) {
        if (lo < 0 || hi > length || lo > hi) {
            throw new IllegalArgumentException("subarray indices out of bounds: [" + lo + ", "
                                               + hi + ")");
        }
    }

    public static TETile uniform(Random random, TETile[] tiles) {
        int index = uniform(random, tiles.length);
        return tiles[index];
    }
}
