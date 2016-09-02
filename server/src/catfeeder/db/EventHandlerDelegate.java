package catfeeder.db;

import com.j256.ormlite.dao.*;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.FieldType;
import com.j256.ormlite.stmt.*;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.support.DatabaseConnection;
import com.j256.ormlite.support.DatabaseResults;
import com.j256.ormlite.table.ObjectFactory;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Spliterator;
import java.util.concurrent.Callable;
import java.util.function.Consumer;

public class EventHandlerDelegate<T, K> implements Dao<T, K> {

    private final Dao<T, K> delegate;

    public EventHandlerDelegate(Dao<T, K> dao) {
        delegate = dao;
    }

    @Override
    public T queryForId(K k) throws SQLException {
        return delegate.queryForId(k);
    }

    @Override
    public T queryForFirst(PreparedQuery<T> preparedQuery) throws SQLException {
        return delegate.queryForFirst(preparedQuery);
    }

    @Override
    public List<T> queryForAll() throws SQLException {
        return delegate.queryForAll();
    }

    @Override
    public List<T> queryForEq(String fieldName, Object value) throws SQLException {
        return delegate.queryForEq(fieldName, value);
    }

    @Override
    public List<T> queryForMatching(T matchObj) throws SQLException {
        return delegate.queryForMatching(matchObj);
    }

    @Override
    public List<T> queryForMatchingArgs(T matchObj) throws SQLException {
        return delegate.queryForMatchingArgs(matchObj);
    }

    @Override
    public List<T> queryForFieldValues(Map<String, Object> fieldValues) throws SQLException {
        return delegate.queryForFieldValues(fieldValues);
    }

    @Override
    public List<T> queryForFieldValuesArgs(Map<String, Object> fieldValues) throws SQLException {
        return delegate.queryForFieldValuesArgs(fieldValues);
    }

    @Override
    public T queryForSameId(T data) throws SQLException {
        return delegate.queryForSameId(data);
    }

    @Override
    public QueryBuilder<T, K> queryBuilder() {
        return delegate.queryBuilder();
    }

    @Override
    public UpdateBuilder<T, K> updateBuilder() {
        return delegate.updateBuilder();
    }

    @Override
    public DeleteBuilder<T, K> deleteBuilder() {
        return delegate.deleteBuilder();
    }

    @Override
    public List<T> query(PreparedQuery<T> preparedQuery) throws SQLException {
        return delegate.query(preparedQuery);
    }

    @Override
    public int create(T data) throws SQLException {
        return delegate.create(data);
    }

    @Override
    public int create(Collection<T> datas) throws SQLException {
        return delegate.create(datas);
    }

    @Override
    public T createIfNotExists(T data) throws SQLException {
        return delegate.createIfNotExists(data);
    }

    @Override
    public CreateOrUpdateStatus createOrUpdate(T data) throws SQLException {
        return delegate.createOrUpdate(data);
    }

    @Override
    public int update(T data) throws SQLException {
        return delegate.update(data);
    }

    @Override
    public int updateId(T data, K newId) throws SQLException {
        return delegate.updateId(data, newId);
    }

    @Override
    public int update(PreparedUpdate<T> preparedUpdate) throws SQLException {
        return delegate.update(preparedUpdate);
    }

    @Override
    public int refresh(T data) throws SQLException {
        return delegate.refresh(data);
    }

    @Override
    public int delete(T data) throws SQLException {
        return delegate.delete(data);
    }

    @Override
    public int deleteById(K k) throws SQLException {
        return delegate.deleteById(k);
    }

    @Override
    public int delete(Collection<T> datas) throws SQLException {
        return delegate.delete(datas);
    }

    @Override
    public int deleteIds(Collection<K> ks) throws SQLException {
        return delegate.deleteIds(ks);
    }

    @Override
    public int delete(PreparedDelete<T> preparedDelete) throws SQLException {
        return delegate.delete(preparedDelete);
    }

    @Override
    public CloseableIterator<T> iterator() {
        return delegate.iterator();
    }

    @Override
    public CloseableIterator<T> iterator(int resultFlags) {
        return delegate.iterator(resultFlags);
    }

    @Override
    public CloseableIterator<T> iterator(PreparedQuery<T> preparedQuery) throws SQLException {
        return delegate.iterator(preparedQuery);
    }

    @Override
    public CloseableIterator<T> iterator(PreparedQuery<T> preparedQuery, int resultFlags) throws SQLException {
        return delegate.iterator(preparedQuery, resultFlags);
    }

    @Override
    public CloseableWrappedIterable<T> getWrappedIterable() {
        return delegate.getWrappedIterable();
    }

    @Override
    public CloseableWrappedIterable<T> getWrappedIterable(PreparedQuery<T> preparedQuery) {
        return delegate.getWrappedIterable(preparedQuery);
    }

    @Override
    public void closeLastIterator() throws IOException {
        delegate.closeLastIterator();
    }

    @Override
    public GenericRawResults<String[]> queryRaw(String query, String... arguments) throws SQLException {
        return delegate.queryRaw(query, arguments);
    }

    @Override
    public <UO> GenericRawResults<UO> queryRaw(String query, RawRowMapper<UO> mapper, String... arguments) throws SQLException {
        return delegate.queryRaw(query, mapper, arguments);
    }

