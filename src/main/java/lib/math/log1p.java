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
package lib.math;

import kni.KNI;
import kni.KodeObject;
import math.KodeNumber;

/**
 *
 * @author dell
 */
public class log1p implements KNI {

    @Override
    public KodeObject call(KodeObject... args) throws Throwable {
        return new KodeObject(KodeNumber.valueOf(Math.log1p(((KodeNumber)args[0].get()).getFloat())));
    }
    
}
