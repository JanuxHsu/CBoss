package CBoss.utils.commons;

public class InputUtils {

    public static boolean isInteger(String input) {
        try {
            int num = Integer.parseInt(input);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