    @Override
    public <UO> GenericRawResults<UO> queryRaw(String query, DataType[] columnTypes, RawRowObjectMapper<UO> mapper, String... arguments) throws SQLException {
        return delegate.queryRaw(query, columnTypes, mapper, arguments);
    }

    @Override
    public GenericRawResults<Object[]> queryRaw(String query, DataType[] columnTypes, String... arguments) throws SQLException {
        return delegate.queryRaw(query, columnTypes, arguments);
    }

    @Override
    public <UO> GenericRawResults<UO> queryRaw(String query, DatabaseResultsMapper<UO> mapper, String... arguments) throws SQLException {
        return delegate.queryRaw(query, mapper, arguments);
    }

    @Override
    public long queryRawValue(String query, String... arguments) throws SQLException {
        return delegate.queryRawValue(query, arguments);
    }

    @Override
    public int executeRaw(String statement, String... arguments) throws SQLException {
        return delegate.executeRaw(statement, arguments);
    }

    @Override
    public int executeRawNoArgs(String statement) throws SQLException {
        return delegate.executeRawNoArgs(statement);
    }

    @Override
    public int updateRaw(String statement, String... arguments) throws SQLException {
        return delegate.updateRaw(statement, arguments);
    }

    @Override
    public <CT> CT callBatchTasks(Callable<CT> callable) throws Exception {
        return delegate.callBatchTasks(callable);
    }

    @Override
    public String objectToString(T data) {
        return delegate.objectToString(data);
    }

    @Override
    public boolean objectsEqual(T data1, T data2) throws SQLException {
        return delegate.objectsEqual(data1, data2);
    }

    @Override
    public K extractId(T data) throws SQLException {
        return delegate.extractId(data);
    }

    @Override
    public Class<T> getDataClass() {
        return delegate.getDataClass();
    }

    @Override
    public FieldType findForeignFieldType(Class<?> clazz) {
        return delegate.findForeignFieldType(clazz);
    }

    @Override
    public boolean isUpdatable() {
        return delegate.isUpdatable();
    }

    @Override
    public boolean isTableExists() throws SQLException {
        return delegate.isTableExists();
    }

    @Override
    public long countOf() throws SQLException {
        return delegate.countOf();
    }

    @Override
    public long countOf(PreparedQuery<T> preparedQuery) throws SQLException {
        return delegate.countOf(preparedQuery);
    }

    @Override
    public void assignEmptyForeignCollection(T parent, String fieldName) throws SQLException {
        delegate.assignEmptyForeignCollection(parent, fieldName);
    }

    @Override
    public <FT> ForeignCollection<FT> getEmptyForeignCollection(String fieldName) throws SQLException {
        return delegate.getEmptyForeignCollection(fieldName);
    }

    @Override
    public void setObjectCache(boolean enabled) throws SQLException {
        delegate.setObjectCache(enabled);
    }

    @Override
    public void setObjectCache(ObjectCache objectCache) throws SQLException {
        delegate.setObjectCache(objectCache);
    }

    @Override
    public ObjectCache getObjectCache() {
        return delegate.getObjectCache();
    }

    @Override
    public void clearObjectCache() {
        delegate.clearObjectCache();
    }

    @Override
    public T mapSelectStarRow(DatabaseResults results) throws SQLException {
        return delegate.mapSelectStarRow(results);
    }

    @Override
    public GenericRowMapper<T> getSelectStarRowMapper() throws SQLException {
        return delegate.getSelectStarRowMapper();
    }

    @Override
    public RawRowMapper<T> getRawRowMapper() {
        return delegate.getRawRowMapper();
    }

    @Override
    public boolean idExists(K k) throws SQLException {
        return delegate.idExists(k);
    }

    @Override
    public DatabaseConnection startThreadConnection() throws SQLException {
        return delegate.startThreadConnection();
    }

    @Override
    public void endThreadConnection(DatabaseConnection connection) throws SQLException {
        delegate.endThreadConnection(connection);
    }

    @Override
    public void setAutoCommit(DatabaseConnection connection, boolean autoCommit) throws SQLException {
        delegate.setAutoCommit(connection, autoCommit);
    }

    @Override
    public boolean isAutoCommit(DatabaseConnection connection) throws SQLException {
        return delegate.isAutoCommit(connection);
    }

    @Override
    public void commit(DatabaseConnection connection) throws SQLException {
        delegate.commit(connection);
    }

    @Override
    public void rollBack(DatabaseConnection connection) throws SQLException {
        delegate.rollBack(connection);
    }

    @Override
    public ConnectionSource getConnectionSource() {
        return delegate.getConnectionSource();
    }

    @Override
    public void setObjectFactory(ObjectFactory<T> objectFactory) {
        delegate.setObjectFactory(objectFactory);
    }

    @Override
    public void registerObserver(DaoObserver observer) {
        delegate.registerObserver(observer);
    }

    @Override
    public void unregisterObserver(DaoObserver observer) {
        delegate.unregisterObserver(observer);
    }

    @Override
    public String getTableName() {
        return delegate.getTableName();
    }

    @Override
    public void notifyChanges() {
        delegate.notifyChanges();
    }

    @Override
    public CloseableIterator<T> closeableIterator() {
        return delegate.closeableIterator();
    }

    @Override
    public void forEach(Consumer<? super T> action) {
        delegate.forEach(action);
    }

    @Override
    public Spliterator<T> spliterator() {
        return delegate.spliterator();
    }
}
