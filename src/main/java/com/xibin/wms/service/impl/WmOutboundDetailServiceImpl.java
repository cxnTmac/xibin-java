package com.xibin.wms.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.xibin.core.costants.Constants;
import com.xibin.core.daosupport.BaseManagerImpl;
import com.xibin.core.daosupport.BaseMapper;
import com.xibin.core.daosupport.DaoUtil;
import com.xibin.core.exception.BusinessException;
import com.xibin.core.pojo.Message;
import com.xibin.core.security.pojo.UserDetails;
import com.xibin.core.utils.ComputeUtil;
import com.xibin.core.utils.ReadExcelTools;
import com.xibin.wms.constants.WmsCodeMaster;
import com.xibin.wms.dao.WmOutboundAllocMapper;
import com.xibin.wms.dao.WmOutboundDetailMapper;
import com.xibin.wms.entity.InventoryUpdateEntity;
import com.xibin.wms.entity.OutboundDetailQuickImportEntity;
import com.xibin.wms.pojo.WmActTran;
import com.xibin.wms.pojo.WmInventory;
import com.xibin.wms.pojo.WmOutboundAlloc;
import com.xibin.wms.pojo.WmOutboundDetail;
import com.xibin.wms.pojo.WmOutboundHeader;
import com.xibin.wms.query.BdFittingSkuQueryItem;
import com.xibin.wms.query.WmOutboundDetailPriceQueryItem;
import com.xibin.wms.query.WmOutboundDetailQueryItem;
import com.xibin.wms.query.WmOutboundDetailSaleHistoryQueryItem;
import com.xibin.wms.query.WmOutboundDetailSkuQueryItem;
import com.xibin.wms.query.WmOutboundHeaderQueryItem;
import com.xibin.wms.service.BdFittingSkuService;
import com.xibin.wms.service.WmInventoryService;
import com.xibin.wms.service.WmOutboundAllocService;
import com.xibin.wms.service.WmOutboundDetailService;
import com.xibin.wms.service.WmOutboundHeaderService;
import com.xibin.wms.service.WmOutboundUpdateService;

