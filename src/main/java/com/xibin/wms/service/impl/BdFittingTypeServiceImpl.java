package com.xibin.wms.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.xibin.core.costants.Constants;
import com.xibin.core.daosupport.BaseManagerImpl;
import com.xibin.core.daosupport.BaseMapper;
import com.xibin.core.exception.BusinessException;
import com.xibin.core.exception.DaoException;
import com.xibin.core.security.pojo.UserDetails;
import com.xibin.wms.dao.BdFittingTypeMapper;
import com.xibin.wms.pojo.BdFittingType;
import com.xibin.wms.service.BdFittingTypeService;
@Service
public class BdFittingTypeServiceImpl extends BaseManagerImpl implements BdFittingTypeService {
	@Autowired
	HttpSession session;
	@Resource
	private BdFittingTypeMapper bdFittingTypeMapper;
	@Override
	public BdFittingType getFittingTypeById(int id) {
		// TODO Auto-generated method stub
		return bdFittingTypeMapper.selectByPrimaryKey(id);
	}


	@Override
	public List<BdFittingType> getAllFittingTypeByPage(Map map) {
		// TODO Auto-generated method stub
		return bdFittingTypeMapper.selectAllByPage(map);
	}

	@Override
	public int removeFittingType(int id) {
		// TODO Auto-generated method stub
		int []ids = {id};
		return this.delete(ids);
	}

	@Override
	public BdFittingType saveFittingType(BdFittingType model) throws BusinessException{
		// TODO Auto-generated method stub
		UserDetails userDetails = (UserDetails)session.getAttribute(Constants.SESSION_USER_KEY);
		model.setCompanyId(userDetails.getCompanyId());
		List<BdFittingType> list = bdFittingTypeMapper.selectByKey(model.getFittingTypeCode(),userDetails.getCompanyId());
		if(list.size()>0&&model.getId()==0){
			throw new BusinessException("编码：["+model.getFittingTypeCode()+"] 已存在，不能重复！");
		}
		return (BdFittingType) this.save(model);
	}


	@Override
	public List<BdFittingType> selectByKey(String fittingTypeCode) {
		// TODO Auto-generated method stub
		UserDetails userDetails = (UserDetails)session.getAttribute(Constants.SESSION_USER_KEY);
		return bdFittingTypeMapper.selectByKey(fittingTypeCode,userDetails.getCompanyId());
	}

	@Override
	protected BaseMapper getMapper() {
		// TODO Auto-generated method stub
		return bdFittingTypeMapper;
	}

}
