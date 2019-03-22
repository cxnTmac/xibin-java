package com.xibin.fin.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.xibin.core.costants.Constants;
import com.xibin.core.daosupport.BaseManagerImpl;
import com.xibin.core.daosupport.BaseMapper;
import com.xibin.core.exception.BusinessException;
import com.xibin.core.pojo.Message;
import com.xibin.core.security.pojo.UserDetails;
import com.xibin.fin.dao.FiCourseBalanceMapper;
import com.xibin.fin.entity.BalanceSheetEntity;
import com.xibin.fin.entity.FiCourseBalanceBSQueryItem;
import com.xibin.fin.pojo.FiCourseBalance;
import com.xibin.fin.service.FiCourseBalanceService;

@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
@Service
public class FiCourseBalanceServiceImpl extends BaseManagerImpl implements FiCourseBalanceService {
	@Autowired
	private HttpSession session;
	@Autowired
	private FiCourseBalanceMapper fiCourseBalanceMapper;

	@Override
	public FiCourseBalance getCourseBalanceById(int id) {
		// TODO Auto-generated method stub
		return fiCourseBalanceMapper.selectByPrimaryKey(id);
	}

	@Override
	public List<FiCourseBalance> getAllCourseBalanceByPage(Map map) {
		// TODO Auto-generated method stub
		return fiCourseBalanceMapper.selectAllByPage(map);
	}

	@Override
	public List<FiCourseBalance> selectByExample(FiCourseBalance example) {
		// TODO Auto-generated method stub
		return fiCourseBalanceMapper.selectByExample(example);
	}

	@Override
	public int removeCourseBalance(int id) throws BusinessException {
		// TODO Auto-generated method stub
		int[] ids = { id };
		return this.delete(ids);
	}

	@Override
	public FiCourseBalance saveCourseBalance(FiCourseBalance model) throws BusinessException {
		// TODO Auto-generated method stub
		UserDetails userDetails = (UserDetails) session.getAttribute(Constants.SESSION_USER_KEY);
		model.setCompanyId(userDetails.getCompanyId());
		model.setBookId(userDetails.getBookId());
		List<FiCourseBalance> list = fiCourseBalanceMapper.selectByKey(model.getPeriod(), model.getCourseNo(),
				model.getCompanyId().toString(), model.getBookId().toString());
		if (list.size() > 0 && model.getId() == 0) {
			throw new BusinessException(
					"期间：[" + model.getCourseNo() + "] 已存在，科目：[" + model.getCourseNo() + "]余额记录已存在，不能重复！");
		}
		return (FiCourseBalance) this.save(model);
	}

	@Override
	public void updateCourseBalances(List<FiCourseBalance> courseBalances) {
		List<FiCourseBalance> saved = new ArrayList<FiCourseBalance>();
		for (FiCourseBalance e : courseBalances) {
			if (e.getId() != null) {
				saved.add(e);
			}
		}
		this.save(saved);
	}

	@Override
	public List<FiCourseBalance> selectByKey(String period, String courseNo) {
		// TODO Auto-generated method stub
		UserDetails userDetails = (UserDetails) session.getAttribute(Constants.SESSION_USER_KEY);
		return fiCourseBalanceMapper.selectByKey(period, courseNo, userDetails.getCompanyId().toString(),
				userDetails.getBookId().toString());
	}

	@Override
	protected BaseMapper getMapper() {
		// TODO Auto-generated method stub
		return fiCourseBalanceMapper;
	}

