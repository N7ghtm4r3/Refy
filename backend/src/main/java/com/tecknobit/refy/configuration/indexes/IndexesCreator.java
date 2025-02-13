package com.tecknobit.refy.configuration.indexes;

import com.tecknobit.equinoxcore.annotations.Assembler;
import com.tecknobit.equinoxcore.annotations.Wrapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.hibernate.Session;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Collection;
import java.util.List;

import static com.tecknobit.equinoxbackend.environment.services.builtin.service.EquinoxItemsHelper.*;

@Transactional
@Deprecated(since = "INTEGRATE THE OFFICIAL EQUINOX BUILT-IN ONE")
public abstract class IndexesCreator {

    public static final String ALTER_TABLE_ = "ALTER TABLE ";

    public static final String SHOW_INDEX_FROM_ = "SHOW INDEX FROM ";

    public static final String _ADD_FULLTEXT_INDEX_ = " ADD FULLTEXT INDEX %s (";

    public static final String _IN_NATURAL_LANGUAGE_MODE = " IN NATURAL LANGUAGE MODE";

    public static final String _IN_BOOLEAN_MODE = " IN BOOLEAN MODE";

    protected static final String _KEY_NAME = "Key_name='";

    @PersistenceContext
    protected EntityManager entityManager;

    @Wrapper
    protected void createFullTextIndex(String table, String indexName, List<String> fields) {
        createIndex(table, indexName, _ADD_FULLTEXT_INDEX_, fields);
    }

    protected void createIndex(String table, String indexName, String indexType, List<String> fields) {
        String showQuery = SHOW_INDEX_FROM_ + table + _WHERE_ + _KEY_NAME + indexName + SINGLE_QUOTE;
        Session session = entityManager.unwrap(Session.class);
        session.doWork(connection -> {
            try (Statement statement = connection.createStatement()) {
                ResultSet set = statement.executeQuery(showQuery);
                if (!set.next()) {
                    String createIndexQuery = assembleCreateIndexQuery(table, indexType, indexName, fields);
                    statement.execute(createIndexQuery);
                }
            }
        });
    }

    @Assembler
    protected String assembleCreateIndexQuery(String table, String indexType, String indexName, List<String> fields) {
        StringBuilder query = new StringBuilder(ALTER_TABLE_ + table);
        query.append(String.format(indexType, indexName));
        int fieldsNumber = fields.size();
        int lastIndex = fieldsNumber - 1;
        for (int j = 0; j < fieldsNumber; j++) {
            query.append(fields.get(j));
            if (j < lastIndex)
                query.append(COMMA);
        }
        query.append(CLOSED_ROUND_BRACKET);
        return query.toString();
    }

    public static String formatFullTextKeywords(Collection<String> keywords, boolean escapeDoubleQuotes) {
        return formatFullTextKeywords(keywords, "", escapeDoubleQuotes);
    }

    // TODO: 13/02/2025 ADD ALSO THE METHOD WITH THE LEADING CHARACTER
    public static String formatFullTextKeywords(Collection<String> keywords, String trailingCharacter,
                                                boolean escapeDoubleQuotes) {
        if (keywords.isEmpty())
            return "";
        else {
            String formattedKeywords = String.join(" ", keywords);
            if (escapeDoubleQuotes)
                formattedKeywords = formattedKeywords.replaceAll("\"", "");
            return formattedKeywords + trailingCharacter;
        }
    }

}
