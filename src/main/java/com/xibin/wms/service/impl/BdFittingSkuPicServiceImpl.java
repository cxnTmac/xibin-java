package com.xibin.wms.service.impl;

import java.io.File;
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
import com.xibin.core.security.pojo.UserDetails;
import com.xibin.wms.dao.BdFittingSkuPicMapper;
import com.xibin.wms.pojo.BdFittingSkuPic;
import com.xibin.wms.service.BdFittingSkuPicService;
@Service
public class BdFittingSkuPicServiceImpl extends BaseManagerImpl implements BdFittingSkuPicService {
	@Autowired
	HttpSession session;
	
	@Resource
	private BdFittingSkuPicMapper bdFittingSkuPicMapper;
	@Override
	public BdFittingSkuPic getFittingSkuPicById(int id) {
		// TODO Auto-generated method stub
		return bdFittingSkuPicMapper.selectByPrimaryKey(id);
	}


	@Override
	public List<BdFittingSkuPic> getAllFittingSkuPicByPage(Map map) {
		// TODO Auto-generated method stub
		return bdFittingSkuPicMapper.selectAllByPage(map);
	}

	@Override
	public int removeFittingSkuPic(int idNormal,int idZip){
		// TODO Auto-generated method stub
		BdFittingSkuPic normal = bdFittingSkuPicMapper.selectByPrimaryKey(idNormal);
		BdFittingSkuPic zip = bdFittingSkuPicMapper.selectByPrimaryKey(idZip);
		String normalPath = Constants.WEBPICUPLOADURL+"\\"+normal.getFittingSkuPicUrl();
		String zipPath = Constants.WEBPICUPLOADURL+"\\"+zip.getFittingSkuPicUrl();
		File normalfile = new File(normalPath);  
	    // 路径为文件且不为空则进行删除  
	    if (normalfile.isFile() && normalfile.exists()) {  
	    	normalfile.delete();
	    }
	    File zipfile = new File(zipPath);  
	    // 路径为文件且不为空则进行删除  
	    if (zipfile.isFile() && zipfile.exists()) {  
	    	zipfile.delete();
	    }
	    int []ids = {idNormal,idZip};
	    int result = this.delete(ids);  
		return result;
	}
	
	

	@Override
	public BdFittingSkuPic saveFittingSkuPic(BdFittingSkuPic model){
		// TODO Auto-generated method stub
		UserDetails userDetails = (UserDetails)session.getAttribute(Constants.SESSION_USER_KEY);
		model.setCompanyId(userDetails.getCompanyId());
		return (BdFittingSkuPic) this.save(model);
	}


	@Override
	public List<BdFittingSkuPic> selectByFittingSkuCode(String fittingSkuCode) {
		// TODO Auto-generated method stub
		UserDetails userDetails = (UserDetails)session.getAttribute(Constants.SESSION_USER_KEY);
		return bdFittingSkuPicMapper.selectByFittingSkuCode(fittingSkuCode,userDetails.getCompanyId().toString());
	}

	@Override
	protected BaseMapper getMapper() {
		// TODO Auto-generated method stub
		return bdFittingSkuPicMapper;
	}
	
}
