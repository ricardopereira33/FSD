import bookstore.Data.ObjRef;
import bookstore.Interfaces.Book;
import bookstore.Interfaces.Cart;
import bookstore.Interfaces.Store;
import bookstore.DO;
import io.atomix.catalyst.transport.Address;

public class Client {
    public static void main(String[] args) throws Exception {
        Address store = new Address("127.0.0.1:10000");
        DO d = new DO(store);
        Store s =  (Store) d.oImport(new ObjRef(store,1,"AccountImp"));

        /**
         * Manager m = ....;
         *
         *
         * m.begin();
         * */

        Book b = s.search("one");

        System.out.println("isbn = " + b.getIsbn());

        Cart cart = s.newCart();

        /**
         *
         * m.commit();
         * */

        System.out.println("Add: " + cart.add(b));
        System.out.println("Buy: " + cart.buy());
    }
}
