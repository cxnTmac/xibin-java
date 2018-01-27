package com.xibin.wms.service.impl;

import java.security.KeyStore.PrivateKeyEntry;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.xibin.core.costants.Constants;
import com.xibin.core.daosupport.BaseManagerImpl;
import com.xibin.core.daosupport.BaseMapper;
import com.xibin.core.daosupport.DaoUtil;
import com.xibin.core.exception.BusinessException;
import com.xibin.core.pojo.Message;
import com.xibin.core.security.pojo.UserDetails;
import com.xibin.core.utils.CodeGenerator;
import com.xibin.wms.constants.WmsCodeMaster;
import com.xibin.wms.dao.WmInboundDetailMapper;
import com.xibin.wms.dao.WmInboundHeaderMapper;
import com.xibin.wms.dao.WmInboundRecieveMapper;
import com.xibin.wms.pojo.WmInboundDetail;
import com.xibin.wms.pojo.WmInboundHeader;
import com.xibin.wms.pojo.WmInboundRecieve;
import com.xibin.wms.query.WmInboundDetailQueryItem;
import com.xibin.wms.query.WmInboundHeaderQueryItem;
import com.xibin.wms.query.WmInboundRecieveQueryItem;
import com.xibin.wms.query.WmOutboundDetailQueryItem;
import com.xibin.wms.service.WmInboundDetailService;
import com.xibin.wms.service.WmInboundHeaderService;
import com.xibin.wms.service.WmInboundReceiveService;
@Transactional(propagation = Propagation.REQUIRED)
@Service
public class WmInboundDetailServiceImpl  extends BaseManagerImpl implements WmInboundDetailService {
	@Autowired
	private HttpSession session;
	@Autowired
	private WmInboundHeaderService wmInboundHeaderService;
	@Autowired
	private WmInboundReceiveService wmInboundReceiveService;
	@Resource
	private WmInboundDetailMapper wmInboundDetailMapper;
	@Resource
	private WmInboundRecieveMapper wmInboundRecieveMapper;
	@Override
	public WmInboundDetail getInboundDetailById(int id) {
		// TODO Auto-generated method stub
		return wmInboundDetailMapper.selectByPrimaryKey(id);
	}
	@Override
	public List<WmInboundDetail> getInboundDetailByIds(String []ids) {
		// TODO Auto-generated method stub
		return (List<WmInboundDetail>) this.getById(ids);
	}

	@Override
	public List<WmInboundDetailQueryItem> getAllInboundDetailByPage(Map map) {
		// TODO Auto-generated method stub
		return wmInboundDetailMapper.selectAllByPage(map);
	}
	@Override
	public List<WmInboundDetailQueryItem> selectClosedOrderDetail(Map map) {
		// TODO Auto-generated method stub
		return wmInboundDetailMapper.selectClosedOrderDetail(map);
	}
	@Override
	public List<WmInboundDetailQueryItem> selectByKey(String orderNo,String lineNo) {
		// TODO Auto-generated method stub
		UserDetails userDetails = (UserDetails)session.getAttribute(Constants.SESSION_USER_KEY);
		return wmInboundDetailMapper.selectByKey(orderNo,lineNo,userDetails.getCompanyId().toString(),userDetails.getWarehouseId().toString());
	}

	@Override
	protected BaseMapper getMapper() {
		// TODO Auto-generated method stub
		return wmInboundDetailMapper;
	}

