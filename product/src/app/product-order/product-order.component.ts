import { Component, OnInit } from '@angular/core';
import { OrderService } from '../service/order.service';
import { AccountService } from '../service/account.service';

@Component({
  selector: 'app-product-order',
  templateUrl: './product-order.component.html',
  styleUrls: ['./product-order.component.css']
})
export class ProductOrderComponent implements OnInit {
  orderHistory: any[] = [];
  customerId: number | null = null;
  errorMessage: string = '';

  constructor(
    private orderService: OrderService,
    private accountService: AccountService
  ) {}

ngOnInit(): void {
  this.accountService.customerId$.subscribe(id => {
    this.customerId = id;

    if (this.customerId) {
      this.loadOrderHistory();
    } else {
      console.warn('Customer is not logged in.');
      this.errorMessage = 'Please log in to view your orders.';
      this.orderHistory = []; // Optional: clear previous orders
    }
  });
}


loadOrderHistory(): void {
  if (this.customerId !== null) {
    this.orderService.getOrders(this.customerId).subscribe(
      (orders: any[]) => {
        this.orderHistory = orders; // Persistent orders from DB
      },
      (error: any) => {
        console.error('Error fetching orders:', error);
      }
    );
  }
}


  addOrder(order: any): void {
    if (!this.customerId) return;

    this.orderService.placeOrder(order).subscribe(
      (response: any) => {
        // Reload orders after placing new one
        this.loadOrderHistory();
      },
      (error: any) => {
        console.error('Error placing order:', error);
      }
    );
  }
}
