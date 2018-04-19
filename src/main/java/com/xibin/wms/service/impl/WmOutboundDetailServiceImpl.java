package com.xibin.wms.service.impl;

import java.util.Collections;
import java.util.Comparator;
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
import com.xibin.wms.constants.WmsCodeMaster;
import com.xibin.wms.dao.WmOutboundAllocMapper;
import com.xibin.wms.dao.WmOutboundDetailMapper;
import com.xibin.wms.entity.InventoryUpdateEntity;
import com.xibin.wms.pojo.WmActTran;
import com.xibin.wms.pojo.WmInventory;
import com.xibin.wms.pojo.WmOutboundAlloc;
import com.xibin.wms.pojo.WmOutboundDetail;
import com.xibin.wms.query.WmOutboundDetailPriceQueryItem;
import com.xibin.wms.query.WmOutboundDetailQueryItem;
import com.xibin.wms.query.WmOutboundDetailSaleHistoryQueryItem;
import com.xibin.wms.query.WmOutboundHeaderQueryItem;
import com.xibin.wms.service.WmInventoryService;
import com.xibin.wms.service.WmOutboundAllocService;
import com.xibin.wms.service.WmOutboundDetailService;
import com.xibin.wms.service.WmOutboundHeaderService;
import com.xibin.wms.service.WmOutboundUpdateService;

@Transactional(propagation = Propagation.REQUIRED)
@Service
public class WmOutboundDetailServiceImpl extends BaseManagerImpl implements WmOutboundDetailService {
	@Autowired
	private HttpSession session;
	@Autowired
	private WmOutboundHeaderService wmOutboundHeaderService;
	@Autowired
	private WmOutboundAllocService wmOutboundAllocService;
	@Autowired
	private WmOutboundUpdateService wmOutboundUpdateService;
	@Resource
	private WmOutboundDetailMapper wmOutboundDetailMapper;
	@Resource
	private WmOutboundAllocMapper wmOutboundAllocMapper;
	@Resource
	private WmInventoryService wmInventoryService;

	@Override
	public WmOutboundDetail getOutboundDetailById(int id) {
		// TODO Auto-generated method stub
		return wmOutboundDetailMapper.selectByPrimaryKey(id);
	}

	@Override
	public List<WmOutboundDetail> getInboundDetailByIds(String[] ids) {
		// TODO Auto-generated method stub
		return (List<WmOutboundDetail>) this.getById(ids);
	}

	@Override
	public List<WmOutboundDetailQueryItem> getAllOutboundDetailByPage(Map map) {
		// TODO Auto-generated method stub
		return wmOutboundDetailMapper.selectAllByPage(map);
	}

	@Override
	public List<WmOutboundDetailQueryItem> selectByKey(String orderNo, String lineNo) {
		// TODO Auto-generated method stub
		UserDetails userDetails = (UserDetails) session.getAttribute(Constants.SESSION_USER_KEY);
		return wmOutboundDetailMapper.selectByKey(orderNo, lineNo, userDetails.getCompanyId().toString(),
				userDetails.getWarehouseId().toString());
	}

	@Override
	protected BaseMapper getMapper() {
		// TODO Auto-generated method stub
		return wmOutboundDetailMapper;
	}

	@Override
	public int removeOutboundDetail(int[] ids, String orderNo) throws BusinessException {
		// TODO Auto-generated method stub
		Message message = operateBeforeCheck(orderNo);
		if (message.getCode() == 0) {
			throw new BusinessException(message.getMsg());
		} else {
			return this.delete(ids);
		}
	}

	private Message operateBeforeCheck(String orderNo) {
		Message message = new Message();
		List<WmOutboundHeaderQueryItem> headerList = wmOutboundHeaderService.selectByKey(orderNo);
		if (headerList.size() > 0) {
			WmOutboundHeaderQueryItem header = headerList.get(0);
			// 已审核或不审核
			if (WmsCodeMaster.AUDIT_CLOSE.getCode().equals(header.getAuditStatus())) {
				message.setCode(0);
				message.setMsg("出库单[" + orderNo + "]已审核,不能对明细进行编辑");
				return message;
			} else if (!WmsCodeMaster.INB_NEW.getCode().equals(header.getStatus())) {
				message.setCode(0);
				message.setMsg("出库单[" + orderNo + "]不是创建状态,不能对明细进行编辑");
				return message;
			} else {
				message.setCode(200);
				return message;
			}
		}
		message.setCode(0);
		message.setMsg("出库单[" + orderNo + "]数据丢失,请联系管理员");
		return message;
	}

