package com.xibin.fin.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.xibin.core.daosupport.BaseMapper;
import com.xibin.fin.pojo.FiVoucher;

public interface FiVoucherMapper extends BaseMapper {
	FiVoucher selectByPrimaryKey(Integer id);

	List<FiVoucher> selectAllByPage(Map map);

	List<FiVoucher> selectByKey(@Param("voucherNum") String voucherNum, @Param("voucherWord") String voucherWord,
			@Param("period") String period, @Param("companyId") String companyId, @Param("bookId") String bookId);

	Integer getMaxVoucherNum(@Param("voucherWord") String voucherWord, @Param("period") String period,
			@Param("companyId") String companyId, @Param("bookId") String bookId);

	List<FiVoucher> selectByExample(FiVoucher example);

	void updateStatus(Map map);

	void updateForEndTerm(Map map);
}