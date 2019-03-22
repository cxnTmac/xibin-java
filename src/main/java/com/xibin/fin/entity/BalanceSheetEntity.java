package com.xibin.fin.entity;

import java.io.Serializable;

public class BalanceSheetEntity implements Serializable {
	// 货币资金
	private Double monetaryResources;
	// 短期投资
	private Double shortTermInvestment;
	// 应收票据
	private Double billReceivable;
	// 应收账款
	private Double accountsReceivable;
	// 预付款项
	private Double advancePayment;
	// 应收股利
	private Double dividendsPayable;
	// 应收利息
	private Double interestReceivable;
	// 其他应收款
	private Double otherReceivables;
	// 存货
	private Double stock;
	// 原材料
	private Double rawMaterial;
	// 在产品
	private Double product;
	// 库存商品
	private Double merchandiseInventory;
	// 周转材料
	private Double workingCapitalConstructionMaterials;
	// 其他流动资产
	private Double otherCurrentAssets;
	// 短期借款
	private Double shortTermBorrowing;
	// 应付票据
	private Double notesPayable;
	// 应付账款
	private Double accountsPayable;
	// 预收账款
	private Double depositReceived;
	// 应付职工薪酬
	private Double salariesPayable;
	// 应交税费
	private Double taxPayable;
	// 应付利息
	private Double interestPayable;
	// 应付利润
	private Double profitPayable;
	// 其他应付款
	private Double otherPayment;
	// 长期借款
	private Double longTermBorrowing;
	// 长期应付款
	private Double longTermPayable;
	// 递延收益
	private Double deferredIncome;
	// 长期债券投资
	private Double longTermBondInvestment;
	// 长期股权投资
	private Double longTermEquityInvestment;
	// 固定资产原价
	private Double originalPriceOfFixedAssets;
	// 累计折旧
	private Double accumulatedDepreciation;
	// 在建工程
	private Double constructionProject;
	// 工程物资
	private Double engineeringMaterials;
	// 固定资产清理
	private Double fixedAssetsCleaning;
	// 生产性生物资产
	private Double productiveBiologicalAssets;
	// 无形资产
	private Double intangibleAssets;
	// 开发支出
	private Double developmentExpenditure;
	// 长期待摊费用
	private Double longTermApportionedCost;
	// 其他非流动资产
	private Double otherNonCurrentAssets;
	// 实收资本（或股本）
	private Double capitalCollection;
	// 资本公积
	private Double capitalStock;
	// 盈余公积
	private Double earnedSurplus;
	// 未分配利润
	private Double undistributedProfit;
	// 流动资产合计
	private Double totalCurrentAssets;
	// 流动负债合计
	private Double totalCurrentLiabilities;
	// 非流动负债合计
	private Double totalNonCurrentLiabilities;
	// 负债合计
	private Double balanceOfLiabilities;
	// 固定资产账面价值
	private Double bookValueOfFixedAssets;
	// 非流动资产合计
	private Double totalNonCurrentAssets;
	// 所有者权益合计
	private Double proprietorEquity;
	// 负债和所有者权益合计
	private Double totalLiabilitiesAndCapital;
	// 资产总计
	private Double totalAssets;

	public Double getMonetaryResources() {
		return monetaryResources;
	}

	public void setMonetaryResources(Double monetaryResources) {
		this.monetaryResources = monetaryResources;
	}

	public Double getShortTermInvestment() {
		return shortTermInvestment;
	}

	public void setShortTermInvestment(Double shortTermInvestment) {
		this.shortTermInvestment = shortTermInvestment;
	}

	public Double getBillReceivable() {
		return billReceivable;
	}

	public void setBillReceivable(Double billReceivable) {
		this.billReceivable = billReceivable;
	}

	public Double getAccountsReceivable() {
		return accountsReceivable;
	}

	public void setAccountsReceivable(Double accountsReceivable) {
		this.accountsReceivable = accountsReceivable;
	}

	public Double getAdvancePayment() {
		return advancePayment;
	}

	public void setAdvancePayment(Double advancePayment) {
		this.advancePayment = advancePayment;
	}

	public Double getDividendsPayable() {
		return dividendsPayable;
	}

	public void setDividendsPayable(Double dividendsPayable) {
		this.dividendsPayable = dividendsPayable;
	}

	public Double getInterestReceivable() {
		return interestReceivable;
	}

	public void setInterestReceivable(Double interestReceivable) {
		this.interestReceivable = interestReceivable;
	}

	public Double getOtherReceivables() {
		return otherReceivables;
	}

