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
import com.xibin.wms.dao.BdCustomerMapper;
import com.xibin.wms.pojo.BdCustomer;
import com.xibin.wms.query.BdCustomerQueryItem;
import com.xibin.wms.service.BdCustomerService;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
@Transactional(propagation = Propagation.REQUIRED)
@Service
public class BdCustomerServiceImpl extends BaseManagerImpl implements BdCustomerService {
	@Autowired
	private HttpSession session;
	@Autowired
	private BdCustomerMapper bdCustomerMapper;
	@Override
	public BdCustomer getCustomerById(int id) {
		// TODO Auto-generated method stub
		return bdCustomerMapper.selectByPrimaryKey(id);
	}
	@Override
	public List<BdCustomerQueryItem> getAllCustomerByPage(Map map) {
		// TODO Auto-generated method stub
		return bdCustomerMapper.selectAllByPage(map);
	}

	@Override
	public int removeCustomer(int id, String customerCode) throws BusinessException{
		// TODO Auto-generated method stub
		if(!deleteBeforeCheck(customerCode)){
			throw new BusinessException("该客户编码["+customerCode+"]已被库区引用，不能删除");
		}else{
			int []ids = {id};
			return this.delete(ids);
		}
	}

	@Override
	public BdCustomer saveCustomer(BdCustomer model) throws BusinessException{
		// TODO Auto-generated method stub
		UserDetails userDetails = (UserDetails)session.getAttribute(Constants.SESSION_USER_KEY);
		model.setCompanyId(userDetails.getCompanyId());
		List<BdCustomerQueryItem> list = bdCustomerMapper.selectByKey(model.getCustomerCode(),model.getCompanyId().toString());
		if(list.size()>0&&model.getId()==0){
			throw new BusinessException("编码：["+model.getCustomerCode()+"] 已存在，不能重复！");
		}
		return (BdCustomer) this.save(model);
	}

	@Override
	public List<BdCustomerQueryItem> selectByKey(String customerCode) {
		// TODO Auto-generated method stub
		UserDetails userDetails = (UserDetails)session.getAttribute(Constants.SESSION_USER_KEY);
		return bdCustomerMapper.selectByKey(customerCode,userDetails.getCompanyId().toString());
	}

	@Override
	protected BaseMapper getMapper() {
		// TODO Auto-generated method stub
		return bdCustomerMapper;
	}

	private boolean deleteBeforeCheck(String areaCode){
		return true;
	}
}