	public Message queryForBalanceSheet(Map map) {
		Message message = new Message();
		BalanceSheetEntity entity = new BalanceSheetEntity();
		List<FiCourseBalanceBSQueryItem> queryItems = fiCourseBalanceMapper.queryForBalanceSheet(map);
		double[] sum = new double[43];
		for (FiCourseBalanceBSQueryItem queryItem : queryItems) {
			String courseNo = queryItem.getCourseNo();
			String isChild = queryItem.getIsChild();
			double startBalance = queryItem.getStartBalance();
			// 计算货币资金（库存现金，银行存款，其他货币资金）
			if ((courseNo.startsWith("1001") || courseNo.startsWith("1002") || courseNo.startsWith("1012"))
					&& "Y".equals(isChild)) {
				sum[0] += startBalance;
			}
			if (courseNo.startsWith("1101") && "Y".equals(isChild)) {
				sum[1] += startBalance;
			}
			// 计算应收票据
			if (courseNo.startsWith("1121") && "Y".equals(isChild)) {
				sum[2] += startBalance;
			}
			// 计算应收账款
			if (courseNo.startsWith("1122") && "Y".equals(isChild)) {
				sum[3] += startBalance;
			}
			// 计算预收账款
			if (courseNo.startsWith("2203") && "Y".equals(isChild)) {
				sum[4] += startBalance;
			}

			// 计算预付款项
			if (courseNo.startsWith("1123") && "Y".equals(isChild)) {
				sum[5] += startBalance;
			}
			// 计算应付账款
			if (courseNo.startsWith("2202") && "Y".equals(isChild)) {
				sum[6] += startBalance;
			}

			// 计算应收股利
			if (courseNo.startsWith("1131") && "Y".equals(isChild)) {
				sum[7] += startBalance;
			}
			// 计算应收利息
			if (courseNo.startsWith("1132") && "Y".equals(isChild)) {
				sum[8] += startBalance;
			}
			// 计算其他应收款
			if (courseNo.startsWith("1221") && "Y".equals(isChild)) {
				sum[9] += startBalance;
			}
			// 计算材料采购 在途物资 原材料 材料成本差异 库存商品
			if ((courseNo.startsWith("1401") || courseNo.startsWith("1402") || courseNo.startsWith("1403")
					|| courseNo.startsWith("1404") || courseNo.startsWith("1405") || courseNo.startsWith("1411")
					|| courseNo.startsWith("1421") || courseNo.startsWith("5001") || courseNo.startsWith("1408")
					|| courseNo.startsWith("5101") || courseNo.startsWith("5401")) && "Y".equals(isChild)) {
				sum[10] += startBalance;
				if (courseNo.startsWith("1403") && "Y".equals(isChild)) {
					sum[11] += startBalance;
				}
				if (courseNo.startsWith("5001") && "Y".equals(isChild)) {
					sum[12] += startBalance;
				}
				if (courseNo.startsWith("1405") && "Y".equals(isChild)) {
					sum[13] += startBalance;
				}
				if (courseNo.startsWith("1411") && "Y".equals(isChild)) {
					sum[14] += startBalance;
				}
			}
			if (courseNo.startsWith("1407") && "Y".equals(isChild)) {
				sum[15] -= startBalance;
			}
			// 其他流动资产
			if (courseNo.startsWith("190101") && "Y".equals(isChild)) {
				sum[16] += startBalance;
			}
			// 应付职工薪酬
			if (courseNo.startsWith("2211") && "Y".equals(isChild)) {
				sum[17] += startBalance;
			}
			// 应交税费
			if (courseNo.startsWith("2221") && "Y".equals(isChild)) {
				sum[18] += startBalance;
			}
			// 应付利息
			if (courseNo.startsWith("2231") && "Y".equals(isChild)) {
				sum[19] += startBalance;
			}
			// 应付利润
			if (courseNo.startsWith("2232") && "Y".equals(isChild)) {
				sum[20] += startBalance;
			}
			// 其他应付款
			if (courseNo.startsWith("2241") && "Y".equals(isChild)) {
				sum[21] += startBalance;
			}
			// 长期借款
			if (courseNo.startsWith("2501") && "Y".equals(isChild)) {
				sum[22] += startBalance;
			}
			// 长期应付款
			if (courseNo.startsWith("2701") && "Y".equals(isChild)) {
				sum[23] += startBalance;
			}
			// 递延收益
			if (courseNo.startsWith("2401") && "Y".equals(isChild)) {
				sum[24] += startBalance;
			}
			// 长期债券投资
			if (courseNo.startsWith("1501") && "Y".equals(isChild)) {
				sum[25] += startBalance;
			}
			// 长期股权投资
			if (courseNo.startsWith("1511") && "Y".equals(isChild)) {
				sum[26] += startBalance;
			}
			// 固定资产原价
			if (courseNo.startsWith("1601") && "Y".equals(isChild)) {
				sum[27] += startBalance;
			}
			// 累计折旧
			if (courseNo.startsWith("1602") && "Y".equals(isChild)) {
				sum[28] += startBalance;
			}
			// 在建工程
			if (courseNo.startsWith("1604") && "Y".equals(isChild)) {
				sum[29] += startBalance;
			}
			// 工程物资
			if (courseNo.startsWith("1605") && "Y".equals(isChild)) {
				sum[30] += startBalance;
			}
			// 固定资产清理
			if (courseNo.startsWith("1606") && "Y".equals(isChild)) {
				sum[31] += startBalance;
			}
			// 生产性生物资产
			if (courseNo.startsWith("1621") && "Y".equals(isChild)) {
				sum[32] += startBalance;
			}
			// 生产性生物资产折旧
			if (courseNo.startsWith("1622") && "Y".equals(isChild)) {
				sum[33] += startBalance;
			}
			// 无形资产
			if (courseNo.startsWith("1701") && "Y".equals(isChild)) {
				sum[34] += startBalance;
			}
			// 累计摊销
			if (courseNo.startsWith("1702") && "Y".equals(isChild)) {
				sum[35] += startBalance;
			}
			// 开发支出
			if (courseNo.startsWith("5301") && "Y".equals(isChild)) {
				sum[36] += startBalance;
			}
			// 长期待摊费用
			if (courseNo.startsWith("1801") && "Y".equals(isChild)) {
				sum[37] += startBalance;
			}
			// 其他非流动资产
			if (courseNo.startsWith("190102") && "Y".equals(isChild)) {
				sum[38] += startBalance;
			}
			// 实收资本（或股本）
			if (courseNo.startsWith("4001") && "Y".equals(isChild)) {
				sum[39] += startBalance;
			}
			// 资本公积
			if (courseNo.startsWith("4002") && "Y".equals(isChild)) {
				sum[40] += startBalance;
			}
			// 盈余公积
			if (courseNo.startsWith("4101") && "Y".equals(isChild)) {
				sum[41] += startBalance;
			}
			// 未分配利润
			if ((courseNo.startsWith("4103") || courseNo.startsWith("41015")) && "Y".equals(isChild)) {
				sum[42] += startBalance;
			}
			// 货币资金
			entity.setMonetaryResources(sum[0]);
			// 短期投资
			entity.setShortTermInvestment(sum[1]);
			// 应收票据
			entity.setBillReceivable(sum[2]);
			// 应收账款
			if (sum[4] < 0) {
				entity.setAccountsReceivable(sum[3] - sum[4]);
			} else {
				entity.setAccountsReceivable(sum[3]);
			}
			// 预付款项
			if (sum[6] < 0) {
				entity.setAdvancePayment(sum[5] - sum[6]);
			} else {
				entity.setAdvancePayment(sum[5]);
			}
			// 应收股利
			entity.setDividendsPayable(sum[7]);
			// 应收利息
			entity.setInterestReceivable(sum[8]);
			// 其他应收款
			entity.setOtherReceivables(sum[9]);
			// 存货
			entity.setStock(sum[10] + sum[15]);
			// 原材料
			entity.setRawMaterial(sum[11]);
			// 在产品
			entity.setProduct(sum[12]);
			// 库存商品
			entity.setMerchandiseInventory(sum[13]);
			// 周转材料
			entity.setWorkingCapitalConstructionMaterials(sum[14]);
			// 其他流动资产
			entity.setOtherCurrentAssets(sum[16]);
			// 短期借款
			entity.setShortTermInvestment(sum[17]);
			// 应付票据
			entity.setNotesPayable(sum[18]);
			// 应付账款
			if (sum[5] < 0) {
				entity.setAccountsPayable(sum[5] - sum[6]);
			} else {
				entity.setAccountsPayable(sum[6]);
			}
			// 预收账款
			if (sum[3] < 0) {
				entity.setDepositReceived(sum[4] - sum[3]);
			} else {
				entity.setDepositReceived(sum[4]);
			}
			// 应付职工薪酬
			entity.setSalariesPayable(sum[17]);
			// 应交税费
			entity.setTaxPayable(sum[18]);
			// 应付利息
			entity.setInterestPayable(sum[19]);
			// 应付利润
			entity.setProfitPayable(sum[20]);
			// 其他应付款
			entity.setOtherPayment(sum[21]);
			// 长期借款
			entity.setLongTermBorrowing(sum[22]);
			// 长期应付款
			entity.setLongTermPayable(sum[23]);
			// 递延收益
			entity.setDeferredIncome(sum[24]);
			// 长期债券投资
			entity.setLongTermBondInvestment(sum[25]);
			// 长期股权投资
			entity.setLongTermEquityInvestment(sum[26]);
			// 固定资产原价
			entity.setOriginalPriceOfFixedAssets(sum[27]);
			// 累计折旧
			entity.setAccumulatedDepreciation(sum[28]);
			// 在建工程
			entity.setConstructionProject(sum[29]);
			// 工程物资
			entity.setEngineeringMaterials(sum[30]);
			// 固定资产清理
			entity.setFixedAssetsCleaning(sum[31]);
			// 生产性生物资产
			entity.setProductiveBiologicalAssets(sum[32] - sum[33]);
			// 无形资产
			entity.setIntangibleAssets(sum[34] - sum[35]);
			// 开发支出
			entity.setDevelopmentExpenditure(sum[36]);
			// 长期待摊费用
			entity.setLongTermApportionedCost(sum[37]);
			// 其他非流动资产
			entity.setOtherNonCurrentAssets(sum[38]);
			// 实收资本（或股本）
			entity.setCapitalCollection(sum[39]);
			// 资本公积
			entity.setCapitalStock(sum[40]);
			// 盈余公积
			entity.setEarnedSurplus(sum[41]);
			// 未分配利润
			entity.setUndistributedProfit(sum[42]);
			// 流动资产合计
			entity.setTotalCurrentAssets(entity.getMonetaryResources() + entity.getShortTermInvestment()
					+ entity.getBillReceivable() + entity.getAccountsReceivable() + entity.getAdvancePayment()
					+ entity.getDividendsPayable() + entity.getInterestReceivable() + entity.getOtherReceivables()
					+ entity.getStock() + entity.getOtherCurrentAssets());
			// 流动负债合计
			entity.setTotalCurrentLiabilities(
					entity.getShortTermBorrowing() + entity.getNotesPayable() + entity.getAccountsPayable()
							+ entity.getDepositReceived() + entity.getSalariesPayable() + entity.getTaxPayable()
							+ entity.getInterestPayable() + entity.getProfitPayable() + entity.getOtherPayment());
			// 非流动负债合计
			entity.setTotalNonCurrentLiabilities(
					entity.getLongTermBorrowing() + entity.getLongTermPayable() + entity.getDeferredIncome());
			// 负债合计
			entity.setBalanceOfLiabilities(
					entity.getTotalCurrentLiabilities() + entity.getTotalNonCurrentLiabilities());
			// 固定资产账面价值
			entity.setBookValueOfFixedAssets(
					entity.getOriginalPriceOfFixedAssets() - entity.getAccumulatedDepreciation());
			// 非流动资产合计
			entity.setTotalNonCurrentLiabilities(entity.getLongTermBondInvestment()
					+ entity.getLongTermEquityInvestment() + entity.getBookValueOfFixedAssets()
					+ entity.getConstructionProject() + entity.getEngineeringMaterials()
					+ entity.getFixedAssetsCleaning() + entity.getProductiveBiologicalAssets()
					+ entity.getIntangibleAssets() + entity.getDevelopmentExpenditure()
					+ entity.getLongTermApportionedCost() + entity.getOtherNonCurrentAssets());
			// 所有者权益合计
			entity.setProprietorEquity(entity.getCapitalCollection() + entity.getCapitalStock()
					+ entity.getEarnedSurplus() + entity.getUndistributedProfit());
			// 负债和所有者权益合计
			entity.setTotalLiabilitiesAndCapital(entity.getProprietorEquity() + entity.getBalanceOfLiabilities());
			// 资产总计
			entity.setTotalAssets(entity.getTotalCurrentAssets() + entity.getTotalNonCurrentLiabilities());
		}
		message.setData(entity);
		return message;
	}

	private boolean deleteBeforeCheck(String areaCode) {
		return true;
	}

}
