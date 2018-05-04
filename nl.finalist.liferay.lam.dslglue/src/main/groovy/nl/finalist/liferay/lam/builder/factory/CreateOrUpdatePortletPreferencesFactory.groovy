package nl.finalist.liferay.lam.builder.factory

import nl.finalist.liferay.lam.api.PortletPreferences
import nl.finalist.liferay.lam.dslglue.model.PortletPropertiesModel
import org.osgi.framework.Bundle

class CreateOrUpdatePortletPreferencesFactory extends AbstractFactory {

    PortletPreferences portletPreferencesService;
    Bundle bundle;

    CreateOrUpdatePortletPreferencesFactory(PortletPreferences portletPreferencesService, Bundle bundle) {
        this.portletPreferencesService = portletPreferencesService;
        this.bundle = bundle;
    }

    @Override
    Object newInstance(FactoryBuilderSupport builder, Object objectName, Object value, Map attributes)
            throws InstantiationException, IllegalAccessException {
        new PortletPropertiesModel(attributes);
    }

    @Override
    void onNodeCompleted(FactoryBuilderSupport builder, Object parent, Object node) {
        super.onNodeCompleted(builder, parent, node);
        PortletPropertiesModel model = (PortletPropertiesModel) node;


        portletPreferencesService.createOrUpdateTemplate(model.portletId, model.siteKey, model.pageFriendlyURL,
                model.privatePage, model.siteScope, model.properties);
    }
}
