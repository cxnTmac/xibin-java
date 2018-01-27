package com.xibin.wms.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jmx.export.naming.IdentityNamingStrategy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.xibin.core.costants.Constants;
import com.xibin.core.daosupport.BaseManagerImpl;
import com.xibin.core.daosupport.BaseMapper;
import com.xibin.core.exception.BusinessException;
import com.xibin.core.pojo.Message;
import com.xibin.core.security.pojo.UserDetails;
import com.xibin.core.utils.CodeGenerator;
import com.xibin.wms.constants.WmsCodeMaster;
import com.xibin.wms.dao.WmInboundDetailMapper;
import com.xibin.wms.dao.WmInboundHeaderMapper;
import com.xibin.wms.dao.WmInboundRecieveMapper;
import com.xibin.wms.entity.InventoryUpdateEntity;
import com.xibin.wms.pojo.WmInboundDetail;
import com.xibin.wms.pojo.WmInboundHeader;
import com.xibin.wms.pojo.WmInboundRecieve;
import com.xibin.wms.query.WmInboundDetailQueryItem;
import com.xibin.wms.query.WmInboundHeaderQueryItem;
import com.xibin.wms.query.WmInboundRecieveQueryItem;
import com.xibin.wms.service.WmInboundDetailService;
import com.xibin.wms.service.WmInboundHeaderService;
import com.xibin.wms.service.WmInboundReceiveService;
import com.xibin.wms.service.WmInboundUpdateService;
import com.xibin.wms.service.WmInventoryService;
@Transactional(propagation = Propagation.REQUIRED)
@Service
public class WmInboundReceiveServiceImpl  extends BaseManagerImpl implements WmInboundReceiveService {
	@Autowired
	private HttpSession session;
	@Resource
	private WmInboundRecieveMapper wmInboundReceiveMapper;
	@Autowired
	private WmInventoryService wmInventoryService;
	@Autowired
	private WmInboundUpdateService wmInboundUpdateService;
	@Autowired
	private WmInboundHeaderService wmInboundHeaderService;
	@Override
	public List<WmInboundRecieve> getInboundRecById(int []ids) {
		// TODO Auto-generated method stub
		StringBuffer stringBuffer = new StringBuffer();
		for(int i = 0; i<ids.length;i++){
			if(i!=ids.length-1){
				stringBuffer.append(ids[i]+",");
			}else{
				stringBuffer.append(ids[i]+"");
			}
		}
		return wmInboundReceiveMapper.selectByPrimaryKey(stringBuffer.toString());
	}

	@Override
	public List<WmInboundRecieveQueryItem> getAllInboundRecByPage(Map map) {
		// TODO Auto-generated method stub
		return wmInboundReceiveMapper.selectAllByPage(map);
	}

	@Override
	public List<WmInboundRecieveQueryItem> selectByKey(String orderNo,String lineNo) {
		// TODO Auto-generated method stub
		UserDetails userDetails = (UserDetails)session.getAttribute(Constants.SESSION_USER_KEY);
		return wmInboundReceiveMapper.selectByKey(orderNo,lineNo,userDetails.getCompanyId().toString(),userDetails.getWarehouseId().toString());
	}

	@Override
	protected BaseMapper getMapper() {
		// TODO Auto-generated method stub
		return wmInboundReceiveMapper;
	}

	@Override
	public WmInboundRecieve saveInboundRec(WmInboundRecieve model){
		// TODO Auto-generated method stub
		UserDetails userDetails = (UserDetails)session.getAttribute(Constants.SESSION_USER_KEY);
		model.setCompanyId(userDetails.getCompanyId());
		model.setWarehouseId(userDetails.getWarehouseId());
		Integer num = 0;
		if(null ==model.getId()||0==model.getId()){
			List<Integer> lastLineNo = selectLastRecLineNo(model.getOrderNo(),model.getLineNo(),model.getCompanyId().toString(),model.getWarehouseId().toString());
				num = lastLineNo.get(0);
				if(num == null){
					model.setRecLineNo("1");
				}else{
					num++;
					model.setRecLineNo(num+"");
				}
		}
		return (WmInboundRecieve)this.save(model);
	}

