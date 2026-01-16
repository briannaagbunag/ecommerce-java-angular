import { Component, OnInit } from '@angular/core';
import { Product } from '../model/product';
import { ProductCategory } from '../model/product-category';
import { ProductService } from '../service/product.service';
import { CartService } from '../service/cart.service';
import { AccountService } from '../service/account.service';

@Component({
  selector: 'app-shopping-cart',
  templateUrl: './shopping-cart.component.html',
  styleUrls: ['./shopping-cart.component.css'],
})
export class ShoppingCartComponent implements OnInit {

  loadingStates: { [key: number]: boolean } = {};
  public productsCategory: ProductCategory[] = [];
  errorMessage: string = '';
  selectedProduct: Product | null = null;

  customerId: number | null = null;
  cart: any = { items: [], totalPrice: 0 };
  selectedItems: any[] = [];
  showInvoiceModal = false;
  paymentModes = ['Credit Card', 'PayPal', 'Bank Transfer'];
  deliveryOptions = ['Delivery', 'Pickup'];
  selectedPaymentMode: string | null = null;
  shipAddress: string = '';
  pickOption: string = 'Delivery';
  paymentSuccessful: boolean | null = null;

  constructor(
    private productService: ProductService,
    private cartService: CartService,
    private accountService: AccountService
  ) {}

  ngOnInit(): void {
    this.fetchCustomerIdAndLoadCart();
  }

  isCheckoutOpen = false;

  toggleCheckout() {
    this.isCheckoutOpen = !this.isCheckoutOpen;
  }

  closeCheckout() {
    this.isCheckoutOpen = false;
  }


  fetchCustomerIdAndLoadCart(): void {
    this.customerId = this.accountService.getCustomerId();
    if (this.customerId !== null) {
      this.loadProductCategories();
      this.loadCart();
    } else {
      this.errorMessage = 'Please log in to view your shopping cart.';
    }
  }

  loadProductCategories(): void {
    this.productService.getAllProducts().subscribe(
      (data) => {
        this.productsCategory = data;
      },
      (error) => {
        this.errorMessage = 'Failed to load product categories.';
      }
    );
  }

  loadCart(): void {
    if (this.customerId !== null) {
      this.cartService.getCart(this.customerId).subscribe(
        (data) => {
          if (data) {
            this.cart = data;
            this.cart.items.forEach((item: any) => (item.isEditing = false));
            this.calculateTotalPrice();
          } else {
            this.cart = { items: [], totalPrice: 0 };
          }
        },
        (error) => {
          this.errorMessage = 'Failed to load cart. Please try again later.';
        }
      );
    }
  }

  confirmUpdate(item: any): void {
    item.isEditing = false;
    if (this.customerId) {
      this.cartService.updateItemQuantity(this.customerId, item.productId, item.quantity).subscribe(
        () => {
          const selectedItem = this.selectedItems.find(
            i => i.productId === item.productId
          );
          if (selectedItem) {
            selectedItem.quantity = item.quantity;
          }
          this.calculateTotalPrice();
        }
      );
    }
  }

  addToCart(product: Product): void {
    if (this.customerId === null) {
      alert('Please log in to add items to the cart.');
      return;
    }

    if (product.quantityStock < 1) {
      alert(`Sorry, ${product.name} is out of stock.`);
      return;
    }

    const orderItem = {
      productId: product.id,
      productName: product.name,
      price: product.price,
      imageFile: product.imageFile,
      quantity: 1,
      unitOfMeasure: product.unitOfMeasure,
      quantityStock: product.quantityStock
    };

    this.cartService.addToCart(orderItem).subscribe(
      (response: any) => {
        product.quantityStock -= 1;
        this.loadCart();
        alert(`${product.name} has been added to your cart successfully!`);
      },
      (error: any) => {
        alert('Failed to add product to cart. Please try again.');
      }
    );
  }

  removeItemFromCart(item: any): void {
    if (this.customerId) {
      this.cartService.removeFromCart(this.customerId, item.productId).subscribe(
        () => {
          alert('Item removed from cart.');
          this.cart.items = this.cart.items.filter((cartItem: any) => cartItem.productId !== item.productId);
          this.calculateTotalPrice();

          this.productsCategory.forEach(category => {
            const product = category.products.find(p => p.id === item.productId);
            if (product) {
              product.quantityStock += item.quantity;
            }
          });
        },
        (error) => {
          alert('Failed to remove item from cart. Please try again.');
        }
      );
    }
  }


  calculateTotalPrice(): void {
    this.cart.totalPrice = this.cart.items.reduce(
      (total: number, item: any) => total + (item.price * item.quantity),
      0
    );
  }

  confirmCheckout(): void {
    if (this.customerId) {
      const order = {
        customerId: this.customerId,
        items: this.selectedItems,
        pay: this.selectedPaymentMode,
        ship: this.shipAddress,
        pick: this.pickOption,
        status: 'purchased'
      };

      this.cartService.checkoutCart(order).subscribe(
        (response) => {
          this.paymentSuccessful = true;
          this.cartService.addOrderToHistory(order);  // Add the order to history
          this.showInvoiceModal = false;
          alert('Order checked out successfully!');
          this.loadCart();
        },
        (error) => {
          this.paymentSuccessful = false;
          alert('Checkout failed. Please try again.');
        }
      );
    } else {
      alert('You must be logged in to checkout.');
    }
  }

  checkout(): void {
    if (this.selectedItems.length === 0) {
      alert('Please select items to checkout.');
      return;
    }
    if (!this.selectedPaymentMode) {
      alert('Please select a payment mode.');
      return;
    }
    if (!this.shipAddress) {
      alert('Please enter a shipping address.');
      return;
    }

    this.showInvoiceModal = true;
  }

  selectAllItems(): void {
    this.selectedItems = [...this.cart.items];
  }

  deleteSelectedItems(): void {
    const itemsToDelete = [...this.selectedItems];
    itemsToDelete.forEach((item: any) => this.removeItemFromCart(item));
    this.selectedItems = [];
  }

  toggleItemSelection(item: any): void {
    const index = this.selectedItems.findIndex(selected => selected.productId === item.productId);
    if (index === -1) {
      this.selectedItems.push(item);
    } else {
      this.selectedItems.splice(index, 1);
    }
  }

  toggleEditMode(item: any): void {
    item.isEditing = !item.isEditing;
    if (!item.isEditing) {
      this.loadCart();
    }
  }

  closeModal(): void {
    this.showInvoiceModal = false;
    this.paymentSuccessful = null;
  }

  deleteFromCart(product: Product): void {
    if (product.id) {
      this.productService.deleteProduct(product.id).subscribe(() => {
        alert('Product deleted successfully!');
        this.loadCart();
      },
      (error) => {
        console.error('Error deleting product:', error);
      });
    }
  }
}
