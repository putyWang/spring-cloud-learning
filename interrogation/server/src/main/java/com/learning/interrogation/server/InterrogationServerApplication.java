package com.learning.interrogation.server;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;
import com.learning.interrogation.domain.vo.PatientAddVO;
import lombok.Data;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.data.redis.core.script.DefaultRedisScript;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author wangwei
 * @version 1.0
 * @date 2025/4/30 下午10:04
 */
@SpringBootApplication
public class InterrogationServerApplication {
    private final static List<Area> AREA_CODE = new ArrayList<>();
    private final static Map<String, String> CITY_CODE_MAP = new HashMap<>();
    private final static Map<String, String> PROVINCE_CODE_MAP = new HashMap<>();

    private static final int[] WEIGHTS = {7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2};
    private static final char[] CHECK_CODES = {'1', '0', 'X', '9', '8', '7', '6', '5', '4', '3', '2'};

    // 常见的手机号码号段
    private static final List<String> MOBILE_PREFIXES = new ArrayList<>();

    static {
        // 中国移动号段
        MOBILE_PREFIXES.add("134");
        MOBILE_PREFIXES.add("135");
        MOBILE_PREFIXES.add("136");
        MOBILE_PREFIXES.add("137");
        MOBILE_PREFIXES.add("138");
        MOBILE_PREFIXES.add("139");
        MOBILE_PREFIXES.add("147");
        MOBILE_PREFIXES.add("150");
        MOBILE_PREFIXES.add("151");
        MOBILE_PREFIXES.add("152");
        MOBILE_PREFIXES.add("157");
        MOBILE_PREFIXES.add("158");
        MOBILE_PREFIXES.add("159");
        MOBILE_PREFIXES.add("172");
        MOBILE_PREFIXES.add("178");
        MOBILE_PREFIXES.add("182");
        MOBILE_PREFIXES.add("183");
        MOBILE_PREFIXES.add("184");
        MOBILE_PREFIXES.add("187");
        MOBILE_PREFIXES.add("188");
        MOBILE_PREFIXES.add("195");
        MOBILE_PREFIXES.add("197");
        MOBILE_PREFIXES.add("198");

        // 中国联通号段
        MOBILE_PREFIXES.add("130");
        MOBILE_PREFIXES.add("131");
        MOBILE_PREFIXES.add("132");
        MOBILE_PREFIXES.add("145");
        MOBILE_PREFIXES.add("155");
        MOBILE_PREFIXES.add("156");
        MOBILE_PREFIXES.add("166");
        MOBILE_PREFIXES.add("167");
        MOBILE_PREFIXES.add("171");
        MOBILE_PREFIXES.add("175");
        MOBILE_PREFIXES.add("176");
        MOBILE_PREFIXES.add("185");
        MOBILE_PREFIXES.add("186");
        MOBILE_PREFIXES.add("196");

        // 中国电信号段
        MOBILE_PREFIXES.add("133");
        MOBILE_PREFIXES.add("149");
        MOBILE_PREFIXES.add("153");
        MOBILE_PREFIXES.add("162");
        MOBILE_PREFIXES.add("173");
        MOBILE_PREFIXES.add("174");
        MOBILE_PREFIXES.add("177");
        MOBILE_PREFIXES.add("180");
        MOBILE_PREFIXES.add("181");
        MOBILE_PREFIXES.add("189");
        MOBILE_PREFIXES.add("190");
        MOBILE_PREFIXES.add("191");
        MOBILE_PREFIXES.add("193");
        MOBILE_PREFIXES.add("199");

        // 中国广电号段
        MOBILE_PREFIXES.add("192");
    }

    private final static List<String> RELATION_CODE_LIST = new ArrayList<>();

    static {
        RELATION_CODE_LIST.add("00");
        RELATION_CODE_LIST.add("01");
        RELATION_CODE_LIST.add("02");
        RELATION_CODE_LIST.add("03");
        RELATION_CODE_LIST.add("04");
        RELATION_CODE_LIST.add("05");
        RELATION_CODE_LIST.add("06");
    }

    public static String generatePhoneNumber() {
        Random random = new Random();
        // 随机选择一个号段
        String prefix = MOBILE_PREFIXES.get(random.nextInt(MOBILE_PREFIXES.size()));
        StringBuilder phoneNumber = new StringBuilder(prefix);

        // 生成剩余的 8 位数字
        for (int i = 0; i < 8; i++) {
            phoneNumber.append(random.nextInt(10));
        }

        return phoneNumber.toString();
    }

