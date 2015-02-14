/**
 * Copyright 2014 Yahoo! Inc. Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR 
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the 
 * specific language governing permissions and limitations under the License. 
 * See accompanying LICENSE file.
 */
package com.yahoo.json.utils;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import scala.util.Either;
import scala.util.Left;
import scala.util.Right;

/**
 * Ex: If input file content is 
 * {
 *  "a" : 1,
 *  "b" : ["one", "two"],
 *  "c" : [{"a":1}, {"b" :2}]
 * }
 * 
 * Then the output is 
 * 
 * /a->1
 * /b/0->one
 * /b/1->two
 * /c/0/a->1
 * /c/1/b->2
 * @author srikalyan
 */
public class JSONflatten {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Usage : java -jar JSONflatten.jar Absolute_path_to_json_file");
            System.exit(1);
        }
        if (!new File(args[0]).isFile()) {
            System.err.println("Need valid file path , " + args[0] + " is invalid.");
            System.exit(1);
        }
        try {
            String content = new String(Files.readAllBytes(Paths.get(new URI("file://" + args[0]))));
            JSONObject root = new JSONObject(content);
            deep(new Left<JSONObject, JSONArray>(root), "/");
        } catch (URISyntaxException | IOException ex) {
            Logger.getLogger(JSONflatten.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Get deep into JSON array (or) object.
     * @param json
     * @param prefix 
     */
    private static void deep(Either<JSONObject, JSONArray> json, String prefix) {
        if (json.isLeft()) {
            JSONObject o = json.left().get();
            Iterator iter = o.keys();
            while (iter.hasNext()) {
                Object key = iter.next();
                Object value = o.get(key.toString());
                if (value instanceof JSONArray) {
                    deep(new Right<JSONObject, JSONArray>((JSONArray)value), prefix + key + "/");
                } else if (value instanceof JSONObject) {
                    deep(new Left<JSONObject, JSONArray>((JSONObject)value), prefix + key + "/");
                } else {// Assume String, Integer, Long, Double.
                    System.out.println(prefix + key  + "->" + value);   
                }
            }
        } else {
            JSONArray ja = json.right().get();
            for (int i = 0;i < ja.length();i++) {
                Object value = ja.get(i);
                if (value instanceof JSONArray) {
                    deep(new Right<JSONObject, JSONArray>((JSONArray)value), prefix + i + "/");
                } else if (value instanceof JSONObject) {
                    deep(new Left<JSONObject, JSONArray>((JSONObject)value), prefix + i + "/");
                } else {// Assume String, Integer, Long, Double.
                    System.out.println(prefix + i + "->" + value);
                }
            }
        }
    }
}