	@Override
	public WmOutboundDetailQueryItem saveOutboundDetailWithOutCheck(WmOutboundDetail model) throws BusinessException {
		// TODO Auto-generated method stub
		UserDetails userDetails = (UserDetails) session.getAttribute(Constants.SESSION_USER_KEY);
		model.setCompanyId(userDetails.getCompanyId());
		model.setWarehouseId(userDetails.getWarehouseId());
		if (null == model.getId() || 0 == model.getId()) {
			List<Integer> lastLineNo = selectLastLineNo(model.getOrderNo(), model.getCompanyId().toString(),
					model.getWarehouseId().toString());
			Integer num = lastLineNo.get(0);
			if (num == null) {
				model.setLineNo("1");
			} else {
				num++;
				model.setLineNo(num + "");
			}
		}
		this.save(model);
		return this.selectByKey(model.getOrderNo(), model.getLineNo()).get(0);
	}

	@Override
	public WmOutboundDetailQueryItem saveOutboundDetail(WmOutboundDetail model) throws BusinessException {
		// int i = 1 / 0;
		Message message = operateBeforeCheck(model.getOrderNo());
		if (message.getCode() == 0) {
			throw new BusinessException(message.getMsg());
		}
		// TODO Auto-generated method stub
		UserDetails userDetails = (UserDetails) session.getAttribute(Constants.SESSION_USER_KEY);
		model.setCompanyId(userDetails.getCompanyId());
		model.setWarehouseId(userDetails.getWarehouseId());
		if (null == model.getId() || 0 == model.getId()) {
			List<Integer> lastLineNo = selectLastLineNo(model.getOrderNo(), model.getCompanyId().toString(),
					model.getWarehouseId().toString());
			Integer num = lastLineNo.get(0);
			if (num == null) {
				model.setLineNo("1");
			} else {
				num++;
				model.setLineNo(num + "");
			}
		}
		this.save(model);
		return this.selectByKey(model.getOrderNo(), model.getLineNo()).get(0);
	}

	private List<Integer> selectLastLineNo(String orderNo, String companyId, String warehouseId) {
		// TODO Auto-generated method stub
		return wmOutboundDetailMapper.selectLastLineNo(orderNo, companyId, warehouseId);
	}

	@Override
	public List<WmOutboundDetail> selectByExample(WmOutboundDetail model) {
		// TODO Auto-generated method stub
		return wmOutboundDetailMapper.selectByExample(model);
	}

