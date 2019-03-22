package com.xibin.wms.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.xibin.core.daosupport.BaseMapper;
import com.xibin.wms.pojo.SysUser;
import com.xibin.wms.query.SysUserQueryItem;

public interface SysUserMapper extends BaseMapper {

	SysUser selectByPrimaryKey(Integer id);

	List<SysUserQueryItem> selectAllByPage(Map map);

	List<SysUserQueryItem> selectByKey(@Param("userName") String userName, @Param("companyId") String companyId);

	List<SysUser> selectByOpenId(@Param("openId") String openId);

	List<SysUser> selectByExample(SysUser example);

	List<SysUser> selectByUserNameAndPassword(@Param("userName") String userName, @Param("password") String password);

}