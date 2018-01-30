package stream.api;

import common.test.tool.annotation.Difficult;
import common.test.tool.dataset.ClassicOnlineStore;
import common.test.tool.entity.Customer;
import common.test.tool.entity.Item;
import common.test.tool.entity.Shop;
import org.junit.Test;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

public class Exercise8Test extends ClassicOnlineStore {

    @Difficult @Test
    public void itemsNotOnSale() {
        Stream<Customer> customerStream = this.mall.getCustomerList().stream();
        Stream<Shop> shopStream = this.mall.getShopList().stream();

        /**
         * Create a set of item names that are in {@link Customer.wantToBuy} but not on sale in any shop.
         */
        List<String> itemListOnSale = shopStream.flatMap(shop -> shop.getItemList().stream())
                .map(Item::getName)
                .distinct()
                .collect(Collectors.toList());
        System.out.println(itemListOnSale);
        Set<String> itemSetNotOnSale = customerStream.flatMap(c -> c.getWantToBuy().stream())
                .map(Item::getName)
                .filter(itemName -> !itemListOnSale.contains(itemName))
                .collect(Collectors.toSet());
        System.out.println(itemSetNotOnSale);

        assertThat(itemSetNotOnSale, hasSize(3));
        assertThat(itemSetNotOnSale, hasItems("bag", "pants", "coat"));
    }

    @Difficult @Test
    public void havingEnoughMoney() {
        Stream<Customer> customerStream = this.mall.getCustomerList().stream();
        Stream<Shop> shopStream = this.mall.getShopList().stream();

        /**
         * Create a customer's name list including who are having enough money to buy all items they want which is on sale.
         * Items that are not on sale can be counted as 0 money cost.
         * If there is several same items with different prices, customer can choose the cheapest one.
         */
        List<Item> onSale = shopStream.flatMap(shop -> shop.getItemList().stream())
                .sorted(Comparator.comparing(item -> item.getPrice()))
                .distinct()
                .collect(Collectors.toList());
        Predicate<Customer> havingEnoughMoney =
                customer -> customer.getBudget() >=
                        onSale.stream()
                            .filter(saleItem -> customer.getWantToBuy().contains(saleItem))
                            .map(Item::getPrice)
                            .reduce(0, (total, next) -> total + next);
        List<String> customerNameList = customerStream.filter(havingEnoughMoney)
                .map(Customer::getName)
                .collect(Collectors.toList());

        assertThat(customerNameList, hasSize(7));
        assertThat(customerNameList, hasItems("Joe", "Patrick", "Chris", "Kathy", "Alice", "Andrew", "Amy"));
    }
}
