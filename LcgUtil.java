import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigInteger;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class LcgUtil {
    private final BigInteger a;//the multiplier
    private final BigInteger c;//the increment
    private final BigInteger m;//the modulus
    private final BigInteger seed;

    private static Random rand = new Random();
    private static final ObjectMapper objectMapper = ConfiguredObjectMapper.objectMapper.copy().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);


    @JsonCreator
    public LcgUtil(
            @JsonProperty("a") final BigInteger a,
            @JsonProperty("c") final BigInteger c,
            @JsonProperty("m") final BigInteger m,
            @JsonProperty("seed") final BigInteger seed) {
        //https://en.wikipedia.org/wiki/Linear_congruential_generator
        assert NumberUtil.isPositive(m) : "m>0 Is Not Fulfilled";
        assert NumberUtil.isWithinRangExclusive(a,BigInteger.ZERO,m) : "(0 < a < m) Is Not Fulfilled";
        assert NumberUtil.isWithinRangInclusiveLower(c,BigInteger.ZERO,m) : "(0 <= c < m) Is Not Fulfilled";
        assert NumberUtil.isWithinRangInclusiveLower(seed,BigInteger.ZERO,m) : "(0 <= seed < m) Is Not Fulfilled";
        this.a = a;
        this.c = c;
        this.m = m;
        this.seed = seed;
    }

    public Map<String,Object> asMap(){
        return ConfiguredObjectMapper.convertObjectToMap(this);
    }

    public static LcgUtil fromMap(final Map<String,Object> map){
        return objectMapper.convertValue(map, LcgUtil.class);
    }


    public boolean checkIfLcgIsOfMaximalPeriod(){
        final BigInteger aMinusOne = this.a.subtract(BigInteger.ONE);
        final Set<BigInteger> factorsOfM = PrimeNumbersUtil.getDistinctPrimeFactors(this.m);
        final BigInteger four = new BigInteger("4");

        final boolean isCPositive = this.c.compareTo(BigInteger.ZERO) == 1;
        final boolean areCAndMCoPrime = NumberUtil.areCoPrime(this.c,this.m);
        final boolean isAMinusOneDivisibleByAllPrimeFactorsOfM = factorsOfM
                .stream()
                .filter(f -> !aMinusOne.mod(f).equals(BigInteger.ZERO))
                .findFirst()
                .map(f -> false)
                .orElse(true);
        final boolean fourDivisibilityCondition =  (
                (this.m.mod(four).equals(BigInteger.ZERO) && aMinusOne.mod(four).equals(BigInteger.ZERO))
                        ||  (!this.m.mod(four).equals(BigInteger.ZERO))
        );
        return isCPositive && areCAndMCoPrime && isAMinusOneDivisibleByAllPrimeFactorsOfM && fourDivisibilityCondition;
    }

    /**
     * returns LcgUtil object that will consume the whole range of values (0 to m-1) inclusive, that means the period will
     * be equal to m(refer to Hull-Dobell Theorem https://en.wikipedia.org/wiki/Linear_congruential_generator#c_%E2%89%A0_0).
     * m must be a power of 36.
     * a is always 13 (the library can be enhanced later to generate a).
     * seed will be generated randomly.
     * c will be generated randomly.
     * */
    public static LcgUtil defaultFullPeriodLcgForPowerOf36Modulus(final BigInteger m){
        assert NumberUtil.checkIfNumberIsPowerOfN(m,BigInteger.valueOf(36)) : "The Modulus m Is Not A Power Of 36";
        final BigInteger a = new BigInteger("13");
        final BigInteger c = PrimeNumbersUtil.generateRandomSmallerCoPrimeNumber(m);
        final BigInteger seed = generateRandomSeed(m);
        final LcgUtil lcg = new LcgUtil(a,c,m,seed);
        assert lcg.checkIfLcgIsOfMaximalPeriod() : "The Static Factory defaultFullPeriodLcgForPowerOf36Modulus(m) Has A Wrong Implementation";
        return lcg;
    }

    public static LcgUtil defaultFullPeriodLcgForPowerOf36Modulus(int exponentOf36){
        final BigInteger powerOf36Modulus = BigInteger.valueOf(36).pow(exponentOf36);
        return defaultFullPeriodLcgForPowerOf36Modulus(powerOf36Modulus);
    }


    /**
     * Returns the next random number in the LCG sequence
     * https://en.wikipedia.org/wiki/Linear_congruential_generator
     * */
    public BigInteger next(final BigInteger prevVal){
        assert NumberUtil.isWithinRangInclusiveLower(prevVal,BigInteger.ZERO,m);
        final BigInteger newVal = a.multiply(prevVal).add(c).mod(m);
        return newVal;
    }



    /**
     * Returns the kth term after the nth term in the LCG sequence
     * https://math.stackexchange.com/questions/2115756/linear-congruential-generator-for-nkth-can-also-be-computed-with-nth-term
     * */
    private BigInteger getKthTermAfterNthTerm(final BigInteger nthTerm,final BigInteger k){
        assert NumberUtil.isGreaterOrEqualThan(a,BigInteger.valueOf(2)) : "a Should Fulfill (a>=2)";
        assert NumberUtil.isWithinRangInclusiveLower(k,BigInteger.ZERO,m) : "k In getKthTermAfterNthTerm() Method Should Fulfill (0 <= k < m)";

        final BigInteger aPowerKModM = a.modPow(k,m);// (a^k) % m

        final BigInteger part1 = aPowerKModM.multiply(nthTerm).mod(m);// (a^k * Xn) % m = (((a^k) % m) * Xn) % m
        /**
         * (a^k - 1) = (a - 1)*(1 + a + a^2 + a^3 .... + a^n-1)
         * So we find that (a^k - 1)/(a - 1) = (1 + a + a^2 + a^3 .... + a^n-1) which is a geometric series
         * https://math.stackexchange.com/questions/383379/prove-that-x-1-divides-xn-1
         * */
        final BigInteger part2 = MathUtil.geometricSequenceSumModM(a,k.subtract(BigInteger.ONE),m).multiply(c).mod(m);

        final BigInteger kthTerm = part1.add(part2).mod(m);
        return kthTerm;
    }

    /**
     * Returns the kth term in the LCG sequence
     * */
    public BigInteger getKthTerm(final BigInteger k){
        assert NumberUtil.isWithinRangInclusiveLower(k,BigInteger.ZERO,m) : "k In getKthTermAfterNthTerm() Method Should Fulfill (0 <= k < m)";
        assert NumberUtil.isGreaterOrEqualThan(a,BigInteger.valueOf(2)) : "a Should Fulfill (a>=2)";
        return getKthTermAfterNthTerm(seed,k);
    }

    /**
     * returns a random number between 0(inclusive) and the largest power of 2 that is less or equal than m(exclusive)
     * */
    public static BigInteger generateRandomSeed(final BigInteger m){
        assert NumberUtil.isPositive(m);
        final int numOfBits = m.bitLength();
        while (true) {
            final BigInteger random = new BigInteger(numOfBits,rand);
            if (random.compareTo(m) < 0) {
                return random;
            } else {
                continue;
            }
        }
    }

    public BigInteger getA() {
        return a;
    }

    public BigInteger getC() {
        return c;
    }

    public BigInteger getM() {
        return m;
    }

    public BigInteger getSeed() {
        return seed;
    }

    @Override
    public String toString() {
        return String.format("LCG: a=%s, c=%s, m=%s, seed=%s",a,c,m,seed);
    }

}