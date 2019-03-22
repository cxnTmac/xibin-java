package com.xibin.wms.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.xibin.core.costants.Constants;
import com.xibin.core.daosupport.BaseManagerImpl;
import com.xibin.core.daosupport.BaseMapper;
import com.xibin.core.exception.BusinessException;
import com.xibin.core.pojo.Message;
import com.xibin.core.security.pojo.UserDetails;
import com.xibin.core.utils.ReadExcelTools;
import com.xibin.wms.dao.BdSkuAnotherNameMapper;
import com.xibin.wms.pojo.BdSkuAnotherName;
import com.xibin.wms.service.BdSkuAnotherNameService;

@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
@Service
public class BdSkuAnotherNameServiceImpl extends BaseManagerImpl implements BdSkuAnotherNameService {
	@Autowired
	private HttpSession session;
	@Autowired
	private BdSkuAnotherNameMapper bdSkuAnotherNameMapper;

	@Override
	public BdSkuAnotherName getAnotherNameById(int id) {
		// TODO Auto-generated method stub
		return bdSkuAnotherNameMapper.selectByPrimaryKey(id);
	}

	@Override
	public List<BdSkuAnotherName> getAllAnotherNameByPage(Map map) {
		// TODO Auto-generated method stub
		return bdSkuAnotherNameMapper.selectAllByPage(map);
	}

	@Override
	public int removeAnotherName(int id, String fittingSkuCode, String customerCode) throws BusinessException {
		// TODO Auto-generated method stub
		if (!deleteBeforeCheck(fittingSkuCode, customerCode)) {
			throw new BusinessException("不能删除");
		} else {
			int[] ids = { id };
			return this.delete(ids);
		}
	}

	@Override
	public BdSkuAnotherName saveAnotherName(BdSkuAnotherName model) throws BusinessException {
		// TODO Auto-generated method stub
		UserDetails userDetails = (UserDetails) session.getAttribute(Constants.SESSION_USER_KEY);
		model.setCompanyId(userDetails.getCompanyId());
		model.setWarehouseId(userDetails.getWarehouseId());
		List<BdSkuAnotherName> list = bdSkuAnotherNameMapper.selectByKey(model.getFittingSkuCode(),
				model.getCustomerCode(), model.getCompanyId().toString());
		if (list.size() > 0 && model.getId() == 0) {
			throw new BusinessException(
					"编码：[" + model.getFittingSkuCode() + "] 已存在对于客户:[" + model.getCustomerCode() + "]的别名，不能重复！");
		}
		return (BdSkuAnotherName) this.save(model);
	}

	@Override
	public List<BdSkuAnotherName> selectByKey(String fittingSkuCode, String customerCode) {
		// TODO Auto-generated method stub
		UserDetails userDetails = (UserDetails) session.getAttribute(Constants.SESSION_USER_KEY);
		return bdSkuAnotherNameMapper.selectByKey(fittingSkuCode, customerCode, userDetails.getCompanyId().toString());
	}

	@Override
	public List<BdSkuAnotherName> selectByExample(BdSkuAnotherName model) {
		// TODO Auto-generated method stub
		return bdSkuAnotherNameMapper.selectByExample(model);
	}

	@Override
	public Message importByExcel(MultipartFile file, String customerCode, String skuCodeColumnName)
			throws BusinessException {
		Message message = new Message();
		try {
			UserDetails userDetails = (UserDetails) session.getAttribute(Constants.SESSION_USER_KEY);
			Message readMessge = ReadExcelTools.readExcelForGetFittingSkuCode(file, skuCodeColumnName);
			List<String> cutomerskuCodes = (List<String>) readMessge.getData();
			List<String> skuCodes = new ArrayList<String>();
			for (String cutomerskuCode : cutomerskuCodes) {
				if (!"NULL".equals(cutomerskuCode)) {
					BdSkuAnotherName queryExample = new BdSkuAnotherName();
					queryExample.setCompanyId(userDetails.getCompanyId());
					queryExample.setWarehouseId(userDetails.getWarehouseId());
					queryExample.setCustomerCode(customerCode);
					queryExample.setFittingSkuAnotherName(cutomerskuCode);
					List<BdSkuAnotherName> results = this.selectByExample(queryExample);
					if (results.size() > 0) {
						skuCodes.add(results.get(0).getFittingSkuCode());
					} else {
						skuCodes.add("");
					}
				} else {
					skuCodes.add("");
				}
			}
			message.setCode(200);
			message.setData(skuCodes);
			message.setMsg(readMessge.getMsg());
			return message;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			message.setCode(0);
			message.setMsg(e.getMessage());
			return message;
		} catch (BusinessException e) {
			e.printStackTrace();
			message.setCode(0);
			message.setMsg(e.getMessage());
			return message;
		}
	}

	@Override
	public Message importByExcelForSave(MultipartFile file, String customerCode, String skuCodeColumnName,
			String customerSkuCodeColumnName, String remark) throws BusinessException {
		Message message = new Message();
		try {
			UserDetails userDetails = (UserDetails) session.getAttribute(Constants.SESSION_USER_KEY);
			Message readMessge = ReadExcelTools.readExcelForGetCustomerSkuCode(file, customerSkuCodeColumnName,
					skuCodeColumnName);
			StringBuffer errors = new StringBuffer();
			List<String> strs = (List<String>) readMessge.getData();
			for (String str : strs) {
				if (!"NULL".equals(str)) {
					String skuCode = str.split("\\|")[0];
					String cutomerskuCode = str.split("\\|")[1];
					BdSkuAnotherName queryExample = new BdSkuAnotherName();
					queryExample.setCompanyId(userDetails.getCompanyId());
					queryExample.setWarehouseId(userDetails.getWarehouseId());
					queryExample.setCustomerCode(customerCode);
					queryExample.setFittingSkuAnotherName(cutomerskuCode);
					List<BdSkuAnotherName> results = this.selectByExample(queryExample);
					if (results.size() > 0) {
						if (!results.get(0).getFittingSkuCode().equals(skuCode)) {
							errors.append("客户编码[" + cutomerskuCode + "]已存在对应的产品编码[" + results.get(0).getFittingSkuCode()
									+ "]，与文件中的产品编码[" + skuCode + "]不同，请检查数据！" + "\n");
						}
					} else {
						BdSkuAnotherName model = new BdSkuAnotherName();
						model.setCompanyId(userDetails.getCompanyId());
						model.setWarehouseId(userDetails.getWarehouseId());
						model.setCustomerCode(customerCode);
						model.setFittingSkuAnotherName(cutomerskuCode);
						model.setFittingSkuCode(skuCode);
						model.setRemark(remark);
						this.save(model);
					}
				} else {
				}
			}
			message.setCode(200);
			message.setMsg(readMessge.getMsg() + errors.toString());
			return message;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			message.setCode(0);
			message.setMsg(e.getMessage());
			return message;
		} catch (BusinessException e) {
			e.printStackTrace();
			message.setCode(0);
			message.setMsg(e.getMessage());
			return message;
		}
	}

	@Override
	protected BaseMapper getMapper() {
		// TODO Auto-generated method stub
		return bdSkuAnotherNameMapper;
	}

	private boolean deleteBeforeCheck(String fittingSkuCode, String customerCode) {
		return true;
	}
}
