package org.krmdemo.techlabs.core.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.FieldSource;
import org.krmdemo.techlabs.core.dump.DumpUtils;
import org.krmdemo.techlabs.core.model.Angle;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.SequencedSet;
import java.util.stream.StreamSupport;

import static org.assertj.core.api.Assertions.assertThat;
import static org.krmdemo.techlabs.core.utils.CoreCollectors.toLinkedMap;

/**
 * A unit-test to verify the utility-class {@link JacksonUtils}
 */
public class JacksonUtilsTest {

    private static final String RESOURCE_PATH__SINGE_RECORD =
        "org/krmdemo/techlabs/core/dump/testSingleRecord/testSingleRecord--expected.json";

    @Test
    void testSingleResource_JsonTree() {
        JsonNode jsonRoot = JacksonUtils.jsonTreeFromResource(RESOURCE_PATH__SINGE_RECORD);
        assertThat(jsonRoot.getNodeType()).isEqualTo(JsonNodeType.OBJECT);
        assertThat(jsonRoot.get("degrees").asInt()).isEqualTo(40);
        assertThat(jsonRoot.get("radians").asDouble()).isEqualTo(0.6981317007977318);
        assertThat(jsonRoot.get("formulas-result-map").get("sin").asDouble())
            .isEqualTo(0.642787609687);

        JsonNode jsonRootFromString = JacksonUtils.jsonTreeFromString(
            CoreResourceUtils.resourceAsText(RESOURCE_PATH__SINGE_RECORD)
        );
        assertThat(jsonRootFromString).isEqualTo(jsonRoot);
    }

    @Test
    void testSingleResource_JsonObj() {
        Map<String, Object> jsonObj = JacksonUtils.jsonObjFromResource(RESOURCE_PATH__SINGE_RECORD);
        assertThat(jsonObj).containsEntry("degrees", "40");
        assertThat(jsonObj).containsEntry("radians", "0.6981317007977318");
        @SuppressWarnings("unchecked") Map<String, Object> resultMap =
            (Map<String, Object>) jsonObj.get("formulas-result-map");
        assertThat(resultMap).containsEntry("sin", "0.642787609687");

        Map<String, Object> jsonObjFromString = JacksonUtils.jsonObjFromString(
            CoreResourceUtils.resourceAsText(RESOURCE_PATH__SINGE_RECORD)
        );
        assertThat(jsonObjFromString).isEqualTo(jsonObj);
    }

    @Test
    void testSingleResource_JsonValue_Class() {
        Angle angle =  JacksonUtils.jsonValueFromResource(RESOURCE_PATH__SINGE_RECORD, Angle.class);
        assertThat(angle.degrees()).isEqualTo(40);
        assertThat(angle.radians()).isEqualTo(0.6981317007977318);
        assertThat(angle.formulasMap()).containsEntry("sin", 0.642787609687);
    }

    @Test
    void testSingleResource_JsonValue_TypeReference() {
        Angle angle = JacksonUtils.jsonValueFromResource(RESOURCE_PATH__SINGE_RECORD, new TypeReference<>(){});
        assertThat(angle.degrees()).isEqualTo(40);
        assertThat(angle.radians()).isEqualTo(0.6981317007977318);
        assertThat(angle.formulasMap()).containsEntry("sin", 0.642787609687);
    }

    private static final String RESOURCE_PATH__ARRAY_OF_RECORDS =
        "org/krmdemo/techlabs/core/dump/testArrayOfRecords/testArrayOfRecords--expected.json";

    @Test
    void testArrayResource_JsonTree() {
        JsonNode jsonArr = JacksonUtils.jsonTreeFromResource(RESOURCE_PATH__ARRAY_OF_RECORDS);
        assertThat(jsonArr.getNodeType()).isEqualTo(JsonNodeType.ARRAY);
        assertThat(jsonArr.size()).isEqualTo(8);
        List<Integer> degrees = StreamSupport.stream(jsonArr.spliterator(), false)
            .map(childNode -> childNode.get("degrees").asInt())
            .toList();
        assertThat(degrees).containsExactly(180, 120, 90, 60, 45, 40, 30, 20);
    }

    @Test
    void testArrayResource_JsonArr() {
        List<Object> listObj = JacksonUtils.jsonArrFromResource(RESOURCE_PATH__ARRAY_OF_RECORDS);
        assertThat(listObj).hasSize(8);
        List<Integer> degrees = listObj.stream()
            .map(Map.class::cast)
            .map(childMap -> childMap.get("degrees"))
            .map(elem -> Integer.valueOf("" + elem))
            .toList();
        assertThat(degrees).containsExactly(180, 120, 90, 60, 45, 40, 30, 20);
    }

    @Test
    void testArrayResource_JsonValue_TypeReference() {
        List<Angle> anglesList = JacksonUtils.jsonValueFromResource(RESOURCE_PATH__ARRAY_OF_RECORDS, new TypeReference<>(){});
        assertThat(anglesList).hasSize(8);
        List<Integer> degrees = anglesList.stream().map(Angle::degrees).toList();
        assertThat(degrees).containsExactly(180, 120, 90, 60, 45, 40, 30, 20);
    }

    @Test
    void testArrayResource_JsonArr_Class() {
        List<Angle> anglesList = JacksonUtils.jsonArrFromResource(RESOURCE_PATH__ARRAY_OF_RECORDS, Angle.class);
        assertThat(anglesList).hasSize(8);
        List<Integer> degrees = anglesList.stream().map(Angle::degrees).toList();
        assertThat(degrees).containsExactly(180, 120, 90, 60, 45, 40, 30, 20);
    }

