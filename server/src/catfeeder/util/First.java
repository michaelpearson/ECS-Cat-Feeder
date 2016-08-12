package catfeeder.util;

import java.util.List;

public class First {
    public static <T> T orNull(List<T> list) {
        if(list == null || list.size() == 0) {
            return null;
        }
        return list.get(0);
    }

    public static <T> T orElse(List<T> list, T e) {
        if(list == null || list.size() == 0) {
            return e;
        }
        return list.get(0);
    }

}
