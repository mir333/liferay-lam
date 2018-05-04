package nl.finalist.liferay.lam.dslglue;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import nl.finalist.liferay.lam.api.*;
import nl.finalist.liferay.lam.builder.*;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.customizers.ImportCustomizer;
import org.osgi.framework.Bundle;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import java.io.Reader;



/**
 * Executor that evaluates configured scripts using a context containing all
 * available APIs.
 */
@Component(immediate = true, service = Executor.class)
public class DslExecutor implements Executor {

    private static final Log LOG = LogFactoryUtil.getLog(DslExecutor.class);

    @Reference
    private CustomFields customFieldsService;
    @Reference
    private PortalSettings portalSettingsService;
    @Reference
    private Vocabulary vocabularyService;
    @Reference
    private PortalProperties portalPropertiesService;
    @Reference
    private Site siteService;
    @Reference
    private Category categoryService;
    @Reference
    private RoleAndPermissions roleAndPermissionsService;
    @Reference
    private UserGroups userGroupsService;
    @Reference
    private Structure structureService;
    @Reference
    private Template templateService;
    @Reference
    private ADT adtService;
    @Reference
    private Page pageService;
    @Reference
    private WebContent webContentService;
    @Reference
    private User userService;
    @Reference
    private Tag tagService;
    @Reference
    private ForumCategory forumCategoryService;
    @Reference
    private PortletPreferences portletPreferences;

    @Activate
    public void activate() {
        LOG.debug("Bundle Activate DslExecutor");
    }

    @Override
    public void runScripts(Bundle bundle, Reader... scripts) {
        LOG.debug("DSL Executor running the available scripts");

        Binding sharedData = new Binding();

        // Add all available API classes to the context of the scripts
        sharedData.setVariable("LOG", LOG);

        sharedData.setVariable("create", new CreateFactoryBuilder(customFieldsService, vocabularyService,
                siteService, categoryService, userGroupsService, roleAndPermissionsService, pageService,
                tagService, forumCategoryService, userService));

        sharedData.setVariable("update", new UpdateFactoryBuilder(portalSettingsService, vocabularyService,
                        siteService, categoryService, userService));
        sharedData.setVariable("validate", new ValidateFactoryBuilder(portalPropertiesService));
        sharedData.setVariable("delete", new DeleteFactoryBuilder(customFieldsService, vocabularyService,
                        siteService, categoryService, webContentService, tagService, userService));
        sharedData.setVariable("createOrUpdate", new CreateOrUpdateFactoryBuilder(structureService,templateService,
                        adtService, webContentService, portletPreferences, bundle));

        sharedData.setVariable("Roles", new Roles());
        sharedData.setVariable("Entities", new Entities());
        sharedData.setVariable("ActionKeys", new ActionKeys());
        sharedData.setVariable("Templates", new Templates());
        sharedData.setVariable("ADTTypes", new ADTTypes());

        CompilerConfiguration conf = new CompilerConfiguration();
        ImportCustomizer imports = new ImportCustomizer();

        imports.addImport("TypeOfRole", "nl.finalist.liferay.lam.api.TypeOfRole");
        imports.addImport("CustomFieldType", "nl.finalist.liferay.lam.dslglue.CustomFieldType");
        imports.addImport("DisplayType", "nl.finalist.liferay.lam.dslglue.DisplayType");


        // Make these imports available to the scripts

        conf.addCompilationCustomizers(imports);
        // Use the classloader of this class
        ClassLoader classLoader = this.getClass().getClassLoader();

        GroovyShell shell = new GroovyShell(classLoader, sharedData, conf);

        for (Reader script : scripts) {
            shell.evaluate(script);
        }
    }

}
