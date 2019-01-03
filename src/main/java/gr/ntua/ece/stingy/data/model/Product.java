package gr.ntua.ece.stingy.data.model;

import java.util.ArrayList;
import java.util.Objects;

public class Product {

    private final long id;
    private final String name;
    private final String description;
    private final String category;
    private final boolean withdrawn;
    private final ArrayList<String> tags;
    private final ArrayList<String> extraData;

    public Product(long id, String name, String description, String category, boolean withdrawn, ArrayList<String> tags, ArrayList<String> extraData) {
        this.id          = id;
        this.name        = name;
        this.description = description;
        this.category    = category;
        this.withdrawn   = withdrawn;
        this.tags        = tags;
        this.extraData	 = extraData;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getCategory() {
        return category;
    }

    public boolean isWithdrawn() {
        return withdrawn;
    }

    public ArrayList<String> getTags() {
        return tags;
    }

    public ArrayList<String> getExtraData() {
        return extraData;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return id == product.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
