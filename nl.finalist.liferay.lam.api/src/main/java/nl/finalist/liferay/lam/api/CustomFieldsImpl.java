package nl.finalist.liferay.lam.api;

import java.util.Arrays;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.liferay.expando.kernel.exception.NoSuchTableException;
import com.liferay.expando.kernel.model.ExpandoColumn;
import com.liferay.expando.kernel.model.ExpandoColumnConstants;
import com.liferay.expando.kernel.model.ExpandoTable;
import com.liferay.expando.kernel.model.ExpandoTableConstants;
import com.liferay.expando.kernel.service.ExpandoColumnLocalService;
import com.liferay.expando.kernel.service.ExpandoRowLocalService;
import com.liferay.expando.kernel.service.ExpandoTableLocalService;
import com.liferay.expando.kernel.service.ExpandoValueLocalService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.UnicodeProperties;

/**
 * Implementation for {@link nl.finalist.liferay.lam.api.CustomFields}
 */
@Component(immediate = true, service=CustomFields.class)
public class CustomFieldsImpl implements CustomFields {

    @Reference
    private ExpandoTableLocalService tableService;
    @Reference
    private ExpandoColumnLocalService columnService;
    @Reference
    private ExpandoRowLocalService rowService;
    @Reference
    private ExpandoValueLocalService valueService;

    @Reference
    private RoleLocalService roleService;

    @Reference
    private ResourcePermissionLocalService resourcePermissionService;

    private static final Log LOG = LogFactoryUtil.getLog(CustomFieldsImpl.class);


    
    private void addCustomTextField(long companyId, String entityName, String fieldName, String defaultValue, String [] roles) {
        LOG.debug(String.format("Start adding custom text field %s to entity %s with default value %s and roles %s", fieldName, entityName, defaultValue, Arrays.toString(roles)));

        ExpandoTable expandoTable = getOrAddExpandoTable(companyId, entityName, ExpandoTableConstants.DEFAULT_TABLE_NAME);
        LOG.debug("Expando Table ID : " + expandoTable.getTableId());

        ExpandoColumn expandoColumn = getOrAddExpandoTextColumn(companyId, entityName, ExpandoTableConstants.DEFAULT_TABLE_NAME, fieldName, expandoTable);
        saveDefaultValueForColumn(defaultValue, expandoColumn);
        for (String role : roles) {
            addExpandoPermissions(companyId, expandoColumn, role);
        }
        LOG.debug("Expando Column ID : " + expandoColumn.getColumnId());

        LOG.info(String.format("Added custom text field %s to entity %s with default value %s and roles %s", fieldName, entityName, defaultValue, Arrays.toString(roles)));
    }

    @Override
    public void addCustomTextField(String entityName, String fieldName, String defaultValue, String[] roles) {
        this.addCustomTextField(PortalUtil.getDefaultCompanyId(), entityName, fieldName, defaultValue, roles);
    }

    
    private void addCustomIntegerField(long companyId, String entityName, String fieldName, int defaultValue, String[] roles) {
        LOG.debug(String.format("Start adding custom integer field %s to entity %s with default value %s",
                        fieldName, entityName, defaultValue));

        ExpandoTable expandoTable = getOrAddExpandoTable(companyId, entityName, ExpandoTableConstants.DEFAULT_TABLE_NAME);
        LOG.debug("Expando Table ID : " + expandoTable.getTableId());

        ExpandoColumn expandoColumn = getOrAddExpandoIntegerColumn(companyId, entityName, ExpandoTableConstants.DEFAULT_TABLE_NAME, fieldName, expandoTable);
        saveDefaultValueForColumn(String.valueOf(defaultValue), expandoColumn);
        for (String role : roles) {
            addExpandoPermissions(companyId, expandoColumn, role);
        }
        LOG.debug("Expando Column ID : " + expandoColumn.getColumnId());

        LOG.info(String.format("Added custom integer field %s to entity %s with default value %s and roles %s", fieldName, entityName, defaultValue, Arrays.toString(roles)));
    }

    @Override
    public void addCustomIntegerField(String entityName, String fieldName, int defaultValue, String[] roles) {
        this.addCustomIntegerField(PortalUtil.getDefaultCompanyId(), entityName, fieldName, defaultValue, roles);
    }

    @Override
    public void addCustomTextArrayField(String entityName, String fieldName, String possibleValues, String[] roles, String displayType) {
        Long companyId = PortalUtil.getDefaultCompanyId();
        LOG.debug(String.format("Start adding custom text array field %s to entity %s with default value %s and roles %s", fieldName, entityName, possibleValues, Arrays.toString(roles)));

        ExpandoTable expandoTable = getOrAddExpandoTable(companyId, entityName, ExpandoTableConstants.DEFAULT_TABLE_NAME);
        LOG.debug("Expando Table ID : " + expandoTable.getTableId());

        ExpandoColumn expandoColumn = getOrAddExpandoTextArrayColumn(companyId, entityName, ExpandoTableConstants.DEFAULT_TABLE_NAME, fieldName, expandoTable);
        saveDefaultValueForColumn(possibleValues, expandoColumn);

        UnicodeProperties properties = expandoColumn.getTypeSettingsProperties();
        properties.setProperty("display-type", displayType);
        columnService.updateExpandoColumn(expandoColumn);

        // possible display-types: checkbox, radio, selection-list, text-box
        for (String role : roles) {
            addExpandoPermissions(companyId, expandoColumn, role);
        }
        LOG.debug("Expando Column ID : " + expandoColumn.getColumnId());

        LOG.info(String.format("Added custom text array field %s to entity %s with possible values [%s] and roles %s", fieldName, entityName, possibleValues, Arrays.toString(roles)));

    }
  
