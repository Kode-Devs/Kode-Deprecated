/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lib;

import kni.KNI;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import kni.KodeObject;

/**
 *
 * @author dell
 */
public class os implements KNI{

    /**
     * Temporary Native wrapper function of Java System.getProperty() for Kode.
     *
     * @param cmd Key Needed by System.getProperty()
     * @return value if key is present and accessible, else null (or None)
     */
    public static Object getProperties(Object cmd) {
        try {
            return System.getProperty(cmd.toString());
        } catch (Exception e) {
            return null;
        }
    }

    public static void System(Object cmd) throws IOException {
        if (cmd instanceof List) {
            List<String> c = (List) ((List) cmd).stream()
                    .map(a -> Objects.requireNonNullElse(a, ""))
                    .map(Object::toString)
                    .collect(Collectors.toList());
            String temp[] = new String[]{};
            Runtime.getRuntime().exec(c.toArray(temp));
        } else {
            Runtime.getRuntime().exec(Objects.requireNonNullElse(cmd,"").toString());
        }
    }

    @Override
    public KodeObject call(KodeObject[] args) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
