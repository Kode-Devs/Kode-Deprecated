
import java.net.URISyntaxException;
import java.nio.file.Paths;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author dell
 */
public class Test {
    public static void main(String... args) throws URISyntaxException{
        String LIBPATH = Paths.get(Paths.get(Test.class.getProtectionDomain().getCodeSource().getLocation().toURI())
                    .getParent().getParent().toFile().getAbsolutePath(), "libs").toAbsolutePath().toString(); // Get Parent added.
        System.out.println(LIBPATH);
    }
}
