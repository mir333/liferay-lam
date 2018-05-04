package nl.finalist.liferay.lam.api;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.service.PortletPreferencesLocalService;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.util.Validator;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.portlet.ReadOnlyException;
import java.util.Map;

@Component(immediate = true)
public class PortletPreferencesImpl implements PortletPreferences {
    private static final Log LOG = LogFactoryUtil.getLog(PortletPreferencesImpl.class);

    @Reference
    private PortletPreferencesLocalService preferencesService;

    @Reference
    private ScopeHelper scopeHelper;

    @Override
    public void createOrUpdateTemplate(String portletId, String siteKey, String pageFriendlyURL, Boolean privatePage, Boolean siteScope, Map<String, String> properties) {
        Group group = scopeHelper.getGroup(siteKey);
        if (group == null) {
            LOG.warn(String.format("No site found under %s.", siteKey));
            return;
        }
        Layout layout = scopeHelper.getLayout(group.getGroupId(), pageFriendlyURL, privatePage);
        if (layout == null) {
            LOG.warn(String.format("No page found for %s (page set:%s).",
                    pageFriendlyURL, privatePage));
            return;
        }

        javax.portlet.PortletPreferences portletPreferences;
        if (siteScope != null && siteScope) {
            portletPreferences = preferencesService.getPreferences(group.getCompanyId(), group.getGroupId(), PortletKeys.PREFS_OWNER_TYPE_GROUP, 0, portletId);
            portletPreferences = updatePreferences(portletPreferences, properties);
            preferencesService.updatePreferences(group.getGroupId(), PortletKeys.PREFS_OWNER_TYPE_GROUP, 0, portletId, portletPreferences);
        } else {
            portletPreferences = preferencesService.getPreferences(group.getCompanyId(), 0, PortletKeys.PREFS_OWNER_TYPE_LAYOUT, layout.getPlid(), portletId);
            portletPreferences = updatePreferences(portletPreferences, properties);
            preferencesService.updatePreferences(0, PortletKeys.PREFS_OWNER_TYPE_LAYOUT, layout.getPlid(), portletId, portletPreferences);
        }
    }

    private javax.portlet.PortletPreferences updatePreferences(javax.portlet.PortletPreferences portletPreferences, Map<String, String> properties) {
        for (Map.Entry<String, String> entry : properties.entrySet()) {
            try {
                String value = transformValues(entry);
                if (value != null) {
                    portletPreferences.setValue(entry.getKey(), value);
                }
            } catch (ReadOnlyException e) {
                LOG.warn(String.format("Could not set property: %s with value %s", entry.getKey(), entry.getValue()));
            }
        }
        return portletPreferences;
    }

    private String transformValues(Map.Entry<String, String> entry) {
        switch (entry.getKey()) {
            case "groupId":
                if (Validator.isDigit(entry.getValue())) {
                    return entry.getValue();
                } else {
                    Group group = scopeHelper.getGroup(entry.getValue());
                    return group == null ? null : String.valueOf(group.getGroupId());
                }
//            case "articleId":
//                if (Validator.isDigit(entry.getValue())) {
//                    return entry.getValue();
//                } else {
//                    String[] split = entry.getValue().split(StringPool.DOUBLE_DOLLAR);
//                    String siteKey = split[0];
//                    String articleUrl = split[1];
//                    JournalArticle articleIdByGroupAndUrl = scopeHelper.getArticleIdByGroupAndUrl(siteKey, articleUrl);
//                    return articleIdByGroupAndUrl == null ? null : String.valueOf(articleIdByGroupAndUrl.getArticleId());
//                }
            default:
                return entry.getValue();
        }
    }

}
