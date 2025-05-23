package com.example.mapper;

import com.example.entity.Admin;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface AdminMapper {

    void insert(Admin admin);

    @Select("select * from `admin` where username = #{username}")
    Admin selectByUsername(String username);

    void updateByID(Admin admin);

    void deleteById(Integer id);

    @Select("select * from `admin` where id = #{id}")
    Admin selectById(Integer id);

    List<Admin> selectAll(Admin admin);
}