	private  List<Integer> selectLastRecLineNo(String orderNo,String lineNo,String companyId,String warehouseId) {
		// TODO Auto-generated method stub
		return wmInboundReceiveMapper.selectLastRecLineNo(orderNo, lineNo, companyId, warehouseId);
	}

	@Override
	public Message receiveByRecieveIds(int[] detailRecIds){
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Message receiveByRecieve(WmInboundRecieve wmInboundRecieve) throws BusinessException{
		// TODO Auto-generated method stub	
		Message message = new Message();
		Message checkResult = checkOrderBeforeReceiveAndCancel(wmInboundRecieve.getOrderNo());
		if(checkResult.getCode()==0){
			return checkResult;
		}
		updateForReceive(wmInboundRecieve);
		wmInboundRecieve.setStatus(WmsCodeMaster.INB_FULL_RECEIVING.getCode());
		wmInboundRecieve.setRecTime(new Date());
		//如果实收数小于预收数，拆分出一条新的收货明细
		if(wmInboundRecieve.getInboundPreNum()>wmInboundRecieve.getInboundNum()){
			//预收数大于实收数，拆分成两条收货明细
			WmInboundRecieve newInboundRecieve  = new WmInboundRecieve();
			newInboundRecieve.setCompanyId(wmInboundRecieve.getCompanyId());
			newInboundRecieve.setWarehouseId(wmInboundRecieve.getWarehouseId());
			newInboundRecieve.setInboundLocCode(wmInboundRecieve.getInboundLocCode());
			newInboundRecieve.setInboundNum(0.0);
			newInboundRecieve.setInboundPreNum(wmInboundRecieve.getInboundPreNum()-wmInboundRecieve.getInboundNum());
			newInboundRecieve.setInboundPrice(wmInboundRecieve.getInboundPrice());
			newInboundRecieve.setLineNo(wmInboundRecieve.getLineNo());
			newInboundRecieve.setOrderNo(wmInboundRecieve.getOrderNo());
			newInboundRecieve.setPlanLoc(wmInboundRecieve.getPlanLoc());
			newInboundRecieve.setStatus(WmsCodeMaster.INB_NEW.getCode());
			newInboundRecieve.setSkuCode(wmInboundRecieve.getSkuCode());
			newInboundRecieve.setSupplierCode(wmInboundRecieve.getSupplierCode());
			saveInboundRec(newInboundRecieve);
		}
		wmInboundRecieve.setInboundPreNum(wmInboundRecieve.getInboundNum());
		//更新收货明细行状态
		saveInboundRec(wmInboundRecieve);
		//更新入库单及明细状态
		wmInboundUpdateService.updataInboundStatusByInboundReceive(wmInboundRecieve.getOrderNo());
		message.setMsg("操作成功");
		message.setCode(200);
		return message;
	}
	@Override
	public Message cancelReceiveByRecieve(WmInboundRecieve wmInboundRecieve) {
		// TODO Auto-generated method stub
		UserDetails userDetails = (UserDetails)session.getAttribute(Constants.SESSION_USER_KEY);
		Message message = new Message();
		Message checkResult = checkOrderBeforeReceiveAndCancel(wmInboundRecieve.getOrderNo());
		if(checkResult.getCode()==0){
			return checkResult;
		}
		//更新库存
		try {
			updateForCancelReceive(wmInboundRecieve);
		} catch (BusinessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			message.setMsg(e.getMessage());
			message.setCode(0);
			return message;
		}
		//取消收货前，查找同商品明细行号的创建状态的收货明细，将数量合并到同一条，删除多余的
		WmInboundRecieve queryExample = new WmInboundRecieve();
		queryExample.setOrderNo(wmInboundRecieve.getOrderNo());
		queryExample.setLineNo(wmInboundRecieve.getLineNo());
		queryExample.setCompanyId(userDetails.getCompanyId());
		queryExample.setWarehouseId(userDetails.getWarehouseId());
		queryExample.setStatus(WmsCodeMaster.INB_NEW.getCode());
		List<WmInboundRecieve> queryResults = this.selectByExample(queryExample);
		List<Integer> ids = new ArrayList<Integer>();
		Double sumOfInboundPreNum = wmInboundRecieve.getInboundPreNum();
		if(queryResults.size()>0){
			for(WmInboundRecieve queryResult:queryResults){
				sumOfInboundPreNum +=queryResult.getInboundPreNum();
				//其他多余的收货行号
				if(!queryResult.getRecLineNo().equals(wmInboundRecieve.getRecLineNo())){
					ids.add(queryResult.getId());
				}
			}
			//删除多余收货明细行
			this.delete(idListToArray(ids));
		}
		wmInboundRecieve.setInboundNum(0.0);
		wmInboundRecieve.setInboundPreNum(sumOfInboundPreNum);
		wmInboundRecieve.setStatus(WmsCodeMaster.INB_NEW.getCode());
		wmInboundRecieve.setRecTime(null);
		//更新收货明细行状态
		saveInboundRec(wmInboundRecieve);
		//更新入库单及明细状态
		try {
			wmInboundUpdateService.updataInboundStatusByInboundReceive(wmInboundRecieve.getOrderNo());
		} catch (BusinessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			message.setMsg(e.getMessage());
			message.setCode(0);
			return message;
		}
		message.setMsg("操作成功");
		message.setCode(200);
		return message;
	}
	
	private int [] idListToArray(List<Integer> ids){
		int []  idArray = new int  [ids.size()];
		for(int i =0;i<ids.size();i++){
			idArray[i] = ids.get(i);
		}
		return idArray;
	}
	private Message checkOrderBeforeReceiveAndCancel(String orderNo){
		Message message = new Message();
		List<WmInboundHeaderQueryItem> queryItems = wmInboundHeaderService.selectByKey(orderNo);
		if(queryItems.size()>0){
			WmInboundHeaderQueryItem queryItem = queryItems.get(0);
			if(WmsCodeMaster.AUDIT_NEW.getCode().equals(queryItem.getAuditStatus())){
				message.setMsg("入库单["+queryItem.getOrderNo()+"]未审核！");
				message.setCode(0);
			}
		}else{
			message.setMsg("入库单["+orderNo+"]不存在！");
			message.setCode(0);
		}
		message.setCode(200);
		return message;
	}
	//执行
	private void updateForReceive(WmInboundRecieve wmInboundRecieve) throws BusinessException{
		InventoryUpdateEntity inventoryUpdateEntity = new InventoryUpdateEntity();
		inventoryUpdateEntity.setActionCode(WmsCodeMaster.ACT_REC.getCode());
		inventoryUpdateEntity.setLineNo(wmInboundRecieve.getRecLineNo());
		inventoryUpdateEntity.setLocCode(wmInboundRecieve.getInboundLocCode());
		inventoryUpdateEntity.setOrderNo(wmInboundRecieve.getOrderNo());
		inventoryUpdateEntity.setOrderType(WmsCodeMaster.ORDER_INB.getCode());
		inventoryUpdateEntity.setQtyOp(wmInboundRecieve.getInboundNum());
		inventoryUpdateEntity.setSkuCode(wmInboundRecieve.getSkuCode());
		inventoryUpdateEntity.setPrice(wmInboundRecieve.getInboundPrice());
		wmInventoryService.updateInventory(inventoryUpdateEntity);
	}
	private void updateForCancelReceive(WmInboundRecieve wmInboundRecieve) throws BusinessException{
		InventoryUpdateEntity inventoryUpdateEntity = new InventoryUpdateEntity();
		inventoryUpdateEntity.setActionCode(WmsCodeMaster.ACT_CANCEL_REC.getCode());
		inventoryUpdateEntity.setLineNo(wmInboundRecieve.getRecLineNo());
		inventoryUpdateEntity.setLocCode(wmInboundRecieve.getInboundLocCode());
		inventoryUpdateEntity.setOrderNo(wmInboundRecieve.getOrderNo());
		inventoryUpdateEntity.setOrderType(WmsCodeMaster.ORDER_INB.getCode());
		inventoryUpdateEntity.setQtyOp(wmInboundRecieve.getInboundNum());
		inventoryUpdateEntity.setSkuCode(wmInboundRecieve.getSkuCode());
		inventoryUpdateEntity.setPrice(wmInboundRecieve.getInboundPrice());
		wmInventoryService.updateInventory(inventoryUpdateEntity);
	}	

	@Override
	public List<WmInboundRecieve> selectByExample(WmInboundRecieve model) {
		// TODO Auto-generated method stub
		return wmInboundReceiveMapper.selectByExample(model);
	}

	

}
