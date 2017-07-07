package com.xibin.core.daosupport;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;

import com.xibin.core.costants.Constants;
import com.xibin.core.exception.DaoException;
import com.xibin.core.security.pojo.UserDetails;

public class DaoUtil {
	public static BaseModel save(BaseModel baseModel,BaseMapper mapper,HttpSession session) {
		Collection models = new ArrayList<BaseModel>();
		models.add(baseModel);
		models = save(models,mapper,session);
		if (null != models || models.size() > 0) {
			for (Object model : models) {
				return (BaseModel) model;
			}
		}
		return null;
	}

	
	public static Collection save(Collection models,BaseMapper mapper,HttpSession session) {
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
			if (mapper.insert(insertModels) != insertModels.size()) {
				throw new DaoException("Insert failed!");
			}
		}
		if (null != updateModels && updateModels.size() > 0) {
			if (mapper.update(updateModels) != updateModels.size()) {
				throw new DaoException("Update failed,May be data has expired!");
			}
		}
		insertModels.addAll(updateModels);
		return insertModels;
	}
	
	public static int delete(int id,BaseMapper mapper) {
		int[] ids = { id };
		return delete(ids,mapper);
	}

	public  static int delete(int[] ids,BaseMapper mapper) {
		return mapper.delete(ids);
	}
}
