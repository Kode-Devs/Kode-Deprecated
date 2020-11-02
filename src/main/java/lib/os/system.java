/* 
 * Copyright (C) 2020 Kode Devs
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package lib.os;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import kni.KNI;
import kni.KodeNativeObject;

/**
 *
 * @author dell
 */
public class system implements KNI {

    @Override
    public KodeNativeObject call(KodeNativeObject... args) throws Throwable {
        List<String> c = Arrays.asList(args).stream()
                .map(a -> a.get())
                .map(a -> Objects.requireNonNullElse(a, ""))
                .map(Object::toString)
                .collect(Collectors.toList());
        Runtime.getRuntime().exec(c.toArray(new String[]{}));
        return null;
    }

}
