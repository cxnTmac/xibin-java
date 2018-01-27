package com.xibin.wms.service.impl;

import java.io.File;

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
import com.xibin.core.security.pojo.UserDetails;
import com.xibin.core.utils.CustomizedPropertyConfigurer;
import com.xibin.wms.dao.BdFittingSkuPicMapper;
import com.xibin.wms.pojo.BdFittingSkuPic;
import com.xibin.wms.service.BdFittingSkuPicService;
@Transactional(propagation = Propagation.REQUIRED)
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
		String webPicUploadUrl = (String) CustomizedPropertyConfigurer.getContextProperty("webPicUploadUrl");
		String normalPath = webPicUploadUrl+"\\"+normal.getFittingSkuPicUrl().substring(4, normal.getFittingSkuPicUrl().length());
		String zipPath = webPicUploadUrl+"\\"+zip.getFittingSkuPicUrl().substring(4, zip.getFittingSkuPicUrl().length());
		File normalfile = new File(normalPath);  
	    // 路径为文件且不为空则进行删除  
	    if (normalfile.isFile() && normalfile.exists()) {
	    	//如果文件删除失败，则需要进行GC，防止JAVA占用文件无法进行删除
	    	if(!normalfile.delete()){
	    		System.gc();
	    		normalfile.delete();
	    	}
	    }
	    File zipfile = new File(zipPath);  
	    // 路径为文件且不为空则进行删除  
	    if (zipfile.isFile() && zipfile.exists()) {  
	    	//如果文件删除失败，则需要进行GC，防止JAVA占用文件无法进行删除
	    	if(!zipfile.delete()){
	    		System.gc();
	    		zipfile.delete();
	    	}
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
	public List<BdFittingSkuPic> selectByFittingSkuCode(String fittingSkuCode,String companyId) {
		// TODO Auto-generated method stub
		return bdFittingSkuPicMapper.selectByFittingSkuCode(fittingSkuCode,companyId);
	}

	@Override
	protected BaseMapper getMapper() {
		// TODO Auto-generated method stub
		return bdFittingSkuPicMapper;
	}
	
}
