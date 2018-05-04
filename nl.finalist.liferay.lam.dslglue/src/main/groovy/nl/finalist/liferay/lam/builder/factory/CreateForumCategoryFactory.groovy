package nl.finalist.liferay.lam.builder.factory

import nl.finalist.liferay.lam.api.ForumCategory
import nl.finalist.liferay.lam.dslglue.model.ForumCategoryModel

class CreateForumCategoryFactory extends AbstractFactory {

    ForumCategory forumCategoryService;

    CreateForumCategoryFactory(ForumCategory forumCategoryService) {
        this.forumCategoryService = forumCategoryService;
    }

    @Override
    Object newInstance(FactoryBuilderSupport builder, Object objectName, Object value, Map attributes)
            throws InstantiationException, IllegalAccessException {
        new ForumCategoryModel(attributes);
    }

    @Override
    void onNodeCompleted(FactoryBuilderSupport builder, Object parent, Object node) {
        super.onNodeCompleted(builder, parent, node);
        ForumCategoryModel model = (ForumCategoryModel) node;
        forumCategoryService.createForumCategory(model.siteKey, model.parent, model.name, model.description, model.displayStyle);
    }
}
