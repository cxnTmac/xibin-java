package com.xibin.fin.service.impl;

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
import com.xibin.fin.dao.FiAuxiliaryMapper;
import com.xibin.fin.dao.FiBookMapper;
import com.xibin.fin.dao.FiCourseMapper;
import com.xibin.fin.pojo.FiAuxiliary;
import com.xibin.fin.pojo.FiBook;
import com.xibin.fin.pojo.FiCourse;
import com.xibin.fin.service.FiAuxiliaryService;
import com.xibin.fin.service.FiBookService;
import com.xibin.fin.service.FiCourseService;
import com.xibin.wms.dao.BdAreaMapper;
import com.xibin.wms.pojo.BdArea;
import com.xibin.wms.service.BdAreaService;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
@Transactional(propagation = Propagation.REQUIRED)
@Service
public class FiAuxiliaryServiceImpl extends BaseManagerImpl implements FiAuxiliaryService {
	@Autowired
	private HttpSession session;
	@Autowired
	private FiAuxiliaryMapper fiAuxiliaryMapper;
	@Override
	public FiAuxiliary getAuxiliaryById(int id) {
		// TODO Auto-generated method stub
		return fiAuxiliaryMapper.selectByPrimaryKey(id);
	}
	@Override
	public List<FiAuxiliary> getAllAuxiliaryByPage(Map map) {
		// TODO Auto-generated method stub
		return fiAuxiliaryMapper.selectAllByPage(map);
	}

	@Override
	public int removeAuxiliary(int id) throws BusinessException{
		// TODO Auto-generated method stub
		int []ids = {id};
		return this.delete(ids);
	}

	@Override
	public FiAuxiliary saveAuxiliary(FiAuxiliary model) throws BusinessException{
		// TODO Auto-generated method stub
		UserDetails userDetails = (UserDetails)session.getAttribute(Constants.SESSION_USER_KEY);
		model.setCompanyId(userDetails.getCompanyId());
		return (FiAuxiliary) this.save(model);
	}

	

	@Override
	protected BaseMapper getMapper() {
		// TODO Auto-generated method stub
		return fiAuxiliaryMapper;
	}

	private boolean deleteBeforeCheck(String areaCode){
		return true;
	}
	
}