	@Override
	public Message cancelAllocByKey(String orderNo, String lineNo) throws BusinessException {
		UserDetails userDetails = (UserDetails) session.getAttribute(Constants.SESSION_USER_KEY);
		Message message = new Message();
		WmOutboundDetail detailQueryExample = new WmOutboundDetail();
		detailQueryExample.setOrderNo(orderNo);
		detailQueryExample.setLineNo(lineNo);
		detailQueryExample.setCompanyId(userDetails.getCompanyId());
		detailQueryExample.setWarehouseId(userDetails.getWarehouseId());
		List<WmOutboundDetail> detailList = this.selectByExample(detailQueryExample);
		if (detailList.size() > 0) {
			WmOutboundDetail detail = detailList.get(0);
			if (!WmsCodeMaster.SO_FULL_ALLOC.equals(detail.getStatus())
					&& !WmsCodeMaster.SO_PART_ALLOC.equals(detail.getStatus())) {
				WmOutboundAlloc allocQueryExample = new WmOutboundAlloc();
				allocQueryExample.setOrderNo(orderNo);
				allocQueryExample.setLineNo(lineNo);
				allocQueryExample.setCompanyId(userDetails.getCompanyId());
				allocQueryExample.setWarehouseId(userDetails.getWarehouseId());
				List<WmOutboundAlloc> allocList = wmOutboundAllocService.selectByExample(allocQueryExample);
				double allocNumSum = 0.0;
				int[] allocIds = new int[allocList.size()];
				for (int i = 0; i < allocList.size(); i++) {
					allocNumSum += allocList.get(i).getOutboundNum();
					allocIds[i] = allocList.get(i).getId();
					// 更新库存
					InventoryUpdateEntity entity = new InventoryUpdateEntity();
					entity.setActionCode(WmsCodeMaster.ACT_CANCEL_ALLOC.getCode());
					entity.setLineNo(detail.getLineNo());
					entity.setLocCode(allocList.get(i).getAllocLocCode());
					entity.setOrderNo(orderNo);
					entity.setOrderType(WmsCodeMaster.ORDER_OUB.getCode());
					entity.setSkuCode(allocList.get(i).getSkuCode());
					entity.setQtyOp(allocList.get(i).getOutboundNum());
					entity.setPrice(allocList.get(i).getOutboundPrice());
					entity.setCost(allocList.get(i).getCost());

					// 更新库存
					wmInventoryService.updateInventory(entity);
				}
				DaoUtil.delete(allocIds, wmOutboundAllocMapper);
				detail.setOutboundAllocNum(0.0);
				detail.setStatus(WmsCodeMaster.SO_NEW.getCode());
				DaoUtil.save(detail, wmOutboundDetailMapper, session);
				// 根据出库单明细更新出库单的状态
				wmOutboundUpdateService.updataOutboundStatusByOutboundDetail(orderNo);
				message.setMsg("操作成功！");
				message.setCode(200);
				return message;
			} else {
				throw new BusinessException("出库单号[" + orderNo + "]行号[" + lineNo + "]的出库单明细不是完全分配或者部分分配状态");
			}
		} else {
			throw new BusinessException("出库单号[" + orderNo + "]行号[" + lineNo + "]的出库单明细已不存在");
		}

	}

	@Override
	public List<WmOutboundDetailPriceQueryItem> queryHistoryPrice(Map map) {
		UserDetails userDetails = (UserDetails) session.getAttribute(Constants.SESSION_USER_KEY);
		map.put("companyId", userDetails.getCompanyId().toString());
		map.put("warehouseId", userDetails.getWarehouseId().toString());
		return wmOutboundDetailMapper.queryHistoryPrice(map);
	}

	@Override
	public List<WmOutboundDetailSaleHistoryQueryItem> queryHistorySale(Map map) {
		UserDetails userDetails = (UserDetails) session.getAttribute(Constants.SESSION_USER_KEY);
		map.put("companyId", userDetails.getCompanyId().toString());
		map.put("warehouseId", userDetails.getWarehouseId().toString());
		return wmOutboundDetailMapper.queryHistorySale(map);
	}

