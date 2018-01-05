import DO.ObjRef;
import DO.DO;
import bank.Data.Invoice;
import bank.Interfaces.Account;
import bank.Interfaces.Bank;
import bookstore.Impl.Book;
import bookstore.Interfaces.Cart;
import bookstore.Interfaces.Store;
import io.atomix.catalyst.transport.Address;
import manager.Interfaces.Manager;

import java.util.Scanner;

public class Client {
    public static void main(String[] args) throws Exception {
        DO d = new DO();

        // The addresses for Store, Bank and Manager
        Address storeAddress = new Address("127.0.0.1:1135");
        Address bankAddress = new Address("127.0.0.1:1934");
        Address managerAddress= new Address("127.0.0.1:1434");

        // Get Stubs for Store, Bank and Manager
        Store s =  (Store) d.oImport(new ObjRef(storeAddress,1,"Store"));
        Bank bank = (Bank) d.oImport(new ObjRef(bankAddress, 1, "Bank"));
        Manager m = (Manager) d.oImport(new ObjRef(managerAddress, 1, "Manager"));

        /** Transation **/

        System.out.println("Begin.");
        m.begin();

        Book b = s.search("one");
        System.out.println("isbn = " + b.getIsbn());

        Cart cart = s.newCart();
        System.out.println("Add: " + cart.add(b));
        Invoice i = cart.buy();
        System.out.println("Buy: " + i.getValue());

        Account aClient = bank.access("client");
        Account aStore = bank.access(i.getReference());

        aClient.transfer(aStore, i.getValue());

        aClient.getHistory().stream().forEach((e) -> System.out.println(e));

        m.commit();
        System.out.println("End.");
    }
}
