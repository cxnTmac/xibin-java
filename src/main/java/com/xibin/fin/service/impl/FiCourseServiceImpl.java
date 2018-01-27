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
import com.xibin.fin.dao.FiCourseMapper;
import com.xibin.fin.pojo.FiCourse;
import com.xibin.fin.service.FiCourseService;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
@Transactional(propagation = Propagation.REQUIRED)
@Service
public class FiCourseServiceImpl extends BaseManagerImpl implements FiCourseService {
	@Autowired
	private HttpSession session;
	@Autowired
	private FiCourseMapper fiCourseMapper;
	@Override
	public FiCourse getCourseById(int id) {
		// TODO Auto-generated method stub
		return fiCourseMapper.selectByPrimaryKey(id);
	}
	@Override
	public List<FiCourse> getAllCourseByPage(Map map) {
		// TODO Auto-generated method stub
		return fiCourseMapper.selectAllByPage(map);
	}

	@Override
	public int removeCourse(int id, String courseNo) throws BusinessException{
		// TODO Auto-generated method stub
		int []ids = {id};
		return this.delete(ids);
	}

	@Override
	public FiCourse saveCourse(FiCourse model) throws BusinessException{
		// TODO Auto-generated method stub
		UserDetails userDetails = (UserDetails)session.getAttribute(Constants.SESSION_USER_KEY);
		model.setCompanyId(userDetails.getCompanyId());
		List<FiCourse> list = fiCourseMapper.selectByKey(model.getCourseNo(),model.getCompanyId().toString());
		if(list.size()>0&&model.getId()==0){
			throw new BusinessException("编码：["+model.getCourseNo()+"] 已存在，不能重复！");
		}
		return (FiCourse) this.save(model);
	}

	@Override
	public List<FiCourse> selectByKey(String courseNo) {
		// TODO Auto-generated method stub
		UserDetails userDetails = (UserDetails)session.getAttribute(Constants.SESSION_USER_KEY);
		return fiCourseMapper.selectByKey(courseNo,userDetails.getCompanyId().toString());
	}

	@Override
	protected BaseMapper getMapper() {
		// TODO Auto-generated method stub
		return fiCourseMapper;
	}

	private boolean deleteBeforeCheck(String areaCode){
		return true;
	}
}