	public void setOtherReceivables(Double otherReceivables) {
		this.otherReceivables = otherReceivables;
	}

	public Double getStock() {
		return stock;
	}

	public void setStock(Double stock) {
		this.stock = stock;
	}

	public Double getRawMaterial() {
		return rawMaterial;
	}

	public void setRawMaterial(Double rawMaterial) {
		this.rawMaterial = rawMaterial;
	}

	public Double getProduct() {
		return product;
	}

	public void setProduct(Double product) {
		this.product = product;
	}

	public Double getMerchandiseInventory() {
		return merchandiseInventory;
	}

	public void setMerchandiseInventory(Double merchandiseInventory) {
		this.merchandiseInventory = merchandiseInventory;
	}

	public Double getWorkingCapitalConstructionMaterials() {
		return workingCapitalConstructionMaterials;
	}

	public void setWorkingCapitalConstructionMaterials(Double workingCapitalConstructionMaterials) {
		this.workingCapitalConstructionMaterials = workingCapitalConstructionMaterials;
	}

	public Double getOtherCurrentAssets() {
		return otherCurrentAssets;
	}

	public void setOtherCurrentAssets(Double otherCurrentAssets) {
		this.otherCurrentAssets = otherCurrentAssets;
	}

	public Double getShortTermBorrowing() {
		return shortTermBorrowing;
	}

	public void setShortTermBorrowing(Double shortTermBorrowing) {
		this.shortTermBorrowing = shortTermBorrowing;
	}

	public Double getNotesPayable() {
		return notesPayable;
	}

	public void setNotesPayable(Double notesPayable) {
		this.notesPayable = notesPayable;
	}

	public Double getAccountsPayable() {
		return accountsPayable;
	}

	public void setAccountsPayable(Double accountsPayable) {
		this.accountsPayable = accountsPayable;
	}

	public Double getDepositReceived() {
		return depositReceived;
	}

	public void setDepositReceived(Double depositReceived) {
		this.depositReceived = depositReceived;
	}

	public Double getSalariesPayable() {
		return salariesPayable;
	}

	public void setSalariesPayable(Double salariesPayable) {
		this.salariesPayable = salariesPayable;
	}

	public Double getTaxPayable() {
		return taxPayable;
	}

	public void setTaxPayable(Double taxPayable) {
		this.taxPayable = taxPayable;
	}

	public Double getInterestPayable() {
		return interestPayable;
	}

	public void setInterestPayable(Double interestPayable) {
		this.interestPayable = interestPayable;
	}

	public Double getProfitPayable() {
		return profitPayable;
	}

	public void setProfitPayable(Double profitPayable) {
		this.profitPayable = profitPayable;
	}

	public Double getOtherPayment() {
		return otherPayment;
	}

	public void setOtherPayment(Double otherPayment) {
		this.otherPayment = otherPayment;
	}

	public Double getLongTermBorrowing() {
		return longTermBorrowing;
	}

	public void setLongTermBorrowing(Double longTermBorrowing) {
		this.longTermBorrowing = longTermBorrowing;
	}

	public Double getLongTermPayable() {
		return longTermPayable;
	}

	public void setLongTermPayable(Double longTermPayable) {
		this.longTermPayable = longTermPayable;
	}

	public Double getDeferredIncome() {
		return deferredIncome;
	}

	public void setDeferredIncome(Double deferredIncome) {
		this.deferredIncome = deferredIncome;
	}

	public Double getLongTermBondInvestment() {
		return longTermBondInvestment;
	}

	public void setLongTermBondInvestment(Double longTermBondInvestment) {
		this.longTermBondInvestment = longTermBondInvestment;
	}

	public Double getLongTermEquityInvestment() {
		return longTermEquityInvestment;
	}

	public void setLongTermEquityInvestment(Double longTermEquityInvestment) {
		this.longTermEquityInvestment = longTermEquityInvestment;
	}

	public Double getOriginalPriceOfFixedAssets() {
		return originalPriceOfFixedAssets;
	}

	public void setOriginalPriceOfFixedAssets(Double originalPriceOfFixedAssets) {
		this.originalPriceOfFixedAssets = originalPriceOfFixedAssets;
	}

	public Double getAccumulatedDepreciation() {
		return accumulatedDepreciation;
	}

	public void setAccumulatedDepreciation(Double accumulatedDepreciation) {
		this.accumulatedDepreciation = accumulatedDepreciation;
	}

	public Double getConstructionProject() {
		return constructionProject;
	}

	public void setConstructionProject(Double constructionProject) {
		this.constructionProject = constructionProject;
	}

