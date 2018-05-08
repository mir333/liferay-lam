package nl.finalist.liferay.lam.api;

import com.liferay.message.boards.kernel.model.MBCategory;
import com.liferay.message.boards.kernel.model.MBCategoryConstants;
import com.liferay.message.boards.kernel.service.MBCategoryLocalService;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.ProjectionFactoryUtil;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import java.util.List;
import java.util.Map;

@Component(immediate = true)
public class ForumCategoryImpl implements ForumCategory {
    private static final Log LOG = LogFactoryUtil.getLog(ForumCategoryImpl.class);
    @Reference
    private MBCategoryLocalService mbCategoryLocalService;
    @Reference
    private DefaultValue defaultValue;
    @Reference
    private CustomFields customFieldsService;
    @Reference
    private ScopeHelper scopeHelper;

    @Override
    public void createForumCategory(String siteKey, String parent, String name, String description,
                                    String displayStyle, Map<String, String> customFields) {
        long groupId = scopeHelper.getGroupIdWithFallback(siteKey);
        long categoryId = getParentCategory(groupId, parent);
        Long userId = defaultValue.getDefaultUserId();

        if (Validator.isBlank(displayStyle)) {
            displayStyle = MBCategoryConstants.DEFAULT_DISPLAY_STYLE;
        }

        ServiceContext serviceContext = new ServiceContext();
        serviceContext.setCompanyId(PortalUtil.getDefaultCompanyId());
        serviceContext.setScopeGroupId(groupId);

        try {
            MBCategory mbCategory = mbCategoryLocalService.addCategory(userId, categoryId, name, description,
                    displayStyle, null, null, null, 0,
                    false, null, null, 0, null,
                    false, null, 0, false, null, null,
                    false, false, serviceContext);

            if (customFields != null) {
                for (String fieldName : customFields.keySet()) {
                    customFieldsService.addCustomFieldValue(MBCategory.class.getName(), fieldName, mbCategory.getPrimaryKey(),
                            customFields.get(fieldName));
                    LOG.info(String.format("Custom field '%s' now has value '%s'", fieldName, customFields.get(fieldName)));
                }
            }

        } catch (PortalException e) {
            LOG.warn(String.format("Could not create forum category %s. Exception was %s.", name, e.getMessage()));
        }

    }

    private long getParentCategory(long groupId, String parent) {
        if (Validator.isBlank(parent)) {
            return 0;
        }

        DynamicQuery dynamicQuery = mbCategoryLocalService.dynamicQuery();
        dynamicQuery.setProjection(ProjectionFactoryUtil.property("categoryId"));
        dynamicQuery.add(RestrictionsFactoryUtil.eq("name", parent));
        dynamicQuery.add(RestrictionsFactoryUtil.eq("groupId", groupId));
        List<Long> parentIds = mbCategoryLocalService.dynamicQuery(dynamicQuery);

        if (parentIds.isEmpty()) {
            LOG.warn(String.format("Could not find the parent with name %s.", parent));
            return 0;
        } else if (parentIds.size() == 1) {
            return parentIds.get(0);
        } else {
            LOG.warn(String.format("Too many parents found for teh given name %s", parent));
            return 0;
        }
    }
}
