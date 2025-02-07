import javax.annotation.Nullable;
import java.math.BigDecimal;
import java.math.BigInteger;

public class NumberUtil {
    public static boolean isEqualToZero(@Nullable BigDecimal input) {
        return (input != null) && (input.compareTo(BigDecimal.ZERO) == 0);
    }

    public static boolean isEqualToOne(@Nullable BigDecimal input) {
        return (input != null) && (input.compareTo(BigDecimal.ONE) == 0);
    }

    public static boolean checkIfNumberIsPowerOfN(@Nullable final BigInteger number,@Nullable final BigInteger n){
        if (number == null || n == null){
            return false;
        }
        BigInteger temp = n;
        while (NumberUtil.isLessThan(temp,number)){
            temp = temp.multiply(n);
        }
        return temp.equals(number);
    }

    public static boolean isEven(@Nullable final BigInteger number){
        return (number != null) && (number.getLowestSetBit() != 0);
    }

    public static boolean areCoPrime(@Nullable final BigInteger number1,@Nullable final BigInteger number2){
        return (number1 != null) && (number2 != null) && (number1.gcd(number2).equals(BigInteger.ONE));
    }

    public static boolean isPositive(@Nullable final BigInteger number){
        return (number != null) && (number.signum() == 1);
    }

    public static boolean isNegative(@Nullable final BigInteger number){
        return (number != null) && (number.signum() == -1);
    }

    public static boolean isNonNegative(@Nullable final BigInteger number){
        return (number != null) && (number.signum() >= 0);
    }

    public static boolean isWithinRangInclusive(@Nullable final BigInteger number,@Nullable final BigInteger lower,@Nullable final BigInteger upper){
        return (number != null) && (lower != null) && (upper != null) && (number.compareTo(lower) >= 0 && number.compareTo(upper) <= 0);
    }

    public static boolean isWithinRangInclusiveLower(@Nullable final BigInteger number,@Nullable final BigInteger lower,@Nullable final BigInteger upper){
        return (number != null) && (lower != null) && (upper != null) && (number.compareTo(lower) >= 0 && number.compareTo(upper) < 0);
    }

    public static boolean isWithinRangExclusive(@Nullable final BigInteger number,@Nullable final BigInteger lower,@Nullable final BigInteger upper){
        return (number != null) && (lower != null) && (upper != null) && (number.compareTo(lower) > 0 && number.compareTo(upper) < 0);
    }

    public static boolean isLessThan(@Nullable final BigInteger number,@Nullable final BigInteger upper){
        return (number != null) && (upper != null) && (number.compareTo(upper) == -1);
    }

    public static boolean isLessOrEqualThan(@Nullable final BigInteger number,@Nullable final BigInteger upper){
        return (number != null) && (upper != null) && (number.compareTo(upper) <= 0);
    }

    public static boolean isGreaterThan(@Nullable final BigInteger number,@Nullable final BigInteger lower){
        return (number != null) && (lower != null) && (number.compareTo(lower) == 1);
    }

    public static boolean isGreaterOrEqualThan(@Nullable final BigInteger number,@Nullable final BigInteger lower){
        return (number != null) && (lower != null) && (number.compareTo(lower) >= 0);
    }
}
