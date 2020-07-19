/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lib.os;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import kni.KNI;
import kni.KodeObject;

/**
 *
 * @author dell
 */
public class system implements KNI {

    @Override
    public KodeObject call(KodeObject... args) throws Throwable {
        List<String> c = Arrays.asList(args).stream()
                .map(a -> a.get())
                .map(a -> Objects.requireNonNullElse(a, ""))
                .map(Object::toString)
                .collect(Collectors.toList());
        Runtime.getRuntime().exec(c.toArray(new String[]{}));
        return null;
    }

}
