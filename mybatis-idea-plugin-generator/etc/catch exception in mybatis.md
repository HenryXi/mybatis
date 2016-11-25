# Catch exception in MyBatis
I generate mapper interface and xml file in my application. There is no
exception in method signature. But when the sql execute failed MyBatis will throw an exception. I found the 
exception thrown from MyBatis is always `PersistenceException`. After reading the source code, I seemed to understand.

**DefaultSqlSession**
```java
public int update(String statement, Object parameter) {
    try {
        dirty = true;
        MappedStatement ms = configuration.getMappedStatement(statement);
        return executor.update(ms, wrapCollection(parameter));
    } catch (Exception e) {
        throw ExceptionFactory.wrapException("Error updating database.  Cause: " + e, e);
    } finally {
        ErrorContext.instance().reset();
    }
}
```
**ExceptionFactory**
```java
public class ExceptionFactory {

    public static RuntimeException wrapException(String message, Exception e) {
        return new PersistenceException(ErrorContext.instance().message(message).cause(e).toString(), e);
    }

}
```
If you want catch the exception in MyBatis you can capture the `PersistenceException`. Do not forget that
it wraps the real exception. Capture exception code may like following.
```java
try {
    // invoke mapper 
} catch (PersistenceException e) {
    final Throwable cause = e.getCause();
    if (cause instanceof PSQLException) {
        // handle the exception
    }
}
```