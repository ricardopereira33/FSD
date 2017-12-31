import bank.Interfaces.Bank;
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
        DO d = new DO();

        // The addresses for Store, Bank and Manager
        Address storeAddress = new Address("127.0.0.1:1235");
        Address bankAddress = new Address("127.0.0.1:1934");
        Address managerAddress= new Address("127.0.0.1:1434");

        // Get Stubs for Store, Bank and Manager
        //Store s =  (Store) d.oImport(new ObjRef(storeAddress,1,"Store"));
        Bank bank = (Bank) d.oImport(new ObjRef(bankAddress, 1, "Bank"));
        //Manager m = (Manager) d.oImport(new ObjRef(managerAddress, 1, "Manager"));


        bank.transfer("store", "client", 20);


        /**
         * Manager m = ....;
         *
         *
         * m.begin();
         *
        System.out.println("Begin.");
        m.begin();
        
        Book b = s.search("one");

        System.out.println("isbn = " + b.getIsbn());

        Cart cart = s.newCart();

        System.out.println("Add: " + cart.add(b));
        System.out.println("Buy: " + cart.buy());

        m.commit();
        System.out.println("End.");*/
        /**
         *
         * m.commit();
         * */
    }
}
