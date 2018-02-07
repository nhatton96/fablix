package Models;

import java.util.HashMap;

public class Cart {
	HashMap<String, Integer> cartItems;
	HashMap<String, String> idname;

	public Cart() {
		cartItems = new HashMap<>();
		idname = new HashMap<>();
	}

	public HashMap<String, Integer> getCartItems() {
		return cartItems;
	}

	public void addToCart(String itemId, int amount, String name) {
		int newamount = cartItems.getOrDefault(itemId, 0) + amount;
		cartItems.put(itemId, newamount);
		idname.put(itemId, name);
	}

	public void setCart(String itemId, int amount) {
		if (amount == 0) {
			cartItems.remove(itemId);
			idname.remove(itemId);
		}
		cartItems.put(itemId, amount);
	}
}