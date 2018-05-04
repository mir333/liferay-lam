package nl.finalist.liferay.lam.api;

import java.util.Map;

public interface PortletPreferences {
    public void createOrUpdateTemplate(String portletId, String siteKey, String pageFriendlyURL, Boolean privatePage,
                                       Boolean siteScope, Map<String, String> properties);
}
