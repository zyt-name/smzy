package com.clou.userdemo.mapper;

import com.clou.userdemo.POJO.po.MerchantPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface MerchantMapper {

    @Select("SELECT * FROM merchants WHERE id = #{id}")
    MerchantPO selectById(Integer id);

    @Select("<script>" +
            "SELECT * FROM merchants WHERE id IN " +
            "<foreach item='item' index='index' collection='ids' open='(' separator=',' close=')'>" +
            "#{item}" +
            "</foreach>" +
            "</script>")
    List<MerchantPO> selectByIds(List<Integer> ids);

    @Select("<script>" +
            "SELECT * FROM merchants " +
            "<where>" +
            "<if test='username != null and username != \"\"'>" +
            "AND username LIKE CONCAT('%', #{username}, '%')" +
            "</if>" +
            "</where>" +
            "ORDER BY created_at DESC " +
            "LIMIT #{offset}, #{pageSize}" +
            "</script>")
    List<MerchantPO> selectMerchantList(@Param("username") String username,
                                         @Param("offset") Integer offset,
                                         @Param("pageSize") Integer pageSize);

    @Select("<script>" +
            "SELECT COUNT(*) FROM merchants " +
            "<where>" +
            "<if test='username != null and username != \"\"'>" +
            "AND username LIKE CONCAT('%', #{username}, '%')" +
            "</if>" +
            "</where>" +
            "</script>")
    Long countMerchantList(@Param("username") String username);

}