package com.rbyte.dragernesdal.data.inventory.model;

public class InventoryDTO {
    int idinventoryrelation;
    int idItem;
    String itemName;
    int amount;

    public InventoryDTO() {}

    public InventoryDTO(int idItem, int idCharacter, String itemName, int amount) {
        this.idinventoryrelation = idCharacter;
        this.idItem = idItem;
        this.itemName = itemName;
        this.amount = amount;
    }

    public int getIdInventoryRelation() {
        return idinventoryrelation;
    }

    public void setIdInventoryRelation(int idInventoryRelation) {
        this.idinventoryrelation = idInventoryRelation;
    }

    public int getIdItem() {
        return idItem;
    }

    public void setIdItem(int idItem) {
        this.idItem = idItem;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}