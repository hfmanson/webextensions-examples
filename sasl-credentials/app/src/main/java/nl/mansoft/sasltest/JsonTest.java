/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.mansoft.sasltest;

import java.io.IOException;
import javax.json.Json;
import javax.json.JsonBuilderFactory;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

/**
 *
 * @author hfman
 */
public class JsonTest {
    public static void writeInt32(int length) throws IOException {
        System.out.write(length);
        System.out.write(length >> 8);
        System.out.write(length >> 16);
        System.out.write(length >> 24);
    }

    public static void writeJson(JsonObject jsonObject) throws IOException {
        String output = jsonObject.toString();
        writeInt32(output.length());
        System.out.print(output);
    }

    public static void main(String[] args) throws IOException {
        JsonBuilderFactory factory = Json.createBuilderFactory(null);
        JsonObjectBuilder outputJsonBuilder = factory.createObjectBuilder()
            .add("mech", "DIGEST-MD5")
            .add("realm", "test-realm.nl")
            .add("s2c", "cmVhbG09InRlc3QtcmVhbG0ubmwiLG5vbmNlPSJ2ckx6cmdrYjlTMUJHYkdkVFVwS1ZGNHhGNkJDNmZDWTBCRGxGbG9xIixjaGFyc2V0PXV0Zi04LGFsZ29yaXRobT1tZDUtc2Vzcw==")
            ;
        writeJson(outputJsonBuilder.build());
        JsonObjectBuilder outputJsonBuilder2 = factory.createObjectBuilder()
            .add("mech", "DIGEST-MD5")
            .add("realm", "test-realm.nl")
            .add("s2c", "cnNwYXV0aD04OWU4MzUyOWJlMjE4ZWZhZWY4NzUwMjMyZmE2ZGEzNw==")
            ;
        writeJson(outputJsonBuilder2.build());
    }
}
