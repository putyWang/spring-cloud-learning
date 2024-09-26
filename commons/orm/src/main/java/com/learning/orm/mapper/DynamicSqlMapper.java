//package com.learning.orm.mapper;
//
//import com.baomidou.mybatisplus.core.conditions.Wrapper;
//import com.baomidou.mybatisplus.core.metadata.IPage;
//import com.learning.orm.model.DeleteModel;
//import com.learning.orm.model.InsertModel;
//import com.learning.orm.model.SelectModel;
//import com.learning.orm.model.UpdateModel;
//import org.apache.ibatis.annotations.*;
//
//import java.util.List;
//import java.util.Map;
//
///**
// * @ClassName: DynamicSqlMapper
// * @Description: 动态 sql 查询 mapper
// * @Author: WangWei
// * @Date: 2024-05-24
// * @Version V1.0
// **/
//public interface DynamicSqlMapper {
//
//    /**
//     * 分页查询
//     *
//     * @param page
//     * @param ew
//     * @param model
//     * @return
//     */
//    @Select({"<script>select ${model.fieldList} from ${model.tableName}\n        <where>\n            <if test=\"ew !=null and ew.sqlSegment !=null and ew.sqlSegment != ''\">\n                and ${ew.sqlSegment}\n            </if>\n        </where></script>"})
//    IPage<Map<String, Object>> selectPage(IPage page, @Param("ew") Wrapper ew, @Param("model") SelectModel model);
//
//    /**
//     * 列表查询
//     *
//     * @param ew
//     * @param model
//     * @return
//     */
//    @Select({"<script>select ${model.fieldList} from ${model.tableName}\n        <where>\n            <if test=\"ew !=null and ew.sqlSegment !=null and ew.sqlSegment != ''\">\n                and ${ew.sqlSegment}\n            </if>\n        </where></script>"})
//    List<Map<String, Object>> selectList(@Param("ew") Wrapper ew, @Param("model") SelectModel model);
//
//    /**
//     * 单个查询
//     *
//     * @param ew
//     * @param model
//     * @return
//     */
//    @Select({"<script>select ${model.fieldList} from ${model.tableName}\n        <where>\n            <if test=\"ew !=null and ew.sqlSegment !=null and ew.sqlSegment != ''\">\n                and ${ew.sqlSegment}\n            </if>\n        </where></script>"})
//    Map<String, Object> selectOne(@Param("ew") Wrapper ew, @Param("model") SelectModel model);
//
//    /**
//     * 数量统计查询
//     *
//     * @param ew
//     * @param model
//     * @return
//     */
//    @Select({"<script>select count(1) from ${model.tableName}\n        <where>\n            <if test=\"ew !=null and ew.sqlSegment !=null and ew.sqlSegment != ''\">\n                and ${ew.sqlSegment}\n            </if>\n        </where></script>"})
//    int selectCount(@Param("ew") Wrapper ew, @Param("model") SelectModel model);
//
//    /**
//     * 新增
//     *
//     * @param model
//     * @return
//     */
//    @Insert({"<script>insert into ${model.tableName} (${model.keys}) \n        <foreach collection=\"model.fieldMap\" item=\"value\" index=\"key\" separator=\",\" open=\"values(\" close=\")\">\n            #{value}\n        </foreach></script>"})
//    int insert(@Param("model") InsertModel model);
//
//    /**
//     * 更新
//     *
//     * @param ew
//     * @param model
//     * @return
//     */
//    @Update({"<script>update ${model.tableName} set\n        <foreach collection=\"model.fieldMap\" item=\"value\" index=\"key\" separator=\",\">\n            ${key}=#{value}\n        </foreach>\n        <where>\n            <if test=\"ew !=null and ew.sqlSegment !=null and ew.sqlSegment != ''\">\n                and ${ew.sqlSegment}\n            </if>\n        </where></script>"})
//    int update(@Param("ew") Wrapper ew, @Param("model") UpdateModel model);
//
//    /**
//     * 删除
//     *
//     * @param ew
//     * @param model
//     * @return
//     */
//    @Delete({"<script>delete from ${model.tableName}\n        <where>\n            <if test=\"ew !=null and ew.sqlSegment !=null and ew.sqlSegment != ''\">\n                and ${ew.sqlSegment}\n            </if>\n        </where></script>"})
//    int delete(@Param("ew") Wrapper ew, @Param("model") DeleteModel model);
//}
