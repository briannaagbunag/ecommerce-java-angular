package com.agbunag.serviceimpl;

import com.agbunag.entity.ProductData;
import com.agbunag.model.Product;
import com.agbunag.model.ProductCategory;
import com.agbunag.repository.ProductDataRepository;
import com.agbunag.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    ProductDataRepository productDataRepository;

    @Override
    public List<Product> getAllProducts() {
        List<ProductData> productDataRecords = new ArrayList<>();
        List<Product> products = new ArrayList<>();

        productDataRepository.findAll().forEach(productDataRecords::add);
        for (ProductData productData : productDataRecords) {
            Product product = new Product();
            product.setId(productData.getId());
            product.setName(productData.getName());
            product.setCategoryName(productData.getCategoryName());
            product.setImageFile(productData.getImageFile());
            product.setDescription(productData.getDescription());
            product.setUnitOfMeasure(productData.getUnitOfMeasure());
            product.setPrice(productData.getPrice());
            product.setQuantityStock(productData.getQuantityStock()); // Include quantityStock
            products.add(product);
        }
        return products;
    }

    @Override
    public List<ProductCategory> listProductCategories() {
        Map<String, List<Product>> mappedProduct = getCategoryMappedProducts();
        List<ProductCategory> productCategories = new ArrayList<>();
        for (String categoryName : mappedProduct.keySet()) {
            ProductCategory productCategory = new ProductCategory();
            productCategory.setCategoryName(categoryName);
            productCategory.setProducts(mappedProduct.get(categoryName));
            productCategories.add(productCategory);
        }
        return productCategories;
    }

    @Override
    public Map<String, List<Product>> getCategoryMappedProducts() {
        Map<String, List<Product>> mapProducts = new HashMap<>();

        List<ProductData> productDataRecords = new ArrayList<>();
        List<Product> products;

        productDataRepository.findAll().forEach(productDataRecords::add);
        for (ProductData productData : productDataRecords) {
            Product product = new Product();

            if (mapProducts.containsKey(productData.getCategoryName())) {
                products = mapProducts.get(productData.getCategoryName());
            } else {
                products = new ArrayList<>();
                mapProducts.put(productData.getCategoryName(), products);
            }
            product.setId(productData.getId());
            product.setName(productData.getName());
            product.setCategoryName(productData.getCategoryName());
            product.setImageFile(productData.getImageFile());
            product.setDescription(productData.getDescription());
            product.setUnitOfMeasure(productData.getUnitOfMeasure());
            product.setPrice(productData.getPrice());
            product.setQuantityStock(productData.getQuantityStock()); // Include quantityStock
            products.add(product);
        }
        return mapProducts;
    }

    @Override
    public Product[] getAll() {
        List<ProductData> productsData = new ArrayList<>();
        List<Product> products = new ArrayList<>();
        productDataRepository.findAll().forEach(productsData::add);
        for (ProductData productData : productsData) {
            Product product = new Product();
            product.setId(productData.getId());
            product.setName(productData.getName());
            product.setQuantityStock(productData.getQuantityStock()); // Include quantityStock
            products.add(product);
        }
        return products.toArray(new Product[0]);
    }

    @Override
    public Product get(Integer id) {
        log.info("Input id >> " + id);
        Product product = null;
        Optional<ProductData> optional = productDataRepository.findById(id);
        if (optional.isPresent()) {
            log.info("Is present >> ");
            product = new Product();
            product.setId(optional.get().getId());
            product.setName(optional.get().getName());
            product.setDescription(optional.get().getDescription());
            product.setCategoryName(optional.get().getCategoryName());
            product.setImageFile(optional.get().getImageFile());
            product.setUnitOfMeasure(optional.get().getUnitOfMeasure());
            product.setPrice(optional.get().getPrice());
            product.setQuantityStock(optional.get().getQuantityStock()); // Include quantityStock
        } else {
            log.info("Failed >> unable to locate id: " + id);
        }
        return product;
    }

    @Override
    public Product create(Product product) {
        log.info("add: Input " + product.toString());
        ProductData productData = new ProductData();
        productData.setName(product.getName());
        productData.setCategoryName(product.getCategoryName());
        productData.setDescription(product.getDescription());
        productData.setUnitOfMeasure(product.getUnitOfMeasure());
        productData.setImageFile(product.getImageFile());
        productData.setPrice(product.getPrice());
        productData.setQuantityStock(product.getQuantityStock()); // Include quantityStock
        productData = productDataRepository.save(productData);

        Product newProduct = new Product();
        newProduct.setId(productData.getId());
        newProduct.setName(productData.getName());
        newProduct.setDescription(productData.getDescription());
        newProduct.setCategoryName(productData.getCategoryName());
        newProduct.setPrice(productData.getPrice());
        newProduct.setUnitOfMeasure(productData.getUnitOfMeasure());
        newProduct.setImageFile(productData.getImageFile());
        newProduct.setQuantityStock(productData.getQuantityStock()); // Include quantityStock
        return newProduct;
    }

    @Override
    public Product update(Product product) {
        Product updatedProduct = null;
        int id = product.getId();
        Optional<ProductData> optional = productDataRepository.findById(id);
        if (optional.isPresent()) {
            ProductData originalProductData = optional.get();
            originalProductData.setName(product.getName());
            originalProductData.setDescription(product.getDescription());
            originalProductData.setCategoryName(product.getCategoryName());
            originalProductData.setImageFile(product.getImageFile());
            originalProductData.setUnitOfMeasure(product.getUnitOfMeasure());
            originalProductData.setQuantityStock(product.getQuantityStock()); // Include quantityStock
            ProductData productData = productDataRepository.save(originalProductData);

            updatedProduct = new Product();
            updatedProduct.setId(productData.getId());
            updatedProduct.setName(productData.getName());
            updatedProduct.setDescription(productData.getDescription());
            updatedProduct.setCategoryName(productData.getCategoryName());
            updatedProduct.setPrice(productData.getPrice());
            updatedProduct.setUnitOfMeasure(productData.getUnitOfMeasure());
            updatedProduct.setImageFile(productData.getImageFile());
            updatedProduct.setQuantityStock(productData.getQuantityStock()); // Include quantityStock
        } else {
            log.error("Product record with id: {} does not exist", id);
        }
        return updatedProduct;
    }

    @Override
    public void delete(Integer id) {
        log.info("Input >> " + id);
        Optional<ProductData> optional = productDataRepository.findById(id);
        if (optional.isPresent()) {
            productDataRepository.delete(optional.get());
            log.info("Successfully deleted Product record with id: {}", id);
        } else {
            log.error("Unable to locate product with id: {}", id);
        }
    }
}
