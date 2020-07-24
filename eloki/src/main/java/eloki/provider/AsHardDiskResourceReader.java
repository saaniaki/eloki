package eloki.provider;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * All subclasses of `HardDiskResourceReader` must be annotated with this
 * annotation and provide the property key which will be used to indicate
 * the path of the file or folder which contains all valid values relative
 * to the `resources` folder. This path can point to a text file or a folder
 * which contains other text files or folders. In the case of a folder path,
 * `HardDiskResourceReader` children would read values recursively.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface AsHardDiskResourceReader {
    String value(); // property key
}