	@Override
	public Message allocByKey(String orderNo, String lineNo) throws BusinessException {
		// TODO Auto-generated method stub
		Message message = new Message();
		List<WmOutboundDetailQueryItem> detailList = this.selectByKey(orderNo, lineNo);
		if (detailList.size() > 0) {
			UserDetails userDetails = (UserDetails) session.getAttribute(Constants.SESSION_USER_KEY);
			WmOutboundDetailQueryItem detail = detailList.get(0);
			if (WmsCodeMaster.SO_FULL_ALLOC.getCode().equals(detail.getStatus())) {
				throw new BusinessException("出库单号[" + orderNo + "]行号[" + lineNo + "]的出库单明细已经分配!");
			}
			WmInventory example = new WmInventory();
			example.setSkuCode(detail.getSkuCode());
			example.setCompanyId(userDetails.getCompanyId());
			example.setWarehouseId(userDetails.getWarehouseId());
			List<WmInventory> inventoryRecords = wmInventoryService.getAvailableInvByExample(example);
			// 对库存记录进行排序，按照库存可用数升序排序
			Collections.sort(inventoryRecords, new Comparator<WmInventory>() {
				@Override
				public int compare(WmInventory o1, WmInventory o2) {
					return o1.getInvAvailableNum().compareTo(o2.getInvAvailableNum());
				}
			});
			// 获得订货数
			double outboundNumForCalculate = detail.getOutboundNum();
			for (WmInventory inventory : inventoryRecords) {
				WmOutboundAlloc alloc = new WmOutboundAlloc();
				alloc.setBuyerCode(detail.getBuyerCode());
				alloc.setOrderNo(detail.getOrderNo());
				alloc.setLineNo(detail.getLineNo());
				alloc.setSkuCode(inventory.getSkuCode());
				alloc.setOutboundPrice(detail.getOutboundPrice());
				alloc.setToLocCode(detail.getPlanShipLoc());
				alloc.setAllocLocCode(inventory.getLocCode());
				alloc.setStatus(WmsCodeMaster.SO_FULL_ALLOC.getCode());
				InventoryUpdateEntity entity = new InventoryUpdateEntity();
				entity.setActionCode(WmsCodeMaster.ACT_ALLOC.getCode());
				entity.setLineNo(detail.getLineNo());
				entity.setLocCode(inventory.getLocCode());
				entity.setOrderNo(orderNo);
				entity.setOrderType(WmsCodeMaster.ORDER_OUB.getCode());
				entity.setSkuCode(inventory.getSkuCode());
				entity.setPrice(detail.getOutboundPrice());
				// 库存数量足够
				if (inventory.getInvAvailableNum().doubleValue() >= outboundNumForCalculate) {
					alloc.setOutboundNum(outboundNumForCalculate);

					entity.setQtyOp(outboundNumForCalculate);
					entity.setQtyOpBefore(inventory.getInvAvailableNum());
					entity.setQtyOpAfter(inventory.getInvAvailableNum() - outboundNumForCalculate);
					// 更新库存
					WmActTran actTran = wmInventoryService.updateInventory(entity);
					// 回填计算出的成本数据
					alloc.setCost(actTran.getCost());
					wmOutboundAllocService.saveOutboundAlloc(alloc);
					outboundNumForCalculate = 0.0;
					break;
				} else {
					alloc.setOutboundNum(inventory.getInvAvailableNum());

					entity.setQtyOp(inventory.getInvAvailableNum());
					entity.setQtyOpBefore(inventory.getInvAvailableNum());
					entity.setQtyOpAfter(0.0);
					// 更新库存
					WmActTran actTran = wmInventoryService.updateInventory(entity);
					// 回填计算出的成本数据
					alloc.setCost(actTran.getCost());
					wmOutboundAllocService.saveOutboundAlloc(alloc);
					outboundNumForCalculate = outboundNumForCalculate - inventory.getInvAvailableNum().doubleValue();
				}
			}
			WmOutboundDetail targetDetail = new WmOutboundDetail();
			BeanUtils.copyProperties(detail, targetDetail);
			targetDetail.setOutboundAllocNum(detail.getOutboundNum().doubleValue() - outboundNumForCalculate);

			// 订货数已经全部分配
			if (outboundNumForCalculate == 0.0) {
				targetDetail.setStatus(WmsCodeMaster.SO_FULL_ALLOC.getCode());
				DaoUtil.save(targetDetail, wmOutboundDetailMapper, session);
				// 根据出库单明细更新出库单的状态
				wmOutboundUpdateService.updataOutboundStatusByOutboundDetail(orderNo);
				message.setCode(200);
				return message;
			} else if (outboundNumForCalculate > 0.0
					&& outboundNumForCalculate < detail.getOutboundNum().doubleValue()) {
				targetDetail.setStatus(WmsCodeMaster.SO_PART_ALLOC.getCode());
				DaoUtil.save(targetDetail, wmOutboundDetailMapper, session);
				// 根据出库单明细更新出库单的状态
				wmOutboundUpdateService.updataOutboundStatusByOutboundDetail(orderNo);
				// 订货数还有剩余
				String surplusOutboundNum = (detail.getOutboundNum().doubleValue() - outboundNumForCalculate) + "";
				message.setCode(100);
				message.setMsg("出库单号[" + orderNo + "]行号[" + lineNo + "]的出库单明细已分配" + surplusOutboundNum + "剩余"
						+ outboundNumForCalculate + "");
				return message;
			} else {
				// 订货数没有变化
				throw new BusinessException("出库单号[" + orderNo + "]行号[" + lineNo + "]的出库单明细在仓库中没有库存!");
			}
		} else {
			throw new BusinessException("出库单号[" + orderNo + "]行号[" + lineNo + "]的出库单明细已不存在");
		}

	}

	@Override
	public List<WmOutboundDetailQueryItem> selectClosedOrderDetail(Map map) {
		// TODO Auto-generated method stub
		return wmOutboundDetailMapper.selectClosedOrderDetail(map);
	}

}