	@Override
	public int removeInboundDetail(int []ids,String orderNo) throws BusinessException{
		// TODO Auto-generated method stub
		UserDetails userDetails = (UserDetails)session.getAttribute(Constants.SESSION_USER_KEY);
		Message message = operateBeforeCheck(orderNo);
		if(message.getCode()==0){
			throw new BusinessException(message.getMsg());
		}else{
			int deleteSum = 0;
			for(int id:ids){
				WmInboundDetail detail = getInboundDetailById(id);
				WmInboundRecieve receiveQueryExample = new WmInboundRecieve();
				receiveQueryExample.setOrderNo(orderNo);
				receiveQueryExample.setLineNo(detail.getLineNo());
				receiveQueryExample.setCompanyId(userDetails.getCompanyId());
				receiveQueryExample.setWarehouseId(userDetails.getWarehouseId());
				List<WmInboundRecieve> recieves = wmInboundReceiveService.selectByExample(receiveQueryExample);
				int []receiveIds = new int [recieves.size()];
				for(int i = 0;i<recieves.size();i++){
					receiveIds[i] = recieves.get(i).getId();
				}
				DaoUtil.delete(receiveIds, wmInboundRecieveMapper);
				deleteSum+=this.delete(id);
			}
			return deleteSum;
		}
	}
	private Message operateBeforeCheck(String orderNo){
		Message message = new Message();
		List<WmInboundHeaderQueryItem> headerList = wmInboundHeaderService.selectByKey(orderNo);
		
		if(headerList.size()>0){
			WmInboundHeaderQueryItem header = headerList.get(0);
			//已审核或不审核
			if(WmsCodeMaster.AUDIT_CLOSE.getCode().equals(header.getAuditStatus())){
				message.setCode(0);
				message.setMsg("入库单["+orderNo+"]已审核,不能对明细进行编辑");
				return message;
			}else if(!WmsCodeMaster.INB_NEW.getCode().equals(header.getStatus())){
				message.setCode(0);
				message.setMsg("入库单["+orderNo+"]不是创建状态,不能对明细进行编辑");
				return message;
			}else{
				message.setCode(200);
				return message;
			}
		}
		message.setCode(0);
		message.setMsg("入库单["+orderNo+"]数据丢失,请联系管理员");
		return message;
	}
	@Override
	public WmInboundDetail saveInboundDetail(WmInboundDetail model) throws BusinessException {
		Message message = operateBeforeCheck(model.getOrderNo());
		if(message.getCode()==0){
			throw new BusinessException(message.getMsg());
		}
		// TODO Auto-generated method stub
		UserDetails userDetails = (UserDetails)session.getAttribute(Constants.SESSION_USER_KEY);
		model.setCompanyId(userDetails.getCompanyId());
		model.setWarehouseId(userDetails.getWarehouseId());
		if(null ==model.getId()||0==model.getId()){
			List<Integer> lastLineNo = selectLastLineNo(model.getOrderNo(),model.getCompanyId().toString(),model.getWarehouseId().toString());
				Integer num = lastLineNo.get(0);
				if(num == null){
					model.setLineNo("1");
				}else{
					num++;
					model.setLineNo(num+"");
				}
		}
		WmInboundDetail saveResult = (WmInboundDetail) this.save(model);
		if(null ==model.getId()||0==model.getId()){
			WmInboundRecieve inboundRecieve = new WmInboundRecieve();
			inboundRecieve.setStatus(WmsCodeMaster.INB_NEW.getCode());
			inboundRecieve.setOrderNo(saveResult.getOrderNo());
			inboundRecieve.setLineNo(saveResult.getLineNo());
			inboundRecieve.setSupplierCode(saveResult.getSupplierCode());
			inboundRecieve.setSkuCode(saveResult.getSkuCode());
			inboundRecieve.setInboundPreNum(saveResult.getInboundPreNum());
			inboundRecieve.setInboundNum(0.0);
			inboundRecieve.setInboundPrice(saveResult.getInboundPrice());
			inboundRecieve.setPlanLoc(saveResult.getPlanLoc());
			inboundRecieve.setInboundLocCode(saveResult.getPlanLoc());
			wmInboundReceiveService.saveInboundRec(inboundRecieve);
		}else{
			List<WmInboundRecieveQueryItem> inboundRecieves = wmInboundReceiveService.selectByKey(saveResult.getOrderNo(), saveResult.getLineNo());
			for(int i =0;i<inboundRecieves.size();i++){
				WmInboundRecieve inboundRecieve = new WmInboundRecieve(); 
				BeanUtils.copyProperties(inboundRecieves.get(i), inboundRecieve);
				inboundRecieve.setStatus(WmsCodeMaster.INB_NEW.getCode());
				inboundRecieve.setSkuCode(saveResult.getSkuCode());
				inboundRecieve.setInboundPreNum(saveResult.getInboundPreNum());
				inboundRecieve.setInboundNum(0.0);
				inboundRecieve.setInboundPrice(saveResult.getInboundPrice());
				inboundRecieve.setPlanLoc(saveResult.getPlanLoc());
				inboundRecieve.setInboundLocCode(saveResult.getPlanLoc());
				wmInboundReceiveService.saveInboundRec(inboundRecieve);
			}
		}
		return saveResult;
	}

	private  List<Integer> selectLastLineNo(String orderNo,String companyId,String warehouseId) {
		// TODO Auto-generated method stub
		return wmInboundDetailMapper.selectLastLineNo(orderNo, companyId, warehouseId);
	}

	@Override
	public List<WmInboundDetail> selectByExample(WmInboundDetail model) {
		// TODO Auto-generated method stub
		return wmInboundDetailMapper.selectByExample(model);
	}

	@Override
	public int updateStatusByKey(String orderNo, String lineNos,String status) {
		// TODO Auto-generated method stub
		UserDetails userDetails = (UserDetails)session.getAttribute(Constants.SESSION_USER_KEY);
		return wmInboundDetailMapper.updateStatusByKey(orderNo, lineNos,status,userDetails.getCompanyId().toString(), userDetails.getWarehouseId().toString());
	}

}
