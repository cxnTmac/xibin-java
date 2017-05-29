package com.xibin.wms.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.xibin.wms.pojo.SysUser;

public interface SysUserMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(SysUser record);

    int insertSelective(SysUser record);

    SysUser selectByPrimaryKey(Integer id);
    
    List<SysUser> selectAll();
    
    List<SysUser> selectAllByPage(Map map);
    
    List<SysUser> selectByUserNameAndPassword(@Param("userName")String userName,@Param("password")String password);

    int updateByPrimaryKeySelective(SysUser record);

    int updateByPrimaryKey(SysUser record);
}