@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
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
	@Autowired
	private BdFittingSkuService bdFittingSkuService;

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
	public Message removeOutboundDetail(int[] ids, String orderNo) throws BusinessException {
		// TODO Auto-generated method stub
		Message message = newOperateBeforeCheck(orderNo);
		if (message.getCode() == 0) {
			return message;
		} else {
			Message returnMessage = new Message();
			ArrayList<String> errors = new ArrayList<String>();
			ArrayList<Integer> list = new ArrayList<Integer>();
			for (int id : ids) {
				WmOutboundDetail detail = this.getOutboundDetailById(id);
				if (detail.getStatus().equals(WmsCodeMaster.SO_NEW.getCode())) {
					list.add(id);
				} else {
					errors.add("行号[" + detail.getLineNo() + "]的明细不是创建状态，不能删除");
				}
			}
			if (list.size() == 0) {
				returnMessage.setCode(0);
				returnMessage.setMsgs(errors);
				returnMessage.converMsgsToMsg("</br>");
				return returnMessage;
			}
			int[] deleteIds = new int[list.size()];
			for (int i = 0; i < list.size(); i++) {
				deleteIds[i] = list.get(i);
			}
			this.delete(deleteIds);
			// 根据出库单明细更新出库单的状态
			wmOutboundUpdateService.updataOutboundStatusByOutboundDetail(orderNo);
			if (errors.size() == 0) {
				returnMessage.setCode(200);
				returnMessage.setMsg("操作成功");
			} else {
				returnMessage.setCode(100);
				returnMessage.setMsgs(errors);
				returnMessage.converMsgsToMsg("</br>");
			}
			return returnMessage;
		}
	}

	@Override
	public Message removeOutboundDetailAndCreateNewOrder(int[] ids, String orderNo) throws BusinessException {
		Message message = newOperateBeforeCheck(orderNo);
		if (message.getCode() == 0) {
			return message;
		} else {
			Message returnMessage = new Message();
			WmOutboundHeaderQueryItem outboundHeaderQueryItem = (WmOutboundHeaderQueryItem) message.getData();
			WmOutboundHeader newHeader = new WmOutboundHeader();
			BeanUtils.copyProperties(outboundHeaderQueryItem, newHeader);
			newHeader.setId(null);
			newHeader.setAuditTime(null);
			newHeader.setAuditOp(null);
			newHeader.setAuditTime(null);
			newHeader.setCostVoucherId(null);
			newHeader.setIsCalculated("N");
			newHeader.setIsCostCalculated("N");
			newHeader.setOrderNo(null);
			newHeader.setOrderTime(new Date());
			newHeader.setStatus(WmsCodeMaster.SO_NEW.getCode());
			newHeader.setVoucherId(null);
			ArrayList<WmOutboundDetail> neWmOutboundDetails = new ArrayList<WmOutboundDetail>();
			ArrayList<String> errors = new ArrayList<String>();
			ArrayList<Integer> list = new ArrayList<Integer>();
			for (int id : ids) {
				WmOutboundDetail detail = this.getOutboundDetailById(id);
				if (detail.getStatus().equals(WmsCodeMaster.SO_NEW.getCode())) {
					list.add(id);
					// ID置空
					detail.setId(null);
					// 订单号置空
					detail.setOrderNo(null);
					neWmOutboundDetails.add(detail);
				} else {
					errors.add("行号[" + detail.getLineNo() + "]的明细不是创建状态，不能删除");
				}
			}
			if (list.size() == 0) {
				returnMessage.setCode(0);
				returnMessage.setMsgs(errors);
				returnMessage.converMsgsToMsg("</br>");
				return returnMessage;
			}
			int[] deleteIds = new int[list.size()];
			for (int i = 0; i < list.size(); i++) {
				deleteIds[i] = list.get(i);
			}
			this.delete(deleteIds);
			// 根据出库单明细更新出库单的状态
			wmOutboundUpdateService.updataOutboundStatusByOutboundDetail(orderNo);
			if (errors.size() == 0) {
				returnMessage.setCode(200);
				returnMessage.setMsg("操作成功");
			} else {
				returnMessage.setCode(100);
				returnMessage.setMsgs(errors);
				returnMessage.converMsgsToMsg("</br>");
			}
			// 新建订单
			WmOutboundHeaderQueryItem neWmOutboundHeaderQueryItem = wmOutboundHeaderService.saveOutbound(newHeader);

			// 由于是生成的订单，直接生成行号就行
			int lineNo = 1;
			for (WmOutboundDetail detail : neWmOutboundDetails) {
				detail.setOrderNo(neWmOutboundHeaderQueryItem.getOrderNo());
				detail.setLineNo(lineNo + "");
				lineNo++;
			}
			this.save(neWmOutboundDetails);
			returnMessage.setMsg(returnMessage.getMsg() + "创建新的出库单[" + neWmOutboundHeaderQueryItem.getOrderNo() + "]");
			return returnMessage;
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

	private Message newOperateBeforeCheck(String orderNo) {
		Message message = new Message();
		List<WmOutboundHeaderQueryItem> headerList = wmOutboundHeaderService.selectByKey(orderNo);
		if (headerList.size() > 0) {
			WmOutboundHeaderQueryItem header = headerList.get(0);
			// 关闭定单不能编辑明细
			if (WmsCodeMaster.INB_CLOSE.getCode().equals(header.getStatus())) {
				message.setCode(0);
				message.setMsg("出库单[" + orderNo + "]是关闭状态,不能对明细进行编辑");
				return message;
			} else {
				message.setData(header);
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
		Message message = newOperateBeforeCheck(model.getOrderNo());
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
		} else {
			WmOutboundAlloc allocQueryExample = new WmOutboundAlloc();
			allocQueryExample.setCompanyId(userDetails.getCompanyId());
			allocQueryExample.setWarehouseId(userDetails.getWarehouseId());
			allocQueryExample.setOrderNo(model.getOrderNo());
			allocQueryExample.setLineNo(model.getLineNo());
			// 如果明细不是新增状态，可以修改 价格 和 计划发货库位，但是要修改相应的分配明细
			List<WmOutboundAlloc> allocQueryItems = wmOutboundAllocMapper.selectByExample(allocQueryExample);
			if (allocQueryItems.size() > 0) {
				for (WmOutboundAlloc allocQueryItem : allocQueryItems) {
					allocQueryItem.setOutboundPrice(model.getOutboundPrice());
					allocQueryItem.setToLocCode(model.getPlanShipLoc());
				}
				DaoUtil.save(allocQueryItems, wmOutboundAllocMapper, session);
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
	public Message importByExcel(MultipartFile file, String orderNo, String loc, String skuCodeColumnName,
			String priceColumnName, String numColumnName) throws BusinessException {
		Message message = new Message();
		UserDetails userDetails = (UserDetails) session.getAttribute(Constants.SESSION_USER_KEY);
		Message checkMsg = newOperateBeforeCheck(orderNo);
		if (checkMsg.getCode() == 0) {
			return checkMsg;
		}
		WmOutboundHeaderQueryItem header = (WmOutboundHeaderQueryItem) checkMsg.getData();
		List<Integer> lastLineNo = selectLastLineNo(header.getOrderNo(), header.getCompanyId().toString(),
				header.getWarehouseId().toString());
		Integer num = lastLineNo.get(0);
		if (num == null) {
			num = 1;
		} else {
			num++;
		}
		try {
			Message readMessge = new Message();
			readMessge = ReadExcelTools.readExcelForOutboundDetailImport(file, skuCodeColumnName, priceColumnName,
					numColumnName);
			List<OutboundDetailQuickImportEntity> results = (List<OutboundDetailQuickImportEntity>) readMessge
					.getData();
			List<WmOutboundDetail> details = new ArrayList<WmOutboundDetail>();
			for (OutboundDetailQuickImportEntity entity : results) {
				WmOutboundDetail detail = new WmOutboundDetail();
				detail.setBuyerCode(header.getBuyerCode());
				detail.setCompanyId(userDetails.getCompanyId());
				detail.setCost(0.0);
				detail.setLineNo(num + "");
				detail.setOrderNo(header.getOrderNo());
				detail.setOutboundAllocNum(0.0);
				detail.setOutboundNum(entity.getOutboundNum());
				detail.setOutboundPickNum(0.0);
				detail.setOutboundPrice(entity.getOutboundPrice());
				detail.setOutboundShipNum(0.0);
				detail.setPlanShipLoc(loc);
				detail.setSkuCode(entity.getFittingSkuCode());
				detail.setStatus(WmsCodeMaster.SO_NEW.getCode());
				detail.setWarehouseId(userDetails.getWarehouseId());
				details.add(detail);
				num++;
			}
			DaoUtil.save(details, wmOutboundDetailMapper, session);
			message.setCode(200);
			message.setMsg(readMessge.getMsg());
			message.setMsg("导入了" + details.size() + "行记录，请与源文件进行核对！");
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
			message = this.cancelAlloc(detail);
			return message;
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
	public Message cancelAllocByOrderNo(String orderNo) {
		UserDetails userDetails = (UserDetails) session.getAttribute(Constants.SESSION_USER_KEY);
		Message message = new Message();
		WmOutboundHeader headerQueryExample = new WmOutboundHeader();
		headerQueryExample.setOrderNo(orderNo);
		headerQueryExample.setCompanyId(userDetails.getCompanyId());
		headerQueryExample.setWarehouseId(userDetails.getWarehouseId());
		List<WmOutboundHeader> headerList = wmOutboundHeaderService.selectByExample(headerQueryExample);
		if (headerList.size() > 0) {
			WmOutboundHeader header = headerList.get(0);
			if (!header.getStatus().equals(WmsCodeMaster.SO_FULL_ALLOC.getCode())
					&& !header.getStatus().equals(WmsCodeMaster.SO_PART_ALLOC.getCode())
					&& !header.getStatus().equals(WmsCodeMaster.SO_PART_PICKING.getCode())) {
				message.setMsg("出库单[" + orderNo + "]不是部分拣货|部分分配|完全分配状态！");
				message.setCode(0);
				return message;
			}
			if (!header.getAuditStatus().equals(WmsCodeMaster.AUDIT_NOT.getCode())
					&& !header.getAuditStatus().equals(WmsCodeMaster.AUDIT_CLOSE.getCode())) {
				message.setMsg("出库单[" + orderNo + "]未审核！");
				message.setCode(0);
				return message;
			}
			WmOutboundDetail queryExample = new WmOutboundDetail();
			queryExample.setOrderNo(orderNo);
			queryExample.setCompanyId(userDetails.getCompanyId());
			queryExample.setWarehouseId(userDetails.getWarehouseId());
			List<WmOutboundDetail> detailList = this.selectByExample(queryExample);
			if (detailList.size() > 0) {
				List<String> errors = new ArrayList<String>();
				for (WmOutboundDetail detail : detailList) {
					try {
						if (detail.getStatus().equals(WmsCodeMaster.SO_PART_ALLOC.getCode())
								|| detail.getStatus().equals(WmsCodeMaster.SO_FULL_ALLOC.getCode())) {
							message = this.cancelAlloc(detail);
						}
						if (message.getCode() == 100) {
							errors.add(message.getMsg());
						}
					} catch (BusinessException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						errors.add(e.getMessage());
					}
				}
				if (errors.size() > 0) {
					message.setCode(100);
					message.setMsgs(errors);
				} else {
					message.setCode(200);
					message.setMsg("操作成功！");
				}
				return message;
			} else {
				message.setMsg("出库单号[" + orderNo + "]没有明细");
				message.setCode(0);
				return message;
			}
		} else {
			message.setMsg("出库单[" + orderNo + "]不存在！");
			message.setCode(0);
			return message;
		}
	}

	@Override
	public Message allocByOrderNo(String orderNo) {
		UserDetails userDetails = (UserDetails) session.getAttribute(Constants.SESSION_USER_KEY);
		Message message = new Message();
		WmOutboundHeader headerQueryExample = new WmOutboundHeader();
		headerQueryExample.setOrderNo(orderNo);
		headerQueryExample.setCompanyId(userDetails.getCompanyId());
		headerQueryExample.setWarehouseId(userDetails.getWarehouseId());
		List<WmOutboundHeader> headerList = wmOutboundHeaderService.selectByExample(headerQueryExample);
		if (headerList.size() > 0) {
			WmOutboundHeader header = headerList.get(0);
			if (!header.getStatus().equals(WmsCodeMaster.SO_NEW.getCode())
					&& !header.getStatus().equals(WmsCodeMaster.SO_PART_ALLOC.getCode())) {
				message.setMsg("出库单[" + orderNo + "]不是创建状态！");
				message.setCode(0);
				return message;
			}
			if (!header.getAuditStatus().equals(WmsCodeMaster.AUDIT_NOT.getCode())
					&& !header.getAuditStatus().equals(WmsCodeMaster.AUDIT_CLOSE.getCode())) {
				message.setMsg("出库单[" + orderNo + "]未审核！");
				message.setCode(0);
				return message;
			}
			WmOutboundDetail queryExample = new WmOutboundDetail();
			queryExample.setOrderNo(orderNo);
			queryExample.setCompanyId(userDetails.getCompanyId());
			queryExample.setWarehouseId(userDetails.getWarehouseId());
			List<WmOutboundDetail> detailList = this.selectByExample(queryExample);
			if (detailList.size() > 0) {
				List<String> errors = new ArrayList<String>();
				for (WmOutboundDetail detail : detailList) {
					try {
						if (detail.getStatus().equals(WmsCodeMaster.SO_PART_ALLOC.getCode())
								|| detail.getStatus().equals(WmsCodeMaster.SO_NEW.getCode())) {
							message = this.alloc(detail);
							if (message.getCode() == 100 || message.getCode() == 0) {
								errors.add(message.getMsg());
							}
						}
					} catch (BusinessException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						errors.add(e.getMessage());
					}
				}
				if (errors.size() > 0) {
					message.setCode(100);
					message.setMsgs(errors);
				} else {
					message.setCode(200);
					message.setMsg("操作成功！");
				}
				return message;
			} else {
				message.setMsg("出库单号[" + orderNo + "]没有明细");
				message.setCode(0);
				return message;
			}
		} else {
			message.setMsg("出库单[" + orderNo + "]不存在！");
			message.setCode(0);
			return message;
		}
	}

	// 取消分配
	// @param: detail 必须为实时从数据库中获取到的数据
	private Message cancelAlloc(WmOutboundDetail detail) throws BusinessException {
		Message message = new Message();
		UserDetails userDetails = (UserDetails) session.getAttribute(Constants.SESSION_USER_KEY);
		// 如果出库单明细不等于完全分配/部分分配
		if (WmsCodeMaster.SO_FULL_ALLOC.getCode().equals(detail.getStatus())
				|| WmsCodeMaster.SO_PART_ALLOC.getCode().equals(detail.getStatus())) {
			WmOutboundAlloc allocQueryExample = new WmOutboundAlloc();
			allocQueryExample.setOrderNo(detail.getOrderNo());
			allocQueryExample.setLineNo(detail.getLineNo());
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
				if (WmsCodeMaster.ALLOC_AUTO.getCode().equals(allocList.get(i).getAllocType())) {
					entity.setActionCode(WmsCodeMaster.ACT_CANCEL_ALLOC.getCode());
				} else if (WmsCodeMaster.ALLOC_ASS.getCode().equals(allocList.get(i).getAllocType())) {
					entity.setActionCode(WmsCodeMaster.ACT_CANCEL_PRE_ASSEMBLE_ALLOC.getCode());
				}
				entity.setLineNo(detail.getLineNo());
				entity.setLocCode(allocList.get(i).getAllocLocCode());
				entity.setOrderNo(detail.getOrderNo());
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
			wmOutboundUpdateService.updataOutboundStatusByOutboundDetail(detail.getOrderNo());
			message.setMsg("操作成功！");
			message.setCode(200);
			return message;
		} else {
			throw new BusinessException(
					"出库单号[" + detail.getOrderNo() + "]行号[" + detail.getLineNo() + "]的出库单明细不是完全分配或者部分分配状态");
		}
	}

	// 执行预加工分配
	// @param: detail 必须为实时从数据库中获取到的数据
	private Message preAssembleAlloc(WmOutboundDetail detail) throws BusinessException {
		Message message = new Message();
		UserDetails userDetails = (UserDetails) session.getAttribute(Constants.SESSION_USER_KEY);
		// 校验出库明细的产品是否为加工组合件
		List<BdFittingSkuQueryItem> queryResults = bdFittingSkuService.selectByKey(detail.getSkuCode(),
				userDetails.getCompanyId().toString());
		if (queryResults.size() == 0) {
			message.setMsg("出库单[" + detail.getOrderNo() + "]行号[" + detail.getLineNo() + "]的产品[" + detail.getSkuCode()
					+ "]不存在！");
			message.setCode(0);
			return message;
		} else if ("N".equals(queryResults.get(0).getNeedToAssemble())) {
			BdFittingSkuQueryItem queryResult = queryResults.get(0);
			message.setMsg("出库单[" + detail.getOrderNo() + "]行号[" + detail.getLineNo() + "]的产品[" + detail.getSkuCode()
					+ "]不是组装产品，不能使用组装预分配！");
			message.setCode(0);
			return message;
		}
		if (WmsCodeMaster.SO_PART_ALLOC.getCode().equals(detail.getStatus())
				|| WmsCodeMaster.SO_NEW.getCode().equals(detail.getStatus())) {
			WmInventory example = new WmInventory();
			example.setSkuCode(detail.getSkuCode());
			// 预加工库位 只分配库位类别为加工库位的库位，目前暂时都固定为 默认的WORKBENCH加工库位
			example.setLocCode(WmsCodeMaster.LOC_WORKBENCH.getCode());
			example.setCompanyId(userDetails.getCompanyId());
			example.setWarehouseId(userDetails.getWarehouseId());
			List<WmInventory> inventoryRecords = wmInventoryService.selectByExample(example);
			// 由于固定了库位，所以库存记录必然只有一条
			// 获得需要分配的数量 = 订货数 - 分配数
			double outboundNumForCalculate = ComputeUtil.sub(detail.getOutboundNum(), detail.getOutboundAllocNum());
			if (inventoryRecords.size() > 0) {
				WmInventory inventory = inventoryRecords.get(0);
				WmOutboundAlloc alloc = new WmOutboundAlloc();
				alloc.setBuyerCode(detail.getBuyerCode());
				alloc.setOrderNo(detail.getOrderNo());
				alloc.setLineNo(detail.getLineNo());
				// 设置分配类型
				alloc.setAllocType(WmsCodeMaster.ALLOC_ASS.getCode());
				alloc.setSkuCode(inventory.getSkuCode());
				alloc.setOutboundPrice(detail.getOutboundPrice());
				alloc.setToLocCode(detail.getPlanShipLoc());
				alloc.setAllocLocCode(inventory.getLocCode());
				alloc.setStatus(WmsCodeMaster.SO_FULL_ALLOC.getCode());
				InventoryUpdateEntity entity = new InventoryUpdateEntity();
				// 预加工分配！
				entity.setActionCode(WmsCodeMaster.ACT_PRE_ASSEMBLE_ALLOC.getCode());
				entity.setLineNo(detail.getLineNo());
				entity.setLocCode(inventory.getLocCode());
				entity.setOrderNo(detail.getOrderNo());
				entity.setOrderType(WmsCodeMaster.ORDER_OUB.getCode());
				entity.setSkuCode(inventory.getSkuCode());
				entity.setPrice(detail.getOutboundPrice());
				alloc.setOutboundNum(outboundNumForCalculate);
				alloc.setPickNum(outboundNumForCalculate);
				entity.setQtyOp(outboundNumForCalculate);
				entity.setQtyOpBefore(inventory.getInvAvailableNum());
				entity.setQtyOpAfter(ComputeUtil.sub(inventory.getInvAvailableNum(), outboundNumForCalculate));
				// 更新库存
				wmInventoryService.updateInventory(entity);
				alloc.setCost(0.0);
				wmOutboundAllocService.saveOutboundAlloc(alloc);
				// 预加工分配必定分配完
				outboundNumForCalculate = 0.0;
			} else {
				WmOutboundAlloc alloc = new WmOutboundAlloc();
				alloc.setBuyerCode(detail.getBuyerCode());
				alloc.setOrderNo(detail.getOrderNo());
				alloc.setLineNo(detail.getLineNo());
				// 设置分配类型
				alloc.setAllocType(WmsCodeMaster.ALLOC_ASS.getCode());
				alloc.setSkuCode(detail.getSkuCode());
				alloc.setOutboundPrice(detail.getOutboundPrice());
				alloc.setToLocCode(detail.getPlanShipLoc());
				alloc.setAllocLocCode(WmsCodeMaster.LOC_WORKBENCH.getCode());
				alloc.setStatus(WmsCodeMaster.SO_FULL_ALLOC.getCode());

				InventoryUpdateEntity entity = new InventoryUpdateEntity();
				// 预加工分配！
				entity.setActionCode(WmsCodeMaster.ACT_PRE_ASSEMBLE_ALLOC.getCode());
				entity.setLineNo(detail.getLineNo());
				entity.setLocCode(WmsCodeMaster.LOC_WORKBENCH.getCode());
				entity.setOrderNo(detail.getOrderNo());
				entity.setOrderType(WmsCodeMaster.ORDER_OUB.getCode());
				entity.setSkuCode(detail.getSkuCode());
				entity.setPrice(detail.getOutboundPrice());
				alloc.setOutboundNum(outboundNumForCalculate);
				alloc.setPickNum(outboundNumForCalculate);
				entity.setQtyOp(outboundNumForCalculate);
				entity.setQtyOpBefore(0.0);
				entity.setQtyOpAfter(outboundNumForCalculate);
				// 更新库存
				WmActTran actTran = wmInventoryService.updateInventory(entity);
				alloc.setCost(0.0);
				wmOutboundAllocService.saveOutboundAlloc(alloc);
				// 预加工分配必定分配完
				outboundNumForCalculate = 0.0;
			}
			WmOutboundDetail targetDetail = new WmOutboundDetail();
			BeanUtils.copyProperties(detail, targetDetail);
			targetDetail.setOutboundAllocNum(
					ComputeUtil.sub(detail.getOutboundNum().doubleValue(), outboundNumForCalculate));
			targetDetail.setStatus(WmsCodeMaster.SO_FULL_ALLOC.getCode());
			DaoUtil.save(targetDetail, wmOutboundDetailMapper, session);
			// 根据出库单明细更新出库单的状态
			wmOutboundUpdateService.updataOutboundStatusByOutboundDetail(detail.getOrderNo());
			message.setCode(200);
			return message;
		}
		return message;
	}

	// 执行分配
	// @param: detail 必须为实时从数据库中获取到的数据
	private Message alloc(WmOutboundDetail detail) throws BusinessException {
		Message message = new Message();
		UserDetails userDetails = (UserDetails) session.getAttribute(Constants.SESSION_USER_KEY);
		if (WmsCodeMaster.SO_PART_ALLOC.getCode().equals(detail.getStatus())
				|| WmsCodeMaster.SO_NEW.getCode().equals(detail.getStatus())) {
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
			double totalCost = ComputeUtil.mul(detail.getCost(), detail.getOutboundAllocNum());
			// 获得需要分配的数量 = 订货数 - 分配数
			double outboundNumForCalculate = ComputeUtil.sub(detail.getOutboundNum(), detail.getOutboundAllocNum());
			for (WmInventory inventory : inventoryRecords) {
				WmOutboundAlloc alloc = new WmOutboundAlloc();
				alloc.setBuyerCode(detail.getBuyerCode());
				alloc.setOrderNo(detail.getOrderNo());
				alloc.setLineNo(detail.getLineNo());
				// 设置分配类型
				alloc.setAllocType(WmsCodeMaster.ALLOC_AUTO.getCode());
				alloc.setSkuCode(inventory.getSkuCode());
				alloc.setOutboundPrice(detail.getOutboundPrice());
				alloc.setToLocCode(detail.getPlanShipLoc());
				alloc.setAllocLocCode(inventory.getLocCode());
				alloc.setStatus(WmsCodeMaster.SO_FULL_ALLOC.getCode());
				InventoryUpdateEntity entity = new InventoryUpdateEntity();
				entity.setActionCode(WmsCodeMaster.ACT_ALLOC.getCode());
				entity.setLineNo(detail.getLineNo());
				entity.setLocCode(inventory.getLocCode());
				entity.setOrderNo(detail.getOrderNo());
				entity.setOrderType(WmsCodeMaster.ORDER_OUB.getCode());
				entity.setSkuCode(inventory.getSkuCode());
				entity.setPrice(detail.getOutboundPrice());
				// 库存数量足够
				if (inventory.getInvAvailableNum().doubleValue() >= outboundNumForCalculate) {
					alloc.setOutboundNum(outboundNumForCalculate);
					alloc.setPickNum(outboundNumForCalculate);
					entity.setQtyOp(outboundNumForCalculate);
					entity.setQtyOpBefore(inventory.getInvAvailableNum());
					entity.setQtyOpAfter(ComputeUtil.sub(inventory.getInvAvailableNum(), outboundNumForCalculate));
					// 更新库存
					WmActTran actTran = wmInventoryService.updateInventory(entity);
					// 回填计算出的成本数据
					alloc.setCost(actTran.getCost());
					totalCost = ComputeUtil.add(totalCost, actTran.getCost() * outboundNumForCalculate);
					wmOutboundAllocService.saveOutboundAlloc(alloc);
					outboundNumForCalculate = 0.0;
					break;
				} else {
					alloc.setOutboundNum(inventory.getInvAvailableNum());
					alloc.setPickNum(inventory.getInvAvailableNum());
					entity.setQtyOp(inventory.getInvAvailableNum());
					entity.setQtyOpBefore(inventory.getInvAvailableNum());
					entity.setQtyOpAfter(0.0);
					// 更新库存
					WmActTran actTran = wmInventoryService.updateInventory(entity);
					// 回填计算出的成本数据
					alloc.setCost(actTran.getCost());
					totalCost = ComputeUtil.add(totalCost, actTran.getCost() * entity.getQtyOp());
					wmOutboundAllocService.saveOutboundAlloc(alloc);
					outboundNumForCalculate = ComputeUtil.sub(outboundNumForCalculate,
							inventory.getInvAvailableNum().doubleValue());
				}
			}
			WmOutboundDetail targetDetail = new WmOutboundDetail();
			BeanUtils.copyProperties(detail, targetDetail);
			targetDetail.setOutboundAllocNum(
					ComputeUtil.sub(detail.getOutboundNum().doubleValue(), outboundNumForCalculate));

			// 订货数已经全部分配
			if (outboundNumForCalculate == 0.0) {
				targetDetail.setCost(ComputeUtil.div(totalCost, targetDetail.getOutboundAllocNum(), 2));
				targetDetail.setStatus(WmsCodeMaster.SO_FULL_ALLOC.getCode());
				DaoUtil.save(targetDetail, wmOutboundDetailMapper, session);
				// 根据出库单明细更新出库单的状态
				wmOutboundUpdateService.updataOutboundStatusByOutboundDetail(detail.getOrderNo());
				message.setCode(200);
				return message;
			} else if (outboundNumForCalculate > 0.0
					&& outboundNumForCalculate < detail.getOutboundNum().doubleValue()) {
				targetDetail.setCost(ComputeUtil.div(totalCost, targetDetail.getOutboundAllocNum(), 2));
				targetDetail.setStatus(WmsCodeMaster.SO_PART_ALLOC.getCode());
				DaoUtil.save(targetDetail, wmOutboundDetailMapper, session);
				// 根据出库单明细更新出库单的状态
				wmOutboundUpdateService.updataOutboundStatusByOutboundDetail(detail.getOrderNo());
				// 订货数还有剩余
				String surplusOutboundNum = (ComputeUtil.sub(detail.getOutboundNum().doubleValue(),
						outboundNumForCalculate)) + "";
				message.setCode(100);
				message.setMsg("出库单号[" + detail.getOrderNo() + "]行号[" + detail.getLineNo() + "]的出库单明细已分配"
						+ surplusOutboundNum + "剩余" + outboundNumForCalculate + "");
				return message;
			} else {
				// 订货数没有变化
				throw new BusinessException(
						"出库单号[" + detail.getOrderNo() + "]行号[" + detail.getLineNo() + "]的出库单明细在仓库中没有库存!");
			}
		} else {
			throw new BusinessException("出库单号[" + detail.getOrderNo() + "]行号[" + detail.getLineNo() + "]的不能分配");
		}

	}

	@Override
	public Message allocByKey(String orderNo, String lineNo, String type) throws BusinessException {
		UserDetails userDetails = (UserDetails) session.getAttribute(Constants.SESSION_USER_KEY);
		Message message = new Message();
		WmOutboundDetail queryExample = new WmOutboundDetail();
		queryExample.setOrderNo(orderNo);
		queryExample.setLineNo(lineNo);
		queryExample.setCompanyId(userDetails.getCompanyId());
		queryExample.setWarehouseId(userDetails.getWarehouseId());
		List<WmOutboundDetail> detailList = this.selectByExample(queryExample);
		if (detailList.size() > 0) {

			WmOutboundDetail detail = detailList.get(0);
			if (WmsCodeMaster.SO_FULL_ALLOC.getCode().equals(detail.getStatus())) {
				throw new BusinessException("出库单号[" + orderNo + "]行号[" + lineNo + "]的出库单明细已经分配!");
			}
			if (WmsCodeMaster.ALLOC_AUTO.getCode().equals(type)) {
				message = this.alloc(detail);
			} else if (WmsCodeMaster.ALLOC_ASS.getCode().equals(type)) {
				message = this.preAssembleAlloc(detail);
			}

			return message;
		} else {
			throw new BusinessException("出库单号[" + orderNo + "]行号[" + lineNo + "]的出库单明细已不存在");
		}
	}

	@Override
	public List<WmOutboundDetailQueryItem> selectClosedOrderDetail(Map map) {
		// TODO Auto-generated method stub
		return wmOutboundDetailMapper.selectClosedOrderDetail(map);
	}

	@Override
	public List<WmOutboundDetailSkuQueryItem> queryWmOutboundDetailByPage(Map map) {
		// TODO Auto-generated method stub
		return wmOutboundDetailMapper.queryWmOutboundDetailByPage(map);
	}
}
