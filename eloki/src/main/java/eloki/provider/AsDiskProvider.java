package eloki.provider;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Any clas that extends `FromDiskProvider<T>` must also be annotated with
 * `@AsDiskProvider(String path)` and provide the path of the file or folder
 * which contains all valid values relative to the `resources` folder. `path`
 * can be a text file or a folder which contains other text files or folders.
 * In case of a folder path, `FromDiskProvider<T>` childs would read values
 * recursively.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface AsDiskProvider {
    String value();
}
