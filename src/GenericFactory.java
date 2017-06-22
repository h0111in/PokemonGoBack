/**
 * Created by hosein on 2017-06-20.
 */
public abstract class GenericFactory<T> {

    public abstract T GetObject(String type);
}
