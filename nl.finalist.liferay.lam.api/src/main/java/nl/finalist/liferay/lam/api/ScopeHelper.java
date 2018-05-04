package nl.finalist.liferay.lam.api;

import com.liferay.journal.model.JournalArticle;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;

public interface ScopeHelper {
    /**
     * Method will return group id that is associated with the given site name. If a match
     * is not found the default group ID is returned.
     *
     * @param siteKey Group (Site) name
     * @return given group id or default group id
     */
    long getGroupIdWithFallback(String siteKey);

    /**
     * Method will return group that is associated with the given site name.
     *
     * @param siteKey Group (Site) name
     * @return given group or null if site not found.
     */
    Group getGroup(String siteKey);


    /**
     * Method will return layout that is identified by the params.
     *
     * @param groupId Group id of the group that holds the page
     * @param pageFriendlyURL Page friendly url
     * @param privatePage page set indication private/public
     * @return layout on null
     */
    Layout getLayout(long groupId, String pageFriendlyURL, Boolean privatePage);

    /**
     * Method will return article Id that is identified by the params.
     *
     * @param siteKey Group (Site) name
     * @param titleUrl title of the article
     * @return journal article or null
     */
    JournalArticle getArticleIdByGroupAndUrl(String siteKey, String titleUrl);
}
