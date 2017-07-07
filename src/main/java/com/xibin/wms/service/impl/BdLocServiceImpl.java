package com.xibin.wms.service.impl;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.xibin.core.costants.Constants;
import com.xibin.core.daosupport.BaseManagerImpl;
import com.xibin.core.daosupport.BaseMapper;
import com.xibin.core.exception.BusinessException;
import com.xibin.core.security.pojo.UserDetails;
import com.xibin.wms.dao.BdAreaMapper;
import com.xibin.wms.dao.BdLocMapper;
import com.xibin.wms.dao.BdZoneMapper;
import com.xibin.wms.pojo.BdArea;
import com.xibin.wms.pojo.BdLoc;
import com.xibin.wms.pojo.BdZone;
import com.xibin.wms.query.BdLocQueryItem;
import com.xibin.wms.query.BdZoneQueryItem;
import com.xibin.wms.service.BdAreaService;
import com.xibin.wms.service.BdLocService;
import com.xibin.wms.service.BdZoneService;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
@Transactional(propagation = Propagation.REQUIRED)
@Service
public class BdLocServiceImpl extends BaseManagerImpl implements BdLocService {
	@Autowired
	private HttpSession session;
	@Autowired
	private BdLocMapper bdLocMapper;
	@Override
	public BdLoc getLocById(int id) {
		// TODO Auto-generated method stub
		return bdLocMapper.selectByPrimaryKey(id);
	}
	@Override
	public List<BdLocQueryItem> getAllLocByPage(Map map) {
		// TODO Auto-generated method stub
		return bdLocMapper.selectAllByPage(map);
	}

	@Override
	public int removeLoc(int id, String locCde) throws BusinessException{
		// TODO Auto-generated method stub
		if(!deleteBeforeCheck(locCde)){
			throw new BusinessException("该库位编码["+locCde+"]已被库区引用，不能删除");
		}else{
			int []ids = {id};
			return this.delete(ids);
		}
	}

	@Override
	public BdLoc saveLoc(BdLoc model) throws BusinessException{
		// TODO Auto-generated method stub
		UserDetails userDetails = (UserDetails)session.getAttribute(Constants.SESSION_USER_KEY);
		model.setCompanyId(userDetails.getCompanyId());
		model.setWarehouseId(userDetails.getWarehouseId());
		List<BdLocQueryItem> list = bdLocMapper.selectByKey(model.getLocCode(),model.getCompanyId().toString(),model.getWarehouseId().toString());
		if(list.size()>0&&model.getId()==0){
			throw new BusinessException("编码：["+model.getLocCode()+"] 已存在，不能重复！");
		}
		return (BdLoc) this.save(model);
	}

	@Override
	public List<BdLocQueryItem> selectByKey(String locCode) {
		// TODO Auto-generated method stub
		UserDetails userDetails = (UserDetails)session.getAttribute(Constants.SESSION_USER_KEY);
		return bdLocMapper.selectByKey(locCode,userDetails.getCompanyId().toString(),userDetails.getWarehouseId().toString());
	}

	@Override
	protected BaseMapper getMapper() {
		// TODO Auto-generated method stub
		return bdLocMapper;
	}

	private boolean deleteBeforeCheck(String areaCode){
		return true;
	}
}
