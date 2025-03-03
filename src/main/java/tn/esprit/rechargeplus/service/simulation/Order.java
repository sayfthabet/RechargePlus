package tn.esprit.rechargeplus.service.simulation;

public class Order {
    private static long idCounter = 1;
    private final long orderId;
    private final OrderType type;
    private final double price;
    private int quantity;

    public Order(OrderType type, double price, int quantity) {
        this.orderId = idCounter++;
        this.type = type;
        this.price = price;
        this.quantity = quantity;
    }

    public long getOrderId() {
        return orderId;
    }

    public OrderType getType() {
        return type;
    }

    public double getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int qty) {
        this.quantity = qty;
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + orderId +
                ", type=" + type +
                ", price=" + price +
                ", qty=" + quantity +
                '}';
    }
}
