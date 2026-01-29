import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class OrderService {

  private customerApiUrl = 'http://localhost:8080/api/customers';

  private httpOptions = {
    headers: new HttpHeaders({ 'Content-Type': 'application/json' }),
    withCredentials: true
  };

  constructor(private http: HttpClient) {}

  // Get purchased orders for logged-in customer
  getOrders(customerId: number): Observable<any[]> {
    return this.http.get<any[]>(
      `${this.customerApiUrl}/${customerId}/orders`,
      this.httpOptions
    );
  }

  // Checkout
  placeOrder(order: any): Observable<any> {
    return this.http.post(
      'http://localhost:8080/api/orders/cart/checkout',
      order,
      this.httpOptions
    );
  }

  // Cart-related methods (unchanged)
  addToCart(item: any): Observable<any> {
    return this.http.post(
      'http://localhost:8080/api/orders/cart/add',
      item,
      this.httpOptions
    );
  }

  removeFromCart(productId: number): Observable<any> {
    return this.http.delete(
      'http://localhost:8080/api/orders/cart/remove',
      {
        params: { productId: productId.toString() },
        ...this.httpOptions
      }
    );
  }

  updateItemQuantity(productId: number, quantity: number): Observable<any> {
    return this.http.put(
      'http://localhost:8080/api/orders/cart/update',
      {},
      {
        params: {
          productId: productId.toString(),
          quantity: quantity.toString()
        },
        ...this.httpOptions
      }
    );
  }

  clearCart(): Observable<any> {
    return this.http.delete(
      'http://localhost:8080/api/orders/cart/clear',
      this.httpOptions
    );
  }
}