	public Double getEngineeringMaterials() {
		return engineeringMaterials;
	}

	public void setEngineeringMaterials(Double engineeringMaterials) {
		this.engineeringMaterials = engineeringMaterials;
	}

	public Double getFixedAssetsCleaning() {
		return fixedAssetsCleaning;
	}

	public void setFixedAssetsCleaning(Double fixedAssetsCleaning) {
		this.fixedAssetsCleaning = fixedAssetsCleaning;
	}

	public Double getProductiveBiologicalAssets() {
		return productiveBiologicalAssets;
	}

	public void setProductiveBiologicalAssets(Double productiveBiologicalAssets) {
		this.productiveBiologicalAssets = productiveBiologicalAssets;
	}

	public Double getIntangibleAssets() {
		return intangibleAssets;
	}

	public void setIntangibleAssets(Double intangibleAssets) {
		this.intangibleAssets = intangibleAssets;
	}

	public Double getDevelopmentExpenditure() {
		return developmentExpenditure;
	}

	public void setDevelopmentExpenditure(Double developmentExpenditure) {
		this.developmentExpenditure = developmentExpenditure;
	}

	public Double getLongTermApportionedCost() {
		return longTermApportionedCost;
	}

	public void setLongTermApportionedCost(Double longTermApportionedCost) {
		this.longTermApportionedCost = longTermApportionedCost;
	}

	public Double getOtherNonCurrentAssets() {
		return otherNonCurrentAssets;
	}

	public void setOtherNonCurrentAssets(Double otherNonCurrentAssets) {
		this.otherNonCurrentAssets = otherNonCurrentAssets;
	}

	public Double getCapitalCollection() {
		return capitalCollection;
	}

	public void setCapitalCollection(Double capitalCollection) {
		this.capitalCollection = capitalCollection;
	}

	public Double getCapitalStock() {
		return capitalStock;
	}

	public void setCapitalStock(Double capitalStock) {
		this.capitalStock = capitalStock;
	}

	public Double getEarnedSurplus() {
		return earnedSurplus;
	}

	public void setEarnedSurplus(Double earnedSurplus) {
		this.earnedSurplus = earnedSurplus;
	}

	public Double getUndistributedProfit() {
		return undistributedProfit;
	}

	public void setUndistributedProfit(Double undistributedProfit) {
		this.undistributedProfit = undistributedProfit;
	}

	public Double getTotalCurrentAssets() {
		return totalCurrentAssets;
	}

	public void setTotalCurrentAssets(Double totalCurrentAssets) {
		this.totalCurrentAssets = totalCurrentAssets;
	}

	public Double getTotalCurrentLiabilities() {
		return totalCurrentLiabilities;
	}

	public void setTotalCurrentLiabilities(Double totalCurrentLiabilities) {
		this.totalCurrentLiabilities = totalCurrentLiabilities;
	}

	public Double getTotalNonCurrentLiabilities() {
		return totalNonCurrentLiabilities;
	}

	public void setTotalNonCurrentLiabilities(Double totalNonCurrentLiabilities) {
		this.totalNonCurrentLiabilities = totalNonCurrentLiabilities;
	}

	public Double getBalanceOfLiabilities() {
		return balanceOfLiabilities;
	}

	public void setBalanceOfLiabilities(Double balanceOfLiabilities) {
		this.balanceOfLiabilities = balanceOfLiabilities;
	}

	public Double getBookValueOfFixedAssets() {
		return bookValueOfFixedAssets;
	}

	public void setBookValueOfFixedAssets(Double bookValueOfFixedAssets) {
		this.bookValueOfFixedAssets = bookValueOfFixedAssets;
	}

	public Double getTotalNonCurrentAssets() {
		return totalNonCurrentAssets;
	}

	public void setTotalNonCurrentAssets(Double totalNonCurrentAssets) {
		this.totalNonCurrentAssets = totalNonCurrentAssets;
	}

	public Double getProprietorEquity() {
		return proprietorEquity;
	}

	public void setProprietorEquity(Double proprietorEquity) {
		this.proprietorEquity = proprietorEquity;
	}

	public Double getTotalLiabilitiesAndCapital() {
		return totalLiabilitiesAndCapital;
	}

	public void setTotalLiabilitiesAndCapital(Double totalLiabilitiesAndCapital) {
		this.totalLiabilitiesAndCapital = totalLiabilitiesAndCapital;
	}

	public Double getTotalAssets() {
		return totalAssets;
	}

	public void setTotalAssets(Double totalAssets) {
		this.totalAssets = totalAssets;
	}

}
