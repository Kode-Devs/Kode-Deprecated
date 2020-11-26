
import java.util.Map;
import kni.KNI;
import kni.KodeObject;
import kni.MethodDef;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author dell
 */
@KNI(doc = "Math Built-in Lib")
public class _math {

    public static int i = 6;

    /**
     *
     */
    public static MethodDef log = new MethodDef(null, "a") {
        @Override
        public KodeObject call(Map<String, KodeObject> env) {
            return null;
        }

    };
}
