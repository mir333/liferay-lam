package nl.finalist.liferay.lam.builder

import nl.finalist.liferay.lam.api.*
import nl.finalist.liferay.lam.builder.factory.*

class CreateFactoryBuilder extends FactoryBuilderSupport {

    CreateFactoryBuilder(CustomFields customFieldsService, Vocabulary vocabularyService, Site siteService, Category
            categoryService, UserGroups userGroupsService, RoleAndPermissions roleAndPermissionsService, Page
            pageService, Tag tagService,ForumCategory forumCategory, User userService) {

        registerFactory("customField", new CreateCustomFieldsFactory(customFieldsService));
        registerFactory("vocabulary", new CreateVocabularyFactory(vocabularyService));
        registerFactory("site", new CreateSiteFactory(siteService))
        registerFactory("category", new CreateCategoryFactory(categoryService));
        registerFactory("userGroup", new CreateUserGroupFactory(userGroupsService));
        registerFactory("role", new CreateRoleAndPermissionsFactory(roleAndPermissionsService));
        registerFactory("page", new CreatePageFactory(pageService));
        registerFactory("tag", new CreateTagFactory(tagService));
        registerFactory("forumCategory", new CreateForumCategoryFactory(forumCategory));

        registerFactory("user", new CreateUserFactory(userService));
    }

}
