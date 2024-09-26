//package com.learning.orm.dto;
//
//import com.learning.orm.utils.PoUtil;
//import lombok.Getter;
//import lombok.Setter;
//
//import java.util.List;
//
///**
// * @ClassName: GroupSqlListDto
// * @Description:
// * @Author: WangWei
// * @Date: 2024-05-24
// * @Version V1.0
// **/
//@Getter
//@Setter
//public class GroupSqlListDto {
//    private List<String> tables;
//    private String tableCode;
//    private String minTime;
//    private String maxTime;
//
//    public static GroupSqlListDto initByRang(Class<?> cls, String minTime, String maxTime) {
//        return initByTable(PoUtil.getTimeSlotTableByClass(cls, minTime, maxTime));
//    }
//
//    public static GroupSqlListDto initByRang(String tableCode, String minTime, String maxTime) {
//        return initByTable(PoUtil.getTimeSlotTableByTableCode(tableCode, minTime, maxTime));
//    }
//
//    public static GroupSqlListDto initByTable(List<String> tables) {
//        GroupSqlListDto groupSqlListDto = new GroupSqlListDto();
//        groupSqlListDto.setTables(tables);
//        return groupSqlListDto;
//    }
//}
