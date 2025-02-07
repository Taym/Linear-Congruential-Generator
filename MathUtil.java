import java.math.BigInteger;

public class MathUtil {
    /**
     * This method calculates the following sum:
     * (a^0 + a^1 + a^2 + ..... + a^n) mod m
     * The time complexity is O(log2(n))
     */
    public static BigInteger geometricSequenceSumModM(final BigInteger a, final BigInteger n, final BigInteger m){
        BigInteger exp = n;
        BigInteger factor = BigInteger.ONE;
        BigInteger sum = BigInteger.ZERO;
        BigInteger temp;
        BigInteger q = a;

        while (exp.compareTo(BigInteger.ZERO) == 1 && !q.equals(BigInteger.ZERO)){
            if (NumberUtil.isEven(exp)){
                temp =  factor.multiply(q.modPow(exp,m)).mod(m);
                sum = sum.add(temp).mod(m);
                exp = exp.subtract(BigInteger.ONE);
            }
            final BigInteger qPlusOneModM = q.add(BigInteger.ONE).mod(m);
            factor = qPlusOneModM.multiply(factor).mod(m);
            q = q.multiply(q).mod(m);
            exp = exp.divide(BigInteger.valueOf(2));
        }
        return sum.add(factor).mod(m);
    }
}