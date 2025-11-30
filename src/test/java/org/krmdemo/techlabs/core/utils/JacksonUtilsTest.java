package org.krmdemo.techlabs.core.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.FieldSource;
import org.krmdemo.techlabs.core.dump.DumpUtils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.SequencedSet;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * A unit-test to verify the utility-class {@link JacksonUtils}
 */
public class JacksonUtilsTest {

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
