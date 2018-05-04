package nl.finalist.liferay.lam.api;

import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.service.JournalArticleLocalService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component
public class ScopeHelperImpl implements ScopeHelper {
    private static final Log LOG = LogFactoryUtil.getLog(ScopeHelperImpl.class);
    @Reference
    private DefaultValue defaultValue;

    @Reference
    private GroupLocalService groupService;

    @Reference
    private LayoutLocalService layoutService;

    @Reference
    private JournalArticleLocalService articleService;

    @Override
    public long getGroupIdWithFallback(String siteKey) {
        Group site = getGroup(siteKey);
        if(site != null){
            return site.getGroupId();
        }else {
            LOG.error(String.format("Site %s can not be found, returning default global group", siteKey));
            return defaultValue.getGlobalGroupId();
        }
    }

    @Override
    public Group getGroup(String siteKey) {
        Group result = null;
        if (!Validator.isBlank(siteKey)) {
            result = groupService.fetchGroup(defaultValue.getDefaultCompany().getCompanyId(), siteKey);
        }
        if(result == null){
            result = groupService.fetchFriendlyURLGroup(defaultValue.getDefaultCompany().getCompanyId(), siteKey);
        }
        return result;
    }

    @Override
    public Layout getLayout(long groupId, String pageFriendlyURL, Boolean privatePage) {
        Layout result = null;
        if (!Validator.isBlank(pageFriendlyURL) && privatePage != null) {
            result = layoutService.fetchLayoutByFriendlyURL(groupId, privatePage, pageFriendlyURL);
        }
        return result;
    }

    @Override
    public JournalArticle getArticleIdByGroupAndUrl(String siteKey, String titleUrl) {
        JournalArticle result = null;
        Group groupByName = getGroup(siteKey);
        if (groupByName != null && !Validator.isBlank(titleUrl)) {
            try {
                result = articleService.getLatestArticleByUrlTitle(groupByName.getGroupId(), titleUrl, WorkflowConstants.STATUS_APPROVED);
            } catch (PortalException e) {
                LOG.warn(String.format("Unable to find article for site %s and url title %s", siteKey, titleUrl));
            }
        }

        return result;
    }
}
