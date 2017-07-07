package com.xibin.wms.service.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.xibin.core.costants.Constants;
import com.xibin.core.daosupport.BaseManagerImpl;
import com.xibin.core.daosupport.BaseMapper;
import com.xibin.core.exception.BusinessException;
import com.xibin.core.security.pojo.UserDetails;
import com.xibin.wms.dao.BdFittingSkuMapper;
import com.xibin.wms.pojo.BdFittingSku;
import com.xibin.wms.query.BdFittingSkuQueryItem;
import com.xibin.wms.service.BdFittingSkuService;
@Transactional(propagation = Propagation.REQUIRED)
@Service
public class BdFittingSkuServiceImpl extends BaseManagerImpl implements BdFittingSkuService {
	@Autowired
	HttpSession session;
	
	@Resource
	private BdFittingSkuMapper bdFittingSkuMapper;
	@Override
	public BdFittingSku getFittingSkuById(int id) {
		// TODO Auto-generated method stub
		return bdFittingSkuMapper.selectByPrimaryKey(id);
	}


	@Override
	public List<BdFittingSkuQueryItem> getAllFittingSkuByPage(Map map) {
		// TODO Auto-generated method stub
		return bdFittingSkuMapper.selectAllByPage(map);
	}
	
	@Override
	public List<BdFittingSkuQueryItem> MgetAllFittingSkuByPageWithOnePic(Map map) {
		// TODO Auto-generated method stub
		return bdFittingSkuMapper.selectALLByPageWithOnePic(map);
	}
	@Override
	public int removeFittingSku(int id,String fittingSkuCode) throws BusinessException {
		// TODO Auto-generated method stub
		if(!deleteBeforeCheck(fittingSkuCode)){
			throw new BusinessException("该产品编码["+fittingSkuCode+"]已被引用，不能删除");
		}else{
			int []ids = {id};
			return this.delete(ids);
		}
	}
	
	

	@Override
	public BdFittingSku saveFittingSku(BdFittingSku model) throws BusinessException{
		// TODO Auto-generated method stub
		UserDetails userDetails = (UserDetails)session.getAttribute(Constants.SESSION_USER_KEY);
		model.setCompanyId(userDetails.getCompanyId());
		List<BdFittingSkuQueryItem> list = bdFittingSkuMapper.selectByKey(model.getFittingSkuCode(),model.getCompanyId().toString());
		if(list.size()>0&&model.getId()==0){
			throw new BusinessException("编码：["+model.getFittingSkuCode()+"] 已存在，不能重复！");
		}
		return (BdFittingSku) this.save(model);
	}


	@Override
	public List<BdFittingSkuQueryItem> selectByKey(String skuCode) {
		// TODO Auto-generated method stub
		UserDetails userDetails = (UserDetails)session.getAttribute(Constants.SESSION_USER_KEY);
		return bdFittingSkuMapper.selectByKey(skuCode,userDetails.getCompanyId().toString());
	}

	@Override
	protected BaseMapper getMapper() {
		// TODO Auto-generated method stub
		return bdFittingSkuMapper;
	}
	
	private boolean deleteBeforeCheck(String fittingTypeCode){
		return true;
	}
}
