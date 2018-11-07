/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.mansoft.sasltest;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author hfman
 */
public class RegExpTest {
    static final String RE_SASL_MECH = "[A-Z0-9-_]{1,20}";
    static final String RE_MECHSTRING = "(\"" + RE_SASL_MECH + "(?:[ ]" + RE_SASL_MECH + ")*\")";
    static final String RE_DNSSTRING = "\"([a-zA-Z0-9-_]+(?:\\.[a-zA-Z0-9-_]+)+)\"";

    static final String RE_BWS = "[ \\t]*";
    static final String RE_OWS = RE_BWS;
    static final String RE_TOKEN68 = "([a-zA-Z0-9-._~+/]+=*)";
    static final String RE_AUTH_PARAM =
        "(?:" +
            "([CcSs][2][CcSs])" + RE_BWS + "=" + RE_BWS + RE_TOKEN68 +
            "|" +
            "([Mm][Ee][Cc][Hh])" + RE_BWS + "=" + RE_BWS + RE_MECHSTRING +
            "|" +
            "([Rr][Ee][Aa][Ll][Mm])" + RE_BWS + '=' + RE_BWS + RE_DNSSTRING +
        ")"
    ;
    static final String RE_AUTH_SCHEME = "[Ss][Aa][Ss][Ll]";
    static final String RE_CREDENTIALS = RE_AUTH_SCHEME + "(?:[ ]+(" + RE_AUTH_PARAM + "(?:" +
        RE_OWS + "," + RE_OWS + RE_AUTH_PARAM + ")+)?)";


    public static void quoted_string_test() {
        // "[^"\\]*(?:\\.[^"\\]*)*"
        String quoted_string = "\"([^\"\\\\]*(?:\\\\.[^\"\\\\]*)*)\"";
        System.out.println(quoted_string);
        String input = "\"hallo zei hij\\\"\"";
        System.out.println(input);
        Pattern pattern = Pattern.compile(quoted_string);
        Matcher matcher = pattern.matcher(input);
        if (matcher.matches()) {
            for (int i = 0; i <= matcher.groupCount(); i++) {
                System.out.println("group: " + i);
                System.out.println(matcher.group(i));
            }
        }
    }

    public static String readFileAsString(String fileName) throws Exception
    {
        return new String(Files.readAllBytes(Paths.get(fileName)));
    }

    public static String readRegExp() {
        String result = null;
        try {
            result = readFileAsString("c:\\Users\\hfman\\Documents\\arpa2\\re_credentials.txt");
        } catch (Exception ex) {
            Logger.getLogger(RegExpTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        //String input = "SAsL c2s=11bbaa=, s2s=190284ijrjwerowieu987d9fs===, c2c=2kkasjf923y92i3h4, s2c=alskjoeiqwr98237492834=====,mech=\t\"TRA LA LALALA\",realm=\"a.b\"";
        String input = "SAsL c2s=11bbaa=, s2s=190284ijrjwerowieu987d9fs===, c2c=2kkasjf923y92i3h4, s2c=alskjoeiqwr98237492834=====,mech=\t\"TRA LA LALALA\", realm\t = \t\t   \t  \"dynamo.nep\"";

        System.out.println(input);
        //String regexp = readRegExp();
        String regexp = RE_CREDENTIALS;
        System.out.println(regexp);
        Pattern authorization_stx = Pattern.compile(regexp);
        Matcher matcher1 = authorization_stx.matcher(input);
        if (matcher1.matches()) {
            Pattern auth_param_finder = Pattern.compile(RE_AUTH_PARAM);
            Matcher matcher2 = auth_param_finder.matcher(input);
            HashMap<String, String> hasMap = new HashMap<>();
            while (matcher2.find()) {
                for (int i = 1; i <= matcher2.groupCount(); i += 2) {
                    if (matcher2.group(i) != null) {
                        hasMap.put(matcher2.group(i), matcher2.group(i + 1));
                    }
                }
            }
            System.out.println(hasMap);
        } else {
            System.out.println("No match");
        }
    }
}






