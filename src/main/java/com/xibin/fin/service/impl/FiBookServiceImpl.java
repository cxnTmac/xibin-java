package com.xibin.fin.service.impl;

import java.util.HashMap;
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
import com.xibin.core.utils.CodeGenerator;
import com.xibin.fin.dao.FiBookMapper;
import com.xibin.fin.dao.FiCourseMapper;
import com.xibin.fin.pojo.FiBook;
import com.xibin.fin.pojo.FiCourse;
import com.xibin.fin.service.FiBookService;
import com.xibin.fin.service.FiCourseService;
import com.xibin.wms.dao.BdAreaMapper;
import com.xibin.wms.pojo.BdArea;
import com.xibin.wms.service.BdAreaService;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
@Transactional(propagation = Propagation.REQUIRED)
@Service
public class FiBookServiceImpl extends BaseManagerImpl implements FiBookService {
	@Autowired
	private HttpSession session;
	@Autowired
	private FiBookMapper fibookMapper;
	@Override
	public FiBook getBookById(int id) {
		// TODO Auto-generated method stub
		return fibookMapper.selectByPrimaryKey(id);
	}
	@Override
	public List<FiBook> getAllBookByPage(Map map) {
		// TODO Auto-generated method stub
		return fibookMapper.selectAllByPage(map);
	}

	@Override
	public int removeBook(int id) throws BusinessException{
		// TODO Auto-generated method stub
		int []ids = {id};
		return this.delete(ids);
	}

	@Override
	public FiBook saveBook(FiBook model) throws BusinessException{
		// TODO Auto-generated method stub
		UserDetails userDetails = (UserDetails)session.getAttribute(Constants.SESSION_USER_KEY);
		model.setCompanyId(userDetails.getCompanyId());
		if(null == model.getId()||model.getId() == 0){
			model.setBookCode(CodeGenerator.getCodeByCurrentTimeAndRandomNum("BK"));
		}
		this.save(model);
		Map map = new HashMap<>();
		map.put("bookCode", model.getBookCode());
		List<FiBook> saveResult = this.selectByKey(map);
		if(saveResult.size()>0){
			return saveResult.get(0);
		}
		return null;
	}

	

	@Override
	protected BaseMapper getMapper() {
		// TODO Auto-generated method stub
		return fibookMapper;
	}

	private boolean deleteBeforeCheck(String areaCode){
		return true;
	}
	@Override
	public List<FiBook> findByExample(FiBook example) {
		// TODO Auto-generated method stub
		return fibookMapper.selectByExample(example);
	}
	@Override
	public List<FiBook> selectByKey(Map map) {
		// TODO Auto-generated method stub
		UserDetails userDetails = (UserDetails)session.getAttribute(Constants.SESSION_USER_KEY);
		map.put("companyId", userDetails.getCompanyId());
		return fibookMapper.selectByKey(map);
	}
}
