package lib;

import java.util.Locale;

/**
 *
 * @author dell
 */
public class NewClass {

    public static void main(String[] args) {
        Double a = Double.valueOf(1);
        while(Double.isFinite(a)){
            String format = String.format(Locale.US,"%.10G",a);
            format = format.replaceFirst("\\.0+(e|$)", "$1")
                    .replaceFirst("(\\.[0-9]*[1-9])(0+)(e|$)", "$1$3");
            System.out.println(format);
            a*=10;
        }
    }
}
