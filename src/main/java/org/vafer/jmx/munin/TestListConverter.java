package org.vafer.jmx.munin;

import java.awt.image.ReplicateScaleFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TestListConverter implements com.beust.jcommander.IStringConverter<List<String>> {

    @Override
    public List<String> convert(String value) {
        // "a,b,c","c,d,e","e,f,g"
        // "a\,b\,c","c\,d\,e","e\,f\,g"
        List<String> result = new ArrayList<String>();
//        String p ="\",\"";
        String p = "[^\\\\],";
        String replaced = value.replaceAll( "([^\\\\]),", "$1====SEPARATOR====");
        String[] commaSeparated = replaced.split("====SEPARATOR====");
        for(int i = 0; i< commaSeparated.length; i++){
            commaSeparated[i] = commaSeparated[i].replaceAll("\\\\,",",");
        }
//        if(commaSeparated.length >0){
//            commaSeparated[0] = commaSeparated[0].replaceFirst("^\"", "");
//            commaSeparated[commaSeparated.length-1] = commaSeparated[commaSeparated.length-1].replaceFirst("\"$", "");
//        }
        return Arrays.asList(commaSeparated);
    }
    
    public static void main(String[] args){
        TestListConverter t = new TestListConverter();
        List<String> r = t.convert("a\\,b\\,c,c\\,d\\,e,e\\,f\\,g");
        System.out.println(r.size());
    }

    

}
