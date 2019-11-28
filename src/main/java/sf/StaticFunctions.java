package sf;

import java.text.DecimalFormat;

public class StaticFunctions {
    static public String doubleToString(Double d) {
        return new DecimalFormat("#.00#").format(d).replace(',', '.');
    }
}
