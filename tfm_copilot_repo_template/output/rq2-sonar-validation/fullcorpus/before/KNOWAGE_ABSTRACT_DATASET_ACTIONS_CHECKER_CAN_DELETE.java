public class KNOWAGE_ABSTRACT_DATASET_ACTIONS_CHECKER_CAN_DELETE {
@Override
public void canDelete() throws ActionNotPermittedException {
    IDataSet dataSet = getDataset();
    IDomainDAO domainDAO = DAOFactory.getDomainDAO();
    boolean isAdmin = UserUtilities.hasAdministratorRole(userProfile);
    boolean isDeveloper = UserUtilities.hasDeveloperRole(userProfile);
    boolean isUser = UserUtilities.hasUserRole(userProfile);
    boolean isTester = UserUtilities.hasTesterRole(userProfile);
    boolean isModelAdministrator = UserUtilities.hasModelAdminRole(userProfile);
    Object userId = userProfile.getUserUniqueIdentifier();
    SbiDomains scopeEnterprise = null;
    SbiDomains scopeTechnical = null;
    SbiDomains scopeUser = null;
    try {
        scopeEnterprise = domainDAO.loadSbiDomainByCodeAndValue("DS_SCOPE", SpagoBIConstants.DS_SCOPE_ENTERPRISE);
        scopeTechnical = domainDAO.loadSbiDomainByCodeAndValue("DS_SCOPE", SpagoBIConstants.DS_SCOPE_TECHNICAL);
        scopeUser = domainDAO.loadSbiDomainByCodeAndValue("DS_SCOPE", SpagoBIConstants.DS_SCOPE_USER);
    } catch (EMFUserError e) {
        datasetNotVisible();
    }
    List<Integer> categories = UserUtilities.getDataSetCategoriesByUser(userProfile).stream().map(Domain::getValueId).collect(Collectors.toList());
    String currentOwner = dataSet.getOwner();
    String currentScope = dataSet.getScopeCd();
    Integer currentCategory = dataSet.getCategoryId();
    boolean owned = currentOwner.equals(userId);
    boolean isScopeEnterprise = scopeEnterprise.getValueCd().equals(currentScope);
    boolean isScopeTechnical = scopeTechnical.getValueCd().equals(currentScope);
    boolean isScopeUser = scopeUser.getValueCd().equals(currentScope);
    boolean inVisibleCategories = categories.contains(currentCategory);
    if (isAdmin) {
        // All dataset
    } else if (isDeveloper) {
        // @formatter:off
        if (!owned && !(isScopeEnterprise && inVisibleCategories) && !(isScopeTechnical && inVisibleCategories) && !(isScopeUser && inVisibleCategories)) {
            datasetNotVisible();
        }
        // @formatter:on
    } else if (isUser || isTester || isModelAdministrator) {
        // @formatter:off
        if (!owned && !(isScopeEnterprise && inVisibleCategories) && !(isScopeTechnical) && !(isScopeUser && inVisibleCategories)) {
            datasetNotVisible();
        }
        // @formatter:on
    }
}
}

