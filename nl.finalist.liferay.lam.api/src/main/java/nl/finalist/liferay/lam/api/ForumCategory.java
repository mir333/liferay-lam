package nl.finalist.liferay.lam.api;

public interface ForumCategory {

    /**
     *
     * @param siteKey
     * @param parent
     * @param name
     * @param description
     * @param displayStyle
     */
    void createForumCategory(String siteKey, String parent, String name, String description, String displayStyle);
}