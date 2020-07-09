/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kode;

import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import kni.KNI;
import kni.KodeObject;

/**
 *
 * @author dell
 */
class KodeNative implements KodeCallable {

    final String className;
    final String pkg;
    final Interpreter inter;

    KodeNative(String className, String methodName, String pkg, Interpreter inter) {
        this.className = className;
        this.pkg = pkg;
        this.inter = inter;
    }

    @Override
    public List<Pair<String, Object>> arity() {
        return Arrays.asList(new Pair("params", ValueList.create(new ArrayList())).setType(TokenType.STAR));
    }

    @Override
    public Object call(Map<String, Object> arguments) {
        Object p = arguments.get("params");
        List params;
        if (p instanceof KodeInstance) {
            if (ValueList.isList((KodeInstance) p)) {
                params = (List) ValueList.toList(p);
            } else {
                throw new RuntimeError("Argument params must be of type List", null);
            }
        } else {
            throw new RuntimeError("Argument params must be of type List", null);
        }

        // Actual Code goes here
        try {
            Path path = this.pkg == null ? Paths.get("shared-lib") : Paths.get(Kode.LIBPATH, this.pkg, "shared-lib");
            Object newInstance = new URLClassLoader(
                    new URL[]{path.toAbsolutePath().toUri().toURL()},
                    Kode.class.getClassLoader()).loadClass(this.className).newInstance();
            KodeObject[] args = new KodeObject[params.size()];
            for (int i = 0; i < params.size(); i++) {
                args[i] = new KodeObject(Interpreter.toJava(params.get(i)));
            }
            if (newInstance instanceof KNI) {
                return this.inter.toKodeValue(((KNI) newInstance).call(args).get());
            }
            throw new Exception("The Class loaded does not implement Kode Native Interface (KNI).");
        } catch (Throwable e) {
            e.printStackTrace();
            throw new RuntimeError(e.getMessage());
        }
    }

    @Override
    public String toString() {
        return "<native '" + this.className + "'>";
    }

}
