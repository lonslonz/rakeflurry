package com.skplanet.rakeflurry.util;

import org.junit.Test;


public class EscapseTest {

    @Test
    public  void testEscape() {
        
        String test = "my test \n \t my test";
        String test2 = "my test \\n \\t my test";
        
        String unescapsed;
        unescapsed = test.replaceAll("\n", "\\\\n");
        System.out.println("unescapsed : "+ unescapsed);
        unescapsed = unescapsed.replaceAll("\t", "\\\\t");
        System.out.println("unescapsed : "+ unescapsed);
        
        unescapsed = test2.replaceAll("\n", "\\\\n");
        System.out.println("unescapsed : "+ unescapsed);
        unescapsed = unescapsed.replaceAll("\t", "\\\\t");
        System.out.println("unescapsed : "+ unescapsed);
        
        
        unescapsed = test.replaceAll("[^\\\\]\\n", "\\n");
        System.out.println("unescapsed : "+ unescapsed);
        unescapsed = test.replaceAll("[^\\\\]\\t", "\\t");
        
        System.out.println("test : "+ test);
        System.out.println("unescapsed : "+ unescapsed);
        
        test = "my test \\n \\t my test";
        String unescapsed2 = test.replaceAll("[^\\\\]\n", "\\n");
        unescapsed2 = test.replaceAll("[^\\\\]\t", "\\t");

        System.out.println("unescapsed2 : "+ unescapsed2);
    }
}
