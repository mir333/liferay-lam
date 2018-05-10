package nl.finalist.liferay.lam.api;

import java.util.Locale;
import java.util.Map;

public interface Vocabulary {

    /**
     * Adds a vocabulary to the Global group/site
     *
     * @param siteKey   The String siteKey that will be used to place the vocabulary in the right side.
     *                  If not provided the vocabulary will be placed in default space.
     * @param vocabularyName
     *            Map of the names of the vocabulary with the Locale
     */
    public void addVocabulary(String siteKey, Map<Locale, String> vocabularyName);


    /**
     * Deletes a vocabulary if it exists in the Global site
     *
     * @param siteKey   The String siteKey that will be used to place the vocabulary in the right side.
     *                  If not provided the vocabulary will be placed in default space.
     * @param existingName
     *            The name of the vocabulary
     */
    public void deleteVocabulary(String siteKey, String existingName);


    /**
     * Updates the name of the Vocabulary created in the Global site

     * @param siteKey   The String siteKey that will be used to place the vocabulary in the right side.
     *                  If not provided the vocabulary will be placed in default space.
     * @param updateVocabularyName
     *            The updated names of the vocabulary
     */
    public void updateVocabularyTranslation(String siteKey, String existingName,Map<Locale, String> updateVocabularyName);
}
