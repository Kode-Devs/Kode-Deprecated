/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kode;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import kni.KNI;
import kni.KodeObject;

/**
 *
 * @author dell
 */
class KNI_FUNC extends KodeBuiltinFunction {

    public KNI_FUNC(String name, Environment closure, Interpreter inter) {
        super(name, closure, inter);
    }

    @Override
    public List<Pair<String, Object>> arity() {
        return Arrays.asList(new Pair("name", null));
    }

    @Override
    public Object call(Map<String, Object> arguments) {
        try {
            Object newInstance = new URLClassLoader(
                    new URL[]{
                        Paths.get(File.separator + "hi").toUri().toURL(),
                        Paths.get(File.separator).toUri().toURL()
//                        ,Paths.get(Paths.get(Kode.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParent().toFile().getCanonicalPath(), "libs").toUri().toURL()
                    }, Kode.class.getClassLoader())
                    .loadClass(arguments.get("name").toString()).newInstance();
            KodeObject[] args = new KodeObject[]{};
            if (newInstance instanceof KNI) {
                return this.interpreter.toKodeValue(((KNI) newInstance).call(args).get());
            } else {
                throw new Exception("The Class loaded does not implement Kode Native Interface (KNI).");
            }
        } catch (Exception | Error e) {
            throw new RuntimeError(e.getMessage(), null);
        }
    }

}