    static {
        // 创建资源模式解析器
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        // 获取 scripts 文件夹下的所有 Lua 脚本资源
        try {
            Resource[] resources = resolver.getResources("classpath:area/*.json");
            for (Resource resource : resources) {
                // 获取脚本文件的名称
                String scriptName = resource.getFilename();
                if (scriptName != null) {
                    StringBuilder scriptContent = new StringBuilder();
                    try (InputStream inputStream = resource.getInputStream();
                         BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
                        String line;
                        while ((line = reader.readLine()) != null) {
                            scriptContent.append(line).append("\n");
                        }
                    }
                    if (scriptName.startsWith("areas")) {
                        AREA_CODE.addAll(JSON.parseArray(scriptContent.toString(), Area.class));
                    } else if (scriptName.startsWith("cities")) {
                        CITY_CODE_MAP.putAll(JSON.parseArray(scriptContent.toString(), City.class).stream().collect(Collectors.toMap(City::getCode, City::getName)));
                    } else {
                        PROVINCE_CODE_MAP.putAll(JSON.parseArray(scriptContent.toString(), Province.class).stream().collect(Collectors.toMap(Province::getCode, Province::getName)));
                    }

                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Data
    public static class Province {
        private String code;
        private String name;
    }

    @Data
    public static class City {
        private String code;
        private String name;
        private String provinceCode;
    }

    @Data
    public static class Area {
        private String code;
        private String name;
        private String cityCode;
        private String provinceCode;
    }

    public static void setID(PatientAddVO patient) {
        Random random = new Random();
        // 地区码
        Area area = AREA_CODE.get(random.nextInt(AREA_CODE.size()));
        // 生日
        Calendar calendar = new GregorianCalendar();
        calendar.set(Calendar.YEAR, random.nextInt(100) + 1920);
        calendar.set(Calendar.MONTH, random.nextInt(12));
        calendar.set(Calendar.DAY_OF_MONTH, random.nextInt(28) + 1);
        String birthDate = String.format("%04d%02d%02d", calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH));
        // 顺序码
        int sequentialNumber = random.nextInt(1000);
        String sequentialCode = String.format("%03d", sequentialNumber);
        String gender = sequentialNumber % 2 == 0 ? "女" : "男";
        // 组合前 17 位
        String id17 = area.getCode() + birthDate + sequentialCode;
        // 计算校验码
        int sum = 0;
        for (int i = 0; i < 17; i++) {
            sum += Integer.parseInt(String.valueOf(id17.charAt(i))) * WEIGHTS[i];
        }
        char checkCode = CHECK_CODES[sum % 11];
        patient.setDCSNY(DateUtil.formatDate(DateUtil.parse(birthDate, "YYYYMMdd"))).setCXBMC(gender)
                .setCXBBM(gender.equals("男") ? "1" : "2");
        // 设置地区
        patient.setCSDZBM(area.getProvinceCode()).setCSDZMC(PROVINCE_CODE_MAP.get(area.getProvinceCode()))
                .setCSQDZBM(area.getCityCode().substring(2, 4)).setCSQDZMC(CITY_CODE_MAP.get(area.getCityCode()))
                .setCQDZBM(area.getCode().substring(4, 6)).setCQDZMC(area.getName());
        patient.setCJZRSFZH(id17 + checkCode);
    }

    public static void main(String[] args) throws JsonProcessingException {
        SpringApplication.run(InterrogationServerApplication.class, args);
//        Faker faker = new Faker(Locale.CHINA);
//        ObjectMapper objectMapper = new ObjectMapper();
//        List<PatientAddVO> patientList = new ArrayList<>();
//
//        for (int i = 0; i < 600; i++) {
//            PatientAddVO patient = new PatientAddVO();
//            patient.setCJZRXM(faker.name().fullName());
//            patient.setCZJLX("01");
//            patient.setCZJLXMC("居民身份证");
//            setID(patient);
//            patient.setCMPH(faker.address().streetAddress());
//
//
//            patient.setCMZBM(String.valueOf(faker.number().numberBetween(1, 56)));
//            patient.setCMZMC(faker.options().option("汉族", "蒙古族", "回族", "藏族", "维吾尔族"));
//            patient.setCJZRSJ(generatePhoneNumber());
//
//            patient.setCGXBM(RELATION_CODE_LIST.get(new Random().nextInt(RELATION_CODE_LIST.size())));
//
//            patient.setISFMR(0);
//            patient.setOrgCode("00001");
//            patient.setOrgName("测试医院");
//            patientList.add(patient);
//        }
//
//        String json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(patientList);
//        System.out.println(json);
    }
}
