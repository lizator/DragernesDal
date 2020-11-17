package com.example.dragernesdal.data.inventory.model;

public class InventoryDTO {
    int idItem;
    int idCharacter;
    String itemName;
    int amount;

    public InventoryDTO() {}

    public InventoryDTO(int idItem, int idCharacter, String itemName, int amount) {
        this.idItem = idItem;
        this.idCharacter = idCharacter;
        this.itemName = itemName;
        this.amount = amount;
    }

    public int getIdItem() {
        return idItem;
    }

    public void setIdItem(int idItem) {
        this.idItem = idItem;
    }

    public int getIdCharacter() {
        return idCharacter;
    }

    public void setIdCharacter(int idCharacter) {
        this.idCharacter = idCharacter;
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