    @Test
    void testArrayResource_JsonArr_JavaType() {
        JavaType listType = TypeFactory.defaultInstance().constructType(Angle.class);
        List<Angle> anglesList = JacksonUtils.jsonArrFromResource(RESOURCE_PATH__ARRAY_OF_RECORDS, listType);
        assertThat(anglesList).hasSize(8);
        List<Integer> degrees = anglesList.stream().map(Angle::degrees).toList();
        assertThat(degrees).containsExactly(180, 120, 90, 60, 45, 40, 30, 20);
    }

    @Test
    void testStaticLoad_List_PropsA() {
        List<PropsA> listA = JacksonUtils.jsonValueFromString(JSON_INPUT_01, new TypeReference<>(){});
        assertThat(DumpUtils.dumpAsJsonTxt(listA)).isEqualTo(JSON_OUTPUT_01);
        listA.forEach(item -> assertThat(item).isInstanceOf(PropsA.class));
    }

    @Test
    void testStaticLoad_List_PropsB() {
        List<PropsB> listB = JacksonUtils.jsonValueFromString(JSON_INPUT_01, new TypeReference<>(){});
        assertThat(DumpUtils.dumpAsJsonTxt(listB)).isEqualTo(JSON_OUTPUT_01);
        listB.forEach(item -> assertThat(item).isInstanceOf(PropsB.class));
    }

    @Test
    void testStaticLoad_LinkedSet_PropsA() {
        SequencedSet<PropsA> linkedSetA = JacksonUtils.jsonValueFromString(JSON_INPUT_01, new TypeReference<>(){});
        assertThat(DumpUtils.dumpAsJsonTxt(linkedSetA)).isEqualTo(JSON_OUTPUT_01);
        linkedSetA.forEach(item -> assertThat(item).isInstanceOf(PropsA.class));
    }

    @Test
    void testStaticLoad_LinkedSet_PropsB() {
        SequencedSet<PropsB> linkedSetB = JacksonUtils.jsonValueFromString(JSON_INPUT_01, new TypeReference<>(){});
        assertThat(DumpUtils.dumpAsJsonTxt(linkedSetB)).isEqualTo(JSON_OUTPUT_01);
        linkedSetB.forEach(item -> assertThat(item).isInstanceOf(PropsB.class));
    }

    @SuppressWarnings("unused") // <-- used by field-name in @FieldSource
    final static List<Class<?>> ITEM_CLASSES = List.of(PropsA.class, PropsB.class);

    @ParameterizedTest
    @FieldSource("ITEM_CLASSES")
    void testDynamicLoad_List_byClass(Class<?> itemClass) {
        System.out.println("itemClass --> " + itemClass);
        JavaType listType = TypeFactory.defaultInstance().constructCollectionType(List.class, itemClass);
        List<?> listItems = JacksonUtils.jsonValueFromString(JSON_INPUT_01, listType);
        assertThat(DumpUtils.dumpAsJsonTxt(listItems)).isEqualTo(JSON_OUTPUT_01);
        listItems.forEach(item -> assertThat(item).isInstanceOf(itemClass));
    }

    @ParameterizedTest
    @FieldSource("ITEM_CLASSES")
    void testDynamicLoad_LinkedSet_byClass(Class<?> itemClass) {
        JavaType linkedSetType = TypeFactory.defaultInstance().constructCollectionType(SequencedSet.class, itemClass);
        SequencedSet<?> listItems = JacksonUtils.jsonValueFromString(JSON_INPUT_01, linkedSetType);
        assertThat(DumpUtils.dumpAsJsonTxt(listItems)).isEqualTo(JSON_OUTPUT_01);
        listItems.forEach(item -> assertThat(item).isInstanceOf(itemClass));
    }

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JsonPropertyOrder({"prop-one", "prop-two", "prop-three"})
    record PropsA(
        @JsonProperty("prop-one") String propOne,
        @JsonProperty("prop-two") int propTwo,
        @JsonProperty("prop-three") Double propThree
    ) {}

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JsonPropertyOrder({"prop-one", "prop-two", "prop-three"})
    record PropsB(
        @JsonProperty("prop-one") String propOne,
        @JsonProperty("prop-two") BigInteger propTwo,
        @JsonProperty("prop-three") BigDecimal propThree
    ) {}

    final String JSON_INPUT_01 = """
        [
          { "prop-one": "item #1", "prop-two": 111, "prop-three": 123.456 },
          { "prop-one": "item #2", "prop-two": 222, "prop-three": 234.567 },
          { "prop-one": "item #3", "prop-two": 333, "prop-three": 345.678 },
          { "prop-one": "item #4", "prop-two": 444, "prop-three": 456.789 }
        ]""";

    final String JSON_OUTPUT_01 = """
        [
          {
            "prop-one": "item #1",
            "prop-two": "111",
            "prop-three": "123.456"
          },
          {
            "prop-one": "item #2",
            "prop-two": "222",
            "prop-three": "234.567"
          },
          {
            "prop-one": "item #3",
            "prop-two": "333",
            "prop-three": "345.678"
          },
          {
            "prop-one": "item #4",
            "prop-two": "444",
            "prop-three": "456.789"
          }
        ]""";
}
