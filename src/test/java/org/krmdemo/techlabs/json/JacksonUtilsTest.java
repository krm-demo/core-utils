package org.krmdemo.techlabs.json;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.krmdemo.techlabs.json.JacksonUtils.dumpAsJson;
import static org.krmdemo.techlabs.json.JacksonUtils.dumpAsJsonPrettyPrint;
import static org.krmdemo.techlabs.json.JacksonUtils.jsonArrFromResource;
import static org.krmdemo.techlabs.json.JacksonUtils.jsonArrFromString;
import static org.krmdemo.techlabs.json.JacksonUtils.jsonObjFromResource;
import static org.krmdemo.techlabs.json.JacksonUtils.jsonObjFromString;
import static org.krmdemo.techlabs.json.JacksonUtils.jsonTreeFromResource;
import static org.krmdemo.techlabs.json.JacksonUtils.jsonValueFromString;
import static org.krmdemo.techlabs.json.JacksonUtils.prettifyJson;
import static org.krmdemo.techlabs.stream.CoreStreamUtils.linkedMap;
import static org.krmdemo.techlabs.stream.CoreStreamUtils.nameValue;

public class JacksonUtilsTest {

    @Test
    void testPrettifyJson_WrongJson() {
        assertThatIllegalArgumentException().isThrownBy(
            () -> prettifyJson("la-la-la")
        ).withMessage("""
            could not read the JSON-Tree:
            ---
            'la-la-la'
            ---
            """);
    }

    @Test
    void testPrettifyJson_Scalar() {
        assertThat(prettifyJson("123")).isEqualTo("123");
        assertThat(prettifyJson("123.456")).isEqualTo("123.456");
        assertThat(prettifyJson("\"la-la-la\"")).isEqualTo("\"la-la-la\"");
    }

    @Test
    void testPrettifyJson_Array() {
        assertThat(prettifyJson("[1, 2, \"la-la-la\", null, 3.45]")).isEqualTo("""
            [
              1,
              2,
              "la-la-la",
              null,
              3.45
            ]""");
    }

    @Test
    void testPrettifyJson_Object() {
        assertThat(prettifyJson("{\"a\":1, \"b\":{ \"c\":2, \"d\":\"la-la-la\", \"x\": null}}"))
            .isEqualTo("""
                {
                  "a" : 1,
                  "b" : {
                    "c" : 2,
                    "d" : "la-la-la",
                    "x" : null
                  }
                }""");
    }

    @Test
    void testDumpAsJson_Record() {
        record MyRecord ( String name, Double value, List<Integer> list) {}
        MyRecord myRec = new MyRecord( "some-name", Math.PI, asList(1, 2, 3, 4));
        assertThat(dumpAsJsonPrettyPrint(myRec)).isEqualTo("""
            {
              "name" : "some-name",
              "value" : 3.141592653589793,
              "list" : [
                1,
                2,
                3,
                4
              ]
            }""");
        assertThat(dumpAsJson(myRec)).isEqualTo("""
            {
              "name" : "some-name",
              "value" : 3.141592653589793,
              "list" : [ 1, 2, 3, 4 ]
            }""");
        MyRecord myRecCopy = jsonValueFromString(dumpAsJson(myRec), new TypeReference<>(){});
        assertThat(myRecCopy).isEqualTo(myRec);
    }

    @Test
    void testDumpAsJson_Map() {
        Map<String, Object> myMap = linkedMap(
            nameValue("one", 1),
            nameValue("two", 2),
            nameValue("three", 3)
        );
        assertThat(dumpAsJson(myMap)).isEqualTo("""
            {
              "one" : 1,
              "two" : 2,
              "three" : 3
            }""");
        // Note! that when pretty-printed the keys are ALWAYS SORTED !!!
        assertThat(dumpAsJsonPrettyPrint(myMap)).isEqualTo("""
            {
              "one" : 1,
              "three" : 3,
              "two" : 2
            }""");
        Map<String, Object> myMapCopy = jsonObjFromString(dumpAsJson(myMap));
        assertThat(myMapCopy).isInstanceOf(LinkedHashMap.class);
        assertThat(dumpAsJson(myMapCopy)).isEqualTo("""
            {
              "one" : 1,
              "two" : 2,
              "three" : 3
            }""");

    }

    @Test
    void testDumpAsJson_List() {
        List<Integer> intList = IntStream.rangeClosed(0, 10)
            .map(i -> 1 << i)
            .boxed().toList();
        assertThat(dumpAsJson(intList)).isEqualTo("[ 1, 2, 4, 8, 16, 32, 64, 128, 256, 512, 1024 ]");
        assertThat(dumpAsJsonPrettyPrint(intList)).isEqualTo("""
            [
              1,
              2,
              4,
              8,
              16,
              32,
              64,
              128,
              256,
              512,
              1024
            ]""");
        assertThat(jsonArrFromString(dumpAsJson(intList))).containsExactly(
            1, 2, 4, 8, 16, 32, 64, 128, 256, 512, 1024
        );
    }

    @Test
    void testJsonFromResource_Map() {
        Map<String, Object> aaa = jsonObjFromResource("vars/var-aaa.json");
        assertThat(dumpAsJsonPrettyPrint(aaa)).isEqualTo("""
            {
              "a" : "the value of property 'a'",
              "b" : "the value of property 'b'",
              "c" : {
                "d" : "the value of property 'c.d'",
                "e" : "the value of property 'c.e'"
              },
              "seq" : [
                "1",
                "2",
                "3"
              ]
            }""");
        assertThat(jsonTreeFromResource("vars/var-aaa.json").getNodeType()).isEqualTo(JsonNodeType.OBJECT);
    }

    @Test
    void testJsonFromResource_List() {
        List<Object> bbb = jsonArrFromResource("vars/var-bbb.json");
        assertThat(dumpAsJsonPrettyPrint(bbb)).isEqualTo("""
           [
             {
               "a" : "the value of property '[0].a'",
               "b" : "the value of property '[0].b'"
             },
             "the value of element '[1]'",
             "the value of element '[2]'",
             [
               "the value of element '[3][0]'",
               "the value of element '[3][1]'"
             ]
           ]""");
        assertThat(jsonTreeFromResource("vars/var-bbb.json").getNodeType()).isEqualTo(JsonNodeType.ARRAY);
    }

    @Test
    void testJsonFromResource_ListRecords() {
        record MyRecord ( String name, Double value, List<Integer> list) {}
        List<MyRecord> recordsList = List.of(
            new MyRecord("zero", 0.0, emptyList()),
            new MyRecord("one", Math.PI, List.of(1)),
            new MyRecord("two", 2 * Math.PI, List.of(1, 2)),
            new MyRecord("three", 3 * Math.PI, List.of(1, 2, 3))
        );
        assertThat(dumpAsJsonPrettyPrint(recordsList)).isEqualTo("""
            [
              {
                "name" : "zero",
                "value" : 0.0,
                "list" : [ ]
              },
              {
                "name" : "one",
                "value" : 3.141592653589793,
                "list" : [
                  1
                ]
              },
              {
                "name" : "two",
                "value" : 6.283185307179586,
                "list" : [
                  1,
                  2
                ]
              },
              {
                "name" : "three",
                "value" : 9.42477796076938,
                "list" : [
                  1,
                  2,
                  3
                ]
              }
            ]""");
    }
}
