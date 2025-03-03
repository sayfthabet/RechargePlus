package tn.esprit.rechargeplus.service.simulation;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

public class OrderBook {
    // Buy orders sorted by highest price first
    private final PriorityQueue<Order> buyOrders;
    // Sell orders sorted by lowest price first
    private final PriorityQueue<Order> sellOrders;

    public OrderBook() {
        buyOrders = new PriorityQueue<>(Comparator.comparingDouble(Order::getPrice).reversed());
        sellOrders = new PriorityQueue<>(Comparator.comparingDouble(Order::getPrice));
    }

    public void addOrder(Order order) {
        if (order.getType() == OrderType.BUY) {
            buyOrders.offer(order);
        } else {
            sellOrders.offer(order);
        }
    }

    /**
     * Matches orders if the best buy price is greater than or equal to the best sell price.
     * Executes trades by reducing order quantities or removing fully matched orders.
     */
    public void matchOrders() {
        while (!buyOrders.isEmpty() && !sellOrders.isEmpty()) {
            Order bestBuy = buyOrders.peek();
            Order bestSell = sellOrders.peek();

            if (bestBuy.getPrice() >= bestSell.getPrice()) {
                int tradedQty = Math.min(bestBuy.getQuantity(), bestSell.getQuantity());
                double tradePrice = (bestBuy.getPrice() + bestSell.getPrice()) / 2.0;
                System.out.println("Trade executed: " + tradedQty + " units at $" + tradePrice);

                bestBuy.setQuantity(bestBuy.getQuantity() - tradedQty);
                bestSell.setQuantity(bestSell.getQuantity() - tradedQty);

                if (bestBuy.getQuantity() == 0) {
                    buyOrders.poll();
                }
                if (bestSell.getQuantity() == 0) {
                    sellOrders.poll();
                }
            } else {
                break;
            }
        }
    }

    /**
     * Displays the current state of the order book.
     */
    public void display() {
        System.out.println("=== Order Book ===");

        System.out.println("Buy Orders:");
        List<Order> buyList = new ArrayList<>(buyOrders);
        buyList.sort(Comparator.comparingDouble(Order::getPrice).reversed());
        for (Order o : buyList) {
            System.out.println(o);
        }

        System.out.println("Sell Orders:");
        List<Order> sellList = new ArrayList<>(sellOrders);
        sellList.sort(Comparator.comparingDouble(Order::getPrice));
        for (Order o : sellList) {
            System.out.println(o);
        }
        System.out.println("==================");
    }
}
