package bookstore.Data;

import bookstore.Interfaces.Book;
import bookstore.Interfaces.Cart;
import bookstore.Interfaces.Store;
import bookstore.Remote.RemoteBook;
import bookstore.Remote.RemoteCart;
import bookstore.Remote.RemoteStore;

import java.rmi.UnexpectedException;

/**
 *
 * @author Ricardo
 */
public class Util {

    public Util() {
    }

    public Store makeStore(ObjRef o) throws UnexpectedException {
        try {
            return new RemoteStore(o.id, o.address);
        } catch (Exception e) {
            throw new UnexpectedException(e.getMessage());
        }
    }

    public Cart makeCart(ObjRef o) throws UnexpectedException {
        try {
            return new RemoteCart(o.id, o.address);
        } catch (Exception e) {
            throw new UnexpectedException(e.getMessage());
        }
    }

    public Book makeBook(ObjRef o) throws UnexpectedException  {
        try {
            return new RemoteBook(o.id, o.address);
        } catch (Exception e) {
            throw new UnexpectedException(e.getMessage());
        }
    }
}
