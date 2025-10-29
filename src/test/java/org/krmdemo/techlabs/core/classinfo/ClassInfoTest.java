package org.krmdemo.techlabs.core.classinfo;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.slf4j.Logger;

import java.sql.DriverManager;

import static org.krmdemo.techlabs.core.dump.DumpUtils.dumpAsJsonTxt;

/**
 * TODO: get rid of extra garbage in standard output
 */
@Slf4j
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ClassInfoTest {

    @Test
    @Order(1)
    void testClassInfo_This() {
        ClassInfo thisClassInfo = new ClassInfo(getClass());
        System.out.println("thisClassInfo --> " + dumpAsJsonTxt(thisClassInfo));
    }

    @Test
    @Order(2)
    void testClassInfo_System() {
        ClassInfo systemClassInfo = new ClassInfo(System.class);
        System.out.println("systemClassInfo --> " + dumpAsJsonTxt(systemClassInfo));
    }

    @Test
    @Order(3)
    void testClassInfo_JdbcDriverManager() {
        ClassInfo driverManagerClassInfo = new ClassInfo(DriverManager.class);
        System.out.println("driverManagerClassInfo --> " + dumpAsJsonTxt(driverManagerClassInfo));
    }

    @Test
    @Order(4)
    void testClassInfo_Sl4j() {
        ClassInfo sl4jLoggerClassInfo = new ClassInfo(Logger.class);
        System.out.println("sl4jLoggerClassInfo --> " + dumpAsJsonTxt(sl4jLoggerClassInfo));
    }

    @Test
    @Order(5)
    void testClassInfo_Jackson() {
        ClassInfo objectMapperClassInfo = new ClassInfo(ObjectMapper.class);
        System.out.println("objectMapperClassInfo --> " + dumpAsJsonTxt(objectMapperClassInfo));
    }
}
