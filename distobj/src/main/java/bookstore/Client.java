package bookstore;

import bookstore.Data.ObjRef;
import bookstore.Interfaces.Book;
import bookstore.Interfaces.Cart;
import bookstore.Interfaces.Store;
import bookstore.Remote.RemoteStore;
import io.atomix.catalyst.transport.Address;

public class Client {
    public static void main(String[] args) throws Exception {
        Address a = new Address("127.0.0.1:10000");
        DO d = new DO(a);

        Store s =  (Store) d.oImport(new ObjRef(a,1,"Store"));
        Book b = s.search("one");

        System.out.println("isbn = " + b.getIsbn());

        Cart cart = s.newCart();
        
        System.out.println("Add: " + cart.add(b));
        System.out.println("Buy: " + cart.buy());
    }
}
