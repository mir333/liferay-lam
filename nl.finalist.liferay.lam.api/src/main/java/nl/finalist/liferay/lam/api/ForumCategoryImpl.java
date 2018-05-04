package nl.finalist.liferay.lam.api;

import com.liferay.message.boards.kernel.model.MBCategory;
import com.liferay.message.boards.kernel.model.MBCategoryConstants;
import com.liferay.message.boards.kernel.service.MBCategoryLocalService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(immediate = true)
public class ForumCategoryImpl implements ForumCategory {
    private static final Log LOG = LogFactoryUtil.getLog(ForumCategoryImpl.class);
    @Reference
    private MBCategoryLocalService mbCategoryLocalService;
    @Reference
    private DefaultValue defaultValue;

    @Reference
    private ScopeHelper scopeHelper;

    @Override
    public void createForumCategory(String siteKey, String parent, String name, String description, String displayStyle) {
        long groupId = scopeHelper.getGroupIdWithFallback(siteKey);
        long categoryId = getParentCategory(parent);
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
        } catch (PortalException e) {
            LOG.warn(String.format("Could not create forum category %s. Exception was %s.", name, e.getMessage()));
        }
    }

    private long getParentCategory(String parent) {
        return 0;
    }
}
