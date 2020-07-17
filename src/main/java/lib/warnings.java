/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lib;

import kode.IO;

/**
 *
 * @author dell
 */
public class warnings {

    public static void print_warning(Object msg) {
        IO.printfln_err(msg);
    }
}
