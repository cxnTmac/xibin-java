/**
 * 
 */
package com.xibin.core.daosupport;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import javax.jws.soap.SOAPBinding.Use;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;

import com.xibin.core.costants.Constants;
import com.xibin.core.exception.DaoException;
import com.xibin.core.security.pojo.UserDetails;

/**
 * @Description:
 * @author:Vincent.Zhang
 * 
 * 						<pre>
 * 版本号					修改人					修改说明
 * 2015年9月26日 .1			Vincent.Zhang    	新增
 *                       </pre>
 */

public abstract class BaseManagerImpl {

	protected abstract BaseMapper getMapper();
	
	@Autowired  
	private HttpSession session;

	public BaseModel save(BaseModel baseModel) {
		Collection models = new ArrayList<BaseModel>();
		models.add(baseModel);
		models = save(models);
		if (null != models || models.size() > 0) {
			for (Object model : models) {
				return (BaseModel) model;
			}
		}
		return null;
	}

	
	public Collection save(Collection models) {
		if (null == models || models.size() <= 0) {
			throw new DaoException("Save failed,Models is null!");
		}
		Collection<BaseModel> insertModels = new ArrayList<BaseModel>();
		Collection<BaseModel> updateModels = new ArrayList<BaseModel>();
		Date now = new Date();
		// 取登录用户session进行赋值
		UserDetails userDetails = (UserDetails)session.getAttribute(Constants.SESSION_USER_KEY);
		int userId = userDetails.getUserId();
		for (Object object : models) {
			BaseModel baseModel = (BaseModel) object;
			Integer id = baseModel.getId();
			baseModel.setModifier(userId);
			baseModel.setModifyTime(now);
			if (id==null||id==0) {
				baseModel.setCreator(userId);
				baseModel.setCreateTime(now);
				baseModel.setRecVer(1);
				insertModels.add(baseModel);
			} else {
				if (null == baseModel.getRecVer()) {
					throw new DaoException(
							"Update failed,Does not define RecordVersion values!");
				}
				baseModel.setRecVer(baseModel.getRecVer() + 1);
				updateModels.add(baseModel);
			}
		}
		if (null != insertModels && insertModels.size() > 0) {
			if (getMapper().insert(insertModels) != insertModels.size()) {
				throw new DaoException("Insert failed!");
			}
		}
		if (null != updateModels && updateModels.size() > 0) {
			if (getMapper().update(updateModels) != updateModels.size()) {
				throw new DaoException("Update failed,May be data has expired!");
			}
		}
		insertModels.addAll(updateModels);
		return insertModels;
	}

	public int delete(int id) {
		int[] ids = { id };
		return delete(ids);
	}

	public int delete(int[] ids) {
		return getMapper().delete(ids);
	}

	public BaseModel getById(String id) {
		String[] ids = { id };
		Collection<BaseModel> models = getById(ids);
		if (null != models || models.size() > 0) {
			for (BaseModel model : models) {
				return model;
			}
		}
		return null;
	}

	public Collection getById(String[] ids) {
		return getMapper().getById(ids);
	}

}
