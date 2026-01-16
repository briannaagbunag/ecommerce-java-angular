import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class CartService {
  private apiUrl = 'http://localhost:8080/api/orders/cart';
  private orderHistory: any[] = [];  // Array to store each checkout order

  private httpOptions = {
    headers: new HttpHeaders({ 'Content-Type': 'application/json' }),
    withCredentials: true
  };

  constructor(private http: HttpClient) {}

  // Add a new order to the order history
  addOrderToHistory(order: any): void {
    this.orderHistory.push(order);
  }

  // Get all orders in the order history
  getOrderHistory(): any[] {
    return this.orderHistory;
  }

  // Fetch the cart for a specific customer
  getCart(customerId: number): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/view?customerId=${customerId}`, this.httpOptions);
  }

  // Add a product to the cart
  addToCart(item: any): Observable<any> {
    return this.http.post(`${this.apiUrl}/add`, item, this.httpOptions);
  }

  // Remove a product from the cart
  removeFromCart(customerId: number, productId: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/remove`, {
      params: { customerId: customerId.toString(), productId: productId.toString() },
      ...this.httpOptions
    });
  }

  // Update the quantity of a specific item in the cart
  updateItemQuantity(customerId: number, productId: number, quantity: number): Observable<any> {
    return this.http.put<any>(
      `${this.apiUrl}/update`, 
      {}, 
      { params: { customerId: customerId.toString(), productId: productId.toString(), quantity: quantity.toString() }, ...this.httpOptions }
    );
  }

  // Checkout the cart for a specific customer
  checkoutCart(order: any): Observable<any> {
    return this.http.post(`${this.apiUrl}/checkout`, order, this.httpOptions);
  }
}
