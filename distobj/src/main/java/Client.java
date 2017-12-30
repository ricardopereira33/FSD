import bookstore.Data.ObjRef;
import bookstore.Interfaces.Book;
import bookstore.Interfaces.Cart;
import bookstore.Interfaces.Store;
import bookstore.DO;
import io.atomix.catalyst.transport.Address;
import manager.Interfaces.Manager;
import manager.Remote.RemoteManager;

public class Client {
    public static void main(String[] args) throws Exception {
        Address store = new Address("127.0.0.1:1235");
        DO d = new DO(store);
        Store s =  (Store) d.oImport(new ObjRef(store,1,"Store"));
        Manager m = new RemoteManager(1, new Address("127.0.0.1:1434"));

        /**
         * Manager m = ....;
         *
         *
         * m.begin();
         * */
        System.out.println("Begin.");
        m.begin();
        
        Book b = s.search("one");

        System.out.println("isbn = " + b.getIsbn());

        Cart cart = s.newCart();

        System.out.println("Add: " + cart.add(b));
        System.out.println("Buy: " + cart.buy());

        m.commit();
        System.out.println("End.");
        /**
         *
         * m.commit();
         * */
    }
}
