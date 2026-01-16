import { Component, OnInit, OnDestroy } from '@angular/core';
import { Product } from '../model/product';
import { ProductCategory } from '../model/product-category';
import { ProductService } from '../service/product.service';
import { AccountService } from '../service/account.service';
import { CartService } from '../service/cart.service';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-product-category',
  templateUrl: './product-category.component.html',
  styleUrls: ['./product-category.component.css']
})
export class ProductCategoryComponent implements OnInit, OnDestroy {
  public productsCategory: ProductCategory[] = [];
  products: Product[] = [];
  customerId: number | null = null;
  newProduct: Product = {} as Product;
  errorMessage: string = '';
  private subscriptions: Subscription = new Subscription();

  constructor(
    private accountService: AccountService,
    private productService: ProductService,
    private cartService: CartService
  ) {}

  ngOnInit(): void {
    console.log('ngOnInit called');

    // Subscribe to customer ID observable to get the current logged-in user's ID
    this.subscriptions.add(
      this.accountService.customerId$.subscribe(customerId => {
        this.customerId = customerId;
        console.log('Customer ID in ngOnInit:', this.customerId); // Verify customer ID

        if (this.customerId) {
          this.getProducts(); // Load all products when customer ID is available
        } else {
          console.warn('Customer is not logged in.');
          this.errorMessage = 'Please log in to view current products.';
        }
      })
    );
  }

  ngOnDestroy(): void {
    this.subscriptions.unsubscribe(); // Prevent memory leaks by unsubscribing
  }

  // Load all products
  getProducts(): void {
    this.productService.getAllProducts().subscribe(
      (data: Product[]) => {
        this.products = data;
        console.log('Products fetched successfully:', data);
      },
      (error: any) => {
        console.error('Error fetching products:', error);
      }
    );
  }

  // Load products by category
  loadProductsByCategory(categoryName: string): void {
    this.productService.getProductsByCategory(categoryName).subscribe(
      (data: Product[]) => {
        this.products = data;
        console.log('Products loaded:', data); // Debugging output
      },
      (error) => {
        console.error('Error loading products:', error);
        this.errorMessage = 'Failed to load products.';
      }
    );
  }

  // Function to add a product to the cart
  addToCart(product: Product): void {
    if (this.customerId === null) {
      alert('Please log in to add items to the cart.');
      return;
    }

    const orderItem = {
      productId: product.id,
      productName: product.name,
      price: product.price,
      imageFile: product.imageFile,
      quantity: 1,
      unitOfMeasure: product.unitOfMeasure
    };

    this.cartService.addToCart(orderItem).subscribe(
      (response: any) => {
        alert ('Products send using: ' + this.customerId)
        alert('Product sent to order_items table:' + response);
        alert(`${product.name} has been added to your cart successfully!`);
      },
      (error: any) => {
        console.error('Error adding product to order_items table:', error);
        alert('Failed to add product to cart. Please try again.');
      }
    );
  }

  // Open/Close Form
  isFormOpen = false;

  toggleForm() {
    this.isFormOpen = !this.isFormOpen;
  }

  closeForm() {
    this.isFormOpen = false;
  }

  // Function to add a new product
  addProduct(): void {
    this.productService.addProduct(this.newProduct).subscribe(
      (response: Product) => {
        console.log('Product added successfully:', response);
        alert('Product added successfully!');
        this.getProducts(); // Refresh product list
        this.newProduct = {} as Product; // Clear the form
      },
      (error: any) => {
        console.error('Error adding product:', error);
      }
    );
  }

  // Function to delete a product
  deleteProduct(productId: number): void {
    this.productService.deleteProduct(productId).subscribe(
      (response: any) => {
        console.log('Product deleted successfully:', response);
        alert('Product deleted successfully!');
        this.getProducts(); // Refresh product list
      },
      (error: any) => {
        console.error('Error deleting product:', error);
      }
    );
  }
}