    private void deleteCustomField(long companyId, String entityName, String fieldName) {
        LOG.debug(String.format("Start deleting custom field %s of entity %s ", fieldName, entityName));

        try {
            ExpandoTable expandoTable = tableService.getDefaultTable(companyId, entityName);

            ExpandoColumn exandoColumn = columnService.getColumn(companyId, entityName, ExpandoTableConstants.DEFAULT_TABLE_NAME, fieldName);
            if (exandoColumn != null) {
                columnService.deleteExpandoColumn(exandoColumn);
            } else {
                LOG.debug("No column to delete");
            }

            tableService.deleteExpandoTable(expandoTable);
        } catch (NoSuchTableException e) {
            LOG.debug("No table to delete");
        } catch (PortalException e) {
            LOG.error(e);
        }

        LOG.info(String.format("Deleted custom field %s of entity %s ", fieldName, entityName));
    }

    @Override
    public void deleteCustomField(String entityName, String fieldName){
        deleteCustomField(PortalUtil.getDefaultCompanyId(), entityName, fieldName);
    }

    @Override
    public void addCustomFieldValue(String entityName, String fieldName, long classPK, String content) {
		try {
			valueService.addValue(PortalUtil.getDefaultCompanyId(), entityName, ExpandoTableConstants.DEFAULT_TABLE_NAME, fieldName, classPK, content);
		} catch (PortalException e) {
			LOG.error(e);
		}
		LOG.info(String.format("Adding value %s to custom field %s on entity %s with id %d", content, fieldName, entityName, classPK));
    }
    
    @Override
    public void updateCustomFieldValue(String entityName, String fieldName, long classPK, String content) {
		try {
			valueService.deleteValue(PortalUtil.getDefaultCompanyId(), entityName, ExpandoTableConstants.DEFAULT_TABLE_NAME, fieldName, classPK);
			valueService.addValue(PortalUtil.getDefaultCompanyId(), entityName, ExpandoTableConstants.DEFAULT_TABLE_NAME, fieldName, classPK, content);
		} catch (PortalException e) {
			LOG.error(e);
		}
    }

    private ExpandoTable getOrAddExpandoTable(long companyId, String className, String tableName) {
        ExpandoTable expandoTable = null;
        try {
            expandoTable = tableService.getDefaultTable(companyId, className);
        } catch (NoSuchTableException e) {
            LOG.debug("The table did not yet exist");
            try {
                expandoTable = tableService.addTable(companyId, className, tableName);
            } catch (Exception e1) {
                LOG.error(e);
            }
        } catch (Exception e) {
            LOG.error(e);
        }
        return expandoTable;
    }

    private void saveDefaultValueForColumn(String value, ExpandoColumn expandoColumn) {
        expandoColumn.setDefaultData(value);
        columnService.updateExpandoColumn(expandoColumn);
    }

    private ExpandoColumn getOrAddExpandoTextColumn(long companyId, String className, String tableName, String columnName,
                    ExpandoTable expandoTable) {
        return getOrAddExpandoColumn(companyId, className, tableName, columnName, expandoTable, ExpandoColumnConstants.STRING);
    }

    private ExpandoColumn getOrAddExpandoTextArrayColumn(long companyId, String className, String tableName, String columnName,
                                                    ExpandoTable expandoTable) {
        return getOrAddExpandoColumn(companyId, className, tableName, columnName, expandoTable, ExpandoColumnConstants.STRING_ARRAY);
    }

    private ExpandoColumn getOrAddExpandoIntegerColumn(long companyId, String className, String tableName, String columnName,
                    ExpandoTable expandoTable) {
        return getOrAddExpandoColumn(companyId, className, tableName, columnName, expandoTable, ExpandoColumnConstants.INTEGER);
    }

    private ExpandoColumn getOrAddExpandoColumn(long companyId, String className, String tableName, String columnName,
                    ExpandoTable expandoTable, int columnType) {
        ExpandoColumn exandoColumn = null;
        try {
            exandoColumn = columnService.getColumn(companyId, className, tableName, columnName);
            if (exandoColumn == null) {
                exandoColumn = columnService.addColumn(expandoTable.getTableId(), columnName,
                                columnType, defaultValue(columnType));
            }
        } catch (SystemException | PortalException e) {
            LOG.error(e);
        }

        return exandoColumn;
    }

    private Object defaultValue(int columnType) {
        switch (columnType) {
            case ExpandoColumnConstants.INTEGER:
                return Integer.valueOf(0);
            case ExpandoColumnConstants.STRING:
                return StringPool.BLANK;
            case ExpandoColumnConstants.STRING_ARRAY:
                return  StringPool.EMPTY_ARRAY;
            default:
                return StringPool.BLANK;
        }
    }

    private void addExpandoPermissions(long companyId, ExpandoColumn column, String role) {
        try {
            Role guestUserRole = roleService.getRole(companyId, role);
            LOG.debug("Guest role fetched: " + guestUserRole);
            if (guestUserRole != null) {
                // define actions
                String[] actionIds = new String[] { ActionKeys.VIEW, ActionKeys.UPDATE };
                // set the permission
                resourcePermissionService.setResourcePermissions(companyId,
                                ExpandoColumn.class.getName(), ResourceConstants.SCOPE_INDIVIDUAL,
                                String.valueOf(column.getColumnId()), guestUserRole.getRoleId(), actionIds);
                LOG.debug(role + " permissions set");
            }
        } catch (PortalException pe) {
            LOG.error(pe);
        }
    }
}